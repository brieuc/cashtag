# Diagnostic de performance — `ComputationServiceImpl`

**Statut** : diagnostic confirmé par lecture de code, remédiation proposée non implémentée.
**Symptôme observé** : le calcul de la somme sur ~30'000 entries prend plusieurs secondes, probablement > 1 minute.
**Composants concernés** : `ComputationServiceImpl`, `RateServiceImpl`, `RateRepository`, table `rate` (MariaDB).

---

## 1. Résumé exécutif

Le calcul lui-même (`BigDecimal.add`) est négligeable en coût CPU. Le temps observé vient d'un **problème N+1 classique** : la conversion de devise d'une entry déclenche une requête SQL individuelle vers la table `rate`, exécutée séquentiellement — mais seulement pour les entries dont la devise diffère de la devise cible (voir §3.1bis). Sur le jeu de données observé (30'000 entries, dont 19'000 en CHF), cela représente tout de même ~11'000 requêtes séquentielles si la cible est CHF. Le schéma de base de données n'est **pas en cause** — l'index existant est correctement dimensionné pour cette requête. Le problème est entièrement côté application : trop d'allers-retours réseau/JDBC, pas de préchargement.

Un second effet, plus sournois, multiplie encore ce coût dans `getTagAmounts` : le montant d'une entry y est recalculé (donc reconverti, donc requêté) une fois par tag qu'elle porte.

---

## 2. Contexte technique

- SGBD : **MariaDB** (`jdbc:mariadb://.../dailymondb`), pilote `org.mariadb.jdbc.Driver`.
- Pool de connexions : HikariCP (défaut Spring Boot, non surchargé dans `application-*.yml` — taille de pool par défaut = 10).
- `spring.jpa.show-sql: true` est actif en production (`application-production.yml`), ce qui ajoute un coût de logging non négligeable à ce volume de requêtes (cf. §7).
- Schéma `rate` (`db.changelog-29.xml` + `db.changelog-07.xml` de 2025-12) :

```sql
CREATE TABLE rate (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    source_currency_code VARCHAR(3) NOT NULL,
    target_currency_code VARCHAR(3) NOT NULL,
    value_date DATE NOT NULL,
    rate DECIMAL(10,4) NOT NULL,
    ...
);

CREATE INDEX idx_rate_currencies_date
    ON rate (source_currency_code, target_currency_code, value_date);

ALTER TABLE rate
    ADD CONSTRAINT uk_rate_currencies_date
    UNIQUE (source_currency_code, target_currency_code, value_date);
```

Cet index est important pour la suite : il **couvre exactement** le pattern de requête utilisé (voir §4).

---

## 3. Localisation du problème

### 3.1 — Un appel DB par entry (`ComputationServiceImpl.java:65-96`)

```java
private BigDecimal getLocalizedAmount(Entry entry, String targetCurrencyCode) {
      ...
      Rate rate = rateService.getRateByCurrenciesAndDate(
            sourceCurrencyCode, targetCurrencyCode, entry.getAccountingDate().toLocalDate());
      rateValue = rate.getRate();
      ...
}
```

Cette méthode est appelée une fois par entry, depuis `computeSum` :

```java
BigDecimal totalAmount = entries.stream()
      .map(e -> getLocalizedAmount(e, targetCurrencyCode))
      .reduce(BigDecimal.ZERO, BigDecimal::add);
```

`getRateByCurrenciesAndDate` descend jusqu'à `RateRepository.findClosestRateBefore` (`RateRepository.java:24-29`) :

```java
@Query("SELECT r FROM Rate r WHERE r.sourceCurrency.code = :source "
     + "AND r.targetCurrency.code = :target "
     + "AND r.valueDate <= :valueDate "
     + "ORDER BY r.valueDate DESC LIMIT 1")
Optional<Rate> findClosestRateBefore(...);
```

Chaque entry dont la devise diffère de la devise cible déclenche donc **une requête SQL complète** — préparation de statement, aller-retour réseau, exécution, désérialisation — pour un résultat d'une seule ligne.

Point important à ne pas survoler : ce n'est **pas** systématique. Les premières lignes de la méthode court-circuitent l'appel DB quand la devise de l'entry est déjà la devise cible :

```java
String sourceCurrencyCode = entry.getCurrency().getCode();
BigDecimal amount = entry.getAmount();
if (sourceCurrencyCode.equals(targetCurrencyCode)) // 1)
      return amount;
```

Sur le jeu de données réel (30'000 entries, dont **19'000 en CHF**), si la devise cible demandée est CHF, ces 19'000 entries ne coûtent **aucune** requête. Restent les **~11'000 entries en devise étrangère**, qui, elles, déclenchent chacune une exécution de `findClosestRateBefore`, de façon **séquentielle** (une seule connexion JDBC est utilisée pour la durée de la requête HTTP ; HikariCP ne parallélise pas une unité de travail unique). Le chiffrage du §5 est corrigé en conséquence.

### 3.2 — Multiplication du problème dans `getTagAmounts` (`ComputationServiceImpl.java:47-58`)

```java
public List<TagAmount> getTagAmounts(List<Entry> entries, List<Long> tagIds, String targetCurrencyCode) {
      Set<Tag> tags = entries.stream().flatMap(e -> e.getTags().stream()).collect(Collectors.toSet());
      List<TagAmount> tagAmounts = new ArrayList<>();
      for (Tag tag : tags) {
            BigDecimal amount = entries.stream()
                  .filter(e -> e.getTags().contains(tag))
                  .map(e -> getLocalizedAmount(e, targetCurrencyCode))   // <-- recalculé ici
                  .reduce(BigDecimal.ZERO, BigDecimal::add);
            ...
      }
      ...
}
```

Pour chaque tag présent dans le jeu de résultats, on refiltre **toute** la liste d'entries et on rappelle `getLocalizedAmount` sur celles qui matchent. Une entry portant 3 tags voit donc son montant recalculé 3 fois — et, **si sa devise diffère de la cible**, l'appel réseau vers `rate` qui va avec est lui aussi rejoué 3 fois (le court-circuit du §3.1bis s'applique à chaque appel individuellement, il ne supprime pas la répétition). Le nombre d'appels DB n'est plus O(entries non-CHF) mais **O(Σ tags par entry non-CHF)**, ce qui peut dépasser largement les ~11'000 requêtes de base selon la densité de tagging — les 19'000 entries CHF, elles, restent gratuites quel que soit le nombre de fois où elles sont retraitées.

### 3.3 — Suspects secondaires (non confirmés, à vérifier)

Deux autres sources potentielles de N+1, indépendantes du problème `rate` mais qui s'additionneraient au même effet :

- **`Entry.currency`** (`Entry.java`) est un `@ManyToOne` **EAGER** sans `JOIN FETCH` explicite dans la `Specification` utilisée par `EntryService.getEntries` (`EntryServiceImpl.java:27` → `entryRepository.findAll(specification, pageable)`). Le comportement par défaut d'Hibernate pour une association *to-one* eager sans fetch join est `FetchMode.SELECT` : une requête séparée par entity chargée, sauf batching activé (`hibernate.default_batch_fetch_size`, absent de la config actuelle).
- **`Entry.tags`** est un `@ManyToMany` **LAZY**, également sans fetch join. Chaque accès à `entry.getTags()` (utilisé dans `getTagAmounts`) peut déclencher une requête par entry si la collection n'a pas déjà été initialisée en amont.

Ces deux points n'ont pas été confirmés par capture de requêtes SQL (pas de session `show-sql` analysée dans ce diagnostic) mais méritent une vérification avant de considérer le sujet clos — voir §6, plan de validation.

---

## 4. Ce qui n'est PAS en cause : le schéma et l'index

Point important pour éviter une fausse piste côté DB : **l'index `idx_rate_currencies_date (source_currency_code, target_currency_code, value_date)` est correctement conçu** pour cette requête.

- Les deux premières colonnes de l'index correspondent exactement aux deux égalités du `WHERE` (`source_currency_code = :source AND target_currency_code = :target`).
- La troisième colonne (`value_date`) correspond à la fois au filtre `<=` et à l'`ORDER BY ... DESC`, ce qui permet à MariaDB (InnoDB) de faire un **index range scan en ordre inverse avec `LIMIT 1`**, sans tri (`filesort`) et sans accès à la table (si on ne projetait que des colonnes indexées — ici on remonte l'entité complète donc il y a un lookup par clé primaire derrière, mais celui-ci est trivial).
- La contrainte `UNIQUE (source_currency_code, target_currency_code, value_date)` garantit en plus qu'il n'y a jamais qu'une seule ligne par triplet (paire de devises, date), donc le volume de la table `rate` reste borné et petit (≈ nb\_devises² × nb\_jours), typiquement quelques milliers de lignes au plus.

**Conclusion DB** : chaque requête individuelle est probablement exécutée en moins d'une milliseconde côté moteur. Ajouter un index ne changera rien. Le coût vient exclusivement du **nombre** d'allers-retours, pas de leur coût unitaire.

---

## 5. Chiffrage (ordre de grandeur)

| Facteur | Valeur estimée |
|---|---|
| Nombre d'entries | 30'000 |
| Entries déjà dans la devise cible (CHF, court-circuit §3.1bis) | 19'000 (0 requête) |
| Entries en devise étrangère (déclenchent `rate`) | 11'000 |
| Requêtes `rate` dans `computeSum` (cible = CHF) | ~11'000 |
| Requêtes `rate` dans `getTagAmounts` | ~11'000 × (tags moyens par entry non-CHF) |
| Coût réseau + JDBC par aller-retour (local/Docker) | ~0.5 – 2 ms |
| Coût réseau + JDBC par aller-retour (hors localhost) | 2 – 10+ ms |
| Total pour 11'000 requêtes séquentielles à 2 ms (`computeSum` seul) | **~22 secondes** |

`computeSum` seul (~22 s) n'atteint pas déjà la minute observée. Deux compléments l'expliquent :

1. Si l'écran/l'appel enchaîne `compute` **et** `computeTagAmounts` sur la même requête (cas probable pour l'affichage d'un total + d'une grille par tag), le coût de `getTagAmounts` s'additionne — et lui est multiplié par le nombre moyen de tags par entry non-CHF, ce qui peut facilement repasser au-dessus de la minute même avec seulement 11'000 entries de base.
2. Les suspects secondaires du §3.3 (`Entry.currency` eager sans fetch join, `Entry.tags` lazy), s'ils sont confirmés, ajoutent leur propre volume de requêtes à l'hydratation des 30'000 entries — indépendamment de la devise, donc sur la totalité du jeu, pas seulement les 11'000.

Le fait que le temps soit variable d'une exécution à l'autre (latence réseau, charge du pool, cache OS/InnoDB buffer pool) reste cohérent avec un goulot d'étranglement par volume d'appels plutôt qu'un plan de requête pathologique — un mauvais plan donnerait un temps constant et élevé par requête, pas un effet cumulatif.

---

## 6. Plan de remédiation proposé

### 6.1 Précharger les taux en une fois

Toute conversion passe nécessairement par la devise de référence (source ou target = référence, cf. commentaire dans `getLocalizedAmount`). On peut donc précharger **tous les taux pertinents en 2 requêtes fixes**, indépendamment du nombre d'entries, en réutilisant les méthodes déjà existantes sur `RateService` :

```java
List<Rate> outgoing = rateService.getRatesBySourceCurrency(referenceCurrency.getCode());
List<Rate> incoming = rateService.getRatesByTargetCurrency(referenceCurrency.getCode());
```

Puis construire un index en mémoire par devise, trié par date, permettant de reproduire le comportement « closest rate before » via une recherche `floor` (O(log n) en mémoire, 0 aller-retour DB) :

```java
Map<String, TreeMap<LocalDate, BigDecimal>> ratesByCurrency = ...;
// lookup :
BigDecimal rate = ratesByCurrency.get(currencyCode).floorEntry(date).getValue();
```

### 6.2 Mémoïser le montant converti par entry

Calculer `getLocalizedAmount` **une seule fois par entry**, avant la boucle sur les tags, et réutiliser le résultat dans `computeSum` et `getTagAmounts` :

```java
Map<Entry, BigDecimal> amountByEntry = entries.stream()
      .collect(Collectors.toMap(e -> e, e -> getLocalizedAmount(e, targetCurrencyCode)));
```

`getTagAmounts` n'a alors plus qu'à sommer des valeurs déjà calculées, sans jamais retoucher la conversion.

### 6.3 (À confirmer) Fetch join sur `currency` et `tags`

Si la vérification du §3.3 confirme un N+1 à l'hydratation des entries elles-mêmes, ajouter un `JOIN FETCH` (ou une `@EntityGraph`) sur `currency` et `tags` dans la requête portée par la `Specification`, pour ramener leur chargement à 1 requête au lieu de N.

### Effet attendu

Avec 6.1 + 6.2, le calcul passe de **O(entries) requêtes SQL séquentielles** à **2 requêtes fixes + O(entries) opérations en mémoire**. Le temps de traitement pour 30'000 entries devrait tomber à l'ordre de la centaine de millisecondes, dominé par le chargement initial des entries plutôt que par le calcul.

---

## 7. Plan de validation

1. **Compter les requêtes avant/après** : activer `hibernate.generate_statistics=true` (ou une session `p6spy`) sur un environnement de test, lancer le calcul sur un jeu de 30'000 entries, relever le nombre de requêtes exécutées. Attendu avant fix : proche de N (voire N × tags moyens). Attendu après fix : constant (~2 requêtes `rate` + 1 requête `entry`, plus éventuellement 1-2 pour `currency`/`tags` si le fetch join est ajouté).
2. **Mesurer le temps de bout en bout** avant/après sur le même jeu de données, dans les mêmes conditions (même environnement, à froid puis à chaud pour neutraliser l'effet du buffer pool InnoDB).
3. **`EXPLAIN ANALYZE`** sur la requête `findClosestRateBefore` pour confirmer qu'elle utilise bien `idx_rate_currencies_date` (type `ref` ou `range`, pas de `Using filesort`) — sert de baseline pour écarter définitivement une cause côté schéma.

---

## 8. Recommandations complémentaires

- **`spring.jpa.show-sql: true` en production** (`application-production.yml`) génère un log par requête. À ce volume d'appels (30'000+), c'est un coût I/O et CPU non négligeable en soi, indépendamment du problème principal. À désactiver en production, ou remplacer par un seuil de requêtes lentes (`hibernate.session.events.log.LOG_QUERIES_SLOWER_THAN_MS`).
- **Taille du pool HikariCP** : non surchargée, donc 10 connexions par défaut. Ce n'est pas la cause du problème actuel (une seule requête HTTP = un seul thread = une seule connexion utilisée séquentiellement), mais vaut la peine d'être revu si le préchargement (§6.1) est un jour parallélisé.
- **Borne de croissance de `rate`** : la contrainte unique garantit une croissance linéaire en (nb devises² × nb jours), donc le préchargement complet proposé en §6.1 restera bon marché même avec plusieurs années d'historique et une dizaine de devises. À réévaluer seulement si le nombre de devises supportées devient très important (> quelques dizaines).

---

## 9. Prochaine étape

Implémentation de 6.1 et 6.2 dans `ComputationServiceImpl` (et ajout d'une méthode utilitaire de recherche « closest before » en mémoire, potentiellement dans `service/helper/`). Vérification du §3.3 à faire en parallèle via capture de requêtes SQL sur un jeu de données réaliste.

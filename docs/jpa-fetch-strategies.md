# Comprendre le chargement des associations JPA — `EAGER`, `LAZY`, `JOIN FETCH`, N+1

**Objectif** : expliquer pourquoi certaines relations génèrent une requête SQL par ligne (le fameux « N+1 »), pourquoi `EAGER` ne suffit pas à s'en protéger, et comment corriger ça — avec des exemples pris directement dans ce projet, dont le cas concret déjà observé en log : un nombre de requêtes `tag_image` disproportionné.

---

## 1. Vocabulaire de base

Une **association JPA**, c'est un champ d'entité annoté `@ManyToOne`, `@OneToOne`, `@OneToMany` ou `@ManyToMany`, qui pointe vers une autre entité (ou une collection d'entités).

Deux questions distinctes se posent pour chaque association :

1. **Quand** est-elle chargée ? → c'est le `FetchType` (`EAGER` = tout de suite, en même temps que l'entité propriétaire ; `LAZY` = seulement au premier accès au champ, via un proxy Hibernate).
2. **Comment** est-elle chargée, techniquement, en SQL ? → c'est indépendant du `FetchType` et c'est la source de confusion la plus fréquente.

**`EAGER` ne veut pas dire « dans la même requête SQL »**. Ça veut juste dire « ne pas attendre qu'on accède au champ pour la charger ». Par défaut, Hibernate charge une association eager en émettant une **requête SQL séparée**, juste après celle qui a chargé l'entité principale. Si vous chargez 100 entités avec une association `@ManyToOne` eager, vous obtenez 1 requête pour les 100 entités... puis potentiellement 100 requêtes de plus, une par association, sauf configuration contraire (voir §4).

C'est ça, le problème N+1 : **1** requête pour charger la liste, **N** requêtes supplémentaires pour chaque association individuelle.

| Annotation | `FetchType` par défaut |
|---|---|
| `@ManyToOne` | `EAGER` |
| `@OneToOne` | `EAGER` |
| `@OneToMany` | `LAZY` |
| `@ManyToMany` | `LAZY` |

---

## 2. Le cas déjà observé : les requêtes `tag_image`

C'est le plus instructif des deux cas, parce que ce n'est **même pas** un problème de `FetchType` JPA — il n'y a **aucune association JPA** entre `Tag` et `TagImage`. Regardez `Tag.java` : pas de champ `tagImage`. Le lien n'existe que côté `TagImage` :

```java
// TagImage.java
@OneToOne
@JoinColumn(name = "tag_id")
private Tag tag;
```

La récupération de l'image se fait donc « à la main », via un appel de service, depuis le mapper :

```java
// TagMapperImpl.java
public TagDto toDto(Tag tag) {
      TagDto tagDto = TagDto.builder()
            ...
            .icon(tagImageService.getTagImage(tag) != null ? tagImageService.getTagImage(tag).getImagePath() : null)
            ...
            .build();
      return tagDto;
}
```

Deux problèmes cumulés ici :

1. **`tagImageService.getTagImage(tag)` est appelé deux fois** — une fois pour le test de nullité, une fois pour lire `getImagePath()`. Chaque appel refait la requête :

      ```java
      // TagImageServiceImpl.java
      public TagImage getTagImage(Tag tag) {
            return tagImageRepository.findByTag(tag).orElse(null);
      }
      ```

      → **2 requêtes `SELECT ... FROM tag_image WHERE tag_id = ?`** pour un seul tag mappé.

2. **`toDto` est appelé tag par tag, dans une boucle**, depuis au moins 5 endroits du code :

      ```java
      // TagController.java
      .map(tagMapper::toDto)

      // EntryMapperImpl.java (une fois par entry !)
      entry.getTags().stream().map(tag -> tagMapper.toDto(tag))

      // RecurrenceMapperImpl.java, TagGroupMapperImpl.java, TagAmountMapperImpl.java
      .map(tagMapper::toDto)
      ```

      Le pire cas est `EntryMapperImpl` : à chaque entry mappée, on remappe **tous ses tags individuellement**. Pour une liste de 50 entries ayant chacune 3 tags, ça fait potentiellement `50 × 3 × 2 = 300` requêtes vers `tag_image`, pour au final récupérer une poignée d'images distinctes.

C'est exactement le pattern qui produit un « nombre incalculable de requêtes » dans les logs : ce n'est pas une association mal configurée, c'est **une requête par élément, exécutée depuis une boucle de mapping appelée à plusieurs endroits**.

### Correctif

**Étape 1 (gain immédiat, aucun changement d'architecture)** : ne plus appeler le service deux fois.

```java
TagImage tagImage = tagImageService.getTagImage(tag);
String icon = tagImage != null ? tagImage.getImagePath() : null;
```

→ divise déjà le nombre de requêtes par 2, sans rien changer d'autre.

**Étape 2 (le vrai fix) : sortir la requête de la boucle, la batcher.**

Ajouter une méthode qui récupère toutes les images en une seule requête pour un ensemble de tags :

```java
// TagImageRepository.java
List<TagImage> findByTagIn(Collection<Tag> tags);
```

Puis exposer sur `TagMapper` une méthode de mapping **de liste**, qui précharge une fois et distribue ensuite en mémoire :

```java
// TagMapper.java
default List<TagDto> toDtoList(List<Tag> tags) {
      Map<Long, String> imageByTagId = tagImageService.getTagImages(tags).stream()
            .collect(Collectors.toMap(ti -> ti.getTag().getId(), TagImage::getImagePath));

      return tags.stream()
            .map(tag -> toDto(tag, imageByTagId.get(tag.getId())))
            .toList();
}
```

Et remplacer les 5 `.stream().map(tagMapper::toDto)` par `tagMapper.toDtoList(tags)`. On passe de **N requêtes** à **1 requête**, quel que soit le nombre de tags à mapper.

---

## 3. Le cas classique : `Entry.currency` (eager) et `Entry.tags` (lazy)

Ici, contrairement au cas précédent, il y a bien des associations JPA, chargées via `entryRepository.findAll(specification, pageable)` (`EntrySpecificationMapperImpl` / `EntryServiceImpl`), **sans aucun `JOIN FETCH`** dans la `Specification` construite.

```java
// Entry.java
@ManyToOne                                    // EAGER par défaut
@JoinColumn(name = "currency_code")
private Currency currency;

@ManyToMany                                   // LAZY par défaut
@JoinTable(name = "entry_tag", ...)
private Set<Tag> tags = new HashSet<>();
```

- **`currency`** est eager : Hibernate va la charger sans qu'on ait besoin d'appeler `getCurrency()`. Mais eager ≠ joint (§1) : sans fetch join explicite, chaque entry charge sa `Currency` via une requête séparée (`FetchMode.SELECT`, le mode par défaut). Pour 30'000 entries avec, disons, 5 devises différentes, on n'a évidemment pas besoin de 30'000 requêtes différentes en contenu — mais sans configuration particulière, Hibernate ne le sait pas et peut en émettre une par entry (ou par lot, voir §4).
- **`tags`** est lazy : rien n'est chargé tant qu'on n'appelle pas `entry.getTags()`. Mais ce code l'appelle systématiquement (calcul des tag amounts, mapping vers `EntryDto`...), donc le report de chargement ne fait que déplacer le problème : la requête a lieu plus tard, mais toujours une fois par entry.

### Pourquoi on ne corrige pas les deux de la même façon

C'est le point le plus important de cette section, et une source d'erreur classique : **la bonne correction dépend de la cardinalité de l'association**, pas seulement de son `FetchType`.

#### `currency` (`@ManyToOne`, cardinalité 1) → `JOIN FETCH` sans risque

Joindre une association *to-one* ne peut jamais dupliquer la ligne principale : une entry a exactement une devise. On peut donc forcer le join dans la `Specification`, via l'API Criteria :

```java
// dans EntrySpecificationMapperImpl, par exemple en tête de toEntity(...)
return (root, query, cb) -> {
      if (Long.class != query.getResultType() && root.getModel().getJavaType() == Entry.class) {
            root.fetch("currency", JoinType.LEFT);
      }
      return cb.conjunction();
};
```

Résultat : 1 seule requête SQL pour charger les entries **et** leur devise, quel que soit le nombre d'entries.

#### `tags` (`@ManyToMany`, cardinalité N) → `JOIN FETCH` dangereux avec pagination

Ici, un fetch join **duplique la ligne d'`Entry`** autant de fois qu'elle a de tags (produit cartésien) : une entry avec 3 tags redevient 3 lignes dans le `ResultSet`. Deux conséquences :

- Le `Pageable` de `EntryController.getEntries` (une vraie pagination côté DB) ne fonctionne plus correctement : Hibernate log un avertissement (`HHH000104: firstResult/maxResults specified with collection fetch; applying in memory`) et **charge toute la table en mémoire pour paginer lui-même**, ce qui est pire que le problème de départ.
- Un `COUNT` sur la specification renverrait un nombre de lignes gonflé par les doublons.

**Ne jamais faire de `JOIN FETCH` sur une collection en présence de pagination.** La bonne solution pour une collection lazy est le **batch fetching** (§4).

---

## 4. Le batch fetching : la solution générale pour les collections (et une alternative simple pour le `to-one`)

Plutôt que de traiter chaque association au cas par cas, Hibernate propose un réglage global : au lieu d'émettre une requête par entité pour charger une association lazy, il regroupe plusieurs identifiants dans une seule requête `IN (...)`.

```yaml
# application-*.yml
spring:
  jpa:
    properties:
      hibernate:
        default_batch_fetch_size: 25
```

Concrètement, pour 30'000 entries avec 5 devises différentes, au lieu de :

```sql
SELECT * FROM currency WHERE code = 'CHF';
SELECT * FROM currency WHERE code = 'EUR';
-- ... répété jusqu'à 30 000 fois selon l'ordre de chargement
```

Hibernate regroupe :

```sql
SELECT * FROM currency WHERE code IN ('CHF', 'EUR', 'USD', ..., 25 valeurs);
-- répété seulement tant qu'il reste des identifiants à couvrir
```

Le même mécanisme s'applique aux collections (`entry.getTags()`), et transforme un N+1 en `⌈N / batch_size⌉` requêtes, **sans dupliquer aucune ligne** (contrairement au fetch join sur collection), donc **compatible avec la pagination**.

C'est un réglage global, à poser une fois, qui corrige d'un coup toutes les associations lazy du projet non traitées explicitement par un fetch join — y compris des cas qu'on n'a pas encore identifiés. On peut aussi l'affiner association par association avec `@BatchSize(size = 25)` directement sur le champ, si on veut une valeur différente pour un cas précis.

---

## 5. Comment vérifier, avant et après

Impossible de savoir si un correctif fonctionne sans compter les requêtes réellement émises, pas en déduire depuis le temps écoulé (trop de facteurs annexes : cache OS, charge réseau...).

- **Le plus simple ici** : `spring.jpa.show-sql: true` est déjà actif (`application-production.yml`, `application-local.yml`). Lancer un scénario reproductible (ex. lister 50 entries avec leurs tags), compter les lignes `Hibernate:` dans le log, avant et après le correctif.
- **Plus rigoureux** : activer les statistiques Hibernate, qui donnent un compte exact sans avoir à compter des lignes de log à la main :

```yaml
spring:
  jpa:
    properties:
      hibernate:
        generate_statistics: true
logging:
  level:
    org.hibernate.stat: debug
```

Le log affiche alors, à la fin de chaque session, un résumé du type `...queries executed: 312...` — la métrique à surveiller avant/après.

---

## 6. Pièges à connaître pour la suite

- **`MultipleBagFetchException`** : si on tente de faire un `JOIN FETCH` sur *deux* collections de type `List` en même temps sur la même entité (par ex. `tags` et une future collection `attachments`), Hibernate refuse — impossible de représenter deux produits cartésiens simultanés proprement. Utiliser `Set` plutôt que `List` pour les collections fetch-jointes, ou ne fetch-joindre qu'une seule collection et batcher les autres.
- **`default_batch_fetch_size` trop bas** (ex. 5) : le gain existe mais reste modeste. **Trop haut** (ex. 500) : la clause `IN (...)` devient énorme, ce qui a aussi un coût de parsing côté moteur. Une valeur entre 20 et 50 est un bon point de départ standard.
- **Un `@OneToOne` inverse (`mappedBy`) ne peut pas être vraiment lazy** sans instrumentation de bytecode (`spring-boot-starter` ne l'active pas par défaut) : Hibernate ne peut pas savoir, sans requêter, si la ligne associée existe ou non, donc il ne peut pas se contenter de poser un proxy. C'est une des raisons pour lesquelles le lien `Tag → TagImage` n'a pas été modélisé comme une vraie association bidirectionnelle dans ce projet — et pourquoi le correctif proposé au §2 passe par un batch applicatif plutôt que par l'ajout d'un champ `@OneToOne(mappedBy = "tag")` sur `Tag`.

---

## 7. Résumé des actions proposées pour ce projet

| Association | Cardinalité | Constat actuel | Correctif proposé |
|---|---|---|---|
| `Tag` → `TagImage` | 1–1, pas de mapping JPA | Requête dupliquée (×2) par tag, dans une boucle appelée à 5 endroits | Batcher via `findByTagIn` + `TagMapper.toDtoList` (§2) |
| `Entry.currency` | `@ManyToOne` | Eager mais sans join → 1 requête par entry (ou par lot) | `JOIN FETCH` dans la `Specification` (§3), sans risque de duplication |
| `Entry.tags` | `@ManyToMany` | Lazy, chargé au premier accès, par entry | **Ne pas** fetch-joindre (casse la pagination) ; activer `default_batch_fetch_size` (§4) |

Aucun de ces changements n'a été appliqué — ce document sert de base de décision. Dis-moi lesquels tu veux que j'implémente.

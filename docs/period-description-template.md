# Description de période avec expressions calculées

Spécification d'implémentation pour le backend `cashtag` (Spring Boot 3.5, Java 21) et le frontend `cashvue` (Vue 3, Orval).
Rédigée à partir de l'état de `main` au commit `b51cc1b` (merge #57 `feature/currency-map`, 10.10.2026).

Branche suggérée : `feature/period-description`.

---

## 1. Objectif

Ajouter un champ texte libre `description` à l'entité `Period`. Ce texte peut contenir des blocs `{{ ... }}` évalués côté backend et remplacés par des montants calculés sur les entries de la période.

### Exemples

Exemple 1, montant simple :

```
J'ai dépensé "{{ tags: restaurant }}" ce mois-ci
```

Rendu : `J'ai dépensé "1'234.50 CHF" ce mois-ci`

Exemple 2, variables et calcul :

```
{{ MontantRestaurant = tags: restaurant }}
{{ MontantRestaurantItalie = tags: italie, restaurant }}
J'ai dépensé {{ MontantRestaurant - MontantRestaurantItalie }} en restaurant quand je n'étais pas en Italie
```

Rendu : `J'ai dépensé 812.00 CHF en restaurant quand je n'étais pas en Italie`
(les deux lignes d'affectation disparaissent du rendu, retour à la ligne compris).

Exemple 2 bis, même résultat avec l'exclusion de tag :

```
J'ai dépensé {{ tags: restaurant, !italie }} en restaurant quand je n'étais pas en Italie
```

---

## 2. Règles fonctionnelles

- Les dates de calcul sont toujours `period.startDate` et `period.endDate` (pas de dates dans la syntaxe).
- Le calcul réutilise exactement la logique de l'endpoint computation existant (`ComputationController.compute`) :
  `ComputationSpecBuilder.from(request)` puis `entryService.getEntries(spec, Pageable.unpaged())` puis `computationService.computeSum(entries, targetCurrencyCode).totalAmount()`.
  On appelle les services, jamais l'endpoint HTTP.
- Plusieurs tags dans `tags: a, b` = sémantique ET (entries ayant tous les tags), via `hasAllTags` de `EntrySpecificationMapperImpl`.
- `!tag` = tag exclu, via `excludeTags` (champ `excludedTags` de `ComputationRequestDto`).
- La règle des tags cumulatifs est héritée automatiquement de `ComputationSpecBuilder` (un seul tag inclus et cumulatif : date de début ignorée). Comportement identique à l'endpoint computation.
- Devise cible : `period.getCurrency().getCode()` si la période a une devise, sinon `currencyService.getReferenceCurrency().getCode()`.
- Les tags sont résolus par leur titre via `TagRepository.findByTitle(String)`. Tag introuvable = erreur affichée dans le bloc.
- Les variables sont partagées entre les blocs d'une même description et évaluées dans l'ordre du texte. Une variable doit être définie avant d'être utilisée.
- Un bloc d'affectation ne produit aucun texte et supprime le retour à la ligne qui le suit immédiatement.
- Un bloc d'expression produit le montant formaté en `de-CH` avec 2 décimales et le code devise : `1'234.50 CHF`.
- Une erreur dans un bloc n'interrompt pas le rendu : le bloc est remplacé par `[message d'erreur]`, les autres blocs sont rendus normalement.
- Une même requête de tags (mêmes inclus et exclus) n'est exécutée qu'une fois par rendu (cache dans l'évaluateur).
- Le rendu n'est calculé qu'à la demande via un endpoint dédié, pas dans la liste des périodes (évite N calculs à chaque `GET /periods`).

---

## 3. Grammaire

```
statement := IDENT '=' expr | expr
expr      := term (('+' | '-') term)*
term      := factor (('*' | '/') factor)*
factor    := '-' factor | NUMBER | IDENT | '(' expr ')' | tagQuery
tagQuery  := 'tags' ':' tagItem (',' tagItem)*
tagItem   := '!'? tagName
tagName   := IDENT | STRING
```

- `IDENT` : commence par une lettre (accents acceptés) ou `_`, puis lettres, chiffres, `_`.
- `NUMBER` : chiffres avec point décimal optionnel (`12`, `0.5`).
- `STRING` : texte entre guillemets doubles, pour les noms de tags contenant espaces ou tirets : `tags: "fast-food"`.
- `tags` est un mot réservé (insensible à la casse), il ne peut pas servir de nom de variable.
- Précédence standard : `*` et `/` avant `+` et `-`, parenthèses supportées, moins unaire supporté.
- Le contenu de chaque bloc doit être consommé entièrement (sinon erreur de syntaxe).

---

## 4. Architecture

```
PeriodController (GET /periods/{id}/description/rendered)
  -> PeriodService.getById
  -> PeriodDescriptionRenderer.render(Period)
       pour chaque bloc {{ ... }} :
         Lexer -> List<Token>
         Parser -> Statement (AST)
         Evaluator.execute(Statement)
           TagQuery -> fonction tagSum fournie par le renderer
             -> TagRepository.findByTitle + TagMapper.toDto
             -> ComputationSpecBuilder.from(ComputationRequestDto)
             -> EntryService.getEntries(spec, unpaged)
             -> ComputationService.computeSum(entries, target).totalAmount()
```

Lexer, Parser, Evaluator sont des classes Java pures (sans Spring), testables unitairement sans mock.

### Fichiers à créer

| Fichier | Rôle |
|---|---|
| `service/template/TokenType.java` | enum des tokens |
| `service/template/Token.java` | record token |
| `service/template/TemplateException.java` | erreur de syntaxe ou d'évaluation |
| `service/template/Expr.java` | AST des expressions |
| `service/template/Statement.java` | AST des instructions |
| `service/template/Lexer.java` | découpage en tokens |
| `service/template/Parser.java` | parser récursif descendant |
| `service/template/Evaluator.java` | évaluation, variables, cache |
| `service/PeriodDescriptionRenderer.java` | service Spring, branchement sur le calcul |
| `dto/RenderedDescriptionDto.java` | DTO de réponse |
| `db/changelog/2026-10/db.changelog-17.xml` | ajout de la colonne |
| tests (section 9) | |

Tous les chemins Java sont relatifs à `src/main/java/com/brieuc/cashtag/`.

### Fichiers à modifier

| Fichier | Modification |
|---|---|
| `entity/Period.java` | champ `description` |
| `dto/PeriodDto.java` | champ `description` |
| `mapper/PeriodMapperImpl.java` | mapping `description` dans `toEntity` et `toDto` (mapper manuel) |
| `service/PeriodServiceImpl.java` | `setDescription` dans `update` |
| `controller/api/PeriodApi.java` | déclaration du nouvel endpoint |
| `controller/PeriodController.java` | implémentation du nouvel endpoint |
| `db/changelog/db.changelog-master.xml` | include du nouveau changeset |

---

## 5. Base de données et entité

### 5.1 Liquibase

Créer `src/main/resources/db/changelog/2026-10/db.changelog-17.xml`.
Le plus grand id de changeSet existant est `19` : utiliser `20` (vérifier avant avec `grep -rn 'changeSet' src/main/resources/db/changelog`).

```xml
<?xml version="1.0" encoding="UTF-8"?>
<databaseChangeLog
   xmlns="http://www.liquibase.org/xml/ns/dbchangelog"
   xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
   xsi:schemaLocation="http://www.liquibase.org/xml/ns/dbchangelog http://www.liquibase.org/xml/ns/dbchangelog/dbchangelog-3.5.xsd">

    <!-- Add description column to period table (template with {{ }} expressions) -->
    <changeSet id="20" author="Brieuc">
        <addColumn tableName="period">
            <column name="description" type="TEXT">
                <constraints nullable="true"/>
            </column>
        </addColumn>
    </changeSet>

</databaseChangeLog>
```

Ajouter à la fin de `db.changelog-master.xml` :

```xml
    <include file="db/changelog/2026-10/db.changelog-17.xml"/>
```

### 5.2 Entité `Period`

```java
    @Column(columnDefinition = "TEXT")
    private String description;
```

### 5.3 `PeriodDto`

```java
    @Schema(description = "Free text of the period, may contain {{ }} expressions evaluated on demand",
            example = "J'ai dépensé {{ tags: restaurant }} ce mois-ci", nullable = true)
    private String description;
```

### 5.4 `PeriodMapperImpl`

Dans `toEntity` : `.description(periodDto.getDescription())`
Dans `toDto` : `.description(period.getDescription())`

### 5.5 `PeriodServiceImpl.update`

Ajouter, sinon la description n'est jamais mise à jour :

```java
        period.setDescription(detachedPeriod.getDescription());
```

---

## 6. Moteur de template (package `com.brieuc.cashtag.service.template`)

### 6.1 `TokenType.java`

```java
package com.brieuc.cashtag.service.template;

public enum TokenType {
    NUMBER, IDENT, STRING, TAGS,
    COLON, COMMA, BANG,
    PLUS, MINUS, STAR, SLASH,
    LPAREN, RPAREN, EQUALS,
    EOF
}
```

### 6.2 `Token.java`

```java
package com.brieuc.cashtag.service.template;

public record Token(TokenType type, String text, int pos) {}
```

### 6.3 `TemplateException.java`

```java
package com.brieuc.cashtag.service.template;

public class TemplateException extends RuntimeException {
    public TemplateException(String message) {
        super(message);
    }
}
```

### 6.4 `Expr.java`

```java
package com.brieuc.cashtag.service.template;

import java.math.BigDecimal;
import java.util.List;

public sealed interface Expr {
    record Num(BigDecimal value) implements Expr {}
    record Var(String name) implements Expr {}
    record TagQuery(List<String> included, List<String> excluded) implements Expr {}
    record Neg(Expr inner) implements Expr {}
    record BinOp(char op, Expr left, Expr right) implements Expr {}
}
```

`TagQuery` est un record : il sert directement de clé de cache (égalité structurelle).

### 6.5 `Statement.java`

```java
package com.brieuc.cashtag.service.template;

public sealed interface Statement {
    record Assign(String name, Expr expr) implements Statement {}
    record Print(Expr expr) implements Statement {}
}
```

### 6.6 `Lexer.java`

```java
package com.brieuc.cashtag.service.template;

import java.util.ArrayList;
import java.util.List;
import java.util.function.IntPredicate;

public final class Lexer {

    private final String src;
    private int i = 0;

    public Lexer(String src) {
        this.src = src;
    }

    public List<Token> tokenize() {
        List<Token> out = new ArrayList<>();
        while (i < src.length()) {
            char c = src.charAt(i);
            if (Character.isWhitespace(c)) {
                i++;
                continue;
            }
            int start = i;
            switch (c) {
                case '+' -> single(out, TokenType.PLUS);
                case '-' -> single(out, TokenType.MINUS);
                case '*' -> single(out, TokenType.STAR);
                case '/' -> single(out, TokenType.SLASH);
                case '(' -> single(out, TokenType.LPAREN);
                case ')' -> single(out, TokenType.RPAREN);
                case ',' -> single(out, TokenType.COMMA);
                case ':' -> single(out, TokenType.COLON);
                case '=' -> single(out, TokenType.EQUALS);
                case '!' -> single(out, TokenType.BANG);
                case '"' -> out.add(new Token(TokenType.STRING, readString(), start));
                default -> {
                    if (Character.isDigit(c)) {
                        out.add(new Token(TokenType.NUMBER,
                                readWhile(ch -> Character.isDigit(ch) || ch == '.'), start));
                    } else if (Character.isLetter(c) || c == '_') {
                        String word = readWhile(ch -> Character.isLetterOrDigit(ch) || ch == '_');
                        TokenType type = word.equalsIgnoreCase("tags") ? TokenType.TAGS : TokenType.IDENT;
                        out.add(new Token(type, word, start));
                    } else {
                        throw new TemplateException("Caractère inattendu '" + c + "' en position " + i);
                    }
                }
            }
        }
        out.add(new Token(TokenType.EOF, "", i));
        return out;
    }

    private void single(List<Token> out, TokenType type) {
        out.add(new Token(type, String.valueOf(src.charAt(i)), i));
        i++;
    }

    private String readWhile(IntPredicate p) {
        int start = i;
        while (i < src.length() && p.test(src.charAt(i))) {
            i++;
        }
        return src.substring(start, i);
    }

    private String readString() {
        int start = ++i; // skip opening quote
        while (i < src.length() && src.charAt(i) != '"') {
            i++;
        }
        if (i >= src.length()) {
            throw new TemplateException("Guillemet fermant manquant");
        }
        return src.substring(start, i++); // skip closing quote
    }
}
```

### 6.7 `Parser.java`

```java
package com.brieuc.cashtag.service.template;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public final class Parser {

    private final List<Token> tokens;
    private int p = 0;

    public Parser(List<Token> tokens) {
        this.tokens = tokens;
    }

    public Statement parseStatement() {
        Statement s;
        // tokens always ends with EOF, so p + 1 is safe when peek() is IDENT
        if (peek().type() == TokenType.IDENT && tokens.get(p + 1).type() == TokenType.EQUALS) {
            String name = next().text();
            next(); // '='
            s = new Statement.Assign(name, expr());
        } else {
            s = new Statement.Print(expr());
        }
        expect(TokenType.EOF);
        return s;
    }

    private Expr expr() {
        Expr left = term();
        while (peek().type() == TokenType.PLUS || peek().type() == TokenType.MINUS) {
            char op = next().text().charAt(0);
            left = new Expr.BinOp(op, left, term());
        }
        return left;
    }

    private Expr term() {
        Expr left = factor();
        while (peek().type() == TokenType.STAR || peek().type() == TokenType.SLASH) {
            char op = next().text().charAt(0);
            left = new Expr.BinOp(op, left, factor());
        }
        return left;
    }

    private Expr factor() {
        Token tok = next();
        return switch (tok.type()) {
            case MINUS -> new Expr.Neg(factor());
            case NUMBER -> new Expr.Num(new BigDecimal(tok.text()));
            case IDENT -> new Expr.Var(tok.text());
            case LPAREN -> {
                Expr e = expr();
                expect(TokenType.RPAREN);
                yield e;
            }
            case TAGS -> tagQuery();
            default -> throw new TemplateException("Expression attendue en position " + tok.pos());
        };
    }

    private Expr tagQuery() {
        expect(TokenType.COLON);
        List<String> included = new ArrayList<>();
        List<String> excluded = new ArrayList<>();
        do {
            boolean exclude = accept(TokenType.BANG);
            Token tok = next();
            if (tok.type() != TokenType.IDENT && tok.type() != TokenType.STRING) {
                throw new TemplateException("Nom de tag attendu en position " + tok.pos());
            }
            (exclude ? excluded : included).add(tok.text());
        } while (accept(TokenType.COMMA));
        return new Expr.TagQuery(List.copyOf(included), List.copyOf(excluded));
    }

    private Token peek() {
        return tokens.get(p);
    }

    private Token next() {
        return tokens.get(p++);
    }

    private boolean accept(TokenType type) {
        if (peek().type() == type) {
            p++;
            return true;
        }
        return false;
    }

    private void expect(TokenType type) {
        if (!accept(type)) {
            throw new TemplateException(type + " attendu en position " + peek().pos());
        }
    }
}
```

Note : `next()` sur EOF puis un nouvel appel dépasserait la liste. Ce cas n'arrive pas car `factor()` lève une exception sur EOF (cas `default`) et `tagQuery()` aussi. À couvrir par un test (`{{ }}`, `{{ 1 + }}`, `{{ tags: }}`).

### 6.8 `Evaluator.java`

```java
package com.brieuc.cashtag.service.template;

import java.math.BigDecimal;
import java.math.MathContext;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;

/**
 * Evaluates statements of one description. One instance per rendering:
 * holds the variables and the cache of tag queries.
 */
public final class Evaluator {

    private final Map<String, BigDecimal> vars = new HashMap<>();
    private final Map<Expr.TagQuery, BigDecimal> cache = new HashMap<>();
    private final Function<Expr.TagQuery, BigDecimal> tagSum;

    public Evaluator(Function<Expr.TagQuery, BigDecimal> tagSum) {
        this.tagSum = tagSum;
    }

    /** @return the value to display, empty for an assignment */
    public Optional<BigDecimal> execute(Statement s) {
        return switch (s) {
            case Statement.Assign a -> {
                vars.put(a.name(), eval(a.expr()));
                yield Optional.empty();
            }
            case Statement.Print pr -> Optional.of(eval(pr.expr()));
        };
    }

    private BigDecimal eval(Expr e) {
        return switch (e) {
            case Expr.Num n -> n.value();
            case Expr.Var v -> Optional.ofNullable(vars.get(v.name()))
                    .orElseThrow(() -> new TemplateException("Variable inconnue : " + v.name()));
            case Expr.TagQuery q -> {
                // no computeIfAbsent: tagSum may throw, and we don't want a recursive-update issue
                BigDecimal cached = cache.get(q);
                if (cached != null) yield cached;
                BigDecimal value = tagSum.apply(q);
                cache.put(q, value);
                yield value;
            }
            case Expr.Neg n -> eval(n.inner()).negate();
            case Expr.BinOp b -> apply(b.op(), eval(b.left()), eval(b.right()));
        };
    }

    private BigDecimal apply(char op, BigDecimal l, BigDecimal r) {
        return switch (op) {
            case '+' -> l.add(r);
            case '-' -> l.subtract(r);
            case '*' -> l.multiply(r);
            case '/' -> {
                if (r.signum() == 0) throw new TemplateException("Division par zéro");
                yield l.divide(r, MathContext.DECIMAL64);
            }
            default -> throw new IllegalStateException("Opérateur inconnu : " + op);
        };
    }
}
```

---

## 7. Service de rendu

### 7.1 `service/PeriodDescriptionRenderer.java`

```java
package com.brieuc.cashtag.service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.brieuc.cashtag.dto.TagDto;
import com.brieuc.cashtag.dto.calculation.ComputationRequestDto;
import com.brieuc.cashtag.entity.Entry;
import com.brieuc.cashtag.entity.Period;
import com.brieuc.cashtag.mapper.TagMapper;
import com.brieuc.cashtag.repository.TagRepository;
import com.brieuc.cashtag.service.helper.ComputationSpecBuilder;
import com.brieuc.cashtag.service.template.Evaluator;
import com.brieuc.cashtag.service.template.Expr;
import com.brieuc.cashtag.service.template.Lexer;
import com.brieuc.cashtag.service.template.Parser;
import com.brieuc.cashtag.service.template.Statement;
import com.brieuc.cashtag.service.template.TemplateException;

import lombok.RequiredArgsConstructor;

/**
 * Renders a period description by replacing the {{ ... }} blocks with computed amounts.
 * Amounts are computed on the period dates with the same rules as the computation endpoint.
 */
@Service
@RequiredArgsConstructor
public class PeriodDescriptionRenderer {

    // group(1): block content, group(2): optional line break right after the block
    private static final Pattern BLOCK = Pattern.compile("\\{\\{(.*?)\\}\\}(\\R)?", Pattern.DOTALL);
    private static final Locale SWISS = Locale.forLanguageTag("de-CH");

    private final ComputationSpecBuilder computationSpecBuilder;
    private final ComputationService computationService;
    private final EntryService entryService;
    private final CurrencyService currencyService;
    private final TagRepository tagRepository;
    private final TagMapper tagMapper;

    public String render(Period period) {
        String text = period.getDescription();
        if (text == null || text.isBlank()) {
            return text;
        }

        String target = period.getCurrency() != null
                ? period.getCurrency().getCode()
                : currencyService.getReferenceCurrency().getCode();
        Evaluator evaluator = new Evaluator(q -> sum(q, period, target));

        Matcher m = BLOCK.matcher(text);
        StringBuilder sb = new StringBuilder();
        while (m.find()) {
            String lineBreak = m.group(2) != null ? m.group(2) : "";
            String replacement;
            try {
                Statement st = new Parser(new Lexer(m.group(1)).tokenize()).parseStatement();
                // assignment: empty string and the following line break is dropped
                replacement = evaluator.execute(st)
                        .map(v -> String.format(SWISS, "%,.2f %s", v, target) + lineBreak)
                        .orElse("");
            } catch (TemplateException ex) {
                replacement = "[" + ex.getMessage() + "]" + lineBreak;
            }
            m.appendReplacement(sb, Matcher.quoteReplacement(replacement));
        }
        m.appendTail(sb);
        return sb.toString();
    }

    private BigDecimal sum(Expr.TagQuery q, Period period, String target) {
        ComputationRequestDto request = new ComputationRequestDto(
                period.getStartDate(),
                period.getEndDate(),
                toTagDtos(q.included()),
                toTagDtos(q.excluded()),
                null,   // currencies: no filter
                null,   // searchText: no filter
                target);
        List<Entry> entries = entryService
                .getEntries(computationSpecBuilder.from(request), Pageable.unpaged())
                .getContent();
        return computationService.computeSum(entries, target).totalAmount();
    }

    private Set<TagDto> toTagDtos(List<String> titles) {
        return titles.stream()
                .map(t -> tagRepository.findByTitle(t)
                        .orElseThrow(() -> new TemplateException("Tag inconnu : " + t)))
                .map(tagMapper::toDto)
                .collect(Collectors.toSet());
    }
}
```

Points d'attention :

- `ComputationSpecBuilder.from` gère déjà `tags` et `excludedTags` vides ou null (il ne les pose dans la spec que s'ils sont non vides).
- Si `computeSum` lève `EntityNotFoundException` (conversion de devise impossible, voir `getLocalizedAmount`), elle remonte et fait échouer tout le rendu. À décider : la convertir en `TemplateException` dans `sum(...)` pour l'afficher dans le bloc (recommandé) :

```java
        try {
            return computationService.computeSum(entries, target).totalAmount();
        } catch (EntityNotFoundException ex) {
            throw new TemplateException("Conversion impossible : " + ex.getMessage());
        }
```

- `tags:` sans aucun tag inclus mais avec des exclusions (`tags: !italie`) calcule toutes les entries de la période sauf celles exclues. C'est voulu.

---

## 8. Endpoint

### 8.1 `dto/RenderedDescriptionDto.java`

```java
package com.brieuc.cashtag.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Period description with the {{ }} expressions replaced by computed amounts")
public record RenderedDescriptionDto(
      @Schema(description = "Rendered text", example = "J'ai dépensé 1'234.50 CHF ce mois-ci")
      String rendered) {
}
```

### 8.2 `PeriodApi`

```java
    @Operation(summary = "Rendre la description d'une période",
               description = "Retourne la description avec les expressions {{ }} remplacées par les montants calculés sur la période")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Description rendue",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = RenderedDescriptionDto.class))),
            @ApiResponse(responseCode = "404", description = "Période non trouvée", content = @Content)
    })
    @GetMapping("/{id}/description/rendered")
    ResponseEntity<RenderedDescriptionDto> getRenderedDescription(
            @Parameter(description = "ID de la période", required = true, example = "1") @PathVariable Long id);
```

### 8.3 `PeriodController`

Injecter `PeriodDescriptionRenderer` puis :

```java
    @Override
    public ResponseEntity<RenderedDescriptionDto> getRenderedDescription(@PathVariable Long id) {
        Period period = periodService.getById(id);
        return ResponseEntity.ok(new RenderedDescriptionDto(periodDescriptionRenderer.render(period)));
    }
```

`rendered` est `null` si la période n'a pas de description.

---

## 9. Tests

Style existant : JUnit 5 + Mockito (`@ExtendWith(MockitoExtension.class)`), dans `src/test/java/com/brieuc/cashtag/`.

### 9.1 `ParserTest` (sans mock)

- `1 + 2 * 3` donne `BinOp('+', Num 1, BinOp('*', Num 2, Num 3))`.
- `(1 + 2) * 3` respecte les parenthèses.
- `-x` donne `Neg(Var x)`.
- `A = tags: restaurant` donne `Assign("A", TagQuery([restaurant], []))`.
- `tags: italie, restaurant` donne deux tags inclus.
- `tags: restaurant, !italie` donne `included=[restaurant]`, `excluded=[italie]`.
- `tags: "fast-food"` accepte le nom entre guillemets.
- `TAGS: restaurant` (casse) est reconnu.
- Erreurs (`TemplateException`) : bloc vide, `1 +`, `tags:`, `tags: ,`, `1 2`, `"abc` (guillemet manquant), `#`.

### 9.2 `EvaluatorTest` (sans mock, `tagSum` en lambda)

- `tagSum` renvoyant une valeur fixe par `TagQuery`, vérifier l'exemple 2 complet : affectations puis soustraction.
- Variable inconnue : `TemplateException`.
- Division par zéro : `TemplateException`.
- Cache : un compteur dans la lambda, deux évaluations de `tags: restaurant` = un seul appel.
- Affectation : `execute` renvoie `Optional.empty()`.

### 9.3 `PeriodDescriptionRendererTest` (Mockito)

Mocks : `ComputationSpecBuilder`, `ComputationService`, `EntryService`, `CurrencyService`, `TagRepository`, `TagMapper`.

- Description null ou vide : renvoyée telle quelle, aucun appel aux mocks.
- Exemple 1 : rendu `1'234.50 CHF` (attention, le séparateur de milliers `de-CH` peut être `'` ou `’` selon la version du JDK : comparer avec `String.format(Locale.forLanguageTag("de-CH"), "%,.2f", ...)` plutôt qu'en dur).
- Exemple 2 : les lignes d'affectation disparaissent, le texte final est correct.
- Période avec devise `MXN` : `target = "MXN"`, `getReferenceCurrency` jamais appelé.
- Période sans devise : `target` = devise de référence.
- Tag inconnu : `[Tag inconnu : xyz]` dans le texte, les autres blocs rendus.
- Vérifier avec `ArgumentCaptor<ComputationRequestDto>` que `startDate`, `endDate`, `tags`, `excludedTags` et `targetCurrencyCode` sont bien ceux de la période.

---

## 10. Frontend (`cashvue`)

1. Après le déploiement du backend, régénérer le client Orval depuis le spec OpenAPI (nouveau champ `description` dans `PeriodDto`, nouvel appel `getRenderedDescription`).
2. Formulaire de période : ajouter un `textarea` pour `description` (saisie brute avec les `{{ }}`).
3. Vue détail de période : appeler `getRenderedDescription(id)` et afficher `rendered` (respecter les retours à la ligne : `white-space: pre-line`).
4. Ne pas appeler le rendu dans la liste des périodes.
5. Optionnel : une aide de syntaxe sous le `textarea` (exemples 1 et 2 de ce document).

---

## 11. Points ouverts

- Signe des montants : si les dépenses sont stockées en négatif, `{{ tags: restaurant }}` affiche un montant négatif. Options : écrire `{{ -tags: restaurant }}` (déjà supporté), inverser le signe dans `sum(...)`, ou ajouter une fonction `abs(...)` à la grammaire.
- Casse des noms de tags : `findByTitle` dépend de la collation MariaDB de la colonne `tag.title` (`utf8mb4_unicode_ci` = insensible à la casse). À vérifier.
- Performance : une requête SQL par `TagQuery` distincte. Acceptable pour quelques blocs ; si besoin, charger une seule fois les entries de la période et filtrer en mémoire.
- Évolutions possibles de la grammaire : fonctions (`abs`, `round`, `count(tags: x)` pour le nombre d'entries via `ComputeResult.numberOfEntries`), pourcentage (`A / B * 100`), filtre texte (`search: "migros"` vers `searchText`), filtre devise (`currency: EUR` vers `currencies`).
- Validation à l'enregistrement : optionnellement parser la description dans `create`/`update` pour signaler les erreurs de syntaxe à la saisie (sans évaluer les montants).

---

## 12. Ordre d'implémentation conseillé

1. Package `service/template` (Token, TokenType, TemplateException, Expr, Statement, Lexer, Parser, Evaluator) + `ParserTest` + `EvaluatorTest`.
2. Liquibase + `Period` + `PeriodDto` + `PeriodMapperImpl` + `PeriodServiceImpl.update`.
3. `PeriodDescriptionRenderer` + `PeriodDescriptionRendererTest`.
4. `RenderedDescriptionDto` + endpoint dans `PeriodApi` et `PeriodController`.
5. `mvn test`, puis test manuel avec une vraie période.
6. Frontend `cashvue` : régénération Orval, formulaire, affichage.

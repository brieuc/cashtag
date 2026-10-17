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
                BigDecimal cached = cache.get(q);
                if (cached != null) yield cached;
                BigDecimal value = tagSum.apply(q);
                cache.put(q, value);
                yield value;
            }
            case Expr.Neg n -> eval(n.inner()).negate();
            case Expr.Abs a -> eval(a.inner()).abs();
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

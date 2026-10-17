package com.brieuc.cashtag.service.template;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;

import org.junit.jupiter.api.Test;

public class EvaluatorTest {

    private static final Expr.TagQuery RESTAURANT = new Expr.TagQuery(List.of("restaurant"), List.of());
    private static final Expr.TagQuery RESTAURANT_ITALIE = new Expr.TagQuery(List.of("italie", "restaurant"), List.of());

    private Statement parse(String src) {
        return new Parser(new Lexer(src).tokenize()).parseStatement();
    }

    private Evaluator evaluator() {
        return new Evaluator(q -> {
            if (q.equals(RESTAURANT)) return new BigDecimal("1000");
            if (q.equals(RESTAURANT_ITALIE)) return new BigDecimal("188");
            return BigDecimal.ZERO;
        });
    }

    @Test
    void variablesAndSubtraction() {
        Evaluator evaluator = evaluator();
        assertEquals(Optional.empty(), evaluator.execute(parse("A = tags: restaurant")));
        assertEquals(Optional.empty(), evaluator.execute(parse("B = tags: italie, restaurant")));
        assertEquals(0, new BigDecimal("812").compareTo(evaluator.execute(parse("A - B")).orElseThrow()));
    }

    @Test
    void absRemovesTheSign() {
        Evaluator evaluator = new Evaluator(q -> new BigDecimal("-50.25"));
        assertEquals(0, new BigDecimal("50.25").compareTo(evaluator.execute(parse("abs(tags: x)")).orElseThrow()));
        assertEquals(0, new BigDecimal("3").compareTo(evaluator.execute(parse("abs(1 - 4)")).orElseThrow()));
    }

    @Test
    void unknownVariable() {
        assertThrows(TemplateException.class, () -> evaluator().execute(parse("A + 1")));
    }

    @Test
    void divisionByZero() {
        assertThrows(TemplateException.class, () -> evaluator().execute(parse("1 / 0")));
    }

    @Test
    void sameTagQueryIsComputedOnce() {
        AtomicInteger calls = new AtomicInteger();
        Evaluator evaluator = new Evaluator(q -> {
            calls.incrementAndGet();
            return BigDecimal.TEN;
        });
        evaluator.execute(parse("tags: restaurant + tags: restaurant"));
        evaluator.execute(parse("tags: restaurant"));
        assertEquals(1, calls.get());
    }

    @Test
    void assignmentReturnsNothing() {
        assertTrue(evaluator().execute(parse("A = 1")).isEmpty());
    }
}

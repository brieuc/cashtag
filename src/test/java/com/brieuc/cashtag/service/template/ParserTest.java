package com.brieuc.cashtag.service.template;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.math.BigDecimal;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

public class ParserTest {

    private Statement parse(String src) {
        return new Parser(new Lexer(src).tokenize()).parseStatement();
    }

    private Expr.Num num(String v) {
        return new Expr.Num(new BigDecimal(v));
    }

    @Test
    void multiplicationHasPrecedenceOverAddition() {
        assertEquals(new Statement.Print(new Expr.BinOp('+', num("1"),
                new Expr.BinOp('*', num("2"), num("3")))), parse("1 + 2 * 3"));
    }

    @Test
    void parenthesesAreRespected() {
        assertEquals(new Statement.Print(new Expr.BinOp('*',
                new Expr.BinOp('+', num("1"), num("2")), num("3"))), parse("(1 + 2) * 3"));
    }

    @Test
    void unaryMinus() {
        assertEquals(new Statement.Print(new Expr.Neg(new Expr.Var("x"))), parse("-x"));
    }

    @Test
    void absFunction() {
        assertEquals(new Statement.Print(new Expr.Abs(new Expr.TagQuery(List.of("restaurant"), List.of()))),
                parse("abs(tags: restaurant)"));
    }

    @Test
    void absIsCaseInsensitive() {
        assertEquals(new Statement.Print(new Expr.Abs(num("1"))), parse("ABS(1)"));
    }

    @Test
    void assignment() {
        assertEquals(new Statement.Assign("A", new Expr.TagQuery(List.of("restaurant"), List.of())),
                parse("A = tags: restaurant"));
    }

    @Test
    void severalIncludedTags() {
        assertEquals(new Statement.Print(new Expr.TagQuery(List.of("italie", "restaurant"), List.of())),
                parse("tags: italie, restaurant"));
    }

    @Test
    void includedAndExcludedTags() {
        assertEquals(new Statement.Print(new Expr.TagQuery(List.of("restaurant"), List.of("italie"))),
                parse("tags: restaurant, !italie"));
    }

    @Test
    void quotedTagCode() {
        assertEquals(new Statement.Print(new Expr.TagQuery(List.of("fast-food"), List.of())),
                parse("tags: \"fast-food\""));
    }

    @Test
    void tagsKeywordIsCaseInsensitive() {
        assertEquals(new Statement.Print(new Expr.TagQuery(List.of("restaurant"), List.of())),
                parse("TAGS: restaurant"));
    }

    @ParameterizedTest
    @ValueSource(strings = {"", "1 +", "tags:", "tags: ,", "1 2", "\"abc", "#", "abs", "abs(", "abs(1", "abs 1", "1.2.3"})
    void syntaxErrors(String src) {
        assertThrows(TemplateException.class, () -> parse(src));
    }
}

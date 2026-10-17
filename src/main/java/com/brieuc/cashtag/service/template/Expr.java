package com.brieuc.cashtag.service.template;

import java.math.BigDecimal;
import java.util.List;

public sealed interface Expr {
    record Num(BigDecimal value) implements Expr {}
    record Var(String name) implements Expr {}
    record TagQuery(List<String> included, List<String> excluded) implements Expr {}
    record Neg(Expr inner) implements Expr {}
    record Abs(Expr inner) implements Expr {}
    record BinOp(char op, Expr left, Expr right) implements Expr {}
}

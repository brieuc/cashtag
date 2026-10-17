package com.brieuc.cashtag.service.template;

public sealed interface Statement {
    record Assign(String name, Expr expr) implements Statement {}
    record Print(Expr expr) implements Statement {}
}

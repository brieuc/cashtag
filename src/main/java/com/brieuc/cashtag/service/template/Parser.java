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
            case NUMBER -> new Expr.Num(parseNumber(tok));
            case IDENT -> new Expr.Var(tok.text());
            case LPAREN -> {
                Expr e = expr();
                expect(TokenType.RPAREN);
                yield e;
            }
            case ABS -> {
                expect(TokenType.LPAREN);
                Expr e = expr();
                expect(TokenType.RPAREN);
                yield new Expr.Abs(e);
            }
            case TAGS -> tagQuery();
            default -> {
                p--; // do not consume EOF, keeps the token list consistent
                throw new TemplateException("Expression attendue en position " + tok.pos());
            }
        };
    }

    private BigDecimal parseNumber(Token tok) {
        try {
            return new BigDecimal(tok.text());
        } catch (NumberFormatException ex) {
            throw new TemplateException("Nombre invalide '" + tok.text() + "' en position " + tok.pos());
        }
    }

    private Expr tagQuery() {
        expect(TokenType.COLON);
        List<String> included = new ArrayList<>();
        List<String> excluded = new ArrayList<>();
        do {
            boolean exclude = accept(TokenType.BANG);
            Token tok = peek();
            if (tok.type() != TokenType.IDENT && tok.type() != TokenType.STRING) {
                throw new TemplateException("Code de tag attendu en position " + tok.pos());
            }
            p++;
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

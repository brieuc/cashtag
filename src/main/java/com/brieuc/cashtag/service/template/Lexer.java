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
                        out.add(new Token(wordType(word), word, start));
                    } else {
                        throw new TemplateException("Caractère inattendu '" + c + "' en position " + i);
                    }
                }
            }
        }
        out.add(new Token(TokenType.EOF, "", i));
        return out;
    }

    private TokenType wordType(String word) {
        if (word.equalsIgnoreCase("tags")) return TokenType.TAGS;
        if (word.equalsIgnoreCase("abs")) return TokenType.ABS;
        return TokenType.IDENT;
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

package com.craftinginterpreters.lox;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.craftinginterpreters.lox.TokenType.*;

class Scanner {
    private static final Map<String, TokenType> keywords;

    static {
        keywords = new HashMap<>();
        keywords.put("and",    AND);
        keywords.put("class",  CLASS);
        keywords.put("else",   ELSE);
        keywords.put("false",  FALSE);
        keywords.put("for",    FOR);
        keywords.put("fun",    FUN);
        keywords.put("if",     IF);
        keywords.put("nil",    NIL);
        keywords.put("or",     OR);
        keywords.put("print",  PRINT);
        keywords.put("return", RETURN);
        keywords.put("super",  SUPER);
        keywords.put("this",   THIS);
        keywords.put("true",   TRUE);
        keywords.put("var",    VAR);
        keywords.put("while",  WHILE);
    }
    private final String source;
    private final List<Token> tokens = new ArrayList<>();
    // int start and int current are offsets that index into the string
    private int start = 0;
    private int current = 0;
    // tracks which source line int current is on so we can produce tokens that know their location
    private int line = 1;

    Scanner(String source) {
        this.source = source;
    }

    List<Token> scanTokens() {
        while (!isAtEnd()) {
            // We are at the beginning of the next lexeme
            start = current;
            scanToken();
        }

        tokens.add(new Token(EOF, "", null, line));
        return tokens;
    }

    private void scanToken() {
        char c = advance();
        switch (c) {
            case '(': addToken(LEFT_PAREN); break;
            case ')': addToken(RIGHT_PAREN); break;
            case '{': addToken(LEFT_BRACE); break;
            case '}': addToken(RIGHT_BRACE); break;
            case ',': addToken(COMMA); break;
            case '.': addToken(DOT); break;
            case '-': addToken(MINUS); break;
            case '+': addToken(PLUS); break;
            case ';': addToken(SEMICOLON); break;
            case '*': addToken(STAR); break;
            // two stage lexeme recognition follows
            case '!':
                addToken(match('=') ? BANG_EQUAL : BANG);
                break;
            case '=':
                addToken(match('=') ? EQUAL_EQUAL : EQUAL);
                break;
            case '<':
                addToken(match('=') ? LESS_EQUAL : LESS);
                break;
            case '>':
                addToken(match('=') ? GREATER_EQUAL : GREATER);
                break;
            case '/':
                if (match('/')) {
                    // a comment goes until the end of the line
                    while (peek() != '\n' && !isAtEnd()) advance();
                } else {
                    addToken(SLASH);
                }
                break;

            // dealing with newlines and whitespace
            case ' ':
            case '\r':
            case '\t':
                // ignore whitespace
                break;

            case '\n':
                line++;
                break;

            case '"': string(); break;

            // for characters not used by Lox language
            default:
                if (isDigit(c)) {
                    number();
                } else if (isAlpha(c)) {
                    identifier();
                } else {
                    Lox.error(line, "Unexpected character.");
                }
                break;
        }
    }

    // if identifier matches anything in the map, we use that keyword's token type, otherwise it's a user-defined identifier
    private void identifier() {
        while (isAlphaNumeric(peek())) advance();

        String text = source.substring(start, current);
        TokenType type = keywords.get(text);
        if (type == null) type = IDENTIFIER;
        addToken(type);
    }

    private void string() {
        while (peek() != '"' && !isAtEnd()) {
            if (peek() == '\n') line++;
            advance();
        }

        if (isAtEnd()) {
            Lox.error(line, "Unterminated string.");
            return;
        }

        // the closing "
        advance();

        // trim surrounding quotes
        String value = source.substring(start +1, current -1);
        addToken(STRING, value);
    }

    private void number() {
        while (isDigit(peek())) advance();

        // look for a fractional part (the decimal point) followed by at least one digit
        if (peek() == '.' && isDigit(peekNext())) {
            // consume the "."
            advance();

            while (isDigit(peek())) advance();
        }

        // this interpreter uses java's Double type to represent numbers
        addToken(NUMBER, Double.parseDouble(source.substring(start,current)));
    }

    // conditional version of advance() for consuming the current character only if it's what we're looking for
    private boolean match(char expected) {
        if (isAtEnd()) return false;
        if (source.charAt(current) != expected) return false;

        current++;
        return true;
    }

    // lookahead version of advance for not consuming character
    private char peek() {
        if (isAtEnd()) return '\0';
        return source.charAt(current);
    }

    // lookahead ensuring we can search at most two characters ahead
    private char peekNext() {
        if (current + 1 >= source.length()) return '\0';
        return source.charAt(current + 1);
    }

    private boolean isAlpha(char c) {
        return (c >= 'a' && c <= 'z') ||
                (c >= 'A' && c <= 'Z') ||
                c == '_';
    }

    private boolean isAlphaNumeric(char c) {
        return isAlpha(c) || isDigit(c);
    }

    private boolean isDigit(char c) {
        return c >= '0' && c <= '9';
    }

    // helper function to tell us if we've consumed all characters in a token
    private boolean isAtEnd() {
        return current >= source.length();
    }

    // consums the next character in the source file and returns it
    private char advance() {
        return source.charAt(current++);
    }

    // grabs the text of the current lexeme and creates a new token for it
    private void addToken(TokenType type) {
        addToken(type, null);
    }

    // for tokens with literal values
    private void addToken(TokenType type, Object literal) {
        String text = source.substring(start,current);
        tokens.add(new Token(type, text, literal, line));
    }
}








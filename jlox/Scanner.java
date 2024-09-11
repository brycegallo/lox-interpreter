package com.craftinginterpreters.lox;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.craftinginterpreters.lox.TokenType.*;

class Scanner {
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
}

List<Token> scanTokens() {
    while (!isAtEnd()) {
        // We are at the beginning of the next lexeme
        start = current;
        scanToken();
    }

    tokens.add(newToken(EOF, "", null, line));
    return tokens;
}

// helper function to tell us if we've consumed all characters in a token
private boolean isAtEnd() {
    return current >= source.length();
}


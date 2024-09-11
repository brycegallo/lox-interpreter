package com.craftinginterpreters.lox;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

public class Lox {
    static boolean hadError = false;

    public static void main(String[] args) throws IOException {
        if (args.length > 1) {
            System.out.println("Usage: jlox [script]");
            System.exit(64);
        } else if (args.length == 1) {
            runFile(args[0]);
        } else {
            runPrompt();
        }
    }

    // runFile function, for when lox is run from the command line and given the path to a file to execute
    private static void runFile(String path) throws IOException {
        byte[] bytes = Files.readAllBytes(Paths.get(path));
        run(new String(bytes, Charset.defaultCharset()));

        // Indicate an error in the exit code
        if (hadError) System.exit(65);
    }

    // runPrompt function, for when lox is run interactively
    private static void runPrompt() throws IOException {
        InputStreamReader input = new InputStreamReader(System.in);
        BufferedReader reader = new BufferedReader(input);

        // reads line of input from user and returns result
        for (;;) {
            System.out.print("> ");
            String line = reader.readLine();
            if (line == null) break;
            run(line);
            // reset error flag in interactive loop to prevent killing entire session
            hadError = false;
        }
    }

    private static void run(String source) {
        Scanner scanner = new Scanner(source);
        List<Token> tokens = scanner.scanTokens();

        // sort of place holder for just printing tokens for now
        for (Token token : tokens) {
            System.out.println(token);
        }
    }

    // provide some basic error handling
    static void error(int line, String message) {
        report(line, "", message);
    }

    // report where error was found
    private static void report(int line, String where, String message) {
        System.err.println("[line " + line + "] Error" + where ": " + message);
        hadError = true;
    }
}
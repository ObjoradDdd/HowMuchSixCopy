package App.howmuchsix.hms.Handlers;

import java.util.ArrayList;
import java.util.List;

public final class Lexer {
    private static final String OPERATOR_CHARS = "+-*/%()^=<>!&|";
    private static final String SINGLE_OPERATOR = "+-*/^%";
    private static final TokenType[] OPERATOR_TOKENS = {
            TokenType.PLUS,
            TokenType.MINUS,
            TokenType.STAR,
            TokenType.SLASH,
            TokenType.REMAINDER,
            TokenType.OPEN_PAREN,
            TokenType.CLOSE_PAREN,
            TokenType.POWER,
            TokenType.EQ,
            TokenType.LESS,
            TokenType.MORE
    };

    private final String input;
    private final int length;
    private final List<Token> tokens;

    private int pos;


    public Lexer(String input) {
        this.input = input;
        length = input.length();
        tokens = new ArrayList<>();
    }

    public List<Token> tokenize() {
        while (pos < length) {
            final char current = peek(0);
            if (Character.isDigit(current)) tokenizeNumber();
            else if (current == '\"' || current == '\'') tokenizeString();
            else if (Character.isLetter(current)) tokenizeWord();
            else if (OPERATOR_CHARS.indexOf(current) != -1) {
                tokenizeOperator();
            } else {
                next();
            }
        }

        for (int i = 0; i < tokens.size() - 1; i++) {
            if (
                    (SINGLE_OPERATOR.contains(tokens.get(i).getText()) && SINGLE_OPERATOR.contains(tokens.get(i + 1).getText())) ||
                            (!OPERATOR_CHARS.contains(tokens.get(i + 1).getText()) && !OPERATOR_CHARS.contains(tokens.get(i).getText()))
            ) {
                throw new RuntimeException("Wrong input");
            }
        }


        return tokens;
    }

    private void tokenizeWord() {
        final StringBuilder buffer = new StringBuilder();
        char current = peek(0);
        while (Character.isLetterOrDigit(current) || (current == '_') || (current == '$')) {
            buffer.append(current);
            current = next();
        }

        String word = buffer.toString();

        if (buffer.toString().equals("true")) {
            addToken(TokenType.TRUE, "true");
            return;
        }

        if (buffer.toString().equals("false")) {
            addToken(TokenType.FALSE, "false");
            return;
        }

        if (buffer.toString().equals("null")) {
            addToken(TokenType.NULL, "null");
            return;
        }

        if (peek(0) == '(') {
            List<String> args = parseFunctionArguments();
            tokens.add(new Token(TokenType.FUNCTION, word, args));
        } else {
            addToken(TokenType.WORD, word);
        }

    }

    private void tokenizeString() {
        final StringBuilder buffer = new StringBuilder();

        char quote = peek(0);
        char current = next();


        while (pos < length) {
            if (current == quote) {
                next();
                addToken(TokenType.STRING, buffer.toString());
                return;
            }
            buffer.append(current);
            current = next();
        }
        addToken(TokenType.STRING, buffer.toString());
    }

    private void tokenizeOperator() {
        char current = peek(0);
        char next = peek(1);

        if (current == '&' && next == '&') {
            addToken(TokenType.AND, "&&");
            next();
            next();
            return;
        }

        if (current == '|' && next == '|') {
            addToken(TokenType.OR, "||");
            next();
            next();
            return;
        }

        if (current == '=' && next == '=') {
            addToken(TokenType.EQ, "==");
            next();
            next();
            return;
        }

        if (current == '!' && next == '=') {
            addToken(TokenType.NOT_EQ, "!=");
            next();
            next();
            return;
        }

        if (current == '<' && next == '=') {
            addToken(TokenType.LESS_EQ, "<=");
            next();
            next();
            return;
        }

        if (current == '>' && next == '=') {
            addToken(TokenType.MORE_EQ, ">=");
            next();
            next();
            return;
        }

        if (current == '!') {
            addToken(TokenType.NOT, "!");
            next();
            return;
        }

        final int position = OPERATOR_CHARS.indexOf(current);
        addToken(OPERATOR_TOKENS[position], String.valueOf(current));
        next();
    }

    private void tokenizeNumber() {
        final StringBuilder buffer = new StringBuilder();
        char current = peek(0);
        boolean flag = false;
        while (true) {
            if (current == '.') {
                if (buffer.indexOf(".") != -1) {
                    throw new RuntimeException("Invalid float number");
                }
                flag = true;
            } else if (!Character.isDigit(current)) {
                break;
            }
            buffer.append(current);
            current = next();
        }
        if (!flag) {
            addToken(TokenType.INT, buffer.toString());
        } else {
            addToken(TokenType.DOUBLE, buffer.toString());
        }
    }

    private char next() {
        pos++;
        return peek(0);
    }

    private char peek(int relativePosition) {
        final int position = pos + relativePosition;
        if (position >= length) return '\0';
        return input.charAt(position);
    }

    public void addToken(TokenType type, String text) {
        tokens.add(new Token(type, text));
    }

    public List<Token> tokenizeComplex() {
        while (pos < length) {
            char current = peek(0);

            if (current == '(') {
                addToken(TokenType.OPEN_PAREN, "(");
                next();
            } else if (current == ')') {
                addToken(TokenType.CLOSE_PAREN, ")");
                next();
            } else if (lookAhead("number(")) {
                tokenizeNumberExpression();
            } else if (lookAhead("str(")) {
                tokenizeStrExpression();
            } else if (Character.isLetter(current)) tokenizeWord();

            else if (OPERATOR_CHARS.indexOf(current) != -1) {
                tokenizeOperator();
            } else {
                next();
            }
        }

        if (input.contains(")(")) {
            throw new RuntimeException("Wrong input");
        }

        return tokens;
    }

    private boolean lookAhead(String prefix) {
        if (pos + prefix.length() > length) return false;
        return input.startsWith(prefix, pos);
    }

    private void tokenizeNumberExpression() {
        pos += "number(".length();
        StringBuilder buffer = new StringBuilder();


        int bracketsCount = 1;
        while (pos < length && bracketsCount > 0) {
            char current = peek(0);
            if (current == '(') bracketsCount++;
            if (current == ')') bracketsCount--;

            if (bracketsCount > 0) {
                buffer.append(current);
            }
            next();
        }

        addToken(TokenType.MATH, buffer.toString().trim());
    }

    private void tokenizeStrExpression() {
        pos += "str(".length();
        StringBuilder buffer = new StringBuilder();

        int bracketsCount = 1;
        while (pos < length && bracketsCount > 0) {
            char current = peek(0);
            if (current == '(') bracketsCount++;
            if (current == ')') bracketsCount--;

            if (bracketsCount > 0) {
                buffer.append(current);
            }
            next();
        }
        addToken(TokenType.STR, buffer.toString().trim());
    }

    private List<String> parseFunctionArguments() {
        next();

        List<String> arguments = new ArrayList<>();
        StringBuilder buffer = new StringBuilder();
        int bracketCount = 1;
        boolean inString = false;
        char stringQuote = '\0';

        while (pos < length && bracketCount > 0) {
            char current = peek(0);

            if ((current == '"' || current == '\'') && peek(-1) != '\\') {
                if (!inString) {
                    inString = true;
                    stringQuote = current;
                } else if (current == stringQuote) {
                    inString = false;
                }
            }

            if (!inString) {
                if (current == '(') {
                    bracketCount++;
                } else if (current == ')') {
                    bracketCount--;
                    if (bracketCount == 0) {
                        if (buffer.length() > 0) {
                            arguments.add(buffer.toString().trim());
                        }
                        next();
                        return arguments;
                    }
                } else if (current == ',' && bracketCount == 1) {
                    if (buffer.length() > 0) {
                        arguments.add(buffer.toString().trim());
                    }
                    buffer = new StringBuilder();
                    next();
                    continue;
                }
            }

            buffer.append(current);
            next();
        }

        return arguments;
    }
}

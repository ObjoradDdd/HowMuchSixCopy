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
            Token current = tokens.get(i);
            Token next = tokens.get(i + 1);

            if (isInterpolationToken(current) || isInterpolationToken(next)) {
                continue;
            }

            if (
                    (SINGLE_OPERATOR.contains(current.getText()) && SINGLE_OPERATOR.contains(next.getText())) ||
                            (!OPERATOR_CHARS.contains(current.getText()) && !OPERATOR_CHARS.contains(next.getText()) &&
                                    isSpecialToken(current) && isSpecialToken(next))
            ) {
                throw new RuntimeException("Wrong input");
            }
        }

        return tokens;
    }

    private void tokenizeWord() {
        final StringBuilder buffer = new StringBuilder();
        char current = peek(0);
        while (Character.isLetterOrDigit(current) || (current == '_') || (current == '#') || (current == '$')) {
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
        } else if (peek(0) == '[') {
            String arrayIndex = parseArrayIndex();
            tokens.add(new Token(TokenType.ARRAY, word, arrayIndex));
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

    private String parseArrayIndex() {
        next();
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
                if (current == '[') {
                    bracketCount++;
                } else if (current == ']') {
                    bracketCount--;
                    if (bracketCount == 0) {
                        String result = buffer.toString().trim();
                        next();
                        return result;
                    }
                }
            }

            buffer.append(current);
            next();
        }
        throw new RuntimeException("Unclosed array brackets");
    }

    public Token tokenizeForAssignment() {
        final StringBuilder buffer = new StringBuilder();
        char current = peek(0);
        while (Character.isLetterOrDigit(current) || (current == '_') || (current == '#') || (current == '$')) {
            buffer.append(current);
            current = next();
        }

        String word = buffer.toString();

        if (peek(0) == '[') {
            String arrayIndex = parseArrayIndex();
            return new Token(TokenType.ARRAY, word, arrayIndex);
        } else {
            return new Token(TokenType.WORD, word);
        }
    }

    public Token tokenizeFunction() {
        final StringBuilder buffer = new StringBuilder();
        char current = peek(0);
        while (Character.isLetterOrDigit(current) || (current == '_') || (current == '$')) {
            buffer.append(current);
            current = next();
        }

        String word = buffer.toString();

        if (peek(0) == '(') {
            List<String> args = parseFunctionArguments();
            return new Token(TokenType.FUNCTION, word, args);
        }
        throw new RuntimeException("Invalid function expression");
    }


    private boolean isInterpolationToken(Token token) {
        return token.getType() == TokenType.TAG ||
                token.getType() == TokenType.INTERPOLATION_START ||
                token.getType() == TokenType.INTERPOLATION_END;
    }

    private boolean isSpecialToken(Token token) {
        return token.getType() != TokenType.WORD &&
                token.getType() != TokenType.STRING &&
                token.getType() != TokenType.INT &&
                token.getType() != TokenType.DOUBLE &&
                token.getType() != TokenType.TRUE &&
                token.getType() != TokenType.FALSE &&
                token.getType() != TokenType.NULL &&
                token.getType() != TokenType.FUNCTION &&
                token.getType() != TokenType.ARRAY &&
                token.getType() != TokenType.TAG &&
                token.getType() != TokenType.INTERPOLATION_START &&
                token.getType() != TokenType.INTERPOLATION_END;
    }


    public List<Token> tokenizeInterpolation() {
        tokens.clear();
        pos = 0;

        while (pos < length) {
            final char current = peek(0);


            if (current == '#' && pos + 1 < length && peek(1) == '{') {
                addToken(TokenType.INTERPOLATION_START, "#{");
                pos += 2;
                tokenizeExpressionInBraces();
            } else if (current == '#' && pos + 1 < length &&
                    (Character.isLetter(peek(1)) || peek(1) == '_')) {
                addToken(TokenType.TAG, "#");
                next();
                tokenizeIdentifierAfterTag();
            } else {
                tokenizeTextUntilTag();
            }
        }

        return tokens;
    }


    private void tokenizeExpressionInBraces() {
        int braceCount = 1;
        StringBuilder buffer = new StringBuilder();

        while (pos < length && braceCount > 0) {
            char current = peek(0);

            if (current == '{') {
                braceCount++;
            } else if (current == '}') {
                braceCount--;
                if (braceCount == 0) {

                    if (buffer.length() > 0) {
                        Lexer innerLexer = new Lexer(buffer.toString());
                        List<Token> innerTokens = innerLexer.tokenize();
                        tokens.addAll(innerTokens);
                    }
                    addToken(TokenType.INTERPOLATION_END, "}");
                    next();
                    return;
                }
            }

            buffer.append(current);
            next();
        }

        throw new RuntimeException("Unclosed interpolation expression");
    }


    private void tokenizeIdentifierAfterTag() {
        final StringBuilder buffer = new StringBuilder();
        char current = peek(0);


        while (Character.isLetterOrDigit(current) || current == '_') {
            buffer.append(current);
            current = next();
        }

        String name = buffer.toString();
        if (name.isEmpty()) {
            throw new RuntimeException("Expected identifier after #");
        }


        current = peek(0);

        if (current == '(') {

            List<String> args = parseFunctionArguments();
            tokens.add(new Token(TokenType.FUNCTION, name, args));
        } else if (current == '[') {

            String arrayIndex = parseArrayIndex();
            tokens.add(new Token(TokenType.ARRAY, name, arrayIndex));
        } else {

            addToken(TokenType.WORD, name);
        }
    }


    private void tokenizeTextUntilTag() {
        final StringBuilder buffer = new StringBuilder();

        while (pos < length) {
            char current = peek(0);


            if (current == '#') {
                break;
            }

            buffer.append(current);
            next();
        }

        if (buffer.length() > 0) {
            addToken(TokenType.TEXT, buffer.toString());
        }
    }
}

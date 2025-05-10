package App.howmuchsix.hms.Handlers;

import java.util.ArrayList;
import java.util.List;

public final class Lexer {
    private static final String OPERATOR_CHARS = "+-*/%()^=<>";
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
        for (Token token : tokens){
            System.out.println(token.getType());
            System.out.println(token.getText());
        }
        for (int i = 0; i < tokens.size() - 1; i++) {
            if(
               (OPERATOR_CHARS.contains(tokens.get(i).getText()) && OPERATOR_CHARS.contains(tokens.get(i + 1).getText())) ||
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
        addToken(TokenType.WORD, buffer.toString());
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
        final int position = OPERATOR_CHARS.indexOf(peek(0));
        addToken(OPERATOR_TOKENS[position], input.charAt(pos) + "");
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


}

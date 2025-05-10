package App.howmuchsix.hms.Handlers;

import java.util.List;

import App.howmuchsix.hms.Expression.*;
import App.howmuchsix.hms.Library.Variables;


public final class Parser {
    private final List<Token> tokens;
    private final int size;
    private static final Token EOF = new Token(TokenType.EOF, "");
    private int pos;

    public Parser(List<Token> tokens) {
        this.tokens = tokens;
        size = tokens.size();
    }

    private Expression<?> conditional(){
        Expression<?> result = additiveConditional();

        while (true) {
            if (match(TokenType.EQ)) {
                result = new LogicalExpression("+", result, additiveConditional());
                continue;
            }
            if (match(TokenType.MORE)) {
                result = new LogicalExpression(">", result, additiveConditional());
                continue;
            }
            if (match(TokenType.LESS)) {
                result = new LogicalExpression("<", result, additiveConditional());
                continue;
            }

            break;
        }
        return result;
    }

    private Expression<?> additiveConditional(){
        Expression<Number> result = multiply();

        while (true) {
            if (match(TokenType.PLUS)) {
                result = new BinaryExpression<>("+", result, multiply());
                continue;
            }
            if (match(TokenType.MINUS)) {
                result = new BinaryExpression<>("-", result, multiply());
                continue;
            }

            break;
        }
        return result;
    }

    public Expression<String> parseString(){
        return stringExpression();
    }

    private Expression<String> stringExpression() {
        return additiveString();
    }

    private Expression<String> additiveString(){
        Expression<String> result = primaryString();

        while (true) {
            if (match(TokenType.PLUS)) {
                result = new BinaryExpression<>("+", result, primaryString());
                continue;
            }
            if (matchWithoutMove(TokenType.MINUS) || matchWithoutMove(TokenType.STAR) || matchWithoutMove(TokenType.SLASH) || matchWithoutMove(TokenType.REMAINDER)) {
                throw new RuntimeException("Incorrect operator for string type");
            }
            break;
        }
        return result;
    }

    private Expression<String> primaryString() {
        final Token current = peek(0);
        if (match(TokenType.STRING)) {
            return new StringExpression(current.getText());
        }
        if (match(TokenType.WORD)) {
            return new StringExpression(Variables.getExpression(current.getText(), List.of(String.class, Number.class)).eval().toString());
        }
        if (match(TokenType.OPEN_PAREN)) {
            Expression<String> result = stringExpression();
            match(TokenType.CLOSE_PAREN);
            return result;
        }
        throw new RuntimeException("Invalid expression");
    }

    public Expression<Number> parseArithmetic() {
        return numberExpression();
    }
    private Expression<Number> numberExpression() {
        return additive();
    }

    private Expression<Number> additive() {
        Expression<Number> result = multiply();

        while (true) {
            if (match(TokenType.PLUS)) {
                result = new BinaryExpression<>("+", result, multiply());
                continue;
            }
            if (match(TokenType.MINUS)) {
                result = new BinaryExpression<>("-", result, multiply());
                continue;
            }

            break;
        }
        return result;
    }

    private Expression<Number> multiply() {
        Expression<Number> result = power();
        while (true) {
            if (match(TokenType.STAR)) {
                result = new BinaryExpression<>("*", result, power());
                continue;
            }
            if (match(TokenType.SLASH)) {
                result = new BinaryExpression<>("/", result, power());
                continue;
            }
            if (match(TokenType.REMAINDER)) {
                result = new BinaryExpression<>("%", result, power());
                continue;
            }
            break;
        }
        return result;
    }

    private Expression<Number> power() {
        Expression<Number> result = unary();
        while (true) {
            if (match(TokenType.POWER)) {
                result = new BinaryExpression<>("^", result, unary());
                continue;
            }
            break;
        }
        return result;
    }

    private Expression<Number> unary() {
        if (match(TokenType.MINUS)) {
            return new UnaryExpression<>("-", primary());
        }
        return primary();
    }

    private Expression<Number> primary() {
        final Token current = peek(0);
        if (match(TokenType.INT)) {
            return new NumberExpression(
                    new IntExpression(Integer.parseInt(current.getText()))
            );
        }
        if (match(TokenType.DOUBLE)) {
            return new NumberExpression(
                    new DoubleExpression(Double.parseDouble(current.getText()))
            );
        }
        if (match(TokenType.WORD)) {
            return Variables.getExpression(current.getText(), List.of(Number.class));
        }
        if (match(TokenType.OPEN_PAREN)) {
            Expression<Number> result = numberExpression();
            match(TokenType.CLOSE_PAREN);
            return result;
        }
        throw new RuntimeException("Invalid expression");
    }

    private boolean match(TokenType type) {
        final Token current = peek(0);
        if (type != current.getType()) return false;
        pos++;
        return true;
    }

    private boolean matchWithoutMove(TokenType type){
        final Token current = peek(0);
        return type == current.getType();
    }

    private Token peek(int relativePosition) {
        final int position = pos + relativePosition;
        if (position >= size) return EOF;
        return tokens.get(position);
    }


}

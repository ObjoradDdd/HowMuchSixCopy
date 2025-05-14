package App.howmuchsix.hms.Handlers;

import java.util.List;

import App.howmuchsix.hms.Blocks.Types;
import App.howmuchsix.hms.Expression.BinaryExpression;
import App.howmuchsix.hms.Expression.BooleanExpression;
import App.howmuchsix.hms.Expression.DoubleExpression;
import App.howmuchsix.hms.Expression.Expression;
import App.howmuchsix.hms.Expression.IntExpression;
import App.howmuchsix.hms.Expression.LogicalExpression;
import App.howmuchsix.hms.Expression.NullExpression;
import App.howmuchsix.hms.Expression.NumberExpression;
import App.howmuchsix.hms.Expression.StringExpression;
import App.howmuchsix.hms.Expression.UnaryExpression;
import App.howmuchsix.hms.Library.Variables;


public final class Parser {
    private final List<Token> tokens;
    private final int size;
    private static final Token EOF = new Token(TokenType.EOF, "");

    private List<String> scopeNames = List.of("MainScope");
    private int pos;

    public Parser(List<Token> tokens, List<String> scopeNames) {
        this.scopeNames = scopeNames;
        this.tokens = tokens;
        size = tokens.size();
    }

    public Parser(List<Token> tokens) {
        this.tokens = tokens;
        size = tokens.size();
    }

    public Expression<String> parseString() {
        return stringExpression();
    }

    private Expression<String> stringExpression() {
        return additiveString();
    }

    private Expression<String> additiveString() {
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
        if (match(TokenType.NULL)) {
            return new NullExpression<>();
        }
        if (match(TokenType.WORD)) {
            return Variables.getExpression(current.getText(), List.of(String.class), scopeNames);
        }
        if (match(TokenType.FUNCTION)) {
            return new StringExpression(Variables.getFunctionValue(current.getText(),scopeNames,current.getArguments(), List.of(Types.STRING)));
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

        if (match(TokenType.NULL)) {
            return new NullExpression<>();
        }

        if (match(TokenType.WORD)) {
            return Variables.getExpression(current.getText(), List.of(Number.class), scopeNames);
        }
        if (match(TokenType.FUNCTION)) {
            Object value =  Variables.getFunctionValue(current.getText(),scopeNames,current.getArguments(), List.of(Types.DOUBLE, Types.INT));
            if (value instanceof Double) {
                return new NumberExpression(new DoubleExpression((Double) value));
            }
            if (value instanceof Integer) {
                return new NumberExpression(new IntExpression((Integer) value));
            }
        }
        if (match(TokenType.OPEN_PAREN)) {
            Expression<Number> result = numberExpression();
            match(TokenType.CLOSE_PAREN);
            return result;
        }
        throw new RuntimeException("Invalid expression");
    }

    public Expression<Boolean> parseLogical() {
        return logicalExpression();
    }

    private Expression<Boolean> logicalExpression() {
        Expression<Boolean> result = primaryLogical();

        while (true) {
            if (match(TokenType.OR)) {
                result = new LogicalExpression("||", result, primaryLogical());
                continue;
            }
            if (match(TokenType.AND)) {
                result = new LogicalExpression("&&", result, primaryLogical());
                continue;
            }
            break;
        }
        return result;
    }

    private Expression<Boolean> primaryLogical() {
        if (match(TokenType.OPEN_PAREN)) {
            Expression<Boolean> result = logicalExpression();
            match(TokenType.CLOSE_PAREN);
            return result;
        }

        if (match(TokenType.NOT)) {
            return new LogicalExpression("!", primaryLogical(), null);
        }

        if (match(TokenType.TRUE)) {
            return new BooleanExpression(true);
        }
        if (match(TokenType.FALSE)) {
            return new BooleanExpression(false);
        }

        if (match(TokenType.WORD)) {
            return Variables.getExpression(previous().getText(), List.of(Boolean.class), scopeNames);
        }
        if (match(TokenType.FUNCTION)) {
            return new BooleanExpression(Variables.getFunctionValue(previous().getText(),scopeNames, previous().getArguments(), List.of(Types.BOOLEAN)));
        }

        Expression<?> left = primaryValue();

        if (match(TokenType.EQ)) {
            return new LogicalExpression("==", left, primaryValue());
        }
        if (match(TokenType.NOT_EQ)) {
            return new LogicalExpression("!=", left, primaryValue());
        }
        if (match(TokenType.MORE)) {
            return new LogicalExpression(">", left, primaryValue());
        }
        if (match(TokenType.LESS)) {
            return new LogicalExpression("<", left, primaryValue());
        }
        if (match(TokenType.MORE_EQ)) {
            return new LogicalExpression(">=", left, primaryValue());
        }
        if (match(TokenType.LESS_EQ)) {
            return new LogicalExpression("<=", left, primaryValue());
        }

        throw new RuntimeException("Expected boolean expression");
    }

    private Expression<?> primaryValue() {
        if (match(TokenType.MATH)) {
            List<Token> tokens = new Lexer(previous().getText()).tokenize();
            return new Parser(tokens, scopeNames).parseArithmetic();
        }
        if (match(TokenType.STR)) {
            List<Token> tokens = new Lexer(previous().getText()).tokenize();
            return new Parser(tokens, scopeNames).parseString();
        }

        throw new RuntimeException("Expected value expression");
    }

    public Expression<String> parseToPrint(){
        StringBuilder returnString = new StringBuilder();
        for(int i = 0; i < tokens.size(); i++){
            Token current = peek(0);
            if (match(TokenType.MATH)) {
                List<Token> tokens = new Lexer(previous().getText()).tokenize();
                returnString.append(new Parser(tokens, scopeNames).parseArithmetic().toString());
                continue;
            }
            if (match(TokenType.STR)) {
                List<Token> tokens = new Lexer(previous().getText()).tokenize();
                returnString.append(new Parser(tokens, scopeNames).parseString().toString());
                continue;
            }
            if (match(TokenType.WORD)){
                returnString.append(Variables.getExpression(current.getText(), List.of(Number.class, String.class, Boolean.class), scopeNames).toString());
                continue;
            }
            if (match(TokenType.FUNCTION)) {
                return new StringExpression(Variables.getFunctionValue(current.getText(),scopeNames,current.getArguments(), List.of(Types.DOUBLE, Types.INT, Types.BOOLEAN, Types.STRING)).toString());
            }
            if (!matchWithoutMove(TokenType.MATH) && !matchWithoutMove(TokenType.STR) && !matchWithoutMove(TokenType.WORD)){
                returnString.append(current.getText());
            }
        }
        return new StringExpression(returnString.toString());
    }

    private Token previous() {
        return peek(-1);
    }

    private boolean match(TokenType type) {
        final Token current = peek(0);
        if (type != current.getType()) return false;
        pos++;
        return true;
    }

    private boolean matchWithoutMove(TokenType type) {
        final Token current = peek(0);
        return type == current.getType();
    }

    private Token peek(int relativePosition) {
        final int position = pos + relativePosition;
        if (position >= size) return EOF;
        return tokens.get(position);
    }
}

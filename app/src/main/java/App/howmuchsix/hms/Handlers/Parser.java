package App.howmuchsix.hms.Handlers;

import java.util.List;

import App.howmuchsix.hms.Blocks.Types;
import App.howmuchsix.hms.Expression.BinaryExpression;
import App.howmuchsix.hms.Expression.BooleanExpression;
import App.howmuchsix.hms.Expression.DoubleExpression;
import App.howmuchsix.hms.Expression.Expression;
import App.howmuchsix.hms.Expression.IntExpression;
import App.howmuchsix.hms.Expression.LogicalBinaryExpression;
import App.howmuchsix.hms.Expression.NullExpression;
import App.howmuchsix.hms.Expression.NumberExpression;
import App.howmuchsix.hms.Expression.StringExpression;
import App.howmuchsix.hms.Expression.UnaryExpression;
import App.howmuchsix.hms.Expression.UnaryLogicalExpression;
import App.howmuchsix.hms.Library.Variables;


public final class Parser {
    private final List<Token> tokens;
    private final int size;
    private static final Token EOF = new Token(TokenType.EOF, "");

    private final List<String> scopeNames;
    private int pos;

    public Parser(List<Token> tokens, List<String> scopeNames) {
        this.scopeNames = scopeNames;
        this.tokens = tokens;
        size = tokens.size();
    }

    public Expression<String> parseString() {
        pos = 0;
        return stringExpression();
    }

    private Expression<String> stringExpression() {
        return additiveString();
    }

    private Expression<String> additiveString() {
        Expression<String> result = primaryString();

        while (true) {
            if (match(TokenType.PLUS)) {
                result = new StringExpression((new BinaryExpression<>("+", result, primaryString())).eval());
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
            return new NullExpression<>(Types.STRING);
        }
        if (match(TokenType.WORD)) {
            return Variables.getExpression(current.getText(), Types.STRING.getTypeClass(), scopeNames);
        }
        if (match(TokenType.FUNCTION)) {
            return Variables.getFunctionValue(current.getText(),scopeNames,current.getArguments(), Types.STRING.getTypeClass());
        }
        if (match(TokenType.ARRAY)){
            return Variables.getFromArray(current.getText(), current.getBody(), Types.STRING.getTypeClass(), scopeNames);
        }
        if (match(TokenType.OPEN_PAREN)) {
            Expression<String> result = stringExpression();
            match(TokenType.CLOSE_PAREN);
            return result;
        }
        throw new RuntimeException("Invalid STRING expression");
    }

    public Expression<Number> parseArithmetic() {
        pos = 0;
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
            return new NullExpression<>(Types.NUMBER);
        }

        if (match(TokenType.WORD)) {
            return Variables.getExpression(current.getText(), Types.NUMBER.getTypeClass(), scopeNames);
        }
        if (match(TokenType.FUNCTION)) {
            Expression<? extends Number> value =  Variables.getFunctionValue(current.getText(),scopeNames,current.getArguments(), Types.NUMBER.getTypeClass());
            if (value.eval() instanceof Double) {
                return new NumberExpression(value);
            }
            if (value.eval() instanceof Integer) {
                return new NumberExpression(value);
            }
        }
        if (match(TokenType.ARRAY)){
            return Variables.getFromArray(current.getText(), current.getBody(), Types.NUMBER.getTypeClass(), scopeNames);
        }
        if (match(TokenType.OPEN_PAREN)) {
            Expression<Number> result = numberExpression();
            match(TokenType.CLOSE_PAREN);
            return result;
        }
        throw new RuntimeException("Invalid NUMBER expression");
    }

    public Expression<Boolean> parseLogical() {
        pos = 0;
        return logicalExpression();
    }

    private Expression<Boolean> logicalExpression() {
        Expression<Boolean> result = primaryLogical();

        while (true) {
            if (match(TokenType.OR)) {
                result = new LogicalBinaryExpression("||", result, primaryLogical());
                continue;
            }
            if (match(TokenType.AND)) {
                result = new LogicalBinaryExpression("&&", result, primaryLogical());
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
            return new UnaryLogicalExpression("!", primaryLogical());
        }

        if (match(TokenType.TRUE)) {
            return new BooleanExpression(true);
        }
        if (match(TokenType.FALSE)) {
            return new BooleanExpression(false);
        }

        if (match(TokenType.WORD)) {
            return Variables.getExpression(previous().getText(), Types.BOOLEAN.getTypeClass(), scopeNames);
        }
        if (match(TokenType.FUNCTION)) {
            return Variables.getFunctionValue(previous().getText(),scopeNames, previous().getArguments(), Types.BOOLEAN.getTypeClass());
        }

        if (match(TokenType.ARRAY)){
            return Variables.getFromArray(previous().getText(), previous().getBody(), Types.BOOLEAN.getTypeClass(), scopeNames);
        }

        Expression<?> left = primaryValue();

        if (match(TokenType.EQ)) {
            return new LogicalBinaryExpression("==", left, primaryValue());
        }
        if (match(TokenType.NOT_EQ)) {
            return new LogicalBinaryExpression("!=", left, primaryValue());
        }
        if (match(TokenType.MORE)) {
            return new LogicalBinaryExpression(">", left, primaryValue());
        }
        if (match(TokenType.LESS)) {
            return new LogicalBinaryExpression("<", left, primaryValue());
        }
        if (match(TokenType.MORE_EQ)) {
            return new LogicalBinaryExpression(">=", left, primaryValue());
        }
        if (match(TokenType.LESS_EQ)) {
            return new LogicalBinaryExpression("<=", left, primaryValue());
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

        throw new RuntimeException("Invalid BOOLEAN expression");
    }

    public Expression<String> parseToPrint(){
        pos = 0;
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
                returnString.append(Variables.getExpression(current.getText(), Types.OBJECT.getTypeClass(), scopeNames).toString());
                continue;
            }
            if (match(TokenType.FUNCTION)) {
                return new StringExpression(Variables.getFunctionValue(current.getText(),scopeNames,current.getArguments(), Types.OBJECT.getTypeClass()).toString());
            }
            if (match(TokenType.ARRAY)){
                return Variables.getFromArray(current.getText(), current.getBody(), Types.OBJECT.getTypeClass(), scopeNames);
            }
            if (!matchWithoutMove(TokenType.MATH) && !matchWithoutMove(TokenType.STR) && !matchWithoutMove(TokenType.WORD)){
                returnString.append(current.getText());
            }
        }
        return new StringExpression(returnString.toString());
    }

    public Expression<Expression<?>[]> parseCollection(){
        pos = 0;

        if (match(TokenType.WORD)) {
            return Variables.getExpression(previous().getText(), Types.ARRAY.getTypeClass(), scopeNames);
        }
        if (match(TokenType.FUNCTION)) {
            return Variables.getFunctionValue(previous().getText(),scopeNames, previous().getArguments(), Types.ARRAY.getTypeClass());
        }

        throw new RuntimeException("parse collection error");

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

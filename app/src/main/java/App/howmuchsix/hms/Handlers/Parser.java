package App.howmuchsix.hms.Handlers;

import java.util.ArrayList;
import java.util.List;

import App.howmuchsix.hms.Blocks.ProgramRunException;
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

    private final String id;
    private static final Token EOF = new Token(TokenType.EOF, "");

    private final List<String> scopeNames;
    private int pos;

    private final Variables lib;

    public Parser(List<Token> tokens, List<String> scopeNames, Variables lib, String id) {
        this.id = id;
        this.scopeNames = scopeNames;
        this.tokens = tokens;
        size = tokens.size();
        this.lib = lib;
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
                result = new StringExpression((new BinaryExpression<>("+", result, primaryString(), id)).eval());
                continue;
            }
            if (matchWithoutMove(TokenType.MINUS) || matchWithoutMove(TokenType.STAR) || matchWithoutMove(TokenType.SLASH) || matchWithoutMove(TokenType.REMAINDER)) {
                throw new ProgramRunException("Incorrect operator for string type", id);
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
            return lib.getExpression(current.getText(), Types.STRING.getTypeClass(), scopeNames, id);
        }
        if (match(TokenType.FUNCTION)) {
            return lib.getFunctionValue(current.getText(), scopeNames, current.getArguments(), Types.STRING.getTypeClass(), id);
        }
        if (match(TokenType.ARRAY)) {
            return lib.getFromArray(current.getText(), current.getBody(), Types.STRING.getTypeClass(), scopeNames, id);
        }
        if (match(TokenType.OPEN_PAREN)) {
            Expression<String> result = stringExpression();
            match(TokenType.CLOSE_PAREN);
            return result;
        }
        throw new ProgramRunException("Invalid STRING expression", id);
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
                result = new BinaryExpression<>("+", result, multiply(), id);
                continue;
            }
            if (match(TokenType.MINUS)) {
                result = new BinaryExpression<>("-", result, multiply(), id);
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
                result = new BinaryExpression<>("*", result, power(), id);
                continue;
            }
            if (match(TokenType.SLASH)) {
                result = new BinaryExpression<>("/", result, power(), id);
                continue;
            }
            if (match(TokenType.REMAINDER)) {
                result = new BinaryExpression<>("%", result, power(), id);
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
                result = new BinaryExpression<>("^", result, unary(), id);
                continue;
            }
            break;
        }
        return result;
    }

    private Expression<Number> unary() {
        if (match(TokenType.MINUS)) {
            return new UnaryExpression<>("-", primary(), id);
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
            return lib.getExpression(current.getText(), Types.NUMBER.getTypeClass(), scopeNames, id);
        }
        if (match(TokenType.FUNCTION)) {
            Expression<? extends Number> value = lib.getFunctionValue(current.getText(), scopeNames, current.getArguments(), Types.NUMBER.getTypeClass(), id);
            if (value.eval() instanceof Double) {
                return new NumberExpression(value);
            }
            if (value.eval() instanceof Integer) {
                return new NumberExpression(value);
            }
        }
        if (match(TokenType.ARRAY)) {
            return lib.getFromArray(current.getText(), current.getBody(), Types.NUMBER.getTypeClass(), scopeNames, id);
        }
        if (match(TokenType.OPEN_PAREN)) {
            Expression<Number> result = numberExpression();
            match(TokenType.CLOSE_PAREN);
            return result;
        }
        throw new ProgramRunException("Invalid NUMBER expression", id);
    }

    public Expression<Expression<?>[]> parseCollection() {
        pos = 0;

        if (match(TokenType.WORD)) {
            return lib.getExpression(previous().getText(), Types.ARRAY.getTypeClass(), scopeNames, id);
        }
        if (match(TokenType.FUNCTION)) {
            return lib.getFunctionValue(previous().getText(), scopeNames, previous().getArguments(), Types.ARRAY.getTypeClass(), id);
        }

        throw new ProgramRunException("Parse collection error", id);

    }

    public Expression<Boolean> parseLogical() {
        pos = 0;
        return unifiedLogicalOr();
    }

    private Expression<Boolean> unifiedLogicalOr() {
        Expression<Boolean> left = unifiedLogicalAnd();

        while (match(TokenType.OR)) {
            Expression<Boolean> right = unifiedLogicalAnd();
            left = new LogicalBinaryExpression("||", left, right, id);
        }
        return left;
    }

    private Expression<Boolean> unifiedLogicalAnd() {
        Expression<Boolean> left = unifiedLogicalComparison();

        while (match(TokenType.AND)) {
            Expression<Boolean> right = unifiedLogicalComparison();
            left = new LogicalBinaryExpression("&&", left, right, id);
        }
        return left;
    }

    private Expression<Boolean> unifiedLogicalComparison() {
        if (matchWithoutMove(TokenType.TRUE) || matchWithoutMove(TokenType.FALSE) ||
                matchWithoutMove(TokenType.NOT) || matchWithoutMove(TokenType.OPEN_PAREN)) {
            return unifiedLogicalPrimary();
        }


        Expression<?> left = unifiedExpression();

        if (match(TokenType.EQ)) {
            Expression<?> right = unifiedExpression();
            return new LogicalBinaryExpression("==", left, right, id);
        }
        if (match(TokenType.NOT_EQ)) {
            Expression<?> right = unifiedExpression();
            return new LogicalBinaryExpression("!=", left, right, id);
        }
        if (match(TokenType.MORE)) {
            Expression<?> right = unifiedExpression();
            return new LogicalBinaryExpression(">", left, right, id);
        }
        if (match(TokenType.LESS)) {
            Expression<?> right = unifiedExpression();
            return new LogicalBinaryExpression("<", left, right, id);
        }
        if (match(TokenType.MORE_EQ)) {
            Expression<?> right = unifiedExpression();
            return new LogicalBinaryExpression(">=", left, right, id);
        }
        if (match(TokenType.LESS_EQ)) {
            Expression<?> right = unifiedExpression();
            return new LogicalBinaryExpression("<=", left, right, id);
        }

        if (left instanceof Expression && isBooleanType(left)) {
            return (Expression<Boolean>) left;
        }

        throw new ProgramRunException("Expected boolean expression or comparison", id);
    }

    private Expression<Boolean> unifiedLogicalPrimary() {
        if (match(TokenType.OPEN_PAREN)) {
            Expression<Boolean> result = unifiedLogicalOr();
            match(TokenType.CLOSE_PAREN);
            return result;
        }

        if (match(TokenType.NOT)) {
            return new UnaryLogicalExpression("!", unifiedLogicalPrimary());
        }

        if (match(TokenType.TRUE)) {
            return new BooleanExpression(true);
        }

        if (match(TokenType.FALSE)) {
            return new BooleanExpression(false);
        }

        final Token current = peek(0);

        if (match(TokenType.WORD)) {
            Expression<?> expr = getBooleanVariable(current.getText());
            if (isBooleanType(expr)) {
                return (Expression<Boolean>) expr;
            }
            throw new ProgramRunException("Variable '" + current.getText() + "' is not boolean type", id);
        }

        if (match(TokenType.FUNCTION)) {
            Expression<?> expr = getBooleanFunction(current.getText(), current.getArguments());
            if (isBooleanType(expr)) {
                return (Expression<Boolean>) expr;
            }
            throw new ProgramRunException("Function '" + current.getText() + "' does not return boolean", id);
        }

        if (match(TokenType.ARRAY)) {
            Expression<?> expr = getBooleanArray(current.getText(), current.getBody());
            if (isBooleanType(expr)) {
                return (Expression<Boolean>) expr;
            }
            throw new ProgramRunException("Array element is not boolean type", id);
        }

        throw new ProgramRunException("Expected boolean expression", id);
    }

    private Expression<?> unifiedExpression() {
        return unifiedAdditive();
    }

    private Expression<?> unifiedAdditive() {
        Expression<?> left = unifiedMultiplicative();

        while (true) {
            if (match(TokenType.PLUS)) {
                Expression<?> right = unifiedMultiplicative();
                if (isStringType(left) || isStringType(right)) {
                    left = new StringExpression(
                            new BinaryExpression<>("+", convertToString(left), convertToString(right), id).eval()
                    );
                } else {
                    left = new BinaryExpression<>("+", convertToNumber(left), convertToNumber(right), id);
                }
            } else if (match(TokenType.MINUS)) {
                Expression<?> right = unifiedMultiplicative();
                left = new BinaryExpression<>("-", convertToNumber(left), convertToNumber(right), id);
            } else {
                break;
            }
        }
        return left;
    }

    private Expression<?> unifiedMultiplicative() {
        Expression<?> left = unifiedPower();

        while (true) {
            if (match(TokenType.STAR)) {
                Expression<?> right = unifiedPower();
                left = new BinaryExpression<>("*", convertToNumber(left), convertToNumber(right), id);
            } else if (match(TokenType.SLASH)) {
                Expression<?> right = unifiedPower();
                left = new BinaryExpression<>("/", convertToNumber(left), convertToNumber(right), id);
            } else if (match(TokenType.REMAINDER)) {
                Expression<?> right = unifiedPower();
                left = new BinaryExpression<>("%", convertToNumber(left), convertToNumber(right), id);
            } else {
                break;
            }
        }
        return left;
    }

    private Expression<?> unifiedPower() {
        Expression<?> left = unifiedUnary();

        while (match(TokenType.POWER)) {
            Expression<?> right = unifiedUnary();
            left = new BinaryExpression<>("^", convertToNumber(left), convertToNumber(right), id);
        }
        return left;
    }

    private Expression<?> unifiedUnary() {
        if (match(TokenType.MINUS)) {
            return new UnaryExpression<>("-", convertToNumber(unifiedPrimary()), id);
        }
        return unifiedPrimary();
    }

    private Expression<?> unifiedPrimary() {
        final Token current = peek(0);

        if (match(TokenType.INT)) {
            return new NumberExpression(new IntExpression(Integer.parseInt(current.getText())));
        }

        if (match(TokenType.DOUBLE)) {
            return new NumberExpression(new DoubleExpression(Double.parseDouble(current.getText())));
        }

        if (match(TokenType.STRING)) {
            return new StringExpression(current.getText());
        }

        if (match(TokenType.NULL)) {
            return new NullExpression<>(Types.OBJECT);
        }

        if (match(TokenType.WORD)) {
            return getVariableAnyType(current.getText());
        }

        if (match(TokenType.FUNCTION)) {
            return getFunctionAnyType(current.getText(), current.getArguments());
        }

        if (match(TokenType.ARRAY)) {
            return getArrayAnyType(current.getText(), current.getBody());
        }

        if (match(TokenType.OPEN_PAREN)) {
            Expression<?> result = unifiedExpression();
            match(TokenType.CLOSE_PAREN);
            return result;
        }

        throw new ProgramRunException("Unexpected token: " + current.getText(), id);
    }

    private Expression<?> getBooleanVariable(String name) {
        try {
            return lib.getExpression(name, Types.BOOLEAN.getTypeClass(), scopeNames, id);
        } catch (Exception e) {
            throw new ProgramRunException("Boolean variable '" + name + "' not found", id);
        }
    }

    private Expression<?> getBooleanFunction(String name, List<String> arguments) {
        try {
            return lib.getFunctionValue(name, scopeNames, arguments, Types.BOOLEAN.getTypeClass(), id);
        } catch (Exception e) {
            throw new ProgramRunException("Boolean function '" + name + "' not found", id);
        }
    }

    private Expression<?> getBooleanArray(String name, String body) {
        try {
            return lib.getFromArray(name, body, Types.BOOLEAN.getTypeClass(), scopeNames, id);
        } catch (Exception e) {
            throw new ProgramRunException("Boolean array element '" + name + "' not found", id);
        }
    }

    private Expression<?> getVariableAnyType(String name) {
        try {
            return lib.getExpression(name, Types.NUMBER.getTypeClass(), scopeNames, id);
        } catch (Exception e1) {
            try {
                return lib.getExpression(name, Types.STRING.getTypeClass(), scopeNames, id);
            } catch (Exception e2) {
                try {
                    return lib.getExpression(name, Types.BOOLEAN.getTypeClass(), scopeNames, id);
                } catch (Exception e3) {
                    return lib.getExpression(name, Types.OBJECT.getTypeClass(), scopeNames, id);
                }
            }
        }
    }

    private Expression<?> getFunctionAnyType(String name, List<String> arguments) {
        try {
            return lib.getFunctionValue(name, scopeNames, arguments, Types.NUMBER.getTypeClass(), id);
        } catch (Exception e1) {
            try {
                return lib.getFunctionValue(name, scopeNames, arguments, Types.STRING.getTypeClass(), id);
            } catch (Exception e2) {
                try {
                    return lib.getFunctionValue(name, scopeNames, arguments, Types.BOOLEAN.getTypeClass(), id);
                } catch (Exception e3) {
                    return lib.getFunctionValue(name, scopeNames, arguments, Types.OBJECT.getTypeClass(), id);
                }
            }
        }
    }

    private Expression<?> getArrayAnyType(String name, String body) {
        try {
            return lib.getFromArray(name, body, Types.NUMBER.getTypeClass(), scopeNames, id);
        } catch (Exception e1) {
            try {
                return lib.getFromArray(name, body, Types.STRING.getTypeClass(), scopeNames, id);
            } catch (Exception e2) {
                try {
                    return lib.getFromArray(name, body, Types.BOOLEAN.getTypeClass(), scopeNames, id);
                } catch (Exception e3) {
                    return lib.getFromArray(name, body, Types.OBJECT.getTypeClass(), scopeNames, id);
                }
            }
        }
    }

    private boolean isBooleanType(Expression<?> expr) {
        if (expr instanceof BooleanExpression) return true;
        try {
            return expr.eval() instanceof Boolean;
        } catch (Exception e) {
            return false;
        }
    }

    private boolean isStringType(Expression<?> expr) {
        if (expr instanceof StringExpression) return true;
        try {
            return expr.eval() instanceof String;
        } catch (Exception e) {
            return false;
        }
    }

    private Expression<String> convertToString(Expression<?> expr) {
        if (expr instanceof StringExpression) {
            return (Expression<String>) expr;
        }
        return new StringExpression(expr.eval().toString());
    }

    private Expression<Number> convertToNumber(Expression<?> expr) {
        if (expr instanceof NumberExpression) {
            return (Expression<Number>) expr;
        }
        if (expr.eval() instanceof Number) {
            return (Expression<Number>) expr;
        }
        throw new ProgramRunException("Cannot convert to number: " + expr.getClass(), id);
    }

    public Expression<String> parseStringInterpolation() {
        pos = 0;
        StringBuilder result = new StringBuilder();

        while (pos < size) {
            Token current = peek(0);

            if (current.getType() == TokenType.EOF) {
                break;
            }

            if (match(TokenType.INTERPOLATION_START)) {
                Expression<?> expr = parseInterpolationExpression();
                result.append(expr.eval().toString());
                match(TokenType.INTERPOLATION_END);
            } else if (match(TokenType.TAG)) {
                Expression<?> expr = parseSimpleInterpolation();
                result.append(expr.eval().toString());
            } else if (match(TokenType.TEXT)) {
                result.append(current.getText());
            } else {
                result.append(current.getText());
                pos++;
            }
        }

        return new StringExpression(result.toString());
    }

    private Expression<?> parseSimpleInterpolation() {
        Token current = peek(0);

        if (match(TokenType.WORD)) {
            return getVariableAnyType(current.getText());
        } else if (match(TokenType.FUNCTION)) {
            return getFunctionAnyType(current.getText(), current.getArguments());
        } else if (match(TokenType.ARRAY)) {
            return getArrayAnyType(current.getText(), current.getBody());
        } else {
            throw new ProgramRunException("Expected variable, function, or array after #", id);
        }
    }

    private Expression<?> parseInterpolationExpression() {

        List<Token> expressionTokens = new ArrayList<>();

        while (pos < size) {
            Token current = peek(0);

            if (current.getType() == TokenType.INTERPOLATION_END) {
                break;
            }

            expressionTokens.add(current);
            pos++;
        }

        if (expressionTokens.isEmpty()) {
            throw new ProgramRunException("Empty interpolation expression", id);
        }

        Parser subParser = new Parser(expressionTokens, scopeNames, lib, id);

        try {
            subParser.pos = 0;
            return subParser.parseLogical();
        } catch (Exception e1) {
            try {
                subParser.pos = 0;
                return subParser.parseArithmetic();
            } catch (Exception e2) {
                try {
                    subParser.pos = 0;
                    return subParser.parseString();
                } catch (Exception e3) {
                    if (expressionTokens.size() == 1) {
                        Token token = expressionTokens.get(0);
                        if (token.getType() == TokenType.WORD) {
                            return getVariableAnyType(token.getText());
                        } else if (token.getType() == TokenType.FUNCTION) {
                            return getFunctionAnyType(token.getText(), token.getArguments());
                        } else if (token.getType() == TokenType.ARRAY) {
                            return getArrayAnyType(token.getText(), token.getBody());
                        }
                    }
                    throw new ProgramRunException("Cannot parse interpolation expression: " +
                            expressionTokens.stream().map(Token::getText).reduce("", (a, b) -> a + b), id);
                }
            }
        }
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

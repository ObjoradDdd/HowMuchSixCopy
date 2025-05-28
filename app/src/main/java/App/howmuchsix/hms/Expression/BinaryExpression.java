package App.howmuchsix.hms.Expression;

import java.util.Objects;

import App.howmuchsix.hms.Blocks.Types;

public final class BinaryExpression<T> implements Expression<T> {
    private final Expression<T> ex1;
    private final Expression<T> ex2;
    private final String operation;

    public BinaryExpression(String operation, Expression<T> ex1, Expression<T> ex2) {
        this.operation = operation;
        this.ex1 = ex1;
        this.ex2 = ex2;
        if (ex1.eval() == null || ex2.eval() == null) {
            throw new RuntimeException("You can't use operator " + operation + " with null");
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    public T eval() {
        T val1 = ex1.eval();
        T val2 = ex2.eval();

        if (val1 instanceof String || val2 instanceof String) {
            return (T) evaluateString(val1, val2);
        } else if (val1 instanceof Number && val2 instanceof Number) {
            return (T) evaluateNumber((Number) val1, (Number) val2);
        }

        throw new IllegalArgumentException("Unsupported types for operation: " +
                val1.getClass() + " and " + val2.getClass());
    }

    @Override
    public Types getType() {
        return null;
    }

    private String evaluateString(Object val1, Object val2) {
        if (Objects.equals(operation, "+")) {
            return String.valueOf(val1) + String.valueOf(val2);
        } else {
            throw new UnsupportedOperationException(
                    "Operation " + operation + " is not supported for Strings"
            );
        }
    }

    private Number evaluateNumber(Number val1, Number val2) {
        boolean useDouble = val1 instanceof Double || val2 instanceof Double;

        if (useDouble) {
            double v1 = val1.doubleValue();
            double v2 = val2.doubleValue();
            return switch (operation) {
                case "+" -> v1 + v2;
                case "-" -> v1 - v2;
                case "*" -> v1 * v2;
                case "/" -> v1 / v2;
                case "%" -> v1 % v2;
                case "^" -> Math.pow(v1, v2);
                default -> throw new UnsupportedOperationException(
                        "Unknown operation: " + operation
                );
            };
        } else {
            int v1 = val1.intValue();
            int v2 = val2.intValue();
            return switch (operation) {
                case "+" -> v1 + v2;
                case "-" -> v1 - v2;
                case "*" -> v1 * v2;
                case "/" -> v1 / v2;
                case "%" -> v1 % v2;
                case "^" -> (int) Math.pow(v1, v2);
                default -> throw new UnsupportedOperationException(
                        "Unknown operation: " + operation
                );
            };
        }
    }
}
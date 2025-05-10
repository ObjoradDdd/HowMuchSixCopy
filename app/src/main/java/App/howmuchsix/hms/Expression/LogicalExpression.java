package App.howmuchsix.hms.Expression;

import java.util.Objects;

import App.howmuchsix.hms.Blocks.Types;

public class LogicalExpression implements Expression<Boolean>{
    private final Expression<?> ex1;
    private final Expression<?> ex2;
    private final String operation;

    public LogicalExpression(String operation, Expression<?> ex1, Expression<?> ex2) {
        this.operation = operation;
        this.ex1 = ex1;
        this.ex2 = ex2;
    }

    @Override
    public Boolean eval() {
        Object val1 = ex1.eval();
        Object val2 = ex2.eval();

        if (val1 instanceof String || val2 instanceof String) {
            return evaluateString(val1, val2);
        } else if (val1 instanceof Number && val2 instanceof Number) {
            return evaluateNumber((Number) val1, (Number) val2);
        }

        throw new IllegalArgumentException("Unsupported types for operation: " +
                val1.getClass() + " and " + val2.getClass());
    }

    @Override
    public Types getType() {
        return null;
    }

    private boolean evaluateString(Object val1, Object val2) {

        return switch (operation) {
            case "=" -> String.valueOf(val1).equals(String.valueOf(val2));
            case "<" -> String.valueOf(val1).compareTo(String.valueOf(val2)) < 0;
            case ">" -> String.valueOf(val1).compareTo(String.valueOf(val2)) > 0;
            default -> throw new UnsupportedOperationException(
                    "Operation " + operation + " is not supported for Strings"
            );
        };
    }

    private boolean evaluateNumber(Number val1, Number val2) {
            return switch (operation) {
                case "=" -> Objects.equals(val1, val2);
                case ">" -> val1.doubleValue() > val2.doubleValue();
                case "<" -> val1.doubleValue() < val2.doubleValue();
                default -> throw new UnsupportedOperationException(
                        "Unknown operation: " + operation
                );
            };
    }
}

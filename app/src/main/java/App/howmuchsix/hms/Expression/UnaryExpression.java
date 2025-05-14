package App.howmuchsix.hms.Expression;

import App.howmuchsix.hms.Blocks.Types;

public class UnaryExpression<T> implements Expression<T> {
    private final Expression<T> ex1;
    private final String operation;

    public UnaryExpression(String operation, Expression<T> ex1) {
        this.operation = operation;
        this.ex1 = ex1;
    }

    @SuppressWarnings("unchecked")
    @Override
    public T eval() {
        T value = ex1.eval();

        if (value instanceof Number) {
            Number num = (Number) value;
            return (T) evaluateNumber(num);
        }

        throw new IllegalArgumentException(
                "Unary operations are only supported for numeric types, got: " +
                        value.getClass().getSimpleName()
        );
    }

    @Override
    public Types getType() {
        return null;
    }


    private Number evaluateNumber(Number value) {
        boolean useDouble = value instanceof Double;

        if (useDouble) {
            double val = value.doubleValue();
            return switch (operation) {
                case "-" -> -val;
                case "+" -> val;
                default -> throw new IllegalArgumentException(
                        "Unknown operation: " + operation
                );
            };
        } else {
            int val = value.intValue();
            return switch (operation) {
                case "-" -> -val;
                case "+" -> val;
                default -> throw new IllegalArgumentException(
                        "Unknown operation: " + operation
                );
            };
        }
    }
}

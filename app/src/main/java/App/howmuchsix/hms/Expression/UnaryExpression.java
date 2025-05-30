package App.howmuchsix.hms.Expression;

import App.howmuchsix.hms.Blocks.ProgramRunException;
import App.howmuchsix.hms.Blocks.Types;

public final class UnaryExpression<T> implements Expression<T> {
    private final Expression<T> ex1;
    private final String operation;

    private final String id;

    public UnaryExpression(String operation, Expression<T> ex1, String id) {
        this.operation = operation;
        this.ex1 = ex1;
        this.id = id;
    }

    @SuppressWarnings("unchecked")
    @Override
    public T eval() {
        T value = ex1.eval();

        if (value instanceof Number) {
            Number num = (Number) value;
            return (T) evaluateNumber(num);
        }

        throw new ProgramRunException(
                "Unary operations are only supported for numeric types, got: " +
                        value.getClass().getSimpleName(), id
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
                default -> throw new ProgramRunException(
                        "Unknown operation: " + operation, id
                );
            };
        } else {
            int val = value.intValue();
            return switch (operation) {
                case "-" -> -val;
                case "+" -> val;
                default -> throw new ProgramRunException(
                        "Unknown operation: " + operation, id
                );
            };
        }
    }
}

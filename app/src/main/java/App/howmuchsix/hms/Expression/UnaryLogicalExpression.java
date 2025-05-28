package App.howmuchsix.hms.Expression;

import App.howmuchsix.hms.Blocks.Types;

public final class UnaryLogicalExpression implements Expression<Boolean> {
    private final String operator;
    private final Expression<?> left;

    public UnaryLogicalExpression(String operator, Expression<?> left) {
        this.operator = operator;
        this.left = left;
        if (left.eval() == null) {
            throw new RuntimeException("You can't use operator " + operator + " with null");
        }
    }

    @Override
    public Boolean eval() {
        if (operator.equals("!")) {
            return !toBoolean(left.eval());
        }
        throw new RuntimeException("Unknown operator: " + operator);
    }

    private boolean toBoolean(Object value) {
        if (value instanceof Boolean) {
            return (Boolean) value;
        }
        if (value instanceof Number) {
            return ((Number) value).doubleValue() != 0;
        }
        if (value instanceof String) {
            return !((String) value).isEmpty();
        }
        return value != null;
    }

    @Override
    public Types getType() {
        return null;
    }
}

package App.howmuchsix.hms.Expression;
import App.howmuchsix.hms.Blocks.Types;

public class LogicalExpression implements Expression<Boolean> {
    private final String operator;
    private final Expression<?> left;
    private final Expression<?> right;

    public LogicalExpression(String operator, Expression<?> left, Expression<?> right) {
        this.operator = operator;
        this.left = left;
        this.right = right;
    }

    @Override
    public Boolean eval() {
        if (operator.equals("!")) {
            return !toBoolean(left.eval());
        }

        if (operator.isEmpty()){
            return toBoolean(left.eval());
        }

        Object leftVal = left.eval();
        Object rightVal = right.eval();

        return switch (operator) {
            case "&&" -> toBoolean(leftVal) && toBoolean(rightVal);
            case "||" -> toBoolean(leftVal) || toBoolean(rightVal);
            case "==" -> equals(leftVal, rightVal);
            case "!=" -> !equals(leftVal, rightVal);
            case ">" -> compare(leftVal, rightVal) > 0;
            case "<" -> compare(leftVal, rightVal) < 0;
            case ">=" -> compare(leftVal, rightVal) >= 0;
            case "<=" -> compare(leftVal, rightVal) <= 0;
            default -> throw new RuntimeException("Unknown operator: " + operator);
        };
    }

    @Override
    public Types getType() {
        return null;
    }


    private boolean equals(Object left, Object right) {
        if (left == null) return right == null;
        if (right == null) return false;
        return left.equals(right);
    }

    private int compare(Object left, Object right) {
        if (left instanceof Number && right instanceof Number) {
            return Double.compare(
                    ((Number) left).doubleValue(),
                    ((Number) right).doubleValue()
            );
        }
        if (left instanceof String && right instanceof String) {
            return ((String) left).compareTo((String) right);
        }
        throw new RuntimeException("Cannot compare " + left + " and " + right);
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
}
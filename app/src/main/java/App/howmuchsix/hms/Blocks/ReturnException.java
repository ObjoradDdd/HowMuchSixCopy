package App.howmuchsix.hms.Blocks;

import App.howmuchsix.hms.Expression.Expression;

public class ReturnException extends Exception {
    private final Expression<?> value;

    public ReturnException(String returnExpressionString, Expression<?> value) {
        super(returnExpressionString);
        this.value = value;
    }

    public Expression<?> getValue() {
        return value;
    }
}

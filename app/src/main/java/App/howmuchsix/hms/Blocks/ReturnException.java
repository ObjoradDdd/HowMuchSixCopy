package App.howmuchsix.hms.Blocks;

import App.howmuchsix.hms.Expression.Expression;

public class ReturnException extends Exception {
    private final Expression<?> value;

    public ReturnException(Expression<?> value) {
        super("Return block outside a function");
        this.value = value;
    }

    public Expression<?> getValue() {
        return value;
    }
}

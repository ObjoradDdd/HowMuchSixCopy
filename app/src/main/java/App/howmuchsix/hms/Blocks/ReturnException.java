package App.howmuchsix.hms.Blocks;

import App.howmuchsix.hms.Expression.Expression;

public class ReturnException extends ProgramRunException {
    private final Expression<?> value;

    public ReturnException(Expression<?> value, String id) {
        super("Return block outside a function", id);
        this.value = value;
    }

    public Expression<?> getValue() {
        return value;
    }
}

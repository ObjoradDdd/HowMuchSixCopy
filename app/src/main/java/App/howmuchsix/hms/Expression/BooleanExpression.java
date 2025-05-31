package App.howmuchsix.hms.Expression;

import androidx.annotation.NonNull;

import App.howmuchsix.hms.Blocks.Types;

public final class BooleanExpression implements Expression<Boolean> {
    private static final Types type = Types.BOOLEAN;
    private final Boolean value;

    public BooleanExpression(Boolean value) {
        this.value = value;
    }

    @Override
    public Boolean eval() {
        return value;
    }

    @Override
    public Types getType() {
        return type;
    }

    @NonNull
    @Override
    public String toString() {
        return Boolean.toString(value);
    }
}

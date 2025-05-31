package App.howmuchsix.hms.Expression;

import androidx.annotation.NonNull;

import App.howmuchsix.hms.Blocks.Types;

public final class IntExpression implements Expression<Integer> {
    static final Types type = Types.INT;
    private final int value;

    public IntExpression(int value) {
        this.value = value;
    }

    @Override
    public Integer eval() {
        return value;
    }


    @Override
    public Types getType() {
        return type;
    }


    @NonNull
    @Override
    public String toString() {
        return Integer.toString(value);
    }
}

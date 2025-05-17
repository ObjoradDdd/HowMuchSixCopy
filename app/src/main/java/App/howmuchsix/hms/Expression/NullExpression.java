package App.howmuchsix.hms.Expression;

import androidx.annotation.NonNull;

import App.howmuchsix.hms.Blocks.Types;

public class NullExpression<T> implements Expression<T> {

    private final Types type;

    public NullExpression(Types type) {
        this.type = type;
    }

    @Override
    public T eval() {
        return null;
    }


    @Override
    public Types getType() {
        return type;
    }

    @NonNull
    @Override
    public String toString() {
        return "null";
    }
}

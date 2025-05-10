package App.howmuchsix.hms.Expression;

import androidx.annotation.NonNull;

import App.howmuchsix.hms.Blocks.Types;

public class NullExpression<T> implements Expression<T> {

    private static final Types type =  Types.NUll;

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

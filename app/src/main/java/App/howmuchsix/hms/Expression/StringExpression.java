package App.howmuchsix.hms.Expression;

import androidx.annotation.NonNull;

import App.howmuchsix.hms.Blocks.Types;

public final class StringExpression implements Expression<String> {
    private static final Types type = Types.STRING;
    private final String value;

    public StringExpression(String value) {
        this.value = value;
    }

    @Override
    public String eval() {
        return value;
    }

    @Override
    public Types getType() {
        return type;
    }

    @NonNull
    @Override
    public String toString() {
        return value;
    }
}

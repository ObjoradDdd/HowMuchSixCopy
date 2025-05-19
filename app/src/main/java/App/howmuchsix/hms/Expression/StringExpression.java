package App.howmuchsix.hms.Expression;

import androidx.annotation.NonNull;

import App.howmuchsix.hms.Blocks.Types;

public class StringExpression implements Expression<String>{
    private final String value;
    private static final Types type = Types.STRING;

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
    public String toString(){
        return value;
    }
}

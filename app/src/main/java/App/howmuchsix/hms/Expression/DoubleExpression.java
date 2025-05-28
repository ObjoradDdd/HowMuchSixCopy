package App.howmuchsix.hms.Expression;

import androidx.annotation.NonNull;

import App.howmuchsix.hms.Blocks.Types;

public final class DoubleExpression implements Expression<Double> {

    private final Double value;
    private static final Types type = Types.DOUBLE;

    public DoubleExpression(Double value) {
        this.value = value;
    }

    @Override
    public Double eval() {
        return value;
    }

    @Override
    public Types getType() {
        return type;
    }

    @NonNull
    @Override
    public String toString() {
        return Double.toString(value);
    }
}

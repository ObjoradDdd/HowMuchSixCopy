package App.howmuchsix.hms.Expression;

import androidx.annotation.NonNull;

import App.howmuchsix.hms.Blocks.Types;

public final class NumberExpression implements Expression<Number> {

    private final Expression<? extends Number> expression;

    public NumberExpression(Expression<? extends Number> expression) {
        this.expression = expression;
    }

    @Override
    public Number eval() {
        return expression.eval();
    }


    @Override
    public Types getType() {
        return expression.getType();
    }

    @NonNull
    @Override
    public String toString() {
        return expression.toString();
    }
}

package App.howmuchsix.hms.Blocks;

import App.howmuchsix.hms.Expression.Expression;

public class PrintBlock extends Block{
    Expression<?> output;
    public PrintBlock(Expression<?> output) {
        this.output = output;
    }

}

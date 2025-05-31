package App.howmuchsix.hms.Blocks.typeConverter;

import java.util.List;

import App.howmuchsix.hms.Blocks.Block;
import App.howmuchsix.hms.Blocks.ProgramRunException;
import App.howmuchsix.hms.Blocks.ReturnException;
import App.howmuchsix.hms.Blocks.Types;
import App.howmuchsix.hms.Expression.Expression;
import App.howmuchsix.hms.Expression.IntExpression;
import App.howmuchsix.hms.Expression.NumberExpression;
import App.howmuchsix.hms.Library.Variables;

public final class DoubleToIntBlock extends Block {

    public DoubleToIntBlock() {
        this.blockID = "double_to_int_block";
    }

    @Override
    public void Action(List<String> scopes, Variables lib) {
        try {
            Expression<Number> numberExpression = lib.getExpression("value", Types.DOUBLE.getTypeClass(), scopes, this.blockID);

            Number value = numberExpression.eval();
            int intValue = value.intValue();

            throw new ReturnException(new NumberExpression(new IntExpression(intValue)), this.getUUID());
        } catch (ReturnException e) {
            throw e;
        } catch (Exception e) {
            throw new ProgramRunException("Cannot convert double to int", this.getUUID());
        }
    }
}

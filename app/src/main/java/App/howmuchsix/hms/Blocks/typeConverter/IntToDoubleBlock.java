package App.howmuchsix.hms.Blocks.typeConverter;

import java.util.List;

import App.howmuchsix.hms.Blocks.Block;
import App.howmuchsix.hms.Blocks.ProgramRunException;
import App.howmuchsix.hms.Blocks.ReturnException;
import App.howmuchsix.hms.Blocks.Types;
import App.howmuchsix.hms.Expression.DoubleExpression;
import App.howmuchsix.hms.Expression.Expression;
import App.howmuchsix.hms.Expression.NumberExpression;
import App.howmuchsix.hms.Library.Variables;

public final class IntToDoubleBlock extends Block {

    public IntToDoubleBlock() {
        this.blockID = "int_to_double_block";
    }

    @Override
    public void Action(List<String> scopes, Variables lib) {
        try {
            Expression<Number> numberExpression = lib.getExpression("value", Types.INT.getTypeClass(), scopes, this.blockID);
            Number value = numberExpression.eval();
            double doubleValue = value.doubleValue();
            throw new ReturnException(new NumberExpression(new DoubleExpression(doubleValue)), this.getUUID());
        } catch (ReturnException e) {
            throw e;
        } catch (Exception e) {
            throw new ProgramRunException("Cannot convert int to double", this.getUUID());
        }
    }
}
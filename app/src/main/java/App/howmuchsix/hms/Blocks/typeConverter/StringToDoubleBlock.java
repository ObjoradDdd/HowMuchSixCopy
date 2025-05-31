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

public final class StringToDoubleBlock extends Block {

    public StringToDoubleBlock() {
        this.blockID = "string_to_double_block";
    }

    @Override
    public void Action(List<String> scopes, Variables lib) {
        try {

            Expression<String> stringExpression = lib.getExpression("value", Types.STRING.getTypeClass(), scopes, this.blockID);

            String stringValue = stringExpression.eval();
            double doubleValue = Double.parseDouble(stringValue.trim());

            throw new ReturnException(new NumberExpression(new DoubleExpression(doubleValue)), this.getUUID());
        } catch (ReturnException e) {
            throw e;
        } catch (NumberFormatException e) {
            throw new ProgramRunException("Cannot parse string to double", this.getUUID());
        } catch (Exception e) {
            throw new ProgramRunException("Cannot convert string to double", this.getUUID());
        }
    }
}


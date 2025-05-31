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

public final class StringToIntBlock extends Block {
    public StringToIntBlock() {
        this.blockID = "string_to_int_block";
    }

    @Override
    public void Action(List<String> scopes, Variables lib) {
        try {
            Expression<String> stringExpression = lib.getExpression("value", Types.STRING.getTypeClass(), scopes, this.blockID);

            String stringValue = stringExpression.eval();
            int intValue = Integer.parseInt(stringValue.trim());

            throw new ReturnException(new NumberExpression(new IntExpression(intValue)), this.getUUID());
        } catch (ReturnException e) {
            throw e;
        } catch (NumberFormatException e) {
            throw new ProgramRunException("Cannot parse string to integer", this.getUUID());
        } catch (Exception e) {
            throw new ProgramRunException("Cannot convert string to integer", this.getUUID());
        }
    }
}
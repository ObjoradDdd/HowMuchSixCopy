package App.howmuchsix.hms.Blocks.typeConverter;

import java.util.List;

import App.howmuchsix.hms.Blocks.Block;
import App.howmuchsix.hms.Blocks.ProgramRunException;
import App.howmuchsix.hms.Blocks.ReturnException;
import App.howmuchsix.hms.Blocks.Types;
import App.howmuchsix.hms.Expression.Expression;
import App.howmuchsix.hms.Expression.StringExpression;
import App.howmuchsix.hms.Library.Variables;

public final class BooleanToStringBlock extends Block {

    public BooleanToStringBlock() {
        this.blockID = "boolean_to_string_block";
    }

    @Override
    public void Action(List<String> scopes, Variables lib) {
        try {

            Expression<Boolean> booleanExpression = lib.getExpression("value", Types.BOOLEAN.getTypeClass(), scopes, this.blockID);

            Boolean value = booleanExpression.eval();
            String stringValue = value.toString();

            throw new ReturnException(new StringExpression(stringValue), this.getUUID());

        } catch (ReturnException e) {
            throw e;
        } catch (Exception e) {
            throw new ProgramRunException("Cannot convert boolean to string", this.getUUID());
        }
    }
}
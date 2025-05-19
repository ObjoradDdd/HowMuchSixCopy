package App.howmuchsix.hms.Blocks;

import java.util.List;

import App.howmuchsix.hms.Expression.Expression;
import App.howmuchsix.hms.Expression.NullExpression;

public class ReturnBlock extends Block{

    private final String valueString;
    private final Types returnType;
    public ReturnBlock(String valueString, Types returnType){
        this.blockID = "return_block";
        this.valueString = valueString;
        this.returnType =  returnType;
    }

    public ReturnBlock(){
        this.blockID = "return_block";
        this.valueString = "";
        this.returnType = Types.VOID;
    }

    @Override
    public void Action(List<String> scopes) throws ReturnException {
        if (returnType == Types.VOID){
            throw new ReturnException("return", new NullExpression<>(Types.VOID));
        }
        Expression<?> valueExpression = returnType.getValue(valueString, scopes);
        throw new ReturnException("return", valueExpression);
    }
}

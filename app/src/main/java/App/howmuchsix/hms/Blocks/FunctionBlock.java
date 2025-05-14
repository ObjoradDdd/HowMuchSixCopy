package App.howmuchsix.hms.Blocks;

import java.util.List;

import App.howmuchsix.hms.Expression.FunctionExpression;
import App.howmuchsix.hms.Library.Variables;

public class FunctionBlock extends Block {

    String functionName;
    List<String> functionArguments;

    public FunctionBlock(String functionName, List<String> functionArguments){
        this.blockID = "function_block";
        this.functionName = functionName;
        this.functionArguments = functionArguments;
    }

    public void Action(List<String> scopes){
        FunctionExpression<?> function = Variables.getFunction(functionName, scopes);
        function.functionReturn(functionArguments, scopes);
    }
}

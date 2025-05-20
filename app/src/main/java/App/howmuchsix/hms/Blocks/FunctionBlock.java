package App.howmuchsix.hms.Blocks;

import java.util.List;

import App.howmuchsix.hms.Expression.FunctionExpression;
import App.howmuchsix.hms.Handlers.Lexer;
import App.howmuchsix.hms.Handlers.Token;
import App.howmuchsix.hms.Library.Variables;

public final class FunctionBlock extends Block {
    String functionName;
    public FunctionBlock(String functionName){
        this.blockID = "function_block";
        this.functionName = functionName;
    }

    public void Action(List<String> scopes) {
        Token functionToken = new Lexer(functionName).tokenizeFunction();
        FunctionExpression<?> functionExpression = Variables.getFunction(functionToken.getText());
        functionExpression.functionReturn(functionToken.getArguments(), scopes);
    }
}

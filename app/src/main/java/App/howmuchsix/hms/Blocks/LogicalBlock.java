package App.howmuchsix.hms.Blocks;

import java.util.List;

import App.howmuchsix.hms.Expression.BooleanExpression;
import App.howmuchsix.hms.Expression.Expression;
import App.howmuchsix.hms.Handlers.Lexer;
import App.howmuchsix.hms.Handlers.Parser;
import App.howmuchsix.hms.Handlers.Token;
import App.howmuchsix.hms.Library.Variables;

public final class LogicalBlock extends Block{
    String input;
    public LogicalBlock(String input){
        this.input = input;
        this.blockID = "string_block";
    }

    public Expression<Boolean> eval(List<String> scopes, Variables lib){
        this.scopeNames = scopes;
        List<Token> tokens = new Lexer(this.input).tokenizeComplex();
        return new BooleanExpression(new Parser(tokens, scopeNames, lib).parseLogical().eval());
    }
}

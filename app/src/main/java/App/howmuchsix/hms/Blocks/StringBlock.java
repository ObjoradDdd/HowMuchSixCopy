package App.howmuchsix.hms.Blocks;

import java.util.List;


import App.howmuchsix.hms.Expression.Expression;
import App.howmuchsix.hms.Expression.StringExpression;
import App.howmuchsix.hms.Handlers.Lexer;
import App.howmuchsix.hms.Handlers.Parser;
import App.howmuchsix.hms.Handlers.Token;

public class StringBlock extends Block{
    String input;
    public StringBlock(String input){
        this.input = input;
        this.blockID = "string_block";
    }

    public Expression<String> eval(){
        List<Token> tokens = new Lexer(this.input).tokenize();
        return new StringExpression(new Parser(tokens).parseString().eval());
    }
}

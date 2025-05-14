package App.howmuchsix.hms.Blocks;

import java.util.List;

import App.howmuchsix.hms.Expression.Expression;
import App.howmuchsix.hms.Handlers.Lexer;
import App.howmuchsix.hms.Handlers.Parser;
import App.howmuchsix.hms.Handlers.Token;

public class PrintBlock extends Block{
    String output;
    public PrintBlock(String output) {
        this.output = output;
    }

    @Override
    public void Action(List<String> scopes){
        List<Token> tokens = new Lexer(output).tokenizeComplex();
        Expression<String> outputString = new Parser(tokens, scopes).parseToPrint();
        System.out.println(outputString.eval());
    }
}

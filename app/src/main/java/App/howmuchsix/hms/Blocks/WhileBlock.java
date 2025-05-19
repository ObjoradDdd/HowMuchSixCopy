package App.howmuchsix.hms.Blocks;

import java.util.ArrayList;
import java.util.List;

import App.howmuchsix.hms.Handlers.Lexer;
import App.howmuchsix.hms.Handlers.Parser;
import App.howmuchsix.hms.Handlers.Token;
import App.howmuchsix.hms.Library.Variables;

public class WhileBlock extends Block {
    private final List<Block> body;
    private final String logicalExpressionString;


    public WhileBlock(String logicalExpressionString, List<Block> body) {
        this.logicalExpressionString = logicalExpressionString;
        this.body = body;
    }

    @Override
    public void Action(List<String> scopes) throws ReturnException {
        String name = "Scope - " + Variables.getNumberOfScopes();
        List<String> newScopes = new ArrayList<>(scopes);
        List<Token> tokens = new Lexer(logicalExpressionString).tokenizeComplex();
        Parser logicalExpression = new Parser(tokens, newScopes);
        newScopes.add(name);
        try {
            while (logicalExpression.parseLogical().eval()) {
                Variables.newScope(name);
                for (Block block : body) {
                    block.Action(newScopes);
                }
                Variables.deleteScope(name);
            }
        } finally {
            Variables.deleteScope(name);
        }
    }
}

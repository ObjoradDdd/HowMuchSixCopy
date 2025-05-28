package App.howmuchsix.hms.Blocks;

import java.util.ArrayList;
import java.util.List;

import App.howmuchsix.hms.Handlers.Lexer;
import App.howmuchsix.hms.Handlers.Parser;
import App.howmuchsix.hms.Handlers.Token;
import App.howmuchsix.hms.Library.Variables;

public final class WhileBlock extends Block {
    private final List<Block> body;
    private final String logicalExpressionString;


    public WhileBlock(String logicalExpressionString, List<Block> body) {
        this.logicalExpressionString = logicalExpressionString;
        this.body = body;
    }

    @Override
    public void Action(List<String> scopes, Variables lib) throws ReturnException {
        String name = "Scope - " + lib.getNumberOfScopes();
        List<String> newScopes = new ArrayList<>(scopes);
        List<Token> tokens = new Lexer(logicalExpressionString).tokenize();
        Parser logicalExpression = new Parser(tokens, newScopes, lib);
        newScopes.add(name);
        try {
            while (logicalExpression.parseLogical().eval()) {
                lib.newScope(name);
                for (Block block : body) {
                    block.Action(newScopes, lib);
                }
                lib.deleteScope(name);
            }
        } catch (BreakException ignored) {
        } finally {
            lib.deleteScope(name);
        }
    }
}

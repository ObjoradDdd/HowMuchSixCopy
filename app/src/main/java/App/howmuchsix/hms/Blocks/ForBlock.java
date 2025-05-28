package App.howmuchsix.hms.Blocks;

import java.util.ArrayList;
import java.util.List;

import App.howmuchsix.hms.Handlers.Lexer;
import App.howmuchsix.hms.Handlers.Parser;
import App.howmuchsix.hms.Handlers.Token;
import App.howmuchsix.hms.Library.Variables;

public final class ForBlock extends Block {
    private final List<Block> body;
    private final Block iterator;
    private final String logicalExpressionString;
    private final Block action;

    public ForBlock(AssignmentBlock iterator, String logicalExpressionString, AssignmentBlock action, List<Block> body) {
        this.iterator = iterator;
        this.logicalExpressionString = logicalExpressionString;
        this.action = action;
        this.body = body;
    }

    public ForBlock(DeclarationBlock iterator, String logicalExpressionString, AssignmentBlock action, List<Block> body) {
        this.iterator = iterator;
        this.logicalExpressionString = logicalExpressionString;
        this.action = action;
        this.body = body;
    }

    @Override
    public void Action(List<String> scopes, Variables lib) throws ReturnException {
        String name = "Scope - " + lib.getNumberOfScopes();
        String iteratorScope = "Scope - " + (lib.getNumberOfScopes() + 1);
        List<String> newScopes = new ArrayList<>(scopes);
        List<Token> tokens = new Lexer(logicalExpressionString).tokenize();
        Parser logicalExpression = new Parser(tokens, newScopes, lib);
        lib.newScope(iteratorScope);
        try {
            iterator.Action(newScopes, lib);
            newScopes.add(name);
            while (logicalExpression.parseLogical().eval()) {
                lib.newScope(name);
                for (Block block : body) {
                    try {
                        block.Action(newScopes, lib);
                    }
                    catch (ContinueException ignored){
                        break;
                    }

                }
                action.Action(newScopes, lib);
                lib.deleteScope(name);
            }
        } catch (BreakException ignored) {
        } finally {
            lib.deleteScope(name);
        }
    }

}

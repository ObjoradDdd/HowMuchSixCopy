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
    public void Action(List<String> scopes) throws ReturnException {
        String name = "Scope - " + Variables.getNumberOfScopes();
        String iteratorScope = "Scope - " + Variables.getNumberOfScopes();
        List<String> newScopes = new ArrayList<>(scopes);
        List<Token> tokens = new Lexer(logicalExpressionString).tokenizeComplex();
        Parser logicalExpression = new Parser(tokens, newScopes);
        Variables.newScope(iteratorScope);
        try {
            iterator.Action(newScopes);
            newScopes.add(name);
            while (logicalExpression.parseLogical().eval()) {
                Variables.newScope(name);
                for (Block block : body) {
                    block.Action(newScopes);
                }
                action.Action(newScopes);
                Variables.deleteScope(name);
            }
        } catch (BreakException ignored) {
        } finally {
            Variables.deleteScope(name);
        }
    }

}

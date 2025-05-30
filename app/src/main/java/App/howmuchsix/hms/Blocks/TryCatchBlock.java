package App.howmuchsix.hms.Blocks;

import java.util.ArrayList;
import java.util.List;

import App.howmuchsix.hms.Library.Variables;

public final class TryCatchBlock extends Block {
    private final List<Block> tryBody;
    private final List<Block> catchBody;

    public TryCatchBlock(List<Block> tryBody, List<Block> catchBody) {
        this.blockID = "try_catch_block";
        this.tryBody = tryBody;
        this.catchBody = catchBody;
    }

    @Override
    public void Action(List<String> scopes, Variables lib) throws ReturnException {
        String tryScope = "Scope - " + lib.getNumberOfScopes() + " try";
        String catchScope = "Scope - " + (lib.getNumberOfScopes() + 1) + " catch";
        List<String> newTryScopes = new ArrayList<>(scopes);
        List<String> newCatchScopes = new ArrayList<>(scopes);
        newTryScopes.add(tryScope);
        newCatchScopes.add(catchScope);

        try {
            lib.newScope(tryScope);
            for (Block block : tryBody) {
                block.Action(newTryScopes, lib);
            }
        } catch (ProgramRunException e) {
            try {
                lib.newScope(catchScope);
                for (Block block : catchBody) {
                    block.Action(newCatchScopes, lib);
                }
            } finally {
                lib.deleteScope(catchScope);
            }
        } finally {
            lib.deleteScope(tryScope);
        }
    }
}
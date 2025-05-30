package App.howmuchsix.hms.Blocks;

import java.util.ArrayList;
import java.util.List;

import App.howmuchsix.hms.Library.Variables;

public final class IfBlock extends Block {
    private final String trueCondition;
    private final List<Block> trueAction;
    private List<String> elifConditions = null;
    private List<List<Block>> elifActions = null;
    private List<Block> falseAction = null;

    public IfBlock(String trueCondition, List<Block> trueAction) {
        this.trueCondition = trueCondition;
        this.trueAction = trueAction;
    }

    public IfBlock(String trueCondition, List<Block> trueAction,
                   List<String> elifConditions, List<List<Block>> elifActions,
                   List<Block> falseAction) {
        this.trueCondition = trueCondition;
        this.trueAction = trueAction;
        this.elifConditions = elifConditions;
        this.elifActions = elifActions;
        this.falseAction = falseAction;
    }

    public IfBlock(String trueCondition, List<Block> trueAction,
                   List<Block> falseAction) {
        this.trueCondition = trueCondition;
        this.trueAction = trueAction;
        this.falseAction = falseAction;
    }

    @Override
    public void Action(List<String> scopeNames, Variables lib) throws ReturnException {
        String name = "Scope - " + lib.getNumberOfScopes();
        List<String> newScopes = new ArrayList<>(scopeNames);
        newScopes.add(name);
        lib.newScope(name);
        try {
            if (elifActions == null && falseAction == null) {
                if ((boolean) (Types.BOOLEAN.getValue(trueCondition, scopeNames, lib, this.getUUID()).eval())) {
                    for (Block block : trueAction) {
                        block.Action(newScopes, lib);
                    }
                }
            } else if (elifActions != null && falseAction == null) {
                if ((boolean) (Types.BOOLEAN.getValue(trueCondition, scopeNames, lib, this.getUUID()).eval())) {
                    for (Block block : trueAction) {
                        block.Action(newScopes, lib);
                    }
                    return;
                }
                for (int i = 0; i < elifActions.size(); i++) {
                    if ((boolean) (Types.BOOLEAN.getValue(elifConditions.get(i), scopeNames, lib, this.getUUID()).eval())) {
                        for (Block block : elifActions.get(i)) {
                            block.Action(newScopes, lib);
                        }
                        return;
                    }
                }

            } else if (elifActions != null) {

                if ((boolean) (Types.BOOLEAN.getValue(trueCondition, scopeNames, lib, this.getUUID()).eval())) {
                    for (Block block : trueAction) {
                        block.Action(newScopes, lib);
                    }
                    return;
                }

                for (int i = 0; i < elifActions.size(); i++) {
                    if ((boolean) (Types.BOOLEAN.getValue(elifConditions.get(i), scopeNames, lib, this.getUUID()).eval())) {
                        for (Block block : elifActions.get(i)) {
                            block.Action(newScopes, lib);
                        }
                        return;
                    }
                }

                for (Block block : falseAction) {
                    block.Action(newScopes, lib);
                }

            } else {
                if ((boolean) (Types.BOOLEAN.getValue(trueCondition, scopeNames, lib, this.getUUID()).eval())) {
                    for (Block block : trueAction) {
                        block.Action(newScopes, lib);
                    }
                } else {
                    for (Block block : falseAction) {
                        block.Action(newScopes, lib);
                    }
                }
            }
        } finally {
            lib.deleteScope(name);
        }
    }
}

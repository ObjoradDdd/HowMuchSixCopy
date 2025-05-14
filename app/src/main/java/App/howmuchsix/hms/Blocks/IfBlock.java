package App.howmuchsix.hms.Blocks;

import java.util.ArrayList;
import java.util.List;

import App.howmuchsix.hms.Library.Variables;

public class IfBlock extends Block {
    private String trueCondition;
    private List<Block> trueAction;
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
    public void Action(List<String> scopeNames) {
        String name = "Scope - " + Variables.getNumberOfScopes();
        List<String> newScopes = new ArrayList<>(scopeNames);
        newScopes.add(name);
        Variables.newScope(name);
        if (elifActions == null && falseAction == null) {
            if (!(new LogicalBlock(trueCondition).eval(scopeNames).eval())) {
                for (Block block : trueAction) {
                    block.Action(newScopes);
                }
            }
        } else if (elifActions != null && falseAction == null) {
            if (!(new LogicalBlock(trueCondition).eval(scopeNames).eval())) {
                for (Block block : trueAction) {
                    block.Action(newScopes);
                }
                return;
            }
            for (int i = 0; i < elifActions.size(); i++) {
                if (!(new LogicalBlock(elifConditions.get(i)).eval(scopeNames).eval())) {
                    for (Block block : elifActions.get(i)) {
                        block.Action(newScopes);
                    }
                    return;
                }
            }

        } else if (elifActions != null) {
            if (!(new LogicalBlock(trueCondition).eval(scopeNames).eval())) {
                for (Block block : trueAction) {
                    block.Action(newScopes);
                }
                return;
            }

            for (int i = 0; i < elifActions.size(); i++) {
                if (!(new LogicalBlock(elifConditions.get(i)).eval(scopeNames).eval())) {
                    for (Block block : elifActions.get(i)) {
                        block.Action(newScopes);
                    }
                    return;
                }
            }

            for (Block block : falseAction) {
                block.Action(newScopes);
            }

        } else {
            for (Block block : falseAction) {
                block.Action(newScopes);
            }
        }
        Variables.deleteScope(name);
    }


}

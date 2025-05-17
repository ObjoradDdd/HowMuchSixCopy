package App.howmuchsix.hms.Expression;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import App.howmuchsix.hms.Blocks.Block;
import App.howmuchsix.hms.Blocks.ReturnBlock;
import App.howmuchsix.hms.Blocks.Types;
import App.howmuchsix.hms.Library.Variables;

public class FunctionExpression<T> implements Expression<T> {

    final List<Types> argumentsTypes;
    final List<String> argumentNames;
    final String name;
    private final List<Block> body;
    public List<String> scopes = List.of("MainScope");
    private final Types returnType;

    public FunctionExpression(Types returnType, String name, List<Types> argumentsTypes, List<String> argumentsNames, List<Block> body) {
        this.name = name;
        this.body = body;
        this.argumentsTypes = argumentsTypes;
        this.argumentNames = argumentsNames;
        this.returnType = returnType;
    }

    @SuppressWarnings("unchecked")
    public T functionReturn(List<String> argumentsValuesStrings, List<String> newScopes) {

        this.scopes = new ArrayList<>(newScopes);
        Variables.newScope(name);
        this.scopes.add(name);

        if (argumentsValuesStrings.size() != argumentsTypes.size()) {
            throw new RuntimeException("Function " + name + " has " + argumentsTypes.size() + " arguments but " + argumentsValuesStrings.size() + " given");
        }

        for (int i = 0; i < argumentsTypes.size(); i++) {
            Variables.set(argumentNames.get(i), argumentsTypes.get(i).getValue(argumentsValuesStrings.get(i), scopes), name);
        }
        try {
            if (this.returnType != Types.VOID) {
                for (Block block : this.body) {
                    if (Objects.equals(block.blockID, "return_block")) {
                        ReturnBlock returnBlock = (ReturnBlock) block;
                        String returnValue = returnBlock.eval();
                        return (T) returnType.getValue(returnValue, List.of(name)).eval();
                    } else {
                        block.Action(List.of(name));
                    }
                }
                throw new RuntimeException("No return in " + returnType + " function");
            } else {
                for (Block block : this.body) {
                    block.Action(List.of(name));
                }
            }
        } finally {
            Variables.deleteScope(name);
        }
        return (T) (Void) null;
    }

    public Types getReturnType() {
        return returnType;
    }

    @Override
    public T eval() {
        return null;
    }

    @Override
    public Types getType() {
        return Types.FUNCTION;
    }
}
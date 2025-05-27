package App.howmuchsix.hms.Expression;

import java.util.ArrayList;
import java.util.List;

import App.howmuchsix.hms.Blocks.Block;
import App.howmuchsix.hms.Blocks.ReturnException;
import App.howmuchsix.hms.Blocks.Types;
import App.howmuchsix.hms.Library.Variables;

public final class FunctionExpression<T> implements Expression<T> {

    final List<Types> argumentsTypes;
    final List<String> argumentNames;
    final String name;
    private final List<Block> body;
    public List<String> scopes = List.of("MainScope");
    private final Types returnType;
    private Variables lib;

    public FunctionExpression(Types returnType, String name, List<Types> argumentsTypes, List<String> argumentsNames, List<Block> body, Variables lib) {
        this.lib = lib;
        this.name = name + " " + this.lib.getNumberOfScopes();
        this.body = body;
        this.argumentsTypes = argumentsTypes;
        this.argumentNames = argumentsNames;
        this.returnType = returnType;
    }

    public Expression<T> functionReturn(List<String> argumentsValuesStrings, List<String> newScopes) {


        this.scopes = new ArrayList<>(newScopes);
        lib.newScope(name);
        this.scopes.add(name);
        List<String> nameScope = List.of(name);

        if (argumentsValuesStrings.size() != argumentsTypes.size()) {
            throw new RuntimeException("Function " + name + " has " + argumentsTypes.size() + " arguments but " + argumentsValuesStrings.size() + " given");
        }

        for (int i = 0; i < argumentsTypes.size(); i++) {
            lib.set(argumentNames.get(i), argumentsTypes.get(i).getValue(argumentsValuesStrings.get(i), scopes, lib), name);
        }

        try {
            for (Block block : this.body) {
                block.Action(nameScope, lib);
            }

            if (returnType != Types.VOID){
                throw new RuntimeException("No return in " + returnType + " function");
            }
        } catch (ReturnException exception) {
            if (returnType == Types.VOID){
                return new NullExpression<>(returnType);
            }

            Expression<?> returnExpression = exception.getValue();
            if (returnExpression.getType() == returnType) {
                return (Expression<T>) returnExpression;
            } else {
                throw new RuntimeException("Invalid return expression type. " + returnType + " was expected");
            }
        }finally {
            lib.deleteScope(name);
        }
        return null;
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
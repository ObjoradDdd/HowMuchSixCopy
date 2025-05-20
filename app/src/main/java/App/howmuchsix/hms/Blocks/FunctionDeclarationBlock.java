package App.howmuchsix.hms.Blocks;

import java.util.List;

import App.howmuchsix.hms.Expression.FunctionExpression;
import App.howmuchsix.hms.Library.Variables;

public final class FunctionDeclarationBlock extends Block{
    final List<Types> argumentsTypes;
    final List<String> argumentNames;
    final String name;
    final List<Block> body;
    final Types returnType;


    public FunctionDeclarationBlock(Types returnType, String name, List<Types> argumentsTypes, List<String> argumentsNames, List<Block> body) {
        this.blockID = "function_declaration_block";
        this.name = name;
        this.body = body;
        this.argumentsTypes = argumentsTypes;
        this.argumentNames = argumentsNames;
        this.returnType = returnType;
    }

    @Override
    public void Action(List<String> scopes) {
        if (Variables.isExistsFunction(name)) {
            Variables.set(name, new FunctionExpression<>(returnType, name, argumentsTypes, argumentNames, body), "MainScope");

        }
    }
}

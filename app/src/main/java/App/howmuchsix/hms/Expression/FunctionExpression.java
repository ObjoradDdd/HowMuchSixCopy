package App.howmuchsix.hms.Expression;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import App.howmuchsix.hms.Blocks.ArithmeticBlock;
import App.howmuchsix.hms.Blocks.Block;
import App.howmuchsix.hms.Blocks.LogicalBlock;
import App.howmuchsix.hms.Blocks.ReturnBlock;
import App.howmuchsix.hms.Blocks.StringBlock;
import App.howmuchsix.hms.Blocks.Types;
import App.howmuchsix.hms.Library.Variables;

public class FunctionExpression<T> implements Expression<T> {

    final List<Types> argumentsTypes;
    final List<String> argumentNames;
    final String name;
    private final List<Block> body;
    public List<String> scopes  = List.of("MainScope");
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
            switch (argumentsTypes.get(i)) {
                case INT -> Variables.set(argumentNames.get(i), new ArithmeticBlock(argumentsValuesStrings.get(i)).eval(scopes), name);
                case DOUBLE -> Variables.set(argumentNames.get(i), new ArithmeticBlock(argumentsValuesStrings.get(i), true).eval(scopes), name);
                case STRING -> Variables.set(argumentNames.get(i), new StringBlock(argumentsValuesStrings.get(i)).eval(scopes), name);
                case BOOLEAN -> Variables.set(argumentNames.get(i), new LogicalBlock(argumentsValuesStrings.get(i)).eval(scopes), name);
            }
        }

        try {
            for (Block block : this.body) {
                if (Objects.equals(block.blockID, "return_block")) {
                    ReturnBlock returnBlock = (ReturnBlock) block;
                    String returnValue = returnBlock.eval();

                    return switch (returnType) {
                        case INT -> {
                            ArithmeticBlock intBlock = new ArithmeticBlock(returnValue);
                            Expression<?> result = intBlock.eval(List.of(name));
                            if (!(result.eval() instanceof Integer)) {
                                throw new RuntimeException("Expected INT return type but got " + result.getType());
                            }
                            yield (T) result.eval();
                        }
                        case DOUBLE -> {
                            ArithmeticBlock doubleBlock = new ArithmeticBlock(returnValue, true);
                            Expression<?> result = doubleBlock.eval(List.of(name));
                            if (!(result.eval() instanceof Double)) {
                                throw new RuntimeException("Expected DOUBLE return type but got " + result.getType());
                            }
                            yield (T) result.eval();
                        }
                        case STRING -> {
                            StringBlock stringBlock = new StringBlock(returnValue);
                            Expression<?> result = stringBlock.eval(List.of(name));
                            if (!(result.eval() instanceof String)) {
                                throw new RuntimeException("Expected STRING return type but got " + result.getType());
                            }
                            yield (T) result.eval();
                        }
                        case BOOLEAN -> {
                            LogicalBlock booleanBlock = new LogicalBlock(returnValue);
                            Expression<?> result = booleanBlock.eval(List.of(name));
                            if (!(result.eval() instanceof Boolean)) {
                                throw new RuntimeException("Expected BOOLEAN return type but got " + result.getType());
                            }
                            yield (T) result.eval();
                        }
                        default -> throw new RuntimeException("Unsupported return type: " + returnType);
                    };
                } else {
                    block.Action(List.of(name));
                }
            }
        } finally {
            Variables.deleteScope(name);
        }

        return null;
    }

    public Types getReturnType(){
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
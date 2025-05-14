package App.howmuchsix.hms.Blocks;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import App.howmuchsix.hms.Expression.Expression;
import App.howmuchsix.hms.Expression.NullExpression;
import App.howmuchsix.hms.Library.Variables;

public class DeclarationBlock extends Block {

    List<String> variables;
    Types type;
    List<Expression<?>> results = new ArrayList<>();
    List<String> values;

    public DeclarationBlock(List<String> variables, List<String> values, Types type) {
        this.blockID = "declaration_block";
        this.variables = variables;
        this.type = type;
        this.values = values;
    }

    public void Action(List<String> scopes) {
        this.scopeNames = scopes;
        switch (type) {
            case INT -> {
                for (String inputString : values) {
                    Expression<?> expressionResult = new ArithmeticBlock(inputString).eval(scopeNames);
                    Object result = expressionResult.eval();
                    if (result instanceof Integer || result == null) {
                        this.results.add(expressionResult);
                    } else {
                        throw new RuntimeException("Invalid type");
                    }
                }
            }
            case DOUBLE -> {
                for (String inputString : values) {
                    Expression<?> expressionResult = new ArithmeticBlock(inputString, true).eval(scopeNames);
                    Object result = expressionResult.eval();
                    if (result instanceof Double || result == null) {
                        this.results.add(expressionResult);
                    } else {
                        throw new RuntimeException("Invalid type");
                    }
                }
            }
            case STRING -> {
                for (String inputString : values) {
                    Expression<?> expressionResult = new StringBlock(inputString).eval(scopeNames);
                    Object result = expressionResult.eval();
                    if (result instanceof String || result == null) {
                        this.results.add(expressionResult);
                    } else {
                        throw new RuntimeException("Invalid type");
                    }
                }
            }
            case BOOLEAN -> {
                for (String inputString : values) {
                    Expression<?> expressionResult = new LogicalBlock(inputString).eval(scopeNames);
                    Object result = expressionResult.eval();
                    if (result instanceof Boolean || result == null) {
                        this.results.add(expressionResult);
                    } else {
                        throw new RuntimeException("Invalid type");
                    }
                }
            }
        }

        for (int i = 0; i < this.variables.size(); i++) {
            if (!Variables.isExistsVariable(this.variables.get(i))){
                throw new RuntimeException(this.variables.get(i) + " is already declared");
            }
            if (Objects.equals(this.variables.get(i), "true") || Objects.equals(this.variables.get(i), "false")){
                throw new RuntimeException("Variable cannot be named true or false");
            }
            if (i < this.results.size()) {
                Variables.set(this.variables.get(i), this.results.get(i), scopeNames.get(scopeNames.size() - 1));
            } else {
                Variables.set(this.variables.get(i), new NullExpression<>(), scopeNames.get(scopeNames.size() - 1));
            }
        }
    }
}

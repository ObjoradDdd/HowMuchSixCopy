package App.howmuchsix.hms.Blocks;

import java.util.List;

import App.howmuchsix.hms.Expression.Expression;
import App.howmuchsix.hms.Library.Variables;

public class AssignmentBlock extends Block {
    String variable;
    String stringValue;


    public AssignmentBlock(String variable, String value) {
        this.blockID = "assignment_block";
        this.variable = variable;
        this.stringValue = value;
    }
    public void Action(List<String> scopes) {
        this.scopeNames = scopes;
        Expression<?> variableExpression = Variables.getExpressionWithNoType(this.variable, scopeNames);
        Types variableType = variableExpression.getType();

        switch (variableType) {
            case INT -> {
                Expression<?> expressionResult = new ArithmeticBlock(stringValue).eval(scopeNames);
                Object result = expressionResult.eval();
                if (result instanceof Integer || result == null) {
                    Variables.set(variable, expressionResult, scopeNames);
                } else {
                    throw new RuntimeException("Invalid type");
                }
            }
            case DOUBLE -> {
                Expression<?> expressionResult = new ArithmeticBlock(stringValue, true).eval(scopeNames);
                Object result = expressionResult.eval();
                if (result instanceof Double || result == null) {
                    Variables.set(variable, expressionResult, scopeNames);
                } else {
                    throw new RuntimeException("Invalid type");
                }
            }
            case STRING -> {
                Expression<?> expressionResult = new StringBlock(stringValue).eval(scopeNames);
                Object result = expressionResult.eval();
                if (result instanceof String || result == null) {
                    Variables.set(variable, expressionResult, scopeNames);
                } else {
                    throw new RuntimeException("Invalid type");
                }
            }
            case BOOLEAN -> {
                Expression<?> expressionResult = new LogicalBlock(stringValue).eval(scopeNames);
                Object result = expressionResult.eval();
                if (result instanceof Boolean || result == null) {
                    Variables.set(variable, expressionResult, scopeNames);
                } else {
                    throw new RuntimeException("Invalid type");
                }
            }
            case NUll -> {
            }
        }
    }
}

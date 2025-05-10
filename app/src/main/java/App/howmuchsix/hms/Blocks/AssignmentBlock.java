package App.howmuchsix.hms.Blocks;

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

    public void Action() {
        Expression<?> variableExpression = Variables.getExpressionWithNoType(this.variable);
        Types variableType = variableExpression.getType();

        switch (variableType) {
            case INT -> {
                Object result = new ArithmeticBlock(stringValue).eval().eval();
                if (result instanceof Integer) {
                    Variables.set(variable, new ArithmeticBlock(stringValue).eval());
                } else {
                    throw new RuntimeException("Invalid type");
                }
            }
            case DOUBLE -> {
                Variables.set(variable, new ArithmeticBlock(stringValue, true).eval());
            }
            case STRING -> {
                Variables.set(variable, new StringBlock(stringValue).eval());
            }
            case BOOLEAN -> {
            }
            case NUll -> {
            }
        }
    }
}

package App.howmuchsix.hms.Blocks;

import java.util.List;

import App.howmuchsix.hms.Expression.ArrayExpression;
import App.howmuchsix.hms.Expression.Expression;
import App.howmuchsix.hms.Handlers.Lexer;
import App.howmuchsix.hms.Handlers.Token;
import App.howmuchsix.hms.Library.Variables;

public final class AssignmentBlock extends Block {
    String variable;
    String stringValue;

    public AssignmentBlock(String variable, String value) {
        this.blockID = "assignment_block";
        this.variable = variable;
        this.stringValue = value;
    }

    @Override
    public void Action(List<String> scopes, Variables lib) {
        Token isSubscriptable = new Lexer(variable).tokenizeForAssignment();
        if (isSubscriptable.getBody() == null) {
            Expression<?> variableExpression = lib.getExpressionWithNoType(variable, scopes);
            Types variableType = variableExpression.getType();
            if (variableType == Types.ARRAY) {
                ArrayExpression newValue = (ArrayExpression) variableType.getValue(stringValue, scopes, lib);
                ArrayExpression currentValue = (ArrayExpression) variableExpression;
                if (newValue.getInsideType() == currentValue.getInsideType()) {
                    lib.set(variable, newValue, scopes);
                    return;
                } else {
                    throw new RuntimeException(variable + " contains " + currentValue.getInsideType() + " not " + newValue.getInsideType());
                }
            }
            lib.set(variable, variableType.getValue(stringValue, scopes, lib), scopes);
        } else {
            Expression<?> variableExpression = lib.getExpressionWithNoType(isSubscriptable.getText(), scopes);
            Types variableType = variableExpression.getType();
            Types insideType;
            if (variableType == Types.ARRAY) {
                insideType = ((ArrayExpression) variableExpression).getInsideType();
            } else {
                throw new RuntimeException(variable + " is not subscriptable");
            }
            lib.setValueIntoArray(isSubscriptable.getText(), isSubscriptable.getBody(), insideType.getValue(stringValue, scopes, lib), scopes);
        }
    }
}

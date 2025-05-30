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
            Expression<?> variableExpression = lib.getExpressionWithNoType(variable, scopes, this.getUUID());
            Types variableType = variableExpression.getType();
            if (variableType == Types.ARRAY) {
                ArrayExpression newValue = (ArrayExpression) variableType.getValue(stringValue, scopes, lib, this.getUUID());
                ArrayExpression currentValue = (ArrayExpression) variableExpression;
                if (newValue.getInsideType() == currentValue.getInsideType()) {
                    lib.set(variable, newValue, scopes);
                    return;
                } else {
                    throw new ProgramRunException(variable + " contains " + currentValue.getInsideType() + " not " + newValue.getInsideType(), this.getUUID());
                }
            }
            lib.set(variable, variableType.getValue(stringValue, scopes, lib, this.getUUID()), scopes);
        } else {
            Expression<?> variableExpression = lib.getExpressionWithNoType(isSubscriptable.getText(), scopes, this.getUUID());
            Types variableType = variableExpression.getType();
            Types insideType;
            if (variableType == Types.ARRAY) {
                insideType = ((ArrayExpression) variableExpression).getInsideType();
            } else {
                throw new ProgramRunException(variable + " is not subscriptable", this.getUUID());
            }
            lib.setValueIntoArray(isSubscriptable.getText(), isSubscriptable.getBody(), insideType.getValue(stringValue, scopes, lib, this.getUUID()), scopes, this.getUUID());
        }
    }
}

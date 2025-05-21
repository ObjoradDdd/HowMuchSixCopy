package App.howmuchsix.hms.Blocks;

import java.util.List;
import java.util.Objects;

import App.howmuchsix.hms.Expression.NullExpression;
import App.howmuchsix.hms.Library.Variables;

public final class DeclarationBlock extends Block {

    List<String> variables;
    Types type;
    List<String> values;

    public DeclarationBlock(List<String> variables, List<String> values, Types type) {
        this.blockID = "declaration_block";
        this.variables = variables;
        this.type = type;
        this.values = values;
    }

    /*public DeclarationBlock(String variables, String values, Types type) {
        this.blockID = "declaration_block";
        this.variables = List.of(variables);
        this.type = type;
        this.values = List.of(values);
    }*/

    public void Action(List<String> scopes) {
        for (int i = 0; i < this.variables.size(); i++) {
            if (Variables.isExistsVariable(this.variables.get(i))){
                throw new RuntimeException(this.variables.get(i) + " is already declared");
            }
            if (Objects.equals(this.variables.get(i), "true") || Objects.equals(this.variables.get(i), "false")){
                throw new RuntimeException("Variable cannot be named true or false");
            }
            if (i < this.values.size()) {
                Variables.set(this.variables.get(i), type.getValue(values.get(i), scopes), scopes.get(scopes.size() - 1));
            } else {
                Variables.set(this.variables.get(i), new NullExpression<>(type), scopes.get(scopes.size() - 1));
            }
        }
    }
}

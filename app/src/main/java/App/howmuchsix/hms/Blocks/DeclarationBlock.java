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


    public void Action(List<String> scopes, Variables lib) {

        for (int i = 0; i < this.variables.size(); i++) {
            if (lib.isExistsVariable(this.variables.get(i))) {
                throw new ProgramRunException(this.variables.get(i) + " is already declared", this.getUUID());
            }
            if (Objects.equals(this.variables.get(i), "true") || Objects.equals(this.variables.get(i), "false")) {
                throw new ProgramRunException("Variable cannot be named true or false", this.getUUID());
            }
            if (i < this.values.size()) {
                lib.set(this.variables.get(i), type.getValue(values.get(i), scopes, lib, this.getUUID()), scopes.get(scopes.size() - 1));
            } else {
                lib.set(this.variables.get(i), new NullExpression<>(type), scopes.get(scopes.size() - 1));
            }
        }
    }
}

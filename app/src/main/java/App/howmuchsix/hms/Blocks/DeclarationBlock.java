package App.howmuchsix.hms.Blocks;

import java.util.ArrayList;
import java.util.List;

import App.howmuchsix.hms.Expression.Expression;
import App.howmuchsix.hms.Expression.NullExpression;
import App.howmuchsix.hms.Library.Variables;

public class DeclarationBlock extends Block {

    List<String> variables;
    Types type;
    List<Expression<?>> results = new ArrayList<>();
    ArrayList<String> values;

    public DeclarationBlock(List<String> variables, ArrayList<String> values, Types type) {
        this.blockID = "declaration_block";
        this.variables = variables;
        this.type = type;
        this.values = values;
    }

    public void Action() {
        switch (type) {
            case INT -> {
                for (String inputString : values) {
                    Object result = new ArithmeticBlock(inputString).eval().eval();
                    if (result instanceof Integer) {
                        this.results.add(new ArithmeticBlock(inputString).eval());
                    } else {
                        throw new RuntimeException("Invalid type");
                    }

                }
            }
            case DOUBLE -> {
                for (String inputString : values) {
                    this.results.add(new ArithmeticBlock(inputString, true).eval());
                }
            }
            case STRING -> {
                for (String inputString : values) {
                    this.results.add(new StringBlock(inputString).eval());
                }
            }
        }

        for (int i = 0; i < this.variables.size(); i++) {
            if (!Variables.isExistsVariable(this.variables.get(i))){
                throw new RuntimeException(this.variables.get(i) + " is already declared");
            }
            if (i < this.results.size()) {
                System.out.println("declared");
                Variables.set(this.variables.get(i), this.results.get(i));
            } else {
                Variables.set(this.variables.get(i), new NullExpression<>());
            }
        }
    }
}

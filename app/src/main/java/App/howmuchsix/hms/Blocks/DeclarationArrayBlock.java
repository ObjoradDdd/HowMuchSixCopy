package App.howmuchsix.hms.Blocks;

import java.util.ArrayList;
import java.util.List;

import App.howmuchsix.hms.Expression.ArrayExpression;
import App.howmuchsix.hms.Expression.Expression;
import App.howmuchsix.hms.Library.Variables;

public final class DeclarationArrayBlock extends Block {
    private final String name;
    private final int length;
    Types type;
    List<String> values = null;

    public DeclarationArrayBlock(Types type, String name, int length) {
        this.type = type;
        this.name = name;
        this.length = length;
    }

    public DeclarationArrayBlock(Types type, String name, List<String> values) {
        this.type = type;
        this.name = name;
        this.length = values.size();
        this.values = values;
    }

    public DeclarationArrayBlock(Types type, String name, int length, List<String> values) {
        this.type = type;
        this.name = name;
        this.length = length;
        this.values = values;
    }


    @Override
    public void Action(List<String> scopes, Variables lib) {
        if (values == null) {
            lib.set(name, new ArrayExpression(type, length), scopes.get(scopes.size() - 1));
        } else if (values.size() != length) {
            List<Expression<?>> valueExpressions = new ArrayList<>();
            for (String input : values) {
                valueExpressions.add(type.getValue(input, scopes, lib, this.getUUID()));
            }
            lib.set(name, new ArrayExpression(type, length, valueExpressions), scopes.get(scopes.size() - 1));
        } else {
            List<Expression<?>> valueExpressions = new ArrayList<>();
            for (String input : values) {
                valueExpressions.add(type.getValue(input, scopes, lib,this.getUUID()));
            }
            lib.set(name, new ArrayExpression(type, valueExpressions), scopes.get(scopes.size() - 1));
        }
    }
}

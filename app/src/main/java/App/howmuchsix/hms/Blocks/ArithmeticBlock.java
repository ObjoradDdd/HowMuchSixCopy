package App.howmuchsix.hms.Blocks;

import java.util.List;

import App.howmuchsix.hms.Expression.DoubleExpression;
import App.howmuchsix.hms.Expression.Expression;
import App.howmuchsix.hms.Expression.IntExpression;
import App.howmuchsix.hms.Expression.NumberExpression;
import App.howmuchsix.hms.Handlers.Lexer;
import App.howmuchsix.hms.Handlers.Parser;
import App.howmuchsix.hms.Handlers.Token;
import App.howmuchsix.hms.Library.Variables;

public final class ArithmeticBlock extends Block{
    String input;
    Boolean isDouble = false;

    public ArithmeticBlock(String input){
        this.blockID = "arithmetic_block";
        this.input = input;
    }
    public ArithmeticBlock(String input, Boolean isDouble){
        this.blockID = "arithmetic_block";
        this.input = input;
        this.isDouble = isDouble;
    }


    @Override
    public Expression<Number> eval(List<String> scopes, Variables lib){
        this.scopeNames = scopes;
        List<Token> tokens = new Lexer(this.input).tokenize();
        Expression<Number> expression = new Parser(tokens, this.scopeNames, lib).parseArithmetic();
        Expression<Number> value;
        if (expression.eval() instanceof Double || isDouble){
            value = new NumberExpression(new DoubleExpression(expression.eval().doubleValue()));
        }
        else {
            value = new NumberExpression(new IntExpression((int) expression.eval()));
        }
        return value;
    }
}

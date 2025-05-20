package App.howmuchsix.hms.Expression;

import java.util.Arrays;
import java.util.List;
import App.howmuchsix.hms.Blocks.Types;

public final class ArrayExpression implements Expression<Expression<?>[]> {
    Expression<?>[] array;
    private final int length;
    Types type = Types.ARRAY;
    Types insideType;

    public ArrayExpression(Types type, int length){
        this.insideType = type;
        this.length = length;
        this.array = new Expression<?>[length];
        Arrays.fill(array, new NullExpression<>(insideType));
    }

    public ArrayExpression(Types type, List<Expression<?>> values){
        this.insideType = type;
        this.length = values.size();
        this.array = new Expression<?>[this.length];
        for(int i = 0; i < values.size(); i++){
            this.array[i] = values.get(i);
        }
    }

    public ArrayExpression(Types type, int length, List<Expression<?>> values){
        this.insideType = type;
        this.length = length;
        this.array = new Expression<?>[length];
        for(int i = 0; i < values.size(); i++){
            this.array[i] = values.get(i);
        }
        for (int i = values.size(); i < length; i++){
            this.array[i] = new NullExpression<>(type);
        }
    }

    @Override
    public Expression<?>[] eval() {
        return array;
    }
    @Override
    public Types getType() {
        return type;
    }

    public Types getInsideType() {
        return insideType;
    }

    public int getLength() {
        return length;
    }

    public void set(int index, Expression<?> value){
        this.array[index] = value;
    }
}

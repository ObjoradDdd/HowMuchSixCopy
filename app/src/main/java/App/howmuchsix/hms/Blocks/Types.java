package App.howmuchsix.hms.Blocks;

import java.util.Arrays;
import java.util.List;

import App.howmuchsix.hms.Expression.ArrayExpression;
import App.howmuchsix.hms.Expression.Expression;
import App.howmuchsix.hms.Handlers.Lexer;
import App.howmuchsix.hms.Handlers.Parser;
import App.howmuchsix.hms.Handlers.Token;

public enum Types {
    INT(Integer.class) {
        @Override
        public  Expression<Number> getValue(String input, List<String> scopeNames) {
            return new ArithmeticBlock(input).eval(scopeNames);
        }
    },
    DOUBLE(Double.class) {
        @Override
        public  Expression<Number> getValue(String input, List<String> scopeNames) {
            return new ArithmeticBlock(input, true).eval(scopeNames);
        }
    },
    STRING(String.class) {
        @Override
        public Expression<String> getValue(String input, List<String> scopeNames) {
            return new StringBlock(input).eval(scopeNames);
        }
    },
    BOOLEAN(Boolean.class) {
        @Override
        public  Expression<Boolean> getValue(String input, List<String> scopeNames) {
            return new LogicalBlock(input).eval(scopeNames);
        }
    },
    FUNCTION(Object.class) {
        @Override
        public Expression<Object> getValue(String input, List<String> scopeNames) {
            throw new UnsupportedOperationException("Cannot get value for FUNCTION type");
        }
    },

    NUMBER(Number.class) {
        @Override
        public Expression<Number> getValue(String input, List<String> scopeNames) {
            throw new UnsupportedOperationException("Cannot get value for NUMBER type directly");
        }
    },

    VOID(Void.class) {
        @Override
        public Expression<Void> getValue(String input, List<String> scopeNames) {
            throw new UnsupportedOperationException("Cannot get value for VOID type");
        }
    },

    ARRAY(Arrays.class){
        @Override
        public ArrayExpression getValue(String input, List<String> scopeNames) {
            List<Token> tokens = new Lexer(input).tokenize();
            Expression<?> expression = new Parser(tokens, scopeNames).parseCollection();
            if (expression.getType() == Types.ARRAY){
                return (ArrayExpression) expression;
            }
            throw new UnsupportedOperationException(input + " is not ARRAY");
        }
    },

    OBJECT(Object.class){
        @Override
        public Expression<Object> getValue(String input, List<String> scopeNames) {
            throw new UnsupportedOperationException("Cannot get value for OBJECT type");
        }
    };

    private final Class<?> typeClass;

    Types(Class<?> typeClass) {
        this.typeClass = typeClass;
    }

    public Class<?> getTypeClass() {
        return typeClass;
    }

    public abstract  Expression<?> getValue(String input, List<String> scopeNames);

}
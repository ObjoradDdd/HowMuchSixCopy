package App.howmuchsix.hms.Blocks;

import java.util.Arrays;
import java.util.List;

import App.howmuchsix.hms.Expression.ArrayExpression;
import App.howmuchsix.hms.Expression.Expression;
import App.howmuchsix.hms.Handlers.Lexer;
import App.howmuchsix.hms.Handlers.Parser;
import App.howmuchsix.hms.Handlers.Token;
import App.howmuchsix.hms.Library.Variables;

public enum Types {
    INT(Integer.class) {
        @Override
        public Expression<Number> getValue(String input, List<String> scopeNames, Variables lib, String id) {
            ArithmeticBlock block = new ArithmeticBlock(input);
            block.setUUID(id);

            return block.eval(scopeNames, lib);
        }
    },
    DOUBLE(Double.class) {
        @Override
        public Expression<Number> getValue(String input, List<String> scopeNames, Variables lib, String id) {
            ArithmeticBlock block = new ArithmeticBlock(input, true);
            block.setUUID(id);

            return block.eval(scopeNames, lib);
        }
    },
    STRING(String.class) {
        @Override
        public Expression<String> getValue(String input, List<String> scopeNames, Variables lib, String id) {
            StringBlock block = new StringBlock(input);
            block.setUUID(id);

            return block.eval(scopeNames, lib);
        }
    },
    BOOLEAN(Boolean.class) {
        @Override
        public Expression<Boolean> getValue(String input, List<String> scopeNames, Variables lib, String id) {
            LogicalBlock block = new LogicalBlock(input);
            block.setUUID(id);
            return block.eval(scopeNames, lib);
        }
    },
    FUNCTION(Object.class) {
        @Override
        public Expression<Object> getValue(String input, List<String> scopeNames, Variables lib, String id) {
            throw new UnsupportedOperationException("Cannot get value for FUNCTION type");
        }
    },

    NUMBER(Number.class) {
        @Override
        public Expression<Number> getValue(String input, List<String> scopeNames, Variables lib, String id) {
            throw new UnsupportedOperationException("Cannot get value for NUMBER type directly");
        }
    },

    VOID(Void.class) {
        @Override
        public Expression<Void> getValue(String input, List<String> scopeNames, Variables lib, String id) {
            throw new UnsupportedOperationException("Cannot get value for VOID type");
        }
    },

    ARRAY(Arrays.class) {
        @Override
        public ArrayExpression getValue(String input, List<String> scopeNames, Variables lib, String id) {
            List<Token> tokens = new Lexer(input).tokenize();
            Expression<?> expression = new Parser(tokens, scopeNames, lib, id).parseCollection();
            if (expression.getType() == Types.ARRAY) {
                return (ArrayExpression) expression;
            }
            throw new UnsupportedOperationException(input + " is not ARRAY");
        }
    },

    OBJECT(Object.class) {
        @Override
        public Expression<Object> getValue(String input, List<String> scopeNames, Variables lib, String id) {
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

    public abstract Expression<?> getValue(String input, List<String> scopeNames, Variables lib, String id);

}
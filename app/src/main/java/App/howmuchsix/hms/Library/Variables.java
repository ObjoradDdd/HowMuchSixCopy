package App.howmuchsix.hms.Library;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import App.howmuchsix.hms.Blocks.Types;
import App.howmuchsix.hms.Expression.ArrayExpression;
import App.howmuchsix.hms.Expression.Expression;
import App.howmuchsix.hms.Expression.FunctionExpression;
import App.howmuchsix.hms.Expression.NullExpression;


public final class Variables {
    private final Map<String, Map<String, Expression<?>>> variablesScopes = new HashMap<>();

    public Variables(){
        this.variablesScopes.put("MainScope", new HashMap<>());
    }

    public boolean isExistsVariable(String key) {
        List<String> scopes = new ArrayList<>(variablesScopes.keySet());
        for (int i = 0; i < scopes.size(); i++) {
            String scope = scopes.get(i);

            Map<String, Expression<?>> scopeVariables = variablesScopes.get(scope);
            if (scopeVariables != null && scopeVariables.containsKey(key)) {
                return true;
            }

            if (!scope.startsWith("Scope - ") && !scope.equals("MainScope")) {
                return false;
            }
        }
        return false;
    }

    public boolean isExistsFunction(String key) {
        if (Objects.requireNonNull(variablesScopes.get("MainScope")).containsKey(key)) {
            return Objects.requireNonNull(Objects.requireNonNull(variablesScopes.get("MainScope")).get(key)).getType() != Types.FUNCTION;
        }
        return true;
    }

    public <T> Expression<T> getExpression(String key, Class<?> expectedType, List<String> scopes) {
        Expression<?> expression;
        for (String scope : scopes) {
            Map<String, Expression<?>> scopeVariables = variablesScopes.get(scope);
            if (scopeVariables != null) {
                expression = scopeVariables.get(key);
                if (expression != null) {

                    if (!expectedType.isAssignableFrom(expression.getType().getTypeClass())) {
                        throw new RuntimeException(String.format(
                                "Type mismatch for variable %s: expected %s but got %s",
                                key,
                                expectedType.getSimpleName(),
                                expression.getType().getTypeClass().getSimpleName()
                        ));
                    }
                    @SuppressWarnings("unchecked")
                    Expression<T> typedExpression = (Expression<T>) expression;
                    return typedExpression;
                }
            }
        }
        throw new RuntimeException("Unknown variable " + key);
    }

    @SuppressWarnings("unchecked")
    public <T> Expression<T> getFromArray(String key, String indexString, Class<?> expectedType, List<String> scopes) {
        if (isExistsVariable(key)) {
            for (String scope : scopes) {
                Map<String, Expression<?>> scopeVariables = variablesScopes.get(scope);
                if (scopeVariables != null) {
                    Expression<?> expression = scopeVariables.get(key);
                    if (expression != null && expression.getType() == Types.ARRAY) {
                        ArrayExpression array = ((ArrayExpression) expression);
                        if (!expectedType.isAssignableFrom(array.getInsideType().getTypeClass())) {
                            throw new RuntimeException("Wrong type");
                        }
                        int length = array.getLength();
                        int index = (int) Types.INT.getValue(indexString, scopes, this).eval();
                        if (index > length - 1 || index < 0) {
                            throw new RuntimeException("Wrong index");
                        }
                        Expression<?>[] values = array.eval();
                        return (Expression<T>) values[index];

                    } else {
                        throw new RuntimeException(key + " is not subscriptable");
                    }
                }
            }
        }
        throw new RuntimeException("Unknown variable " + key);
    }

    public void setValueIntoArray(String key, String indexString, Expression<?> value, List<String> scopes) {
        for (int i = scopes.size() - 1; i >= 0; i--) {
            String scope = scopes.get(i);
            Map<String, Expression<?>> scopeMap = variablesScopes.get(scope);

            if (scopeMap != null) {
                if (scopeMap.containsKey(key)) {
                    Expression<?> expression = scopeMap.get(key);
                    if (expression != null && expression.getType() == Types.ARRAY) {
                        ArrayExpression array = ((ArrayExpression) expression);
                        int length = array.getLength();
                        int index = (int) Types.INT.getValue(indexString, scopes, this).eval();
                        if (index > length - 1 || index < 0) {
                            throw new RuntimeException("Wrong index");
                        }
                        array.set(index, value);
                    } else {
                        throw new RuntimeException(key + " is not subscriptable");
                    }
                }
            }
        }
    }

    public Expression<?> getExpressionWithNoType(String key, List<String> scopes) {
        if (scopes == null || scopes.isEmpty()) {
            scopes = List.of("MainScope");
        }
        if (isExistsVariable(key)) {
            for (String scope : scopes) {
                Map<String, Expression<?>> scopeVariables = variablesScopes.get(scope);
                if (scopeVariables != null) {
                    Expression<?> expression = scopeVariables.get(key);
                    if (expression != null) {
                        return expression;
                    }
                }
            }
        }
        throw new RuntimeException("Unknown variable " + key);
    }
    public FunctionExpression<?> getFunction(String key) {
        if (isExistsVariable(key)) {
            Set<String> scopes = variablesScopes.keySet();
            for (String scope : scopes) {
                Map<String, Expression<?>> scopeVariables = variablesScopes.get(scope);
                if (scopeVariables != null) {
                    Expression<?> expression = scopeVariables.get(key);
                    if (expression != null && expression.getType() == Types.FUNCTION) {
                        return (FunctionExpression<?>) expression;
                    } else {
                        throw new RuntimeException(key + " is not a function");
                    }
                }
            }
        }
        throw new RuntimeException("Unknown function " + key);
    }

    @SuppressWarnings("unchecked")
    public <T> Expression<T> getFunctionValue(String key, List<String> scopes, List<String> arguments, Class<?> expectedType) {
        FunctionExpression<?> function;
        if (!scopes.contains("MainScope")) {
            scopes = new ArrayList<>(scopes);
            scopes.add("MainScope");
        }
        if (isExistsVariable(key)) {
            for (String scope : scopes) {
                Map<String, Expression<?>> scopeVariables = variablesScopes.get(scope);
                if (scopeVariables != null) {
                    Expression<?> expression = scopeVariables.get(key);
                    if (expression != null && expression.getType() == Types.FUNCTION) {
                        function = (FunctionExpression<?>) expression;
                        if (function.getReturnType() == Types.VOID) {
                            function.functionReturn(arguments, scopes);
                            return new NullExpression<>(Types.VOID);
                        }
                        if (!expectedType.isAssignableFrom(function.getReturnType().getTypeClass())) {
                            throw new RuntimeException("Invalid type");
                        }
                        return (Expression<T>) function.functionReturn(arguments, scopes);
                    } else {
                        throw new RuntimeException(key + " is not a function");
                    }
                }
            }
        }
        throw new RuntimeException("Unknown function " + key);
    }


    public void set(String key, Expression<?> value, String scope) {
        Objects.requireNonNull(variablesScopes.get(scope)).put(key, value);
    }

    public void set(String key, Expression<?> value, List<String> scopes) {

        for (int i = scopes.size() - 1; i >= 0; i--) {
            String scope = scopes.get(i);
            Map<String, Expression<?>> scopeMap = variablesScopes.get(scope);

            if (scopeMap != null) {
                if (scopeMap.containsKey(key)) {
                    scopeMap.put(key, value);
                    return;
                }
            }
        }
    }

    public void newScope(String name) {
        variablesScopes.put(name, new HashMap<>());
    }

    public int getNumberOfScopes() {
        return variablesScopes.keySet().size();
    }

    public void deleteScope(String name) {
        if (!"MainScope".equals(name)) {
            variablesScopes.remove(name);
        }
    }
}
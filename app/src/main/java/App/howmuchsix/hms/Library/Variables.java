package App.howmuchsix.hms.Library;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import App.howmuchsix.hms.Blocks.Types;
import App.howmuchsix.hms.Expression.DoubleExpression;
import App.howmuchsix.hms.Expression.Expression;
import App.howmuchsix.hms.Expression.FunctionExpression;
import App.howmuchsix.hms.Expression.StringExpression;

public final class Variables {
    private static final Map<String, Map<String, Expression<?>>> variablesScopes;

    static {
        variablesScopes = new HashMap<>();
        variablesScopes.put("MainScope", new HashMap<>());
        Objects.requireNonNull(variablesScopes.get("MainScope")).put("PI", new DoubleExpression(Math.PI));
        Objects.requireNonNull(variablesScopes.get("MainScope")).put("Name", new StringExpression("Andrew"));
    }

    public static boolean isExistsVariable(String key) {
        for (String scope : variablesScopes.keySet()) {
            Map<String, Expression<?>> scopeVariables = variablesScopes.get(scope);
            if (scopeVariables != null && scopeVariables.containsKey(key)) {
                return false;
            }
        }
        return true;
    }

    public static boolean isExistsFunction(String key) {
        for (String scope : variablesScopes.keySet()) {
            Map<String, Expression<?>> scopeVariables = variablesScopes.get(scope);
            if (scopeVariables != null && scopeVariables.containsKey(key)) {
                if(Objects.requireNonNull(scopeVariables.get(key)).getType() == Types.FUNCTION) {
                    return false;
                }

            }
        }
        return true;
    }

    public static Expression<?> get(String key) {
        for (String scope : variablesScopes.keySet()) {
            Map<String, Expression<?>> scopeVariables = variablesScopes.get(scope);
            if (scopeVariables != null) {
                Expression<?> expression = scopeVariables.get(key);
                if (expression != null) {
                    Object value = expression.eval();
                    if (value != null) {
                        return expression;
                    }
                }
            }
        }
        throw new RuntimeException("Unknown variable " + key);
    }

    public static <T> Expression<T> getExpression(String key, List<Class<?>> expectedTypes, List<String> scopes) {
        if (scopes == null || scopes.isEmpty()) {
            scopes = List.of("MainScope");
        }

        Expression<?> expression = null;
        for (String scope : scopes) {
            Map<String, Expression<?>> scopeVariables = variablesScopes.get(scope);
            if (scopeVariables != null) {
                expression = scopeVariables.get(key);
                if (expression != null) {
                    break;
                }
            }
        }

        if (expression == null) {
            throw new RuntimeException("Unknown variable " + key);
        }

        Object value = expression.eval();
        boolean isValidType = false;

        for (Class<?> expectedType : expectedTypes) {
            if (value == null || expectedType.isInstance(value)) {
                isValidType = true;
                break;
            }
        }

        if (isValidType) {
            @SuppressWarnings("unchecked")
            Expression<T> typedExpression = (Expression<T>) expression;
            return typedExpression;
        } else {
            throw new RuntimeException(String.format(
                    "Type mismatch for variable %s: expected %s but got %s",
                    key,
                    expectedTypes.get(0).getSimpleName(),
                    value.getClass().getSimpleName()
            ));
        }
    }

    public static Expression<?> getExpressionWithNoType(String key, List<String> scopes) {
        if (scopes == null || scopes.isEmpty()) {
            scopes = List.of("MainScope");
        }

        if (!isExistsVariable(key)) {
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

    public static FunctionExpression<?> getFunction(String key, List<String> scopes){
        if(!scopes.contains("MainScope")){
            scopes = new ArrayList<>(scopes);
            scopes.add("MainScope");
        }
        if (!isExistsVariable(key)) {
            for (String scope : scopes) {
                Map<String, Expression<?>> scopeVariables = variablesScopes.get(scope);
                if (scopeVariables != null) {
                    Expression<?> expression = scopeVariables.get(key);
                    if (expression != null && expression.getType() == Types.FUNCTION) {
                        return (FunctionExpression<?>) expression;
                    }
                    else{
                        throw new RuntimeException(key + " is not a function");
                    }
                }
            }
        }
        throw new RuntimeException("Unknown function " + key);
    }

    public static <T> T getFunctionValue(String key, List<String> scopes, List<String> arguments, List<Types> expectedTypes){
        FunctionExpression<?> function = null;
        if(!scopes.contains("MainScope")){
            scopes = new ArrayList<>(scopes);
            scopes.add("MainScope");
        }
        if (!isExistsVariable(key)) {
            for (String scope : scopes) {
                Map<String, Expression<?>> scopeVariables = variablesScopes.get(scope);
                if (scopeVariables != null) {
                    Expression<?> expression = scopeVariables.get(key);
                    if (expression != null && expression.getType() == Types.FUNCTION) {
                        function = (FunctionExpression<?>) expression;
                        boolean isValid = false;
                        for (Types type : expectedTypes){
                            if(type == function.getReturnType()){
                                isValid = true;
                                break;
                            }
                        }
                        if (!isValid){
                            throw new RuntimeException("Invalid type");
                        }
                        break;
                    }
                    else{
                        for(String scop : scopes){
                            System.out.println(scop);
                        }
                        throw new RuntimeException(key + " is not a function");
                    }
                }
            }
        }

        if (function != null){
            return (T) function.functionReturn(arguments, scopes);
        }else {
            throw new RuntimeException("Unknown function " + key);
        }
    }



    public static void set(String key, Expression<?> value, String scope) {
        Objects.requireNonNull(variablesScopes.get(scope)).put(key, value);
    }

    public static void set(String key, Expression<?> value, List<String> scopes) {
        for (int i = scopes.size() - 1; i >= 0; i--) {
            String scope = scopes.get(i);
            Map<String, Expression<?>> scopeMap = variablesScopes.get(scope);

            if (scopeMap != null) {
                if (scopeMap.containsKey(key)) {
                    scopeMap.put(key, value);
                    return;
                } else if (i == scopes.size() - 1) {
                    scopeMap.put(key, value);
                    return;
                }
            }
        }
    }

    public static void newScope(String name) {
        variablesScopes.put(name, new HashMap<>());
    }

    public static int getNumberOfScopes(){
        return variablesScopes.keySet().size();
    }

    public static void deleteScope(String name) {
        if (!"MainScope".equals(name)) {
            variablesScopes.remove(name);
        }
    }
}
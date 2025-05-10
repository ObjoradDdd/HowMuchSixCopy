package App.howmuchsix.hms.Library;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import App.howmuchsix.hms.Expression.DoubleExpression;
import App.howmuchsix.hms.Expression.Expression;
import App.howmuchsix.hms.Expression.StringExpression;

public final class Variables {
    private static final Map<String ,Map<String, Expression<?>>> variablesSpaces;

    static {
        variablesSpaces = new HashMap<>();
        variablesSpaces.put("MainSpace", new HashMap<>());
        Objects.requireNonNull(variablesSpaces.get("MainSpace")).put("PI", new DoubleExpression(Math.PI));
        Objects.requireNonNull(variablesSpaces.get("MainSpace")).put("Name", new StringExpression("Andrew"));
    }

    public static boolean isExistsVariable(String key){
        for(String name : variablesSpaces.keySet()){
            if(!Objects.requireNonNull(variablesSpaces.get(name)).containsKey(key)){
                return true;
            }
        }
        return false;
    }

    public static Expression<?> get(String key) {
        if (isExistsVariable(key)) {
            throw new RuntimeException("Unknown variable " + key);
        }

        Expression<?> expression = Objects.requireNonNull(variablesSpaces.get("MainSpace")).get(key);
        if (expression == null) {
            throw new RuntimeException("Variable " + key + " has null expression");
        }
        Object value = expression.eval();
        if (value == null) {
            return null;
        }
        else {
            return expression;
        }
    }

    public static Expression<?> get(String key, String scope) {
        if (isExistsVariable(key)) {
            throw new RuntimeException("Unknown variable " + key);
        }

        Expression<?> expression = Objects.requireNonNull(variablesSpaces.get(scope)).get(key);
        if (expression == null) {
            throw new RuntimeException("Variable " + key + " has null expression");
        }
        Object value = expression.eval();
        if (value == null) {
            return null;
        }
        else {
            return expression;
        }
    }



    public static <T> Expression<T> getExpression(String key, List<Class<?>> expectedTypes){
        if (isExistsVariable(key)) {
            throw new RuntimeException("Unknown variable " + key);
        }
        Expression<?> expression = Objects.requireNonNull(variablesSpaces.get("MainSpace")).get(key);

        if (expression == null) {
            throw new RuntimeException("Variable " + key + " has null expression");
        }

        Object value = expression.eval();

        boolean isValidType = false;

        for(Class<?> expectedType : expectedTypes) {
            if (expectedType.isInstance(value)) {
                isValidType = true;
                break;
            }
        }

        if (isValidType) {
            @SuppressWarnings("unchecked")
            Expression<T> typedExpression = (Expression<T>) expression;
            return typedExpression;
        }
        else {
            throw new RuntimeException(String.format(
                    "Type mismatch for variable %s: expected %s but got %s",
                    key,
                    expectedTypes.get(0).getSimpleName(),
                    value.getClass().getSimpleName()
            ));
        }
    }

    public static <T> Expression<T> getExpression(String key, List<Class<?>> expectedTypes, String scope){
        if (isExistsVariable(key)) {
            throw new RuntimeException("Unknown variable " + key);
        }
        Expression<?> expression = Objects.requireNonNull(variablesSpaces.get(scope)).get(key);

        if (expression == null) {
            throw new RuntimeException("Variable " + key + " has null expression");
        }

        Object value = expression.eval();

        boolean isValidType = false;

        for(Class<?> expectedType : expectedTypes) {
            if (expectedType.isInstance(value)) {
                isValidType = true;
                break;
            }
        }

        if (isValidType) {
            @SuppressWarnings("unchecked")
            Expression<T> typedExpression = (Expression<T>) expression;
            return typedExpression;
        }
        else {
            throw new RuntimeException(String.format(
                    "Type mismatch for variable %s: expected %s but got %s",
                    key,
                    expectedTypes.get(0).getSimpleName(),
                    value.getClass().getSimpleName()
            ));
        }
    }

    public static Expression<?> getExpressionWithNoType(String key){
        if (!isExistsVariable(key)) {
            return Objects.requireNonNull(variablesSpaces.get("MainSpace")).get(key);
        }
        else {
            throw new RuntimeException("Unknown variable " + key);

        }
    }

    public static Expression<?> getExpressionWithNoType(String key, String scope){
        if (!isExistsVariable(key)) {
            return Objects.requireNonNull(variablesSpaces.get(scope)).get(key);
        }
        else {
            throw new RuntimeException("Unknown variable " + key);

        }
    }

    public static void set(String key, Expression<?> value){
        Objects.requireNonNull(variablesSpaces.get("MainSpace")).put(key, value);
    }


    public static void set(String key, Expression<?> value, String scope){
        Objects.requireNonNull(variablesSpaces.get(scope)).put(key, value);
    }


}

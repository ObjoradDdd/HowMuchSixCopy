package App.howmuchsix.hms.Blocks.typeConverter;

import java.util.List;

import App.howmuchsix.hms.Blocks.Types;
import App.howmuchsix.hms.Expression.FunctionExpression;
import App.howmuchsix.hms.Library.Variables;

public final class DefaultFunctions {
    public static void registerAll(Variables variables) {
        variables.set("intToString", new FunctionExpression<>(
                Types.STRING, "intToString",
                List.of(Types.INT),
                List.of("value"),
                List.of(
                        new IntToStringBlock()
                ),
                variables
        ), "MainScope");

        variables.set("booleanToString", new FunctionExpression<>(
                Types.STRING, "booleanToString",
                List.of(Types.BOOLEAN),
                List.of("value"),
                List.of(
                        new BooleanToStringBlock()
                ),
                variables
        ), "MainScope");

        variables.set("stringToInt", new FunctionExpression<>(
                Types.INT, "stringToInt",
                List.of(Types.STRING),
                List.of("value"),
                List.of(
                        new StringToIntBlock()
                ),
                variables
        ), "MainScope");

        variables.set("doubleToString", new FunctionExpression<>(
                Types.STRING, "doubleToString",
                List.of(Types.DOUBLE),
                List.of("value"),
                List.of(
                        new DoubleToStringBlock()
                ),
                variables
        ), "MainScope");

        variables.set("stringToDouble", new FunctionExpression<>(
                Types.DOUBLE, "stringToDouble",
                List.of(Types.STRING),
                List.of("value"),
                List.of(
                        new StringToDoubleBlock()
                ),
                variables
        ), "MainScope");

        variables.set("doubleToInt", new FunctionExpression<>(
                Types.INT, "doubleToInt",
                List.of(Types.DOUBLE),
                List.of("value"),
                List.of(
                        new DoubleToIntBlock()
                ),
                variables
        ), "MainScope");

        variables.set("intToDouble", new FunctionExpression<>(
                Types.DOUBLE, "intToDouble",
                List.of(Types.INT),
                List.of("value"),
                List.of(
                        new IntToDoubleBlock()
                ),
                variables
        ), "MainScope");
    }
}
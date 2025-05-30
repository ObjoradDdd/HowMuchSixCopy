package App.howmuchsix.ui.blocks

import App.howmuchsix.hms.Blocks.Types

enum class FunctionTypes {
    Int, Str, Bool, Double, Void, Array;

    fun toTypes(): Types {
        return when (this) {
            Int -> Types.INT
            Str -> Types.STRING
            Bool -> Types.BOOLEAN
            Double -> Types.DOUBLE
            Void -> Types.VOID
            Array -> Types.ARRAY
        }
    }
}
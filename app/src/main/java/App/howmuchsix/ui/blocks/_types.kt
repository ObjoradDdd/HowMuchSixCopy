package App.howmuchsix.ui.blocks

import App.howmuchsix.hms.Blocks.Types

enum class _types {
    Int, Str, Bool, Double;

    fun toTypes(): Types {
        return when (this) {
            Int -> Types.INT
            Str -> Types.STRING
            Bool -> Types.BOOLEAN
            Double -> Types.DOUBLE
        }
    }

    companion object {
        fun fromTypes(types: Types): _types {
            return when (types) {
                Types.INT -> Int
                Types.STRING -> Str
                Types.BOOLEAN -> Bool
                Types.DOUBLE -> Double
                else -> throw IllegalArgumentException("Unsupported Types value: $types")
            }
        }

        fun fromString(typeString: String): _types {
            return when (typeString.uppercase()) {
                "INT", "INTEGER" -> Int
                "STR", "STRING" -> Str
                "BOOL", "BOOLEAN" -> Bool
                "DOUBLE", "FLOAT" -> Double
                else -> throw IllegalArgumentException("Unknown type string: $typeString")
            }
        }
    }
}
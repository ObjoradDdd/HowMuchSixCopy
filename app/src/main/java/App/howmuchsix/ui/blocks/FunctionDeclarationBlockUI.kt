package App.howmuchsix.ui.blocks

import App.howmuchsix.hms.Blocks.Block
import App.howmuchsix.hms.Blocks.FunctionDeclarationBlock
import App.howmuchsix.hms.Blocks.Types
import App.howmuchsix.viewmodel.BlockEditorViewModel
import App.howmuchsix.viewmodel.ConsoleViewModel
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier

class FunctionDeclarationBlockUI : BlockUI() {
    private var functionName by mutableStateOf("")
    private var returnType by mutableStateOf("")
    private var argumentsTypes by mutableStateOf<List<String>>(emptyList())
    private var argumentsNames by mutableStateOf<List<String>>(emptyList())
    private var body by mutableStateOf<List<BlockUI>>(emptyList())

    fun initializeFromBD(
        name: String,
        retType: String,
        argTypes: List<String>,
        argNames: List<String>,
        bodyUI: List<BlockUI>
    ) {
        functionName = name
        returnType = retType
        argumentsTypes = argTypes
        argumentsNames = argNames
        body = bodyUI
    }


    private fun stringToTypes(typeString: String): Types {
        return when (typeString.uppercase()) {
            "INT", "INTEGER" -> Types.INT
            "DOUBLE", "FLOAT" -> Types.DOUBLE
            "STRING", "STR" -> Types.STRING
            "BOOLEAN", "BOOL" -> Types.BOOLEAN
            "VOID" -> Types.VOID
            "ARRAY" -> Types.ARRAY
            else -> throw IllegalArgumentException("Unknown type string: $typeString")
        }
    }

    override fun metamorphosis(consoleViewModel: ConsoleViewModel): Block {
        if (functionName.isEmpty()) {
            throw IllegalArgumentException("Function name is required")
        }
        if (returnType.isEmpty()) {
            throw IllegalArgumentException("Return type is required")
        }
        if (argumentsTypes.size != argumentsNames.size) {
            throw IllegalArgumentException("Arguments types and names lists must have the same size")
        }

        val returnTypeEnum = stringToTypes(returnType)
        val argumentsTypesEnum = argumentsTypes.map { stringToTypes(it) }


        val bodyBlocks = body.map { it.metamorphosis(consoleViewModel) }

        return FunctionDeclarationBlock(
            returnTypeEnum,
            functionName,
            argumentsTypesEnum,
            argumentsNames,
            bodyBlocks
        )
    }

    @Composable
    override fun Render(modifier: Modifier, viewModel: BlockEditorViewModel?) {
        TODO("Not yet implemented")
    }

}
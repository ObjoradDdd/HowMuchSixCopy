package App.howmuchsix.ui.blocks

import App.howmuchsix.hms.Blocks.Block
import App.howmuchsix.hms.Blocks.FunctionDeclarationBlock
import App.howmuchsix.hms.Blocks.Types
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

class FunctionDeclarationBlockUI : BlockUI() {
    override fun metamorphosis(params: HashMap<String, Any>): Block {

        val returnType = params["returnType"] as? Types
            ?: throw IllegalArgumentException("Return type parameter is required")


        val name = params["name"] as? String
            ?: throw IllegalArgumentException("Function name parameter is required")


        val argumentTypes = params["argumentTypes"]?.let { typesValue ->
            when (typesValue) {
                is List<*> -> typesValue.filterIsInstance<Types>()
                else -> listOf()
            }
        } ?: listOf()


        val argumentNames = params["argumentNames"]?.let { namesValue ->
            when (namesValue) {
                is List<*> -> namesValue.filterIsInstance<String>()
                else -> listOf()
            }
        } ?: listOf()


        val bodyBlocks = params["body"]?.let { bodyValue ->
            when (bodyValue) {
                is List<*> -> bodyValue.filterIsInstance<Block>()
                else -> listOf()
            }
        } ?: listOf()


        if (argumentTypes.size != argumentNames.size) {
            throw IllegalArgumentException("Argument types and names lists must have the same size")
        }

        return FunctionDeclarationBlock(returnType, name, argumentTypes, argumentNames, bodyBlocks)
    }

    @Composable
    override fun Render(modifier: Modifier) {
        TODO("Not yet implemented")
    }
}
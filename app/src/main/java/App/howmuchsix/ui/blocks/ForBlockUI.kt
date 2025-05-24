package App.howmuchsix.ui.blocks

import App.howmuchsix.hms.Blocks.AssignmentBlock
import App.howmuchsix.hms.Blocks.Block
import App.howmuchsix.hms.Blocks.DeclarationBlock
import App.howmuchsix.hms.Blocks.ForBlock
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

class ForBlockUI : BlockUI() {
    override fun metamorphosis(params: HashMap<String, Any>): Block {

        val iterator = params["iterator"]?.let { iteratorValue ->
            when (iteratorValue) {
                is AssignmentBlock -> iteratorValue
                is DeclarationBlock -> iteratorValue
                else -> throw IllegalArgumentException("Iterator must be an AssignmentBlock or DeclarationBlock")
            }
        } ?: throw IllegalArgumentException("Iterator parameter is required")

        val logicalExpression = params["condition"] as? String
            ?: throw IllegalArgumentException("Logical expression parameter is required")

        val action = params["action"] as? AssignmentBlock
            ?: throw IllegalArgumentException("Action parameter must be an AssignmentBlock")

        val bodyBlocks = params["body"]?.let { bodyValue ->
            when (bodyValue) {
                is List<*> -> bodyValue.filterIsInstance<Block>()
                else -> listOf()
            }
        } ?: listOf()

        return when (iterator) {
            is AssignmentBlock -> ForBlock(iterator, logicalExpression, action, bodyBlocks)
            is DeclarationBlock -> ForBlock(iterator, logicalExpression, action, bodyBlocks)
            else -> throw IllegalStateException("Unexpected iterator type")
        }
    }

    @Composable
    override fun Render(modifier: Modifier) {
        TODO("Not yet implemented")
    }
}
package App.howmuchsix.ui.blocks

import App.howmuchsix.hms.Blocks.Block
import App.howmuchsix.hms.Blocks.WhileBlock
import App.howmuchsix.viewmodel.BlockEditorViewModel
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

class WhileBlockUI : BlockUI() {
    override fun metamorphosis(params: HashMap<String, Any>): Block {

        val condition = params["condition"] as? String
            ?: throw IllegalArgumentException("Condition parameter is required")


        val bodyBlocks = params["body"]?.let { bodyValue ->
            when (bodyValue) {
                is List<*> -> bodyValue.filterIsInstance<Block>()
                else -> throw IllegalArgumentException("Body must be a list of blocks")
            }
        } ?: throw IllegalArgumentException("Body parameter is required")

        return WhileBlock(condition, bodyBlocks)
    }

    @Composable
    override fun Render(modifier: Modifier, viewModel: BlockEditorViewModel?) {
        TODO("Not yet implemented")
    }
}
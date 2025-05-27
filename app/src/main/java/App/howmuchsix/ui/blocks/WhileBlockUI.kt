package App.howmuchsix.ui.blocks

import App.howmuchsix.hms.Blocks.Block
import App.howmuchsix.hms.Blocks.WhileBlock
import App.howmuchsix.viewmodel.BlockEditorViewModel
import App.howmuchsix.viewmodel.ConsoleViewModel
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier

class WhileBlockUI : BlockUI() {
    private var condition by mutableStateOf("")
    private var body by mutableStateOf<List<BlockUI>>(emptyList())

    fun initializeFromBD(conditionString: String, bodyUI: List<BlockUI>) {
        condition = conditionString
        body = bodyUI
    }

    override fun metamorphosis(consoleViewModel: ConsoleViewModel): Block {
        if (condition.isEmpty()) {
            throw IllegalArgumentException("Condition is required")
        }
        if (body.isEmpty()) {
            throw IllegalArgumentException("Body is required")
        }

        val bodyBlocks = body.map { it.metamorphosis(consoleViewModel) }
        return WhileBlock(condition, bodyBlocks)
    }

    @Composable
    override fun Render(modifier: Modifier, viewModel: BlockEditorViewModel?) {
        TODO("Not yet implemented")
    }
}
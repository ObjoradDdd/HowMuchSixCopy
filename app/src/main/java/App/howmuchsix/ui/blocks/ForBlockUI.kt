package App.howmuchsix.ui.blocks

import App.howmuchsix.hms.Blocks.AssignmentBlock
import App.howmuchsix.hms.Blocks.Block
import App.howmuchsix.hms.Blocks.DeclarationBlock
import App.howmuchsix.hms.Blocks.ForBlock
import App.howmuchsix.viewmodel.BlockEditorViewModel
import App.howmuchsix.viewmodel.ConsoleViewModel
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier

class ForBlockUI : BlockUI() {
    private var iterator by mutableStateOf<BlockUI?>(null)
    private var condition by mutableStateOf("")
    private var action by mutableStateOf<BlockUI?>(null)
    private var body by mutableStateOf<List<BlockUI>>(emptyList())


    fun initializeFromBD(
        iteratorUI: BlockUI,
        conditionString: String,
        actionUI: BlockUI,
        bodyUI: List<BlockUI>
    ) {
        iterator = iteratorUI
        condition = conditionString
        action = actionUI
        body = bodyUI
    }

    override fun metamorphosis(consoleViewModel: ConsoleViewModel): Block {
        if (iterator == null) {
            throw IllegalArgumentException("Iterator is required")
        }
        if (condition.isEmpty()) {
            throw IllegalArgumentException("Condition is required")
        }
        if (action == null) {
            throw IllegalArgumentException("Action is required")
        }

        val iteratorBlock = iterator!!.metamorphosis(consoleViewModel)
        val actionBlock = action!!.metamorphosis(consoleViewModel) as AssignmentBlock
        val bodyBlocks = body.map { it.metamorphosis(consoleViewModel) }

        return when (iteratorBlock) {
            is AssignmentBlock -> ForBlock(iteratorBlock, condition, actionBlock, bodyBlocks)
            is DeclarationBlock -> ForBlock(iteratorBlock, condition, actionBlock, bodyBlocks)
            else -> throw IllegalStateException("Iterator must be AssignmentBlock or DeclarationBlock")
        }
    }

    @Composable
    override fun Render(modifier: Modifier, viewModel: BlockEditorViewModel?) {
        TODO("Not yet implemented")
    }


}
package App.howmuchsix.ui.blocks

import App.howmuchsix.hms.Blocks.Block
import App.howmuchsix.hms.Blocks.IfBlock
import App.howmuchsix.viewmodel.BlockEditorViewModel
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier

class IfBlockUI : BlockUI() {
    private var condition by mutableStateOf("")
    private var trueAction by mutableStateOf<List<BlockUI>>(emptyList())
    private var elifConditions by mutableStateOf<List<String>?>(null)
    private var elifActions by mutableStateOf<List<List<BlockUI>>?>(null)
    private var falseAction by mutableStateOf<List<BlockUI>?>(null)


    fun initializeFromBD(
        conditionString: String,
        trueActionUI: List<BlockUI>,
        elifConditionsList: List<String>?,
        elifActionsUI: List<List<BlockUI>>?,
        falseActionUI: List<BlockUI>?
    ) {
        condition = conditionString
        trueAction = trueActionUI
        elifConditions = elifConditionsList
        elifActions = elifActionsUI
        falseAction = falseActionUI
    }

    override fun metamorphosis(): Block {
        if (condition.isEmpty()) {
            throw IllegalArgumentException("Condition is required")
        }
        if (trueAction.isEmpty()) {
            throw IllegalArgumentException("True action is required")
        }

        val trueActionBlocks = trueAction.map { it.metamorphosis() }
        val falseActionBlocks = falseAction?.map { it.metamorphosis() }
        val elifActionsList = elifActions?.map { actionList ->
            actionList.map { it.metamorphosis() }
        }

        return when {
            elifActionsList != null && elifConditions != null && falseActionBlocks != null ->
                IfBlock(condition, trueActionBlocks, elifConditions!!, elifActionsList, falseActionBlocks)
            falseActionBlocks != null ->
                IfBlock(condition, trueActionBlocks, falseActionBlocks)
            else ->
                IfBlock(condition, trueActionBlocks)
        }
    }

    @Composable
    override fun Render(modifier: Modifier, viewModel: BlockEditorViewModel?) {
        TODO("Not yet implemented")
    }

}
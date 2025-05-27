package App.howmuchsix.ui.blocks

import App.howmuchsix.hms.Blocks.Block
import App.howmuchsix.hms.Blocks.IfBlock
import App.howmuchsix.viewmodel.BlockEditorViewModel
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

class IfBlockUI : BlockUI() {
    override fun metamorphosis(params: HashMap<String, Any>): Block {

        val condition = params["condition"] as? String
            ?: throw IllegalArgumentException("Condition parameter is required")


        val trueAction = params["trueAction"]?.let { actionValue ->
            when (actionValue) {
                is List<*> -> actionValue.filterIsInstance<Block>()
                else -> throw IllegalArgumentException("True action must be a list of blocks")
            }
        } ?: throw IllegalArgumentException("True action parameter is required")


        val falseAction = params["falseAction"]?.let { actionValue ->
            when (actionValue) {
                is List<*> -> actionValue.filterIsInstance<Block>()
                else -> listOf()
            }
        }

        val elifConditions = params["elifConditions"]?.let { conditionsValue ->
            when (conditionsValue) {
                is List<*> -> conditionsValue.filterIsInstance<String>()
                else -> listOf()
            }
        }

        val elifActions = params["elifActions"]?.let { actionsValue ->
            when (actionsValue) {
                is List<*> -> actionsValue.mapNotNull { action ->
                    when (action) {
                        is List<*> -> action.filterIsInstance<Block>()
                        else -> null
                    }
                }
                else -> listOf()
            }
        }


        if ((elifConditions == null && elifActions != null) ||
            (elifConditions != null && elifActions == null) ||
            (elifConditions != null && elifActions != null && elifConditions.size != elifActions.size)) {
            throw IllegalArgumentException("Elif conditions and actions must match")
        }

        return when {
            elifConditions != null && falseAction != null ->
                IfBlock(condition, trueAction, elifConditions, elifActions!!, falseAction)

            falseAction != null ->
                IfBlock(condition, trueAction, falseAction)

            else ->
                IfBlock(condition, trueAction)
        }
    }

    @Composable
    override fun Render(modifier: Modifier, viewModel: BlockEditorViewModel?) {
        TODO("Not yet implemented")
    }
}
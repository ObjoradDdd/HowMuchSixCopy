package App.howmuchsix.localeDataStorage.project.blocks

import App.howmuchsix.localeDataStorage.project.BlockDB
import App.howmuchsix.ui.blocks.IfBlockUI
import App.howmuchsix.ui.blocks.BlockUI
import kotlinx.serialization.Serializable

@Serializable
data class IfBlockBD(
    override val name: String = "if_block",
    override val type: BlockType = BlockType.IF,
    val condition: String,
    val trueAction: List<BlockDB>,
    val elifConditions: List<String>? = null,
    val elifActions: List<List<BlockDB>>? = null,
    val falseAction: List<BlockDB>? = null
) : BlockDB {
    override fun fromBDBlocToUI(): BlockUI {
        val ifUI = IfBlockUI()

        val trueActionUI = trueAction.map { it.fromBDBlocToUI() }
        val elifActionsUI = elifActions?.map { actionList ->
            actionList.map { it.fromBDBlocToUI() }
        }
        val falseActionUI = falseAction?.map { it.fromBDBlocToUI() }

        ifUI.initializeFromBD(condition, trueActionUI, elifConditions, elifActionsUI, falseActionUI)
        return ifUI
    }
}
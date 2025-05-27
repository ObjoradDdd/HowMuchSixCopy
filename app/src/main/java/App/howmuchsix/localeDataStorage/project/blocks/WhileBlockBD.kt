package App.howmuchsix.localeDataStorage.project.blocks

import App.howmuchsix.localeDataStorage.project.BlockDB
import App.howmuchsix.ui.blocks.WhileBlockUI
import App.howmuchsix.ui.blocks.BlockUI
import kotlinx.serialization.Serializable

@Serializable
data class WhileBlockBD(
    override val name: String = "while_block",
    override val type: BlockType = BlockType.WHILE,
    val condition: String,
    val body: List<BlockDB>
) : BlockDB {
    override fun fromBDBlocToUI(): BlockUI {
        val whileUI = WhileBlockUI()

        val bodyUI = body.map { it.fromBDBlocToUI() }

        whileUI.initializeFromBD(condition, bodyUI)
        return whileUI
    }
}
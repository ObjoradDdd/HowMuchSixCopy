package App.howmuchsix.localeDataStorage.project.blocks

import App.howmuchsix.localeDataStorage.project.BlockDB
import App.howmuchsix.ui.blocks.BreakBlockUI
import App.howmuchsix.ui.blocks.BlockUI
import kotlinx.serialization.Serializable

@Serializable
data class BreakBlockBD(
    override val name: String = "break_block",
    override val type: BlockType = BlockType.BREAK
) : BlockDB {
    override fun fromBDBlocToUI(): BlockUI {
        return BreakBlockUI()
    }
}
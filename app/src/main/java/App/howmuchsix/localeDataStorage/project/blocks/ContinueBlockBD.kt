package App.howmuchsix.localeDataStorage.project.blocks

import App.howmuchsix.localeDataStorage.project.BlockDB
import App.howmuchsix.ui.blocks.BreakBlockUI
import App.howmuchsix.ui.blocks.BlockUI
import App.howmuchsix.ui.blocks.ContinueBlockUI
import kotlinx.serialization.Serializable

@Serializable
data class ContinueBlockBD(
    override val name: String = "continue_block",
    override val type: BlockType = BlockType.CONTINUE
) : BlockDB {
    override fun fromBDBlocToUI(): BlockUI {
        return ContinueBlockUI()
    }
}
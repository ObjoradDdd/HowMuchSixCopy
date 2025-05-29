package App.howmuchsix.localeDataStorage.project.blocks

import App.howmuchsix.localeDataStorage.project.BlockDB
import App.howmuchsix.ui.blocks.ReturnBlockUI
import App.howmuchsix.ui.blocks.BlockUI
import kotlinx.serialization.Serializable

@Serializable
data class ReturnBlockBD(
    override val name: String = "return_block",
    override val type: BlockType = BlockType.RETURN,
    val value: String
) : BlockDB {
    override fun fromBDBlocToUI(): BlockUI {
        val returnUI = ReturnBlockUI()
        //returnUI.initializeFromBD(value)
        return returnUI
    }
}
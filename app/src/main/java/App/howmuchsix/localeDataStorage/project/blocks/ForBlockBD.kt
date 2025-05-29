package App.howmuchsix.localeDataStorage.project.blocks

import App.howmuchsix.localeDataStorage.project.BlockDB
import App.howmuchsix.ui.blocks.ForBlockUI
import App.howmuchsix.ui.blocks.BlockUI
import kotlinx.serialization.Serializable

@Serializable
data class ForBlockBD(
    override val name: String = "for_block",
    override val type: BlockType = BlockType.FOR,
    val iterator: BlockDB?,
    val condition: String,
    val action: BlockDB?,
    val body: List<BlockDB>
) : BlockDB {
    override fun fromBDBlocToUI(): BlockUI {
        val forUI = ForBlockUI()
        var iteratorUI : BlockUI? = null
        var actionUI : BlockUI? = null

        if (iterator !=null){
            iteratorUI = iterator.fromBDBlocToUI()
        }

        if (action !=null){
            actionUI = action.fromBDBlocToUI()
        }

        val bodyUI = body.map { it.fromBDBlocToUI() }

        //forUI.initializeFromBD(iteratorUI, condition, actionUI, bodyUI)
        return forUI
    }
}
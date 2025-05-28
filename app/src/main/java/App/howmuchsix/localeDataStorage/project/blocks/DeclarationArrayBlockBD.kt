package App.howmuchsix.localeDataStorage.project.blocks

import App.howmuchsix.localeDataStorage.project.BlockDB
import App.howmuchsix.ui.blocks.DeclarationArrayBlockUI
import App.howmuchsix.ui.blocks.BlockUI
import kotlinx.serialization.Serializable

@Serializable
data class DeclarationArrayBlockBD(
    override val name: String = "declaration_array_block",
    override val type: BlockType = BlockType.DECLARATION_ARRAY,
    val arrayName: String,
    val dataType: String,
    val length: Int,
    val values: List<String>? = null
) : BlockDB {
    override fun fromBDBlocToUI(): BlockUI {
        val declarationArrayUI = DeclarationArrayBlockUI()
        declarationArrayUI.initializeFromBD(arrayName, dataType, length, values)
        return declarationArrayUI
    }
}
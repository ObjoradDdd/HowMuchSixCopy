package App.howmuchsix.localeDataStorage.project.blocks

import App.howmuchsix.localeDataStorage.project.BlockDB
import App.howmuchsix.ui.blocks.BlockUI
import App.howmuchsix.ui.blocks.DeclarationBlockUI
import kotlinx.serialization.Serializable

@Serializable
data class DeclarationBlockBD(
    override val name: String = "declaration_block",
    override val type: BlockType = BlockType.DECLARATION,
    val variables: List<String>,
    val values: List<String>,
    val dataType: String
) : BlockDB {
    override fun fromBDBlocToUI(): BlockUI {
        val declarationUI = DeclarationBlockUI()
        declarationUI.initializeFromBD(variables, values, dataType)
        return declarationUI
    }
}
package App.howmuchsix.localeDataStorage.project.blocks

import App.howmuchsix.localeDataStorage.project.BlockDB
import App.howmuchsix.ui.blocks.FunctionDeclarationBlockUI
import App.howmuchsix.ui.blocks.BlockUI
import kotlinx.serialization.Serializable

@Serializable
data class FunctionDeclarationBlockBD(
    override val name: String = "function_declaration_block",
    override val type: BlockType = BlockType.FUNCTION_DECLARATION,
    val functionName: String,
    val returnType: String,
    val argumentsTypes: List<String>,
    val argumentsNames: List<String>,
    val body: List<BlockDB>
) : BlockDB {
    override fun fromBDBlocToUI(): BlockUI {
        val functionDeclarationUI = FunctionDeclarationBlockUI()

        val bodyUI = body.map { it.fromBDBlocToUI() }

        functionDeclarationUI.initializeFromBD(functionName, returnType, argumentsTypes, argumentsNames, bodyUI)
        return functionDeclarationUI
    }
}
package App.howmuchsix.localeDataStorage.project.blocks

import App.howmuchsix.localeDataStorage.project.BlockDB
import App.howmuchsix.ui.blocks.FunctionBlockUI
import App.howmuchsix.ui.blocks.BlockUI
import kotlinx.serialization.Serializable

@Serializable
data class FunctionBlockBD(
    override val name: String = "function_block",
    override val type: BlockType = BlockType.FUNCTION,
    val functionName: String
) : BlockDB {
    override fun fromBDBlocToUI(): BlockUI {
        val functionUI = FunctionBlockUI()
        TODO("""
            functionUI.initializeFromBD(functionName)
            return functionUI      
        """.trimIndent())
        }
}
package App.howmuchsix.localeDataStorage.project.blocks

import App.howmuchsix.localeDataStorage.project.BlockDB
import App.howmuchsix.ui.blocks.PrintBlockUI
import App.howmuchsix.ui.blocks.BlockUI
import kotlinx.serialization.Serializable

@Serializable
data class PrintBlockBD(
    override val name: String = "print_block",
    override val type: BlockType = BlockType.PRINT,
    val value: String
) : BlockDB {
    override fun fromBDBlocToUI(): BlockUI {
        val printUI = PrintBlockUI()


        TODO("""
            printUI.initializeFromBD(value)
        return printUI    
        """.trimIndent())

    }
}
package App.howmuchsix.localeDataStorage.project.blocks

import App.howmuchsix.localeDataStorage.project.BlockDB
import App.howmuchsix.ui.blocks.AssignmentBlockUI
import App.howmuchsix.ui.blocks.BlockUI

data class AssignmentBlockBD(
    override val name: String = "assignment_block",
    override val type: BlockType = BlockType.ASSIGNMENT,
    val variable: String,
    val value: String
) : BlockDB {
    override fun fromBDBlocToUI(): BlockUI {
        return AssignmentBlockUI()
    }
}
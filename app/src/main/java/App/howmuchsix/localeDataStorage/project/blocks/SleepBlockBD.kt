package App.howmuchsix.localeDataStorage.project.blocks

import App.howmuchsix.localeDataStorage.project.BlockDB
import App.howmuchsix.ui.blocks.PrintBlockUI
import App.howmuchsix.ui.blocks.BlockUI
import App.howmuchsix.ui.blocks.SleepBlockUI
import kotlinx.serialization.Serializable

@Serializable
data class SleepBlockBD(
    override val name: String = "sleep_block",
    override val type: BlockType = BlockType.SLEEP,
    val value: String
) : BlockDB {
    override fun fromBDBlocToUI(): BlockUI {
        val sleepBlockUI = SleepBlockUI()
        sleepBlockUI.initializeFromBD(value)
        return sleepBlockUI
    }
}
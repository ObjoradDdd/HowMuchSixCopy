package App.howmuchsix.localeDataStorage.project

import App.howmuchsix.localeDataStorage.project.blocks.BlockType
import App.howmuchsix.ui.blocks.BlockUI
import kotlinx.serialization.Serializable


@Serializable
interface BlockDB {
    val name : String
    val type : BlockType
    fun fromBDBlocToUI() : BlockUI
}
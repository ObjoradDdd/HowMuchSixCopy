package App.howmuchsix.ui.blocks

import App.howmuchsix.hms.Blocks.Block
import App.howmuchsix.localeDataStorage.project.BlockDB
import App.howmuchsix.viewmodel.BlockEditorViewModel
import App.howmuchsix.viewmodel.ConsoleViewModel
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import java.util.UUID

abstract class BlockUI {

    var id : String = ""
    var body: MutableList<BlockUI> = mutableListOf()

    abstract fun metamorphosis(consoleViewModel: ConsoleViewModel): Block

    @Composable
    abstract fun Render(modifier: Modifier, viewModel: BlockEditorViewModel?)

    abstract fun toDBBlock(): BlockDB

    fun addToBody(blockUI: BlockUI){
        this.body.add(blockUI)
    }
    fun deleteToBody(blockUI: BlockUI){
        this.body.remove(blockUI)
    }

    fun setUUID(id : String){
        this.id = id
    }

    fun getUUID(): String {
        return id
    }

}
package App.howmuchsix.ui.blocks

import App.howmuchsix.hms.Blocks.Block
import App.howmuchsix.localeDataStorage.project.BlockDB
import App.howmuchsix.viewmodel.BlockEditorViewModel
import App.howmuchsix.viewmodel.ConsoleViewModel
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

abstract class BlockUI {

    abstract fun metamorphosis(consoleViewModel: ConsoleViewModel) : Block

    @Composable
    abstract fun Render(modifier: Modifier, viewModel: BlockEditorViewModel?)

    abstract fun toDBBlock() :BlockDB


}
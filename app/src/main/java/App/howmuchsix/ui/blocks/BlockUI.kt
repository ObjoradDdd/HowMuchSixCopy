package App.howmuchsix.ui.blocks

import App.howmuchsix.hms.Blocks.Block
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

abstract class BlockUI {
    abstract fun metamorphosis() : Block

    @Composable
    abstract fun Render(modifier: Modifier)
}
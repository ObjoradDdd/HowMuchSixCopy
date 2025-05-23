package App.howmuchsix.ui.blocks

import App.howmuchsix.hms.Blocks.Block
import App.howmuchsix.hms.Blocks.BreakBlock
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

class BreakBlockUI : BlockUI() {
    override fun metamorphosis(params: HashMap<String, Any>): Block {
        return BreakBlock()
    }

    @Composable
    override fun Render(modifier: Modifier) {
        TODO("Not yet implemented")
    }
}
package App.howmuchsix.ui.blocks

import App.howmuchsix.hms.Blocks.Block
import App.howmuchsix.hms.Blocks.BreakBlock
import App.howmuchsix.ui.theme.design_elements.BlockPeach
import App.howmuchsix.ui.theme.design_elements.SubTitle1
import App.howmuchsix.ui.theme.design_elements.size60
import App.howmuchsix.ui.theme.design_elements.size8
import App.howmuchsix.viewmodel.BlockEditorViewModel
import App.howmuchsix.viewmodel.ConsoleViewModel
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier

class StartProgramBlockUI : BlockUI() {

    @Composable
    override fun Render(modifier: Modifier, viewModel: BlockEditorViewModel?) {
        val error = viewModel?.isBlockWithError(this.id)
        Row(
            modifier = modifier
                .defaultMinSize(minHeight = size60)
                .background(BlockPeach, RoundedCornerShape(size8))
                .padding(size8)
        ) {
            Spacer(Modifier.width(size8))
            Text(
                text = "Start program",
                style = SubTitle1,
                modifier = Modifier.align(Alignment.CenterVertically)
            )
            Spacer(Modifier.width(size8))
        }
    }

    override fun metamorphosis(consoleViewModel: ConsoleViewModel): Block {
        return BreakBlock()
    }
}
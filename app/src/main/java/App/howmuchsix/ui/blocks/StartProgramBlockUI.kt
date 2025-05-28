package App.howmuchsix.ui.blocks

import App.howmuchsix.hms.Blocks.Block
import App.howmuchsix.hms.Blocks.BreakBlock
import App.howmuchsix.ui.theme.design_elements.*
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
import androidx.compose.ui.unit.dp

class StartProgramBlockUI: BlockUI() {

    @Composable
    override fun Render(modifier: Modifier, viewModel: BlockEditorViewModel?) {
        Row (
            modifier = modifier
                .defaultMinSize(minHeight = 60.dp)
                .background(BlockPeach, RoundedCornerShape(8.dp))
                .padding(8.dp)
        ){
            Spacer(Modifier.width(8.dp))
            Text(text = "Start program",
                style = SubTitle1,
                modifier = Modifier.align(Alignment.CenterVertically)
            )
            Spacer(Modifier.width(8.dp))
        }
    }

    override fun metamorphosis(consoleViewModel: ConsoleViewModel): Block {
        return BreakBlock()
    }
}
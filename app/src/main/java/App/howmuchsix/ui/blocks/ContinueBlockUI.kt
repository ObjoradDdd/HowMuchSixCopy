package App.howmuchsix.ui.blocks

import App.howmuchsix.hms.Blocks.Block
import App.howmuchsix.hms.Blocks.BreakBlock
import App.howmuchsix.hms.Blocks.ContinueBlock
import App.howmuchsix.localeDataStorage.project.BlockDB
import App.howmuchsix.localeDataStorage.project.blocks.ContinueBlockBD
import App.howmuchsix.localeDataStorage.project.blocks.DeclarationBlockBD
import App.howmuchsix.ui.theme.design_elements.*
import App.howmuchsix.viewmodel.BlockEditorViewModel
import App.howmuchsix.viewmodel.ConsoleViewModel
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

class ContinueBlockUI : BlockUI() {

    @Composable
    override fun Render(modifier: Modifier, viewModel: BlockEditorViewModel?) {
        Row (
            modifier = modifier
                .background(BlockOrange, RoundedCornerShape(size8))
                .padding(size8)
        ){
            Spacer(Modifier.width(size8))
            Text(text = "Continue",
                style = SubTitle1,
                modifier = Modifier.align(Alignment.CenterVertically)
            )
            Spacer(Modifier.width(size8))
        }
    }

    override fun metamorphosis(consoleViewModel: ConsoleViewModel): Block {
        return ContinueBlock()
    }

    override fun toDBBlock(): BlockDB {
        return ContinueBlockBD()
    }
}
package App.howmuchsix.ui.blocks

import App.howmuchsix.hms.Blocks.Block
import App.howmuchsix.hms.Blocks.PrintBlock
import App.howmuchsix.ui.theme.ButtonTextField
import App.howmuchsix.ui.theme.design_elements.BlockRed
import App.howmuchsix.ui.theme.design_elements.SubTitle1
import App.howmuchsix.ui.theme.design_elements.TextWhite
import App.howmuchsix.ui.theme.design_elements.size8
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier

class PrintBlockUI : BlockUI() {
    private var textValue by mutableStateOf("")

    @Composable
    override fun Render(modifier: Modifier, viewModel: BlockEditorViewModel?) {
        Row(
            modifier = modifier
                .background(BlockRed, RoundedCornerShape(size8))
                .padding(size8)
        ) {
            Spacer(Modifier.width(size8))

            Text(
                text = "Print",
                style = SubTitle1,
                modifier = Modifier.align(Alignment.CenterVertically)
            )
            Spacer(Modifier.width(size8))
            ButtonTextField(
                value = textValue,
                onValueChange = { textValue = it },
                textStyle = SubTitle1,
                placeholder = "value",
                focusedBorderColor = TextWhite
            )
        }
    }

    override fun metamorphosis(consoleViewModel: ConsoleViewModel): Block {
        val block = PrintBlock(textValue, consoleViewModel)
        block.uuid = this.id
        return block
    }

}
package App.howmuchsix.ui.blocks

import App.howmuchsix.hms.Blocks.Block
import App.howmuchsix.hms.Blocks.PrintBlock
import App.howmuchsix.localeDataStorage.project.BlockDB
import App.howmuchsix.localeDataStorage.project.blocks.PrintBlockBD
import App.howmuchsix.ui.theme.ButtonTextField
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

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
                placeholder = "value"
            )
        }
    }

    fun initializeFromBD(value: String) {
        this.textValue = value
    }

    override fun toDBBlock(): BlockDB {
        val printBlock = PrintBlockBD(value = textValue)
        return printBlock
    }

    override fun metamorphosis(consoleViewModel: ConsoleViewModel): Block {
        return PrintBlock(textValue, consoleViewModel)
    }

}
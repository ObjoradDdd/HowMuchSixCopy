package App.howmuchsix.ui.blocks

import App.howmuchsix.hms.Blocks.Block
import App.howmuchsix.hms.Blocks.ReturnBlock
import App.howmuchsix.ui.theme.ButtonTextField
import App.howmuchsix.ui.theme.DropDownFunctionMenuTypeSelector
import App.howmuchsix.ui.theme.design_elements.BlockPink
import App.howmuchsix.ui.theme.design_elements.SubTitle1
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

class ReturnBlockUI : BlockUI() {

    private var value by mutableStateOf("")
    private var selectedType by mutableStateOf<FunctionTypes?>(null)

    @Composable
    override fun Render(modifier: Modifier, viewModel: BlockEditorViewModel?) {
        Row(
            modifier = modifier
                .background(BlockPink, RoundedCornerShape(size8))
                .padding(size8),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Spacer(Modifier.width(size8))

            Text(
                text = "Return",
                style = SubTitle1,
                modifier = Modifier.align(Alignment.CenterVertically)
            )

            Spacer(Modifier.width(size8))
            DropDownFunctionMenuTypeSelector(

                selectedType = selectedType,
                onTypeSelected = { selectedType = it }
            )
            Spacer(Modifier.width(size8))
            ButtonTextField(
                value = value,
                onValueChange = { value = it },
                textStyle = SubTitle1,
                placeholder = "value"
            )
        }
    }

    override fun metamorphosis(consoleViewModel: ConsoleViewModel): Block {
        if (selectedType != null) {
            val block = ReturnBlock(value, selectedType!!.toTypes())
            block.uuid = this.id
            return block
        } else {
            throw RuntimeException("type required")
        }
    }
}
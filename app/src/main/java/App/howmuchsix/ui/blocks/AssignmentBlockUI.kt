package App.howmuchsix.ui.blocks

import App.howmuchsix.hms.Blocks.AssignmentBlock
import App.howmuchsix.hms.Blocks.Block
import App.howmuchsix.hms.Blocks.ProgramRunException
import App.howmuchsix.ui.theme.ButtonTextField
import App.howmuchsix.ui.theme.design_elements.BlockYellow
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
import androidx.compose.ui.graphics.Color

class AssignmentBlockUI : BlockUI() {

    private var name by mutableStateOf("")
    private var value by mutableStateOf("")

    override fun metamorphosis(consoleViewModel: ConsoleViewModel): Block {
        if (name.isEmpty()) {
            throw ProgramRunException("Variable name is required", id)
        }
        if (value.isEmpty()) {
            throw ProgramRunException("Value is required", id)
        }

        val block = AssignmentBlock(name.trim(), value)
        block.uuid = this.id
        return block
    }

    @Composable
    override fun Render(modifier: Modifier, viewModel: BlockEditorViewModel?) {
        val error = viewModel?.isBlockWithError(this.id)
        Row(
            modifier = modifier
                .background(
                    if (error == true) Color.Gray else BlockYellow,
                    RoundedCornerShape(size8)
                )
                .padding(size8)
        ) {
            Spacer(Modifier.width(size8))
            ButtonTextField(
                value = name,
                onValueChange = { name = it },
                textStyle = SubTitle1,
                placeholder = "name"
            )

            Spacer(Modifier.width(size8))
            Text(
                text = "=",
                style = SubTitle1,
                modifier = Modifier.align(Alignment.CenterVertically)
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
}
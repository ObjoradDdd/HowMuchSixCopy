package App.howmuchsix.ui.blocks

import App.howmuchsix.hms.Blocks.AssignmentBlock
import App.howmuchsix.hms.Blocks.Block
import App.howmuchsix.localeDataStorage.project.BlockDB
import App.howmuchsix.localeDataStorage.project.blocks.AssignmentBlockBD
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

class AssignmentBlockUI : BlockUI() {

    private var name by mutableStateOf("")
    private var value by mutableStateOf("")

    fun initializeFromBD(variable: String, assignedValue: String) {
        name = variable
        value = assignedValue
    }

    override fun toDBBlock(): BlockDB {
        return AssignmentBlockBD(variable = name, value = value)
    }

    override fun metamorphosis(consoleViewModel: ConsoleViewModel): Block {
        if (name.isEmpty()) {
            throw IllegalArgumentException("Variable name is required")
        }
        if (value.isEmpty()) {
            throw IllegalArgumentException("Value is required")
        }
        return AssignmentBlock(name, value)
    }

    @Composable
    override fun Render(modifier: Modifier, viewModel: BlockEditorViewModel?) {
        Row (
            modifier = modifier
                .background(BlockYellow, RoundedCornerShape(size8))
                .padding(size8)
        ){
            Spacer(Modifier.width(size8))
            ButtonTextField(
                value = name,
                onValueChange = {name = it},
                textStyle = SubTitle1,
                placeholder = "name"
            )

            Spacer(Modifier.width(size8))
            Text(text ="=",
                style = SubTitle1,
                modifier = Modifier.align(Alignment.CenterVertically)
            )
            Spacer(Modifier.width(size8))

            ButtonTextField(
                value = value,
                onValueChange = {value = it},
                textStyle = SubTitle1,
                placeholder = "value"
            )
        }
    }
}
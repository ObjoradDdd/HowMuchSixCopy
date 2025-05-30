package App.howmuchsix.ui.blocks

import App.howmuchsix.hms.Blocks.Block
import App.howmuchsix.hms.Blocks.ReturnBlock
import App.howmuchsix.localeDataStorage.project.BlockDB
import App.howmuchsix.localeDataStorage.project.blocks.ReturnBlockBD
import App.howmuchsix.ui.theme.ButtonTextField
import App.howmuchsix.ui.theme.DropDownMenuTypeSelector
import App.howmuchsix.ui.theme.design_elements.BlockPink
import App.howmuchsix.ui.theme.design_elements.SubTitle1
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

class ReturnBlockUI : BlockUI() {

    private var value by mutableStateOf("")
    private var selectedType by mutableStateOf<_types?>(null)

    @Composable
    override fun Render(modifier: Modifier, viewModel: BlockEditorViewModel?) {
        Row(
            modifier = modifier
                .background(BlockPink, RoundedCornerShape(8.dp))
                .padding(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Spacer(Modifier.width(8.dp))

            Text(
                text = "Return",
                style = SubTitle1,
                modifier = Modifier.align(Alignment.CenterVertically)
            )
            Spacer(Modifier.width(8.dp))
            DropDownMenuTypeSelector(
                selectedType = selectedType,
                onTypeSelected = { selectedType = it}
            )
            Spacer(Modifier.width(8.dp))
            ButtonTextField(
                value = value,
                onValueChange = { value = it },
                textStyle = SubTitle1,
                placeholder = "value"
            )
        }
    }

    fun initializeFromBD(value: String) {
        this.value = value
    }

    override fun toDBBlock(): BlockDB {
        val returnBlock = ReturnBlockBD(value = value)
        return returnBlock
    }


    override fun metamorphosis(consoleViewModel: ConsoleViewModel): Block {
        return ReturnBlock()
    }

}
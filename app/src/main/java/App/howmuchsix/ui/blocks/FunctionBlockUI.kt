package App.howmuchsix.ui.blocks

import App.howmuchsix.hms.Blocks.Block
import App.howmuchsix.hms.Blocks.FunctionBlock
import App.howmuchsix.localeDataStorage.project.BlockDB
import App.howmuchsix.localeDataStorage.project.blocks.FunctionBlockBD
import App.howmuchsix.ui.theme.ButtonTextField
import App.howmuchsix.ui.theme.DropdownMenuFunSelector
import App.howmuchsix.ui.theme.design_elements.*
import App.howmuchsix.viewmodel.BlockEditorViewModel
import App.howmuchsix.viewmodel.ConsoleViewModel
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

class FunctionBlockUI : BlockUI() {
    private var value by mutableStateOf("")
    private var ownerBlockId by mutableStateOf("")
    private var selectedFun by mutableStateOf("")

    fun setOwnerId(id: String) {
        ownerBlockId = id
    }

    @Composable
    override fun Render(modifier: Modifier, viewModel: BlockEditorViewModel?) {
        Column (
            modifier = modifier
                .background(BlockPink, RoundedCornerShape(8.dp))
                .padding(12.dp)
                .defaultMinSize(minWidth = 220.dp, minHeight = 60.dp)
        ){
            Text(
                text = "Call fun",
                style = SubTitle1,
                color = TextWhite
            )
            Spacer(Modifier.height(12.dp))
            Row (
                modifier = Modifier.wrapContentSize(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                DropdownMenuFunSelector(
                    selectedFun = selectedFun,
                    onFunctionSelected = { functionName ->
                        selectedFun = functionName
                    },
                    viewModel = viewModel,
                    placeholderText = "name",
                    buttonBackgroundColor = BlockRed
                )

                Spacer(Modifier.width(12.dp))
                Text(
                    text = ":",
                    style = SubTitle1
                )
                Spacer(Modifier.width(8.dp))
                ButtonTextField(
                    value = value,
                    onValueChange = {value = it},
                    textStyle = SubTitle1,
                    placeholder = "parameters"
                )
            }
        }
    }

    fun initializeFromBD(name: String, arguments : String){
        this.selectedFun = name
        this.value = arguments
    }

    override fun toDBBlock(): BlockDB {
        return FunctionBlockBD(functionName = selectedFun, arguments = value)
    }

    override fun metamorphosis(consoleViewModel: ConsoleViewModel): Block {
        val block = FunctionBlock("$selectedFun($value)")
        block.uuid = this.id
        return block
    }
}
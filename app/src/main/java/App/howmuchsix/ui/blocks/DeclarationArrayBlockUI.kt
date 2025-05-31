package App.howmuchsix.ui.blocks

import App.howmuchsix.hms.Blocks.Block
import App.howmuchsix.hms.Blocks.DeclarationArrayBlock
import App.howmuchsix.hms.Blocks.ProgramRunException
import App.howmuchsix.ui.theme.ButtonTextField
import App.howmuchsix.ui.theme.DropDownMenuTypeSelector
import App.howmuchsix.ui.theme.design_elements.BlockYellow
import App.howmuchsix.ui.theme.design_elements.SubTitle1
import App.howmuchsix.ui.theme.design_elements.TextWhite
import App.howmuchsix.ui.theme.design_elements.size12
import App.howmuchsix.ui.theme.design_elements.size140
import App.howmuchsix.ui.theme.design_elements.size170
import App.howmuchsix.ui.theme.design_elements.size190
import App.howmuchsix.ui.theme.design_elements.size45
import App.howmuchsix.ui.theme.design_elements.size8
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
import androidx.compose.ui.graphics.Color

class DeclarationArrayBlockUI : BlockUI() {

    private var value by mutableStateOf("")
    private var ownerBlockId by mutableStateOf("")
    private var arrName by mutableStateOf("")
    private var arrSize by mutableStateOf("")
    private var selectedType by mutableStateOf<_types?>(null)

    fun setOwnerId(id: String) {
        ownerBlockId = id
    }

    @Composable
    override fun Render(modifier: Modifier, viewModel: BlockEditorViewModel?) {
        val error = viewModel?.isBlockWithError(this.id)
        Column(
            modifier = modifier
                .background(
                    if (error == true) Color.Gray else BlockYellow,
                    RoundedCornerShape(size8)
                )
                .padding(size12)
                .defaultMinSize(minWidth = size190, minHeight = size140)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "Array",
                    style = SubTitle1,
                    color = TextWhite
                )
                Spacer(Modifier.width(size45))
                DropDownMenuTypeSelector(
                    selectedType = selectedType,
                    onTypeSelected = { selectedType = it },
                    error
                )
            }
            Spacer(Modifier.height(size12))
            Row(
                modifier = Modifier.wrapContentSize(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                ButtonTextField(
                    value = arrSize,
                    onValueChange = { newSize ->
                        arrSize = newSize
                        viewModel?.updateFunctionName(ownerBlockId, newSize)
                    },
                    textStyle = SubTitle1,
                    placeholder = "arr size"
                )

                Spacer(Modifier.width(size12))
                ButtonTextField(
                    value = arrName,
                    onValueChange = { newName ->
                        arrName = newName
                        viewModel?.updateFunctionName(ownerBlockId, newName)
                    },
                    textStyle = SubTitle1,
                    placeholder = "arr name"
                )
            }
            Spacer(Modifier.height(size12))
            ButtonTextField(
                value = value,
                onValueChange = { value = it },
                textStyle = SubTitle1,
                placeholder = "components",
                modifier = Modifier.defaultMinSize(minWidth = size170)
            )
        }
    }


    override fun metamorphosis(consoleViewModel: ConsoleViewModel): Block {
        if (selectedType == null) {
            throw ProgramRunException("Type is required", id)
        }
        if (arrName.isEmpty()) {
            throw ProgramRunException("Array name is required", id)
        }

        val types = selectedType!!.toTypes()

        return when {
            value != "" && arrSize != "" ->
                DeclarationArrayBlock(types, arrName, arrSize, value.split(","))

            value == "" && arrSize != "" ->
                DeclarationArrayBlock(types, arrName, arrSize)

            value != "" -> {
                val block = DeclarationArrayBlock(types, arrName, value.split(","))
                block.uuid = id
                block
            }

            else ->
                throw ProgramRunException("Wrong array declaration", id)
        }
    }

}
package App.howmuchsix.ui.blocks

import App.howmuchsix.hms.Blocks.Block
import App.howmuchsix.hms.Blocks.DeclarationArrayBlock
import App.howmuchsix.hms.Blocks.Types
import App.howmuchsix.ui.DropZone
import App.howmuchsix.ui.theme.ButtonTextField
import App.howmuchsix.ui.theme.DropDownMenuTypeSelector
import App.howmuchsix.ui.theme.design_elements.BlockPink
import App.howmuchsix.ui.theme.design_elements.BlockYellow
import App.howmuchsix.ui.theme.design_elements.SubTitle1
import App.howmuchsix.ui.theme.design_elements.TextWhite
import App.howmuchsix.viewmodel.BlockEditorViewModel
import App.howmuchsix.viewmodel.ConsoleViewModel
import App.howmuchsix.viewmodel.BlockType
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
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

class DeclarationArrayBlockUI : BlockUI() {

    private var value by mutableStateOf("")
    private var ownerBlockId by mutableStateOf("")
    private var arrName by mutableStateOf("")
    private var selectedType by mutableStateOf<_types?>(null)

    fun setOwnerId(id: String) {
        ownerBlockId = id
    }

    @Composable
    override fun Render(modifier: Modifier, viewModel: BlockEditorViewModel?) {
        Column (
            modifier = modifier
                .background(BlockYellow, RoundedCornerShape(8.dp))
                .padding(12.dp)
                .defaultMinSize(minWidth = 190.dp, minHeight = 140.dp)
        ) {
            Text(
                text = "Array",
                style = SubTitle1,
                color = TextWhite
            )
            Spacer(Modifier.height(12.dp))
            Row (
                modifier = Modifier.wrapContentSize(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                DropDownMenuTypeSelector(
                    selectedType = selectedType,
                    onTypeSelected = { selectedType = it}
                )

                Spacer(Modifier.width(12.dp))
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
            Spacer(Modifier.height(12.dp))
            ButtonTextField(
                value = value,
                onValueChange = {value = it},
                textStyle = SubTitle1,
                placeholder = "components",
                modifier = Modifier.defaultMinSize(minWidth = 180.dp)
            )
        }
    }


    private var arrayName by mutableStateOf("")
    private var dataType by mutableStateOf<_types?>(null)
    private var length by mutableStateOf(0)
    private var values by mutableStateOf<List<String>?>(null)


    fun initializeFromBD(name: String, type: String, arrayLength: Int, valuesList: List<String>?) {
        arrayName = name
        dataType = try {
            _types.fromString(type)
        } catch (e: IllegalArgumentException) {
            _types.Int
        }
        length = arrayLength
        values = valuesList
    }


    override fun metamorphosis(consoleViewModel: ConsoleViewModel): Block {
        if (dataType == null) {
            throw IllegalArgumentException("Type is required")
        }
        if (arrayName.isEmpty()) {
            throw IllegalArgumentException("Array name is required")
        }

        val types = dataType!!.toTypes()

        return when {
            values != null && length > 0 && values!!.size != length ->
                DeclarationArrayBlock(types, arrayName, length, values!!)
            values != null ->
                DeclarationArrayBlock(types, arrayName, values!!)
            else ->
                DeclarationArrayBlock(types, arrayName, length)
        }
    }

}
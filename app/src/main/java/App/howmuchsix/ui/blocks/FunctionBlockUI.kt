package App.howmuchsix.ui.blocks

import App.howmuchsix.hms.Blocks.Block
import App.howmuchsix.hms.Blocks.FunctionBlock
import App.howmuchsix.ui.DropZone
import App.howmuchsix.ui.theme.ButtonTextField
import App.howmuchsix.ui.theme.DropdownMenuFunSelector
import App.howmuchsix.ui.theme.design_elements.*
import App.howmuchsix.viewmodel.BlockEditorViewModel
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

class FunctionBlockUI : BlockUI() {

    private var ownerBlockId by mutableStateOf("")
    private var selectedFun by mutableStateOf("")

    fun setOwnerId(id: String) {
        ownerBlockId = id
    }

    @Composable
    override fun Render(modifier: Modifier, viewModel: BlockEditorViewModel?) {
        Row (
            modifier = Modifier
                .background(BlockPink, RoundedCornerShape(8.dp))
                .padding(12.dp)
                .defaultMinSize(minWidth = 220.dp, minHeight = 60.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Call fun",
                style = SubTitle1,
                color = TextWhite
            )
            Spacer(Modifier.width(12.dp))

            DropdownMenuFunSelector(
                selectedFun = selectedFun,
                onFunctionSelected = { functionName ->
                    selectedFun = functionName
                },
                viewModel = viewModel,
                placeholderText = "select",
                buttonBackgroundColor = BlockRed,
                textColor = TextWhite
            )

            Spacer(Modifier.width(12.dp))
            Text(
                text = ":",
                style = SubTitle1
            )

            if (viewModel != null) {
                Spacer(Modifier.width(8.dp))
                DropZone(
                    id = "function_params_dropzone_${ownerBlockId}",
                    ownerBlockId = ownerBlockId,
                    viewModel = viewModel,
                    acceptedTypes = listOf(
                        BlockType.Declaration
                    ),
                    placeholder = "parameters",
                    modifier = Modifier.defaultMinSize(minWidth = 80.dp, minHeight = 36.dp)
                )
            }
        }
    }


    override fun metamorphosis(): Block {
       TODO()
    }

}
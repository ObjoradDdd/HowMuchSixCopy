package App.howmuchsix.ui.blocks

import App.howmuchsix.hms.Blocks.Block
import App.howmuchsix.hms.Blocks.PrintBlock
import App.howmuchsix.ui.DropZone
import App.howmuchsix.ui.theme.ButtonTextField
import App.howmuchsix.ui.theme.design_elements.*
import App.howmuchsix.ui.theme.design_elements.SubTitle1
import App.howmuchsix.viewmodel.BlockEditorViewModel
import App.howmuchsix.viewmodel.BlockType
import App.howmuchsix.viewmodel.PlacedBlockUI
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
    private var droppedBlock by mutableStateOf<PlacedBlockUI?>(null)
    private var useDropZone by mutableStateOf(true)
    private var ownerBlockId by mutableStateOf("")

    fun setOwnerId(id: String){
        ownerBlockId = id
    }

    @Composable
    override fun Render(modifier: Modifier, viewModel: BlockEditorViewModel?) {
        Row (
            modifier = modifier
                .background(BlockRed, RoundedCornerShape(8.dp))
                .padding(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ){
            Spacer(Modifier.width(8.dp))

            Text(text = "Print",
                style = SubTitle1,
                modifier = Modifier.align(Alignment.CenterVertically)
            )
            Spacer(Modifier.width(8.dp))

            if (viewModel != null) {
                DropZone(
                    id = "return_dropzone_${ownerBlockId}",
                    ownerBlockId = ownerBlockId,
                    viewModel = viewModel,
                    acceptedTypes = listOf(
                        BlockType.Declaration,
                        BlockType.Assignment
                    ),
                    placeholder = "value"
                )
            }
        }
    }

    override fun metamorphosis(): Block {
        TODO()
    }
}
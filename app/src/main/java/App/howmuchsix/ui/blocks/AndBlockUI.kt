package App.howmuchsix.ui.blocks

import App.howmuchsix.hms.Blocks.Block
import App.howmuchsix.ui.DropZone
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
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

class AndBlockUI : BlockUI() {

    private var ownerBlockId by mutableStateOf("")
    fun setOwnerId(id: String){
        ownerBlockId = id
    }

    @Composable
    override fun Render(modifier: Modifier, viewModel: BlockEditorViewModel?) {
        Column (
            modifier = modifier
                .background(BlockPeach, RoundedCornerShape(8.dp))
                .padding(12.dp)
                .defaultMinSize(minWidth = 80.dp, minHeight = 40.dp)
        ){
            Row (
                modifier = Modifier.wrapContentSize(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (viewModel != null) {
                    DropZone(
                        id = "and_first_dropzone_${ownerBlockId}",
                        ownerBlockId = ownerBlockId,
                        viewModel = viewModel,
                        acceptedTypes = listOf(
                            BlockType.Declaration,
                            BlockType.Assignment
                        ),
                        placeholder = "drop here",
                        modifier = Modifier.defaultMinSize(minWidth = 80.dp, minHeight = 40.dp)
                    )
                }
                Spacer(Modifier.width(12.dp))

                Text(
                    text = "and",
                    style = SubTitle1,
                    color = TextWhite
                )
                Spacer(Modifier.width(12.dp))

                if (viewModel != null) {
                    DropZone(
                        id = "and_second_dropzone_${ownerBlockId}",
                        ownerBlockId = ownerBlockId,
                        viewModel = viewModel,
                        acceptedTypes = listOf(
                            BlockType.Declaration,
                            BlockType.Assignment
                        ),
                        placeholder = "drop here",
                        modifier = Modifier.defaultMinSize(minWidth = 80.dp, minHeight = 40.dp)
                    )
                }
            }
        }
    }


    override fun metamorphosis(): Block {
        TODO("Not yet implemented")
    }
}
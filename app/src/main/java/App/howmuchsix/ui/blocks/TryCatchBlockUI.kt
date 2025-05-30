package App.howmuchsix.ui.blocks

import App.howmuchsix.hms.Blocks.Block
import App.howmuchsix.hms.Blocks.ProgramRunException
import App.howmuchsix.hms.Blocks.TryCatchBlock
import App.howmuchsix.ui.DropZone
import App.howmuchsix.ui.theme.design_elements.BlockRed
import App.howmuchsix.ui.theme.design_elements.SubTitle1
import App.howmuchsix.ui.theme.design_elements.TextWhite
import App.howmuchsix.ui.theme.design_elements.size1
import App.howmuchsix.ui.theme.design_elements.size12
import App.howmuchsix.ui.theme.design_elements.size140
import App.howmuchsix.ui.theme.design_elements.size220
import App.howmuchsix.ui.theme.design_elements.size4
import App.howmuchsix.ui.theme.design_elements.size40
import App.howmuchsix.ui.theme.design_elements.size50
import App.howmuchsix.ui.theme.design_elements.size6
import App.howmuchsix.ui.theme.design_elements.size8
import App.howmuchsix.viewmodel.BlockEditorViewModel
import App.howmuchsix.viewmodel.BlockType
import App.howmuchsix.viewmodel.ConsoleViewModel
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color

class TryCatchBlockUI : BlockUI() {

    private var ownerBlockId by mutableStateOf("")

    var tryBlocks = mutableListOf<BlockUI>()
    var catchBlocks = mutableListOf<BlockUI>()


    fun setOwnerId(id: String) {
        ownerBlockId = id
    }

    @Composable
    override fun Render(modifier: Modifier, viewModel: BlockEditorViewModel?) {
        val error = viewModel?.isBlockWithError(this.id)
        Column(
            modifier = modifier
                .background(if (error == true) Color.Gray else BlockRed, RoundedCornerShape(size8))
                .padding(size12)
                .defaultMinSize(minWidth = size220, minHeight = size140)
        ) {
            Text(
                text = "Try",
                style = SubTitle1,
                color = TextWhite
            )
            Spacer(Modifier.height(size4))
            if (viewModel != null) {
                Box(
                    modifier = Modifier
                        .defaultMinSize(minHeight = size50)
                        .background(TextWhite.copy(alpha = 0.2f), RoundedCornerShape(size4))
                        .border(size1, TextWhite.copy(alpha = 0.1f), RoundedCornerShape(size4))
                        .padding(size6)
                ) {
                    DropZone(
                        id = "try_dropzone_${ownerBlockId}",
                        ownerBlockId = ownerBlockId,
                        viewModel = viewModel,
                        acceptedTypes = listOf(
                            BlockType.Function,
                            BlockType.Declaration,
                            BlockType.Assignment,
                            BlockType.DeclarationArray,
                            BlockType.If,
                            BlockType.For,
                            BlockType.While,
                            BlockType.Continue,
                            BlockType.Print,
                            BlockType.Sleep
                        ),
                        placeholder = "try statements",
                        multipleBlocks = true,
                        modifier = Modifier.defaultMinSize(minWidth = size220, minHeight = size40)
                    )
                }
            }
            Spacer(Modifier.height(size8))
            Text(
                text = "Catch",
                style = SubTitle1,
                color = TextWhite
            )
            Spacer(Modifier.height(size4))
            if (viewModel != null) {
                Box(
                    modifier = Modifier
                        .defaultMinSize(minHeight = size50)
                        .background(TextWhite.copy(alpha = 0.2f), RoundedCornerShape(size4))
                        .border(size1, TextWhite.copy(alpha = 0.1f), RoundedCornerShape(size4))
                        .padding(size6)
                ) {
                    DropZone(
                        id = "catch_dropzone_${ownerBlockId}",
                        ownerBlockId = ownerBlockId,
                        viewModel = viewModel,
                        acceptedTypes = listOf(
                            BlockType.Function,
                            BlockType.Declaration,
                            BlockType.Assignment,
                            BlockType.DeclarationArray,
                            BlockType.If,
                            BlockType.For,
                            BlockType.While,
                            BlockType.Continue,
                            BlockType.Print,
                            BlockType.Sleep
                        ),
                        placeholder = "catch statements",
                        multipleBlocks = true,
                        modifier = Modifier.defaultMinSize(minWidth = size220, minHeight = size40)
                    )
                }
            }
        }
    }

    override fun metamorphosis(consoleViewModel: ConsoleViewModel): Block {
        if (tryBlocks.isEmpty()) {
            throw ProgramRunException("Try body required", id)
        }

        val tryBody = tryBlocks.map { it.metamorphosis(consoleViewModel) }
        val catchBody = catchBlocks.map { it.metamorphosis(consoleViewModel) }

        val block = TryCatchBlock(tryBody, catchBody)
        block.uuid = this.id
        return block
    }

}
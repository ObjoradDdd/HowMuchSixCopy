package App.howmuchsix.ui.blocks

import App.howmuchsix.hms.Blocks.AssignmentBlock
import App.howmuchsix.hms.Blocks.Block
import App.howmuchsix.hms.Blocks.DeclarationBlock
import App.howmuchsix.hms.Blocks.ForBlock
import App.howmuchsix.hms.Blocks.ProgramRunException
import App.howmuchsix.ui.DropZone
import App.howmuchsix.ui.theme.ButtonTextField
import App.howmuchsix.ui.theme.design_elements.BlockOrange
import App.howmuchsix.ui.theme.design_elements.SubTitle1
import App.howmuchsix.ui.theme.design_elements.TextWhite
import App.howmuchsix.ui.theme.design_elements.size1
import App.howmuchsix.ui.theme.design_elements.size12
import App.howmuchsix.ui.theme.design_elements.size140
import App.howmuchsix.ui.theme.design_elements.size220
import App.howmuchsix.ui.theme.design_elements.size270
import App.howmuchsix.ui.theme.design_elements.size4
import App.howmuchsix.ui.theme.design_elements.size40
import App.howmuchsix.ui.theme.design_elements.size50
import App.howmuchsix.ui.theme.design_elements.size6
import App.howmuchsix.ui.theme.design_elements.size60
import App.howmuchsix.ui.theme.design_elements.size73
import App.howmuchsix.ui.theme.design_elements.size8
import App.howmuchsix.viewmodel.BlockEditorViewModel
import App.howmuchsix.viewmodel.BlockType
import App.howmuchsix.viewmodel.ConsoleViewModel
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
import androidx.compose.ui.graphics.Color

class ForBlockUI : BlockUI() {

    private var value by mutableStateOf("")
    private var ownerBlockId by mutableStateOf("")

    var declareBlock: BlockUI? = null
    var assignBlock: BlockUI? = null
    var doBlocks = mutableListOf<BlockUI>()

    fun setOwnerId(id: String) {
        ownerBlockId = id
    }

    @Composable
    override fun Render(modifier: Modifier, viewModel: BlockEditorViewModel?) {
        val error = viewModel?.isBlockWithError(this.id)
        Column(
            modifier = modifier
                .background(
                    if (error == true) Color.Gray else BlockOrange,
                    RoundedCornerShape(size8)
                )
                .padding(size12)
                .defaultMinSize(minWidth = size220, minHeight = size140)
        ) {
            Row(
                modifier = Modifier.wrapContentSize(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "For",
                    style = SubTitle1,
                    color = TextWhite
                )
                Spacer(Modifier.width(size12))

                if (viewModel != null) {
                    DropZone(
                        id = "for_declare_dropzone_${ownerBlockId}",
                        ownerBlockId = ownerBlockId,
                        viewModel = viewModel,
                        acceptedTypes = listOf(
                            BlockType.Declaration,
                            BlockType.Assignment
                        ),
                        placeholder = "declare",
                        modifier = Modifier.defaultMinSize(minWidth = size73, minHeight = size40)
                    )
                    Spacer(Modifier.width(size4))
                    ButtonTextField(
                        value = value,
                        onValueChange = { value = it },
                        textStyle = SubTitle1,
                        placeholder = "condition",
                        modifier = Modifier.defaultMinSize(minWidth = size60)
                    )
                    Spacer(Modifier.width(size4))
                    DropZone(
                        id = "for_assign_dropzone_${ownerBlockId}",
                        ownerBlockId = ownerBlockId,
                        viewModel = viewModel,
                        acceptedTypes = listOf(
                            BlockType.Assignment
                        ),
                        placeholder = "assign",
                        modifier = Modifier.defaultMinSize(minWidth = size73, minHeight = size40)
                    )
                }
            }
            Spacer(Modifier.height(size8))
            Text(
                text = "Do:",
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
                        id = "for_do_dropzone_${ownerBlockId}",
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
                            BlockType.Sleep,
                            BlockType.Break
                        ),
                        placeholder = "do statements",
                        multipleBlocks = true,
                        modifier = Modifier.defaultMinSize(minWidth = size270, minHeight = size40)
                    )
                }
            }
        }
    }


    override fun metamorphosis(consoleViewModel: ConsoleViewModel): Block {
        if (declareBlock == null) {
            throw ProgramRunException("Iterator is required", id)
        }
        if (value.isEmpty()) {
            throw ProgramRunException("Condition is required", id)
        }
        if (assignBlock == null) {
            throw ProgramRunException("Action is required", id)
        }

        val iteratorBlock = declareBlock!!.metamorphosis(consoleViewModel)
        val actionBlock = assignBlock!!.metamorphosis(consoleViewModel) as AssignmentBlock
        val bodyBlocks = doBlocks.map { it.metamorphosis(consoleViewModel) }

        return when (iteratorBlock) {
            is AssignmentBlock -> {
                val block = ForBlock(iteratorBlock, value, actionBlock, bodyBlocks)
                block.uuid = this.id
                block
            }

            is DeclarationBlock -> {
                val block = ForBlock(iteratorBlock, value, actionBlock, bodyBlocks)
                block.uuid = this.id
                block
            }

            else -> throw ProgramRunException(
                "Iterator must be AssignmentBlock or DeclarationBlock",
                id
            )
        }
    }
}
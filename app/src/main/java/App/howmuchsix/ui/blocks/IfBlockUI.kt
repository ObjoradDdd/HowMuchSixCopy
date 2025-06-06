package App.howmuchsix.ui.blocks

import App.howmuchsix.hms.Blocks.Block
import App.howmuchsix.hms.Blocks.IfBlock
import App.howmuchsix.hms.Blocks.ProgramRunException
import App.howmuchsix.ui.DropZone
import App.howmuchsix.ui.theme.ButtonTextField
import App.howmuchsix.ui.theme.design_elements.BlockOrange
import App.howmuchsix.ui.theme.design_elements.SubTitle1
import App.howmuchsix.ui.theme.design_elements.TextWhite
import App.howmuchsix.ui.theme.design_elements.size1
import App.howmuchsix.ui.theme.design_elements.size12
import App.howmuchsix.ui.theme.design_elements.size140
import App.howmuchsix.ui.theme.design_elements.size15
import App.howmuchsix.ui.theme.design_elements.size200
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
import androidx.compose.ui.graphics.Color

class IfBlockUI : BlockUI() {

    private var value by mutableStateOf("")
    private var ownerBlockId by mutableStateOf("")
    private var showElse by mutableStateOf(false)

    var thenBlocks = mutableListOf<BlockUI>()
    var elseBlocks = mutableListOf<BlockUI>()

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
                    text = "If",
                    style = SubTitle1,
                    color = TextWhite
                )
                Spacer(Modifier.width(size15))
                ButtonTextField(
                    value = value,
                    onValueChange = { value = it },
                    textStyle = SubTitle1,
                    placeholder = "conditions",
                    modifier = Modifier.defaultMinSize(minWidth = size200)
                )
            }
            Spacer(Modifier.height(size8))
            Text(
                text = "Then:",
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
                        id = "if_then_dropzone_${ownerBlockId}",
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
                        placeholder = "then statements",
                        multipleBlocks = true,
                        modifier = Modifier.defaultMinSize(minWidth = size220, minHeight = size40)
                    )
                }
            }
            if (showElse && viewModel != null) {
                Spacer(Modifier.height(size8))
                Text(
                    text = "Else:",
                    style = SubTitle1,
                    color = TextWhite
                )
                Spacer(Modifier.height(size4))
                Box(
                    modifier = Modifier
                        .defaultMinSize(minHeight = size50)
                        .background(TextWhite.copy(alpha = 0.2f), RoundedCornerShape(size4))
                        .border(size1, TextWhite.copy(alpha = 0.1f), RoundedCornerShape(size4))
                        .padding(size6)
                ) {
                    DropZone(
                        id = "if_else_dropzone_${ownerBlockId}",
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
                        placeholder = "else statements",
                        multipleBlocks = true,
                        modifier = Modifier.defaultMinSize(minWidth = size220, minHeight = size40)
                    )
                }
            }
            Spacer(Modifier.height(size8))
            TextButton(
                onClick = { showElse = !showElse },
                modifier = Modifier.align(Alignment.End)
            ) {
                Text(
                    text = if (showElse) "Remove else" else "Add else",
                    style = SubTitle1,
                    color = TextWhite
                )
            }
        }
    }


    override fun metamorphosis(consoleViewModel: ConsoleViewModel): Block {
        if (value.isEmpty()) {
            throw ProgramRunException("Condition is required", id)
        }
        if (thenBlocks.isEmpty()) {
            throw ProgramRunException("True action is required", id)
        }

        val trueActionBlocks = thenBlocks.map { it.metamorphosis(consoleViewModel) }
        val falseActionBlocks = if (elseBlocks.isNotEmpty()) {
            elseBlocks.map { it.metamorphosis(consoleViewModel) }
        } else null

        return when {
            falseActionBlocks != null -> {
                val block = IfBlock(value, trueActionBlocks, falseActionBlocks)
                block.uuid = this.id
                block
            }

            else -> {
                val block = IfBlock(value, trueActionBlocks)
                block.uuid = this.id
                block
            }
        }
    }
}
package App.howmuchsix.ui.blocks

import App.howmuchsix.hms.Blocks.Block
import App.howmuchsix.hms.Blocks.WhileBlock
import App.howmuchsix.localeDataStorage.project.BlockDB
import App.howmuchsix.localeDataStorage.project.blocks.WhileBlockBD
import App.howmuchsix.ui.DropZone
import App.howmuchsix.ui.theme.ButtonTextField
import App.howmuchsix.ui.theme.design_elements.BlockOrange
import App.howmuchsix.ui.theme.design_elements.SubTitle1
import App.howmuchsix.ui.theme.design_elements.TextWhite
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
import androidx.compose.ui.unit.dp

class WhileBlockUI : BlockUI() {

    private var value by mutableStateOf("")
    private var ownerBlockId by mutableStateOf("")

    var doBlocks = mutableListOf<BlockUI>()

    fun setOwnerId(id: String) {
        ownerBlockId = id
    }

    @Composable
    override fun Render(modifier: Modifier, viewModel: BlockEditorViewModel?) {
        Column(
            modifier = modifier
                .background(BlockOrange, RoundedCornerShape(8.dp))
                .padding(12.dp)
                .defaultMinSize(minWidth = 220.dp, minHeight = 140.dp)
        ) {
            Row(
                modifier = Modifier.wrapContentSize(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "While",
                    style = SubTitle1,
                    color = TextWhite
                )
                Spacer(Modifier.width(15.dp))
                ButtonTextField(
                    value = value,
                    onValueChange = { value = it },
                    textStyle = SubTitle1,
                    placeholder = "condition",
                    modifier = Modifier.defaultMinSize(minWidth = 200.dp)
                )
            }
            Spacer(Modifier.height(8.dp))
            Text(
                text = "Do:",
                style = SubTitle1,
                color = TextWhite
            )
            Spacer(Modifier.height(4.dp))
            if (viewModel != null) {
                Box(
                    modifier = Modifier
                        .defaultMinSize(minHeight = 50.dp)
                        .background(TextWhite.copy(alpha = 0.2f), RoundedCornerShape(4.dp))
                        .border(1.dp, TextWhite.copy(alpha = 0.1f), RoundedCornerShape(4.dp))
                        .padding(6.dp)
                ) {
                    DropZone(
                        id = "while_do_dropzone_${ownerBlockId}",
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
                        modifier = Modifier.defaultMinSize(minWidth = 220.dp, minHeight = 40.dp)
                    )
                }
            }
        }
    }

    override fun toDBBlock(): BlockDB {
        val whileBlockDB = WhileBlockBD(
            condition = value,
            body = doBlocks.map { it.toDBBlock() }
        )
        return whileBlockDB
    }

    fun initializeFromBD(
        conditionString: String,
        bodyUI: List<BlockUI>
    ) {
        value = conditionString
        doBlocks.clear()
        doBlocks.addAll(bodyUI)
    }

    override fun metamorphosis(consoleViewModel: ConsoleViewModel): Block {
        if (value.isEmpty()) {
            throw IllegalArgumentException("Condition is required")
        }

        val bodyBlocks = doBlocks.map { it.metamorphosis(consoleViewModel) }
        return WhileBlock(value, bodyBlocks)
    }
}
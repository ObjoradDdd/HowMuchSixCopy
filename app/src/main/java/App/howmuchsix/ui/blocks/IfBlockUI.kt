package App.howmuchsix.ui.blocks

import App.howmuchsix.hms.Blocks.Block
import App.howmuchsix.hms.Blocks.IfBlock
import App.howmuchsix.localeDataStorage.project.BlockDB
import App.howmuchsix.localeDataStorage.project.blocks.IfBlockBD
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
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

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
                    text = "If",
                    style = SubTitle1,
                    color = TextWhite
                )
                Spacer(Modifier.width(15.dp))
                ButtonTextField(
                    value = value,
                    onValueChange = { value = it },
                    textStyle = SubTitle1,
                    placeholder = "conditions",
                    modifier = Modifier.defaultMinSize(minWidth = 200.dp)
                )
            }
            Spacer(Modifier.height(8.dp))
            Text(
                text = "Then:",
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
                        modifier = Modifier.defaultMinSize(minWidth = 220.dp, minHeight = 40.dp)
                    )
                }
            }
            if (showElse && viewModel != null) {
                Spacer(Modifier.height(8.dp))
                Text(
                    text = "Else:",
                    style = SubTitle1,
                    color = TextWhite
                )
                Spacer(Modifier.height(4.dp))
                Box(
                    modifier = Modifier
                        .defaultMinSize(minHeight = 50.dp)
                        .background(TextWhite.copy(alpha = 0.2f), RoundedCornerShape(4.dp))
                        .border(1.dp, TextWhite.copy(alpha = 0.1f), RoundedCornerShape(4.dp))
                        .padding(6.dp)
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
                        modifier = Modifier.defaultMinSize(minWidth = 220.dp, minHeight = 40.dp)
                    )
                }
            }
            Spacer(Modifier.height(8.dp))
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

    private var condition by mutableStateOf("")

    override fun toDBBlock(): BlockDB {
        val ifBlock = IfBlockBD(
            condition = if (value != "") value else condition,
            trueAction = thenBlocks.map { it.toDBBlock() },
            falseAction = if (elseBlocks.isNotEmpty()) elseBlocks.map { it.toDBBlock() } else null
        )
        return ifBlock
    }

    fun initializeFromBD(
        conditionString: String,
        trueActionUI: List<BlockUI>,
        elifConditionsList: List<String>?,
        elifActionsUI: List<List<BlockUI>>?,
        falseActionUI: List<BlockUI>?
    ) {
        condition = conditionString
        value = conditionString
        thenBlocks.clear()
        elseBlocks.clear()
        thenBlocks.addAll(trueActionUI)
        falseActionUI?.let { elseBlocks.addAll(it) }
        showElse = elseBlocks.isNotEmpty()
    }

    override fun metamorphosis(consoleViewModel: ConsoleViewModel): Block {
        if (value.isEmpty()) {
            throw IllegalArgumentException("Condition is required")
        }
        if (thenBlocks.isEmpty()) {
            throw IllegalArgumentException("True action is required")
        }

        val trueActionBlocks = thenBlocks.map { it.metamorphosis(consoleViewModel) }
        val falseActionBlocks = if (elseBlocks.isNotEmpty()) {
            elseBlocks.map { it.metamorphosis(consoleViewModel) }
        } else null

        return when {
            falseActionBlocks != null ->
                IfBlock(value, trueActionBlocks, falseActionBlocks)

            else ->
                IfBlock(value, trueActionBlocks)
        }
    }
}
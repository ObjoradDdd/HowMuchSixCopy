package App.howmuchsix.ui.blocks

import App.howmuchsix.hms.Blocks.AssignmentBlock
import App.howmuchsix.hms.Blocks.Block
import App.howmuchsix.hms.Blocks.DeclarationBlock
import App.howmuchsix.hms.Blocks.ForBlock
import App.howmuchsix.localeDataStorage.project.BlockDB
import App.howmuchsix.localeDataStorage.project.blocks.ForBlockBD
import App.howmuchsix.ui.DropZone
import App.howmuchsix.ui.theme.ButtonTextField
import App.howmuchsix.ui.theme.design_elements.*
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
import androidx.compose.foundation.layout.fillMaxWidth
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

class ForBlockUI : BlockUI() {

    private var value by mutableStateOf("")
    private var ownerBlockId by mutableStateOf("")

    var declareBlock: BlockUI? = null
    var assignBlock: BlockUI? = null
    var doBlocks = mutableListOf<BlockUI>()

    fun setOwnerId(id: String){
        ownerBlockId = id
    }

    @Composable
    override fun Render(modifier: Modifier, viewModel: BlockEditorViewModel?) {
        Column (
            modifier = modifier
                .background(BlockOrange, RoundedCornerShape(size8))
                .padding(size12)
                .defaultMinSize(minWidth = size220, minHeight = size140)
        ){
            Row (
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
                        onValueChange = {value = it},
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

    override fun toDBBlock(): BlockDB {
        val forBlockDB = ForBlockBD(
            iterator = declareBlock?.toDBBlock(),
            condition = value,
            action = assignBlock?.toDBBlock(),
            body = doBlocks.map { it.toDBBlock() }
        )
        return forBlockDB
    }

    fun initializeFromBD(
        iteratorUI: BlockUI?,
        conditionString: String,
        actionUI: BlockUI?,
        bodyUI: List<BlockUI>
    ) {
        declareBlock = iteratorUI
        value = conditionString
        assignBlock = actionUI
        doBlocks.clear()
        doBlocks.addAll(bodyUI)
    }

    override fun metamorphosis(consoleViewModel: ConsoleViewModel): Block {
        if (declareBlock == null) {
            throw IllegalArgumentException("Iterator is required")
        }
        if (value.isEmpty()) {
            throw IllegalArgumentException("Condition is required")
        }
        if (assignBlock == null) {
            throw IllegalArgumentException("Action is required")
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
            else -> throw IllegalStateException("Iterator must be AssignmentBlock or DeclarationBlock")
        }
    }
}
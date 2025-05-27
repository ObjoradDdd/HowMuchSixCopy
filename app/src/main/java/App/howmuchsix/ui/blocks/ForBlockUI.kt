package App.howmuchsix.ui.blocks

import App.howmuchsix.hms.Blocks.AssignmentBlock
import App.howmuchsix.hms.Blocks.Block
import App.howmuchsix.hms.Blocks.DeclarationBlock
import App.howmuchsix.hms.Blocks.ForBlock
import App.howmuchsix.ui.DropZone
import App.howmuchsix.ui.theme.ButtonTextField
import App.howmuchsix.ui.theme.DropDownMenuTypeSelector
import App.howmuchsix.ui.theme.design_elements.BlockOrange
import App.howmuchsix.ui.theme.design_elements.BlockYellow
import App.howmuchsix.ui.theme.design_elements.SubTitle1
import App.howmuchsix.ui.theme.design_elements.TextWhite
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

class ForBlockUI : BlockUI() {
    private var ownerBlockId by mutableStateOf("")
    fun setOwnerId(id: String){
        ownerBlockId = id
    }

    @Composable
    override fun Render(modifier: Modifier, viewModel: BlockEditorViewModel?) {
        Column (
            modifier = modifier
                .background(BlockOrange, RoundedCornerShape(8.dp))
                .padding(12.dp)
                .defaultMinSize(minWidth = 220.dp, minHeight = 140.dp)
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
                Spacer(Modifier.width(12.dp))

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
                        modifier = Modifier.defaultMinSize(minWidth = 73.dp, minHeight = 40.dp)
                    )
                    Spacer(Modifier.width(4.dp))
                    DropZone(
                        id = "for_condition_dropzone_${ownerBlockId}",
                        ownerBlockId = ownerBlockId,
                        viewModel = viewModel,
                        acceptedTypes = listOf(
                            BlockType.Assignment
                        ),
                        placeholder = "condition",
                        modifier = Modifier.defaultMinSize(minWidth = 73.dp, minHeight = 40.dp)
                    )
                    Spacer(Modifier.width(4.dp))
                    DropZone(
                        id = "for_assign_dropzone_${ownerBlockId}",
                        ownerBlockId = ownerBlockId,
                        viewModel = viewModel,
                        acceptedTypes = listOf(
                            BlockType.Assignment
                        ),
                        placeholder = "assign",
                        modifier = Modifier.defaultMinSize(minWidth = 73.dp, minHeight = 40.dp)
                    )
                }
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
                        .background(TextWhite.copy(alpha = 0.1f), RoundedCornerShape(4.dp))
                        .border(1.dp, TextWhite.copy(alpha = 0.3f), RoundedCornerShape(4.dp))
                        .padding(6.dp)
                ) {
                    DropZone(
                        id = "for_do_dropzone_${ownerBlockId}",
                        ownerBlockId = ownerBlockId,
                        viewModel = viewModel,
                        acceptedTypes = listOf(
                            BlockType.Declaration,
                            BlockType.Assignment,
                            BlockType.Print,
                            BlockType.Break
                        ),
                        placeholder = "do statements",
                        modifier = Modifier.defaultMinSize(minWidth = 220.dp, minHeight = 40.dp)
                    )
                }
            }
        }
    }


    private var iterator by mutableStateOf<BlockUI?>(null)
    private var condition by mutableStateOf("")
    private var action by mutableStateOf<BlockUI?>(null)
    private var body by mutableStateOf<List<BlockUI>>(emptyList())

    fun initializeFromBD(
        iteratorUI: BlockUI,
        conditionString: String,
        actionUI: BlockUI,
        bodyUI: List<BlockUI>
    ) {
        iterator = iteratorUI
        condition = conditionString
        action = actionUI
        body = bodyUI
    }

    override fun metamorphosis(): Block {
        if (iterator == null) {
            throw IllegalArgumentException("Iterator is required")
        }
        if (condition.isEmpty()) {
            throw IllegalArgumentException("Condition is required")
        }
        if (action == null) {
            throw IllegalArgumentException("Action is required")
        }

        val iteratorBlock = iterator!!.metamorphosis()
        val actionBlock = action!!.metamorphosis() as AssignmentBlock
        val bodyBlocks = body.map { it.metamorphosis() }

        return when (iteratorBlock) {
            is AssignmentBlock -> ForBlock(iteratorBlock, condition, actionBlock, bodyBlocks)
            is DeclarationBlock -> ForBlock(iteratorBlock, condition, actionBlock, bodyBlocks)
            else -> throw IllegalStateException("Iterator must be AssignmentBlock or DeclarationBlock")
        }
    }

}
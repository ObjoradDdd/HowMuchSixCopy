package App.howmuchsix.ui.blocks

import App.howmuchsix.hms.Blocks.Block
import App.howmuchsix.hms.Blocks.IfBlock
import App.howmuchsix.ui.DropZone
import App.howmuchsix.ui.theme.design_elements.BlockOrange
import App.howmuchsix.ui.theme.design_elements.SubTitle1
import App.howmuchsix.ui.theme.design_elements.TextWhite
import App.howmuchsix.viewmodel.BlockEditorViewModel
import App.howmuchsix.viewmodel.BlockType
import android.widget.Space
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
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
import androidx.compose.ui.focus.focusModifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import kotlin.math.round

class IfBlockUI : BlockUI() {

    private var ownerBlockId by mutableStateOf("")
    private var showElse by mutableStateOf(false)

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
                    text = "If",
                    style = SubTitle1,
                    color = TextWhite
                )
                Spacer(Modifier.width(12.dp))

                if (viewModel != null) {
                    DropZone(
                        id = "if_condition_dropzone_${ownerBlockId}",
                        ownerBlockId = ownerBlockId,
                        viewModel = viewModel,
                        acceptedTypes = listOf(
                            BlockType.Declaration,
                            BlockType.Assignment
                        ),
                        placeholder = "condition",
                        modifier = Modifier.defaultMinSize(minWidth = 220.dp, minHeight = 40.dp)
                    )
                }
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
                        //.fillMaxWidth()
                        .defaultMinSize(minHeight = 50.dp)
                        .background(TextWhite.copy(alpha = 0.1f), RoundedCornerShape(4.dp))
                        .border(1.dp, TextWhite.copy(alpha = 0.3f), RoundedCornerShape(4.dp))
                        .padding(6.dp)
                ) {
                    DropZone(
                        id = "if_then_dropzone_${ownerBlockId}",
                        ownerBlockId = ownerBlockId,
                        viewModel = viewModel,
                        acceptedTypes = listOf(
                            BlockType.Declaration,
                            BlockType.Assignment,
                            BlockType.Print
                        ),
                        placeholder = "then statements",
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
                        //.fillMaxWidth()
                        .defaultMinSize(minHeight = 50.dp)
                        .background(TextWhite.copy(alpha = 0.1f), RoundedCornerShape(4.dp))
                        .border(1.dp, TextWhite.copy(alpha = 0.3f), RoundedCornerShape(4.dp))
                        .padding(6.dp)
                ) {
                    DropZone(
                        id = "if_else_dropzone_${ownerBlockId}",
                        ownerBlockId = ownerBlockId,
                        viewModel = viewModel,
                        acceptedTypes = listOf(
                            BlockType.Declaration,
                            BlockType.Assignment,
                            BlockType.Print
                        ),
                        placeholder = "else statements",
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
    private var trueAction by mutableStateOf<List<BlockUI>>(emptyList())
    private var elifConditions by mutableStateOf<List<String>?>(null)
    private var elifActions by mutableStateOf<List<List<BlockUI>>?>(null)
    private var falseAction by mutableStateOf<List<BlockUI>?>(null)

    fun initializeFromBD(
        conditionString: String,
        trueActionUI: List<BlockUI>,
        elifConditionsList: List<String>?,
        elifActionsUI: List<List<BlockUI>>?,
        falseActionUI: List<BlockUI>?
    ) {
        condition = conditionString
        trueAction = trueActionUI
        elifConditions = elifConditionsList
        elifActions = elifActionsUI
        falseAction = falseActionUI
    }

    override fun metamorphosis(): Block {
        if (condition.isEmpty()) {
            throw IllegalArgumentException("Condition is required")
        }
        if (trueAction.isEmpty()) {
            throw IllegalArgumentException("True action is required")
        }

        val trueActionBlocks = trueAction.map { it.metamorphosis() }
        val falseActionBlocks = falseAction?.map { it.metamorphosis() }
        val elifActionsList = elifActions?.map { actionList ->
            actionList.map { it.metamorphosis() }
        }

        return when {
            elifActionsList != null && elifConditions != null && falseActionBlocks != null ->
                IfBlock(condition, trueActionBlocks, elifConditions!!, elifActionsList, falseActionBlocks)
            falseActionBlocks != null ->
                IfBlock(condition, trueActionBlocks, falseActionBlocks)
            else ->
                IfBlock(condition, trueActionBlocks)
        }
    }

}
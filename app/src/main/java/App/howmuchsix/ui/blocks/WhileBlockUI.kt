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
import App.howmuchsix.viewmodel.ConsoleViewModel
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
                    text = "While",
                    style = SubTitle1,
                    color = TextWhite
                )
                Spacer(Modifier.width(12.dp))
                ButtonTextField(
                    value = value,
                    onValueChange = {value = it},
                    textStyle = SubTitle1,
                    placeholder = "condition",
                    modifier = Modifier.defaultMinSize(minWidth = 170.dp)
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
                        .background(TextWhite.copy(alpha = 0.1f), RoundedCornerShape(4.dp))
                        .border(1.dp, TextWhite.copy(alpha = 0.3f), RoundedCornerShape(4.dp))
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


    private var condition by mutableStateOf("")
    private var body by mutableStateOf<List<BlockUI>>(emptyList())


    override fun toDBBlock(): BlockDB {
        val whileBlock = WhileBlockBD(condition = value, body = body.map { it.toDBBlock() })
        return whileBlock
    }


    fun initializeFromBD(conditionString: String, bodyUI: List<BlockUI>) {
        condition = conditionString
        body = bodyUI
    }



    override fun metamorphosis(consoleViewModel: ConsoleViewModel): Block {
        if (condition.isEmpty()) {
            throw IllegalArgumentException("Condition is required")
        }
        if (body.isEmpty()) {
            throw IllegalArgumentException("Body is required")
        }

        val bodyBlocks = body.map { it.metamorphosis(consoleViewModel) }
        return WhileBlock(condition, bodyBlocks)
    }

}
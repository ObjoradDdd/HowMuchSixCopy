package App.howmuchsix.ui.blocks

import App.howmuchsix.hms.Blocks.Block
import App.howmuchsix.hms.Blocks.FunctionDeclarationBlock
import App.howmuchsix.hms.Blocks.Types
import App.howmuchsix.ui.DropZone
import App.howmuchsix.ui.theme.ButtonTextField
import App.howmuchsix.ui.theme.CustomTextField
import App.howmuchsix.ui.theme.DropDownMenuTypeSelector
import App.howmuchsix.ui.theme.design_elements.BlockOrange
import App.howmuchsix.ui.theme.design_elements.BlockPink
import App.howmuchsix.ui.theme.design_elements.InputText
import App.howmuchsix.ui.theme.design_elements.SubTitle1
import App.howmuchsix.ui.theme.design_elements.TextOrange
import App.howmuchsix.ui.theme.design_elements.TextWhite
import App.howmuchsix.viewmodel.BlockEditorViewModel
import App.howmuchsix.viewmodel.ConsoleViewModel
import App.howmuchsix.viewmodel.BlockType
import android.widget.Space
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
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

class FunctionDeclarationBlockUI : BlockUI() {

    private var value by mutableStateOf("")
    private var ownerBlockId by mutableStateOf("")
    private var funName by mutableStateOf("")
    private var selectedType by mutableStateOf<_types?>(null)

    fun setOwnerId(id: String) {
        ownerBlockId = id
    }
    //fun getFunName() : String = funName

    @Composable
    override fun Render(modifier: Modifier, viewModel: BlockEditorViewModel?) {
        Column (
            modifier = modifier
                .background(BlockPink, RoundedCornerShape(8.dp))
                .padding(12.dp)
                .defaultMinSize(minWidth = 220.dp, minHeight = 140.dp)
        ) {
            Text(
                text = "Fun",
                style = SubTitle1,
                color = TextWhite
            )
            Spacer(Modifier.height(12.dp))
            Row (
                modifier = Modifier.wrapContentSize(),
                verticalAlignment = Alignment.CenterVertically
            ) {

                DropDownMenuTypeSelector(
                    selectedType = selectedType,
                    onTypeSelected = { selectedType = it}
                )

                Spacer(Modifier.width(12.dp))
                ButtonTextField(
                    value = funName,
                    onValueChange = { newName ->
                        funName = newName
                        viewModel?.updateFunctionName(ownerBlockId, newName)
                    },
                    textStyle = SubTitle1,
                    placeholder = "name"
                )
                Spacer(Modifier.width(12.dp))
                Text(
                    text = ":",
                    style = SubTitle1
                )
                Spacer(Modifier.width(8.dp))
                ButtonTextField(
                    value = value,
                    onValueChange = {value = it},
                    textStyle = SubTitle1,
                    placeholder = "parameters"
                )
            }
            Spacer(Modifier.height(12.dp))
            if (viewModel != null) {
                Box(
                    modifier = Modifier
                        .defaultMinSize(minHeight = 50.dp)
                        .background(TextWhite.copy(alpha = 0.1f), RoundedCornerShape(4.dp))
                        .border(1.dp, TextWhite.copy(alpha = 0.3f), RoundedCornerShape(4.dp))
                        .padding(6.dp)
                ) {
                    DropZone(
                        id = "function_body_dropzone_${ownerBlockId}",
                        ownerBlockId = ownerBlockId,
                        viewModel = viewModel,
                        acceptedTypes = listOf(
                            BlockType.Declaration,
                            BlockType.Assignment,
                            BlockType.Print,
                            BlockType.Return,
                            BlockType.For,
                            BlockType.If,
                            BlockType.While
                        ),
                        placeholder = "body",
                        modifier = Modifier.defaultMinSize(minWidth = 270.dp, minHeight = 40.dp)
                    )
                }
            }
        }
    }


    private var functionName by mutableStateOf("")
    private var returnType by mutableStateOf("")
    private var argumentsTypes by mutableStateOf<List<String>>(emptyList())
    private var argumentsNames by mutableStateOf<List<String>>(emptyList())
    private var body by mutableStateOf<List<BlockUI>>(emptyList())

    fun initializeFromBD(
        name: String,
        retType: String,
        argTypes: List<String>,
        argNames: List<String>,
        bodyUI: List<BlockUI>
    ) {
        functionName = name
        returnType = retType
        argumentsTypes = argTypes
        argumentsNames = argNames
        body = bodyUI
    }


    private fun stringToTypes(typeString: String): Types {
        return when (typeString.uppercase()) {
            "INT", "INTEGER" -> Types.INT
            "DOUBLE", "FLOAT" -> Types.DOUBLE
            "STRING", "STR" -> Types.STRING
            "BOOLEAN", "BOOL" -> Types.BOOLEAN
            "VOID" -> Types.VOID
            "ARRAY" -> Types.ARRAY
            else -> throw IllegalArgumentException("Unknown type string: $typeString")
        }
    }

    override fun metamorphosis(consoleViewModel: ConsoleViewModel): Block {
        if (functionName.isEmpty()) {
            throw IllegalArgumentException("Function name is required")
        }
        if (returnType.isEmpty()) {
            throw IllegalArgumentException("Return type is required")
        }
        if (argumentsTypes.size != argumentsNames.size) {
            throw IllegalArgumentException("Arguments types and names lists must have the same size")
        }

        val returnTypeEnum = stringToTypes(returnType)
        val argumentsTypesEnum = argumentsTypes.map { stringToTypes(it) }


        val bodyBlocks = body.map { it.metamorphosis(consoleViewModel) }

        return FunctionDeclarationBlock(
            returnTypeEnum,
            functionName,
            argumentsTypesEnum,
            argumentsNames,
            bodyBlocks
        )
    }

}
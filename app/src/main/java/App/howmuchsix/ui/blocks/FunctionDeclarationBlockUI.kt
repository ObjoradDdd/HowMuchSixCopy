package App.howmuchsix.ui.blocks

import App.howmuchsix.hms.Blocks.Block
import App.howmuchsix.hms.Blocks.FunctionDeclarationBlock
import App.howmuchsix.hms.Blocks.Types
import App.howmuchsix.localeDataStorage.project.BlockDB
import App.howmuchsix.localeDataStorage.project.blocks.FunctionDeclarationBlockBD
import App.howmuchsix.ui.DropZone
import App.howmuchsix.ui.theme.ButtonTextField
import App.howmuchsix.ui.theme.DropDownMenuTypeSelector
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
                .background(BlockPink, RoundedCornerShape(size8))
                .padding(size12)
                .defaultMinSize(minWidth = size220, minHeight = size140)
        ) {
            Text(
                text = "Fun",
                style = SubTitle1,
                color = TextWhite
            )
            Spacer(Modifier.height(size12))
            Row (
                modifier = Modifier.wrapContentSize(),
                verticalAlignment = Alignment.CenterVertically
            ) {

                DropDownMenuTypeSelector(
                    selectedType = selectedType,
                    onTypeSelected = { selectedType = it}
                )

                Spacer(Modifier.width(size12))
                ButtonTextField(
                    value = funName,
                    onValueChange = { newName ->
                        funName = newName
                        viewModel?.updateFunctionName(ownerBlockId, newName)
                    },
                    textStyle = SubTitle1,
                    placeholder = "name"
                )
                Spacer(Modifier.width(size12))
                Text(
                    text = ":",
                    style = SubTitle1
                )
                Spacer(Modifier.width(size8))
                ButtonTextField(
                    value = value,
                    onValueChange = {value = it},
                    textStyle = SubTitle1,
                    placeholder = "parameters"
                )
            }
            Spacer(Modifier.height(size12))
            if (viewModel != null) {
                Box(
                    modifier = Modifier
                        .defaultMinSize(minHeight = size50)
                        .background(TextWhite.copy(alpha = 0.2f), RoundedCornerShape(size4))
                        .border(size1, TextWhite.copy(alpha = 0.1f), RoundedCornerShape(size4))
                        .padding(size6)
                ) {
                    DropZone(
                        id = "function_body_dropzone_${ownerBlockId}",
                        ownerBlockId = ownerBlockId,
                        viewModel = viewModel,
                        acceptedTypes = listOf(
                            BlockType.Function,
                            BlockType.Return,
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

                        placeholder = "body",
                        multipleBlocks = true,
                        modifier = Modifier.defaultMinSize(minWidth = size270, minHeight = size40)
                    )
                }
            }
        }
    }

    private var functionName by mutableStateOf("")
    private var returnType by mutableStateOf("")
    private var argumentsTypes by mutableStateOf<List<String>>(emptyList())
    private var argumentsNames by mutableStateOf<List<String>>(emptyList())

    override fun toDBBlock(): BlockDB {
        val functionDeclarationBlock = FunctionDeclarationBlockBD(functionName = functionName, returnType = returnType, argumentsTypes = argumentsTypes, argumentsNames = argumentsNames, body = body.map { it.toDBBlock() } )
        return functionDeclarationBlock
    }

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
        body = mutableListOf()
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
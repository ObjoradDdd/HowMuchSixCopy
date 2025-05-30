package App.howmuchsix.ui.blocks

import App.howmuchsix.hms.Blocks.Block
import App.howmuchsix.hms.Blocks.FunctionDeclarationBlock
import App.howmuchsix.hms.Blocks.Types
import App.howmuchsix.ui.DropZone
import App.howmuchsix.ui.theme.ButtonTextField
import App.howmuchsix.ui.theme.DropDownFunctionMenuTypeSelector
import App.howmuchsix.ui.theme.design_elements.BlockPink
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

class FunctionDeclarationBlockUI : BlockUI() {

    private var value by mutableStateOf("")
    private var ownerBlockId by mutableStateOf("")
    private var funName by mutableStateOf("")
    private var selectedType by mutableStateOf<FunctionTypes?>(null)

    fun setOwnerId(id: String) {
        ownerBlockId = id
    }
    //fun getFunName() : String = funName

    @Composable
    override fun Render(modifier: Modifier, viewModel: BlockEditorViewModel?) {
        Column(
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
            Row(
                modifier = Modifier.wrapContentSize(),
                verticalAlignment = Alignment.CenterVertically
            ) {

                DropDownFunctionMenuTypeSelector(
                    selectedType = selectedType,
                    onTypeSelected = { selectedType = it }
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
                    onValueChange = { value = it },
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


    private fun stringToTypes(typeString: String): Types {
        return when (typeString.uppercase()) {
            "INT", "INTEGER" -> Types.INT
            "DOUBLE", "FLOAT" -> Types.DOUBLE
            "STRING", "STR" -> Types.STRING
            "BOOLEAN", "BOOL" -> Types.BOOLEAN
            "ARRAY" -> Types.ARRAY
            else -> throw RuntimeException("Unknown type string: $typeString")
        }
    }

    private fun parseArguments(value: String): Pair<List<String>, List<String>> {
        if (value.isBlank()) {
            return Pair(emptyList(), emptyList())
        }

        val argumentsTypes = mutableListOf<String>()
        val argumentsNames = mutableListOf<String>()

        val arguments = value.split(",")

        for (argument in arguments) {
            val trimmed = argument.trim()
            if (trimmed.isNotEmpty()) {
                val parts = trimmed.split("\\s+".toRegex())
                if (parts.size >= 2) {
                    argumentsTypes.add(parts[0])
                    argumentsNames.add(parts[1])
                }
            }
        }

        return Pair(argumentsTypes, argumentsNames)
    }

    override fun metamorphosis(consoleViewModel: ConsoleViewModel): Block {
        if (funName == "") {
            throw RuntimeException("Function name is required")
        }
        if (selectedType == null) {
            throw RuntimeException("Return type is required")
        }

        val returnTypeEnum = selectedType!!.toTypes()

        val (argumentsTypes, argumentsNames) = parseArguments(value)

        if (argumentsTypes.size != argumentsNames.size) {
            throw RuntimeException("Arguments types and names lists must have the same size")
        }

        val argumentsTypesEnum = argumentsTypes.map { stringToTypes(it) }

        val bodyBlocks = body.map { it.metamorphosis(consoleViewModel) }

        val block = FunctionDeclarationBlock(
            returnTypeEnum,
            funName,
            argumentsTypesEnum,
            argumentsNames,
            bodyBlocks
        )
        block.uuid = this.id

        return block
    }

}
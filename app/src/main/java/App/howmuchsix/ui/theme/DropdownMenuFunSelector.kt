package App.howmuchsix.ui.theme

import App.howmuchsix.ui.blocks._types
import App.howmuchsix.viewmodel.BlockEditorViewModel
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.Placeholder
import App.howmuchsix.ui.theme.design_elements.*
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Text
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.unit.dp

@Composable
fun DropdownMenuFunSelector(
    selectedFun: String,
    onFunctionSelected: (String) -> Unit,
    viewModel: BlockEditorViewModel?,
    modifier: Modifier = Modifier,
    placeholderText: String = "select",
    buttonBackgroundColor: Color = BlockRed
) {
    var expanded by remember { mutableStateOf(false) }

    Box(modifier = modifier) {
        Button(
            colors = ButtonDefaults.buttonColors(containerColor = BlockRed),
            onClick = { expanded = true },
            shape = RoundedCornerShape(4.dp),
            modifier = Modifier
                .defaultMinSize(minWidth = 90.dp, minHeight = 40.dp)
        ) {
            Text(
                text = if (selectedFun.isNotBlank()) selectedFun
                else placeholderText,
                style = PlaceholderText
            )

        }

        if (viewModel != null) {
            val availableFunctions = viewModel.getAllFunNames()
            DropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false }
            ) {
                if (availableFunctions.isEmpty()) {
                    DropdownMenuItem(
                        text = {
                            Text(
                                text = "No available functions",
                                style = SubTitle1
                            )
                        },
                        onClick = { expanded = false }
                    )
                } else {
                    availableFunctions.forEach { functionName ->
                        DropdownMenuItem(
                            text = {
                                Text(
                                    text = functionName,
                                    style = SubTitle2
                                )
                            },
                            onClick = {
                                onFunctionSelected(functionName)
                                expanded = false
                            }
                        )
                    }
                }
            }
        }
    }
}
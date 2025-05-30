package App.howmuchsix.ui.theme

import App.howmuchsix.ui.theme.design_elements.BlockRed
import App.howmuchsix.ui.theme.design_elements.PlaceholderText
import App.howmuchsix.ui.theme.design_elements.SubTitle1
import App.howmuchsix.ui.theme.design_elements.size4
import App.howmuchsix.ui.theme.design_elements.size40
import App.howmuchsix.ui.theme.design_elements.size90
import App.howmuchsix.viewmodel.BlockEditorViewModel
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier

@Composable
fun DropdownMenuFunSelector(
    selectedFun: String,
    onFunctionSelected: (String) -> Unit,
    viewModel: BlockEditorViewModel?,
    modifier: Modifier = Modifier,
    placeholderText: String = "select",
) {
    var expanded by remember { mutableStateOf(false) }

    Box(modifier = modifier) {
        Button(
            colors = ButtonDefaults.buttonColors(containerColor = BlockRed),
            onClick = { expanded = true },
            shape = RoundedCornerShape(size4),
            modifier = Modifier
                .defaultMinSize(minWidth = size90, minHeight = size40)
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
                                    style = SubTitle1,
                                    color = BlockRed
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
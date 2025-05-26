package App.howmuchsix.ui.theme

import App.howmuchsix.ui.blocks._types
import App.howmuchsix.ui.theme.design_elements.BlockPink
import App.howmuchsix.ui.theme.design_elements.SubTitle1
import App.howmuchsix.ui.theme.design_elements.TextOrange
import androidx.compose.foundation.layout.Box
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color

@Composable
fun DropDownMenuTypeSelector(
    selectedType: _types?,
    onTypeSelected: (_types) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    Box {
        Button(
            colors = ButtonDefaults.buttonColors(containerColor = BlockPink),
            onClick = { expanded = true }
        ) {
            Text(
                text = selectedType?.name ?: "Select",
                style = SubTitle1
            )

        }

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = {expanded = false}
        ) {
            _types.values().forEach{ type ->
                DropdownMenuItem(
                    text = { Text(
                        text = type.name,
                        style = SubTitle1,
                        color = TextOrange
                    ) },
                    onClick = {
                        onTypeSelected(type)
                        expanded = false
                    }
                )
            }
        }
    }
}
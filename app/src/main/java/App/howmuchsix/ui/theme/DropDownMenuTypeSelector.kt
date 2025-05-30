package App.howmuchsix.ui.theme

import App.howmuchsix.ui.blocks._types
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
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ModifierLocalBeyondBoundsLayout
import androidx.compose.ui.unit.dp

@Composable
fun DropDownMenuTypeSelector(
    selectedType: _types?,
    onTypeSelected: (_types) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    Box {
        Button(
            colors = ButtonDefaults.buttonColors(containerColor = BlockRed),
            onClick = { expanded = true },
            shape = RoundedCornerShape(size4),
            modifier = Modifier.defaultMinSize(minWidth = size80, minHeight = size40)
        ) {
            Text(
                text = selectedType?.name ?: "type",
                style = PlaceholderText
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
                        color = BlockRed
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
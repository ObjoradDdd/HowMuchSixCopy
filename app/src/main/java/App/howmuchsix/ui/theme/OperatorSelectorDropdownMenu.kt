package App.howmuchsix.ui.theme

import App.howmuchsix.ui.blocks._types
import App.howmuchsix.ui.blocks.operators
import App.howmuchsix.ui.theme.design_elements.*
import androidx.compose.foundation.layout.Box
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun OperatorSelectorDropdownMenu(
    selectedOperator: operators?,
    onSelected: (operators) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    Box {
        Button(
            colors = ButtonDefaults.buttonColors(containerColor = BlockRed),
            onClick = { expanded = true },
            shape = RoundedCornerShape(4.dp)
        ) {
            Text(
                text = selectedOperator?.symbol ?: "select",
                style = SubTitle1
            )

        }

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = {expanded = false}
        ) {
            operators.values().forEach{operator ->
                DropdownMenuItem(
                    text = { Text(
                        text = operator.symbol,
                        style = SubTitle1,
                        color = BlockRed
                    ) },
                    onClick = {
                        onSelected(operator)
                        expanded = false
                    }
                )
            }
        }
    }
}
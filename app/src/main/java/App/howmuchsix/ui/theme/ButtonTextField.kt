package App.howmuchsix.ui.theme

import App.howmuchsix.ui.theme.design_elements.ButtonText
import App.howmuchsix.ui.theme.design_elements.DarkerBeige
import App.howmuchsix.ui.theme.design_elements.InputText
import App.howmuchsix.ui.theme.design_elements.TextOrange
import App.howmuchsix.ui.theme.design_elements.inter
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.Placeholder
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp


@Composable
fun ButtonTextField(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    placeholder: String,
    isSingleLine: Boolean = true,
    textStyle: TextStyle = InputText,
    borderColor: Color = DarkerBeige,
    focusedBorderColor: Color = TextOrange,
    cornerRadius: Int = 16,
) {
    var isFocused by remember { mutableStateOf(false) }

    Column {
        BasicTextField(
            value = value,
            onValueChange = onValueChange,
            singleLine = isSingleLine,
            textStyle = textStyle,
            modifier = Modifier
                .width(40.dp)
                .clip(RoundedCornerShape(cornerRadius.dp))
                .border(
                    width = 2.dp,
                    color = if (isFocused) focusedBorderColor else borderColor,
                    shape = RoundedCornerShape(cornerRadius.dp)
                )
                .padding(10.dp)
                .onFocusChanged { focusState ->
                    isFocused = focusState.isFocused
                }
        )
    }
}
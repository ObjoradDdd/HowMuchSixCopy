package App.howmuchsix.ui.theme

import App.howmuchsix.ui.theme.design_elements.ButtonText
import App.howmuchsix.ui.theme.design_elements.*
import App.howmuchsix.ui.theme.design_elements.TextOrange
import App.howmuchsix.ui.theme.design_elements.inter
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.Placeholder
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.max

@Composable
fun ButtonTextField(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    placeholder: String,
    isSingleLine: Boolean = true,
    textStyle: TextStyle = InputText,
    borderColor: Color = TextWhite,
    focusedBorderColor: Color = BlockRed,
    cornerRadius: Int = 4,
) {
    var isFocused by remember { mutableStateOf(false) }

    val textMeasurer = rememberTextMeasurer()
    val density = LocalDensity.current
    var measuredWith by remember { mutableStateOf(0.dp) }

    LaunchedEffect(value) {
        val layoutResult = textMeasurer.measure(
            AnnotatedString(value.ifEmpty{placeholder}),
            style = textStyle
        )
        measuredWith = with(density){
            layoutResult.size.width.toDp() + size10
        }
    }

    Column {
        BasicTextField(
            value = value,
            onValueChange = onValueChange,
            singleLine = isSingleLine,
            textStyle = textStyle,
            modifier = modifier
                .defaultMinSize(minWidth = size60, minHeight = size40)
                .width(measuredWith)
                .height(size40)
                .clip(RoundedCornerShape(cornerRadius.dp))
                .border(
                    width = size1,
                    color = if (isFocused) focusedBorderColor else borderColor,
                    shape = RoundedCornerShape(cornerRadius.dp)
                )
                .padding(horizontal = size10, vertical = size4)
                .onFocusChanged { focusState ->
                    isFocused = focusState.isFocused
                },
            decorationBox = {innerTextField ->
                Box(
                    modifier = Modifier.fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ){
                    if (value.isEmpty()){
                        Text(text = placeholder,
                            style = PlaceholderText
                        )
                    }
                    innerTextField()
                }
            }
        )
    }
}
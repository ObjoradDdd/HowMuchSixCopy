package App.howmuchsix.ui.theme

import App.howmuchsix.ui.theme.design_elements.TextWhite
import App.howmuchsix.ui.theme.design_elements.size16
import App.howmuchsix.ui.theme.design_elements.size8
import App.howmuchsix.viewmodel.BlockItemData
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput

@Composable
fun BlockItem(
    block: BlockItemData,
    color: Color,
    isDraggable: Boolean = true,
    onStartDrag: ((BlockItemData, Offset) -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    val baseModifier = if (isDraggable && onStartDrag != null) {
        modifier
            .wrapContentSize()
            .pointerInput(Unit) {
                detectDragGestures(
                    onDragStart = { offset ->
                        onStartDrag(block, offset)
                    },
                    onDrag = { change, _ ->
                        change.consume()
                    },
                    onDragEnd = {},
                    onDragCancel = {}
                )
            }
    } else {
        modifier.wrapContentSize()
    }

    Surface(
        modifier = baseModifier,
        color = color,
        shape = RoundedCornerShape(size8)
    ) {
        Row(
            modifier = Modifier.padding(size16),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = block.label,
                color = TextWhite
            )
        }
    }
}
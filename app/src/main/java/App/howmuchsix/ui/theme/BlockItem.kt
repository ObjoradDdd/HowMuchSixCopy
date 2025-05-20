package App.howmuchsix.ui.theme

import App.howmuchsix.ui.theme.design_elements.TextWhite
import App.howmuchsix.viewmodel.BlockItemData
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import kotlin.math.roundToInt
import App.howmuchsix.viewmodel.*
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Surface

@Composable
fun BlockItem(
    block: BlockItemData,
    color: Color,
    isDraggable: Boolean = true,
    onStartDrag: ((BlockItemData, Offset) -> Unit)? = null,
    modifier: Modifier = Modifier
){
    val baseModifier = if (isDraggable && onStartDrag != null) {
        modifier
            .size(100.dp,60.dp)
            .pointerInput(Unit) {
                detectDragGestures (
                    onDragStart = {offset ->
                        onStartDrag(block, offset)
                    },
                    onDrag = {change, _ ->
                        change.consume()
                    },
                    onDragEnd = {},
                    onDragCancel = {}
                )
            }
    } else {
        modifier.size(100.dp,60.dp)
    }

    Surface (
        modifier = baseModifier,
        color = color,
        shape = RoundedCornerShape(8.dp)
    ){
        Row (
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ){
            Text(
                text = block.label,
                color = TextWhite
            )
        }
    }
}
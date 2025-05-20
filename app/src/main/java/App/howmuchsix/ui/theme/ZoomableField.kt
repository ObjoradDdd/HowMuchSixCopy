package App.howmuchsix.ui.theme

import App.howmuchsix.ui.theme.design_elements.BlockPink
import App.howmuchsix.ui.theme.design_elements.FieldBG
import App.howmuchsix.ui.theme.design_elements.GridColor
import App.howmuchsix.ui.theme.design_elements.TextWhite
import App.howmuchsix.ui.theme.drawGrid
import App.howmuchsix.viewmodel.BlockItemData
import App.howmuchsix.viewmodel.PlacedBlock
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import kotlin.math.roundToInt

@Composable
fun ZoomableField(
    placedBlocks: List<PlacedBlock>,
    onStartDragPlacedBlock: ((String, Offset) -> Unit)? = null,
    modifier: Modifier = Modifier
){
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(FieldBG)
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            drawGrid(spacing = 50f, GridColor)
        }

        placedBlocks.forEach { block->
            Box(
                modifier = Modifier
                    .offset{IntOffset(block.position.x.roundToInt(), block.position.y.roundToInt())}
                    .size(100.dp, 60.dp)
                    .pointerInput(block.id){
                        detectDragGestures(
                            onDragStart = {offset ->
                                onStartDragPlacedBlock?.invoke(block.id, offset + block.position)
                            },
                            onDrag = { change, _ ->
                                change.consume()
                            }
                        )
                    }
            ) {
                BlockItem(
                    block = BlockItemData(block.label),
                    color = BlockPink,
                    isDraggable = false
                )
            }
        }
    }
}

fun DrawScope.drawGrid(spacing: Float, color: Color){
    val step = spacing
    val width = size.width
    val height = size.height

    for (x in 0..(width / step).toInt()) {
        drawLine(
            color = color,
            start = Offset(x * step, 0f),
            end = Offset(x * step, height),
            strokeWidth = 1f
        )
    }

    for (y in 0..(height / step).toInt()) {
        drawLine(
            color = color,
            start = Offset(0f,y * step),
            end = Offset(width, y * step),
            strokeWidth = 1f
        )
    }
}

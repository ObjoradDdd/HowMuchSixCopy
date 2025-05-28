package App.howmuchsix.ui.theme

import App.howmuchsix.ui.blocks.AndBlockUI
import App.howmuchsix.ui.blocks.DeclarationArrayBlockUI
import App.howmuchsix.ui.blocks.ForBlockUI
import App.howmuchsix.ui.blocks.FunctionBlockUI
import App.howmuchsix.ui.blocks.FunctionDeclarationBlockUI
import App.howmuchsix.ui.blocks.IfBlockUI
import App.howmuchsix.ui.blocks.LogicBlockUI
import App.howmuchsix.ui.blocks.OperatorBlockUI
import App.howmuchsix.ui.blocks.OrBlockUI
import App.howmuchsix.ui.blocks.PrintBlockUI
import App.howmuchsix.ui.blocks.ReturnBlockUI
import App.howmuchsix.ui.blocks.WhileBlockUI
import App.howmuchsix.ui.theme.design_elements.BlockPink
import App.howmuchsix.ui.theme.design_elements.FieldBG
import App.howmuchsix.ui.theme.design_elements.GridColor
import App.howmuchsix.ui.theme.design_elements.TextWhite
import App.howmuchsix.ui.theme.drawGrid
import App.howmuchsix.viewmodel.*
import App.howmuchsix.viewmodel.PlacedBlockUI
import android.util.Log
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.DrawStyle
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import kotlin.math.roundToInt

@Composable
fun ZoomableField(
    placedBlocks: List<PlacedBlockUI>,
    nearbyConnectionPoint: NearbyConnection?,
    dropZoneHighlight: DropZoneHighlight?,
    onStartDragPlacedBlock: (String, Offset) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: BlockEditorViewModel = viewModel()
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
                    .wrapContentSize()
                    .pointerInput(block.id){
                        detectDragGestures(
                            onDragStart = {offset ->
                                onStartDragPlacedBlock(block.id, offset + block.position)
                            },
                            onDrag = { change, _ ->
                                change.consume()
                            }
                        )
                    }
            ) {
                when (block.uiBlock) {
                    is PrintBlockUI -> block.uiBlock.setOwnerId(block.id)
                    is IfBlockUI -> block.uiBlock.setOwnerId(block.id)
                    is ForBlockUI -> block.uiBlock.setOwnerId(block.id)
                    is WhileBlockUI -> block.uiBlock.setOwnerId(block.id)
                    is FunctionDeclarationBlockUI -> block.uiBlock.setOwnerId(block.id)
                    is FunctionBlockUI -> block.uiBlock.setOwnerId(block.id)
                    is DeclarationArrayBlockUI -> block.uiBlock.setOwnerId(block.id)
                    is AndBlockUI -> block.uiBlock.setOwnerId(block.id)
                    is OrBlockUI -> block.uiBlock.setOwnerId(block.id)
                    is OperatorBlockUI -> block.uiBlock.setOwnerId(block.id)
                    is LogicBlockUI -> block.uiBlock.setOwnerId(block.id)
                }
                block.uiBlock.Render(modifier, viewModel)
            }
        }

        Canvas(modifier = Modifier.fillMaxSize()) {
            nearbyConnectionPoint?.let { nearbyConn ->
                val targetBlock = placedBlocks.find{ it.id == nearbyConn.ownerBlockId }

                targetBlock?.let { block ->
                    val absolutePosition = block.position + nearbyConn.connectionPoint.position
                    drawCircle(
                        color = Color.Magenta,
                        radius = 10f,
                        center = absolutePosition
                    )
                }
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
package App.howmuchsix.ui.theme

import App.howmuchsix.ui.blocks.DeclarationArrayBlockUI
import App.howmuchsix.ui.blocks.ForBlockUI
import App.howmuchsix.ui.blocks.FunctionBlockUI
import App.howmuchsix.ui.blocks.FunctionDeclarationBlockUI
import App.howmuchsix.ui.blocks.IfBlockUI
import App.howmuchsix.ui.blocks.PrintBlockUI
import App.howmuchsix.ui.blocks.ReturnBlockUI
import App.howmuchsix.ui.blocks.SleepBlockUI
import App.howmuchsix.ui.blocks.Try_catchBlockUI
import App.howmuchsix.ui.blocks.WhileBlockUI
import App.howmuchsix.ui.theme.design_elements.*
import App.howmuchsix.ui.theme.drawGrid
import App.howmuchsix.viewmodel.*
import App.howmuchsix.viewmodel.PlacedBlockUI
import android.util.Log
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.DrawStyle
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import kotlin.math.roundToInt
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.drawscope.DrawTransform
import androidx.compose.ui.graphics.graphicsLayer

@Composable
fun ZoomableField(
    placedBlocks: List<PlacedBlockUI>,
    nearbyConnectionPoint: NearbyConnection?,
    dropZoneHighlight: DropZoneHighlight?,
    onStartDragPlacedBlock: (String, Offset) -> Unit,
    onTransformChange: (scale: Float, offset: Offset) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: BlockEditorViewModel = viewModel()
){
    var scale by remember { mutableFloatStateOf(1f) }
    var offset by remember { mutableStateOf(Offset.Zero) }

    val minScale = 0.3f
    val maxScale = 3f

    LaunchedEffect(scale, offset) {
        onTransformChange(scale, offset)
        viewModel.updateFieldTransform(scale, offset)
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(FieldBG)
            .pointerInput(Unit) {
                detectTransformGestures(
                    panZoomLock = false
                ) { _, pan, zoom, _ ->
                    val newScale = (scale * zoom).coerceIn(minScale, maxScale)

                    val newOffsetX = offset.x + pan.x
                    val newOffsetY = offset.y + pan.y

                    val constrainedOffset = if (newScale > 1f) {
                        val maxX = size.width * (newScale - 1) / 2
                        val maxY = size.height * (newScale - 1) / 2
                        Offset(
                            x = newOffsetX.coerceIn(-maxX, maxX),
                            y = newOffsetY.coerceIn(-maxY, maxY)
                        )
                    } else {
                        val buffer = 50f
                        Offset(
                            x = newOffsetX.coerceIn(-buffer, buffer),
                            y = newOffsetY.coerceIn(-buffer, buffer)
                        )
                    }

                    scale = newScale
                    offset = constrainedOffset
                }
            }
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .graphicsLayer(
                    scaleX = scale,
                    scaleY = scale,
                    translationX = offset.x,
                    translationY = offset.y
                )
        ) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                drawGrid(spacing = 50f, GridColor)
            }

            placedBlocks.forEach { block->
                Box(
                    modifier = Modifier
                        .offset{IntOffset(block.position.x.roundToInt(), block.position.y.roundToInt())}
                        .wrapContentSize()
                        .onGloballyPositioned { coordinates ->
                            val size = coordinates.size
                            viewModel.updateBlockSize(
                                block.id,
                                Size(size.width.toFloat(), size.height.toFloat()))
                        }
                        .pointerInput(block.id){
                            detectDragGestures(
                                onDragStart = { offset ->
                                    onStartDragPlacedBlock(block.id, offset + block.position)
                                },
                                onDrag = { change, _ ->
                                    change.consume()
                                }
                            )
                        }
                ) {
                    when (block.uiBlock) {
                        is IfBlockUI -> block.uiBlock.setOwnerId(block.id)
                        is ForBlockUI -> block.uiBlock.setOwnerId(block.id)
                        is WhileBlockUI -> block.uiBlock.setOwnerId(block.id)
                        is FunctionDeclarationBlockUI -> block.uiBlock.setOwnerId(block.id)
                        is FunctionBlockUI -> block.uiBlock.setOwnerId(block.id)
                        is DeclarationArrayBlockUI -> block.uiBlock.setOwnerId(block.id)
                        is Try_catchBlockUI -> block.uiBlock.setOwnerId(block.id)
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
                            radius = 12f,
                            center = absolutePosition
                        )
                    }
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
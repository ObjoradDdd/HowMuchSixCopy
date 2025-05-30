package App.howmuchsix.ui.theme

import App.howmuchsix.ui.blocks.DeclarationArrayBlockUI
import App.howmuchsix.ui.blocks.ForBlockUI
import App.howmuchsix.ui.blocks.FunctionBlockUI
import App.howmuchsix.ui.blocks.FunctionDeclarationBlockUI
import App.howmuchsix.ui.blocks.IfBlockUI
import App.howmuchsix.ui.blocks.PrintBlockUI
import App.howmuchsix.ui.blocks.ReturnBlockUI
import App.howmuchsix.ui.blocks.SleepBlockUI
import App.howmuchsix.ui.blocks.WhileBlockUI
import App.howmuchsix.ui.theme.design_elements.BlockPink
import App.howmuchsix.ui.theme.design_elements.FieldBG
import App.howmuchsix.ui.theme.design_elements.GridColor
import App.howmuchsix.ui.theme.design_elements.TextWhite
import App.howmuchsix.viewmodel.*
import App.howmuchsix.viewmodel.PlacedBlockUI
import android.util.Log
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
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

    val minScale = 0.1f
    val maxScale = 5f


    val virtualFieldSize = 10000f

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
                ) { centroid, pan, zoom, _ ->
                    val newScale = (scale * zoom).coerceIn(minScale, maxScale)


                    val newOffset = if (zoom != 1f) {
                        val screenCenter = Offset(size.width / 2f, size.height / 2f)
                        val zoomCenter = centroid
                        val deltaScale = newScale / scale

                        val offsetFromCenter = offset - screenCenter
                        val newOffsetFromCenter = offsetFromCenter * deltaScale
                        val deltaFromZoomCenter = (zoomCenter - screenCenter) * (1 - deltaScale)

                        newOffsetFromCenter + screenCenter + deltaFromZoomCenter + pan
                    } else {
                        offset + pan
                    }


                    val maxOffsetX = virtualFieldSize * newScale
                    val maxOffsetY = virtualFieldSize * newScale
                    val minOffsetX = -virtualFieldSize * newScale
                    val minOffsetY = -virtualFieldSize * newScale

                    val constrainedOffset = Offset(
                        x = newOffset.x.coerceIn(minOffsetX, maxOffsetX),
                        y = newOffset.y.coerceIn(minOffsetY, maxOffsetY)
                    )

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
                drawExtendedGrid(
                    spacing = 50f,
                    color = GridColor,
                    offset = offset,
                    scale = scale,
                    virtualSize = virtualFieldSize
                )
            }


            placedBlocks.forEach { block ->
                Box(
                    modifier = Modifier
                        .offset {
                            IntOffset(
                                block.position.x.roundToInt(),
                                block.position.y.roundToInt()
                            )
                        }
                        .wrapContentSize()
                        .onGloballyPositioned { coordinates ->
                            val size = coordinates.size
                            viewModel.updateBlockSize(
                                block.id,
                                Size(size.width.toFloat(), size.height.toFloat())
                            )
                        }
                        .pointerInput(block.id) {
                            detectDragGestures(
                                onDragStart = { dragOffset ->

                                    val fieldPosition = screenToFieldCoords(
                                        dragOffset + block.position,
                                        offset,
                                        scale
                                    )
                                    onStartDragPlacedBlock(block.id, fieldPosition)
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
                    }
                    block.uiBlock.Render(Modifier.wrapContentSize(), viewModel)
                }
            }


            Canvas(modifier = Modifier.fillMaxSize()) {
                nearbyConnectionPoint?.let { nearbyConn ->
                    val targetBlock = placedBlocks.find { it.id == nearbyConn.ownerBlockId }
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


fun DrawScope.drawExtendedGrid(
    spacing: Float,
    color: Color,
    offset: Offset,
    scale: Float,
    virtualSize: Float
) {
    val step = spacing
    val width = size.width
    val height = size.height


    val visibleLeft = (-offset.x) / scale
    val visibleTop = (-offset.y) / scale
    val visibleRight = visibleLeft + width / scale
    val visibleBottom = visibleTop + height / scale


    val extendedLeft = visibleLeft - step * 2
    val extendedTop = visibleTop - step * 2
    val extendedRight = visibleRight + step * 2
    val extendedBottom = visibleBottom + step * 2


    val startX = (extendedLeft / step).toInt() * step
    val endX = (extendedRight / step).toInt() * step
    var x = startX
    while (x <= endX) {
        if (x >= -virtualSize && x <= virtualSize) {
            drawLine(
                color = color,
                start = Offset(x, extendedTop.coerceAtLeast(-virtualSize)),
                end = Offset(x, extendedBottom.coerceAtMost(virtualSize)),
                strokeWidth = 1f
            )
        }
        x += step
    }

    val startY = (extendedTop / step).toInt() * step
    val endY = (extendedBottom / step).toInt() * step
    var y = startY
    while (y <= endY) {
        if (y >= -virtualSize && y <= virtualSize) {
            drawLine(
                color = color,
                start = Offset(extendedLeft.coerceAtLeast(-virtualSize), y),
                end = Offset(extendedRight.coerceAtMost(virtualSize), y),
                strokeWidth = 1f
            )
        }
        y += step
    }
}


fun screenToFieldCoords(screenPosition: Offset, fieldOffset: Offset, fieldScale: Float): Offset {
    return (screenPosition - fieldOffset) / fieldScale
}


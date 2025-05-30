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
import App.howmuchsix.ui.theme.design_elements.FieldBG
import App.howmuchsix.ui.theme.design_elements.GridColor
import App.howmuchsix.viewmodel.*
import App.howmuchsix.viewmodel.PlacedBlockUI
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
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
    onTransformChange: (scale: Float, offset: Offset) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: BlockEditorViewModel = viewModel()
){
    val horizontalScrollState = rememberScrollState(initial = 1000)
    val verticalScrollState = rememberScrollState(initial = 1000)
    val density = LocalDensity.current

    val fieldSizeDp = 2000.dp
    val fieldSizePx = with(density) { fieldSizeDp.toPx() }
    // Убираем centerOffset полностью

    val scale = 1f

    val scrollOffset = Offset(
        x = -horizontalScrollState.value.toFloat(),
        y = -verticalScrollState.value.toFloat()
    )

    LaunchedEffect(scrollOffset) {
        onTransformChange(scale, scrollOffset)
        viewModel.updateFieldTransform(scale, scrollOffset)
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(FieldBG)
            .horizontalScroll(horizontalScrollState)
            .verticalScroll(verticalScrollState)
    ) {
        Box(
            modifier = Modifier.size(fieldSizeDp, fieldSizeDp)
        ) {
            // Рисование сетки
            Canvas(modifier = Modifier.size(fieldSizeDp, fieldSizeDp)) {
                drawGrid(spacing = 50f, color = GridColor)
            }

            // Отрисовка блоков - теперь просто используем position напрямую
            placedBlocks.forEach { block ->
                Box(
                    modifier = Modifier
                        .offset {
                            // Блоки размещаются по их прямым координатам
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
                                    // Передаем текущую позицию блока
                                    onStartDragPlacedBlock(block.id, block.position)
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

            // Отрисовка точек соединения
            Canvas(modifier = Modifier.size(fieldSizeDp, fieldSizeDp)) {
                nearbyConnectionPoint?.let { nearbyConn ->
                    val targetBlock = placedBlocks.find { it.id == nearbyConn.ownerBlockId }
                    targetBlock?.let { block ->
                        // Убираем centerOffset из расчета позиции точки соединения
                        val absolutePosition = Offset(
                            x = block.position.x + nearbyConn.connectionPoint.position.x,
                            y = block.position.y + nearbyConn.connectionPoint.position.y
                        )
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

fun DrawScope.drawGrid(spacing: Float, color: Color) {
    val width = size.width
    val height = size.height

    // Вертикальные линии
    var x = 0f
    while (x <= width) {
        drawLine(
            color = color,
            start = Offset(x, 0f),
            end = Offset(x, height),
            strokeWidth = 1f
        )
        x += spacing
    }

    // Горизонтальные линии
    var y = 0f
    while (y <= height) {
        drawLine(
            color = color,
            start = Offset(0f, y),
            end = Offset(width, y),
            strokeWidth = 1f
        )
        y += spacing
    }
}

fun screenToFieldCoords(screenPosition: Offset, fieldOffset: Offset, fieldScale: Float): Offset {
    return (screenPosition - fieldOffset) / fieldScale
}
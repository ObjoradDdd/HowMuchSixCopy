package App.howmuchsix.ui.theme.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import App.howmuchsix.R
import App.howmuchsix.viewmodel.BlockEditorViewModel
import App.howmuchsix.viewmodel.*
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ModifierLocalBeyondBoundsLayout
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.modifier.modifierLocalConsumer
import androidx.compose.ui.unit.IntOffset
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewmodel.viewModelFactory
import kotlin.math.roundToInt
import App.howmuchsix.viewmodel.BlockItemData
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.lifecycle.viewmodel.compose.viewModel
import App.howmuchsix.ui.theme.design_elements.*
import App.howmuchsix.ui.theme.BlockCategory
import App.howmuchsix.ui.theme.Project
import App.howmuchsix.ui.theme.BlockItem
import App.howmuchsix.ui.theme.BlockPanel
import App.howmuchsix.ui.theme.BottomMenuContent
import androidx.compose.material3.FabPosition
import App.howmuchsix.ui.theme.ZoomableField
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.ui.input.pointer.PointerEventPass
import androidx.compose.ui.input.pointer.PointerEventType
import androidx.compose.ui.input.pointer.PointerInputScope

@Composable
fun WorkingScreen(viewModel: BlockEditorViewModel = viewModel()){
    var isBlockPanelVisible by remember { mutableStateOf(false) }

    val placedBlocks = viewModel.placedBlocks
    val draggedBlock = viewModel.draggedBlock
    val dragPosition= viewModel.dragPosition
    val draggedPlacedBlockId = viewModel.draggedPlacedBlockId

    val blockCategories = listOf(
        BlockCategory(
            name = "Variables",
            blocks = listOf(
                BlockItemData(
                    type = BlockType.Declaration,
                    label = "Declare",
                    color = BlockYellow
                ),
                BlockItemData(
                    type = BlockType.Assignment,
                    label = "Assign",
                    color = BlockYellow
                )
            ),
            blockColor = BlockYellow
        ),
        BlockCategory(
            name = "Logic",
            blocks = listOf(
                BlockItemData(
                    type = BlockType.Break,
                    label = "Break",
                    color = BlockOrange
                ),
                BlockItemData(
                    type = BlockType.Return,
                    label = "Return",
                    color = BlockOrange
                )
            ),
            blockColor = BlockOrange
        ),
        BlockCategory(
            name = "Action",
            blocks = listOf(
                BlockItemData(
                    type = BlockType.Print,
                    label = "Print",
                    color = BlockPink
                )
            ),
            blockColor = BlockPink
        )
    )

    Box(
        modifier = Modifier
            .wrapContentSize()
            .pointerInput(Unit){
                detectPointerPositionChanges {position ->
                    if (viewModel.isDragging) {
                        viewModel.updatePosition(position)
                    }
                }
            }
            .pointerInput(Unit){
                awaitPointerEventScope {
                    while (true) {
                        val event = awaitPointerEvent(PointerEventPass.Main)
                        if (event.type == PointerEventType.Release && viewModel.isDragging) {
                            viewModel.stopDragging(placeOnField = true)
                        }
                    }
                }
            }
    ) {
        ZoomableField(
            placedBlocks = placedBlocks.filter { it.id != draggedPlacedBlockId },
            onStartDragPlacedBlock = {blockId, offset ->
                viewModel.startDraggingPlacedBlock(blockId, offset)
            }
        )

        draggedBlock?.let { block ->
            Box(
                modifier = Modifier
                    .offset{IntOffset(dragPosition.x.roundToInt(), dragPosition.y.roundToInt())}
                    .wrapContentSize()
            ) {
                BlockItem(
                    block = block,
                    color = block.color,
                    isDraggable = false
                )
            }
        }

        AnimatedVisibility(
            visible = isBlockPanelVisible,
            enter = slideInVertically(initialOffsetY = { it }) + fadeIn(),
            exit = slideOutVertically(targetOffsetY = { it }) + fadeOut(),
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 88.dp)
        ) {
            BlockPanel(
                categories = blockCategories,
                onBlockStartDrag = { block, offset ->
                    viewModel.startDragging(block, offset)
                    isBlockPanelVisible = false
                }
            )
        }

        BottomMenu(
            items = listOf(
                BottomMenuContent("Blocks", R.drawable.ic_blocks),
                BottomMenuContent("Console", R.drawable.ic_console),
                BottomMenuContent("Save", R.drawable.ic_save),
                BottomMenuContent("Home", R.drawable.ic_home),
            ),
            onItemClick = { item ->
                when (item.title) {
                    "Blocks" -> isBlockPanelVisible = !isBlockPanelVisible
                }
            },
            modifier = Modifier.align(Alignment.BottomCenter)
        )
    }
}

@Composable
fun BottomMenu(
    items: List<BottomMenuContent>,
    modifier: Modifier = Modifier,
    onItemClick: (BottomMenuContent) -> Unit
) {
    Surface(
        modifier = modifier
            .padding(horizontal = 16.dp, vertical = 12.dp)
            .fillMaxWidth()
            .height(72.dp),
        color = TextOrange,
        shape = RoundedCornerShape(20.dp),
        shadowElevation = 4.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            items.forEach { item ->
                Column(
                    modifier = Modifier
                        .clickable { onItemClick(item) }
                        .padding(8.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        painter = painterResource(id = item.iconId),
                        contentDescription = item.title,
                        tint = Color.White,
                        modifier = Modifier
                            .size(40.dp)
                    )
                }
            }
        }
    }
}

suspend fun PointerInputScope.detectPointerPositionChanges(onPositionChanged: (Offset) -> Unit){
    awaitPointerEventScope {
        while (true) {
            val event = awaitPointerEvent(PointerEventPass.Main)
            if (event.changes.isNotEmpty()) {
                onPositionChanged(event.changes.first().position)
            }
        }
    }
}
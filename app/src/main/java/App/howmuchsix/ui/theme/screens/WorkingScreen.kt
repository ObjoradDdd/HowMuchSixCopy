package App.howmuchsix.ui.theme.screens

import App.howmuchsix.R
import App.howmuchsix.navigation.Screens
import App.howmuchsix.ui.console.Console
import App.howmuchsix.ui.theme.BlockCategory
import App.howmuchsix.ui.theme.BlockItem
import App.howmuchsix.ui.theme.BlockPanel
import App.howmuchsix.ui.theme.BottomMenuContent
import App.howmuchsix.ui.theme.ZoomableField
import App.howmuchsix.ui.theme.design_elements.BlockOrange
import App.howmuchsix.ui.theme.design_elements.BlockPeach
import App.howmuchsix.ui.theme.design_elements.BlockPink
import App.howmuchsix.ui.theme.design_elements.BlockRed
import App.howmuchsix.ui.theme.design_elements.BlockYellow
import App.howmuchsix.ui.theme.design_elements.TextOrange
import App.howmuchsix.ui.theme.design_elements.size12
import App.howmuchsix.ui.theme.design_elements.size16
import App.howmuchsix.ui.theme.design_elements.size20
import App.howmuchsix.ui.theme.design_elements.size4
import App.howmuchsix.ui.theme.design_elements.size40
import App.howmuchsix.ui.theme.design_elements.size72
import App.howmuchsix.ui.theme.design_elements.size8
import App.howmuchsix.ui.theme.design_elements.size88
import App.howmuchsix.viewmodel.BlockEditorViewModel
import App.howmuchsix.viewmodel.BlockItemData
import App.howmuchsix.viewmodel.BlockType
import App.howmuchsix.viewmodel.ConsoleViewModel
import App.howmuchsix.viewmodel.InterpreterViewModel
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.PointerEventPass
import androidx.compose.ui.input.pointer.PointerEventType
import androidx.compose.ui.input.pointer.PointerInputScope
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.IntOffset
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import kotlin.math.roundToInt

@Composable
fun WorkingScreen(
    viewModel: BlockEditorViewModel = viewModel(),
    projectId: String,
    navController: NavController,
    consoleViewModel: ConsoleViewModel = viewModel()
) {
    var isBlockPanelVisible by remember { mutableStateOf(false) }
    var isConsoleVisible by remember { mutableStateOf(false) }

    var fieldScale by remember { mutableFloatStateOf(1f) }
    var fieldOffset by remember { mutableStateOf(Offset.Zero) }

    val interpreterViewModel = remember {
        InterpreterViewModel(consoleViewModel, viewModel)
    }

    val placedBlocks = viewModel.placedBlocks
    val draggedBlock = viewModel.draggedBlock
    val dragPosition = viewModel.dragPosition
    val draggedPlacedBlockId = viewModel.draggedPlacedBlockId
    val nearbyConnectionPoint = viewModel.nearbyConnectionPoint
    val dropZoneHighlight = viewModel.dropZoneHighlight

    val blockCategories = listOf(
        BlockCategory(
            name = "Events",
            blocks = listOf(
                BlockItemData(
                    type = BlockType.StartProgram,
                    label = "Start program",
                    color = BlockPeach
                )
            ),
            blockColor = BlockPeach
        ),
        BlockCategory(
            name = "Functions",
            blocks = listOf(
                BlockItemData(
                    type = BlockType.FunctionDeclaration,
                    label = "Declare function",
                    color = BlockPink
                ),
                BlockItemData(
                    type = BlockType.Function,
                    label = "Call function",
                    color = BlockPink
                ),
                BlockItemData(
                    type = BlockType.Return,
                    label = "Return",
                    color = BlockPink
                )
            ),
            blockColor = BlockPink
        ),
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
                ),
                BlockItemData(
                    type = BlockType.DeclarationArray,
                    label = "Declare array",
                    color = BlockYellow
                )
            ),
            blockColor = BlockYellow
        ),
        BlockCategory(
            name = "Logic",
            blocks = listOf(
                BlockItemData(
                    type = BlockType.If,
                    label = "If",
                    color = BlockOrange
                ),
                BlockItemData(
                    type = BlockType.For,
                    label = "For",
                    color = BlockOrange
                ),
                BlockItemData(
                    type = BlockType.While,
                    label = "While",
                    color = BlockOrange
                ),
                BlockItemData(
                    type = BlockType.Break,
                    label = "Break",
                    color = BlockOrange
                ),
                BlockItemData(
                    type = BlockType.Continue,
                    label = "Continue",
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
                    color = BlockRed
                ),
                BlockItemData(
                    type = BlockType.Sleep,
                    label = "Sleep",
                    color = BlockRed
                ),
                BlockItemData(
                    type = BlockType.Try_catch,
                    label = "Try / catch",
                    color = BlockRed
                )
            ),
            blockColor = BlockRed
        )
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .pointerInput(Unit) {
                detectPointerPositionChanges { position ->
                    if (viewModel.isDragging) {
                        viewModel.updatePosition(position)
                    }
                }
            }
            .pointerInput(Unit) {
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
            modifier = Modifier.fillMaxSize(),
            placedBlocks = placedBlocks.filter { it.id != draggedPlacedBlockId },
            nearbyConnectionPoint = nearbyConnectionPoint,
            onStartDragPlacedBlock = { blockId, offset ->
                viewModel.startDraggingPlacedBlock(blockId, offset)
            },
            onTransformChange = { scale, offset ->
                fieldScale = scale
                fieldOffset = offset
            },
            viewModel = viewModel
        )

        draggedBlock?.let { block ->
            val draggedPlacedBlock = draggedPlacedBlockId?.let { id ->
                placedBlocks.find { it.id == id }
            }
            Box(
                modifier = Modifier
                    .offset {
                        IntOffset(
                            viewModel.dragScreenPosition.x.roundToInt(),
                            viewModel.dragScreenPosition.y.roundToInt()
                        )
                    }
                    .wrapContentSize()
            ) {
                if (draggedPlacedBlock != null) {
                    draggedPlacedBlock.uiBlock.Render(Modifier, viewModel)
                } else {
                    BlockItem(
                        block = block,
                        color = block.color,
                        isDraggable = false
                    )
                }
            }
        }
        AnimatedVisibility(
            visible = isBlockPanelVisible,
            enter = slideInVertically(initialOffsetY = { it }) + fadeIn(),
            exit = slideOutVertically(targetOffsetY = { it }) + fadeOut(),
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = size88)
        ) {
            BlockPanel(
                categories = blockCategories,
                onBlockStartDrag = { block, offset ->
                    viewModel.startDragging(block, offset)
                    isBlockPanelVisible = false
                }
            )
        }

        AnimatedVisibility(
            visible = isConsoleVisible,
            enter = slideInVertically(initialOffsetY = { it }) + fadeIn(),
            exit = slideOutVertically(targetOffsetY = { it }) + fadeOut(),
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = size88)
        ) {
            Console(consoleViewModel)
        }

        BottomMenu(
            items = listOf(
                BottomMenuContent("Blocks", R.drawable.ic_blocks),
                BottomMenuContent("Console", R.drawable.ic_console),
                BottomMenuContent("Run", R.drawable.ic_play),
                BottomMenuContent("Home", R.drawable.ic_home),
            ),
            onItemClick = { item ->
                when (item.title) {
                    "Blocks" -> {
                        isBlockPanelVisible = !isBlockPanelVisible
                        isConsoleVisible = false
                    }

                    "Console" -> {
                        isConsoleVisible = !isConsoleVisible
                        isBlockPanelVisible = false
                    }

                    "Run" -> {
                        consoleViewModel.clearConsole()
                        interpreterViewModel.runProgram()
                    }

                    "Home" -> {
                        navController.navigate(Screens.ProjectListScreen.name)
                        isConsoleVisible = false
                        isBlockPanelVisible = false
                    }
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
            .padding(horizontal = size16, vertical = size12)
            .fillMaxWidth()
            .height(size72),
        color = TextOrange,
        shape = RoundedCornerShape(size20),
        shadowElevation = size4
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = size16),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            items.forEach { item ->
                Column(
                    modifier = Modifier
                        .clickable { onItemClick(item) }
                        .padding(size8),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        painter = painterResource(id = item.iconId),
                        contentDescription = item.title,
                        tint = Color.White,
                        modifier = Modifier
                            .size(size40)
                    )
                }
            }
        }
    }
}

suspend fun PointerInputScope.detectPointerPositionChanges(onPositionChanged: (Offset) -> Unit) {
    awaitPointerEventScope {
        while (true) {
            val event = awaitPointerEvent(PointerEventPass.Main)
            if (event.changes.isNotEmpty()) {
                onPositionChanged(event.changes.first().position)
            }
        }
    }
}



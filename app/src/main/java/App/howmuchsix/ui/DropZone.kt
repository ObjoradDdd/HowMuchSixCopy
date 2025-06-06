package App.howmuchsix.ui

import App.howmuchsix.ui.theme.design_elements.DarkerBeige
import App.howmuchsix.ui.theme.design_elements.PlaceholderText
import App.howmuchsix.ui.theme.design_elements.TextWhite
import App.howmuchsix.ui.theme.design_elements.size1
import App.howmuchsix.ui.theme.design_elements.size4
import App.howmuchsix.ui.theme.design_elements.size5000
import App.howmuchsix.ui.theme.design_elements.size60
import App.howmuchsix.viewmodel.BlockEditorViewModel
import App.howmuchsix.viewmodel.BlockType
import App.howmuchsix.viewmodel.DropZoneTarget
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.boundsInRoot
import androidx.compose.ui.layout.onGloballyPositioned

@Composable
fun DropZone(
    id: String,
    ownerBlockId: String,
    viewModel: BlockEditorViewModel,
    acceptedTypes: List<BlockType> = emptyList(),
    placeholder: String = "Drop here",
    multipleBlocks: Boolean = false,
    modifier: Modifier = Modifier
) {
    val dropZoneHighlight = viewModel.dropZoneHighlight
    val isHighlighted = dropZoneHighlight?.targetId == id
    val isValidDrop = dropZoneHighlight?.isValid == true

    val currBlocks = viewModel.getDropZoneContents(id)

    Box(
        modifier = modifier
            .defaultMinSize(minHeight = size60)
            .background(
                color = when {
                    isHighlighted && isValidDrop -> Color.Green.copy(alpha = 0.3f)
                    isHighlighted && !isValidDrop -> Color.Red.copy(alpha = 0.3f)
                    currBlocks.isNotEmpty() -> Color.Transparent
                    else -> DarkerBeige.copy(alpha = 0.3f)
                },
                shape = RoundedCornerShape(size4)
            )
            .border(
                width = size1,
                color = when {
                    isHighlighted && isValidDrop -> Color.Green
                    isHighlighted && !isValidDrop -> Color.Red
                    else -> TextWhite
                },
                shape = RoundedCornerShape(size4)
            )
            .padding(size4)
            .onGloballyPositioned { coordinates ->
                val globalBounds = coordinates.boundsInRoot()
                viewModel.registerDropZone(
                    DropZoneTarget(
                        id = id,
                        position = Offset(globalBounds.left, globalBounds.top),
                        size = Size(globalBounds.width, globalBounds.height),
                        acceptedTypes = acceptedTypes,
                        ownerBlockId = ownerBlockId,
                        multipleBlocks = multipleBlocks
                    )
                )
            },
        contentAlignment = Alignment.Center
    ) {
        if (currBlocks.isNotEmpty()) {
            if (multipleBlocks) {
                LazyColumn(
                    modifier = Modifier.heightIn(max = size5000),
                    verticalArrangement = Arrangement.spacedBy(size4)
                ) {
                    items(
                        items = currBlocks,
                        key = { block -> block?.id!! }
                    ) { block ->
                        if (block != null) {
                            Box(
                                modifier = Modifier
                                    .wrapContentSize()
                                    .pointerInput(block.id) {
                                        detectTapGestures(
                                            onLongPress = {
                                                viewModel.removeBlockFromDropZone(id, block.id)

                                                val fieldPosition = Offset(0f, 0f)
                                                viewModel.addToFieldFromDropZone(
                                                    block.copy(position = fieldPosition),
                                                    id
                                                )
                                                viewModel.startDraggingPlacedBlock(
                                                    block.id,
                                                    fieldPosition
                                                )
                                            }
                                        )
                                    }
                            ) {
                                block.uiBlock.Render(Modifier.wrapContentSize(), viewModel)
                            }
                        }
                    }
                }
            } else {
                val singleBlock = currBlocks.first()
                if (singleBlock != null) {
                    Box(
                        modifier = Modifier
                            .wrapContentSize()
                            .scale(1f)
                            .pointerInput(singleBlock.id) {
                                detectTapGestures(
                                    onLongPress = {
                                        viewModel.removeBlockFromDropZone(id, singleBlock.id)

                                        val fieldPosition = Offset(0f, 0f)
                                        viewModel.addToFieldFromDropZone(
                                            singleBlock.copy(position = fieldPosition),
                                            id
                                        )
                                        viewModel.startDraggingPlacedBlock(
                                            singleBlock.id,
                                            fieldPosition
                                        )
                                    }
                                )
                            }
                    ) {
                        singleBlock.uiBlock.Render(Modifier.wrapContentSize(), viewModel)
                    }
                }
            }
        } else {
            Text(
                text = placeholder,
                style = PlaceholderText
            )
        }
    }

    DisposableEffect(id) {
        onDispose {
            viewModel.unregisterDropZone(id)
        }
    }
}
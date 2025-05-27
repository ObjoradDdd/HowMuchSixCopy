package App.howmuchsix.ui

import App.howmuchsix.ui.theme.design_elements.DarkerBeige
import App.howmuchsix.ui.theme.design_elements.InputText
import App.howmuchsix.ui.theme.design_elements.LighterBeige
import App.howmuchsix.viewmodel.BlockEditorViewModel
import App.howmuchsix.viewmodel.BlockType
import App.howmuchsix.viewmodel.DropZoneTarget
import App.howmuchsix.viewmodel.PlacedBlockUI
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.currentComposer
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.boundsInRoot
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.text.Placeholder
import androidx.compose.ui.unit.dp

@Composable
fun DropZone(
    id: String,
    ownerBlockId: String,
    viewModel: BlockEditorViewModel,
    acceptedTypes: List<BlockType> = emptyList(),
    currBlock: PlacedBlockUI? = null,
    placeholder: String = "Drop here",
    onBlockDropped: (PlacedBlockUI) -> Unit = {},
    onBlockRemoved: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val dropZoneHighlight = viewModel.dropZoneHighlight
    val isHighlighted = dropZoneHighlight?.targetId == id
    val isValidDrop = dropZoneHighlight?.isValid == true

    Box(
        modifier = modifier
            .height(40.dp)
            .background(
                color = when {
                    isHighlighted && isValidDrop -> Color.Green.copy(alpha = 0.3f)
                    isHighlighted && !isValidDrop -> Color.Red.copy(alpha = 0.3f)
                    currBlock != null -> Color.Transparent
                    else -> DarkerBeige.copy(alpha = 0.3f)
                },
                shape = RoundedCornerShape(4.dp)
            )
            .border(
                width = 1.dp,
                color = when {
                    isHighlighted && isValidDrop -> Color.Green
                    isHighlighted && !isValidDrop -> Color.Red
                    else -> DarkerBeige
                },
                shape = RoundedCornerShape(4.dp)
            )
            .padding(4.dp)
            .onGloballyPositioned { coordinates ->
                val globalBounds = coordinates.boundsInRoot()
                viewModel.registerDropZone(
                    DropZoneTarget(
                        id = id,
                        position = Offset(globalBounds.left, globalBounds.top),
                        size = Size(globalBounds.width, globalBounds.height),
                        acceptedTypes = acceptedTypes,
                        ownerBlockId = ownerBlockId
                    )
                )
            },
        contentAlignment = Alignment.Center
    ) {
        if (currBlock != null) {
            Box(
                modifier = Modifier
                    .scale(0.8f)
                    .pointerInput(currBlock.id) {
                        detectTapGestures(
                            onLongPress = { onBlockRemoved() }
                        )
                    }
            ){
                currBlock.uiBlock.Render(Modifier)
            }
        } else {
            Text(
                text = placeholder,
                style = InputText
            )
        }
    }

    DisposableEffect(id) {
        onDispose {
            viewModel.unregisterDropZone(id)
        }
    }
}
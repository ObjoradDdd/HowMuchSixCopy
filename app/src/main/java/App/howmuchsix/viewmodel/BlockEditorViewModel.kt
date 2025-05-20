package App.howmuchsix.viewmodel

import App.howmuchsix.hms.Blocks.Block
import androidx.compose.runtime.*
import androidx.lifecycle.ViewModel
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import java.util.UUID

data class BlockItemData(val label: String)

data class BlockCategory(
    val name: String,
    val blocks: List<BlockItemData>,
    val blockColor: Color
)

data class PlacedBlock(
    val id: String = UUID.randomUUID().toString(),
    val label: String,
    val position: Offset
)

class BlockEditorViewModel : ViewModel() {

    private val _placedBlocks = mutableStateListOf<PlacedBlock>()
    val placedBlocks: List<PlacedBlock> = _placedBlocks

    private val _draggedBlock = mutableStateOf<BlockItemData?>(null)
    val draggedBlock: BlockItemData? get() = _draggedBlock.value

    private val _dragPosition = mutableStateOf(Offset.Zero)
    val dragPosition: Offset get() = _dragPosition.value

    private val _isDragging = mutableStateOf(false)
    val isDragging: Boolean get() = _isDragging.value

    private val _draggedPlacedBlockId = mutableStateOf<String?>(null)
    val draggedPlacedBlockId: String? get() = _draggedPlacedBlockId.value

    fun startDragging(block: BlockItemData, initialOffset: Offset){
        _draggedBlock.value = block
        _dragPosition.value = initialOffset
        _isDragging.value = true
        _draggedPlacedBlockId.value = null
    }

    fun startDraggingPlacedBlock(blockId :String, initialOffset: Offset){
        val placedBlock = _placedBlocks.find {it.id == blockId} ?: return

        _draggedBlock.value = BlockItemData(placedBlock.label)
        _dragPosition.value = initialOffset
        _isDragging.value = true
        _draggedPlacedBlockId.value = blockId
    }

    fun updatePosition(newPosition: Offset){
        if (_isDragging.value){
            _dragPosition.value = newPosition
        }
    }
    fun stopDragging(placeOnField: Boolean){
        val currentBlock = _draggedBlock.value
        val currentPosition = _dragPosition.value
        val placedBlockId = _draggedPlacedBlockId.value

        if (placeOnField && currentBlock != null){
            if(placedBlockId != null){
                val index = _placedBlocks.indexOfFirst { it.id == placedBlockId }
                if (index >= 0){
                    _placedBlocks[index] = _placedBlocks[index].copy(position = currentPosition)
                }
            }
            else {
                _placedBlocks.add(
                    PlacedBlock(
                        label = currentBlock.label,
                        position = currentPosition
                    )
                )
            }
        }
        _draggedBlock.value = null
        _dragPosition.value = Offset.Zero
        _isDragging.value = false
        _draggedPlacedBlockId.value = null
    }

}
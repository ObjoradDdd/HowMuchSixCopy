package App.howmuchsix.viewmodel

import App.howmuchsix.hms.Blocks.Block
import App.howmuchsix.hms.Blocks.BreakBlock
import App.howmuchsix.hms.Blocks.FunctionDeclarationBlock
import App.howmuchsix.ui.blocks.AssignmentBlockUI
import App.howmuchsix.ui.blocks.BlockUI
import App.howmuchsix.ui.blocks.BreakBlockUI
import App.howmuchsix.ui.blocks.DeclarationArrayBlockUI
import App.howmuchsix.ui.blocks.DeclarationBlockUI
import App.howmuchsix.ui.blocks.ForBlockUI
import App.howmuchsix.ui.blocks.FunctionBlockUI
import App.howmuchsix.ui.blocks.FunctionDeclarationBlockUI
import App.howmuchsix.ui.blocks.IfBlockUI
import App.howmuchsix.ui.blocks.PrintBlockUI
import App.howmuchsix.ui.blocks.ReturnBlockUI
import App.howmuchsix.ui.blocks.WhileBlockUI
import App.howmuchsix.ui.theme.design_elements.BlockOrange
import androidx.compose.runtime.*
import androidx.lifecycle.ViewModel
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import java.util.UUID

enum class BlockType{
    Declaration, Assignment, Break,
    DeclarationArray, For, Function,
    If, Print, Return, While,
    FunctionDeclaration
}

data class BlockItemData(
    val label: String,
    val color: Color,
    val type: BlockType
)

data class BlockCategory(
    val name: String,
    val blocks: List<BlockItemData>,
    val blockColor: Color
)

data class PlacedBlockUI(
    val id: String = UUID.randomUUID().toString(),
    val type: BlockType,
    val uiBlock: BlockUI,
    val position: Offset,
)

class BlockEditorViewModel : ViewModel() {

    private val _placedBlocks = mutableStateListOf<PlacedBlockUI>()
    val placedBlocks: List<PlacedBlockUI> = _placedBlocks

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
        val block = _placedBlocks.find {it.id == blockId} ?: return

        _draggedBlock.value = BlockItemData(
            type = block.type,
            label = block.type.name,
            color = BlockOrange
        )
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
                val uiBlock = createUIBlockByType(currentBlock.type)
                _placedBlocks.add(
                    PlacedBlockUI(
                        type = currentBlock.type,
                        uiBlock = uiBlock,
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

    private fun createUIBlockByType(type: BlockType): BlockUI{
        return when (type) {
            BlockType.Declaration -> DeclarationBlockUI()
            BlockType.Assignment -> AssignmentBlockUI()
            BlockType.For -> ForBlockUI()
            BlockType.Break -> BreakBlockUI()
            BlockType.DeclarationArray -> DeclarationArrayBlockUI()
            BlockType.Function -> FunctionBlockUI()
            BlockType.FunctionDeclaration -> FunctionDeclarationBlockUI()
            BlockType.If -> IfBlockUI()
            BlockType.Print -> PrintBlockUI()
            BlockType.Return -> ReturnBlockUI()
            BlockType.While -> WhileBlockUI()
        }
    }

}
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
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Color
import java.util.UUID
import kotlin.math.pow
import kotlin.math.sqrt

enum class BlockType{
    Declaration, Assignment, Break,
    DeclarationArray, For, Function,
    If, Print, Return, While,
    FunctionDeclaration, And, Or,
    Operator, Type, Logic
}

enum class ConnectionType{
    Top,
    Bottom,
    Input,
    Output
}

data class BlockItemData(
    val label: String,
    val color: Color,
    val type: BlockType
)

data class ConnectionPoint(
    val id: String,
    val type: ConnectionType,
    val position: Offset,
    val blockId: String
)

data class BlockConnection(
    val parentBlockId: String,
    val childBlockId: String,
    val parentConnectionPoint: String,
    val childConnectionPoint: String
)

data class NearbyConnection(
    val connectionPoint: ConnectionPoint,
    val ownerBlockId: String
)

data class DropZoneData(
    val id: String = UUID.randomUUID().toString(),
    val acceptedTypes: List<BlockType> = emptyList(),
    val droppedBlock: PlacedBlockUI? = null,
    val placeholder: String = "Drop here"
)

data class DropZoneTarget(
    val id: String,
    val position: Offset,
    val size: androidx.compose.ui.geometry.Size,
    val acceptedTypes: List<BlockType> = emptyList(),
    val ownerBlockId: String
)

data class DropZoneHighlight(
    val targetId: String,
    val isValid: Boolean
)

data class PlacedBlockUI(
    val id: String = UUID.randomUUID().toString(),
    val type: BlockType,
    val uiBlock: BlockUI,
    val position: Offset,
    val originalBlockData: BlockItemData,
    val connectionPoints: List<ConnectionPoint> = emptyList(),
    val isConnected: Boolean = false,
    val parentConnection: BlockConnection? = null,
    val dropZones: List<DropZoneData> = emptyList()
) {
    override fun toString(): String {
        return this.type.toString()
    }
}

class BlockEditorViewModel : ViewModel() {
    private val snapThreshold = 50f

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

    private val _connections = mutableStateListOf<BlockConnection>()
    val connections: List<BlockConnection> = _connections

    private val _nearbyConnectionPoint = mutableStateOf<NearbyConnection?>(null)
    val nearbyConnectionPoint: NearbyConnection? get() = _nearbyConnectionPoint.value

    private val _dropZoneTargets = mutableStateListOf<DropZoneTarget>()
    val dropzoneTargets: List<DropZoneTarget> = _dropZoneTargets

    private val _dropZoneHighlight = mutableStateOf<DropZoneHighlight?>(null)
    val dropZoneHighlight: DropZoneHighlight? get() = _dropZoneHighlight.value

    private val _dropZoneContents = mutableStateMapOf<String, PlacedBlockUI>()
    val dropZoneContents: Map<String, PlacedBlockUI> = _dropZoneContents

    private val _functionNames = mutableStateMapOf<String, String>()
    val functionNames: Map<String, String> = _functionNames

    fun updateFunctionName(blockId: String, functionName: String) {
        if (functionName.isBlank()) {
            _functionNames.remove(blockId)
        } else {
            _functionNames[blockId] = functionName
        }
    }
    fun getAllFunNames(): List<String> {
        return _functionNames.values.filter { it.isNotBlank() }
    }
    fun removeFunctionName(blockId: String){
        _functionNames.remove(blockId)
    }

    fun findDropZoneAtPosition(position: Offset) : DropZoneTarget? {
        return _dropZoneTargets.find { target ->
            val dropZoneRect = Rect(
                offset = target.position,
                size = target.size
            )
            dropZoneRect.contains(position)
        }
    }
    fun addBlockToDropZone(blockId: String, dropZoneTarget: DropZoneTarget){
        val block = _placedBlocks.find { it.id == blockId } ?: return
        _placedBlocks.removeIf{ it.id == blockId}

        if(block.type == BlockType.FunctionDeclaration){
            removeFunctionName(blockId)
        }
        _dropZoneContents[dropZoneTarget.id] = block
    }
    fun removeBlockFropmDropZone(dropZoneId: String) {
        val removedBlock = _dropZoneContents.remove(dropZoneId)
    }
    fun getDropZoneContent(dropZoneId: String): PlacedBlockUI? {
        return _dropZoneContents[dropZoneId]
    }

    fun registerDropZone(dropZone: DropZoneTarget){
        _dropZoneTargets.removeIf { it.id == dropZone.id }
        _dropZoneTargets.add(dropZone)
    }
    fun unregisterDropZone(dropZoneId: String){
        _dropZoneTargets.removeIf { it.id == dropZoneId }
    }

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

            val draggedBlock = _draggedBlock.value
            val draggedBlockId = _draggedPlacedBlockId.value

            if (draggedBlock != null){
                findNearbyConnectionPoint(newPosition, draggedBlockId)

                val dropZone = findDropZoneAtPosition(newPosition)
                _dropZoneHighlight.value = dropZone?.let { zone ->
                    DropZoneHighlight(
                        targetId = zone.id,
                        isValid = zone.acceptedTypes.isEmpty() ||
                                zone.acceptedTypes.contains(draggedBlock.type)
                    )
                }
            }
        }
    }
    fun stopDragging(placeOnField: Boolean){
        val currentBlock = _draggedBlock.value
        val currentPosition = _dragPosition.value
        val placedBlockId = _draggedPlacedBlockId.value
        val nearbyConnection = _nearbyConnectionPoint.value

        if (currentBlock != null) {
            val dropZoneTarget = findDropZoneAtPosition(currentPosition)
            if (dropZoneTarget != null && placedBlockId != null) {
                addBlockToDropZone(placedBlockId, dropZoneTarget)
            } else if (placeOnField) {
                var finalPosition = currentPosition
                nearbyConnection?.let { nearbyConn ->
                    val targetBlock = _placedBlocks.find { it.id == nearbyConn.ownerBlockId }
                    targetBlock?.let { target ->
                        finalPosition = calculateSnapPosition(target, nearbyConn.connectionPoint, currentBlock.type)
                    }
                }
                if(placedBlockId != null){
                    val index = _placedBlocks.indexOfFirst { it.id == placedBlockId }
                    if (index >= 0){
                        _placedBlocks[index] = _placedBlocks[index].copy(
                            position = currentPosition,
                            isConnected = nearbyConnection != null
                        )
                    }
                }
                else {
                    val uiBlock = createUIBlockByType(currentBlock.type)
                    val newBlockId = UUID.randomUUID().toString()
                    val connectionPoints = createConnectionPointsForBlock(currentBlock.type).map {
                        it.copy(blockId = newBlockId)
                    }

                    val newBlock = PlacedBlockUI(
                        id = newBlockId,
                        type = currentBlock.type,
                        uiBlock = uiBlock,
                        position = finalPosition,
                        originalBlockData = currentBlock,
                        connectionPoints = connectionPoints,
                        isConnected = nearbyConnection != null
                    )
                    _placedBlocks.add(newBlock)

                }
            }
        }

        _draggedBlock.value = null
        _dragPosition.value = Offset.Zero
        _isDragging.value = false
        _draggedPlacedBlockId.value = null
        _nearbyConnectionPoint.value = null
        _dropZoneHighlight.value = null
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
            else -> WhileBlockUI()
        }
    }
    private fun findNearbyConnectionPoint(dragPosition: Offset, excludeBlockId: String?) {
        var closestPoint: ConnectionPoint? = null
        var closestDistance = Float.MAX_VALUE
        var ownerBlockId: String? = null

        _placedBlocks.forEach { block ->
            if (block.id != excludeBlockId) {
                block.connectionPoints.forEach { connectionPoint ->
                    val absolutePosition = block.position + connectionPoint.position
                    val distance = sqrt(
                        (dragPosition.x - absolutePosition.x).pow(2) +
                                (dragPosition.y - absolutePosition.y).pow(2)
                    )
                    if (distance < snapThreshold && distance < closestDistance){
                        closestDistance = distance
                        closestPoint = connectionPoint
                        ownerBlockId = block.id
                    }
                }
            }
        }
        val previousPoint = _nearbyConnectionPoint.value
        _nearbyConnectionPoint.value = if (closestDistance < snapThreshold &&
            closestPoint != null && ownerBlockId != null) {
            NearbyConnection(closestPoint!!, ownerBlockId!!)
        } else null

    }
    private fun calculateSnapPosition(
        targetBlock: PlacedBlockUI,
        connectionPoint: ConnectionPoint,
        draggingBlockType: BlockType
    ): Offset {
        val targetConnectionPosition = targetBlock.position + connectionPoint.position

        return when (connectionPoint.type) {
            ConnectionType.Top -> {
                Offset(
                    targetConnectionPosition.x - getBlockWidth(draggingBlockType) / 2,
                    targetConnectionPosition.y - getBlockHeight(draggingBlockType) - 10f
                )
            }
            ConnectionType.Bottom -> {
                Offset(
                    targetConnectionPosition.x - getBlockWidth(draggingBlockType) / 2,
                    targetConnectionPosition.y + 10f
                )
            }
            ConnectionType.Input -> {
                Offset(
                    targetConnectionPosition.x + 10f,
                    targetConnectionPosition.y - getBlockHeight(draggingBlockType) / 2
                )
            }
            ConnectionType.Output -> {
                Offset(
                    targetConnectionPosition.x - getBlockWidth(draggingBlockType) - 10f,
                    targetConnectionPosition.y - getBlockHeight(draggingBlockType) / 2
                )
            }//лучше через таргет дроп поле хз??
        }
    }
    private fun createConnection(parentId: String, childId: String, connectionPointId: String){
        val connection = BlockConnection(
            parentBlockId = parentId,
            childBlockId = childId,
            parentConnectionPoint = connectionPointId,
            childConnectionPoint = ""
        )
        _connections.add(connection)
    }
    private fun createConnectionPointsForBlock(blockType: BlockType): List<ConnectionPoint> {

        return when(blockType) {
            BlockType.Declaration, BlockType.Assignment -> {
                listOf(
                    ConnectionPoint(
                        id = UUID.randomUUID().toString(),
                        type = ConnectionType.Top,
                        position = Offset(0f, 0f),
                        blockId = ""
                    ),
                    ConnectionPoint(
                        id = UUID.randomUUID().toString(),
                        type = ConnectionType.Bottom,
                        position = Offset(50f, 150f),
                        blockId = ""
                    )
                )
            }
            BlockType.For, BlockType.If, BlockType.While -> {
                listOf(
                    ConnectionPoint(
                        id = UUID.randomUUID().toString(),
                        type = ConnectionType.Top,
                        position = Offset(0f, 0f),
                        blockId = ""
                    ),
                    ConnectionPoint(
                        id = UUID.randomUUID().toString(),
                        type = ConnectionType.Bottom,
                        position = Offset(50f, 150f),
                        blockId = ""
                    ),
                    ConnectionPoint(
                        id = UUID.randomUUID().toString(),
                        type = ConnectionType.Input,
                        position = Offset(50f, 80f),
                        blockId = ""
                    )
                )
            }
            else -> {
                listOf(
                    ConnectionPoint(
                        id = UUID.randomUUID().toString(),
                        type = ConnectionType.Top,
                        position = Offset(0f, 0f),
                        blockId = ""
                    ),
                    ConnectionPoint(
                        id = UUID.randomUUID().toString(),
                        type = ConnectionType.Bottom,
                        position = Offset(50f, 150f),
                        blockId = ""
                    )
                )
            }
        }
    }
    private fun getBlockHeight(blockType: BlockType): Float {
        return when (blockType) {
            BlockType.Declaration, BlockType.Assignment -> 100f
            else -> 100f
        }
    }
    private fun getBlockWidth(blockType: BlockType): Float {
        return when (blockType) {
            BlockType.Declaration, BlockType.Assignment -> 80f
            else -> 80f
        }
    }

}
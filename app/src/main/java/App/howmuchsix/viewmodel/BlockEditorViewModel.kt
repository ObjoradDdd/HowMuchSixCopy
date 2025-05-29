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
import App.howmuchsix.ui.blocks.SleepBlockUI
import App.howmuchsix.ui.blocks.WhileBlockUI
import App.howmuchsix.ui.blocks.ContinueBlockUI
import App.howmuchsix.ui.blocks.StartProgramBlockUI
import App.howmuchsix.ui.theme.design_elements.BlockOrange
import App.howmuchsix.ui.theme.design_elements.BlockPeach
import android.util.Log
import androidx.compose.animation.core.snap
import androidx.compose.runtime.*
import androidx.lifecycle.ViewModel
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import java.util.UUID
import kotlin.coroutines.Continuation
import kotlin.math.pow
import kotlin.math.sqrt

enum class BlockType{
    Declaration, Assignment, Break,
    DeclarationArray, For, Function,
    If, Print, Return, While,
    FunctionDeclaration, Sleep,
    Continue, StartProgram
}

enum class ConnectionType{
    Top,
    Bottom
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
    val ownerBlockId: String,
    val multipleBlocks: Boolean = false
)

data class DropZoneHighlight(
    val targetId: String,
    val isValid: Boolean
)

data class PlacedBlockUI(
    val id: String = UUID.randomUUID().toString(),
    val type: BlockType,
    val position: Offset,
    val parentId: String? = null,
    val parentDropZoneId: String? = null,
    val dropZoneChildren: Map<String, List<String>> = emptyMap(),
    val nextBlockId: String? = null,
    val previousBlockId: String? =  null,
    val uiBlock: BlockUI,
    val originalBlockData: BlockItemData,
    val connectionPoints: List<ConnectionPoint> = emptyList(),
    val isConnected: Boolean = false,
    val dropZones: List<DropZoneData> = emptyList(),
    val size: Size = Size(80f, 100f)
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

    private val _dropZoneContents = mutableStateMapOf<String, MutableList<PlacedBlockUI>>()
    val dropZoneContents: Map<String, List<PlacedBlockUI>> = _dropZoneContents

    private val _originalChainPositions = mutableMapOf<String, Offset>()
    private val _dragStartPosition = mutableStateOf(Offset.Zero)

    private val _functionNames = mutableStateMapOf<String, String>().apply {
        put("intToString", "intToString")
        put("BooleanToString", "BooleanToString")
        put("stringToInt", "stringToInt")
        put("doubleToString", "doubleToString")
        put("stringToDouble", "stringToDouble")
    }
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
        return _dropZoneTargets.filter { target ->
            val dropZoneRect = Rect(
                offset = target.position,
                size = target.size
            )
            dropZoneRect.contains(position)
        }
            .minByOrNull { it.size.width * it.size.height }
    }
    fun addBlockToDropZone(blockId: String, dropZoneTarget: DropZoneTarget): Boolean {
        val block = _placedBlocks.find { it.id == blockId } ?: return false
        disconnectBlock(blockId)
        _placedBlocks.removeIf{ it.id == blockId}

        val chain = getChain(blockId)
        chain.forEach { chainBlock ->
            _placedBlocks.removeIf { it.id == chainBlock.id }
        }
        chain.filter { it.id != blockId }.forEach { chainBlock ->
            _placedBlocks.add(
                chainBlock.copy(
                    isConnected = false,
                    previousBlockId = null
                )
            )
        }

        if(block.type == BlockType.FunctionDeclaration){
            removeFunctionName(blockId)
        }
        if (dropZoneTarget.multipleBlocks) {
            _dropZoneContents.getOrPut(dropZoneTarget.id) { mutableListOf() }.add(block)
        } else {
            _dropZoneContents[dropZoneTarget.id] = mutableListOf(block)
        }
        return true
    }
    fun addToFieldFromDropZone(block: PlacedBlockUI, dropZoneId: String) {
        val zone = _dropZoneTargets.find { it.id ==dropZoneId }
        val fieldPosition = zone?.position ?: Offset.Zero

        _placedBlocks.add(
            block.copy(
                position = fieldPosition,
                isConnected = false,
                nextBlockId = null,
                previousBlockId = null
            )
        )
    }
    fun removeBlockFromDropZone(dropZoneId: String, blockId: String? = null) {
        val blocks = _dropZoneContents[dropZoneId] ?: return

        if (blockId != null) {
            blocks.removeIf{ it.id == blockId}
            if (blocks.isEmpty()) {
                _dropZoneContents.remove(dropZoneId)
            }
        } else {
            _dropZoneContents.remove(dropZoneId)
        }
    }
    fun getDropZoneContents(dropZoneId: String): List<PlacedBlockUI?> {
        return _dropZoneContents[dropZoneId] ?: emptyList()
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
        val block = _placedBlocks.find { it.id == blockId } ?: return

        _originalChainPositions.clear()
        val chain = getChain(blockId)
        chain.forEach { chainBlock ->
            _originalChainPositions[chainBlock.id] = chainBlock.position
        }

        _draggedBlock.value = BlockItemData(
            type = block.type,
            label = block.type.name,
            color = BlockPeach
        )
        _dragPosition.value = initialOffset
        _dragStartPosition.value = block.position
        _isDragging.value = true
        _draggedPlacedBlockId.value = blockId
    }
    fun updatePosition(newPosition: Offset){
        if (_isDragging.value) {
            _dragPosition.value = newPosition

            val draggedBlock = _draggedBlock.value
            val draggedBlockId = _draggedPlacedBlockId.value

            if (draggedBlock != null && draggedBlockId != null) {
                moveChain(draggedBlockId, newPosition)
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
    fun stopDragging(placeOnField: Boolean) {
        val currentBlock = _draggedBlock.value
        val currentPosition = _dragPosition.value
        val placedBlockId = _draggedPlacedBlockId.value
        val nearbyConnection = _nearbyConnectionPoint.value

        if (currentBlock != null) {
            val dropZoneTarget = findDropZoneAtPosition(currentPosition)
            val isValid = dropZoneTarget != null && (dropZoneTarget.acceptedTypes.isEmpty() ||
                    dropZoneTarget.acceptedTypes.contains(currentBlock.type))

            if (dropZoneTarget != null && isValid && placedBlockId != null) {
                addBlockToDropZone(placedBlockId, dropZoneTarget)
            } else if (placeOnField) {
                if (placedBlockId != null) {
                    val index = _placedBlocks.indexOfFirst { it.id == placedBlockId }
                    if (index >= 0) {
                        var finalPosition = currentPosition
                        var shouldConnect = false
                        var connectionTargetId: String? = null

                        nearbyConnection?.let { nearbyConn ->
                            val targetBlock = _placedBlocks.find { it.id == nearbyConn.ownerBlockId }
                            targetBlock?.let { target ->
                                finalPosition = calculateSnapPosition(target, nearbyConn.connectionPoint, currentBlock.type)
                                shouldConnect = true
                                connectionTargetId = target.id
                            }
                        }

                        if (shouldConnect && connectionTargetId != null) {
                            val draggedBlock = _placedBlocks[index]
                            draggedBlock.previousBlockId?.let { prevId ->
                                val prevIndex = _placedBlocks.indexOfFirst { it.id == prevId }
                                if (prevIndex >= 0) {
                                    _placedBlocks[prevIndex] = _placedBlocks[prevIndex].copy(
                                        nextBlockId = null
                                    )
                                }
                            }
                            moveChain(placedBlockId, finalPosition)
                            _placedBlocks[index] = _placedBlocks[index].copy(
                                isConnected = true,
                                previousBlockId = null
                            )

                            nearbyConnection?.let { nearbyConn ->
                                if (nearbyConn.connectionPoint.type == ConnectionType.Bottom) {
                                    connectBlocks(connectionTargetId!!, placedBlockId)
                                } else if (nearbyConn.connectionPoint.type == ConnectionType.Top) {
                                    connectBlocks(placedBlockId, connectionTargetId!!)
                                }
                            }
                        } else {
                            moveChain(placedBlockId, currentPosition)
                            _placedBlocks[index] = _placedBlocks[index].copy(
                                isConnected = false
                            )
                        }
                    }
                } else {
                    var finalPosition = currentPosition
                    var shouldConnect = false
                    var connectionTargetId: String? = null

                    nearbyConnection?.let { nearbyConn ->
                        val targetBlock = _placedBlocks.find { it.id == nearbyConn.ownerBlockId }
                        targetBlock?.let { target ->
                            finalPosition = calculateSnapPosition(target, nearbyConn.connectionPoint, currentBlock.type)
                            shouldConnect = true
                            connectionTargetId = target.id
                        }
                    }

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
                        isConnected = shouldConnect,
                        nextBlockId = null,
                        previousBlockId = null
                    )
                    _placedBlocks.add(newBlock)
                    if (shouldConnect && connectionTargetId != null) {
                        nearbyConnection?.let { nearbyConn ->
                            if (nearbyConn.connectionPoint.type == ConnectionType.Bottom) {
                                connectBlocks(connectionTargetId!!, newBlockId)
                            } else if (nearbyConn.connectionPoint.type == ConnectionType.Top) {
                                connectBlocks(newBlockId, connectionTargetId!!)
                            }
                        }
                    }
                }
            }
        }
        _draggedBlock.value = null
        _dragPosition.value = Offset.Zero
        _dragStartPosition.value = Offset.Zero
        _originalChainPositions.clear()
        _isDragging.value = false
        _draggedPlacedBlockId.value = null
        _nearbyConnectionPoint.value = null
        _dropZoneHighlight.value = null
    }

    fun updateBlockSize(blockId: String, size: Size) {
        val index = _placedBlocks.indexOfFirst { it.id == blockId }
        if (index != -1) {
            val currentBlock = _placedBlocks[index]
            val updatedConnectionPoints = createConnectionPointsForBlock(currentBlock.type, size).map {
                it.copy(blockId = blockId)
            }
            _placedBlocks[index] = currentBlock.copy(
                size = size,
                connectionPoints = updatedConnectionPoints
            )
        }
    }
    fun createConnectionPointsForBlock(blockType: BlockType, size: Size? = null): List<ConnectionPoint> {
        val blockWidth = size?.width ?: 80f
        val blockHeight = size?.height ?: 100f

        return when (blockType) {
            BlockType.StartProgram -> {
                listOf(
                    ConnectionPoint(
                        id = UUID.randomUUID().toString(),
                        type = ConnectionType.Bottom,
                        position = Offset(blockWidth / 2, blockHeight),
                        blockId = ""
                    )
                )
            }
            else -> {
                listOf(
                    ConnectionPoint(
                        id = UUID.randomUUID().toString(),
                        type = ConnectionType.Top,
                        position = Offset(blockWidth / 2, 0f),
                        blockId = ""
                    ),
                    ConnectionPoint(
                        id = UUID.randomUUID().toString(),
                        type = ConnectionType.Bottom,
                        position = Offset(blockWidth / 2, blockHeight),
                        blockId = ""
                    )
                )
            }
        }
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
            BlockType.Sleep -> SleepBlockUI()
            BlockType.Continue -> ContinueBlockUI()
            BlockType.StartProgram -> StartProgramBlockUI()
            else -> DeclarationBlockUI()
        }
    }
    private fun connectBlocks(parentBlockId: String, childBlockId: String) {
        disconnectBlock(childBlockId)
        val parentIdx = _placedBlocks.indexOfFirst { it.id == parentBlockId }
        if (parentIdx >= 0){
            _placedBlocks[parentIdx] = _placedBlocks[parentIdx].copy(
                nextBlockId = childBlockId
            )
        }

        val childIdx = _placedBlocks.indexOfFirst { it.id == childBlockId }
        if (childIdx >= 0){
            _placedBlocks[childIdx] = _placedBlocks[childIdx].copy(
                previousBlockId = parentBlockId,
                isConnected = true
            )
        }
    }
    private fun disconnectBlock(blockId: String) {
        val blockIndex = _placedBlocks.indexOfFirst { it.id == blockId }
        if (blockIndex >= 0) {
            val block = _placedBlocks[blockIndex]

            block.previousBlockId?.let { prevId ->
                val prevIndex = _placedBlocks.indexOfFirst { it.id == prevId }
                if (prevIndex >= 0) {
                    _placedBlocks[prevIndex] = _placedBlocks[prevIndex].copy(
                        nextBlockId = null
                    )
                }
            }
            block.nextBlockId?.let { nextId ->
                val nextIndex = _placedBlocks.indexOfFirst { it.id == nextId }
                if (nextIndex >= 0) {
                    _placedBlocks[nextIndex] = _placedBlocks[nextIndex].copy(
                        previousBlockId = null,
                        isConnected = false
                    )
                }
            }
            _placedBlocks[blockIndex] = block.copy(
                nextBlockId = null,
                previousBlockId = null,
                isConnected = false
            )
        }
    }

    private fun getChain(blockId: String): List<PlacedBlockUI> {
        val chain = mutableListOf<PlacedBlockUI>()
        var currentId: String? = blockId

        while (currentId != null) {
            val block = _placedBlocks.find { it.id == currentId }
            if (block != null) {
                chain.add(block)
                currentId = block.nextBlockId
            } else {
                break
            }
        }

        return chain
    }
    private fun moveChain(draggedBlockId: String, delta: Offset){
        val chain = getChain(draggedBlockId)
        if (chain.isEmpty()) return

        val draggedIndex = _placedBlocks.indexOfFirst { it.id == draggedBlockId }
        if (draggedIndex >= 0) {
            _placedBlocks[draggedIndex] = _placedBlocks[draggedIndex].copy(
                position = delta
            )
        }

        for (i in 1 until chain.size) {
            val currentBlock = chain[i]
            val previousBlock = chain[i - 1]

            val currentIndex = _placedBlocks.indexOfFirst { it.id == currentBlock.id }
            val previousIndex = _placedBlocks.indexOfFirst { it.id == previousBlock.id }

            if (currentIndex >= 0 && previousIndex >= 0) {
                val previousBlockPosition = _placedBlocks[previousIndex].position
                val previousBlockSize = _placedBlocks[previousIndex].size
                val newPosition = Offset(
                    x = previousBlockPosition.x,
                    y = previousBlockPosition.y + previousBlockSize.height
                )
                _placedBlocks[currentIndex] = _placedBlocks[currentIndex].copy(
                    position = newPosition
                )
            }
        }
    }

    fun hasBottomChain(blockId: String): Boolean {
        val block = _placedBlocks.find { it.id == blockId }
        return block?.nextBlockId != null
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
        val targetSize = targetBlock.size
        val draggedBlockId = _draggedPlacedBlockId.value
        val draggingSize = if (draggedBlockId != null) {
            val existingBlock = _placedBlocks.find { it.id == draggedBlockId }
            existingBlock?.size ?: Size(getBlockWidth(draggingBlockType), getBlockHeight(draggingBlockType))
        } else {
            Size(getBlockWidth(draggingBlockType), getBlockHeight(draggingBlockType))
        }

        return when (connectionPoint.type) {
            ConnectionType.Top -> {
                Offset(
                    targetBlock.position.x,
                    targetBlock.position.y - draggingSize.height
                )
            }
            ConnectionType.Bottom -> {
                Offset(
                    targetBlock.position.x,
                    targetBlock.position.y + targetSize.height
                )
            }
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
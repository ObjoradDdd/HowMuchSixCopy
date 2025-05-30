package App.howmuchsix.viewmodel

import App.howmuchsix.ui.blocks.AssignmentBlockUI
import App.howmuchsix.ui.blocks.BlockUI
import App.howmuchsix.ui.blocks.BreakBlockUI
import App.howmuchsix.ui.blocks.ContinueBlockUI
import App.howmuchsix.ui.blocks.DeclarationArrayBlockUI
import App.howmuchsix.ui.blocks.DeclarationBlockUI
import App.howmuchsix.ui.blocks.ForBlockUI
import App.howmuchsix.ui.blocks.FunctionBlockUI
import App.howmuchsix.ui.blocks.FunctionDeclarationBlockUI
import App.howmuchsix.ui.blocks.IfBlockUI
import App.howmuchsix.ui.blocks.PrintBlockUI
import App.howmuchsix.ui.blocks.ReturnBlockUI
import App.howmuchsix.ui.blocks.SleepBlockUI
import App.howmuchsix.ui.blocks.StartProgramBlockUI
import App.howmuchsix.ui.blocks.TryCatchBlockUI
import App.howmuchsix.ui.blocks.WhileBlockUI
import App.howmuchsix.ui.theme.design_elements.BlockOrange
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel
import java.util.UUID
import kotlin.collections.List
import kotlin.collections.MutableList
import kotlin.collections.emptyList
import kotlin.collections.filter
import kotlin.collections.find
import kotlin.collections.forEach
import kotlin.collections.forEachIndexed
import kotlin.collections.getOrPut
import kotlin.collections.indexOfFirst
import kotlin.collections.indices
import kotlin.collections.listOf
import kotlin.collections.map
import kotlin.collections.minByOrNull
import kotlin.collections.mutableListOf
import kotlin.collections.mutableSetOf
import kotlin.collections.set
import kotlin.math.pow
import kotlin.math.sqrt

enum class BlockType {
    Declaration, Assignment, Break,
    DeclarationArray, For, Function,
    If, Print, Return, While,
    FunctionDeclaration, Sleep,
    Continue, StartProgram, Try_catch
}

enum class ConnectionType {
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
    val size: Size,
    val acceptedTypes: List<BlockType> = emptyList(),
    val ownerBlockId: String,
    val multipleBlocks: Boolean = false
)

data class DropZoneHighlight(
    val targetId: String,
    val isValid: Boolean
)

data class ChainNeighbors(
    val previousBlock1: String? = null,
    val previousBlock2: String? = null,
    val nextBlock1: String? = null,
    val nextBlock2: String? = null
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
    val dropZones: List<DropZoneData> = emptyList(),
    val size: Size = Size(80f, 100f),
    val chainNeighbors: ChainNeighbors = ChainNeighbors()
) {
    init {
        this.uiBlock.setUUID(id)
    }

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


    private val _nearbyConnectionPoint = mutableStateOf<NearbyConnection?>(null)
    val nearbyConnectionPoint: NearbyConnection? get() = _nearbyConnectionPoint.value

    private val _dropZoneTargets = mutableStateListOf<DropZoneTarget>()

    private val _dropZoneHighlight = mutableStateOf<DropZoneHighlight?>(null)
    val dropZoneHighlight: DropZoneHighlight? get() = _dropZoneHighlight.value

    private val _dropZoneContents = mutableStateMapOf<String, MutableList<PlacedBlockUI>>()

    private val _dragScreenPosition = mutableStateOf(Offset.Zero)
    val dragScreenPosition: Offset get() = _dragScreenPosition.value

    private val _functionNames = mutableStateMapOf<String, String>().apply {
        put("intToString", "intToString")
        put("booleanToString", "BooleanToString")
        put("stringToInt", "stringToInt")
        put("doubleToString", "doubleToString")
        put("stringToDouble", "stringToDouble")
    }

    private val _directConnections = mutableStateMapOf<String, String>()

    private var _fieldScale = 1f
    private var _fieldOffset = Offset.Zero

    fun updateFieldTransform(scale: Float, offset: Offset) {
        _fieldScale = scale
        _fieldOffset = offset
    }

    private fun getStartProgramBlock(): PlacedBlockUI? {
        return _placedBlocks.find { it.type == BlockType.StartProgram }
    }


    fun getBlockChain(): List<PlacedBlockUI> {
        val startBlock = getStartProgramBlock() ?: return emptyList()
        val chain = mutableListOf<PlacedBlockUI>()

        var currentBlockId: String? = startBlock.id
        val visited = mutableSetOf<String>()

        while (currentBlockId != null && !visited.contains(currentBlockId)) {
            val currentBlock = _placedBlocks.find { it.id == currentBlockId }
            if (currentBlock != null) {
                chain.add(currentBlock)
                visited.add(currentBlockId)
                currentBlockId = _directConnections[currentBlockId]

                if (currentBlockId != null && _placedBlocks.find { it.id == currentBlockId } == null) {
                    break
                }
            } else {
                break
            }
        }

        return chain
    }

    private fun updateChainNeighbors() {
        _placedBlocks.forEachIndexed { index, block ->
            _placedBlocks[index] = block.copy(chainNeighbors = ChainNeighbors())
        }

        val chain = getBlockChain()

        for (i in chain.indices) {
            val currentBlock = chain[i]
            val newNeighbors = ChainNeighbors(
                previousBlock1 = if (i > 0) chain[i - 1].id else null,
                previousBlock2 = if (i > 1) chain[i - 2].id else null,
                nextBlock1 = if (i < chain.size - 1) chain[i + 1].id else null,
                nextBlock2 = if (i < chain.size - 2) chain[i + 2].id else null
            )

            updateBlockNeighbors(currentBlock.id, newNeighbors)
        }
    }

    private fun updateBlockNeighbors(blockId: String, neighbors: ChainNeighbors) {
        val index = _placedBlocks.indexOfFirst { it.id == blockId }
        if (index != -1) {
            _placedBlocks[index] = _placedBlocks[index].copy(chainNeighbors = neighbors)
        }
    }

    private fun clearBlockNeighbors(blockId: String) {
        val index = _placedBlocks.indexOfFirst { it.id == blockId }
        if (index != -1) {
            _placedBlocks[index] = _placedBlocks[index].copy(
                chainNeighbors = ChainNeighbors()
            )
        }
    }

    private fun insertBlockInChain(
        newBlockId: String,
        beforeBlockId: String?,
        afterBlockId: String?
    ) {
        when {
            beforeBlockId != null && afterBlockId != null -> {
                removeDirectConnection(beforeBlockId)
                addDirectConnection(beforeBlockId, newBlockId)
                addDirectConnection(newBlockId, afterBlockId)
            }

            beforeBlockId != null && afterBlockId == null -> {
                addDirectConnection(beforeBlockId, newBlockId)
            }
        }

        updateChainNeighbors()
    }

    private fun removeBlockFromChain(blockId: String) {
        val parentId = _directConnections.entries.find { it.value == blockId }?.key
        val childId = _directConnections[blockId]

        removeDirectConnection(blockId)
        parentId?.let { removeDirectConnection(it) }

        if (parentId != null && childId != null) {
            addDirectConnection(parentId, childId)
        }

        clearBlockNeighbors(blockId)
        updateChainNeighbors()
    }

    private fun addDirectConnection(parentId: String, childId: String) {
        _directConnections[parentId] = childId
    }

    private fun removeDirectConnection(parentId: String) {
        _directConnections.remove(parentId)
    }

    private fun handleBlockConnection(
        newBlockId: String,
        targetBlockId: String,
        connectionType: ConnectionType
    ) {
        _placedBlocks.find { it.id == targetBlockId } ?: return

        when (connectionType) {
            ConnectionType.Top -> {
                val beforeBlockId =
                    _directConnections.entries.find { it.value == targetBlockId }?.key
                insertBlockInChain(newBlockId, beforeBlockId, targetBlockId)
            }

            ConnectionType.Bottom -> {
                val afterBlockId = _directConnections[targetBlockId]
                insertBlockInChain(newBlockId, targetBlockId, afterBlockId)
            }
        }
    }

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

    private fun removeFunctionName(blockId: String) {
        _functionNames.remove(blockId)
    }

    private fun findDropZoneAtPosition(screenPosition: Offset): DropZoneTarget? {
        return _dropZoneTargets.filter { target ->
            val dropZoneRect = Rect(
                offset = target.position,
                size = target.size
            )
            dropZoneRect.contains(screenPosition)
        }
            .minByOrNull { it.size.width * it.size.height }
    }

    private fun findBlockInDropZones(blockId: String): PlacedBlockUI? {
        _dropZoneContents.values.forEach { blockList ->
            val foundBlock = blockList.find { it.id == blockId }
            if (foundBlock != null) {
                return foundBlock
            }
        }
        return null
    }

    private fun setOwnerIdForNestedBlocks(blockUI: BlockUI, blockId: String) {
        when (blockUI) {
            is IfBlockUI -> blockUI.setOwnerId(blockId)
            is ForBlockUI -> blockUI.setOwnerId(blockId)
            is WhileBlockUI -> blockUI.setOwnerId(blockId)
            is FunctionDeclarationBlockUI -> blockUI.setOwnerId(blockId)
            is TryCatchBlockUI -> blockUI.setOwnerId(blockId)
        }
    }

    private fun addBlockToDropZone(blockId: String, dropZoneTarget: DropZoneTarget): Boolean {
        val block = _placedBlocks.find { it.id == blockId } ?: return false

        removeBlockFromChain(blockId)
        _placedBlocks.removeIf { it.id == blockId }

        if (block.type == BlockType.FunctionDeclaration) {
            removeFunctionName(blockId)
        }

        if (dropZoneTarget.multipleBlocks) {
            _dropZoneContents.getOrPut(dropZoneTarget.id) { mutableListOf() }.add(block)
        } else {
            _dropZoneContents[dropZoneTarget.id] = mutableListOf(block)
        }

        addToUIBlockBody(block, dropZoneTarget)

        return true
    }

    fun removeBlockFromDropZone(dropZoneId: String, blockId: String? = null) {
        val blocks = _dropZoneContents[dropZoneId] ?: return
        val zone = _dropZoneTargets.find { it.id == dropZoneId }

        if (blockId != null) {
            val blockToRemove = blocks.find { it.id == blockId }
            if (blockToRemove != null && zone != null) {
                removeFromUIBlockBody(blockToRemove, zone)
            }

            blocks.removeIf { it.id == blockId }
            if (blocks.isEmpty()) {
                _dropZoneContents.remove(dropZoneId)
            }
        } else {
            if (zone != null) {
                blocks.forEach { block ->
                    removeFromUIBlockBody(block, zone)
                }
            }
            _dropZoneContents.remove(dropZoneId)
        }
    }

    fun addToFieldFromDropZone(block: PlacedBlockUI, dropZoneId: String) {
        val zone = _dropZoneTargets.find { it.id == dropZoneId }
        val fieldPosition = zone?.position ?: Offset.Zero

        zone?.let {
            removeFromUIBlockBody(block, it)
        }

        _placedBlocks.add(
            block.copy(
                position = fieldPosition,
                isConnected = false,
                parentConnection = null,
                chainNeighbors = ChainNeighbors()
            )
        )
    }

    fun getDropZoneContents(dropZoneId: String): List<PlacedBlockUI?> {
        return _dropZoneContents[dropZoneId] ?: emptyList()
    }

    fun registerDropZone(dropZone: DropZoneTarget) {
        _dropZoneTargets.removeIf { it.id == dropZone.id }
        _dropZoneTargets.add(dropZone)
    }

    fun unregisterDropZone(dropZoneId: String) {
        _dropZoneTargets.removeIf { it.id == dropZoneId }
    }

    fun startDragging(block: BlockItemData, initialOffset: Offset) {
        _draggedBlock.value = block
        _dragPosition.value = initialOffset
        _isDragging.value = true
        _draggedPlacedBlockId.value = null
    }

    fun startDraggingPlacedBlock(blockId: String, initialOffset: Offset) {
        val block = _placedBlocks.find { it.id == blockId } ?: return

        clearBlockNeighbors(blockId)
        removeBlockFromChain(blockId)

        _draggedBlock.value = BlockItemData(
            type = block.type,
            label = block.type.name,
            color = BlockOrange
        )
        _dragPosition.value = initialOffset
        _isDragging.value = true
        _draggedPlacedBlockId.value = blockId
    }

    fun updatePosition(newPosition: Offset) {
        if (_isDragging.value) {
            _dragScreenPosition.value = newPosition


            val fieldPosition = screenToFieldCoords(newPosition, _fieldOffset, _fieldScale)
            _dragPosition.value = fieldPosition

            val draggedBlock = _draggedBlock.value
            val draggedBlockId = _draggedPlacedBlockId.value

            if (draggedBlock != null) {
                findNearbyConnectionPoint(fieldPosition, draggedBlockId)


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
        val fieldPosition = _dragPosition.value
        val screenPosition = _dragScreenPosition.value
        val placedBlockId = _draggedPlacedBlockId.value
        val nearbyConnection = _nearbyConnectionPoint.value

        if (currentBlock != null) {
            val dropZoneTarget = findDropZoneAtPosition(screenPosition)
            val isValid = dropZoneTarget != null && (dropZoneTarget.acceptedTypes.isEmpty() ||
                    dropZoneTarget.acceptedTypes.contains(currentBlock.type))

            if (dropZoneTarget != null && isValid && placedBlockId != null) {
                addBlockToDropZone(placedBlockId, dropZoneTarget)
            } else if (placeOnField) {
                var finalPosition = fieldPosition
                var targetBlockId: String? = null
                var connectionType: ConnectionType? = null

                nearbyConnection?.let { nearbyConn ->
                    val targetBlock = _placedBlocks.find { it.id == nearbyConn.ownerBlockId }
                    targetBlock?.let { target ->
                        finalPosition = calculateSnapPosition(
                            target,
                            nearbyConn.connectionPoint,
                            currentBlock.type
                        )
                        targetBlockId = target.id
                        connectionType = nearbyConn.connectionPoint.type
                    }
                }

                if (placedBlockId != null) {
                    val index = _placedBlocks.indexOfFirst { it.id == placedBlockId }
                    if (index >= 0) {
                        _placedBlocks[index] = _placedBlocks[index].copy(
                            position = finalPosition,
                            isConnected = nearbyConnection != null
                        )

                        if (nearbyConnection != null && targetBlockId != null) {
                            handleBlockConnection(placedBlockId, targetBlockId!!, connectionType!!)
                        } else {
                            updateChainNeighbors()
                        }
                    }
                } else {
                    val newBlockId = UUID.randomUUID().toString()
                    val uiBlock = createUIBlockByType(currentBlock.type, newBlockId)
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

                    if (nearbyConnection != null && targetBlockId != null) {
                        handleBlockConnection(newBlockId, targetBlockId!!, connectionType!!)
                    } else {
                        updateChainNeighbors()
                    }
                }
            }
        }

        _draggedBlock.value = null
        _dragPosition.value = Offset.Zero
        _dragScreenPosition.value = Offset.Zero
        _isDragging.value = false
        _draggedPlacedBlockId.value = null
        _nearbyConnectionPoint.value = null
        _dropZoneHighlight.value = null
    }


    fun updateBlockSize(blockId: String, size: Size) {
        val index = _placedBlocks.indexOfFirst { it.id == blockId }
        if (index != -1) {
            val currentBlock = _placedBlocks[index]
            val updatedConnectionPoints =
                createConnectionPointsForBlock(currentBlock.type, size).map {
                    it.copy(blockId = blockId)
                }
            _placedBlocks[index] = currentBlock.copy(
                size = size,
                connectionPoints = updatedConnectionPoints
            )
        }
    }

    private fun createUIBlockByType(
        type: BlockType,
        blockId: String = UUID.randomUUID().toString()
    ): BlockUI {
        val uiBlock = when (type) {
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
            BlockType.Try_catch -> TryCatchBlockUI()
        }

        setOwnerIdForNestedBlocks(uiBlock, blockId)
        return uiBlock
    }

    private fun addToUIBlockBody(block: PlacedBlockUI, dropZoneTarget: DropZoneTarget) {
        val ownerBlock = _placedBlocks.find { it.id == dropZoneTarget.ownerBlockId }
            ?: findBlockInDropZones(dropZoneTarget.ownerBlockId) ?: return
        val dropZoneId = dropZoneTarget.id

        when {
            dropZoneId.startsWith("if_then_dropzone_") -> {
                val ifBlock = ownerBlock.uiBlock as? IfBlockUI
                ifBlock?.thenBlocks?.add(block.uiBlock)
            }

            dropZoneId.startsWith("if_else_dropzone_") -> {
                val ifBlock = ownerBlock.uiBlock as? IfBlockUI
                ifBlock?.elseBlocks?.add(block.uiBlock)
            }

            dropZoneId.startsWith("for_declare_dropzone_") -> {
                val forBlock = ownerBlock.uiBlock as? ForBlockUI
                forBlock?.declareBlock = block.uiBlock
            }

            dropZoneId.startsWith("for_assign_dropzone_") -> {
                val forBlock = ownerBlock.uiBlock as? ForBlockUI
                forBlock?.assignBlock = block.uiBlock
            }

            dropZoneId.startsWith("for_do_dropzone_") -> {
                val forBlock = ownerBlock.uiBlock as? ForBlockUI
                forBlock?.doBlocks?.add(block.uiBlock)
            }

            dropZoneId.startsWith("while_do_dropzone_") -> {
                val whileBlock = ownerBlock.uiBlock as? WhileBlockUI
                whileBlock?.doBlocks?.add(block.uiBlock)
            }

            dropZoneId.startsWith("function_body_dropzone_") -> {
                val funcBlock = ownerBlock.uiBlock as? FunctionDeclarationBlockUI
                funcBlock?.body?.add(block.uiBlock)
            }

            dropZoneId.startsWith("try_dropzone_") -> {
                val tryCatchBlock = ownerBlock.uiBlock as? TryCatchBlockUI
                tryCatchBlock?.tryBlocks?.add(block.uiBlock)
            }

            dropZoneId.startsWith("catch_dropzone_") -> {
                val tryCatchBlock = ownerBlock.uiBlock as? TryCatchBlockUI
                tryCatchBlock?.catchBlocks?.add(block.uiBlock)
            }

            else -> {
                ownerBlock.uiBlock.addToBody(block.uiBlock)
            }
        }

        setOwnerIdForNestedBlocks(block.uiBlock, block.id)
    }

    private fun removeFromUIBlockBody(block: PlacedBlockUI, dropZoneTarget: DropZoneTarget) {
        val ownerBlock = _placedBlocks.find { it.id == dropZoneTarget.ownerBlockId }
            ?: findBlockInDropZones(dropZoneTarget.ownerBlockId) ?: return
        val dropZoneId = dropZoneTarget.id

        when {
            dropZoneId.startsWith("if_then_dropzone_") -> {
                val ifBlock = ownerBlock.uiBlock as? IfBlockUI
                ifBlock?.thenBlocks?.remove(block.uiBlock)
            }

            dropZoneId.startsWith("if_else_dropzone_") -> {
                val ifBlock = ownerBlock.uiBlock as? IfBlockUI
                ifBlock?.elseBlocks?.remove(block.uiBlock)
            }

            dropZoneId.startsWith("for_declare_dropzone_") -> {
                val forBlock = ownerBlock.uiBlock as? ForBlockUI
                forBlock?.declareBlock = null
            }

            dropZoneId.startsWith("for_assign_dropzone_") -> {
                val forBlock = ownerBlock.uiBlock as? ForBlockUI
                forBlock?.assignBlock = null
            }

            dropZoneId.startsWith("for_do_dropzone_") -> {
                val forBlock = ownerBlock.uiBlock as? ForBlockUI
                forBlock?.doBlocks?.remove(block.uiBlock)
            }

            dropZoneId.startsWith("while_do_dropzone_") -> {
                val whileBlock = ownerBlock.uiBlock as? WhileBlockUI
                whileBlock?.doBlocks?.remove(block.uiBlock)
            }

            dropZoneId.startsWith("function_body_dropzone_") -> {
                val funcBlock = ownerBlock.uiBlock as? FunctionDeclarationBlockUI
                funcBlock?.body?.remove(block.uiBlock)
            }

            dropZoneId.startsWith("try_dropzone_") -> {
                val tryCatchBlock = ownerBlock.uiBlock as? TryCatchBlockUI
                tryCatchBlock?.tryBlocks?.remove(block.uiBlock)
            }

            dropZoneId.startsWith("catch_dropzone_") -> {
                val tryCatchBlock = ownerBlock.uiBlock as? TryCatchBlockUI
                tryCatchBlock?.catchBlocks?.remove(block.uiBlock)
            }

            else -> {
                ownerBlock.uiBlock.deleteToBody(block.uiBlock)
            }
        }
    }

    private fun createConnectionPointsForBlock(
        blockType: BlockType,
        size: Size? = null
    ): List<ConnectionPoint> {
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
                    if (distance < snapThreshold && distance < closestDistance) {
                        closestDistance = distance
                        closestPoint = connectionPoint
                        ownerBlockId = block.id
                    }
                }
            }
        }

        _nearbyConnectionPoint.value = if (closestDistance < snapThreshold) {
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
            existingBlock?.size ?: Size(
                getBlockWidth(draggingBlockType),
                getBlockHeight(draggingBlockType)
            )
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

    private fun screenToFieldCoords(
        screenPosition: Offset,
        fieldOffset: Offset,
        fieldScale: Float
    ): Offset {
        return (screenPosition - fieldOffset) / fieldScale
    }
}
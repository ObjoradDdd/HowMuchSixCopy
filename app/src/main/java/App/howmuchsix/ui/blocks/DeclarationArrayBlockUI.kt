package App.howmuchsix.ui.blocks

import App.howmuchsix.hms.Blocks.Block
import App.howmuchsix.hms.Blocks.DeclarationArrayBlock
import App.howmuchsix.hms.Blocks.Types
import App.howmuchsix.viewmodel.BlockEditorViewModel
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier

class DeclarationArrayBlockUI : BlockUI() {
    private var arrayName by mutableStateOf("")
    private var dataType by mutableStateOf<_types?>(null)
    private var length by mutableStateOf(0)
    private var values by mutableStateOf<List<String>?>(null)


    fun initializeFromBD(name: String, type: String, arrayLength: Int, valuesList: List<String>?) {
        arrayName = name
        dataType = try {
            _types.fromString(type)
        } catch (e: IllegalArgumentException) {
            _types.Int
        }
        length = arrayLength
        values = valuesList
    }


    override fun metamorphosis(): Block {
        if (dataType == null) {
            throw IllegalArgumentException("Type is required")
        }
        if (arrayName.isEmpty()) {
            throw IllegalArgumentException("Array name is required")
        }

        val types = dataType!!.toTypes()

        return when {
            values != null && length > 0 && values!!.size != length ->
                DeclarationArrayBlock(types, arrayName, length, values!!)
            values != null ->
                DeclarationArrayBlock(types, arrayName, values!!)
            else ->
                DeclarationArrayBlock(types, arrayName, length)
        }
    }

    @Composable
    override fun Render(modifier: Modifier, viewModel: BlockEditorViewModel?) {
        TODO("Not yet implemented")
    }

}
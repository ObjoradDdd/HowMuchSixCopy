package App.howmuchsix.ui.blocks

import App.howmuchsix.hms.Blocks.Block
import App.howmuchsix.hms.Blocks.DeclarationArrayBlock
import App.howmuchsix.hms.Blocks.Types
import App.howmuchsix.viewmodel.BlockEditorViewModel
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

class DeclarationArrayBlockUI : BlockUI() {
    override fun metamorphosis(params: HashMap<String, Any>): Block {

        val type = params["type"] as? Types ?: throw IllegalArgumentException("Type parameter is required")

        val name = params["name"] as? String ?: throw IllegalArgumentException("Name parameter is required")

        val valuesList = when (val values = params["values"]) {
            is List<*> -> values.filterIsInstance<String>()
            null -> null
            else -> null
        }

        val length = when (val len = params["length"]) {
            is Int -> len
            is String -> len.toIntOrNull() ?: 0
            null -> valuesList?.size ?: 0
            else -> 0
        }

        return when {
            valuesList != null && params.containsKey("length") ->
                DeclarationArrayBlock(type, name, length, valuesList)

            valuesList != null ->
                DeclarationArrayBlock(type, name, valuesList)

            else ->
                DeclarationArrayBlock(type, name, length)
        }
    }

    @Composable
    override fun Render(modifier: Modifier, viewModel: BlockEditorViewModel?) {
        TODO("Not yet implemented")
    }
}
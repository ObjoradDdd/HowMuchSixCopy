package App.howmuchsix.ui.blocks

import App.howmuchsix.hms.Blocks.Block
import App.howmuchsix.hms.Blocks.DeclarationBlock
import App.howmuchsix.hms.Blocks.Types

class DeclarationBlockUI : BlockUI {
    override fun metamorphosis(params: HashMap<String, Any>): Block {
        val names = params["names"]?.let { namesValue ->
            when (namesValue) {
                is List<*> -> namesValue.filterIsInstance<String>()
                else -> listOf()
            }
        } ?: listOf()

        val values = params["values"]?.let { valuesValue ->
            when (valuesValue) {
                is List<*> -> valuesValue.filterIsInstance<String>()
                else -> listOf()
            }
        } ?: listOf()

        val type = params["type"] as? Types

        return DeclarationBlock(names, values, type)
    }
}
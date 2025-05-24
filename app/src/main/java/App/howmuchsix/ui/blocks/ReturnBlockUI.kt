package App.howmuchsix.ui.blocks

import App.howmuchsix.hms.Blocks.Block
import App.howmuchsix.hms.Blocks.ReturnBlock
import App.howmuchsix.hms.Blocks.Types
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

class ReturnBlockUI : BlockUI() {
    override fun metamorphosis(params: HashMap<String, Any>): Block {
        if (params.isEmpty() || (params.containsKey("type") && params["type"] == Types.VOID)) {
            return ReturnBlock()
        }
        val returnType = params["type"] as? Types
            ?: throw IllegalArgumentException("Return type parameter is required for non-void returns")

        val value = params["value"]?.let {
            when (it) {
                is String -> it
                else -> it.toString()
            }
        } ?: throw IllegalArgumentException("Value parameter is required for non-void returns")

        return ReturnBlock(value, returnType)
    }

    @Composable
    override fun Render(modifier: Modifier) {
        TODO("Not yet implemented")
    }
}
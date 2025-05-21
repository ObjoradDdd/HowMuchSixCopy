package App.howmuchsix.ui.blocks

import App.howmuchsix.hms.Blocks.Block
import App.howmuchsix.hms.Blocks.FunctionBlock

class FunctionBlockUI : BlockUI {
    override fun metamorphosis(params: HashMap<String, Any>): Block {
        val functionName = params["function"] as? String
            ?: throw IllegalArgumentException("Function name parameter is required")

        return FunctionBlock(functionName)
    }
}
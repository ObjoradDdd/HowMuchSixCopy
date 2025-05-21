package App.howmuchsix.ui.blocks

import App.howmuchsix.hms.Blocks.Block
import App.howmuchsix.hms.Blocks.BreakBlock

class BreakBlockUI : BlockUI {
    override fun metamorphosis(params: HashMap<String, Any>): Block {
        return BreakBlock()
    }
}
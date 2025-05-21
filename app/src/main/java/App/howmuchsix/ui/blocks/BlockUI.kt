package App.howmuchsix.ui.blocks

import App.howmuchsix.hms.Blocks.Block

interface BlockUI {
    public fun metamorphosis(params: HashMap<String, Any>) : Block
}
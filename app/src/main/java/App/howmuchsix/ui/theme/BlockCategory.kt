package App.howmuchsix.ui.theme

import App.howmuchsix.viewmodel.BlockItemData
import androidx.compose.ui.graphics.Color

data class BlockCategory(
    val name: String,
    val blocks: List<BlockItemData>,
    val blockColor: Color
)

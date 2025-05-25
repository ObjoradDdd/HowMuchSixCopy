package App.howmuchsix.ui.theme

import App.howmuchsix.viewmodel.BlockItemData
import androidx.compose.ui.graphics.Color
import androidx.annotation.DrawableRes

data class BlockCategory(
    val name: String,
    val blocks: List<BlockItemData>,
    val blockColor: Color
)

data class BottomMenuContent(
    val title: String,
    @DrawableRes val iconId: Int
)



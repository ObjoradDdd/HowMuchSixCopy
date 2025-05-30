package App.howmuchsix.ui.theme

import App.howmuchsix.ui.theme.design_elements.LighterBeige
import App.howmuchsix.ui.theme.design_elements.SubTitle2
import App.howmuchsix.ui.theme.design_elements.size16
import App.howmuchsix.ui.theme.design_elements.size500
import App.howmuchsix.ui.theme.design_elements.size8
import App.howmuchsix.viewmodel.BlockItemData
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset


@Composable
fun BlockPanel(
    categories: List<BlockCategory>,
    onBlockStartDrag: (BlockItemData, Offset) -> Unit,
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .height(size500)
            .padding(size16),
        color = LighterBeige,
        shape = RoundedCornerShape(size16),
    ) {
        LazyColumn(modifier = Modifier.padding(size16)) {
            items(categories) { category ->
                CategorySection(
                    category = category,
                    onBlockStartDrag = onBlockStartDrag
                )
            }
        }
    }
}

@Composable
private fun CategorySection(
    category: BlockCategory,
    onBlockStartDrag: (BlockItemData, Offset) -> Unit
) {
    Column {
        Text(
            text = category.name,
            style = SubTitle2,
            modifier = Modifier.padding(vertical = size8)
        )
        category.blocks.forEach { block ->
            BlockItem(
                block = block,
                color = category.blockColor,
                onStartDrag = onBlockStartDrag
            )
            Spacer(modifier = Modifier.height(size8))
        }
    }
}
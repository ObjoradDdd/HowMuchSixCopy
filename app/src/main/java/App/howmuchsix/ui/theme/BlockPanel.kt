package App.howmuchsix.ui.theme

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
import androidx.compose.ui.unit.dp
import App.howmuchsix.ui.theme.design_elements.*
import App.howmuchsix.viewmodel.ConsoleViewModel
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue


@Composable
fun BlockPanel(
    categories: List<BlockCategory>,
    onBlockStartDrag: (BlockItemData, Offset) -> Unit,
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .height(500.dp)
            .padding(16.dp),
        color = LighterBeige,
        shape = RoundedCornerShape(16.dp),
    ) {
        LazyColumn(modifier = Modifier.padding(16.dp)) {
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
            modifier = Modifier.padding(vertical = 8.dp)
        )
        category.blocks.forEach { block ->
            BlockItem(
                block = block,
                color = category.blockColor,
                onStartDrag = onBlockStartDrag
            )
            Spacer(modifier = Modifier.height(8.dp))
        }
    }
}
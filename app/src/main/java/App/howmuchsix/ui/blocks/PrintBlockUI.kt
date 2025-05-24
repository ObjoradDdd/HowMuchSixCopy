package App.howmuchsix.ui.blocks

import App.howmuchsix.hms.Blocks.Block
import App.howmuchsix.hms.Blocks.PrintBlock
import App.howmuchsix.ui.theme.ButtonTextField
import App.howmuchsix.ui.theme.design_elements.BlockPink
import App.howmuchsix.ui.theme.design_elements.BlockYellow
import App.howmuchsix.ui.theme.design_elements.SubTitle1
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

class PrintBlockUI : BlockUI() {
    private var value by mutableStateOf("")

    @Composable
    override fun Render(modifier: Modifier) {
        Row (
            modifier = modifier
                .background(BlockPink, RoundedCornerShape(8.dp))
                .padding(8.dp)
        ){
            Spacer(Modifier.width(8.dp))

            Text(text = "Print",
                style = SubTitle1,
                modifier = Modifier.align(Alignment.CenterVertically)
            )
            Spacer(Modifier.width(8.dp))

            ButtonTextField(
                value = value,
                onValueChange = {value = it},
                textStyle = SubTitle1,
                placeholder = "value"
            )
        }
    }

    override fun metamorphosis(params: HashMap<String, Any>): Block {
        val output = params["output"]?.let {
            when (it) {
                is String -> it
                else -> it.toString()
            }
        } ?: throw IllegalArgumentException("Output parameter is required")
        return PrintBlock(output)
    }
}
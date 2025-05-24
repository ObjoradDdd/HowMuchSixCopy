package App.howmuchsix.ui.blocks

import App.howmuchsix.hms.Blocks.Block
import App.howmuchsix.hms.Blocks.DeclarationBlock
import App.howmuchsix.ui.theme.design_elements.BlockYellow
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import App.howmuchsix.ui.theme.ButtonTextField
import App.howmuchsix.ui.theme.design_elements.InputText
//import android.text.Layout.Alignment
import androidx.compose.material3.Text
import androidx.compose.ui.Alignment
import App.howmuchsix.ui.theme.DropDownMenuTypeSelector
import App.howmuchsix.hms.Blocks.Types
import App.howmuchsix.ui.theme.design_elements.ProjectTitle
import App.howmuchsix.ui.theme.design_elements.SubTitle1
import androidx.compose.foundation.layout.size

class DeclarationBlockUI : BlockUI() {
    private var name by mutableStateOf("")
    private var value by mutableStateOf("")
    private var selectedType by mutableStateOf<_types?>(null)

    @Composable
    override fun Render(modifier: Modifier){
        Row (
            modifier = modifier
                .background(BlockYellow, RoundedCornerShape(8.dp))
                .padding(8.dp)
        ){
            DropDownMenuTypeSelector(
                selectedType = selectedType,
                onTypeSelected = { selectedType = it}
            )
            Spacer(Modifier.width(8.dp))
            ButtonTextField(
                value = name,
                onValueChange = {name = it},
                textStyle = SubTitle1,
                placeholder = "name"
            )

            Spacer(Modifier.width(8.dp))
            Text(text ="=",
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
package App.howmuchsix.ui.blocks

import App.howmuchsix.hms.Blocks.AssignmentBlock
import App.howmuchsix.hms.Blocks.Block
import App.howmuchsix.hms.Blocks.DeclarationBlock
import App.howmuchsix.hms.Blocks.Types
import App.howmuchsix.ui.theme.ButtonTextField
import App.howmuchsix.ui.theme.DropDownMenuTypeSelector
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

class AssignmentBlockUI : BlockUI() {
    private var name by mutableStateOf("")
    private var value by mutableStateOf("")

    @Composable
    override fun Render(modifier: Modifier){
        Row (
            modifier = modifier
                .background(BlockYellow, RoundedCornerShape(8.dp))
                .padding(8.dp)
        ){
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

        val variable = params["variable"] as? String
            ?: throw IllegalArgumentException("Variable name parameter is required")

        val value = params["value"]?.let {
            when (it) {
                is String -> it
                else -> it.toString()
            }
        } ?: throw IllegalArgumentException("Value parameter is required")

        return AssignmentBlock(variable, value)
    }
}
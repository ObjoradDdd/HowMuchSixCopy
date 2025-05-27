package App.howmuchsix.ui.blocks

import App.howmuchsix.hms.Blocks.AssignmentBlock
import App.howmuchsix.hms.Blocks.Block
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier

class AssignmentBlockUI : BlockUI() {

    private var name by mutableStateOf("")
    private var value by mutableStateOf("")

    fun initializeFromBD(variable: String, assignedValue: String) {
        name = variable
        value = assignedValue
    }


    override fun metamorphosis(): Block {
        if (name.isEmpty()) {
            throw IllegalArgumentException("Variable name is required")
        }
        if (value.isEmpty()) {
            throw IllegalArgumentException("Value is required")
        }
        return AssignmentBlock(name, value)
    }

    @Composable
    override fun Render(modifier: Modifier) {
        TODO("Not yet implemented")
    }
}
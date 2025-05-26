package App.howmuchsix.ui.console

import App.howmuchsix.ui.theme.design_elements.LighterBeige
import App.howmuchsix.viewmodel.ConsoleViewModel
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun Console(consoleViewModel: ConsoleViewModel) {
    val state by consoleViewModel.state.collectAsState()

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .height(500.dp)
            .padding(16.dp),
        color = LighterBeige,
        shape = RoundedCornerShape(16.dp),
    ) {
        Text(text = state.text)
    }
}
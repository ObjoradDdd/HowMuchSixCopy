package App.howmuchsix.ui.console

import App.howmuchsix.ui.theme.design_elements.LighterBeige
import App.howmuchsix.ui.theme.design_elements.SubTitle2
import App.howmuchsix.ui.theme.design_elements.consoleOutput
import App.howmuchsix.ui.theme.design_elements.size16
import App.howmuchsix.ui.theme.design_elements.size8
import App.howmuchsix.viewmodel.ConsoleViewModel
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
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
        LazyColumn(modifier = Modifier.padding(size16)) {
            item {
                Column {
                    Text(
                        text = "Console",
                        style = SubTitle2,
                    )
                    Spacer(Modifier.height(size8))
                    Text(
                        text = state.text,
                        style = consoleOutput
                    )
                    Spacer(Modifier.height(size8))
                }
            }
        }
    }
}
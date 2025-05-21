package App.howmuchsix

import App.howmuchsix.hms.Blocks.*
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import App.howmuchsix.ui.theme.screens.ProjectsScreen
import App.howmuchsix.ui.theme.screens.StartingScreen
import App.howmuchsix.ui.theme.screens.WorkingScreen
import androidx.compose.material3.Surface

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            Surface {
                //StartingScreen()
                //ProjectsScreen()
                WorkingScreen()
                val program : MutableList<Block> = mutableListOf()
            }
        }
    }
}

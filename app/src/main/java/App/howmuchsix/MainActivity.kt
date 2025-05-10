package App.howmuchsix

import App.howmuchsix.hms.Blocks.AssignmentBlock
import App.howmuchsix.hms.Blocks.Block
import App.howmuchsix.hms.Blocks.DeclarationBlock
import App.howmuchsix.hms.Blocks.Types
import App.howmuchsix.hms.Library.Variables

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {

            var consoleString = ""

            val program: MutableList<Block> = mutableListOf()

            var wrongBlockNumber = 1


            runBlocking {
                launch {
                    try {

                        program.add(
                            DeclarationBlock(
                                listOf("x"),
                                arrayListOf("1.0"),
                                Types.DOUBLE
                            )
                        )

                        wrongBlockNumber++

                        program.add(
                            DeclarationBlock(
                                listOf("egf"),
                                arrayListOf("x"),
                                Types.STRING
                            )
                        )

                        program.add(
                            AssignmentBlock("egf", "egf + x")
                        )

                        wrongBlockNumber++

                        wrongBlockNumber = 1

                        for (block in program) {
                            block.Action()
                            wrongBlockNumber++
                        }

                        consoleString += Variables.get("egf")
                        consoleString += "\n"
                        consoleString += Variables.get("x")

                    } catch (e: Exception) {
                        consoleString = """                   
                        ${e.message}
                        Error in block $wrongBlockNumber
                    """.trimIndent()
                    }

                    println(consoleString)

                }
            }
            
            Greeting(consoleString)
        }
    }
}


@Preview(showBackground = true)
@Composable
fun Greeting(name: String = " ") {
    Column {
        Spacer(
            modifier = Modifier
                .height(100.dp)
                .fillMaxWidth()
        )
        Text(
            text = name,
            fontSize = 40.sp
        )
    }
}

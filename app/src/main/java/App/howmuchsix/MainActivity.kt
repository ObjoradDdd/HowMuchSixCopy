package App.howmuchsix



import App.howmuchsix.hms.Blocks.*
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
import kotlin.system.measureTimeMillis

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
                    //    try {
                    program.addAll(
                        listOf(
                            ForBlock(
                                DeclarationBlock("i", "0", Types.INT),
                                "number(i) < number(15)",
                                AssignmentBlock("i", "i+1"),
                                listOf(
                                    PrintBlock("number(i)"),
                                    IfBlock("number(i) == number(8)", listOf(PrintBlock("str('END')"), BreakBlock()))
                                )
                            )
                        )
                    )

                    wrongBlockNumber++

                    wrongBlockNumber = 1
                    val timeMillis = measureTimeMillis {
                        for (block in program) {
                            block.Action(listOf("MainScope"))
                            wrongBlockNumber++
                        }
                    }



                    consoleString += "\n"


                    consoleString += "\n $timeMillis"

                    /*       } catch (e: Exception) {
                                consoleString = """
                            ${e.message}
                            Error in block $wrongBlockNumber
                        """.trimIndent()
                            }*/
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
            fontSize = 30.sp
        )
    }
}

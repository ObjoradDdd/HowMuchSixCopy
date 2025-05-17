package App.howmuchsix

import App.howmuchsix.hms.Blocks.AssignmentBlock
import App.howmuchsix.hms.Blocks.Block
import App.howmuchsix.hms.Blocks.DeclarationArrayBlock
import App.howmuchsix.hms.Blocks.DeclarationBlock
import App.howmuchsix.hms.Blocks.ForBlock
import App.howmuchsix.hms.Blocks.FunctionDeclarationBlock
import App.howmuchsix.hms.Blocks.IfBlock
import App.howmuchsix.hms.Blocks.ReturnBlock
import App.howmuchsix.hms.Blocks.Types
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

                            FunctionDeclarationBlock(
                                Types.ARRAY,
                                "BubbleSort",
                                listOf(Types.ARRAY, Types.INT),
                                listOf("Numbers", "n"),
                                listOf(
                                    ForBlock(
                                        DeclarationBlock("i", "0", Types.INT),
                                        "number(i) < number(n)",
                                        AssignmentBlock("i", "i+1"),
                                        listOf(
                                            ForBlock(
                                                DeclarationBlock("j", "0", Types.INT),
                                                "number(j) < number(n-1)",
                                                AssignmentBlock("j", "j+1"),
                                                listOf(
                                                    IfBlock(
                                                        "number(Numbers[j]) > number(Numbers[j+1]))",
                                                        listOf(
                                                            DeclarationBlock(
                                                                "temporary",
                                                                "Numbers[j]",
                                                                Types.INT
                                                            ),
                                                            AssignmentBlock(
                                                                "Numbers[j]",
                                                                "Numbers[j+1]"
                                                            ),
                                                            AssignmentBlock(
                                                                "Numbers[j+1]",
                                                                "temporary"
                                                            )
                                                        )
                                                    )
                                                )
                                            )
                                        )
                                    ),
                                    ReturnBlock("Numbers")
                                ),
                            ),
                            DeclarationBlock("x", "20", Types.INT),

                            DeclarationArrayBlock(
                                Types.INT, "N", listOf(
                                    "5", "4", "3", "2", "1",
                                    "20", "19", "18", "17", "16",
                                    "15", "14", "13", "12", "11",
                                    "10", "9", "8", "7", "6"
                                )
                            ),

                            AssignmentBlock(
                                "N", "BubbleSort(N, x)"
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

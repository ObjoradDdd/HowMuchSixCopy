package App.howmuchsix

import App.howmuchsix.hms.Blocks.AssignmentBlock
import App.howmuchsix.hms.Blocks.Block
import App.howmuchsix.hms.Blocks.DeclarationArrayBlock
import App.howmuchsix.hms.Blocks.DeclarationBlock
import App.howmuchsix.hms.Blocks.ForBlock
import App.howmuchsix.hms.Blocks.FunctionBlock
import App.howmuchsix.hms.Blocks.FunctionDeclarationBlock
import App.howmuchsix.hms.Blocks.IfBlock
import App.howmuchsix.hms.Blocks.PrintBlock
import App.howmuchsix.hms.Blocks.ReturnBlock
import App.howmuchsix.hms.Blocks.Types
import App.howmuchsix.hms.Blocks.WhileBlock
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
                                Types.INT,
                                "BinarySearch",
                                listOf(Types.ARRAY, Types.INT, Types.INT),
                                listOf("Numbers", "n", "target"),
                                listOf(
                                    DeclarationBlock(listOf("l", "r"), listOf("0","n-1"), Types.INT),
                                    WhileBlock("number(l) <= number(r)", listOf(
                                        DeclarationBlock("mid", "(l + r) / 2", Types.INT),
                                        IfBlock(
                                            "number(Numbers[mid]) == number(target)",
                                            listOf(ReturnBlock("mid", Types.INT)),
                                            listOf("number(Numbers[mid]) > number(target)"),
                                            listOf(listOf(AssignmentBlock("r", "mid - 1"))),
                                            listOf(AssignmentBlock("l", "mid + 1"))
                                            )
                                        )
                                    ),
                                    ReturnBlock("-1", Types.INT)
                                ),
                            ),

                            DeclarationBlock("x", "20", Types.INT),

                            DeclarationArrayBlock(
                                Types.INT, "N", listOf(
                                    "1", "2", "3", "4", "5",
                                    "6", "7", "8", "9", "10",
                                    "11", "12", "13", "14", "15",
                                    "16", "17", "18", "19", "20"
                                )
                            ),

                            FunctionBlock("BinarySearch(N, x, 9)"),

                            DeclarationBlock("index", "BinarySearch(N, x, 9)", Types.INT),

                            PrintBlock("str('index of 9 - ') number(index) ")
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

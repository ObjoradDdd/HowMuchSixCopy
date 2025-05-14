package App.howmuchsix


import App.howmuchsix.hms.Blocks.AssignmentBlock
import App.howmuchsix.hms.Blocks.Block
import App.howmuchsix.hms.Blocks.DeclarationBlock
import App.howmuchsix.hms.Blocks.FunctionBlock
import App.howmuchsix.hms.Blocks.FunctionDeclarationBlock
import App.howmuchsix.hms.Blocks.IfBlock
import App.howmuchsix.hms.Blocks.PrintBlock
import App.howmuchsix.hms.Blocks.ReturnBlock
import App.howmuchsix.hms.Blocks.Types
import App.howmuchsix.hms.Library.Variables
import kotlin.system.measureTimeMillis

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
                    //    try {
                            program.add(
                                DeclarationBlock(
                                    listOf("x"),
                                    listOf("21"),
                                    Types.DOUBLE
                                )
                            )

                            program.add(
                                FunctionDeclarationBlock(
                                    Types.INT,
                                    "Sum",
                                    listOf(Types.INT),
                                    listOf("y"),
                                    listOf(
                                        ReturnBlock("y + 1")
                                    )
                                )
                            )

                            program.add(
                                FunctionBlock(
                                    "Sum",
                                    listOf("")
                                )
                            )

                            program.add(
                                IfBlock(
                                    "number(x) == number(2)",
                                    listOf(
                                        DeclarationBlock(
                                            listOf("y"),
                                            listOf("Sum(4)"),
                                            Types.INT
                                        ),
                                        PrintBlock(
                                            "number(y)"
                                        )
                                    ),
                                    listOf(
                                        DeclarationBlock(
                                            listOf("z"),
                                            listOf("Sum(2)"),
                                            Types.INT
                                        ),
                                        PrintBlock(
                                            "number(z)"
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

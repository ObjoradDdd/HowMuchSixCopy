package App.howmuchsix


import App.howmuchsix.navigation.Navigation
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.material3.Surface

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            Surface {
                Navigation()
                //StartingScreen()
                //ProjectsScreen()
                //WorkingScreen()
                /*val Program : MutableList<Block> = mutableListOf()
                Program.addAll(
                    listOf(

                        FunctionDeclarationBlock(
                            Types.INT,
                            "BinarySearch",
                            listOf(Types.ARRAY, Types.INT, Types.INT),
                            listOf("Numbers", "n", "target"),
                            listOf(
                                DeclarationBlock(
                                    listOf("l", "r"),
                                    listOf("0", "n-1"),
                                    Types.INT
                                ),
                                WhileBlock(
                                    "number(l) <= number(r)", listOf(
                                        DeclarationBlock(listOf("mid"), listOf("(l + r) / 2"), Types.INT),
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

                        DeclarationBlock(listOf("x"), listOf("20"), Types.INT),

                        DeclarationArrayBlock(
                            Types.INT, "N", listOf(
                                "1", "2", "3", "4", "5",
                                "6", "7", "8", "9", "10",
                                "11", "12", "13", "14", "15",
                                "16", "17", "18", "19", "20"
                            )
                        ),

                        FunctionBlock("BinarySearch(N, x, 9)"),

                        DeclarationBlock(listOf("index"), listOf("BinarySearch(N, x, 9)"), Types.INT),

                        PrintBlock("str('index of 9 - ') number(index) ")
                    )
                )*/

            }
        }
    }
}

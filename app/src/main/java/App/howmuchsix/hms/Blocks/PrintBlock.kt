package App.howmuchsix.hms.Blocks

import App.howmuchsix.hms.Expression.Expression
import App.howmuchsix.hms.Handlers.Lexer
import App.howmuchsix.hms.Handlers.Parser
import App.howmuchsix.hms.Handlers.Token
import App.howmuchsix.hms.Library.Variables
import App.howmuchsix.viewmodel.ConsoleViewModel

class PrintBlock(private val output: String, private val consoleViewModel: ConsoleViewModel) :
    Block() {
    override fun Action(scopes: List<String>, lib: Variables) {
        val tokens: List<Token> = Lexer(output).tokenizeInterpolation()
        val outputString: Expression<String> =
            Parser(tokens, scopes, lib).parseStringInterpolation()
        println(outputString.eval())
        consoleViewModel.updateConsole(outputString.eval())
    }
}
package App.howmuchsix.viewmodel

import App.howmuchsix.hms.Blocks.Block
import App.howmuchsix.hms.Library.Variables
import App.howmuchsix.ui.blocks.BlockUI
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class InterpreterViewModel(
    private val consoleViewModel: ConsoleViewModel,
    private val blockEditorViewModel: BlockEditorViewModel
) : ViewModel() {

    private val _isRunning = MutableStateFlow(false)
    val isRunning: StateFlow<Boolean> = _isRunning.asStateFlow()


    fun runProgram() {
        viewModelScope.launch {
            _isRunning.value = true

            consoleViewModel.addToConsole("\nStarting program execution...")

            val blocks : List<Block>

            try {
                blocks = withContext(Dispatchers.Default) {
                    blockEditorViewModel.getBlockChain().drop(1).map { it.uiBlock }
                        .map { it.metamorphosis(consoleViewModel) }
                }
            }
            catch (e : Exception){
                withContext(Dispatchers.Main) {
                    consoleViewModel.addToConsole("\n${e.message}")
                    consoleViewModel.addToConsole("\nProgram failed")
                }
                return@launch
            }

            withContext(Dispatchers.IO) {
                executeProgram(blocks)
            }
            _isRunning.value = false
        }
    }

    private suspend fun executeProgram(blocks: List<Block>) {
        withContext(Dispatchers.IO) {
            var isSuccess = true
            val localeLib: Variables = Variables()
            for (block in blocks) {
                try {
                    block.Action(listOf("MainScope"), localeLib)
                } catch (e: Exception) {
                    isSuccess = false
                    withContext(Dispatchers.Main) {
                        consoleViewModel.addToConsole("\n${e.message}")
                    }
                    break
                }
            }
            if (isSuccess) {
                consoleViewModel.addToConsole("\nProgram completed successfully!")
            } else {
                consoleViewModel.addToConsole("\nProgram failed")
            }
        }
    }

    fun stopExecution() {
        if (_isRunning.value) {
            consoleViewModel.addToConsole("Execution stopped by user")
            _isRunning.value = false
        }
    }
}
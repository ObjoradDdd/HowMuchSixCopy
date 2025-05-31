package App.howmuchsix.viewmodel

import App.howmuchsix.hms.Blocks.Block
import App.howmuchsix.hms.Blocks.ProgramRunException
import App.howmuchsix.hms.Library.Variables
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.NonCancellable.isActive
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.concurrent.atomic.AtomicBoolean

class InterpreterViewModel(
    private val consoleViewModel: ConsoleViewModel,
    private val blockEditorViewModel: BlockEditorViewModel
) : ViewModel() {

    private val _isRunning = MutableStateFlow(false)
    val isRunning: StateFlow<Boolean> = _isRunning.asStateFlow()

    private var programJob: Job? = null
    private var executionThread: Thread? = null
    private val forceStop = AtomicBoolean(false)

    fun runProgram() {
        stopExecution()


        forceStop.set(false)

        programJob = viewModelScope.launch {
            blockEditorViewModel.clearBlocks()

            _isRunning.value = true

            consoleViewModel.addToConsole("\nStarting program execution...")

            val blocks: List<Block>
            val functions: List<Block>

            try {
                functions = withContext(Dispatchers.Default) {
                    blockEditorViewModel.placedBlocks.filter { it.type == BlockType.FunctionDeclaration }
                        .map { it.uiBlock }
                        .map { it.metamorphosis(consoleViewModel) }
                }
            } catch (e: ProgramRunException) {
                withContext(Dispatchers.Main) {
                    blockEditorViewModel.markBlock(e.id)
                    consoleViewModel.addToConsole("\n${e.message}")
                    consoleViewModel.addToConsole("\nProgram failed")
                }
                _isRunning.value = false
                executionThread = null
                return@launch
            }


            try {
                blocks = withContext(Dispatchers.Default) {
                    blockEditorViewModel.getBlockChain().drop(1).map { it.uiBlock }
                        .map { it.metamorphosis(consoleViewModel) }
                }
            } catch (e: ProgramRunException) {
                withContext(Dispatchers.Main) {
                    blockEditorViewModel.markBlock(e.id)
                    consoleViewModel.addToConsole("\n${e.message}")
                    consoleViewModel.addToConsole("\nProgram failed")
                }
                _isRunning.value = false
                executionThread = null
                return@launch
            }

            val program = functions + blocks

            try {
                withContext(Dispatchers.IO) {
                    executionThread = Thread.currentThread()
                    executeProgram(program)
                }
            } catch (e: InterruptedException) {
                consoleViewModel.addToConsole("\nProgram execution was interrupted")
            } catch (e: Exception) {
                consoleViewModel.addToConsole("\nProgram execution failed with error: ${e.message}")
            } finally {
                _isRunning.value = false
                executionThread = null
            }
        }
    }

    private suspend fun executeProgram(blocks: List<Block>) {
        try {
            var isSuccess = true
            val localeLib: Variables = Variables()

            for (block in blocks) {
                if (forceStop.get() ||
                    Thread.currentThread().isInterrupted ||
                    !isActive
                ) {

                    withContext(Dispatchers.Main) {
                        consoleViewModel.addToConsole("\nProgram execution stopped")
                    }
                    return
                }

                try {

                    block.Action(listOf("MainScope"), localeLib)


                    if (forceStop.get() || Thread.currentThread().isInterrupted) {
                        withContext(Dispatchers.Main) {
                            consoleViewModel.addToConsole("\nProgram execution stopped")
                        }
                        return
                    }

                } catch (e: InterruptedException) {

                    withContext(Dispatchers.Main) {
                        consoleViewModel.addToConsole("\nProgram execution interrupted")
                    }
                    Thread.currentThread().interrupt()
                    return
                } catch (e: ProgramRunException) {
                    isSuccess = false
                    withContext(Dispatchers.Main) {
                        consoleViewModel.addToConsole("\n${e.message}")
                        blockEditorViewModel.markBlock(e.id)
                    }
                    break
                } catch (e: Exception) {
                    isSuccess = false
                    withContext(Dispatchers.Main) {
                        consoleViewModel.addToConsole("\nUnexpected error: ${e.message}")
                    }
                    break
                }
            }


            if (!forceStop.get() && !Thread.currentThread().isInterrupted && isActive) {
                withContext(Dispatchers.Main) {
                    if (isSuccess) {
                        consoleViewModel.addToConsole("\nProgram completed successfully!")
                    } else {
                        consoleViewModel.addToConsole("\nProgram failed")
                    }
                }
            }

        } catch (e: InterruptedException) {
            withContext(Dispatchers.Main) {
                consoleViewModel.addToConsole("\nProgram execution forcibly stopped")
            }
            Thread.currentThread().interrupt()
        } catch (e: Exception) {
            withContext(Dispatchers.Main) {
                consoleViewModel.addToConsole("\nCritical error during execution: ${e.message}")
            }
        }
    }

    fun stopExecution() {
        forceStop.set(true)

        executionThread?.interrupt()

        programJob?.cancel()

        if (_isRunning.value) {
            _isRunning.value = false
            consoleViewModel.addToConsole("\nProgram execution stopped by user")
        }

        executionThread = null
    }

    override fun onCleared() {
        super.onCleared()
        stopExecution()
    }
}
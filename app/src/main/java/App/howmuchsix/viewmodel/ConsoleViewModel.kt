package App.howmuchsix.viewmodel

import App.howmuchsix.ui.console.ConsoleState
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class ConsoleViewModel : ViewModel() {
    private val _state = MutableStateFlow(ConsoleState())
    val state: StateFlow<ConsoleState> = _state.asStateFlow()

    fun addToConsole(newText: String) {
        _state.update { currentState ->
            val newContent = if (currentState.text.isEmpty()) {
                newText
            } else {
                "${currentState.text}\n$newText"
            }
            currentState.copy(text = newContent)
        }
    }

    fun updateConsole(newText: String) {
        _state.update { currentState ->
            currentState.copy(text = newText)
        }
    }

    fun clearConsole() {
        _state.update { currentState ->
            currentState.copy(text = "")
        }
    }
}
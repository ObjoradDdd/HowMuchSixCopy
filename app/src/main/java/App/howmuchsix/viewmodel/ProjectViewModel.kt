package App.howmuchsix.viewmodel

import App.howmuchsix.MainActivity
import App.howmuchsix.localeDataStorage.ProjectRepository
import App.howmuchsix.localeDataStorage.project.Project
import android.annotation.SuppressLint
import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

object ProjectRepositoryProvider {
    val repository = ProjectRepository()
}

class ProjectViewModel(application: Application) : AndroidViewModel(application) {
    private val repository = ProjectRepositoryProvider.repository
    init {
        repository.loadFromPrefs(getApplication())
    }

    fun getProjects(): StateFlow<List<Project>> {
        return repository.projects
    }

    fun addProject(project: Project) {
        repository.addProject(getApplication(), project)
    }

    fun updateProject(project: Project) {
        repository.updateProject(getApplication(), project)
    }

    fun deleteProject(project: Project) {
        repository.deleteProject(getApplication(), project)
    }
}
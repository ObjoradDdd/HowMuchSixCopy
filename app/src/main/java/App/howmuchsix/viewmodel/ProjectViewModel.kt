package App.howmuchsix.viewmodel


import App.howmuchsix.localeDataStorage.ProjectRepository
import App.howmuchsix.localeDataStorage.project.Project
import android.app.Application
import androidx.lifecycle.AndroidViewModel
import kotlinx.coroutines.flow.StateFlow


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
    
    fun deleteProject(project: Project) {
        repository.deleteProject(getApplication(), project)
    }
}
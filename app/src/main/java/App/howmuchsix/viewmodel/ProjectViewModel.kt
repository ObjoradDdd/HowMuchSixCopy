package App.howmuchsix.viewmodel


import App.howmuchsix.localeDataStorage.ProjectRepository
import App.howmuchsix.localeDataStorage.project.Project
import android.app.Application
import androidx.lifecycle.AndroidViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import java.util.UUID


class ProjectViewModel(application: Application) : AndroidViewModel(application) {
    private val repository = ProjectRepository()
    init {
        repository.loadFromPrefs(getApplication())
    }

    fun getProjects(): StateFlow<List<Project>> {
        return repository.projects
    }

    fun getProjectByID(uuid: UUID): Flow<Project?> {
        return repository.projects.map { projects ->
            projects.find { project -> project.id == uuid.toString()}
        }
    }

    fun addProject(project: Project) {
        project.setUUID(UUID.randomUUID())
        repository.addProject(getApplication(), project)
    }
    
    fun deleteProject(project: Project) {
        repository.deleteProject(getApplication(), project)
    }
}
package App.howmuchsix.localeDataStorage

import android.content.Context
import androidx.datastore.dataStore
import App.howmuchsix.localeDataStorage.project.Project
import com.google.firebase.crashlytics.buildtools.reloc.com.google.common.reflect.TypeToken
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import com.google.gson.Gson

class ProjectRepository {
    private var _projects = MutableStateFlow<List<Project>>(emptyList())
    val projects = _projects.asStateFlow()

    fun loadFromPrefs(context: Context) {
        val prefs = context.getSharedPreferences("projects", Context.MODE_PRIVATE)
        val json = prefs.getString("projects_list", "[]")
        _projects.value = Gson().fromJson(json, object : TypeToken<List<Project>>() {}.type)
    }

    private fun saveToPrefs(context: Context) {
        val prefs = context.getSharedPreferences("projects", Context.MODE_PRIVATE)
        val json = Gson().toJson(_projects.value)
        prefs.edit().putString("projects_list", json).apply()
    }

    fun getProjectById(id: Int): Project? {
        return _projects.value.find { it.id == id }
    }

    fun addProject(context: Context, project: Project) {
        _projects.update { currentList -> currentList + project }
        saveToPrefs(context)
    }

    fun updateProject(context: Context, project: Project) {
        _projects.update { currentList ->
            currentList.map { if (it.id == project.id) project else it }
        }
        saveToPrefs(context)
    }

    fun deleteProject(context: Context, project: Project) {
        _projects.update { currentList ->
            currentList.filter { it.id != project.id }
        }
        saveToPrefs(context)
    }
}



package App.howmuchsix.localeDataStorage

import App.howmuchsix.localeDataStorage.project.Project
import android.content.Context
import com.google.firebase.crashlytics.buildtools.reloc.com.google.common.reflect.TypeToken
import com.google.gson.Gson
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

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


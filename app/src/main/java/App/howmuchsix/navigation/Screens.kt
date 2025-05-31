package App.howmuchsix.navigation

enum class Screens(name: String) {
    StartScreen("start_screen"),
    ProjectListScreen("project_list_screen"),
    ProjectScreen("project_screen");

    companion object {
        fun createProjectRoute(projectId: String): String {
            return "${ProjectScreen.name}/${projectId}"
        }
    }
}
package App.howmuchsix.navigation

import App.howmuchsix.ui.theme.screens.ProjectsScreen
import App.howmuchsix.ui.theme.screens.StartingScreen
import App.howmuchsix.ui.theme.screens.WorkingScreen
import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import java.util.UUID


@Composable
fun Navigation() {
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = Screens.StartScreen.name) {
        composable(route = Screens.StartScreen.name) {
            StartingScreen(navController = navController)
        }
        composable(route = Screens.ProjectListScreen.name) {
            ProjectsScreen(navController = navController)
        }
        composable(route = "${Screens.ProjectScreen.name}/{projectId}") { backStackEntry ->
            val projectIdString = backStackEntry.arguments?.getString("projectId")
            if (projectIdString != null) {
                WorkingScreen(navController = navController, projectId = projectIdString)
            } else {
                navController.navigate(Screens.ProjectListScreen.name)
            }
        }
    }
}
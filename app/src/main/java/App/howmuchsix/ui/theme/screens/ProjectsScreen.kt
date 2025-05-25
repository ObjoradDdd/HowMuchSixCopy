package App.howmuchsix.ui.theme.screens

import App.howmuchsix.R
import App.howmuchsix.localeDataStorage.ProjectRepository
import App.howmuchsix.localeDataStorage.project.Program
import App.howmuchsix.localeDataStorage.project.Project
import App.howmuchsix.navigation.Screens
import App.howmuchsix.ui.theme.CustomTextField
import App.howmuchsix.ui.theme.design_elements.Beige
import App.howmuchsix.ui.theme.design_elements.BlockOrange
import App.howmuchsix.ui.theme.design_elements.BlockPink
import App.howmuchsix.ui.theme.design_elements.BlockYellow
import App.howmuchsix.ui.theme.design_elements.FunTitle
import App.howmuchsix.ui.theme.design_elements.InputText
import App.howmuchsix.ui.theme.design_elements.LighterBeige
import App.howmuchsix.ui.theme.design_elements.ProjectTitle
import App.howmuchsix.ui.theme.design_elements.SubTitle2
import App.howmuchsix.ui.theme.design_elements.TextWhite
import App.howmuchsix.viewmodel.ProjectViewModel
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController

val cardColors = listOf(BlockOrange, BlockYellow, BlockPink)

@Composable
fun ProjectsScreen(
    navController: NavController,
    viewModel: ProjectViewModel = viewModel()
) {
    var isAddPanelVisible by remember { mutableStateOf(false) }
    var projects by remember { mutableStateOf(listOf<Project>()) }
    var projectList : List<Project> = mutableListOf()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Beige)
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 40.dp, start = 16.dp, end = 16.dp, bottom = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Your projects",
                    style = FunTitle,
                    modifier = Modifier.weight(1f)
                )

                AddButton(onClick = {
                    isAddPanelVisible = !isAddPanelVisible
                })
            }
            Box(modifier = Modifier.weight(1f)) {
                DisplayProjects(viewModel, navController =  navController)
            }
        }

        AnimatedVisibility(
            visible = isAddPanelVisible,
            enter = slideInVertically(initialOffsetY = { it }) + fadeIn(),
            exit = slideOutVertically(targetOffsetY = { it }) + fadeOut(),
            modifier = Modifier
                .align(Alignment.Center)
                .padding(horizontal = 16.dp)
            //.padding(bottom = 88.dp)
        ) {
            AddPanel(
                onAddProject = { title, description ->
                    if (title.isNotBlank()) {
                        viewModel.addProject(Project(title, description, program = Program(emptyList())))
                        isAddPanelVisible = false
                    }
                },
                onDismiss = {
                    isAddPanelVisible = false
                }
            )
        }
    }
}

@Composable
fun DisplayProjects(viewModel: ProjectViewModel = viewModel(), navController: NavController) {
    val projects by viewModel.getProjects().collectAsState()
    if (projects.isEmpty()) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 200.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Image(
                painter = painterResource(id = R.drawable.emptyicon),
                contentDescription = null,
                modifier = Modifier.size(128.dp)
            )
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = "Nothing here yet...",
                style = SubTitle2
            )
        }
    } else {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            itemsIndexed(projects) { index, project ->
                val cardColor = cardColors[index % cardColors.size]
                ProjectCard(project, cardColor, navController = navController)
            }
        }
    }
}

@Composable
fun AddButton(onClick: () -> Unit) {
    Image(
        painter = painterResource(id = R.drawable.iconsix),
        contentDescription = "Add project",
        modifier = Modifier
            .size(85.dp)
            .clip(CircleShape)
            .clickable(onClick = onClick)
    )
}

@Composable
fun AddPanel(
    modifier: Modifier = Modifier,
    onAddProject: (String, String) -> Unit,
    onDismiss: () -> Unit
) {
    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    Surface(
        modifier = modifier
            .padding(horizontal = 16.dp, vertical = 12.dp)
            .fillMaxWidth()
            .height(290.dp),
        color = LighterBeige,
        shape = RoundedCornerShape(16.dp),
        shadowElevation = 4.dp
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                Image(
                    painter = painterResource(id = R.drawable.ic_cross),
                    contentDescription = "Add project",
                    modifier = Modifier
                        .size(20.dp)
                        .clickable(onClick = onDismiss)
                )
            }
            CustomTextField(
                value = title,
                onValueChange = { title = it },
                label = "Project name",
                modifier = Modifier.padding(8.dp),
            )
            Spacer(modifier = Modifier.height(8.dp))
            CustomTextField(
                value = description,
                onValueChange = { description = it },
                label = "Description",
                modifier = Modifier.padding(8.dp),
            )
            Spacer(modifier = Modifier.height(8.dp))
            Button(
                onClick = {
                    onAddProject(title, description)
                    title = ""
                    description = ""
                },
                modifier = Modifier.align(Alignment.End)
            ) {
                Text("Add")
            }
        }
    }
}

@Composable
fun ProjectCard(project: Project, cardColor: Color, navController: NavController) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(min = 100.dp),
        color = cardColor,
        shape = RoundedCornerShape(20.dp),
        onClick = {
            navController.navigate(Screens.ProjectScreen.name)
        }
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = project.title,
                style = ProjectTitle,
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = project.description,
                style = InputText,
                color = TextWhite
            )
        }
    }
}
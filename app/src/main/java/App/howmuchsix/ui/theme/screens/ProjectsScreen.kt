package App.howmuchsix.ui.theme.screens

import App.howmuchsix.R
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
import App.howmuchsix.ui.theme.design_elements.TextOrange
import App.howmuchsix.ui.theme.design_elements.TextWhite
import App.howmuchsix.ui.theme.design_elements.size100
import App.howmuchsix.ui.theme.design_elements.size12
import App.howmuchsix.ui.theme.design_elements.size128
import App.howmuchsix.ui.theme.design_elements.size16
import App.howmuchsix.ui.theme.design_elements.size20
import App.howmuchsix.ui.theme.design_elements.size200
import App.howmuchsix.ui.theme.design_elements.size290
import App.howmuchsix.ui.theme.design_elements.size4
import App.howmuchsix.ui.theme.design_elements.size40
import App.howmuchsix.ui.theme.design_elements.size8
import App.howmuchsix.ui.theme.design_elements.size85
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
import androidx.compose.material3.ButtonDefaults
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
import androidx.compose.ui.res.painterResource
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController

val cardColors = listOf(BlockOrange, BlockYellow, BlockPink)

@Composable
fun ProjectsScreen(
    navController: NavController,
    viewModel: ProjectViewModel = viewModel()
) {
    var isAddPanelVisible by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Beige)
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = size40, start = size16, end = size16, bottom = size8),
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
                DisplayProjects(viewModel, navController = navController)
            }
        }

        AnimatedVisibility(
            visible = isAddPanelVisible,
            enter = slideInVertically(initialOffsetY = { it }) + fadeIn(),
            exit = slideOutVertically(targetOffsetY = { it }) + fadeOut(),
            modifier = Modifier
                .align(Alignment.Center)
                .padding(horizontal = size16)
        ) {
            AddPanel(
                onAddProject = { title, description ->
                    if (title.isNotBlank()) {
                        viewModel.addProject(
                            Project(
                                title,
                                description
                            )
                        )
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
                .padding(top = size200),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Image(
                painter = painterResource(id = R.drawable.emptyicon),
                contentDescription = null,
                modifier = Modifier.size(size128)
            )
            Spacer(modifier = Modifier.height(size12))
            Text(
                text = "Nothing here yet...",
                style = SubTitle2
            )
        }
    } else {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = size16, vertical = size8),
            verticalArrangement = Arrangement.spacedBy(size12)
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
            .size(size85)
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
            .padding(horizontal = size16, vertical = size12)
            .fillMaxWidth()
            .height(size290),
        color = LighterBeige,
        shape = RoundedCornerShape(size16),
        shadowElevation = size4
    ) {
        Column(modifier = Modifier.padding(size16)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                Image(
                    painter = painterResource(id = R.drawable.ic_cross),
                    contentDescription = "Add project",
                    modifier = Modifier
                        .size(size20)
                        .clickable(onClick = onDismiss)
                )
            }
            CustomTextField(
                value = title,
                onValueChange = { title = it },
                label = "Project name",
                modifier = Modifier.padding(size8),
            )
            Spacer(modifier = Modifier.height(size8))
            CustomTextField(
                value = description,
                onValueChange = { description = it },
                label = "Description",
                modifier = Modifier.padding(size8),
            )
            Spacer(modifier = Modifier.height(size8))
            Button(
                colors = ButtonDefaults.buttonColors(
                    containerColor = BlockPink,
                    contentColor = TextWhite
                ),
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
            .heightIn(min = size100),
        color = cardColor,
        shape = RoundedCornerShape(size20),
        onClick = {
            if (project.id != null) {
                navController.navigate(Screens.createProjectRoute(project.id!!))
            }
        }
    ) {
        Column(modifier = Modifier.padding(size16)) {
            Text(
                text = project.title,
                style = ProjectTitle,
            )
            Spacer(modifier = Modifier.height(size8))
            Text(
                text = project.description,
                style = InputText,
                color = TextWhite
            )
        }
    }
}
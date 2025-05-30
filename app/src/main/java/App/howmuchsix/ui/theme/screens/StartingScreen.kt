package App.howmuchsix.ui.theme.screens

import App.howmuchsix.R
import App.howmuchsix.navigation.Screens
import App.howmuchsix.ui.theme.design_elements.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredHeight
import androidx.compose.foundation.layout.requiredWidth
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController

@Composable
fun StartingScreen(navController: NavController) {
    Box(
        modifier = Modifier
            .background(BackgroundOrange)
            .fillMaxSize()
    ) {
        Column {
            Image(
                painter = painterResource(id = R.drawable.backgsiximg),
                contentDescription = null,
                modifier = Modifier
                    .requiredHeight(size1300)
                    .requiredWidth(size850)
                    .padding(end = size150)
            )
        }
        Column(
            modifier = Modifier.fillMaxSize(),
        ) {
            StartingText()
            Spacer(modifier = Modifier.height(size350))
            DisplayButton(navController)
        }
    }
}

@Composable
fun StartingText() {
    Column(
        verticalArrangement = Arrangement.Center
    ) {
        Spacer(modifier = Modifier.height(size140))
        Text(
            modifier = Modifier
                .padding(start = size40, end = size25),
            text = "Get into coding easily with our app",
            style = StrictTitle
        )
        Spacer(modifier = Modifier.height(size25))
        Text(
            modifier = Modifier
                .padding(start = size40, end = size25),
            text = "Build your code by using drag-and-drop blocks. It’s a great way to learn and teach coding principles",
            style = SubTitle1
        )
    }
}

@Composable
fun DisplayButton(navController: NavController) {
    Button(
        onClick = { navController.navigate(Screens.ProjectListScreen.name) },
        shape = RoundedCornerShape(size20),
        colors = ButtonDefaults.buttonColors(
            containerColor = Beige,
            contentColor = TextOrange
        ),
        elevation = ButtonDefaults.buttonElevation(defaultElevation = size4),
        modifier = Modifier
            .width(size335)
            .height(size65)
            .offset(size40)
    ) {
        Text(
            text = "Get started!",
            style = ButtonText
        )
    }
}

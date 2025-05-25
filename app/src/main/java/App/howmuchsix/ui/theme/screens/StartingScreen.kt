package App.howmuchsix.ui.theme.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import org.intellij.lang.annotations.JdkConstants.HorizontalAlignment
import App.howmuchsix.R
import App.howmuchsix.navigation.Screens
import androidx.compose.foundation.layout.requiredHeight
import androidx.compose.foundation.layout.requiredWidth
import androidx.compose.foundation.layout.size
import androidx.compose.ui.text.font.FontWeight
import App.howmuchsix.ui.theme.design_elements.*
import App.howmuchsix.viewmodel.ProjectViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController

@Composable
fun StartingScreen(navController: NavController){
    Box(modifier = Modifier
        .background(BackgroundOrange)
        .fillMaxSize()
    ) {
        Column {
            Image(
                painter = painterResource(id = R.drawable.backgsiximg),
                contentDescription = null,
                modifier = Modifier
                    .requiredHeight(1300.dp)
                    .requiredWidth(850.dp)
                    .padding(end = 150.dp)
            )
        }
        Column(
            modifier = Modifier.fillMaxSize(),
        ) {
            StartingText()
            Spacer(modifier = Modifier.height(350.dp))
            DisplayButton(navController)
        }
    }
}

@Composable
fun StartingText(){
    Column (
        verticalArrangement = Arrangement.Center
    ){
        Spacer(modifier = Modifier.height(140.dp))
        Text(
            modifier = Modifier
                .padding(start = 40.dp, end = 25.dp),
            text = "Get into coding easily with our app",
            style = StrictTitle
        )
        Spacer(modifier = Modifier.height(25.dp))
        Text(
            modifier = Modifier
                .padding(start = 40.dp, end = 25.dp),
            text = "Build your code by using drag-and-drop blocks. It’s a great way to learn and teach coding principles",
            style = SubTitle1
        )
    }
}

@Composable
fun DisplayButton(navController: NavController){
    Button(
        onClick = { navController.navigate(Screens.ProjectListScreen.name) },
        shape = RoundedCornerShape(20.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = Beige,
            contentColor = TextOrange
        ),
        elevation = ButtonDefaults.buttonElevation(defaultElevation = 4.dp),
        modifier = Modifier
            .width(335.dp)
            .height(65.dp)
            .offset(40.dp)
    ) {
        Text(
            text = "Get started!" ,
            style = ButtonText
        )
    }
}

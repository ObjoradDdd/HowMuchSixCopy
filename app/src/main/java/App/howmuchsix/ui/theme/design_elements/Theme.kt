package App.howmuchsix.ui.theme.design_elements

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val LightColorScheme = lightColorScheme(
    primary = BackgroundOrange,
    secondary = Yellow,
    tertiary = Beige
)

@Composable
fun MuhamedTheme(
    content: @Composable () -> Unit
) {

    MaterialTheme(
        colorScheme = LightColorScheme,
        content = content
    )
}
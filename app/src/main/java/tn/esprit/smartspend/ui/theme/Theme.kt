package tn.esprit.smartspend.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

// Color palette
val PrimaryColor = Color(0xFF155263) // Most important
val SecondaryColor = Color(0xFF155250)
val AccentColor = Color(0xFF625b71)
val BackgroundColor = Color(0xFF2F7E79) // Least important

// Light color scheme
private val LightColorScheme = lightColorScheme(
    primary = PrimaryColor,
    secondary = SecondaryColor,
    tertiary = AccentColor,
    background = Color.White,
    surface = Color.White,
    onPrimary = Color.Black,
    onSecondary = Color.Black,
    onTertiary = Color.Black,
    onBackground = BackgroundColor,
    onSurface = BackgroundColor
)

@Composable
fun SmartSpendTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = LightColorScheme,
        typography = Typography,
        content = content
    )
}
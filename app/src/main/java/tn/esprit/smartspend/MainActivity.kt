
package tn.esprit.smartspend
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.*
import tn.esprit.smartspend.ui.theme.SmartSpendTheme


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            SmartSpendTheme {
                var isLoginScreen by remember { mutableStateOf(true) }
                var showForgotPasswordScreen by remember { mutableStateOf(false) } // Nouvelle variable d'état pour afficher l'écran de mot de passe oublié

                // Si showForgotPasswordScreen est true, on affiche l'écran ForgotPasswordScreen
                if (showForgotPasswordScreen) {
                    ForgotPasswordScreen(onBackToLogin = {
                        showForgotPasswordScreen = false // Revenir à l'écran de connexion
                        isLoginScreen = true // Assurez-vous de revenir sur l'écran de login
                    })
                } else {
                    // Si on est sur l'écran de connexion
                    if (isLoginScreen) {
                        LoginScreen(
                            onSignUpClick = { isLoginScreen = false },
                            onForgotPasswordClick = { showForgotPasswordScreen = true } // Afficher l'écran de mot de passe oublié
                        )
                    } else {
                        // Si on est sur l'écran d'inscription
                        SignUpScreen(onSignInClick = { isLoginScreen = true })
                    }
                }
            }

        }
    }
}


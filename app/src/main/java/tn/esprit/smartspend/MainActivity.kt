package tn.esprit.smartspend

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.*
import tn.esprit.smartspend.ui.theme.SmartSpendTheme
import tn.esprit.smartspend.utils.SharedPrefsManager
import tn.esprit.smartspend.utils.TranslationManager
import java.util.Locale


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Initialize language settings
        TranslationManager.loadLanguagePreference(this)
        
        val sharedPrefsManager = SharedPrefsManager(this)

        // Check if "Remember Me" is enabled and a token exists
        if (sharedPrefsManager.getRememberMe() && sharedPrefsManager.getToken() != null && !sharedPrefsManager.isTokenExpired()) {
            // Navigate to HomeActivity
            val intent = Intent(this, HomeActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            finish()
        } else {
            setContent {
                SmartSpendTheme {
                    var isLoginScreen by remember { mutableStateOf(!intent.getBooleanExtra("showSignUp", false)) }
                    var showForgotPasswordScreen by remember { mutableStateOf(intent.getBooleanExtra("showForgotPassword", false)) }

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


}
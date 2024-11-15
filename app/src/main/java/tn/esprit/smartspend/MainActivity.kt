// MainActivity.kt
package tn.esprit.smartspend

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import tn.esprit.smartspend.ui.theme.SmartSpendTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            SmartSpendTheme {
                var isLoginScreen by remember { mutableStateOf(true) }

                if (isLoginScreen) {
                    LoginScreen(onSignUpClick = { isLoginScreen = false })
                } else {
                    SignUpScreen(onSignInClick = { isLoginScreen = true })
                }
            }
        }
    }
}

package tn.esprit.smartspend

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import tn.esprit.smartspend.ui.theme.SmartSpendTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            SmartSpendTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = Color.White // Fond blanc pour le mode clair
                ) {
                    LoginScreen()
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // Logo
        Image(
            painter = painterResource(id = R.drawable.logo_sans_ktiba_removebg_preview), // Remplacez avec votre logo
            contentDescription = "Logo",
            modifier = Modifier.size(80.dp)
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Titre "Sign In"
        Text(
            text = "Sign In",
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF6A6A6A), // Gris foncé pour le titre
            modifier = Modifier.align(Alignment.Start)
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Champ de texte pour l'email
        OutlinedTextField(
            value = "",
            onValueChange = {},
            label = { Text("Email") },
            leadingIcon = {
                Icon(
                    painter = painterResource(id = R.drawable.email_black),
                    contentDescription = "Email Icon",
                    modifier = Modifier.size(20.dp) // Taille réduite de l'icône
                )
            },
            modifier = Modifier.fillMaxWidth(),
            colors = TextFieldDefaults.outlinedTextFieldColors(
                focusedBorderColor = Color(0xFFD1C4E9),
                unfocusedBorderColor = Color(0xFFB0BEC5)
            )
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Champ de texte pour le mot de passe
        val passwordVisible = remember { mutableStateOf(false) }
        OutlinedTextField(
            value = "",
            onValueChange = {},
            label = { Text("Password") },
            leadingIcon = {
                Icon(
                    painter = painterResource(id = R.drawable.lock_black),
                    contentDescription = "Lock Icon",
                    modifier = Modifier.size(20.dp) // Taille réduite de l'icône
                )
            },
            trailingIcon = {
                val image = if (passwordVisible.value)
                    R.drawable.visibility_black else R.drawable.viss_off_black
                Icon(
                    painter = painterResource(id = image),
                    contentDescription = "Toggle Password Visibility",
                    modifier = Modifier
                        .size(20.dp) // Taille réduite de l'icône
                        .clickable { passwordVisible.value = !passwordVisible.value }
                )
            },
            visualTransformation = if (passwordVisible.value) VisualTransformation.None else PasswordVisualTransformation(),
            modifier = Modifier.fillMaxWidth(),
            colors = TextFieldDefaults.outlinedTextFieldColors(
                focusedBorderColor = Color(0xFFD1C4E9),
                unfocusedBorderColor = Color(0xFFB0BEC5)
            )
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Lien "Forgot password?" en bleu et souligné
        Text(
            text = "Forgot password?",
            color = Color.Blue,
            fontSize = 14.sp,
            modifier = Modifier
                .align(Alignment.End)
                .clickable { /* Ajouter l'action ici */ },
            style = TextStyle(textDecoration = TextDecoration.Underline)
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Bouton de connexion
        Button(
            onClick = { /* Action de connexion */ },
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFFD54F)),
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp),
            shape = RoundedCornerShape(25.dp)
        ) {
            Text("Sign in", fontSize = 18.sp)
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Texte "Or sign in with"
        Text(
            text = "Or sign in with",
            color = Color(0xFF6A6A6A),
            fontSize = 14.sp,
            modifier = Modifier.padding(vertical = 8.dp)
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Boutons Google et Facebook
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            IconButton(onClick = { /* Action Google */ }) {
                Image(
                    painter = painterResource(id = R.drawable.google_removebg_preview), // Remplacez avec l'icône Google
                    contentDescription = "Google Sign-In"
                )
            }
            IconButton(onClick = { /* Action Facebook */ }) {
                Image(
                    painter = painterResource(id = R.drawable.facebook_removebg_preview), // Remplacez avec l'icône Facebook
                    contentDescription = "Facebook Sign-In"
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Lien pour s'inscrire
        Text(
            text = "Don't have an account? Sign up",
            color = Color.Blue,
            fontSize = 14.sp,
            modifier = Modifier.clickable { /* Action Sign Up */ }
        )
    }
}

@Composable
@Preview(showBackground = true)
fun LoginScreenPreview() {
    SmartSpendTheme {
        LoginScreen()
    }
}

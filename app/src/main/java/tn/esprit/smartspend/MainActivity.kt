package tn.esprit.smartspend

import android.content.Context
import android.os.Bundle
import android.widget.Toast
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import tn.esprit.smartspend.model.SignInRequest
import tn.esprit.smartspend.model.SignInResponse
import tn.esprit.smartspend.network.RetrofitInstance
import tn.esprit.smartspend.ui.theme.SmartSpendTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            SmartSpendTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = Color.White
                ) {
                    MainScreen()
                }
            }
        }
    }
}

@Composable
fun MainScreen() {
    var currentScreen by remember { mutableStateOf("login") }

    when (currentScreen) {
        "login" -> LoginScreen(onSignUpClick = { currentScreen = "signup" })
        "signup" -> SignUpScreen(onSignInClick = { currentScreen = "login" })
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(onSignUpClick: () -> Unit) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    val passwordVisible = remember { mutableStateOf(false) }

    val context = LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Image(
            painter = painterResource(id = R.drawable.logo_sans_ktiba_removebg_preview),
            contentDescription = "Logo",
            modifier = Modifier.size(80.dp)
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Sign In",
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF6A6A6A),
            modifier = Modifier.align(Alignment.Start)
        )

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Email") },
            leadingIcon = {
                Icon(
                    painter = painterResource(id = R.drawable.email_black),
                    contentDescription = "Email Icon",
                    modifier = Modifier.size(20.dp)
                )
            },
            modifier = Modifier.fillMaxWidth(),
            colors = TextFieldDefaults.outlinedTextFieldColors(
                focusedBorderColor = Color(0xFFD1C4E9),
                unfocusedBorderColor = Color(0xFFB0BEC5)
            )
        )

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Password") },
            leadingIcon = {
                Icon(
                    painter = painterResource(id = R.drawable.lock_black),
                    contentDescription = "Lock Icon",
                    modifier = Modifier.size(20.dp)
                )
            },
            trailingIcon = {
                val image = if (passwordVisible.value)
                    R.drawable.visibility_black else R.drawable.viss_off_black
                Icon(
                    painter = painterResource(id = image),
                    contentDescription = "Toggle Password Visibility",
                    modifier = Modifier
                        .size(20.dp)
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

        Spacer(modifier = Modifier.height(16.dp))

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

        Button(
            onClick = {
                val signInRequest = SignInRequest(email, password)

                RetrofitInstance.api.signIn(signInRequest).enqueue(object : Callback<SignInResponse> {
                    override fun onResponse(call: Call<SignInResponse>, response: Response<SignInResponse>) {
                        if (response.isSuccessful) {
                            val signInResponse = response.body()
                            val token = signInResponse?.token

                            // Sauvegarder le token dans SharedPreferences
                            val sharedPreferences = context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
                            sharedPreferences.edit().putString("TOKEN", token).apply()

                            // Rediriger l'utilisateur (Exemple : Naviguer vers un autre écran)
                            Toast.makeText(context, "Connexion réussie", Toast.LENGTH_SHORT).show()
                        } else {
                            Toast.makeText(context, "Erreur de connexion", Toast.LENGTH_SHORT).show()
                        }
                    }

                    override fun onFailure(call: Call<SignInResponse>, t: Throwable) {
                        Toast.makeText(context, "Échec de la connexion : ${t.message}", Toast.LENGTH_SHORT).show()
                    }
                })
            },
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFFD54F)),
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp),
            shape = RoundedCornerShape(25.dp)
        ) {
            Text("Sign in", fontSize = 18.sp)
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Or sign in with",
            color = Color.Gray,
            fontSize = 14.sp
        )

        Spacer(modifier = Modifier.height(8.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            IconButton(onClick = { /* Action Google */ }) {
                Image(
                    painter = painterResource(id = R.drawable.google_removebg_preview),
                    contentDescription = "Google Sign-In"
                )
            }
            IconButton(onClick = { /* Action Facebook */ }) {
                Image(
                    painter = painterResource(id = R.drawable.facebook_removebg_preview),
                    contentDescription = "Facebook Sign-In"
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Don't have an account? Sign up",
            color = Color.Blue,
            fontSize = 14.sp,
            modifier = Modifier.clickable { onSignUpClick() }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SignUpScreen(onSignInClick: () -> Unit) {
    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    val passwordVisible = remember { mutableStateOf(false) }
    val confirmPasswordVisible = remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Image(
            painter = painterResource(id = R.drawable.logo_sans_ktiba_removebg_preview),
            contentDescription = "Logo",
            modifier = Modifier.size(80.dp)
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Sign Up",
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF6A6A6A),
            modifier = Modifier.align(Alignment.Start)
        )

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = name,
            onValueChange = { name = it },
            label = { Text("Name") },
            modifier = Modifier.fillMaxWidth(),
            colors = TextFieldDefaults.outlinedTextFieldColors(
                focusedBorderColor = Color(0xFFD1C4E9),
                unfocusedBorderColor = Color(0xFFB0BEC5)
            )
        )

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Email") },
            modifier = Modifier.fillMaxWidth(),
            colors = TextFieldDefaults.outlinedTextFieldColors(
                focusedBorderColor = Color(0xFFD1C4E9),
                unfocusedBorderColor = Color(0xFFB0BEC5)
            )
        )

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Password") },
            leadingIcon = {
                Icon(
                    painter = painterResource(id = R.drawable.lock_black),
                    contentDescription = "Lock Icon",
                    modifier = Modifier.size(20.dp)
                )
            },
            trailingIcon = {
                val image = if (passwordVisible.value)
                    R.drawable.visibility_black else R.drawable.viss_off_black
                Icon(
                    painter = painterResource(id = image),
                    contentDescription = "Toggle Password Visibility",
                    modifier = Modifier
                        .size(20.dp)
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

        OutlinedTextField(
            value = confirmPassword,
            onValueChange = { confirmPassword = it },
            label = { Text("Confirm Password") },
            leadingIcon = {
                Icon(
                    painter = painterResource(id = R.drawable.lock_black),
                    contentDescription = "Lock Icon",
                    modifier = Modifier.size(20.dp)
                )
            },
            trailingIcon = {
                val image = if (confirmPasswordVisible.value)
                    R.drawable.visibility_black else R.drawable.viss_off_black
                Icon(
                    painter = painterResource(id = image),
                    contentDescription = "Toggle Password Visibility",
                    modifier = Modifier
                        .size(20.dp)
                        .clickable { confirmPasswordVisible.value = !confirmPasswordVisible.value }
                )
            },
            visualTransformation = if (confirmPasswordVisible.value) VisualTransformation.None else PasswordVisualTransformation(),
            modifier = Modifier.fillMaxWidth(),
            colors = TextFieldDefaults.outlinedTextFieldColors(
                focusedBorderColor = Color(0xFFD1C4E9),
                unfocusedBorderColor = Color(0xFFB0BEC5)
            )
        )

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = { /* Action d'inscription */ },
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFFD54F)),
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp),
            shape = RoundedCornerShape(25.dp)
        ) {
            Text("Sign up", fontSize = 18.sp)
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Already have an account? Sign In",
            color = Color.Blue,
            fontSize = 14.sp,
            modifier = Modifier.clickable { onSignInClick() }
        )
    }
}

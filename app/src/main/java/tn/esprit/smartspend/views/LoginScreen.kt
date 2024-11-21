package tn.esprit.smartspend

import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(onSignUpClick: () -> Unit,onForgotPasswordClick: () -> Unit) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    val passwordVisible = remember { mutableStateOf(false) }

    val context = LocalContext.current

    // Couleurs personnalisées
    val backgroundColor = Color(0xFFF3E5F5) // Mauve très clair
    val primaryColor = Color(0xFF9575CD) // Mauve plus soutenu
    val textColor = Color(0xFF6A6A6A)
    val borderColor = Color(0xFFB39DDB)

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(Color(0xFFEDE7F6), Color(0xFFD1C4E9))
                )
            )
            .padding(horizontal = 16.dp)
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.fillMaxSize()
        ) {
            // Logo
            Image(
                painter = painterResource(id = R.drawable.logo2),
                contentDescription = "Logo",
                modifier = Modifier
                    .size(100.dp)
                    .padding(bottom = 16.dp)
            )

            // Carte pour les champs de connexion
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                shape = RoundedCornerShape(16.dp),
                elevation = CardDefaults.cardElevation(8.dp)
            ) {
                Column(
                    modifier = Modifier
                        .padding(16.dp)
                        .fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Sign In",
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Bold,
                        color = primaryColor,
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
                                modifier = Modifier.size(20.dp),
                                tint = primaryColor
                            )
                        },
                        modifier = Modifier.fillMaxWidth(),
                        textStyle = TextStyle(color = textColor),
                        colors = TextFieldDefaults.outlinedTextFieldColors(
                            focusedBorderColor = primaryColor,
                            unfocusedBorderColor = borderColor,
                            cursorColor = primaryColor
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
                                modifier = Modifier.size(20.dp),
                                tint = primaryColor
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
                                    .clickable { passwordVisible.value = !passwordVisible.value },
                                tint = primaryColor
                            )
                        },
                        visualTransformation = if (passwordVisible.value) VisualTransformation.None else PasswordVisualTransformation(),
                        modifier = Modifier.fillMaxWidth(),
                        textStyle = TextStyle(color = textColor),
                        colors = TextFieldDefaults.outlinedTextFieldColors(
                            focusedBorderColor = primaryColor,
                            unfocusedBorderColor = borderColor,
                            cursorColor = primaryColor
                        )
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "Forgot password?",
                        color = primaryColor, // Assurez-vous que `primaryColor` est défini dans votre thème ou en tant que variable.
                        fontSize = 14.sp,
                        modifier = Modifier
                            .align(Alignment.End)
                            .clickable { onForgotPasswordClick() }, // Utilisation d'un callback pour gérer la navigation
                        style = TextStyle(textDecoration = TextDecoration.Underline)
                    )


                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Bouton de connexion avec un design amélioré
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
                    .background(
                        brush = Brush.horizontalGradient(
                            colors = listOf(
                                Color(0xFF7E57C2), // Couleur mauve
                                Color(0xFF9575CD)  // Couleur un peu plus claire
                            )
                        ),
                        shape = RoundedCornerShape(25.dp) // Coins arrondis
                    )
                    .clickable(
                        onClick = {
                            val signInRequest = SignInRequest(email, password)

                            RetrofitInstance.api.signIn(signInRequest).enqueue(object : Callback<SignInResponse> {
                                override fun onResponse(call: Call<SignInResponse>, response: Response<SignInResponse>) {
                                    if (response.isSuccessful) {
                                        val signInResponse = response.body()
                                        val token = signInResponse?.access_token

                                        if (token.isNullOrEmpty()) {
                                            Toast.makeText(context, "Token is null or empty", Toast.LENGTH_SHORT).show()
                                        } else {
                                            Toast.makeText(context, "Token: $token", Toast.LENGTH_SHORT).show()
                                        }
                                    } else {
                                        Toast.makeText(context, "Erreur de connexion", Toast.LENGTH_SHORT).show()
                                    }
                                }

                                override fun onFailure(call: Call<SignInResponse>, t: Throwable) {
                                    Toast.makeText(context, "Échec de la connexion : ${t.message}", Toast.LENGTH_SHORT).show()
                                }
                            })
                        }
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Sign In",
                    fontSize = 18.sp,
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(8.dp)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Options sociales
            Text(
                text = "Or sign in with",
                color = textColor,
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

            // Texte de création de compte
            Text(
                text = "Don't have an account? Sign up",
                color = primaryColor,
                fontSize = 14.sp,
                modifier = Modifier.clickable { onSignUpClick() }
            )
        }
    }
}

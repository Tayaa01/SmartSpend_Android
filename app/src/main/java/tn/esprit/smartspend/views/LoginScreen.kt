package tn.esprit.smartspend

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.material3.CardDefaults.cardElevation
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import tn.esprit.smartspend.model.SignInRequest
import tn.esprit.smartspend.model.SignInResponse
import tn.esprit.smartspend.network.RetrofitInstance
import tn.esprit.smartspend.utils.SharedPrefsManager
import tn.esprit.smartspend.ui.theme.PrimaryColor
import tn.esprit.smartspend.ui.theme.SecondaryColor
import tn.esprit.smartspend.ui.theme.BackgroundColor
import tn.esprit.smartspend.utils.TranslationManager

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(onSignUpClick: () -> Unit, onForgotPasswordClick: () -> Unit) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    val passwordVisible = remember { mutableStateOf(false) }
    val rememberMe = remember { mutableStateOf(false) }

    val context = LocalContext.current
    val sharedPrefsManager = SharedPrefsManager(context)

    // Background Gradient for a modern touch
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(Color(0xFFF1F1F1), Color(0xFFE5E5E5))
                )
            )
    ) {
        // Content Layout
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.fillMaxSize().padding(16.dp)
        ) {
            // Logo Image
            Image(
                painter = painterResource(id = R.drawable.icon4),
                contentDescription = "Logo",
                modifier = Modifier
                    .size(120.dp)
                    .padding(bottom = 24.dp)
            )

            // Login Card with Rounded Corners and Subtle Shadow
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                shape = RoundedCornerShape(16.dp),
                elevation = cardElevation(8.dp)
            ) {
                Column(
                    modifier = Modifier
                        .padding(24.dp)
                        .fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Title Text
                    Text(
                        text = TranslationManager.getTranslation("login_title"),
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Bold,
                        color = PrimaryColor,
                        modifier = Modifier.align(Alignment.Start)
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    // Email Input Field
                    OutlinedTextField(
                        value = email,
                        onValueChange = { email = it },
                        label = { Text(TranslationManager.getTranslation("login_email_hint")) },
                        leadingIcon = {
                            Icon(
                                painter = painterResource(id = R.drawable.email_black),
                                contentDescription = "Email Icon",
                                modifier = Modifier.size(20.dp),
                                tint = PrimaryColor
                            )
                        },
                        modifier = Modifier.fillMaxWidth(),
                        textStyle = TextStyle(fontSize = 18.sp, color = PrimaryColor), // Apply BackgroundColor to text
                        singleLine = true,
                        colors = TextFieldDefaults.outlinedTextFieldColors(
                            focusedBorderColor = PrimaryColor,
                            unfocusedBorderColor = SecondaryColor,
                            cursorColor = PrimaryColor
                        )
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Password Input Field
                    OutlinedTextField(
                        value = password,
                        onValueChange = { password = it },
                        label = { Text(TranslationManager.getTranslation("login_password_hint")) },
                        leadingIcon = {
                            Icon(
                                painter = painterResource(id = R.drawable.lock_black),
                                contentDescription = "Lock Icon",
                                modifier = Modifier.size(20.dp),
                                tint = PrimaryColor
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
                                tint = PrimaryColor
                            )
                        },
                        visualTransformation = if (passwordVisible.value) VisualTransformation.None else PasswordVisualTransformation(),
                        modifier = Modifier.fillMaxWidth(),
                        textStyle = TextStyle(fontSize = 18.sp, color = BackgroundColor), // Apply BackgroundColor to text
                        singleLine = true,
                        colors = TextFieldDefaults.outlinedTextFieldColors(
                            focusedBorderColor = PrimaryColor,
                            unfocusedBorderColor = SecondaryColor,
                            cursorColor = PrimaryColor
                        )
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Remember Me Checkbox
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Checkbox(
                            checked = rememberMe.value,
                            onCheckedChange = { rememberMe.value = it },
                            colors = CheckboxDefaults.colors(checkedColor = PrimaryColor)
                        )
                        Text(
                            text = TranslationManager.getTranslation("login_remember_me"),
                            color = PrimaryColor,
                            fontSize = 16.sp
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Forgot Password Link
                    Text(
                        text = TranslationManager.getTranslation("login_forgot_password"),
                        color = PrimaryColor,
                        fontSize = 15.sp,
                        modifier = Modifier
                            .align(Alignment.End)
                            .clickable { onForgotPasswordClick() },
                        style = TextStyle(fontWeight = FontWeight.Bold)
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Sign-In Button with Gradient
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
                    .background(
                        brush = Brush.horizontalGradient(
                            colors = listOf(SecondaryColor, PrimaryColor)
                        ),
                        shape = RoundedCornerShape(10) // 5% corner radius
                    )
                    .clickable {
                        val signInRequest = SignInRequest(email, password)

                        RetrofitInstance.api.signIn(signInRequest).enqueue(object : Callback<SignInResponse> {
                            override fun onResponse(call: Call<SignInResponse>, response: Response<SignInResponse>) {
                                if (response.isSuccessful) {
                                    val signInResponse = response.body()
                                    val token = signInResponse?.access_token

                                    if (token.isNullOrEmpty()) {
                                        Toast.makeText(context, "Token is null or empty", Toast.LENGTH_SHORT).show()
                                    } else {
                                        sharedPrefsManager.saveToken(token)
                                        if (rememberMe.value) {
                                            sharedPrefsManager.saveRememberMe(true)
                                        }
                                        Toast.makeText(context, "Welcome!", Toast.LENGTH_SHORT).show()

                                        val intent = android.content.Intent(context, HomeActivity::class.java)
                                        intent.flags = android.content.Intent.FLAG_ACTIVITY_NEW_TASK or android.content.Intent.FLAG_ACTIVITY_CLEAR_TASK
                                        context.startActivity(intent)
                                    }
                                } else {
                                    Toast.makeText(context, "Erreur de connexion", Toast.LENGTH_SHORT).show()
                                }
                            }

                            override fun onFailure(call: Call<SignInResponse>, t: Throwable) {
                                Toast.makeText(context, "Échec de la connexion : ${t.message}", Toast.LENGTH_SHORT).show()
                            }
                        })
                    },
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = TranslationManager.getTranslation("login_button"),
                    fontSize = 20.sp,
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(8.dp)
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Sign Up Text
            Text(
                text = TranslationManager.getTranslation("login_no_account"),
                color = PrimaryColor,
                fontSize = 16.sp,
                modifier = Modifier.clickable { onSignUpClick() }
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewLoginScreen() {
    LoginScreen(onSignUpClick = {}, onForgotPasswordClick = {})
}

package tn.esprit.smartspend

import android.util.Log
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
import tn.esprit.smartspend.model.SignUpRequest
import tn.esprit.smartspend.model.SignUpResponse
import tn.esprit.smartspend.network.RetrofitInstance
import tn.esprit.smartspend.ui.theme.PrimaryColor
import tn.esprit.smartspend.ui.theme.SecondaryColor
import tn.esprit.smartspend.utils.TranslationManager

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SignUpScreen(onSignInClick: () -> Unit) {
    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    val passwordVisible = remember { mutableStateOf(false) }
    val confirmPasswordVisible = remember { mutableStateOf(false) }

    val context = LocalContext.current

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(Color(0xFFF1F1F1), Color(0xFFE5E5E5))
                )
            )
    ) {
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

            // Sign-Up Card
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
                    Text(
                        text = TranslationManager.getTranslation("signup_title"),
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Bold,
                        color = PrimaryColor,
                        modifier = Modifier.align(Alignment.Start)
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Name Input
                    OutlinedTextField(
                        value = name,
                        onValueChange = { name = it },
                        label = { Text(TranslationManager.getTranslation("signup_name_hint")) },
                        modifier = Modifier.fillMaxWidth(),
                        textStyle = TextStyle(fontSize = 18.sp, color = PrimaryColor),
                        colors = TextFieldDefaults.outlinedTextFieldColors(
                            focusedBorderColor = PrimaryColor,
                            unfocusedBorderColor = SecondaryColor,
                            cursorColor = PrimaryColor
                        )
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Email Input
                    OutlinedTextField(
                        value = email,
                        onValueChange = { email = it },
                        label = { Text(TranslationManager.getTranslation("signup_email_hint")) },
                        modifier = Modifier.fillMaxWidth(),
                        textStyle = TextStyle(fontSize = 18.sp, color = PrimaryColor),
                        colors = TextFieldDefaults.outlinedTextFieldColors(
                            focusedBorderColor = PrimaryColor,
                            unfocusedBorderColor = SecondaryColor,
                            cursorColor = PrimaryColor
                        )
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Password Input
                    OutlinedTextField(
                        value = password,
                        onValueChange = { password = it },
                        label = { Text("Password") },
                        visualTransformation = if (passwordVisible.value) VisualTransformation.None else PasswordVisualTransformation(),
                        trailingIcon = {
                            val image = if (passwordVisible.value) R.drawable.visibility_black else R.drawable.viss_off_black
                            Icon(
                                painter = painterResource(id = image),
                                contentDescription = "Toggle Password Visibility",
                                modifier = Modifier
                                    .size(20.dp)
                                    .clickable { passwordVisible.value = !passwordVisible.value },
                                tint = PrimaryColor
                            )
                        },
                        modifier = Modifier.fillMaxWidth(),
                        textStyle = TextStyle(fontSize = 18.sp, color = PrimaryColor),
                        colors = TextFieldDefaults.outlinedTextFieldColors(
                            focusedBorderColor = PrimaryColor,
                            unfocusedBorderColor = SecondaryColor,
                            cursorColor = PrimaryColor
                        )
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Confirm Password Input
                    OutlinedTextField(
                        value = confirmPassword,
                        onValueChange = { confirmPassword = it },
                        label = { Text("Confirm Password") },
                        visualTransformation = if (confirmPasswordVisible.value) VisualTransformation.None else PasswordVisualTransformation(),
                        trailingIcon = {
                            val image = if (confirmPasswordVisible.value) R.drawable.visibility_black else R.drawable.viss_off_black
                            Icon(
                                painter = painterResource(id = image),
                                contentDescription = "Toggle Confirm Password Visibility",
                                modifier = Modifier
                                    .size(20.dp)
                                    .clickable { confirmPasswordVisible.value = !confirmPasswordVisible.value },
                                tint = PrimaryColor
                            )
                        },
                        modifier = Modifier.fillMaxWidth(),
                        textStyle = TextStyle(fontSize = 18.sp, color = PrimaryColor),
                        colors = TextFieldDefaults.outlinedTextFieldColors(
                            focusedBorderColor = PrimaryColor,
                            unfocusedBorderColor = SecondaryColor,
                            cursorColor = PrimaryColor
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Sign-Up Button with Gradient
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
                    .background(
                        brush = Brush.horizontalGradient(
                            colors = listOf(SecondaryColor, PrimaryColor)
                        ),
                        shape = RoundedCornerShape(10)
                    )
                    .clickable {
                        if (password != confirmPassword) {
                            Toast.makeText(context, TranslationManager.getTranslation("signup_passwords_not_match"), Toast.LENGTH_SHORT).show()
                        } else {
                            val signUpRequest = SignUpRequest(name, email, password)

                            RetrofitInstance.api.signUp(signUpRequest).enqueue(object : Callback<SignUpResponse> {
                                override fun onResponse(call: Call<SignUpResponse>, response: Response<SignUpResponse>) {
                                    if (response.isSuccessful) {
                                        Toast.makeText(context, TranslationManager.getTranslation("signup_success"), Toast.LENGTH_SHORT).show()
                                        onSignInClick()
                                    } else {
                                        Toast.makeText(context, TranslationManager.getTranslation("signup_failed"), Toast.LENGTH_SHORT).show()
                                    }
                                }

                                override fun onFailure(call: Call<SignUpResponse>, t: Throwable) {
                                    Toast.makeText(context, TranslationManager.getTranslation("signup_failed"), Toast.LENGTH_SHORT).show()
                                }
                            })
                        }
                    },
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = TranslationManager.getTranslation("signup_button"),
                    fontSize = 20.sp,
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(8.dp)
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Sign-In Text Link
            Text(
                text = TranslationManager.getTranslation("signup_have_account"),
                color = PrimaryColor,
                fontSize = 16.sp,
                modifier = Modifier.clickable { onSignInClick() }
            )
        }
    }
}


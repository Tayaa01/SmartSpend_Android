// SignUpScreen.kt
package tn.esprit.smartspend

import android.util.Log
import android.widget.Toast
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import tn.esprit.smartspend.model.SignUpRequest
import tn.esprit.smartspend.model.SignUpResponse
import tn.esprit.smartspend.network.RetrofitInstance

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
            visualTransformation = if (passwordVisible.value) VisualTransformation.None else PasswordVisualTransformation(),
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
            visualTransformation = if (confirmPasswordVisible.value) VisualTransformation.None else PasswordVisualTransformation(),
            trailingIcon = {
                val image = if (confirmPasswordVisible.value)
                    R.drawable.visibility_black else R.drawable.viss_off_black
                Icon(
                    painter = painterResource(id = image),
                    contentDescription = "Toggle Confirm Password Visibility",
                    modifier = Modifier
                        .size(20.dp)
                        .clickable { confirmPasswordVisible.value = !confirmPasswordVisible.value }
                )
            },
            modifier = Modifier.fillMaxWidth(),
            colors = TextFieldDefaults.outlinedTextFieldColors(
                focusedBorderColor = Color(0xFFD1C4E9),
                unfocusedBorderColor = Color(0xFFB0BEC5)
            )
        )

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                val signUpRequest = SignUpRequest(name, email, password)

                RetrofitInstance.api.signUp(signUpRequest).enqueue(object : Callback<SignUpResponse> {
                    override fun onResponse(call: Call<SignUpResponse>, response: Response<SignUpResponse>) {
                        if (response.isSuccessful) {
                            val signUpResponse = response.body()
                            Log.d("SignUp", "Response body: $signUpResponse")
                            Toast.makeText(context, "Compte créé avec succès!", Toast.LENGTH_SHORT).show()
                            onSignInClick()
                        } else {
                            Log.d("SignUp", "Response error: ${response.errorBody()?.string()}")
                            Toast.makeText(context, "Erreur d'inscription", Toast.LENGTH_SHORT).show()
                        }
                    }

                    override fun onFailure(call: Call<SignUpResponse>, t: Throwable) {
                        Toast.makeText(context, "Échec de l'inscription : ${t.message}", Toast.LENGTH_SHORT).show()
                    }
                })
            },
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
            text = "Already have an account? Sign in",
            color = Color.Blue,
            fontSize = 14.sp,
            modifier = Modifier.clickable { onSignInClick() }
        )
    }
}

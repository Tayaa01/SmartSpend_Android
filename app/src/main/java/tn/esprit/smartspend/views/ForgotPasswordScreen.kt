package tn.esprit.smartspend

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import tn.esprit.smartspend.model.ForgotPasswordRequest
import tn.esprit.smartspend.model.ForgotPasswordResponse
import tn.esprit.smartspend.network.RetrofitInstance
import tn.esprit.smartspend.views.OtpDialog
import tn.esprit.smartspend.views.ResetPasswordDialog

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ForgotPasswordScreen(onBackToLogin: () -> Unit) {
    var email by remember { mutableStateOf("") }
    var isOtpDialogVisible by remember { mutableStateOf(false) }
    var isResetPasswordDialogVisible by remember { mutableStateOf(false) }
    var token by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }
    val context = LocalContext.current

    val primaryColor = Color(0xFF9575CD)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(25.dp)
            .background(Color.White),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        // Top Section: Title and Email Input
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.Start
        ) {
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "Forgot Password",
                style = TextStyle(
                    fontSize = 30.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )
            )
            Spacer(modifier = Modifier.height(24.dp))
            Text(
                text = "Enter your email address below to reset your password.",
                style = TextStyle(
                    fontSize = 18.sp,
                    color = Color.Gray
                )
            )
            Spacer(modifier = Modifier.height(35.dp))
            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                label = { Text("Email Address") },
                modifier = Modifier.fillMaxWidth(),
            )
        }

        // Bottom Section: Button and Back to Login
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Button(
                onClick = {
                    if (email.isNotEmpty() && android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                        isLoading = true
                        RetrofitInstance.api.forgotPassword(ForgotPasswordRequest(email))
                            .enqueue(object : Callback<ForgotPasswordResponse> {
                                override fun onResponse(
                                    call: Call<ForgotPasswordResponse>,
                                    response: Response<ForgotPasswordResponse>
                                ) {
                                    isLoading = false
                                    if (response.isSuccessful) {
                                        Toast.makeText(context, "Password reset email sent!", Toast.LENGTH_SHORT).show()
                                        isOtpDialogVisible = true
                                    } else {
                                        Toast.makeText(context, "Error: ${response.message()}", Toast.LENGTH_SHORT).show()
                                    }
                                }

                                override fun onFailure(call: Call<ForgotPasswordResponse>, t: Throwable) {
                                    isLoading = false
                                    Toast.makeText(context, "Error: ${t.message}", Toast.LENGTH_SHORT).show()
                                }
                            })
                    } else {
                        Toast.makeText(context, "Please enter a valid email address", Toast.LENGTH_SHORT).show()
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                shape = RoundedCornerShape(9.dp), // Rounded corners with 9° degree
                colors = ButtonDefaults.buttonColors(containerColor = primaryColor)
            ) {
                if (isLoading) {
                    CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
                } else {
                    Text("Send Reset Code", style = TextStyle(fontSize = 16.sp, fontWeight = FontWeight.Bold))
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Back to Login",
                style = TextStyle(
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium,
                    color = primaryColor, // Matching button color
                    letterSpacing = 0.5.sp
                ),
                modifier = Modifier
                    .clickable { onBackToLogin() }
                    .padding(8.dp)
            )
        }
    }

    // OTP Dialog
    if (isOtpDialogVisible) {
        OtpDialog(
            onTokenSubmitted = { enteredToken ->
                token = enteredToken
                isOtpDialogVisible = false
                isResetPasswordDialogVisible = true
            },
            onDismiss = {
                isOtpDialogVisible = false
            }
        )
    }

    // Reset Password Dialog
    if (isResetPasswordDialogVisible) {
        ResetPasswordDialog(
            token = token,
            onPasswordReset = {
                Toast.makeText(context, "Password reset successful!", Toast.LENGTH_SHORT).show()
                onBackToLogin()
            },
            onDismiss = {
                isResetPasswordDialogVisible = false
            }
        )
    }
}

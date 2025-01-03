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
import tn.esprit.smartspend.ui.theme.MostImportantColor
import tn.esprit.smartspend.ui.theme.Navy
import tn.esprit.smartspend.ui.theme.Sand
import tn.esprit.smartspend.utils.TranslationManager
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

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(25.dp),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.Start
        ) {
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = TranslationManager.getTranslation("forgot_password_title"),
                style = TextStyle(
                    fontSize = 30.sp,
                    fontWeight = FontWeight.Bold,
                    color = Navy
                )
            )
            Spacer(modifier = Modifier.height(24.dp))
            Text(
                text = TranslationManager.getTranslation("forgot_password_desc"),
                style = TextStyle(
                    fontSize = 18.sp,
                    color = MostImportantColor
                )
            )
            Spacer(modifier = Modifier.height(35.dp))
            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                label = { Text(TranslationManager.getTranslation("email_address")) },
                modifier = Modifier.fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Navy,
                    focusedLabelColor = Navy,
                    cursorColor = Navy
                )
            )
        }

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
                                        Toast.makeText(context, TranslationManager.getTranslation("reset_email_sent"), Toast.LENGTH_SHORT).show()
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
                        Toast.makeText(context, TranslationManager.getTranslation("invalid_email"), Toast.LENGTH_SHORT).show()
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                shape = RoundedCornerShape(9.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Navy)
            ) {
                if (isLoading) {
                    CircularProgressIndicator(color = Sand, modifier = Modifier.size(24.dp))
                } else {
                    Text(
                        text = TranslationManager.getTranslation("send_reset_code"),
                        style = TextStyle(
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = Sand
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = TranslationManager.getTranslation("back_to_login"),
                style = TextStyle(
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium,
                    color = Navy
                ),
                modifier = Modifier
                    .clickable { onBackToLogin() }
                    .padding(8.dp)
            )
        }
    }

    // Show dialogs with updated styling
    if (isOtpDialogVisible) {
        OtpDialog(
            onTokenSubmitted = { enteredToken ->
                token = enteredToken
                isOtpDialogVisible = false
                isResetPasswordDialogVisible = true
            },
            onDismiss = { isOtpDialogVisible = false }
        )
    }

    if (isResetPasswordDialogVisible) {
        ResetPasswordDialog(
            token = token,
            onPasswordReset = {
                Toast.makeText(context, TranslationManager.getTranslation("reset_success"), Toast.LENGTH_SHORT).show()
                onBackToLogin()
            },
            onDismiss = { isResetPasswordDialogVisible = false }
        )
    }
}

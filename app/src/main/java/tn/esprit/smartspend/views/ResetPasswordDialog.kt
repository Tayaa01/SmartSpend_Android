package tn.esprit.smartspend.views

import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import tn.esprit.smartspend.model.ResetPasswordRequest
import tn.esprit.smartspend.model.ResetPasswordResponse
import tn.esprit.smartspend.network.RetrofitInstance
import tn.esprit.smartspend.ui.theme.Navy
import tn.esprit.smartspend.ui.theme.Sand
import tn.esprit.smartspend.utils.TranslationManager

@Composable
fun ResetPasswordDialog(
    token: String,
    onPasswordReset: () -> Unit,
    onDismiss: () -> Unit
) {
    var newPasswordState by remember { mutableStateOf("") }
    val context = LocalContext.current

    // Toast triggered outside of composable using LaunchedEffect
    val showToast = remember { mutableStateOf("") }
    LaunchedEffect(showToast.value) {
        if (showToast.value.isNotEmpty()) {
            Toast.makeText(context, showToast.value, Toast.LENGTH_SHORT).show()
        }
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = Color.White,
        title = {
            Text(
                text = TranslationManager.getTranslation("reset_password_title"),
                style = TextStyle(
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = Navy
                )
            )
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp)
            ) {
                OutlinedTextField(
                    value = newPasswordState,
                    onValueChange = { newPasswordState = it },
                    label = { Text(TranslationManager.getTranslation("new_password")) },
                    modifier = Modifier.fillMaxWidth(),
                    visualTransformation = PasswordVisualTransformation(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Navy,
                        focusedLabelColor = Navy,
                        cursorColor = Navy
                    )
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (newPasswordState.isNotEmpty()) {
                        // Prepare the request body
                        val resetPasswordRequest = ResetPasswordRequest(newPasswordState)

                        // Make the API request with token as query parameter and password as body
                        RetrofitInstance.api.resetPassword(token, resetPasswordRequest)
                            .enqueue(object : Callback<ResetPasswordResponse> {
                                override fun onResponse(
                                    call: Call<ResetPasswordResponse>,
                                    response: Response<ResetPasswordResponse>
                                ) {
                                    if (response.isSuccessful) {
                                        // Handle successful password reset
                                        onPasswordReset() // Notify success
                                        Toast.makeText(context, TranslationManager.getTranslation("reset_success"), Toast.LENGTH_SHORT).show()
                                    } else {
                                        // Show error if failed
                                        showToast.value = "Error: ${response.message()}"
                                    }
                                }

                                override fun onFailure(call: Call<ResetPasswordResponse>, t: Throwable) {
                                    showToast.value = "Error: ${t.message}"
                                }
                            })
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = Navy)
            ) {
                Text(
                    text = TranslationManager.getTranslation("submit"),
                    color = Sand
                )
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(
                    text = TranslationManager.getTranslation("cancel"),
                    color = Navy
                )
            }
        }
    )
}



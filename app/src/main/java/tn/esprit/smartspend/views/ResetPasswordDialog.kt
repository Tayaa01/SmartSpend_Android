package tn.esprit.smartspend.views

import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.PasswordVisualTransformation
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import tn.esprit.smartspend.model.ResetPasswordRequest
import tn.esprit.smartspend.model.ResetPasswordResponse
import tn.esprit.smartspend.network.RetrofitInstance

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
        title = { Text("Reset Password") },
        text = {
            Column {
                OutlinedTextField(
                    value = newPasswordState,
                    onValueChange = { newPasswordState = it },
                    label = { Text("New Password") },
                    modifier = Modifier.fillMaxWidth(),
                    visualTransformation = PasswordVisualTransformation()
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
                }
            ) {
                Text("Submit")
            }
        },
        dismissButton = {
            Button(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}



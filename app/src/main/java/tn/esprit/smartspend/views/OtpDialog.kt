package tn.esprit.smartspend.views

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight

import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import tn.esprit.smartspend.model.TokenVerificationResponse
import tn.esprit.smartspend.network.RetrofitInstance
import tn.esprit.smartspend.ui.theme.Navy
import tn.esprit.smartspend.ui.theme.Sand
import tn.esprit.smartspend.utils.TranslationManager

@Composable
fun OtpDialog(onTokenSubmitted: (String) -> Unit, onDismiss: () -> Unit) {
    var isLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    val context = LocalContext.current
    
    // Create 6 text states for OTP digits
    val otpFields = remember { List(6) { mutableStateOf("") } }
    val focusRequesters = remember { List(6) { FocusRequester() } }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = Color.White,
        title = {
            Text(
                text = TranslationManager.getTranslation("enter_token"),
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
                    .padding(vertical = 16.dp),
                horizontalAlignment = CenterHorizontally
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    otpFields.forEachIndexed { index, state ->
                        BasicTextField(
                            value = state.value,
                            onValueChange = { newValue ->
                                if (newValue.length <= 1 && newValue.all { it.isDigit() }) {
                                    state.value = newValue
                                    if (newValue.isNotEmpty() && index < 5) {
                                        focusRequesters[index + 1].requestFocus()
                                    }
                                }
                            },
                            modifier = Modifier
                                .width(45.dp)
                                .height(55.dp)
                                .border(
                                    width = 1.dp,
                                    color = if (errorMessage != null) Color.Red else Navy,
                                    shape = MaterialTheme.shapes.small
                                )
                                .focusRequester(focusRequesters[index])
                                .background(
                                    color = Color.White,
                                    shape = MaterialTheme.shapes.small
                                ),
                            textStyle = TextStyle(
                                fontSize = 20.sp,
                                textAlign = TextAlign.Center,
                                color = Navy
                            ),
                            keyboardOptions = KeyboardOptions(
                                keyboardType = KeyboardType.Number
                            ),
                            singleLine = true,
                            decorationBox = { innerTextField ->
                                Box(
                                    contentAlignment = Alignment.Center,
                                    modifier = Modifier.fillMaxSize()
                                ) {
                                    innerTextField()
                                }
                            }
                        )
                    }
                }

                errorMessage?.let {
                    Text(
                        text = it,
                        color = Color.Red,
                        modifier = Modifier.padding(top = 8.dp)
                    )
                }

                if (isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier
                            .padding(top = 16.dp),
                        color = Navy
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = { 
                    val token = otpFields.joinToString("") { it.value }
                    if (token.length == 6) {
                        isLoading = true
                        errorMessage = null
                        
                        RetrofitInstance.api.verifyResetToken(token)
                            .enqueue(object : Callback<TokenVerificationResponse> {
                                override fun onResponse(
                                    call: Call<TokenVerificationResponse>,
                                    response: Response<TokenVerificationResponse>
                                ) {
                                    isLoading = false
                                    if (response.isSuccessful && response.body()?.isValid == true) {
                                        onTokenSubmitted(token)
                                    } else {
                                        errorMessage = TranslationManager.getTranslation("invalid_token")
                                    }
                                }

                                override fun onFailure(call: Call<TokenVerificationResponse>, t: Throwable) {
                                    isLoading = false
                                    errorMessage = t.message ?: TranslationManager.getTranslation("verification_error")
                                }
                            })
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = Navy),
                enabled = !isLoading && otpFields.all { it.value.isNotEmpty() }
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

    LaunchedEffect(Unit) {
        focusRequesters[0].requestFocus()
    }
}


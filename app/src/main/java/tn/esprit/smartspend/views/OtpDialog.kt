package tn.esprit.smartspend.views

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import tn.esprit.smartspend.ui.theme.Navy
import tn.esprit.smartspend.ui.theme.Sand
import tn.esprit.smartspend.utils.TranslationManager

@Composable
fun OtpDialog(onTokenSubmitted: (String) -> Unit, onDismiss: () -> Unit) {
    var token by remember { mutableStateOf("") }

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
                    .padding(vertical = 16.dp)
            ) {
                OutlinedTextField(
                    value = token,
                    onValueChange = { token = it },
                    label = { Text(TranslationManager.getTranslation("token")) },
                    modifier = Modifier.fillMaxWidth(),
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
                onClick = { if (token.isNotEmpty()) onTokenSubmitted(token) },
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


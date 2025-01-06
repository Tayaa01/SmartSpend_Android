package tn.esprit.smartspend.views

import android.content.Context
import androidx.compose.ui.graphics.Brush

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.DialogProperties
import tn.esprit.smartspend.utils.SharedPrefsManager
import tn.esprit.smartspend.utils.TranslationManager
import tn.esprit.smartspend.R // Assurez-vous que le fichier `R.drawable.icon4` est dans `res/drawable`



import kotlinx.coroutines.launch

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import org.json.JSONObject



@Composable
fun ProfileView(
    context: Context,
    navigateToLogin: () -> Unit,
    navigateToPrivacyPolicy: () -> Unit
) {
    TranslationManager.loadLanguagePreference(context)
    var showLanguageDialog by remember { mutableStateOf(false) }
    var selectedLanguage by remember { mutableStateOf(TranslationManager.getTranslation("language")) }

    val sharedPrefsManager = SharedPrefsManager(context)

    if (showLanguageDialog) {
        LanguageSelectionDialog(
            currentLanguage = selectedLanguage,
            onDismiss = { showLanguageDialog = false },
            onLanguageSelected = { language ->
                selectedLanguage = language
                TranslationManager.setLanguage(context, language)
                showLanguageDialog = false
            }
        )
    }

    var username by remember { mutableStateOf<String?>(null) }
    val coroutineScope = rememberCoroutineScope()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF5F5F5)) // Couleur d'arrière-plan moderne et neutre
    ) {
        // Titre "Settings" ajouté en haut de la page
        Text(
            text = TranslationManager.getTranslation("settings"),
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Black,
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .align(Alignment.Start) // Aligne le titre à gauche
        )

        // Carte pour les paramètres
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            shape = RoundedCornerShape(12.dp), // Coins plus arrondis
            colors = CardDefaults.cardColors(containerColor = Color.White),
        ) {
            Column {
                SettingsItem(
                    title = TranslationManager.getTranslation("privacy_policy"),
                    icon = Icons.Default.PrivacyTip,
                    onClick = { navigateToPrivacyPolicy() }
                )

                Divider(color = Color(0xFFE0E0E0), thickness = 1.dp)

                SettingsItem(
                    title = TranslationManager.getTranslation("language"),
                    icon = Icons.Default.Language,
                    onClick = { showLanguageDialog = true }
                )

                Divider(color = Color(0xFFE0E0E0), thickness = 1.dp)

                SettingsItem(
                    title = TranslationManager.getTranslation("logout"),
                    icon = Icons.Default.ExitToApp,
                    isDestructive = true,
                    onClick = {
                        sharedPrefsManager.clearToken()
                        navigateToLogin()
                    }
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Pied de page
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "${TranslationManager.getTranslation("app_version")} 2.0.1",
                fontSize = 14.sp,
                color = Color(0xFF757575)
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = "© 2025 SmartSpend Inc.",
                fontSize = 12.sp,
                color = Color(0xFF757575)
            )
        }
    }
}



// Component for settings with toggle switches
@Composable
fun SwitchSettingItem(
    title: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    isChecked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = title,
            tint = Color(0xFF1F2937),
            modifier = Modifier.size(28.dp)
        )
        Spacer(modifier = Modifier.width(16.dp))
        Text(
            text = title,
            fontSize = 16.sp,
            color = Color(0xFF1F2937),
            modifier = Modifier.weight(1f)
        )
        Switch(
            checked = isChecked,
            onCheckedChange = { onCheckedChange(it) },
            colors = SwitchDefaults.colors(
                checkedThumbColor = Color(0xFF4CAF50),
                uncheckedThumbColor = Color(0xFFE0E0E0),
                checkedTrackColor = Color(0xFF81C784),
                uncheckedTrackColor = Color(0xFFBDBDBD)
            )
        )
    }
}

@Composable
fun SettingsItem(
    title: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    isDestructive: Boolean = false,
    onClick: () -> Unit = {}
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = title,
            tint = if (isDestructive) Color.Red else Color(0xFF1F2937),
            modifier = Modifier.size(28.dp)
        )
        Spacer(modifier = Modifier.width(16.dp))
        Text(
            text = title,
            fontSize = 16.sp,
            color = if (isDestructive) Color.Red else Color(0xFF1F2937),
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
fun LanguageSelectionDialog(
    currentLanguage: String,
    onDismiss: () -> Unit,
    onLanguageSelected: (String) -> Unit
) {
    AlertDialog(
        onDismissRequest = { onDismiss() },
        properties = DialogProperties(dismissOnClickOutside = true),
        containerColor = MaterialTheme.colorScheme.surface,
        shape = RoundedCornerShape(28.dp),
        modifier = Modifier.fillMaxWidth(0.92f),
        title = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Icon(
                    imageVector = Icons.Default.Language,
                    contentDescription = null,
                    modifier = Modifier.size(36.dp),
                    tint = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = TranslationManager.getTranslation("change_language"),
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center
                )
            }
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp)
            ) {
                LanguageOption(
                    languageCode = "en",
                    languageName = "English",
                    flag = "🇺🇸",
                    isSelected = currentLanguage == "en",
                    onSelect = { onLanguageSelected("en") }
                )
                Spacer(modifier = Modifier.height(8.dp))
                LanguageOption(
                    languageCode = "fr",
                    languageName = "Français",
                    flag = "🇫🇷",
                    isSelected = currentLanguage == "fr",
                    onSelect = { onLanguageSelected("fr") }
                )
            }
        },
        confirmButton = {
            TextButton(
                onClick = { onDismiss() },
                content = { Text(TranslationManager.getTranslation("close")) }
            )
        }
    )
}

@Composable
fun LanguageOption(
    languageCode: String,
    languageName: String,
    flag: String,
    isSelected: Boolean,
    onSelect: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onSelect() }
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "$flag $languageName",
            fontSize = 16.sp,
            modifier = Modifier.weight(1f)
        )
        if (isSelected) {
            Icon(
                imageVector = Icons.Default.CheckCircle,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary
            )
        }
    }
}

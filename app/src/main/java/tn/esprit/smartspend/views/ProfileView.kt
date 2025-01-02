package tn.esprit.smartspend.views

import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import tn.esprit.smartspend.utils.TranslationManager

@Composable
fun ProfileView(
    context: Context,
    navigateToLogin: () -> Unit,
    navigateToPrivacyPolicy: () -> Unit
) {
    // Charger la langue depuis les préférences
    TranslationManager.loadLanguagePreference(context)
    var showLanguageDialog by remember { mutableStateOf(false) }
    var selectedLanguage by remember { mutableStateOf(TranslationManager.getTranslation("language")) }
    var isDarkModeEnabled by remember { mutableStateOf(false) } // Mode sombre
    var areNotificationsEnabled by remember { mutableStateOf(true) } // Notifications activées

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

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        // Title Section
        Spacer(modifier = Modifier.height(16.dp))
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .clip(CircleShape)
                    .background(
                        Brush.linearGradient(
                            colors = listOf(Color(0xFF4CAF50), Color(0xFF81C784))
                        )
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Person,
                    contentDescription = "Profile Image",
                    tint = Color.White,
                    modifier = Modifier.size(50.dp)
                )
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column {
                Text(
                    text = "Kapil Mohan",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF1E293B)
                )
                Text(
                    text = TranslationManager.getTranslation("profile_title"),
                    fontSize = 14.sp,
                    color = Color(0xFF6B7280)
                )
            }
        }

        // Main Settings Section
        Spacer(modifier = Modifier.height(16.dp))
        Divider(color = Color(0xFFE5E7EB), thickness = 1.dp)
        Text(
            text = TranslationManager.getTranslation("change_language"),
            fontSize = 18.sp,
            fontWeight = FontWeight.SemiBold,
            color = Color(0xFF1E40AF),
            modifier = Modifier.padding(16.dp)
        )
        SettingsItem(
            title = TranslationManager.getTranslation("privacy_policy"),
            icon = Icons.Default.PrivacyTip,
            onClick = { navigateToPrivacyPolicy() }
        )

        // Other Settings Section
        Spacer(modifier = Modifier.height(16.dp))
        Divider(color = Color(0xFFE5E7EB), thickness = 1.dp)
        SettingsItem(
            title = TranslationManager.getTranslation("language"),
            icon = Icons.Default.Language,
            onClick = { showLanguageDialog = true }
        )
        Divider(color = Color(0xFFE5E7EB), thickness = 1.dp)
        SettingsItem(
            title = TranslationManager.getTranslation("logout"),
            icon = Icons.Default.ExitToApp,
            isDestructive = true,
            onClick = { navigateToLogin() }
        )

        // New Settings Section
        Spacer(modifier = Modifier.height(16.dp))
        Divider(color = Color(0xFFE5E7EB), thickness = 1.dp)

        // Dark Mode Toggle
        SwitchSettingItem(
            title = "Enable Dark Mode",
            icon = Icons.Default.DarkMode,
            isChecked = isDarkModeEnabled,
            onCheckedChange = { isDarkModeEnabled = it }
        )

        // Notifications Toggle
        SwitchSettingItem(
            title = "Enable Notifications",
            icon = Icons.Default.Notifications,
            isChecked = areNotificationsEnabled,
            onCheckedChange = { areNotificationsEnabled = it }
        )

        // Linked Accounts
        SettingsItem(
            title = "Manage Linked Accounts",
            icon = Icons.Default.AccountCircle,
            onClick = { /* Navigate to Linked Accounts Screen */ }
        )

        // Security Settings
        SettingsItem(
            title = "Security & Privacy",
            icon = Icons.Default.Security,
            onClick = { /* Navigate to Security Settings Screen */ }
        )

        // Help & Support
        SettingsItem(
            title = "Help & Support",
            icon = Icons.Default.Help,
            onClick = { /* Navigate to Help & Support Screen */ }
        )

        // App Version Section
        Spacer(modifier = Modifier.height(32.dp))
        Text(
            text = "App ver 2.0.1",
            fontSize = 12.sp,
            color = Color(0xFF9E9E9E),
            modifier = Modifier.align(Alignment.CenterHorizontally)
        )
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
        title = {
            Text(
                text = TranslationManager.getTranslation("change_language"),
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            Column {
                LanguageOption(
                    language = "en",
                    label = "English",
                    isSelected = currentLanguage == "en",
                    onSelect = { onLanguageSelected("en") }
                )
                Spacer(modifier = Modifier.height(8.dp))
                LanguageOption(
                    language = "fr",
                    label = "Français",
                    isSelected = currentLanguage == "fr",
                    onSelect = { onLanguageSelected("fr") }
                )
            }
        },
        confirmButton = {
            TextButton(onClick = { onDismiss() }) {
                Text(text = "Close")
            }
        }
    )
}

@Composable
fun LanguageOption(language: String, label: String, isSelected: Boolean, onSelect: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onSelect() }
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        RadioButton(
            selected = isSelected,
            onClick = { onSelect() }
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = label,
            fontSize = 16.sp
        )
    }
}

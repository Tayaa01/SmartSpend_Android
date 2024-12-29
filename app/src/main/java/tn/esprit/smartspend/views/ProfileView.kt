package tn.esprit.smartspend.views

import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import tn.esprit.smartspend.utils.SharedPrefsManager

@Composable
fun ProfileView(
    context: Context,
    navigateToLogin: () -> Unit,
    navigateToPrivacyPolicy: () -> Unit
) {
    val sharedPrefsManager = SharedPrefsManager(context)

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
                    text = "View and manage your profile",
                    fontSize = 14.sp,
                    color = Color(0xFF6B7280)
                )
            }
        }

        // Main Settings Section
        Spacer(modifier = Modifier.height(16.dp))
        Divider(color = Color(0xFFE5E7EB), thickness = 1.dp)
        Text(
            text = "Main Settings",
            fontSize = 18.sp,
            fontWeight = FontWeight.SemiBold,
            color = Color(0xFF1E40AF),
            modifier = Modifier.padding(16.dp)
        )
        SettingsItem("Change Password", Icons.Default.Lock)
        Divider(color = Color(0xFFE5E7EB), thickness = 1.dp)
        SettingsItem(
            title = "Privacy Policy",
            icon = Icons.Default.PrivacyTip,
            onClick = {
                navigateToPrivacyPolicy() // Navigate to PrivacyPolicyScreen
            }
        )

        // Other Settings Section
        Spacer(modifier = Modifier.height(16.dp))
        Divider(color = Color(0xFFE5E7EB), thickness = 1.dp)
        Text(
            text = "Other Settings",
            fontSize = 18.sp,
            fontWeight = FontWeight.SemiBold,
            color = Color(0xFF1E40AF),
            modifier = Modifier.padding(16.dp)
        )
        SettingsItem("Language", Icons.Default.Language)
        Divider(color = Color(0xFFE5E7EB), thickness = 1.dp)
        SettingsItem(
            title = "Logout",
            icon = Icons.Default.ExitToApp,
            isDestructive = true,
            onClick = {
                sharedPrefsManager.clearToken()
                navigateToLogin() // Redirect to LoginScreen
            }
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

package tn.esprit.smartspend.views

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun ProfileView() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Brush.verticalGradient(colors = listOf(Color(0xFFF7F9FC), Color(0xFFE7E9EF))))
            .padding(16.dp)
    ) {
        // Title Section
        Text(
            text = "Settings",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF1F2937)
        )
        Spacer(modifier = Modifier.height(16.dp))

        // Profile Section
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { /* Navigate to personal details */ }
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(70.dp)
                    .clip(CircleShape)
                    .background(Brush.linearGradient(colors = listOf(Color(0xFF4CAF50), Color(0xFF81C784)))),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Person,
                    contentDescription = "Profile Image",
                    tint = Color.White,
                    modifier = Modifier.size(40.dp)
                )
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column {
                Text(
                    text = "Kapil Mohan",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF1F2937)
                )
                Text(
                    text = "Edit personal details",
                    fontSize = 14.sp,
                    color = Color.Gray
                )
            }
        }

        Divider(color = Color(0xFFE0E0E0), thickness = 1.dp)

        SettingsItemWithSwitch("Dark Mode", Icons.Default.DarkMode, isChecked = true)

        Divider(color = Color(0xFFE0E0E0), thickness = 1.dp)

        // Settings List
        SettingsItem("Edit Profile", Icons.Default.Edit)
        SettingsItem("Change Password", Icons.Default.Lock)
        Divider(color = Color(0xFFE0E0E0), thickness = 1.dp)
        SettingsItemWithSwitch("Notifications", Icons.Default.Notifications, isChecked = true)
        Divider(color = Color(0xFFE0E0E0), thickness = 1.dp)
        SettingsItem("Language", Icons.Default.Language)

        // Privacy Policy
        Divider(color = Color(0xFFE0E0E0), thickness = 1.dp)
        SettingsItem("Privacy Policy", Icons.Default.PrivacyTip)

        // Logout
        SettingsItem("Logout", Icons.Default.ExitToApp, isDestructive = true)


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
    isDestructive: Boolean = false
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { /* Handle click */ }
            .padding(vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = title,
            tint = if (isDestructive) Color.Red else Color(0xFF1F2937),
            modifier = Modifier.size(24.dp)
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
fun SettingsItemWithSwitch(
    title: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    isChecked: Boolean
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = title,
            tint = Color(0xFF1F2937),
            modifier = Modifier.size(24.dp)
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
            onCheckedChange = { /* Handle toggle */ },
            colors = SwitchDefaults.colors(
                checkedThumbColor = Color(0xFF4CAF50),
                uncheckedThumbColor = Color(0xFFE0E0E0)
            )
        )
    }
}



package tn.esprit.smartspend.views

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PrivacyTip
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun PrivacyPolicyScreen() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(Color(0xFF4CAF50), Color(0xFF81C784), Color.White)
                )
            )
            .padding(16.dp)
    ) {
        // Header Section
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Icon(
                    imageVector = Icons.Default.PrivacyTip,
                    contentDescription = "Privacy Icon",
                    tint = Color(0xFF1E293B),
                    modifier = Modifier.size(60.dp)
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Privacy Policy",
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF1E293B)
                )
            }
        }

        // Content Section
        Card(
            modifier = Modifier
                .fillMaxSize()
                .padding(8.dp),
            shape = MaterialTheme.shapes.medium,
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 6.dp) // Adjust elevation for better shadow
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                Text(
                    text = "Overview",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color(0xFF1E293B)
                )
                Divider(
                    modifier = Modifier.padding(vertical = 8.dp),
                    color = Color(0xFFE0E0E0),
                    thickness = 1.dp
                )
                Text(
                    text = "Your privacy is important to us. This application respects and protects the privacy of all users. Below is our policy regarding the collection, use, and disclosure of personal information.",
                    fontSize = 14.sp,
                    color = Color(0xFF374151)
                )
                Spacer(modifier = Modifier.height(16.dp))

                // Sections
                PrivacyPolicySection(
                    title = "1. Data Collection",
                    description = "We only collect data that is necessary for the functioning of the application."
                )
                PrivacyPolicySection(
                    title = "2. Data Usage",
                    description = "The data collected is used to provide and improve the app services."
                )
                PrivacyPolicySection(
                    title = "3. Data Sharing",
                    description = "We do not share your personal data with third parties unless required by law."
                )
                PrivacyPolicySection(
                    title = "4. Data Security",
                    description = "Your data is secured using industry-standard encryption techniques."
                )
                PrivacyPolicySection(
                    title = "5. User Rights",
                    description = "You have the right to access, modify, or delete your personal data at any time."
                )

                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "For more details or inquiries, please contact our support team.",
                    fontSize = 14.sp,
                    color = Color(0xFF374151)
                )
            }
        }

        // Footer Button
        Spacer(modifier = Modifier.height(16.dp))
        Button(
            onClick = { /* Navigate back or to support */ },
            modifier = Modifier.align(Alignment.CenterHorizontally),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4CAF50))
        ) {
            Text(text = "Contact Support", color = Color.White)
        }
    }
}

@Composable
fun PrivacyPolicySection(title: String, description: String) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
    ) {
        Text(
            text = title,
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF1E293B)
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = description,
            fontSize = 14.sp,
            color = Color(0xFF374151)
        )
    }
}

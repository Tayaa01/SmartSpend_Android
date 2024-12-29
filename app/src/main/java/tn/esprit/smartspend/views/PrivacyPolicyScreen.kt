package tn.esprit.smartspend.views

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp


@Composable
fun PrivacyPolicyScreen() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(16.dp)
    ) {
        Text(
            text = "Privacy Policy",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF1E293B)
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = """
                Your privacy is important to us. This application respects and protects the privacy of all users. The following outlines our policies regarding the collection, use, and disclosure of personal information.

                1. **Data Collection**: We only collect data that is necessary for the functioning of the application.
                2. **Data Usage**: The data collected is used to provide and improve the app services.
                3. **Data Sharing**: We do not share your personal data with third parties unless required by law.
                4. **Data Security**: Your data is secured using industry-standard encryption techniques.
                5. **User Rights**: You have the right to access, modify, or delete your personal data at any time.

                For more details or inquiries, please contact our support team.
            """.trimIndent(),
            fontSize = 14.sp,
            color = Color(0xFF374151)
        )
    }
}

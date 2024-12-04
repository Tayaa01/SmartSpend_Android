import android.content.Context
import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.platform.LocalContext
import java.util.Calendar
import tn.esprit.smartspend.utils.SharedPrefsManager
import tn.esprit.smartspend.network.RetrofitInstance

@Composable
fun TimelineView() {
    val context = LocalContext.current // Récupération automatique du contexte
    var recommendationText by remember { mutableStateOf("Loading...") }
    var isLoading by remember { mutableStateOf(true) }  // État de chargement

    // Extraire la date d'aujourd'hui et formater le mois (compatible avec les anciennes versions)
    val calendar = Calendar.getInstance()
    val currentMonth = String.format("%04d-%02d", calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH) + 1)

    // Charger les recommandations dès que le composable est chargé
    LaunchedEffect(Unit) {
        val sharedPrefsManager = SharedPrefsManager(context)
        val userToken = sharedPrefsManager.getToken()

        if (userToken.isNullOrEmpty()) {
            recommendationText = "User token not found!"
            isLoading = false
        } else {
            try {
                // Envoie la requête à l'API avec le token et le mois actuel
                val response = RetrofitInstance.apiService.getRecommendation(userToken, currentMonth)
                recommendationText = response.recommendationText
            } catch (e: Exception) {
                recommendationText = "Failed to fetch recommendation: ${e.localizedMessage}"
            } finally {
                isLoading = false  // Arrêter l'indicateur de chargement
            }
        }
    }

    // UI
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)  // Ajouter un padding autour de l'écran
            .background(MaterialTheme.colorScheme.background)  // Fond clair
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Animation conditionnelle de transition du texte
            AnimatedVisibility(
                visible = isLoading,
                enter = fadeIn() + scaleIn(),
                exit = fadeOut() + scaleOut()
            ) {
                CircularProgressIndicator(
                    modifier = Modifier,
                    color = MaterialTheme.colorScheme.primary
                )
            }

            Spacer(modifier = Modifier.height(16.dp))  // Ajouter de l'espace entre l'indicateur et le texte

            AnimatedVisibility(
                visible = !isLoading,
                enter = slideInVertically(initialOffsetY = { it }) + fadeIn(),
                exit = slideOutVertically(targetOffsetY = { it }) + fadeOut()
            ) {
                Text(
                    text = recommendationText,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                        .background(Color.White, RoundedCornerShape(12.dp)) // Fond blanc avec coins arrondis
                        .padding(24.dp),  // Padding à l'intérieur du fond
                    color = MaterialTheme.colorScheme.onBackground  // Couleur de texte qui contraste bien avec le fond
                )
            }
        }
    }
}

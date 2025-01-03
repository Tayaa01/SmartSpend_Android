package tn.esprit.smartspend.views

import android.content.Context
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import tn.esprit.smartspend.model.RecommendationResponse
import tn.esprit.smartspend.network.RetrofitInstance
import tn.esprit.smartspend.ui.theme.Navy
import tn.esprit.smartspend.ui.theme.Sand
import tn.esprit.smartspend.utils.SharedPrefsManager
import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Brush
import tn.esprit.smartspend.ui.theme.*

@Composable
fun RecommendationsView() {
    val context = LocalContext.current
    val sharedPrefsManager = remember { SharedPrefsManager(context) }
    val storedToken = remember { sharedPrefsManager.getToken() }
    
    var recommendations by remember { mutableStateOf<RecommendationResponse?>(null) }
    var isLoading by remember { mutableStateOf(true) }
    var error by remember { mutableStateOf<String?>(null) }
    val scope = rememberCoroutineScope()

    LaunchedEffect(storedToken) {
        scope.launch {
            try {
                Log.d("RecommendationsView", "Fetching recommendations with token: $storedToken")
                val response = withContext(Dispatchers.IO) {
                    RetrofitInstance.api.getRecommendations(storedToken ?: "").execute()
                }
                if (response.isSuccessful) {
                    recommendations = response.body()
                    Log.d("RecommendationsView", "Received recommendations: ${response.body()}")
                } else {
                    error = "Error: ${response.code()} - ${response.message()}"
                    Log.e("RecommendationsView", "Error response: ${response.errorBody()?.string()}")
                }
            } catch (e: Exception) {
                error = "Error: ${e.message}"
                Log.e("RecommendationsView", "Exception while fetching recommendations", e)
            } finally {
                isLoading = false
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
    ) {
        Text(
            text = "Recommended Actions",
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.onBackground,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(vertical = 16.dp)
        )

        when {
            isLoading -> {
                CircularProgressIndicator(
                    modifier = Modifier
                        .size(50.dp)
                        .align(androidx.compose.ui.Alignment.CenterHorizontally),
                    color = MostImportantColor
                )
            }
            error != null -> {
                Text(
                    text = error ?: "Unknown error occurred",
                    color = CustomRed,
                    modifier = Modifier.padding(16.dp)
                )
            }
            recommendations?.suggestions?.isEmpty() == true -> {
                Text(
                    text = "No recommendations available",
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier.padding(16.dp)
                )
            }
            else -> {
                recommendations?.suggestions?.forEach { suggestion ->
                    RecommendationCard(
                        title = suggestion.category,
                        message = suggestion.advice,
                        color = getCategoryColor(suggestion.category)
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                }
            }
        }
    }
}

@Composable
private fun RecommendationCard(
    title: String,
    message: String,
    color: Color
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        colors = CardDefaults.cardColors(containerColor = color)
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = Sand
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = message,
                style = MaterialTheme.typography.bodyMedium,
                color = Sand.copy(alpha = 0.9f)
            )
        }
    }
}

private fun getCategoryColor(category: String): Color {
    return when (category) {
        "Financial Health" -> MostImportantColor
        "Savings & Investments" -> Teal
        "Debt Management" -> CustomRed
        "Budget Optimization" -> Navy
        else -> LeastImportantColor
    }
}
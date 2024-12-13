import android.content.Context
import androidx.compose.animation.*
import androidx.compose.foundation.Canvas
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
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.platform.LocalContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import tn.esprit.smartspend.model.Category
import tn.esprit.smartspend.model.Expense
import tn.esprit.smartspend.utils.SharedPrefsManager
import tn.esprit.smartspend.network.RetrofitInstance
import java.util.Calendar

@Composable
fun TimelineView() {
    val context = LocalContext.current
    var recommendationText by remember { mutableStateOf("Loading...") }
    var isLoading by remember { mutableStateOf(true) }
    var showChart by remember { mutableStateOf(false) } // State to toggle chart visibility

    // Extract current date and format the month
    val calendar = Calendar.getInstance()
    val currentMonth = String.format("%04d-%02d", calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH) + 1)

    LaunchedEffect(Unit) {
        val sharedPrefsManager = SharedPrefsManager(context)
        val userToken = sharedPrefsManager.getToken()

        if (userToken.isNullOrEmpty()) {
            recommendationText = "User token not found!"
            isLoading = false
        } else {
            try {
                val response = withContext(Dispatchers.IO) {
                    RetrofitInstance.apiService.getRecommendation(userToken, currentMonth)
                }
                recommendationText = response.recommendationText
            } catch (e: Exception) {
                recommendationText = "Failed to fetch recommendation: ${e.localizedMessage}"
            } finally {
                isLoading = false
                showChart = true
            }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .background(MaterialTheme.colorScheme.background)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            AnimatedVisibility(
                visible = isLoading,
                enter = fadeIn() + scaleIn(),
                exit = fadeOut() + scaleOut()
            ) {
                CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
            }

            Spacer(modifier = Modifier.height(16.dp))

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
                        .background(Color.White, RoundedCornerShape(12.dp))
                        .padding(24.dp),
                    color = MaterialTheme.colorScheme.onBackground
                )
            }
            Spacer(modifier = Modifier.height(24.dp))

            // Show the pie chart when loading is complete
            if (showChart) {
                ExpensesPieChartViewWithCategories()
            }
        }
    }
}

@Composable
fun ExpensesPieChartViewWithCategories() {
    val context = LocalContext.current
    var expenses by remember { mutableStateOf<List<Expense>>(emptyList()) }
    var categories by remember { mutableStateOf<Map<String, Category>>(emptyMap()) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var isLoading by remember { mutableStateOf(true) }

    LaunchedEffect(Unit) {
        val sharedPrefsManager = SharedPrefsManager(context)
        val userToken = sharedPrefsManager.getToken()

        if (userToken.isNullOrEmpty()) {
            errorMessage = "User token not found!"
            isLoading = false
        } else {
            try {
                val expenseResponse = withContext(Dispatchers.IO) {
                    RetrofitInstance.apiService.getExpenses(userToken).execute()
                }
                val categoryResponse = withContext(Dispatchers.IO) {
                    RetrofitInstance.apiService.getCategories().execute()
                }

                if (expenseResponse.isSuccessful && categoryResponse.isSuccessful) {
                    expenses = expenseResponse.body().orEmpty()
                    categories = categoryResponse.body()?.associateBy { it._id }.orEmpty()
                } else {
                    errorMessage = "Failed to fetch data."
                }
            } catch (e: Exception) {
                errorMessage = "Exception: ${e.localizedMessage}"
            } finally {
                isLoading = false
            }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .background(MaterialTheme.colorScheme.background)
    ) {
        if (isLoading) {
            Text(text = "Loading...")
        } else if (errorMessage != null) {
            Text(text = errorMessage!!)
        } else {
            val groupedExpenses = expenses.groupBy { categories[it.category]?.name ?: "Unknown" }
                .mapValues { entry -> entry.value.sumOf { it.amount } }

            val categoryAmounts = groupedExpenses.map { (categoryName, total) ->
                CategoryAmount(categoryName, total)
            }

            if (categoryAmounts.isEmpty()) {
                Text(text = "No expenses found.")
            } else {
                Column(
                    Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Expenses by Category",
                        style = MaterialTheme.typography.titleMedium,
                        modifier = Modifier.padding(bottom = 16.dp)
                    )

                    PieChart(
                        data = categoryAmounts,
                        colors = categoryAmounts.map { Color((0xFF000000..0xFFFFFFFF).random()) },
                        modifier = Modifier.size(250.dp)
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    categoryAmounts.forEachIndexed { index, item ->
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(16.dp)
                                    .background(
                                        color = Color((0xFF000000..0xFFFFFFFF).random())
                                    )
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("${item.categoryName}: ${"%.2f".format(item.totalAmount)}")
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun PieChart(
    data: List<CategoryAmount>,
    colors: List<Color>,
    modifier: Modifier = Modifier
) {
    val total = data.sumOf { it.totalAmount }
    val proportions = data.map { it.totalAmount / total }
    val angles = proportions.map { it * 360f }

    Canvas(modifier = modifier) {
        var startAngle = 0f
        angles.forEachIndexed { index, sweepAngle ->
            drawArc(
                color = colors[index % colors.size],
                startAngle = startAngle,
                sweepAngle = sweepAngle.toFloat(),
                useCenter = true,
                size = Size(size.minDimension, size.minDimension)
            )
            startAngle += sweepAngle.toFloat()
        }
    }
}

data class CategoryAmount(
    val categoryName: String,
    val totalAmount: Double
)

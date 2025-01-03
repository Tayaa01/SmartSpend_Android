import android.content.Context
import androidx.compose.animation.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
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
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextOverflow
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import tn.esprit.smartspend.model.Category
import tn.esprit.smartspend.model.Expense
import tn.esprit.smartspend.model.Income
import tn.esprit.smartspend.utils.SharedPrefsManager
import tn.esprit.smartspend.network.RetrofitInstance
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material3.*
import androidx.compose.runtime.*

import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextAlign
import tn.esprit.smartspend.ui.theme.CustomRed
import tn.esprit.smartspend.ui.theme.LightGreen
import tn.esprit.smartspend.ui.theme.MostImportantColor
import tn.esprit.smartspend.ui.theme.Sand
import tn.esprit.smartspend.ui.components.StatCard
import tn.esprit.smartspend.ui.components.StatData
import tn.esprit.smartspend.ui.components.LinearProgressBar
import tn.esprit.smartspend.ui.components.CircularProgressBar
import androidx.compose.ui.viewinterop.AndroidView
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.utils.ColorTemplate
import com.github.mikephil.charting.components.Legend
import kotlinx.coroutines.runBlocking
import tn.esprit.smartspend.views.fetchCategories
import tn.esprit.smartspend.views.resolveCategoryName

@Composable
fun AnalyticsView() {
    val context = LocalContext.current
    var expenses by remember { mutableStateOf<List<Expense>>(emptyList()) }
    var incomes by remember { mutableStateOf<List<Income>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    var totalExpenses by remember { mutableStateOf(0.0) }
    var totalIncomes by remember { mutableStateOf(0.0) }
    var netBalance by remember { mutableStateOf(0.0) }

    LaunchedEffect(Unit) {
        val sharedPrefsManager = SharedPrefsManager(context)
        val token = sharedPrefsManager.getToken()

        try {
            expenses = fetchExpensesData(token)
            incomes = fetchIncomesData(token)

            totalExpenses = expenses.sumOf { it.amount }
            totalIncomes = incomes.sumOf { it.amount }
            netBalance = totalIncomes - totalExpenses

        } catch (e: Exception) {
            errorMessage = e.message
        } finally {
            isLoading = false
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        when {
            isLoading -> {
                CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.CenterHorizontally),
                    color = MostImportantColor
                )
            }
            errorMessage != null -> {
                Text(
                    text = errorMessage ?: "Unknown error",
                    color = CustomRed,
                    modifier = Modifier.padding(16.dp)
                )
            }
            else -> {
                StatisticsContent(
                    expenses = expenses,
                    incomes = incomes,
                    totalExpenses = totalExpenses,
                    totalIncomes = totalIncomes,
                    netBalance = netBalance
                )
            }
        }
    }
}

@Composable
fun StatisticsContent(
    expenses: List<Expense>,
    incomes: List<Income>,
    totalExpenses: Double,
    totalIncomes: Double,
    netBalance: Double
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {
        // Summary Section
        SummarySection(
            totalExpenses = totalExpenses,
            totalIncomes = totalIncomes,
            netBalance = netBalance
        )

        Spacer(modifier = Modifier.height(20.dp))

        // Progress Bar Section
        ExpensesVsIncomesSection(
            totalExpenses = totalExpenses,
            totalIncomes = totalIncomes
        )

        Spacer(modifier = Modifier.height(20.dp))

        // Detailed Insights
        DetailedInsightsSection(
            expenses = expenses,
            incomes = incomes,
            totalExpenses = totalExpenses,
            totalIncomes = totalIncomes
        )

        Spacer(modifier = Modifier.height(20.dp))

        // Charts Section
        ChartsSection(expenses = expenses)
    }
}

@Composable
fun SummarySection(
    totalExpenses: Double,
    totalIncomes: Double,
    netBalance: Double
) {
    Column {
        Text(
            text = "Summary",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(16.dp)
        )

        Row(
            modifier = Modifier
                .horizontalScroll(rememberScrollState())
                .padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            StatCard(
                data = StatData(
                    title = "Total Expenses",
                    value = totalExpenses,
                    color = CustomRed,
                    icon = Icons.Default.ArrowDownward
                ),
                currency = "USD"
            )

            StatCard(
                data = StatData(
                    title = "Total Incomes",
                    value = totalIncomes,
                    color = LightGreen,
                    icon = Icons.Default.ArrowUpward
                ),
                currency = "USD"
            )

            StatCard(
                data = StatData(
                    title = "Net Balance",
                    value = netBalance,
                    color = if (netBalance >= 0) LightGreen else CustomRed,
                    icon = Icons.Default.AccountBalance
                ),
                currency = "USD"
            )
        }
    }
}

@Composable
fun ExpensesVsIncomesSection(
    totalExpenses: Double,
    totalIncomes: Double
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 5.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "Expenses vs Incomes",
                style = MaterialTheme.typography.titleMedium,
                color = MostImportantColor
            )

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("Expenses", color = CustomRed)
                Text("${"%.2f".format(totalExpenses)} USD", color = CustomRed)
            }

            Spacer(modifier = Modifier.height(8.dp))

            LinearProgressBar(
                value = totalExpenses,
                total = totalIncomes,
                color = CustomRed
            )

            Text(
                text = "${if (totalIncomes > 0) ((totalExpenses / totalIncomes) * 100).toInt() else 0}% of Income Spent",
                modifier = Modifier.align(Alignment.CenterHorizontally),
                color = MostImportantColor,
                style = MaterialTheme.typography.bodySmall
            )
        }
    }
}

@Composable
fun DetailedInsightsSection(
    expenses: List<Expense>,
    incomes: List<Income>,
    totalExpenses: Double,
    totalIncomes: Double
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 5.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "Detailed Insights",
                style = MaterialTheme.typography.titleMedium,
                color = MostImportantColor
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                InsightCard(
                    title = "Average Expenses",
                    value = if (expenses.isNotEmpty()) totalExpenses / expenses.size else 0.0,
                    total = totalExpenses,
                    color = CustomRed
                )

                InsightCard(
                    title = "Average Income",
                    value = if (incomes.isNotEmpty()) totalIncomes / incomes.size else 0.0,
                    total = totalIncomes,
                    color = LightGreen
                )
            }
        }
    }
}

@Composable
fun InsightCard(
    title: String,
    value: Double,
    total: Double,
    color: Color
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.padding(8.dp)
    ) {
        CircularProgressBar(
            amount = value,
            totalAmount = total,
            color = color
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = title,
            color = color,
            style = MaterialTheme.typography.bodyMedium
        )

        Text(
            text = "${"%.2f".format(value)} USD",
            color = color,
            style = MaterialTheme.typography.bodySmall
        )
    }
}

@Composable
fun ChartsSection(expenses: List<Expense>) {
    var categories by remember { mutableStateOf<List<Category>>(emptyList()) }

    // Fetch categories once when the component is created
    LaunchedEffect(Unit) {
        categories = fetchCategories()
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Text(
            text = "Expense Distribution",
            style = MaterialTheme.typography.titleMedium,
            color = MostImportantColor
        )

        Spacer(modifier = Modifier.height(16.dp))

        AndroidView(
            modifier = Modifier
                .fillMaxWidth()
                .height(300.dp),
            factory = { context ->
                PieChart(context).apply {
                    description.isEnabled = false
                    setDrawEntryLabels(true)
                    legend.apply {
                        verticalAlignment = Legend.LegendVerticalAlignment.TOP
                        horizontalAlignment = Legend.LegendHorizontalAlignment.RIGHT
                        orientation = Legend.LegendOrientation.VERTICAL
                        setDrawInside(false)
                    }
                    setEntryLabelColor(android.graphics.Color.BLACK)
                    setHoleColor(android.graphics.Color.WHITE)
                }
            },
            update = { chart ->
                val groupedExpenses = expenses
                    .groupBy { expense -> resolveCategoryName(expense.category, categories) }
                    .mapValues { it.value.sumOf { expense -> expense.amount } }

                val totalExpenses = groupedExpenses.values.sum()
                // Only keep expenses that are 3% or more of total
                val significantExpenses = groupedExpenses.filterValues { amount ->
                    (amount / totalExpenses) >= 0.03 // 3% threshold
                }

                val entries = significantExpenses.map { (categoryName, amount) ->
                    PieEntry(
                        amount.toFloat(),
                        categoryName,
                        // You can add percentage in the label
                        "${categoryName}\n(${String.format("%.1f", (amount / totalExpenses) * 100)}%)"
                    )
                }

                val dataSet = PieDataSet(entries, "Expenses").apply {
                    colors = ColorTemplate.MATERIAL_COLORS.toList()
                    valueTextSize = 12f
                    valueTextColor = android.graphics.Color.BLACK
                }

                chart.data = PieData(dataSet)
                chart.invalidate()
            }
        )
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
            Text(text = "")
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
                // Create a list of colors based on the number of categories
                val pieChartColors = generatePieChartColors(categoryAmounts.size)

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
                        colors = pieChartColors,
                        modifier = Modifier.size(250.dp)
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    categoryAmounts.forEachIndexed { index, item ->
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(16.dp)
                                    .background(color = pieChartColors[index])
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
fun IncomePieChartViewWithCategories() {
    val context = LocalContext.current
    var incomes by remember { mutableStateOf<List<Income>>(emptyList()) }
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
                val incomeResponse = withContext(Dispatchers.IO) {
                    RetrofitInstance.apiService.getIncomes(userToken).execute()
                }
                val categoryResponse = withContext(Dispatchers.IO) {
                    RetrofitInstance.apiService.getCategories().execute()
                }

                if (incomeResponse.isSuccessful && categoryResponse.isSuccessful) {
                    incomes = incomeResponse.body().orEmpty()
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
            Text(text = "")
        } else if (errorMessage != null) {
            Text(text = errorMessage!!)
        } else {
            val groupedIncomes = incomes.groupBy { categories[it.category]?.name ?: "Unknown" }
                .mapValues { entry -> entry.value.sumOf { it.amount } }

            val categoryAmounts = groupedIncomes.map { (categoryName, total) ->
                CategoryAmount(categoryName, total)
            }

            if (categoryAmounts.isEmpty()) {
                Text(text = "No incomes found.")
            } else {
                // Create a list of colors based on the number of categories
                val pieChartColors = generatePieChartColors(categoryAmounts.size)

                Column(
                    Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Incomes by Category",
                        style = MaterialTheme.typography.titleMedium,
                        modifier = Modifier.padding(bottom = 16.dp)
                    )

                    PieChart(
                        data = categoryAmounts,
                        colors = pieChartColors,
                        modifier = Modifier.size(250.dp)
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    categoryAmounts.forEachIndexed { index, item ->
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(16.dp)
                                    .background(color = pieChartColors[index])
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

fun generatePieChartColors(size: Int): List<Color> {
    return List(size) { Color((0xFF000000..0xFFFFFFFF).random()) }
}

@Composable
fun ExpensesBarChartViewWithCategories() {
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
            Text(text = "")
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

                    BarChart(
                        data = categoryAmounts,
                        modifier = Modifier.fillMaxWidth().height(250.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun IncomeBarChartViewWithCategories() {
    val context = LocalContext.current
    var incomes by remember { mutableStateOf<List<Income>>(emptyList()) }
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
                val incomeResponse = withContext(Dispatchers.IO) {
                    RetrofitInstance.apiService.getIncomes(userToken).execute()
                }
                val categoryResponse = withContext(Dispatchers.IO) {
                    RetrofitInstance.apiService.getCategories().execute()
                }

                if (incomeResponse.isSuccessful && categoryResponse.isSuccessful) {
                    incomes = incomeResponse.body().orEmpty()
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
            val groupedIncomes = incomes.groupBy { categories[it.category]?.name ?: "Unknown" }
                .mapValues { entry -> entry.value.sumOf { it.amount } }

            val categoryAmounts = groupedIncomes.map { (categoryName, total) ->
                CategoryAmount(categoryName, total)
            }

            if (categoryAmounts.isEmpty()) {
                Text(text = "No incomes found.")
            } else {
                Column(
                    Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Incomes by Category",
                        style = MaterialTheme.typography.titleMedium,
                        modifier = Modifier.padding(bottom = 16.dp)
                    )

                    BarChart(
                        data = categoryAmounts,
                        modifier = Modifier.fillMaxWidth().height(250.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun BarChart(data: List<CategoryAmount>, modifier: Modifier = Modifier) {
    val maxAmount = data.maxOfOrNull { it.totalAmount } ?: 1.0
    val maxHeight = 200.dp
    val scrollState = rememberScrollState()

    Column(
        modifier = modifier
            .horizontalScroll(scrollState)
            .padding(horizontal = 8.dp, vertical = 16.dp)
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(24.dp),
            verticalAlignment = Alignment.Bottom,
            modifier = Modifier.width((80.dp * data.size))
        ) {
            data.forEach { item ->
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.width(80.dp)
                ) {
                    // Amount text
                    Text(
                        text = "%.1f".format(item.totalAmount),
                        style = MaterialTheme.typography.bodySmall,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.padding(bottom = 4.dp)
                    )

                    // Bar
                    Box(
                        modifier = Modifier
                            .width(40.dp)
                            .height(
                                (maxHeight.value * (item.totalAmount / maxAmount))
                                    .coerceAtLeast(30.0)  // Minimum height of 30dp
                                    .dp
                            )
                            .background(
                                color = MaterialTheme.colorScheme.primary,
                                shape = RoundedCornerShape(topStart = 4.dp, topEnd = 4.dp)
                            )
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    // Category name with rotation for better space usage
                    Text(
                        text = item.categoryName,
                        style = MaterialTheme.typography.bodySmall,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier
                            .width(80.dp)
                            .padding(top = 4.dp),
                        textAlign = TextAlign.Center
                    )
                }
            }
        }
    }
}

@Composable
fun PieChart(data: List<CategoryAmount>, colors: List<Color>, modifier: Modifier = Modifier) {
    val total = data.sumOf { it.totalAmount }
    val proportions = data.map { it.totalAmount / total }
    val angles = proportions.map { (it * 360f).toFloat() }

    Canvas(modifier = modifier) {
        var startAngle = 0f
        angles.forEachIndexed { index, sweepAngle ->
            drawArc(
                color = colors[index % colors.size],
                startAngle = startAngle,
                sweepAngle = sweepAngle,
                useCenter = true,
                size = Size(size.minDimension, size.minDimension)
            )
            startAngle = (startAngle + sweepAngle) // Ensure startAngle remains Float
        }
    }
}

// Regular data class - no @Composable needed
data class CategoryAmount(
    val categoryName: String,
    val totalAmount: Double
)

// Utility functions - no @Composable needed
object DateUtils {
    fun getLastWeekDates(): List<String> {
        val calendar = Calendar.getInstance()
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        return List(7) {
            val date = dateFormat.format(calendar.time)
            calendar.add(Calendar.DAY_OF_YEAR, -1)
            date
        }.reversed()
    }

    fun aggregateDataByDate(
        data: List<Pair<String, Double>>,
        dates: List<String>
    ): List<Double> {
        val map = data.groupBy({ it.first }, { it.second }).mapValues { it.value.sum() }
        return dates.map { map[it] ?: 0.0 }
    }

    fun extractDateOnly(timestamp: String): String {
        val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault())
        val outputFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        return try {
            val date = inputFormat.parse(timestamp)
            outputFormat.format(date ?: Date())
        } catch (e: Exception) {
            timestamp.substring(0, 10) // Fallback to first 10 characters (yyyy-MM-dd)
        }
    }
}

@Composable
fun IncomeExpenseLineChart(
    incomeData: List<Double>,
    expenseData: List<Double>,
    dates: List<String>
) {
    val maxY = (incomeData + expenseData).maxOrNull()?.let { it + (it * 0.1) } ?: 100.0

    Canvas(modifier = Modifier.fillMaxWidth().height(300.dp)) {
        val chartWidth = size.width
        val chartHeight = size.height

        val horizontalStep = chartWidth / (dates.size - 1)
        val verticalScale = chartHeight / maxY.toFloat()

        // Draw axes
        drawLine(
            color = Color.Gray,
            start = Offset(0f, chartHeight),
            end = Offset(chartWidth, chartHeight),
            strokeWidth = 2f
        )
        drawLine(
            color = Color.Gray,
            start = Offset(0f, 0f),
            end = Offset(0f, chartHeight),
            strokeWidth = 2f
        )

        // Draw lines for income and expenses
        fun drawLineChart(data: List<Double>, color: Color) {
            for (i in 0 until data.size - 1) {
                val x1 = i * horizontalStep
                val y1 = chartHeight - (data[i] * verticalScale).toFloat()
                val x2 = (i + 1) * horizontalStep
                val y2 = chartHeight - (data[i + 1] * verticalScale).toFloat()

                drawLine(
                    color = color,
                    start = Offset(x1, y1),
                    end = Offset(x2, y2),
                    strokeWidth = 4f
                )
            }
        }

        drawLineChart(incomeData, Color.Green)
        drawLineChart(expenseData, Color.Red)

        // Draw labels with exact dates
        val paint = android.graphics.Paint().apply {
            textSize = 32f
            color = android.graphics.Color.BLACK
            textAlign = android.graphics.Paint.Align.CENTER
        }
        for (i in dates.indices) {
            val x = i * horizontalStep
            val y = chartHeight + 40f // Position below the x-axis
            val label = dates[i] // Full date in yyyy-MM-dd format
            drawContext.canvas.nativeCanvas.drawText(label, x, y, paint)
        }
    }
}

private suspend fun fetchExpensesData(token: String?): List<Expense> {
    return withContext(Dispatchers.IO) {
        try {
            RetrofitInstance.api.getExpenses(token ?: "").execute().body() ?: emptyList()
        } catch (e: Exception) {
            emptyList()
        }
    }
}

private suspend fun fetchIncomesData(token: String?): List<Income> {
    return withContext(Dispatchers.IO) {
        try {
            RetrofitInstance.api.getIncomes(token ?: "").execute().body() ?: emptyList()
        } catch (e: Exception) {
            emptyList()
        }
    }
}

import android.content.Context
import androidx.compose.animation.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
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

@Composable
fun AnalyticsView() {
    val context = LocalContext.current
    var recommendationText by remember { mutableStateOf("Loading...") }
    var isLoading by remember { mutableStateOf(true) }
    var showCharts by remember { mutableStateOf(false) }
    var incomeData by remember { mutableStateOf(emptyList<Pair<String, Double>>()) }
    var expenseData by remember { mutableStateOf(emptyList<Pair<String, Double>>()) }
    var selectedChartType by remember { mutableStateOf("Pie Chart") } // Add state for chart type selection

    val lastWeekDates = getLastWeekDates()

    LaunchedEffect(Unit) {
        val sharedPrefsManager = SharedPrefsManager(context)
        val userToken = sharedPrefsManager.getToken()

        if (userToken.isNullOrEmpty()) {
            recommendationText = "User token not found!"
            isLoading = false
        } else {
            try {
                val incomes = withContext(Dispatchers.IO) {
                    RetrofitInstance.apiService.getIncomes(userToken).execute().body().orEmpty()
                }
                val expenses = withContext(Dispatchers.IO) {
                    RetrofitInstance.apiService.getExpenses(userToken).execute().body().orEmpty()
                }

                // Extract date-only data
                incomeData = incomes.map { extractDateOnly(it.date) to it.amount }
                expenseData = expenses.map { extractDateOnly(it.date) to it.amount }
            } catch (e: Exception) {
                recommendationText = "Failed to fetch data: ${e.localizedMessage}"
            } finally {
                isLoading = false
                showCharts = true
            }
        }
    }

    val aggregatedIncomeData = aggregateDataByDate(incomeData, lastWeekDates)
    val aggregatedExpenseData = aggregateDataByDate(expenseData, lastWeekDates)

    val scrollState = rememberScrollState()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .background(MaterialTheme.colorScheme.background)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top
        ) {
            if (isLoading) {
                CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
            } else {
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
                Spacer(modifier = Modifier.height(24.dp))

                // Chart Type Selector
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center
                ) {
                    Button(
                        onClick = { selectedChartType = "Pie Chart" },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (selectedChartType == "Pie Chart") MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surface
                        )
                    ) {
                        Text("Pie Chart")
                    }
                    Spacer(modifier = Modifier.width(16.dp))
                    Button(
                        onClick = { selectedChartType = "Bar Chart" },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (selectedChartType == "Bar Chart") MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surface
                        )
                    ) {
                        Text("Bar Chart")
                    }
                }
                Spacer(modifier = Modifier.height(24.dp))

                // Display Selected Chart Type
                when (selectedChartType) {
                    "Pie Chart" -> {
                        ExpensesPieChartViewWithCategories()
                        Spacer(modifier = Modifier.height(32.dp))
                        IncomePieChartViewWithCategories()
                    }
                    "Bar Chart" -> {
                        ExpensesBarChartViewWithCategories()
                        Spacer(modifier = Modifier.height(32.dp))
                        IncomeBarChartViewWithCategories()
                    }
                }

                Spacer(modifier = Modifier.height(32.dp))

                Text(
                    text = "Income vs Expenses (Last Week)",
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                IncomeExpenseLineChart(
                    incomeData = aggregatedIncomeData,
                    expenseData = aggregatedExpenseData,
                    dates = lastWeekDates
                )
                Spacer(modifier = Modifier.height(32.dp))
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
//
@Composable
fun BarChart(data: List<CategoryAmount>, modifier: Modifier = Modifier) {
    val maxAmount = data.maxOfOrNull { it.totalAmount } ?: 1f
    val maxHeight = 200.dp  // Set a maximum height for the bars

    Row(
        verticalAlignment = Alignment.Bottom,
        modifier = modifier.padding(horizontal = 16.dp)
    ) {
        data.forEach { item ->
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.padding(horizontal = 8.dp)
            ) {
                Box(
                    modifier = Modifier
                        .width(24.dp)
                        .heightIn(max = maxHeight)  // Limit the height of the bars
                        .fillMaxHeight(fraction = (item.totalAmount.toFloat() / maxAmount.toFloat()))
                        .background(Color((0xFF000000..0xFFFFFFFF).random()))
                )
                Spacer(modifier = Modifier.height(4.dp))

                // Amount text - ensure it's always visible
                Text(
                    text = "%.2f".format(item.totalAmount),
                    style = MaterialTheme.typography.bodyMedium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(4.dp))

                // Category name text
                Text(
                    text = item.categoryName,
                    style = MaterialTheme.typography.bodyMedium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}

@Composable
fun PieChart(data: List<CategoryAmount>, colors: List<Color>, modifier: Modifier = Modifier) {
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

        // Draw labels
        for (i in dates.indices) {
            val x = i * horizontalStep
            val label = dates[i].substring(5) // Show MM-DD
            drawContext.canvas.nativeCanvas.drawText(
                label,
                x,
                chartHeight + 20f,
                android.graphics.Paint().apply {
                    textSize = 32f
                    color = android.graphics.Color.BLACK
                    textAlign = android.graphics.Paint.Align.CENTER
                }
            )
        }
    }
}

fun extractDateOnly(timestamp: String): String {
    // Parse and format the timestamp to extract only the date (yyyy-MM-dd)
    val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault())
    val outputFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    return try {
        val date = inputFormat.parse(timestamp)
        outputFormat.format(date ?: Date())
    } catch (e: Exception) {
        timestamp.substring(0, 10) // Fallback to first 10 characters (yyyy-MM-dd)
    }
}
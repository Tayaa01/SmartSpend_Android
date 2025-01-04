import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import tn.esprit.smartspend.model.Category
import tn.esprit.smartspend.model.Expense
import tn.esprit.smartspend.model.Income
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext

@Composable
fun AnalyticsView(
    fetchExpenses: List<Expense>,
    fetchIncomes: List<Income>,
    fetchCategories: List<Category>
    ) {

    var totalExpensesAmount by remember { mutableStateOf(0.0) }
    var totalIncomesAmount by remember { mutableStateOf(0.0) }
    var netBalanceAmount by remember { mutableStateOf(0.0) }
    var selectedCurrency by remember { mutableStateOf("USD") }
    var categories by remember { mutableStateOf(listOf<Category>()) }
    var isLoadingCategories by remember { mutableStateOf(true) }
    var expenses by remember { mutableStateOf<List<Expense>>(emptyList()) }
    var incomes by remember { mutableStateOf<List<Income>>(emptyList()) }



    // Simulate data fetching on UI load
    LaunchedEffect(Unit) {
        expenses = fetchExpenses
        incomes = fetchIncomes
        categories = fetchCategories
    }

    // Update statistics when expenses or incomes change
    LaunchedEffect(expenses, incomes) {
        totalExpensesAmount = expenses.sumOf { it.amount }
        totalIncomesAmount = incomes.sumOf { it.amount }
        netBalanceAmount = totalIncomesAmount - totalExpensesAmount
    }

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Statistics") })
        }
    ) {
        if (expenses.isEmpty() || incomes.isEmpty()) {
            CircularProgressIndicator(Modifier.padding(16.dp))
        }
        else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                // Summary Section
                StatSection(
                    title = "Summary",
                    stats = listOf(
                        StatData("Total Expenses", totalExpensesAmount, Color.Red, "arrow.down.circle.fill"),
                        StatData("Total Incomes", totalIncomesAmount, Color.Green, "arrow.up.circle.fill"),
                        StatData("Net Balance", netBalanceAmount, if (netBalanceAmount < 0) Color.Red else Color.Green, "equal.circle.fill")
                    ),
                    currency = selectedCurrency
                )

                // Progress bar for Expenses vs Incomes
                ProgressBarSection(totalExpensesAmount, totalIncomesAmount, selectedCurrency)

                // Detailed Insights Section
                DetailedInsightsSection(expenses, incomes, totalExpensesAmount, totalIncomesAmount, selectedCurrency)

                // Additional Statistics Section
                AdditionalStatisticsSection(expenses, incomes, selectedCurrency)

                // Charts and Graphs Section
                ChartsAndGraphsSection(expenses, categories, selectedCurrency)
            }
        }
    }
}

@Composable
fun StatSection(title: String, stats: List<StatData>, currency: String) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(title, style = MaterialTheme.typography.h6, modifier = Modifier.padding(start = 16.dp))
        LazyRow(
            modifier = Modifier.padding(horizontal = 16.dp)
        ) {
            items(stats) { stat ->
                StatCard(stat, currency)
            }
        }
    }
}

@Composable
fun StatCard(stat: StatData, currency: String) {
    Card(
        modifier = Modifier
            .padding(8.dp)
            .size(160.dp)
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.padding(16.dp)
        ) {
            Icon(imageVector = Icons.Filled.ArrowUpward, contentDescription = stat.icon, tint = stat.color)
            Text(stat.title, style = MaterialTheme.typography.body2, color = MaterialTheme.colors.primary)
            Text("$currency ${stat.value}", style = MaterialTheme.typography.h6, color = stat.color)
        }
    }
}

@Composable
fun ProgressBarSection(totalExpensesAmount: Double, totalIncomesAmount: Double, currency: String) {
    Column(modifier = Modifier.padding(vertical = 16.dp)) {
        Text("Expenses vs Incomes", style = MaterialTheme.typography.h6)
        LinearProgressIndicator(
            progress = (totalExpensesAmount / totalIncomesAmount).toFloat(),
            modifier = Modifier.fillMaxWidth().height(10.dp),
            color = Color.Red
        )
        Text("$currency $totalExpensesAmount / $currency $totalIncomesAmount")
    }
}

@Composable
fun DetailedInsightsSection(
    expensesViewModel: List<Expense>,
    incomesViewModel: List<Income>,
    totalExpensesAmount: Double,
    totalIncomesAmount: Double,
    currency: String
) {
    Column {
        Text("Detailed Insights", style = MaterialTheme.typography.h6)
        Row(
            horizontalArrangement = Arrangement.SpaceEvenly,
            modifier = Modifier.fillMaxWidth()
        ) {
            ProgressCard("Average Expenses", totalExpensesAmount / maxOf(1, expensesViewModel.size), totalExpensesAmount, Color.Red, currency)
            ProgressCard("Average Income", totalIncomesAmount / maxOf(1, incomesViewModel.size), totalIncomesAmount, Color.Green, currency)
        }
    }
}

@Composable
fun ProgressCard(
    title: String,
    value: Double,
    total: Double,
    color: Color,
    currency: String
) {
    Column(
        modifier = Modifier
            .width(120.dp)
            .padding()
            .wrapContentHeight()
    ) {
        CircleProgressBar(amount = value, totalAmount = total, color = color)

        Text(
            text = title,
            fontSize = 14.sp,
            color = color,
            modifier = Modifier.padding(top = 8.dp)
        )

        Text(
            text = "$currency %.2f".format(value),
            fontSize = 12.sp,
            color = color,
            modifier = Modifier.padding(top = 4.dp)
        )
    }
}

@Composable
fun CircleProgressBar(
    amount: Double,
    totalAmount: Double,
    color: Color
) {
    val progress = if (totalAmount > 0) amount / totalAmount else 0.0

    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .size(100.dp)
            .clip(CircleShape)
            .background(Color.Gray.copy(alpha = 0.2f))
    ) {
        CircularProgressIndicator(
            progress = progress.toFloat(),
            color = color,
            strokeWidth = 10.dp
        )
    }
}


@Composable
fun AdditionalStatisticsSection(
    expensesViewModel: List<Expense>,
    incomesViewModel: List<Income>,
    currency: String
) {
    Column {
        Text("Additional Statistics", style = MaterialTheme.typography.h6)
        Row(
            horizontalArrangement = Arrangement.SpaceEvenly,
            modifier = Modifier.fillMaxWidth()
        ) {
            ProgressCard("Highest Expense", expensesViewModel.maxOfOrNull { it.amount } ?: 0.0, expensesViewModel.sumOf { it.amount }, Color.Red, currency)
            ProgressCard("Highest Income", incomesViewModel.maxOfOrNull { it.amount } ?: 0.0, incomesViewModel.sumOf { it.amount }, Color.Green, currency)
        }
    }
}

@Composable
fun ChartsAndGraphsSection(expensesViewModel: List<Expense>, categories: List<Category>, currency: String) {
    val groupedExpenses = groupExpensesByCategory(expensesViewModel, categories)
    Column {
        Text("Expense Distribution", style = MaterialTheme.typography.h6)
        // Example Pie chart and Bar chart (use a library or custom implementation)
        PieChartView(groupedExpenses)
        BarChartView(groupedExpenses)
    }
}


@Composable
fun PieChartView(groupedExpenses: List<Pair<String, Double>>) {
    val context = LocalContext.current
    AndroidView(
        factory = {
            PieChart(context).apply {
                setUsePercentValues(true)
                description.isEnabled = false
                setExtraOffsets(5f, 10f, 5f, 5f)
                setDrawHoleEnabled(true)
                isRotationEnabled = true
                isHighlightPerTapEnabled = true
                setEntryLabelColor(Color.White.toArgb())

                // Prepare the data
                val entries = groupedExpenses.map { PieEntry(it.second.toFloat(), it.first) }
                val dataSet = PieDataSet(entries, "Expenses by Category")
                dataSet.colors = listOf(Color.Red.toArgb(), Color.Green.toArgb(), Color.Blue.toArgb(), Color.Yellow.toArgb())
                val pieData = PieData(dataSet)
                data = pieData
            }
        },
        modifier = Modifier.fillMaxWidth().height(300.dp)
    )
}

@Composable
fun BarChartView(groupedExpenses: List<Pair<String, Double>>) {
    val context = LocalContext.current
    AndroidView(
        factory = {
            BarChart(context).apply {
                description.isEnabled = false
                setMaxVisibleValueCount(60)
                setPinchZoom(false)
                setDrawBarShadow(false)
                setDrawValueAboveBar(true)
                isHighlightPerTapEnabled = false

                // Prepare the data
                val entries = groupedExpenses.mapIndexed { index, pair ->
                    BarEntry(index.toFloat(), pair.second.toFloat())
                }
                val dataSet = BarDataSet(entries, "Expense by Category")
                dataSet.colors = listOf(Color.Red.toArgb(), Color.Green.toArgb(), Color.Blue.toArgb(), Color.Yellow.toArgb())
                val barData = BarData(dataSet)
                data = barData

                // Customize the legend
                legend.form = Legend.LegendForm.SQUARE
                legend.textColor = Color.Black.toArgb()
            }
        },
        modifier = Modifier.fillMaxWidth().height(300.dp)
    )
}


fun groupExpensesByCategory(expensesViewModel: List<Expense>, categories: List<Category>): List<Pair<String, Double>> {
    return expensesViewModel
        .groupBy { expense -> categories.firstOrNull { it._id == expense.category }?.name ?: "Unknown" }
        .map { it.key to it.value.sumOf { expense -> expense.amount } }
        .sortedByDescending { it.second }
}


data class StatData(val title: String, val value: Double, val color: Color, val icon: String)

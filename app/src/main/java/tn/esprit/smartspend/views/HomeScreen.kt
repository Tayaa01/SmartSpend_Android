package tn.esprit.smartspend.views

import android.graphics.Typeface
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Paint
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import androidx.navigation.NavHostController
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import tn.esprit.smartspend.R
import tn.esprit.smartspend.model.Category
import tn.esprit.smartspend.model.Expense
import tn.esprit.smartspend.model.Income
import tn.esprit.smartspend.model.Item
import tn.esprit.smartspend.network.RetrofitInstance
import tn.esprit.smartspend.ui.theme.*
import tn.esprit.smartspend.utils.TranslationManager
import kotlin.math.absoluteValue

@Composable
fun HomeScreen(
    token: String,
    navController: NavHostController,
    onViewAllExpensesClick: (List<Expense>) -> Unit,
    onViewAllIncomesClick: (List<Income>) -> Unit,
    onAddItemClick: () -> Unit,
) {
    var expenses by remember { mutableStateOf<List<Expense>>(emptyList()) }
    var incomes by remember { mutableStateOf<List<Income>>(emptyList()) }
    var categories by remember { mutableStateOf<List<Category>>(emptyList()) }
    var totalIncome by remember { mutableStateOf(0.0) }
    var totalExpenses by remember { mutableStateOf(0.0) }
    var isLoading by remember { mutableStateOf(true) }
    var isExpanded by remember { mutableStateOf(false) } // Track expanded state

    val scope = rememberCoroutineScope()

    LaunchedEffect(Unit) {
        scope.launch {
            try {
                isLoading = true
                categories = fetchCategories()
                val (fetchedExpenses, fetchedIncomes) = fetchRecentTransactions(token)
                expenses = fetchedExpenses
                incomes = fetchedIncomes
                totalIncome = incomes.sumOf { it.amount }
                totalExpenses = expenses.sumOf { it.amount }
            } catch (e: Exception) {
                Log.e("HomeScreen", "Error fetching data: ${e.message}")
            } finally {
                isLoading = false
            }
        }
    }

    Surface(modifier = Modifier.fillMaxSize()) {
        Box(modifier = Modifier.fillMaxSize()) {
            // Top Bar Image with Balance Card above it
            Box(modifier = Modifier.fillMaxWidth()) {
                Image(
                    painter = painterResource(id = R.drawable.ic_topbar),
                    contentDescription = null,
                    modifier = Modifier.fillMaxWidth(),
                    contentScale = ContentScale.Fit
                )

                // Balance Card slightly down from the top bar
                BalanceCard(totalIncome, totalExpenses, modifier = Modifier.offset(y = 110.dp))
            }

            // Scrollable Content (Expenses, Incomes, etc.)
            Box(modifier = Modifier.fillMaxSize()) {
                if (isLoading) {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                } else {
                    LazyColumn(modifier = Modifier.padding(top = 330.dp)) {
                        item {
                            // Spending Progress Bar
                            SpendingProgressBar(
                                totalIncome = totalIncome,
                                totalExpenses = totalExpenses,
                                expenses = expenses,
                                categories = categories, // Pass categories here
                                isExpanded = isExpanded,
                                onToggleExpand = { isExpanded = !isExpanded }
                            )

                            // Recent Expenses Section
                            SectionWithItems(
                                title = TranslationManager.getTranslation("recent_expenses"),
                                items = expenses.take(3).map {
                                    val categoryName = resolveCategoryName(it.category, categories)
                                    val iconRes = resolveCategoryIcon(it.category, categories)
                                    Item(
                                        description = it.description, // Expense description
                                        iconRes = iconRes,           // Icon for the category
                                        amount = -it.amount,         // Negative for expenses
                                        date = it.date,               // Original date string
                                        hasBillDetails = it.billDetails.isNotEmpty() // Add this property
                                    )
                                },
                                onViewAllClick = { onViewAllExpensesClick(expenses) }
                            )

                        }

                        item {
                            SectionWithItems(
                                title = TranslationManager.getTranslation("recent_incomes"),
                                items = incomes.take(3).map {
                                    val categoryName = resolveCategoryName(it.category, categories)
                                    val iconRes = resolveCategoryIcon(it.category, categories)
                                    Item(
                                        description = it.description, // Income description
                                        iconRes = iconRes,           // Icon for the category
                                        amount = it.amount,          // Positive for incomes
                                        date = it.date               // Original date string
                                    )
                                },
                                onViewAllClick = { onViewAllIncomesClick(incomes) }
                            )

                        }
                    }
                }

                // Floating Action Button
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.BottomEnd
                ) {
                    FloatingActionButton(
                        onClick = onAddItemClick,
                        modifier = Modifier
                            .padding(16.dp)
                            .size(56.dp),
                        containerColor = Navy
                    ) {
                        Icon(imageVector = Icons.Default.Add, contentDescription = "Add", tint = Sand)
                    }
                }
            }
        }
    }
}

@Composable
fun BalanceCard(totalIncome: Double, totalExpenses: Double, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .height(200.dp)
            .padding(16.dp),
        colors = CardDefaults.cardColors(containerColor = Navy),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(8.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.SpaceEvenly,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "${TranslationManager.getTranslation("balance")}: $${String.format("%.2f", totalIncome - totalExpenses)}",
                color = Sand,
                fontSize = 26.sp,
                fontWeight = FontWeight.Bold
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(text = TranslationManager.getTranslation("expenses"), color = DarkOrangeRed3, fontSize = 18.sp, fontWeight = FontWeight.Normal)
                    Text(
                        text = "$${String.format("%.2f", totalExpenses)}",
                        color = DarkOrangeRed3,
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp
                    )
                }
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(text = TranslationManager.getTranslation("income"), color = Teal, fontSize = 18.sp, fontWeight = FontWeight.Normal)
                    Text(
                        text = "$${String.format("%.2f", totalIncome)}",
                        color = Teal,
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp
                    )
                }
            }
        }
    }
}

@Composable
fun SectionWithItems(
    title: String,
    items: List<Item>,
    onViewAllClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 2.dp
        ),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = title,
                    color = Navy,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )
                TextButton(
                    onClick = onViewAllClick,
                    colors = ButtonDefaults.textButtonColors(
                        contentColor = SupportingColor
                    )
                ) {
                    Text(
                        text = TranslationManager.getTranslation("view_all"),
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            items.forEach { item ->
                TransactionItem(item = item)
                if (item != items.last()) {
                    Divider(
                        modifier = Modifier.padding(vertical = 8.dp),
                        color = Color.Gray.copy(alpha = 0.2f)
                    )
                }
            }
        }
    }
}

@Composable
private fun TransactionItem(item: Item) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Category Icon with Background
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(Navy.copy(alpha = 0.1f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                painter = painterResource(id = item.iconRes),
                contentDescription = null,
                modifier = Modifier.size(24.dp),
                tint = Color.Unspecified
            )
        }

        Spacer(modifier = Modifier.width(12.dp))

        // Description and Date
        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = item.description,
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium,
                color = Color.Black
            )
            Text(
                text = formatDate(item.date),
                fontSize = 12.sp,
                color = Color.Gray
            )
        }

        // Bill Details Icon if present
        if (item.hasBillDetails) {
            Icon(
                painter = painterResource(id = R.drawable.ic_receipt),
                contentDescription = "Has Bill Details",
                modifier = Modifier
                    .size(24.dp)  // Increased from 16.dp
                    .padding(end = 8.dp),
                tint = Navy.copy(alpha = 0.8f)  // Increased opacity for better visibility
            )
        }

        // Amount
        val amountColor = if (item.amount >= 0) Teal else DarkOrangeRed3
        val amountText = if (item.amount >= 0) {
            "+$${String.format("%.2f", item.amount)}"
        } else {
            "-$${String.format("%.2f", item.amount.absoluteValue)}"
        }
        Text(
            text = amountText,
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            color = amountColor
        )
    }
}

// Helper function for date formatting remains the same
fun formatDate(date: String): String {
    return try {
        val inputFormat = java.text.SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", java.util.Locale.getDefault())
        val outputFormat = java.text.SimpleDateFormat("dd/MM/yyyy", java.util.Locale.getDefault())
        val parsedDate = inputFormat.parse(date)
        outputFormat.format(parsedDate ?: date)
    } catch (e: Exception) {
        date // Return original date if parsing fails
    }
}

@Composable
fun SpendingProgressBar(
    totalIncome: Double,
    totalExpenses: Double,
    expenses: List<Expense>,
    categories: List<Category>,
    isExpanded: Boolean,
    onToggleExpand: () -> Unit
) {
    val progress = if (totalIncome > 0) (totalExpenses / totalIncome).toFloat() else 1f
    val cappedProgress = progress.coerceAtMost(1f)
    val mainProgressColor = if (progress > 1f) listOf(CustomRed.copy(alpha = 0.2f), CustomRed) else listOf(Teal, MostImportantColor)

    val gradient = Brush.horizontalGradient(colors = mainProgressColor)

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 16.dp)
            .clickable { onToggleExpand() }, // Toggle expand on click
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Header with Icon
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = TranslationManager.getTranslation("spending_overview"),
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = MostImportantColor
            )
            Icon(
                painter = painterResource(id = R.drawable.ic_trending_up), // Example modern icon
                contentDescription = null,
                modifier = Modifier.size(24.dp),
                tint = MostImportantColor
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Modernized Progress Bar
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(28.dp)
                .clip(RoundedCornerShape(50.dp)) // Fully rounded edges
                .background(Color.Gray.copy(alpha = 0.2f))
        ) {
            Box(
                modifier = Modifier
                    .fillMaxHeight()
                    .fillMaxWidth(cappedProgress)
                    .clip(RoundedCornerShape(50.dp))
                    .background(gradient)
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Percentage and Warning
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "${TranslationManager.getTranslation("spent")}: ${String.format("%.1f", progress * 100)}%",
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold,
                color = if (progress > 1f) CustomRed else Teal
            )
            if (progress > 1f) {
                Text(
                    text = TranslationManager.getTranslation("exceeding_income"),
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = CustomRed
                )
            }
        }

        // Group expenses by category and show additional bars for each category when expanded
        if (isExpanded) {
            val expensesByCategory = expenses.groupBy { it.category }
            expensesByCategory.forEach { (categoryId, categoryExpenses) ->
                val categoryTotal = categoryExpenses.sumOf { it.amount }
                val categoryProgress = (categoryTotal / totalIncome).toFloat()
                val categoryPercentage = (categoryTotal / totalIncome) * 100
                val categoryName = resolveCategoryName(categoryId, categories)
                val categoryColor = getProgressColor(categoryProgress)

                Spacer(modifier = Modifier.height(8.dp))

                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.Start
                ) {
                    Text(
                        text = "$categoryName: ${String.format("%.1f", categoryPercentage)}%",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color.Black
                    )
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(20.dp)
                            .clip(RoundedCornerShape(50.dp))
                            .background(Color.Gray.copy(alpha = 0.2f))
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxHeight()
                                .fillMaxWidth(categoryProgress)
                                .clip(RoundedCornerShape(50.dp))
                                .background(Brush.horizontalGradient(colors = categoryColor))
                        )
                    }
                }
            }
        }
    }
}

fun getProgressColor(progress: Float): List<Color> {
    return when {
        progress < 0.33f -> listOf(Color(0xFF00FF00), Color(0xFF00FF00).copy(alpha = 0.2f)) // Brighter green
        progress < 0.66f -> listOf(Color(0xFFFFA500), Color(0xFFFFA500).copy(alpha = 0.2f)) // Brighter orange
        else -> listOf(CustomRed, CustomRed.copy(alpha = 0.2f))
    }
}

fun resolveCategoryName(categoryId: String, categories: List<Category>): String {
    return categories.find { it._id == categoryId }?.name 
        ?: TranslationManager.getTranslation("unknown")
}

fun resolveCategoryIcon(categoryId: String, categories: List<Category>): Int {
    val categoryName = resolveCategoryName(categoryId, categories)
    return when (categoryName) {
        "Groceries" -> R.drawable.groceriesnav
        "Entertainment" -> R.drawable.movienav
        "Healthcare" -> R.drawable.healthnav
        "Housing" -> R.drawable.housenav
        "Transportation" -> R.drawable.carnav
        "Utilities" -> R.drawable.othernav
        "Salary" -> R.drawable.cashnav
        else -> R.drawable.shopping_bag_4715368
    }
}

suspend fun fetchCategories(): List<Category> {
    return withContext(Dispatchers.IO) {
        try {
            val response = RetrofitInstance.api.getCategories().execute()
            if (response.isSuccessful) {
                response.body() ?: emptyList()
            } else {
                emptyList()
            }
        } catch (e: Exception) {
            Log.e("HomeScreen", "Error fetching categories: ${e.message}")
            emptyList()
        }
    }
}

suspend fun fetchRecentTransactions(token: String): Pair<List<Expense>, List<Income>> {
    return withContext(Dispatchers.IO) {
        try {
            val incomesResponse = RetrofitInstance.api.getIncomes(token).execute()
            val expensesResponse = RetrofitInstance.api.getExpenses(token).execute()

            val incomes = if (incomesResponse.isSuccessful) incomesResponse.body() ?: emptyList() else emptyList()
            val expenses = if (expensesResponse.isSuccessful) expensesResponse.body() ?: emptyList() else emptyList()

            Pair(expenses, incomes)
        } catch (e: Exception) {
            Log.e("HomeScreen", "Error fetching transactions: ${e.message}")
            Pair(emptyList(), emptyList())
        }
    }
}
package tn.esprit.smartspend.views

import android.util.Log
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import tn.esprit.smartspend.R
import tn.esprit.smartspend.model.Category
import tn.esprit.smartspend.model.Expense
import tn.esprit.smartspend.model.Income
import tn.esprit.smartspend.network.RetrofitInstance

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

    // Fetch data (expenses, incomes, and categories) on launch
    val scope = rememberCoroutineScope()

    LaunchedEffect(Unit) {
        scope.launch {
            try {
                isLoading = true
                // Fetch categories
                categories = fetchCategories()

                // Fetch recent transactions
                val (fetchedExpenses, fetchedIncomes) = fetchRecentTransactions(token)
                expenses = fetchedExpenses
                incomes = fetchedIncomes

                // Calculate totals for income and expenses
                totalIncome = incomes.sumOf { it.amount }
                totalExpenses = expenses.sumOf { it.amount }
            } catch (e: Exception) {
                Log.e("HomeScreen", "Error fetching data: ${e.message}")
            } finally {
                isLoading = false
            }
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        if (isLoading) {
            CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
        } else {
            LazyColumn(modifier = Modifier.padding(16.dp)) {
                // Balance Card Item
                item {
                    BalanceCard(totalIncome, totalExpenses)
                }

                // Recent Expenses Section
                item {
                    SectionWithItems(
                        title = "Recent Expenses",
                        items = expenses.take(3).map {
                            val categoryName = resolveCategoryName(it.category, categories)
                            val icon = resolveCategoryIcon(it.category, categories)
                            "${it.description}: $${it.amount} ($categoryName)" to icon
                        },
                        onViewAllClick = { onViewAllExpensesClick(expenses) }
                    )
                }

                // Recent Incomes Section
                item {
                    SectionWithItems(
                        title = "Recent Incomes",
                        items = incomes.take(3).map {
                            val categoryName = resolveCategoryName(it.category, categories)
                            val icon = resolveCategoryIcon(it.category, categories)
                            "${it.description}: $${it.amount} ($categoryName)" to icon
                        },
                        onViewAllClick = { onViewAllIncomesClick(incomes) }
                    )
                }
            }
        }

        // Floating Action Button for Adding Expense
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.BottomEnd
        ) {
            FloatingActionButton(
                onClick = onAddItemClick,
                modifier = Modifier
                    .padding(16.dp)
                    .size(56.dp),
                containerColor = Color(0xFF9575CD)
            ) {
                Icon(imageVector = Icons.Default.Add, contentDescription = "Add", tint = Color.White)
            }
        }
    }
}

@Composable
fun BalanceCard(totalIncome: Double, totalExpenses: Double) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(200.dp)
            .padding(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF9575CD)),
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
                text = "Balance: $${String.format("%.2f", totalIncome - totalExpenses)}",
                color = Color.White,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(text = "Expenses", color = Color.White, fontSize = 16.sp)
                    Text(
                        text = "$${String.format("%.2f", totalExpenses)}",
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp
                    )
                }
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(text = "Income", color = Color.White, fontSize = 16.sp)
                    Text(
                        text = "$${String.format("%.2f", totalIncome)}",
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp
                    )
                }
            }
        }
    }
}

@Composable
fun SectionWithItems(
    title: String,
    items: List<Pair<String, Int>>, // Pair of item description and drawable resource ID
    onViewAllClick: () -> Unit
) {
    Column(modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = title,
                color = Color.Black,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "View All",
                color = Color(0xFF9575CD),
                fontSize = 16.sp,
                modifier = Modifier.clickable { onViewAllClick() }
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        items.forEach { (description, iconRes) ->
            Row(
                modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    painter = painterResource(id = iconRes),
                    contentDescription = null,
                    modifier = Modifier.size(24.dp),
                    tint = Color.Unspecified
                )

                Spacer(modifier = Modifier.width(8.dp))

                Text(text = description, fontSize = 16.sp)
            }
        }
    }
}

fun resolveCategoryName(categoryId: String, categories: List<Category>): String {
    return categories.find { it._id == categoryId }?.name ?: "Unknown"
}

fun resolveCategoryIcon(categoryId: String, categories: List<Category>): Int {
    val categoryName = resolveCategoryName(categoryId, categories)
    return when (categoryName) {
        "Groceries" -> R.drawable.vegetable
        "Entertainment" -> R.drawable.game_controller
        "Healthcare" -> R.drawable.healthcare
        "Housing" -> R.drawable.home
        "Transportation" -> R.drawable.car
        "Utilities" -> R.drawable.other
        "Salary" -> R.drawable.dollar
        else -> R.drawable.dollar
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

package tn.esprit.smartspend

import TimelineView
import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import tn.esprit.smartspend.model.Category
import tn.esprit.smartspend.model.Expense
import tn.esprit.smartspend.model.Income
import tn.esprit.smartspend.network.RetrofitInstance
import tn.esprit.smartspend.ui.theme.Navy
import tn.esprit.smartspend.ui.theme.Sand
import tn.esprit.smartspend.ui.theme.SmartSpendTheme
import tn.esprit.smartspend.utils.SharedPrefsManager
import tn.esprit.smartspend.views.*

@OptIn(ExperimentalMaterial3Api::class)
class HomeActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            SmartSpendTheme {
                MainScreen(context = this@HomeActivity)
            }
        }
    }
}

@Composable
fun MainScreen(context: Context) {
    val navController = rememberNavController()

    val sharedPrefsManager = SharedPrefsManager(context)
    val token: String? = sharedPrefsManager.getToken()

    var categories by remember { mutableStateOf<List<Category>>(emptyList()) }

    LaunchedEffect(Unit) {
        categories = fetchCategories()
    }

    Scaffold(
        bottomBar = { BottomNavigationBar(navController) }
    ) { innerPadding ->
        NavigationGraph(
            navController = navController,
            modifier = Modifier.padding(innerPadding),
            token = token,
            categories = categories
        )
    }
}

@Composable
fun BottomNavigationBar(navController: NavHostController) {
    val items = listOf(
        BottomNavItem.Home,
        BottomNavItem.Timeline,
        BottomNavItem.Analytics,
        BottomNavItem.Profile
    )

    NavigationBar(containerColor = Navy) {
        items.forEach { item ->
            NavigationBarItem(
                icon = {
                    Icon(
                        imageVector = item.icon,
                        contentDescription = item.title,
                        tint = Sand
                    )
                },
                label = { Text(text = item.title, color = Sand) },
                selected = false,
                onClick = {
                    navController.navigate(item.route) {
                        popUpTo(navController.graph.startDestinationId) { saveState = true }
                        launchSingleTop = true
                        restoreState = true
                    }
                }
            )
        }
    }
}

@Composable
fun NavigationGraph(
    navController: NavHostController,
    modifier: Modifier = Modifier,
    token: String?,
    categories: List<Category>
) {
    NavHost(navController, startDestination = BottomNavItem.Home.route, modifier = modifier) {
        composable(BottomNavItem.Home.route) {
            HomeScreen(
                token = token ?: "",
                navController = navController,
                onAddItemClick = {
                    if (token != null) {
                        navController.navigate("addTransaction/$token")
                    }
                },
                onViewAllExpensesClick = { expenses ->
                    val expensesJson = Gson().toJson(expenses)
                    navController.navigate("expensesView/$expensesJson")
                },
                onViewAllIncomesClick = { incomes ->
                    val incomesJson = Gson().toJson(incomes)
                    navController.navigate("incomesView/$incomesJson")
                }
            )
        }
        composable(BottomNavItem.Timeline.route) { TimelineView() }
        composable(BottomNavItem.Analytics.route) { AnalyticsView() }
        composable(BottomNavItem.Profile.route) { ProfileView() }

        composable("expensesView/{expensesJson}") { backStackEntry ->
            val expensesJson = backStackEntry.arguments?.getString("expensesJson") ?: "[]"
            val expenses = Gson().fromJson(expensesJson, Array<Expense>::class.java).toList()
            ExpensesView(
                expenses = expenses,
                categories = categories,
                onExpenseClick = { expense ->
                    Log.d("ExpenseClick", "Clicked expense: $expense")
                    // You can add navigation or other actions here
                }
            )
        }

        composable("incomesView/{incomesJson}") { backStackEntry ->
            val incomesJson = backStackEntry.arguments?.getString("incomesJson") ?: "[]"
            val incomes = Gson().fromJson(incomesJson, Array<Income>::class.java).toList()
            IncomesView(
                incomes = incomes,
                categories = categories,
                onIncomeClick = { income ->
                    Log.d("IncomeClick", "Clicked income: $income")
                    // You can add navigation or other actions here
                }
            )
        }

        composable("addTransaction/{token}") { backStackEntry ->
            val token = backStackEntry.arguments?.getString("token")
            if (token != null) {
                AddTransactionScreen(
                    onSaveTransaction = { expense ->
                        Log.d("AddTransaction", "Expense saved: $expense")
                    },
                    token = token,
                    navController = navController
                )
            } else {
                Log.d("AddTransaction", "Token is missing")
            }
        }
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

data class BottomNavItem(val route: String, val icon: androidx.compose.ui.graphics.vector.ImageVector, val title: String) {
    companion object {
        val Home = BottomNavItem("home", Icons.Default.Home, "Home")
        val Timeline = BottomNavItem("timeline", Icons.Default.DateRange, "Timeline")
        val Analytics = BottomNavItem("analytics", Icons.Default.Menu, "Analytics")
        val Profile = BottomNavItem("profile", Icons.Default.Person, "Profile")
    }
}
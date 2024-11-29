package tn.esprit.smartspend

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.google.gson.Gson
import tn.esprit.smartspend.model.Expense
import tn.esprit.smartspend.ui.theme.SmartSpendTheme
import tn.esprit.smartspend.views.*

@OptIn(ExperimentalMaterial3Api::class)
class HomeActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            SmartSpendTheme {
                MainScreen()
            }
        }
    }
}

@Composable
fun MainScreen() {
    val navController = rememberNavController()

    Scaffold(
        bottomBar = { BottomNavigationBar(navController) }
    ) { innerPadding ->
        NavigationGraph(
            navController = navController,
            modifier = Modifier.padding(innerPadding)
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

    NavigationBar(containerColor = Color(0xFF9575CD)) {
        items.forEach { item ->
            NavigationBarItem(
                icon = {
                    Icon(
                        imageVector = item.icon,
                        contentDescription = item.title,
                        tint = Color.White
                    )
                },
                label = { Text(text = item.title, color = Color.White) },
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
fun NavigationGraph(navController: NavHostController, modifier: Modifier = Modifier) {
    NavHost(navController, startDestination = BottomNavItem.Home.route, modifier = modifier) {
        composable(BottomNavItem.Home.route) {
            HomeScreen(
                onAddItemClick = { /* Handle Add Expense */ },
                onViewAllExpensesClick = { expenses ->
                    val expensesJson = Gson().toJson(expenses)
                    navController.navigate("expensesView/$expensesJson")
                }
            )
        }
        composable(BottomNavItem.Timeline.route) { TimelineView() }
        composable(BottomNavItem.Analytics.route) { AnalyticsView() }
        composable(BottomNavItem.Profile.route) { ProfileView() }

        composable("expensesView/{expensesJson}") { backStackEntry ->
            val expensesJson = backStackEntry.arguments?.getString("expensesJson") ?: "[]"
            val expenses = Gson().fromJson(expensesJson, Array<Expense>::class.java).toList()
            ExpensesView(expenses)
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

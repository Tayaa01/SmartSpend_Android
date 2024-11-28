package tn.esprit.smartspend

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import tn.esprit.smartspend.model.Expense
import tn.esprit.smartspend.network.ApiService
import tn.esprit.smartspend.network.RetrofitInstance
import tn.esprit.smartspend.ui.theme.SmartSpendTheme
import tn.esprit.smartspend.utils.SharedPrefsManager

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
                onAddItemClick = { /* Handle Add Expense action */ }
            )
        }
        composable(BottomNavItem.Timeline.route) { TimelineView() }
        composable(BottomNavItem.Analytics.route) { AnalyticsView() }
        composable(BottomNavItem.Profile.route) { ProfileView() }
    }
}

@Composable
fun HomeScreen(onAddItemClick: () -> Unit) {
    val sharedPrefsManager = SharedPrefsManager(LocalContext.current)
    val token = sharedPrefsManager.getToken() ?: return
    var expenses by remember { mutableStateOf<List<Expense>>(emptyList()) }
    var isViewAll by remember { mutableStateOf(false) }

    // Fetch expenses in a LaunchedEffect
    LaunchedEffect(token) {
        fetchExpenses(token)?.let {
            expenses = it
        }
    }

    val purple = Color(0xFF9575CD)
    val white = Color.White

    // LazyColumn for the list of expenses
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(white)
    ) {
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .padding(16.dp),
                colors = CardDefaults.cardColors(containerColor = purple),
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
                        text = "Balance: $1850",
                        color = white,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(text = "Expenses", color = white, fontSize = 16.sp)
                            Text(text = "$150", color = white, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                        }
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(text = "Income", color = white, fontSize = 16.sp)
                            Text(text = "$2000", color = white, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                        }
                    }
                }
            }
        }

        // Expenses Section (Recent or Full based on isViewAll)
        item {
            SectionWithItems(
                title = "Recent Expenses",
                items = if (isViewAll) {
                    expenses.map { "${it.description}: ${it.amount}" }
                } else {
                    expenses.take(3).map { "${it.description}: ${it.amount}" }
                },
                onViewAllClick = {
                    isViewAll = true
                }
            )
        }
    }

    // Floating Action Button for adding new item
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.BottomEnd
    ) {
        FloatingActionButton(
            onClick = onAddItemClick,
            modifier = Modifier.padding(16.dp).size(56.dp),
            containerColor = purple
        ) {
            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = "Add",
                tint = white
            )
        }
    }
}

@Composable
fun SectionWithItems(title: String, items: List<String>, onViewAllClick: () -> Unit) {
    Column(modifier = Modifier.fillMaxWidth().padding(16.dp)) {
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
        items.forEach { item ->
            Text(
                text = item,
                color = Color.Gray,
                fontSize = 16.sp,
                modifier = Modifier.padding(vertical = 4.dp)
            )
        }
    }
}

suspend fun fetchExpenses(token: String): List<Expense>? {
    return try {
        Log.d("HomeScreen", "Fetching expenses with token: $token")

        // Ensure the network call is done on the IO thread
        val response = withContext(Dispatchers.IO) {
            RetrofitInstance.api.getExpenses(token).execute()
        }

        if (response.isSuccessful) {
            Log.d("HomeScreen", "Expenses fetched successfully: ${response.body()}")
            response.body()
        } else {
            Log.e("HomeScreen", "Error fetching expenses: ${response.errorBody()}")
            null
        }
    } catch (e: Exception) {
        Log.e("HomeScreen", "Exception: ${e.message}", e)
        null
    }
}

@Composable
fun TimelineView() {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text(text = "Timeline View", fontSize = 20.sp, fontWeight = FontWeight.Bold)
    }
}

@Composable
fun AnalyticsView() {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text(text = "Analytics View", fontSize = 20.sp, fontWeight = FontWeight.Bold)
    }
}

@Composable
fun ProfileView() {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text(text = "Profile View", fontSize = 20.sp, fontWeight = FontWeight.Bold)
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

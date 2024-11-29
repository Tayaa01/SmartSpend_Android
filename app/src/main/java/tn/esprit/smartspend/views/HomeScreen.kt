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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import tn.esprit.smartspend.model.Expense
import tn.esprit.smartspend.network.RetrofitInstance
import tn.esprit.smartspend.utils.SharedPrefsManager

@Composable
fun HomeScreen(onAddItemClick: () -> Unit, onViewAllExpensesClick: (List<Expense>) -> Unit) {
    val sharedPrefsManager = SharedPrefsManager(LocalContext.current)
    val token = sharedPrefsManager.getToken() ?: return
    var expenses by remember { mutableStateOf<List<Expense>>(emptyList()) }

    // Fetch expenses when the token is available
    LaunchedEffect(token) {
        fetchExpenses(token)?.let { expenses = it }
    }

    val purple = Color(0xFF9575CD)
    val white = Color.White

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(white)
    ) {
        // Balance Card
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

        // Recent Expenses Section
        item {
            SectionWithItems(
                title = "Recent Expenses",
                items = expenses.take(3).map { "${it.description}: ${it.amount}" },
                onViewAllClick = { onViewAllExpensesClick(expenses) }
            )
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
            containerColor = purple
        ) {
            Icon(imageVector = Icons.Default.Add, contentDescription = "Add", tint = white)
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
            Text(text = title, color = Color.Black, fontSize = 20.sp, fontWeight = FontWeight.Bold)
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
        val response = withContext(Dispatchers.IO) {
            RetrofitInstance.api.getExpenses(token).execute()
        }
        if (response.isSuccessful) response.body() else null
    } catch (e: Exception) {
        Log.e("HomeScreen", "Error: ${e.message}", e)
        null
    }
}

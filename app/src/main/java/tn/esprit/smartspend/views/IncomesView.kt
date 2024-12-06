package tn.esprit.smartspend.views

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import tn.esprit.smartspend.model.Expense
import tn.esprit.smartspend.model.Income

@Composable
fun IncomesView(incomes: List<Income>) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        LazyColumn(modifier = Modifier.fillMaxSize()) {
            // Title Header
            item {
                Text(
                    text = "All Incomes",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF9575CD),
                    modifier = Modifier.padding(bottom = 16.dp)
                )
            }

            // List of Expenses
            items(incomes.size) { index ->
                IncomeItem(incomes = incomes[index])
            }
        }
    }
}

@Composable
fun IncomeItem(incomes: Income) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFEBDEF0)),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                horizontalAlignment = Alignment.Start,
                verticalArrangement = Arrangement.Center
            ) {
                // Expense Description
                Text(
                    text = incomes.description,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )

                // Amount Text
                Text(
                    text = "Amount: ${incomes.amount}",
                    fontSize = 16.sp,
                    color = Color.Gray
                )
            }

            // Add any additional elements to the row if necessary (e.g., date, edit button, etc.)
        }
    }
}

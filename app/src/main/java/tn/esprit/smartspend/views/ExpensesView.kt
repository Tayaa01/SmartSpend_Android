package tn.esprit.smartspend.views

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import tn.esprit.smartspend.R
import tn.esprit.smartspend.model.Category
import tn.esprit.smartspend.model.Expense
import tn.esprit.smartspend.ui.theme.Navy
import tn.esprit.smartspend.ui.theme.Red
import java.text.SimpleDateFormat
import java.util.Locale

@Composable
fun ExpensesView(expenses: List<Expense>, categories: List<Category>, onExpenseClick: (Expense) -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        LazyColumn(modifier = Modifier.fillMaxSize()) {
            // Title Header
            item {
                Text(
                    text = "All Expenses",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = Navy,
                    modifier = Modifier.padding(bottom = 16.dp)
                )
            }

            // List of Expenses
            items(expenses.size) { index ->
                ExpenseListItem(
                    expense = expenses[index],
                    categories = categories,
                    onClick = { onExpenseClick(expenses[index]) }
                )
            }
        }
    }
}

@Composable
fun ExpenseListItem(expense: Expense, categories: List<Category>, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(Color.White)
            .clickable(onClick = onClick)
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        // Icon and Description
        Row(verticalAlignment = Alignment.CenterVertically) {
            // Resolve category icon dynamically
            val iconRes = resolveCategoryIcon(expense.category, categories)

            Image(
                painter = painterResource(id = iconRes),
                contentDescription = "Expense Icon",
                modifier = Modifier
                    .size(40.dp)
                    .padding(end = 12.dp)
            )

            Column {
                Text(
                    text = expense.description,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )

                Text(
                    text = formatDate(expense.date), // Format the date to dd/MM/yyyy
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color.Gray
                )
            }
        }

        // Amount
        Text(
            text = "- $${expense.amount}",
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            color = Red,
            modifier = Modifier.padding(start = 8.dp) // Added padding before the amount
        )
    }
}




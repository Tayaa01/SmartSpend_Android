package tn.esprit.smartspend.views

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
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
import tn.esprit.smartspend.utils.TranslationManager
import java.text.SimpleDateFormat
import java.util.Locale

@Composable
fun ExpensesView(expenses: List<Expense>, categories: List<Category>, onExpenseClick: (Expense) -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF5F5F5))
    ) {
        // Modern Light Header
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            color = Color.Transparent
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = TranslationManager.getTranslation("all_expenses"),
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Bold,
                        color = Navy
                    )
                    Text(
                        text = "${expenses.size} ${TranslationManager.getTranslation("transactions")}",
                        fontSize = 14.sp,
                        color = Color.Gray,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }

            }
        }

        // List content
        LazyColumn(
            contentPadding = PaddingValues(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
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
    var isExpanded by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Icon with background
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(Navy.copy(alpha = 0.1f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        painter = painterResource(id = resolveCategoryIcon(expense.category, categories)),
                        contentDescription = null,
                        modifier = Modifier.size(28.dp),
                        tint = Color.Unspecified
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                // Description and category
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = expense.description,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(top = 4.dp)
                    ) {
                        Text(
                            text = formatDate(expense.date),
                            fontSize = 14.sp,
                            color = Color.Gray
                        )
                        if (expense.billDetails.isNotEmpty()) {
                            IconButton(
                                onClick = { isExpanded = !isExpanded },
                                modifier = Modifier.size(32.dp)
                            ) {
                                Icon(
                                    painter = painterResource(
                                        id = if (isExpanded) R.drawable.ic_arrow_up 
                                        else R.drawable.ic_receipt
                                    ),
                                    contentDescription = null,
                                    modifier = Modifier.size(20.dp),
                                    tint = Navy
                                )
                            }
                        }
                    }
                }

                // Amount
                Text(
                    text = "-$${expense.amount}",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Red
                )
            }

            // Bill Details Section with animation
            AnimatedVisibility(
                visible = isExpanded && expense.billDetails.isNotEmpty(),
                enter = expandVertically() + fadeIn(),
                exit = shrinkVertically() + fadeOut()
            ) {
                Column(
                    modifier = Modifier.padding(top = 8.dp)
                ) {
                    Divider(color = Color.Gray.copy(alpha = 0.2f))
                    Spacer(modifier = Modifier.height(8.dp))
                    expense.billDetails.forEach { detail ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = detail.description,
                                fontSize = 14.sp,
                                color = Color.DarkGray
                            )
                            Text(
                                text = "$${detail.price} × ${detail.quantity}",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Medium,
                                color = Color.DarkGray
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun CategoryIcon(categoryId: String, categories: List<Category>) {
    Box(
        modifier = Modifier
            .size(44.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(
                Brush.linearGradient(
                    colors = listOf(
                        Navy.copy(alpha = 0.1f),
                        Navy.copy(alpha = 0.05f)
                    )
                )
            ),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            painter = painterResource(id = resolveCategoryIcon(categoryId, categories)),
            contentDescription = null,
            modifier = Modifier.size(24.dp),
            tint = Color.Unspecified
        )
    }
}

@Composable
private fun BillDetailsSection(expense: Expense) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 12.dp)
    ) {
        Divider(color = Color.Gray.copy(alpha = 0.2f))
        Spacer(modifier = Modifier.height(12.dp))
        
        expense.billDetails.forEach { detail ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = detail.description,
                    fontSize = 14.sp,
                    color = Color.DarkGray
                )
                Text(
                    text = "$${detail.price} × ${detail.quantity}",
                    fontSize = 14.sp,
                    color = Color.DarkGray,
                    fontWeight = FontWeight.Medium
                )
            }
        }
        
        Divider(
            modifier = Modifier.padding(top = 8.dp),
            color = Color.Gray.copy(alpha = 0.2f)
        )
    }
}




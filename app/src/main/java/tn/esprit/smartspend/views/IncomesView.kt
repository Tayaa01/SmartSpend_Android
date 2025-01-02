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
import tn.esprit.smartspend.model.Category
import tn.esprit.smartspend.model.Income
import tn.esprit.smartspend.ui.theme.Navy
import tn.esprit.smartspend.ui.theme.Teal
import tn.esprit.smartspend.utils.TranslationManager
import java.text.SimpleDateFormat
import java.util.Locale

@Composable
fun IncomesView(incomes: List<Income>, categories: List<Category>, onIncomeClick: (Income) -> Unit) {
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
                        text = TranslationManager.getTranslation("all_incomes"),
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Bold,
                        color = Navy
                    )
                    Text(
                        text = "${incomes.size} ${TranslationManager.getTranslation("transactions")}",
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
            items(incomes.size) { index ->
                IncomeItem(
                    income = incomes[index],
                    categories = categories,
                    onClick = { onIncomeClick(incomes[index]) }
                )
            }
        }
    }
}

@Composable
fun IncomeItem(income: Income, categories: List<Category>, onClick: () -> Unit) {
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
                        painter = painterResource(id = resolveCategoryIcon(income.category, categories)),
                        contentDescription = null,
                        modifier = Modifier.size(28.dp),
                        tint = Color.Unspecified
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                // Description and category
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = income.description,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(top = 4.dp)
                    ) {
                        Text(
                            text = formatDate(income.date),
                            fontSize = 14.sp,
                            color = Color.Gray
                        )
                        Text(
                            text = resolveCategoryName(income.category, categories),
                            fontSize = 14.sp,
                            color = Navy,
                            fontWeight = FontWeight.Medium,
                            modifier = Modifier.padding(start = 8.dp)
                        )
                    }
                }

                // Amount
                Text(
                    text = "+$${income.amount}",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Teal
                )
            }
        }
    }
}
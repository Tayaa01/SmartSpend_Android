package com.example.smartspend.ui.statistics

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.BasicText
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.Equalizer
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import tn.esprit.smartspend.model.Expense
import tn.esprit.smartspend.model.Income
import kotlin.math.max


@Composable
fun AnalyticsView(
    fetchExpenses: suspend () -> List<Expense>,
    fetchIncomes: suspend () -> List<Income>
) {
    var expenses by remember { mutableStateOf<List<Expense>>(emptyList()) }
    var incomes by remember { mutableStateOf<List<Income>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(Unit) {
        try {
            isLoading = true
            errorMessage = null
            expenses = fetchExpenses()
            incomes = fetchIncomes()
        } catch (e: Exception) {
            errorMessage = "Failed to load statistics: ${e.message}"
        } finally {
            isLoading = false
        }
    }

    val totalExpenses = expenses.sumOf { it.amount }
    val totalIncomes = incomes.sumOf { it.amount }
    val netBalance = totalIncomes - totalExpenses

    Box(modifier = Modifier.fillMaxSize().background(MaterialTheme.colors.background)) {
        when {
            isLoading -> CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            errorMessage != null -> Text(
                text = errorMessage ?: "",
                color = Color.Red,
                modifier = Modifier.align(Alignment.Center)
            )
            else -> {
                Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
                    StatSection(
                        title = "Summary",
                        stats = listOf(
                            StatData("Total Expenses", totalExpenses, Color.Red, Icons.Default.ArrowDownward),
                            StatData("Total Incomes", totalIncomes, Color.Green, Icons.Default.ArrowUpward),
                            StatData(
                                "Net Balance",
                                netBalance,
                                if (netBalance < 0) Color.Red else Color.Green,
                                Icons.Default.Equalizer
                            )
                        )
                    )
                    Spacer(modifier = Modifier.height(20.dp))
                    ProgressBarSection(totalExpenses, totalIncomes)
                    Spacer(modifier = Modifier.height(20.dp))
                    DetailedInsightsSection(expenses, incomes)
                }
            }
        }
    }
}

@Composable
fun StatSection(title: String, stats: List<StatData>) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(text = title, style = MaterialTheme.typography.h6, fontWeight = FontWeight.Bold)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceAround
        ) {
            stats.forEach { stat ->
                StatCard(stat)
            }
        }
    }
}

@Composable
fun StatCard(data: StatData) {
    Column(
        modifier = Modifier
            .width(100.dp)
            .padding(8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(data.icon, contentDescription = data.title, tint = data.color, modifier = Modifier.size(40.dp))
        Spacer(modifier = Modifier.height(8.dp))
        Text(text = data.title, fontWeight = FontWeight.Bold)
        Text(text = "$${data.value}", color = data.color)
    }
}

@Composable
fun ProgressBarSection(expenses: Double, incomes: Double) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text("Expenses vs Incomes", style = MaterialTheme.typography.subtitle1)
        Spacer(modifier = Modifier.height(8.dp))
        LinearProgressIndicator(
            progress = (expenses / max(incomes, 1.0)).toFloat(),
            modifier = Modifier.fillMaxWidth(),
            color = Color.Red,
            backgroundColor = Color.Gray.copy(alpha = 0.2f)
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "${(expenses / max(incomes, 1.0) * 100).toInt()}% Spent",
            style = MaterialTheme.typography.caption,
            color = Color.Gray
        )
    }
}

@Composable
fun DetailedInsightsSection(expenses: List<Expense>, incomes: List<Income>) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text("Detailed Insights", style = MaterialTheme.typography.subtitle1)
        Row(horizontalArrangement = Arrangement.SpaceBetween) {
            ProgressCard("Average Expenses", expenses.sumOf { it.amount } / max(1, expenses.size), Color.Red)
            ProgressCard("Average Incomes", incomes.sumOf { it.amount } / max(1, incomes.size), Color.Green)
        }
    }
}

@Composable
fun ProgressCard(title: String, value: Double, color: Color) {
    Column(
        modifier = Modifier
            .width(120.dp)
            .padding(8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        CircularProgressIndicator(
            progress = (value / max(value, 1.0)).toFloat(),
            modifier = Modifier.size(80.dp),
            color = color,
            strokeWidth = 6.dp
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(title, fontWeight = FontWeight.Bold)
        Text("$${value}", color = color)
    }
}

data class StatData(
    val title: String,
    val value: Double,
    val color: Color,
    val icon: ImageVector
)

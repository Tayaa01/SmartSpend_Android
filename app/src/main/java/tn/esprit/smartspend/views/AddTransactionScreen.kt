package tn.esprit.smartspend.views

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import tn.esprit.smartspend.model.Expense

@Composable
fun AddTransactionScreen(onSaveTransaction: (Expense) -> Unit) {
    // State for form inputs
    var description by remember { mutableStateOf("") }
    var amount by remember { mutableStateOf("") }
    var date by remember { mutableStateOf("") }
    var category by remember { mutableStateOf("") }
    var isExpense by remember { mutableStateOf(true) }

    // State for managing form validation
    var isFormValid by remember { mutableStateOf(true) }

    // Handle Save Button Click
    val onSaveClick = {
        if (description.isNotBlank() && amount.isNotBlank() && date.isNotBlank() && category.isNotBlank()) {
            // Parse the amount to double
            val amountValue = amount.toDoubleOrNull()
            if (amountValue != null) {
                // Create Expense object
                val expense = Expense(
                    _id = "newId", // Placeholder ID (could be generated or fetched later)
                    amount = amountValue,
                    description = description,
                    date = date,
                    user = "userId" // Placeholder user ID (fetch or pass from the context)
                )
                onSaveTransaction(expense) // Callback to save the expense
            } else {
                isFormValid = false // Invalid amount
            }
        } else {
            isFormValid = false // Invalid form
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            // Title
            Text(
                text = "Add Transaction",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            // Radio Buttons for Expense/Income selection
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(text = "Expense", modifier = Modifier.padding(end = 8.dp))
                RadioButton(
                    selected = isExpense,
                    onClick = { isExpense = true },
                    colors = RadioButtonDefaults.colors(selectedColor = Color(0xFF9575CD))
                )
                Spacer(modifier = Modifier.width(16.dp))
                Text(text = "Income")
                RadioButton(
                    selected = !isExpense,
                    onClick = { isExpense = false },
                    colors = RadioButtonDefaults.colors(selectedColor = Color(0xFF9575CD))
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Description Input Field
            TextField(
                value = description,
                onValueChange = { description = it },
                label = { Text("Description") },
                modifier = Modifier.fillMaxWidth(),
                isError = !isFormValid
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Amount Input Field
            TextField(
                value = amount,
                onValueChange = { amount = it },
                label = { Text("Amount") },
                keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number),
                isError = !isFormValid
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Date Input Field
            TextField(
                value = date,
                onValueChange = { date = it },
                label = { Text("Date") },
                isError = !isFormValid
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Category Input Field
            TextField(
                value = category,
                onValueChange = { category = it },
                label = { Text("Category") },
                isError = !isFormValid
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Save Button
            Button(
                onClick = onSaveClick,
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF9575CD))
            ) {
                Text("Save", color = Color.White)
            }

            if (!isFormValid) {
                Text(text = "Please fill all fields correctly", color = Color.Red)
            }
        }
    }
}

package tn.esprit.smartspend.views

import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.text.SimpleDateFormat
import java.util.*
import tn.esprit.smartspend.model.Category
import tn.esprit.smartspend.model.Expense
import tn.esprit.smartspend.network.ApiService
import tn.esprit.smartspend.network.RetrofitInstance
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

@Composable
fun AddTransactionScreen(
    onSaveTransaction: (Expense) -> Unit,
    token: String // Pass the token from the parent composable
) {
    var description by rememberSaveable { mutableStateOf("") }
    var amount by rememberSaveable { mutableStateOf("") }
    var date by rememberSaveable { mutableStateOf(getCurrentDate()) }
    var category by rememberSaveable { mutableStateOf<Category?>(null) }
    var isExpense by rememberSaveable { mutableStateOf(true) }

    var categories by remember { mutableStateOf<List<Category>>(emptyList()) }
    var showCategoryDropdown by remember { mutableStateOf(false) }
    var isLoading by remember { mutableStateOf(true) }
    var isError by remember { mutableStateOf(false) }

    // Fetch categories on screen launch
    LaunchedEffect(Unit) {
        isLoading = true
        fetchCategories { fetchedCategories ->
            categories = fetchedCategories
            isLoading = false
        }
    }

    // Handle form validation
    val isFormValid = description.isNotBlank() && amount.isNotBlank() && category != null

    // Handle save action
    val onSaveClick = {
        if (isFormValid) {
            val amountValue = amount.toDoubleOrNull()
            if (amountValue != null) {
                val expense = Expense(
                    amount = amountValue,
                    description = description,
                    date = date,
                    category = category!!._id
                )
                // Pass the token when calling addExpense
                addExpense(token, expense) { success ->
                    if (success) {
                        onSaveTransaction(expense)
                    } else {
                        isError = true
                    }
                }
            } else {
                isError = true
            }
        } else {
            isError = true
        }
    }

    Box(modifier = Modifier.fillMaxSize().padding(16.dp)) {
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

            // Amount Input Field
            TextField(
                value = amount,
                onValueChange = { amount = it },
                label = { Text("Amount") },
                isError = isError,
                keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth()
            )
            if (isError && amount.isBlank()) {
                Text(text = "Amount is required", color = Color.Red, fontSize = 12.sp)
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Description Input Field
            TextField(
                value = description,
                onValueChange = { description = it },
                label = { Text("Description") },
                isError = isError,
                modifier = Modifier.fillMaxWidth()
            )
            if (isError && description.isBlank()) {
                Text(text = "Description is required", color = Color.Red, fontSize = 12.sp)
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Date Input Field (Read-Only)
            TextField(
                value = date,
                onValueChange = {},
                label = { Text("Date") },
                readOnly = true,
                isError = isError,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Category Dropdown
            Text(text = "Select Category", fontSize = 16.sp, fontWeight = FontWeight.Bold)
            TextField(
                value = category?.name ?: "",
                onValueChange = {},
                readOnly = true,
                label = { Text("Category") },
                trailingIcon = {
                    IconButton(onClick = { showCategoryDropdown = !showCategoryDropdown }) {
                        Icon(imageVector = Icons.Default.ArrowDropDown, contentDescription = null)
                    }
                },
                modifier = Modifier.fillMaxWidth()
            )

            if (showCategoryDropdown) {
                LazyColumn(modifier = Modifier.fillMaxWidth()) {
                    items(categories) { categoryItem ->
                        CategoryDropdownItem(
                            category = categoryItem,
                            onSelectCategory = {
                                category = categoryItem
                                showCategoryDropdown = false
                            }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Save Button
            Button(
                onClick = onSaveClick,
                modifier = Modifier.fillMaxWidth(),
                enabled = isFormValid,
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF9575CD))
            ) {
                Text("Save", color = Color.White)
            }

            if (isError) {
                Text(text = "Please fill in all fields correctly", color = Color.Red, modifier = Modifier.padding(top = 8.dp))
            }

            // Loading indicator for network calls
            if (isLoading) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally).padding(top = 16.dp))
            }
        }
    }
}

@Composable
fun CategoryDropdownItem(category: Category, onSelectCategory: (Category) -> Unit) {
    Text(
        text = category.name,
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .clickable { onSelectCategory(category) }
    )
}

fun getCurrentDate(): String {
    val currentDate = Calendar.getInstance().time
    val formatter = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    return formatter.format(currentDate)
}

// Network calls
fun addExpense(token: String, expense: Expense, onResult: (Boolean) -> Unit) {
    RetrofitInstance.api.addExpense(token, expense)
        .enqueue(object : Callback<Expense> {
            override fun onResponse(call: Call<Expense>, response: Response<Expense>) {
                onResult(response.isSuccessful)
            }

            override fun onFailure(call: Call<Expense>, t: Throwable) {
                onResult(false)
            }
        })
}

fun fetchCategories(onCategoriesFetched: (List<Category>) -> Unit) {
    RetrofitInstance.api.getCategories()
        .enqueue(object : Callback<List<Category>> {
            override fun onResponse(call: Call<List<Category>>, response: Response<List<Category>>) {
                if (response.isSuccessful) {
                    response.body()?.let { categories ->
                        onCategoriesFetched(categories)
                    }
                }
            }

            override fun onFailure(call: Call<List<Category>>, t: Throwable) {
                // Handle failure
            }
        })
}

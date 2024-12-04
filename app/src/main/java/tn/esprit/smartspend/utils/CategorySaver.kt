package tn.esprit.smartspend.utils

import androidx.compose.runtime.saveable.Saver
import tn.esprit.smartspend.model.Category

// Custom Saver to store and restore the Category object using its ID
val CategorySaver = Saver<Category, String>(
    save = { category -> category._id }, // Save the ID of the category
    restore = { id -> Category(id, "Groceries", "Expense") } // Restore using ID, you can fetch it from your data source
)

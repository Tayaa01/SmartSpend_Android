package tn.esprit.smartspend.views

import android.Manifest
import android.content.ContentUris
import android.content.Context
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Environment
import android.provider.DocumentsContract
import android.provider.MediaStore
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.navigation.NavController
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import tn.esprit.smartspend.R
import tn.esprit.smartspend.model.Category
import tn.esprit.smartspend.model.Expense
import tn.esprit.smartspend.model.Income
import tn.esprit.smartspend.network.RetrofitInstance
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddTransactionScreen(
    onSaveTransaction: (Any) -> Unit,
    token: String,
    navController: NavController
) {
    var description by rememberSaveable { mutableStateOf("") }
    var amount by rememberSaveable { mutableStateOf("") }
    var date by rememberSaveable { mutableStateOf(getCurrentDate()) }
    var category by remember { mutableStateOf<Category?>(null) }
    var isExpense by rememberSaveable { mutableStateOf(true) }

    var categories by remember { mutableStateOf<List<Category>>(emptyList()) }
    var showCategoryDropdown by remember { mutableStateOf(false) }
    var isLoading by remember { mutableStateOf(true) }
    var isError by remember { mutableStateOf(false) }

    var currentPhotoPath = rememberSaveable { mutableStateOf("") }

    val context = LocalContext.current

    val takePictureLauncher = rememberLauncherForActivityResult(ActivityResultContracts.TakePicture()) { success ->
        if (success) {
            val file = File(currentPhotoPath.value)
            uploadPhoto(file, token, navController)
        }
    }

    val pickImageLauncher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        uri?.let {
            val realPath = getRealPathFromURI(context, it)
            if (realPath != null) {
                val file = File(realPath)
                uploadPhoto(file, token, navController)
            } else {
                Log.e("AddTransactionScreen", "Failed to get real path from URI")
            }
        }
    }

    val cameraPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            dispatchTakePictureIntent(context, takePictureLauncher, currentPhotoPath)
        } else {
            // Handle permission denial
        }
    }

    val readStoragePermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            pickImageLauncher.launch("image/*")
        } else {
            // Handle permission denial
        }
    }

    LaunchedEffect(isExpense) {
        Log.d("LaunchedEffect", "Fetching categories for isExpense = $isExpense")
        isLoading = true
        fetchCategories(isExpense) { fetchedCategories ->
            categories = fetchedCategories
            isLoading = false
            Log.d("fetchCategories", "Fetched ${categories.size} categories")
        }
    }

    val isFormValid = description.isNotBlank() && amount.isNotBlank() && category != null

    val onSaveClick = {
        if (isExpense) {
            if (isFormValid) {
                val amountValue = amount.toDoubleOrNull()
                if (amountValue != null) {
                    val expense = Expense(amountValue,description, date, category!!._id)
                    addExpense(token, expense) { success ->
                        if (success) {
                            navController.navigate("home") {
                                popUpTo("home") { inclusive = true }
                            }
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
        } else {
            if (isFormValid) {
                val amountValue = amount.toDoubleOrNull()
                if (amountValue != null) {
                    val income = Income(amountValue,description, date, category!!._id)
                    addIncome(token, income) { success ->
                        if (success) {
                            navController.navigate("home") {
                                popUpTo("home") { inclusive = true }
                            }
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
    }

    Box(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFFF5F5F5)),
            elevation = CardDefaults.cardElevation(8.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                TextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Description") },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(16.dp))

                TextField(
                    value = amount,
                    onValueChange = { amount = it },
                    label = { Text("Amount") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(16.dp))

                TextField(
                    value = date,
                    onValueChange = { date = it },
                    label = { Text("Date") },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(16.dp))

                Button(onClick = { showCategoryDropdown = !showCategoryDropdown }) {
                    Text(category?.name ?: "Select Category")
                    Icon(Icons.Filled.ArrowDropDown, contentDescription = null)
                }

                if (showCategoryDropdown) {
                    LazyColumn {
                        items(categories) { categoryItem ->
                            CategoryDropdownItem(categoryItem) {
                                category = it
                                showCategoryDropdown = false
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Button(onClick = { onSaveClick() }) {
                    Text("Save Transaction")
                }

                Spacer(modifier = Modifier.height(16.dp))

                Button(onClick = { cameraPermissionLauncher.launch(Manifest.permission.CAMERA) }) {
                    Text("Take Photo")
                }

                Spacer(modifier = Modifier.height(16.dp))

                Button(onClick = { readStoragePermissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE) }) {
                    Text("Upload Photo")
                }

                if (isError) {
                    Spacer(modifier = Modifier.height(16.dp))
                    Text("Error saving transaction", color = Color.Red)
                }

                if (isLoading) {
                    Spacer(modifier = Modifier.height(16.dp))
                    CircularProgressIndicator()
                }
            }
        }
    }
}

fun dispatchTakePictureIntent(context: Context, takePictureLauncher: ActivityResultLauncher<Uri>, currentPhotoPath: MutableState<String>) {
    val photoFile: File? = try {
        createImageFile(context, currentPhotoPath)
    } catch (ex: IOException) {
        null
    }
    photoFile?.also {
        val photoURI = FileProvider.getUriForFile(
            context,
            "tn.esprit.smartspend.fileprovider",
            it
        )
        takePictureLauncher.launch(photoURI)
    }
}

@Throws(IOException::class)
fun createImageFile(context: Context, currentPhotoPath: MutableState<String>): File {
    val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
    val storageDir: File = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES)!!
    return File.createTempFile(
        "JPEG_${timeStamp}_",
        ".jpg",
        storageDir
    ).apply {
        currentPhotoPath.value = absolutePath
    }
}

fun getRealPathFromURI(context: Context, uri: Uri): String? {
    // Check if the URI is a document URI
    if (DocumentsContract.isDocumentUri(context, uri)) {
        if (isExternalStorageDocument(uri)) {
            val docId = DocumentsContract.getDocumentId(uri)
            val split = docId.split(":")
            val type = split[0]
            if ("primary".equals(type, ignoreCase = true)) {
                return "${Environment.getExternalStorageDirectory()}/${split[1]}"
            }
        } else if (isDownloadsDocument(uri)) {
            val id = DocumentsContract.getDocumentId(uri)
            val contentUri = ContentUris.withAppendedId(
                Uri.parse("content://downloads/public_downloads"), id.toLong()
            )
            return getDataColumn(context, contentUri, null, null)
        } else if (isMediaDocument(uri)) {
            val docId = DocumentsContract.getDocumentId(uri)
            val split = docId.split(":")
            val type = split[0]
            var contentUri: Uri? = null
            when (type) {
                "image" -> contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI
                "video" -> contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI
                "audio" -> contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
            }
            val selection = "_id=?"
            val selectionArgs = arrayOf(split[1])
            return getDataColumn(context, contentUri, selection, selectionArgs)
        }
    } else if ("content".equals(uri.scheme, ignoreCase = true)) {
        return getDataColumn(context, uri, null, null)
    } else if ("file".equals(uri.scheme, ignoreCase = true)) {
        return uri.path
    }
    return null
}

fun getDataColumn(context: Context, uri: Uri?, selection: String?, selectionArgs: Array<String>?): String? {
    val column = "_data"
    val projection = arrayOf(column)
    context.contentResolver.query(uri!!, projection, selection, selectionArgs, null)?.use { cursor ->
        if (cursor.moveToFirst()) {
            val columnIndex = cursor.getColumnIndexOrThrow(column)
            return cursor.getString(columnIndex)
        }
    }
    return null
}

fun isExternalStorageDocument(uri: Uri): Boolean {
    return "com.android.externalstorage.documents" == uri.authority
}

fun isDownloadsDocument(uri: Uri): Boolean {
    return "com.android.providers.downloads.documents" == uri.authority
}

fun isMediaDocument(uri: Uri): Boolean {
    return "com.android.providers.media.documents" == uri.authority
}

@Composable
fun CategoryDropdownItem(category: Category, onSelectCategory: (Category) -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .clickable { onSelectCategory(category) },
        verticalAlignment = Alignment.CenterVertically
    ) {
        val iconResourceId = when (category.name) {
            "Groceries" -> R.drawable.groceries_4715353
            "Entertainment" -> R.drawable.movie_tickets_7452230
            "Healthcare" -> R.drawable.health_insurance_15341103
            "Housing" -> R.drawable.house_1352981
            "Transportation" -> R.drawable.car_1680067
            "Utilities" -> R.drawable.maintenance_16587880
            "Salary" -> R.drawable.cash_11761323
            else -> R.drawable.other // A default icon for unspecified categories
        }

        Icon(
            painter = painterResource(id = iconResourceId),
            contentDescription = category.name,
            modifier = Modifier.size(24.dp),
            tint = Color.Unspecified
        )

        Spacer(modifier = Modifier.width(8.dp))

        Text(
            text = category.name,
            modifier = Modifier.weight(1f),
            fontSize = 16.sp,
            color = Color.Black
        )
    }
}

fun getCurrentDate(): String {
    val currentDate = Calendar.getInstance().time
    val formatter = SimpleDateFormat("yyyy-MM-dd'T'HH:mm", Locale.getDefault())
    return formatter.format(currentDate)
}

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

fun addIncome(token: String, income: Income, onResult: (Boolean) -> Unit) {
    RetrofitInstance.api.addIncome(token, income)
        .enqueue(object : Callback<Income> {
            override fun onResponse(call: Call<Income>, response: Response<Income>) {
                onResult(response.isSuccessful)
            }

            override fun onFailure(call: Call<Income>, t: Throwable) {
                onResult(false)
            }
        })
}

fun fetchCategories(isExpense: Boolean, onCategoriesFetched: (List<Category>) -> Unit) {
    val call = if (isExpense) {
        RetrofitInstance.api.getExpenseCategories() // API endpoint for Expense categories
    } else {
        RetrofitInstance.api.getIncomeCategories() // API endpoint for Income categories
    }

    call.enqueue(object : Callback<List<Category>> {
        override fun onResponse(call: Call<List<Category>>, response: Response<List<Category>>) {
            if (response.isSuccessful) {
                response.body()?.let { categories ->
                    onCategoriesFetched(categories)
                }
            }
        }

        override fun onFailure(call: Call<List<Category>>, t: Throwable) {
            // Handle failure (e.g., log error or notify user)
            onCategoriesFetched(emptyList()) // Return an empty list on failure
        }
    })
}

fun uploadPhoto(file: File, token: String, navController: NavController) {
    val requestFile = file.asRequestBody("image/jpeg".toMediaTypeOrNull())
    val body = MultipartBody.Part.createFormData("file", file.name, requestFile)

    RetrofitInstance.api.scanBill(token, body).enqueue(object : Callback<ResponseBody> {
        override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
            if (response.isSuccessful) {
                Log.d("uploadPhoto", "Photo uploaded successfully")
                navController.navigate("home") {
                    popUpTo("home") { inclusive = true }
                }
            } else {
                Log.e("uploadPhoto", "Failed to upload photo: ${response.errorBody()?.string()}")
            }
        }

        override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
            Log.e("uploadPhoto", "Error uploading photo", t)
        }
    })
}
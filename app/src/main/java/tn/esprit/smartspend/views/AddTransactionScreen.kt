package tn.esprit.smartspend.views

import android.Manifest
import android.app.Activity
import android.content.ContentUris
import android.content.Context
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Environment
import android.provider.DocumentsContract
import android.provider.MediaStore
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
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
import tn.esprit.smartspend.ui.theme.PrimaryColor
import tn.esprit.smartspend.ui.theme.SecondaryColor
import tn.esprit.smartspend.ui.theme.AccentColor
import tn.esprit.smartspend.ui.theme.BackgroundColor
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*
import android.content.Intent
import android.provider.Settings
import androidx.activity.result.PickVisualMediaRequest
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddTransactionScreen(
    onSaveTransaction: (Any) -> Unit,
    token: String,
    navController: NavController
) {
    var description by rememberSaveable { mutableStateOf("") }
    var amount by rememberSaveable { mutableStateOf("") }
    var category by remember { mutableStateOf<Category?>(null) }
    var isExpense by rememberSaveable { mutableStateOf(true) }

    var categories by remember { mutableStateOf<List<Category>>(emptyList()) }
    var showCategoryDropdown by remember { mutableStateOf(false) }
    var isLoading by remember { mutableStateOf(true) }
    var isError by remember { mutableStateOf(false) }

    var currentPhotoPath = rememberSaveable { mutableStateOf("") }
    var showProgressDialog by remember { mutableStateOf(false) }

    val context = LocalContext.current

    val takePictureLauncher = rememberLauncherForActivityResult(ActivityResultContracts.TakePicture()) { success ->
        if (success) {
            val file = File(currentPhotoPath.value)
            showProgressDialog = true
            uploadPhoto(file, token, navController) {
                showProgressDialog = false
                navController.navigate("home") {
                    popUpTo("home") { inclusive = true }
                }
                Toast.makeText(context, "Photo uploaded successfully", Toast.LENGTH_SHORT).show()
            }
        }
    }

    val pickMedia = rememberLauncherForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
        if (uri != null) {
            Log.d("PhotoPicker", "Selected URI: $uri")
            val filePath = getRealPathFromURI(context, uri)
            filePath?.let { path ->
                val file = File(path)
                showProgressDialog = true
                uploadPhoto(file, token, navController) {
                    showProgressDialog = false
                    navController.navigate("home") {
                        popUpTo("home") { inclusive = true }
                    }
                    Toast.makeText(context, "Photo uploaded successfully", Toast.LENGTH_SHORT).show()
                }
            }
        } else {
            Log.d("PhotoPicker", "No media selected")
        }
    }

    val cameraPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            dispatchTakePictureIntent(context, takePictureLauncher, currentPhotoPath)
        } else {
            Toast.makeText(context, "Camera permission denied", Toast.LENGTH_SHORT).show()
        }
    }

    val readStoragePermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            pickMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
        } else {
            Toast.makeText(context, "Storage permission denied", Toast.LENGTH_SHORT).show()
        }
    }

    LaunchedEffect(isExpense) {
        isLoading = true
        fetchCategories(isExpense) { fetchedCategories ->
            categories = fetchedCategories
            isLoading = false
        }
    }

    val isFormValid = description.isNotBlank() && amount.isNotBlank() && category != null

    val onSaveClick = {
        val currentDate = getCurrentDate()
        if (isExpense) {
            if (isFormValid) {
                val expense = Expense(amount.toDouble(), description, currentDate, category!!._id)
                addExpense(token, expense) { success ->
                    if (success) {
                        navController.navigate("home") {
                            popUpTo("home") { inclusive = true }
                        }
                    } else {
                        Toast.makeText(context, "Failed to save expense", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        } else {
            if (isFormValid) {
                val income = Income(amount.toDouble(), description, currentDate, category!!._id)
                addIncome(token, income) { success ->
                    if (success) {
                        navController.navigate("home") {
                            popUpTo("home") { inclusive = true }
                        }
                    } else {
                        Toast.makeText(context, "Failed to save income", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }

    Box(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Column {
            Text(
                text = "Add Transaction",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(bottom = 16.dp)
            ) {
                RadioButton(
                    selected = isExpense,
                    onClick = { isExpense = true },
                    colors = RadioButtonDefaults.colors(selectedColor = PrimaryColor)
                )
                Text(text = "Expense", modifier = Modifier.padding(start = 8.dp))
                Spacer(modifier = Modifier.width(16.dp))
                RadioButton(
                    selected = !isExpense,
                    onClick = { isExpense = false },
                    colors = RadioButtonDefaults.colors(selectedColor = PrimaryColor)
                )
                Text(text = "Income", modifier = Modifier.padding(start = 8.dp))
            }

            OutlinedTextField(
                value = amount,
                onValueChange = { amount = it },
                label = { Text("Amount") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp)
            )

            OutlinedTextField(
                value = description,
                onValueChange = { description = it },
                label = { Text("Description") },
                modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp)
            )

            Box(modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp)) {
                OutlinedTextField(
                    value = category?.name ?: "",
                    onValueChange = {},
                    label = { Text("Category") },
                    readOnly = true,
                    trailingIcon = {
                        Icon(
                            imageVector = Icons.Filled.ArrowDropDown,
                            contentDescription = null,
                            modifier = Modifier.clickable { showCategoryDropdown = true }
                        )
                    },
                    modifier = Modifier.fillMaxWidth().clickable { showCategoryDropdown = true }
                )
                DropdownMenu(
                    expanded = showCategoryDropdown,
                    onDismissRequest = { showCategoryDropdown = false }
                ) {
                    categories.forEach { categoryItem ->
                        DropdownMenu(
                            expanded = showCategoryDropdown,
                            onDismissRequest = { showCategoryDropdown = false }
                        ) {
                            categories.forEach { categoryItem ->
                                DropdownMenuItem(
                                    text = { Text(categoryItem.name) },
                                    onClick = {
                                        category = categoryItem
                                        showCategoryDropdown = false
                                    }
                                )
                            }
                        }
                    }
                }
            }

            Button(
                onClick = onSaveClick,
                enabled = isFormValid,
                shape = RoundedCornerShape(5.dp),
                colors = ButtonDefaults.buttonColors(containerColor = PrimaryColor),
                modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp)
            ) {
                Text(text = "Save", color = Color.White)
            }

            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp)
            ) {
                Button(
                    onClick = {
                        if (ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
                            dispatchTakePictureIntent(context, takePictureLauncher, currentPhotoPath)
                        } else {
                            cameraPermissionLauncher.launch(Manifest.permission.CAMERA)
                        }
                    },
                    shape = RoundedCornerShape(5.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = SecondaryColor),
                    modifier = Modifier.weight(1f).padding(end = 8.dp)
                ) {
                    Text(text = "Take Photo", color = Color.White)
                }

                Button(
                    onClick = {
                        if (ContextCompat.checkSelfPermission(context, Manifest.permission.READ_MEDIA_IMAGES) == PackageManager.PERMISSION_GRANTED) {
                            pickMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
                        } else {
                            readStoragePermissionLauncher.launch(Manifest.permission.READ_MEDIA_IMAGES)
                        }
                    },
                    shape = RoundedCornerShape(5.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = SecondaryColor),
                    modifier = Modifier.weight(1f).padding(start = 8.dp)
                ) {
                    Text(text = "Upload Photo", color = Color.White)
                }
            }
        }
    }

    if (showProgressDialog) {
        UploadInProgressDialog(onDismiss = { showProgressDialog = false })
    }
}

@Composable
fun UploadInProgressDialog(onDismiss: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(text = "Uploading", style = MaterialTheme.typography.titleMedium)
        },
        text = {
            Box(
                modifier = Modifier.fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    CircularProgressIndicator()
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(text = "Please wait while the upload completes.")
                }
            }
        },
        confirmButton = {

        }
    )
}

fun uploadPhoto(file: File, token: String, navController: NavController, onUploadComplete: () -> Unit) {
    val requestFile = file.asRequestBody("image/jpeg".toMediaTypeOrNull())
    val body = MultipartBody.Part.createFormData("file", file.name, requestFile)

    RetrofitInstance.api.scanBill(token, body).enqueue(object : Callback<ResponseBody> {
        override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
            onUploadComplete()
        }

        override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
            onUploadComplete()
        }
    })
}

fun showPermissionDeniedDialog(context: Context) {
    AlertDialog.Builder(context)
        .setTitle("Permission Denied")
        .setMessage("Storage permission is permanently denied. Please enable it in the app settings.")
        .setPositiveButton("Settings") { _, _ ->
            val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
            val uri = Uri.fromParts("package", context.packageName, null)
            intent.data = uri
            context.startActivity(intent)
        }
        .setNegativeButton("Cancel", null)
        .show()
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
            val contentUri = when (type) {
                "image" -> MediaStore.Images.Media.EXTERNAL_CONTENT_URI
                "video" -> MediaStore.Video.Media.EXTERNAL_CONTENT_URI
                "audio" -> MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
                else -> null
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
            .clickable { onSelectCategory(category) }
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        val iconResourceId = when (category.name) {
            "Groceries" -> R.drawable.groceriesnav
            "Entertainment" -> R.drawable.movienav
            "Healthcare" -> R.drawable.healthnav
            "Housing" -> R.drawable.housenav
            "Transportation" -> R.drawable.carnav
            "Utilities" -> R.drawable.othernav
            "Salary" -> R.drawable.cashnav
            else -> R.drawable.cash_11761323
        }

        Icon(
            painter = painterResource(id = iconResourceId),
            contentDescription = null,
            modifier = Modifier.size(24.dp)
        )

        Spacer(modifier = Modifier.width(8.dp))

        Text(
            text = category.name,
            fontSize = 16.sp,
            fontWeight = FontWeight.Medium,
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
        RetrofitInstance.api.getExpenseCategories()
    } else {
        RetrofitInstance.api.getIncomeCategories()
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
            onCategoriesFetched(emptyList())
        }
    })
}
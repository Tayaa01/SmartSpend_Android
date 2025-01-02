package tn.esprit.smartspend.network

import okhttp3.MultipartBody
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Query
import tn.esprit.smartspend.model.*

interface ApiService {

    // Méthode pour la connexion (sign-in)
    @POST("/auth/login")
    fun signIn(@Body signInRequest: SignInRequest): Call<SignInResponse>

    // Méthode pour l'inscription (sign-up)
    @POST("/users")
    fun signUp(@Body signUpRequest: SignUpRequest): Call<SignUpResponse>

    @POST("auth/reset-password")
    fun resetPassword(
        @Query("token") token: String, // Token sent as query parameter
        @Body request: ResetPasswordRequest // New password sent as request body
    ): Call<ResetPasswordResponse>

    @POST("auth/forgot-password")
    fun forgotPassword(@Body request: ForgotPasswordRequest): Call<ForgotPasswordResponse>

    @GET("expense")
    fun getExpenses(@Query("token") token: String): Call<List<Expense>>
    @GET("income")
    fun getIncomes(@Query("token") token: String): Call<List<Income>>
    @GET("categories")
    fun getCategories(): Call<List<Category>>

    @GET("categories/expense")
    fun getExpenseCategories(): Call<List<Category>>
    @GET("categories/income")
    fun getIncomeCategories(): Call<List<Category>>

    @POST("expense")
    fun addExpense(
        @Query("token") token: String,
        @Body expense: Expense
    ): Call<Expense>  // Note: Return type should be Call<Expense>

    @GET("recommendations/generate")
    suspend fun getRecommendation(
        @Query("userToken") userToken: String,  // Paramètre pour le token
        @Query("period") period: String         // Paramètre pour la période
    ): Recommendation

    // New method to fetch total expenses
    @GET("expense/total")
    fun getTotalExpenses(@Query("token") token: String): Call<Map<String, Double>>

    @POST("income")
    fun addIncome(
        @Query("token") token: String,
        @Body income: Income
    ): Call<Income>

    @GET("income/total")
    fun getTotalIncome(@Query("token") token: String): Call<Map<String, Double>>

    @Multipart
    @POST("expense/scan-bill")
    fun scanBill(
        @Query("token") token: String,
        @Part file: MultipartBody.Part
    ): Call<ResponseBody>

    @GET("auth/verify-reset-token")
    fun verifyResetToken(@Query("token") token: String): Call<TokenVerificationResponse>
}

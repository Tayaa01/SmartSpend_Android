package tn.esprit.smartspend.network

import retrofit2.Call
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Query
import tn.esprit.smartspend.model.Category
import tn.esprit.smartspend.model.Expense
import tn.esprit.smartspend.model.ForgotPasswordRequest
import tn.esprit.smartspend.model.ForgotPasswordResponse
import tn.esprit.smartspend.model.ResetPasswordRequest
import tn.esprit.smartspend.model.ResetPasswordResponse
import tn.esprit.smartspend.model.SignInRequest
import tn.esprit.smartspend.model.SignInResponse
import tn.esprit.smartspend.model.SignUpRequest
import tn.esprit.smartspend.model.SignUpResponse

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

    @GET("categories")
    fun getCategories(): Call<List<Category>>

    @POST("expense")
    fun addExpense(
        @Query("token") token: String,
        @Body expense: Expense
    ): Call<Expense>  // Note: Return type should be Call<Expense>

}

package tn.esprit.smartspend.network

import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.Query
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
        @Query("token") token: String,
        @Body newPassword: String
    ): Call<Void>
    @POST("auth/forgot-password")
    fun forgotPassword(
        @Body email: Map<String, String> // Utilisation d'un Map pour envoyer l'email comme JSON.
    ): Call<Void>
}

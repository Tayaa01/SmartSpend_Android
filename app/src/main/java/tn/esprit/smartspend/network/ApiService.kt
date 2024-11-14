package tn.esprit.smartspend.network

import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST
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
}

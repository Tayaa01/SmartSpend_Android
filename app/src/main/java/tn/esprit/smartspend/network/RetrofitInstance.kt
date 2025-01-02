package tn.esprit.smartspend.network

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitInstance {
    // Remplacer localhost par 10.0.2.2 pour l'accès via l'émulateur Android
    private const val BASE_URL = "http://10.0.2.2.46:3000/"  // URL pour l'émulateur Android

    val retrofit: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)  // Utilise la nouvelle URL
            .addConverterFactory(GsonConverterFactory.create()) // Utilise Gson pour la sérialisation
            .build()
    }

    val api: ApiService by lazy {
        retrofit.create(ApiService::class.java)
    }
    val apiService: ApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ApiService::class.java)
    }
}

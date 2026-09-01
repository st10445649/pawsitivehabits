package com.zahraag.pawsitivehabits.data.remote

import android.content.Context
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import kotlin.jvm.java

object RetrofitClient {
    // 10.0.2.2 points to host development computer from Android Emulator
    private const val BASE_URL = "http://10.0.2.2:3000/api/"
    private var retrofit: Retrofit? = null


    fun getApiService(context: Context): ApiService {
        if (retrofit == null) {
            val tokenManager = TokenManager(context.applicationContext)
            val authInterceptor = AuthInterceptor(tokenManager)

            val okHttpClient = OkHttpClient.Builder()
                .addInterceptor(authInterceptor)
                .build()

            retrofit = Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(okHttpClient)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
        }
        return retrofit!!.create(ApiService::class.java)
    }
}
package com.zahraag.pawsitivehabits.data.remote

import android.content.Context
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import okhttp3.logging.HttpLoggingInterceptor
import kotlin.jvm.java

private val safeLongDeserializer = JsonDeserializer { json, _, _ ->
    try {
        if (json.isJsonPrimitive) {
            val primitive = json.asJsonPrimitive
            if (primitive.isNumber) {
                primitive.asLong
            } else {
                val str = primitive.asString
                if (str.isNullOrEmpty()) 0L else str.toLongOrNull() ?: 0L
            }
        } else {
            0L
        }
    } catch (e: Exception) {
        0L
    }
}

val gson: Gson = GsonBuilder()
    .registerTypeAdapter(Long::class.javaPrimitiveType, safeLongDeserializer)
    .registerTypeAdapter(Long::class.javaObjectType, safeLongDeserializer)
    .create()
object RetrofitClient {
    // 10.0.2.2 points to host development computer from Android Emulator
    private const val BASE_URL = "http://10.0.2.2:3000/"
    private var retrofit: Retrofit? = null


    fun getApiService(context: Context): ApiService {
        if (retrofit == null) {
            val tokenManager = TokenManager(context.applicationContext)
            val authInterceptor = AuthInterceptor(tokenManager)

            val loggingInterceptor = HttpLoggingInterceptor().apply {
                level = HttpLoggingInterceptor.Level.BODY
            }

            val okHttpClient = OkHttpClient.Builder()
                .addInterceptor(authInterceptor)
                .addInterceptor(loggingInterceptor)
                .build()

            retrofit = Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(okHttpClient)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build()
        }
        return retrofit!!.create(ApiService::class.java)
    }
}
package com.zahraag.pawsitivehabits.data.remote

import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.Response

class AuthInterceptor : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest = chain.request()
        val user = FirebaseAuth.getInstance().currentUser

        // Obtain token synchronously for OkHttp execution context
        val token = user?.let {
            try {
                runBlocking { it.getIdToken(false).await().token }
            } catch (e: Exception) {
                null
            }
        }

        val requestBuilder = originalRequest.newBuilder()
        token?.isEmpty()?.let {
            if (!it) {
                requestBuilder.addHeader("Authorization", "Bearer $token")
            }
        }

        return chain.proceed(requestBuilder.build())
    }
}
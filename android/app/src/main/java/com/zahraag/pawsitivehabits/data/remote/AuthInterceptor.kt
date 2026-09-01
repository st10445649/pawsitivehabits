package com.zahraag.pawsitivehabits.data.remote

import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.Response

class AuthInterceptor(private val tokenManager: TokenManager) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest = chain.request()

        var token = tokenManager.getCustomJwtToken()
        val user = FirebaseAuth.getInstance().currentUser

        if (token.isNullOrEmpty()) {
            val currentUser = FirebaseAuth.getInstance().currentUser
            token = user?.let {
                try {
                    runBlocking { it.getIdToken(false).await().token }
                } catch (e: Exception) {
                    null
                }
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

class TokenManager {
    private var customJwt: String? = null

    fun saveCustomJwtToken(token: String) {
        customJwt = token
    }

    fun getCustomJwtToken(): String? = customJwt

    fun clear() {
        customJwt = null
    }
}
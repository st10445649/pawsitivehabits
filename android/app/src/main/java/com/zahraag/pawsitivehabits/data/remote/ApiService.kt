package com.zahraag.pawsitivehabits.data.remote

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Path

interface ApiService {
    @POST("auth/register")
    suspend fun register(
        @Body request: RegisterRequest
    ): Response<AuthResponse>

    @POST("auth/login")
    suspend fun login(
        @Body request: LoginRequest
    ): Response<AuthResponse>

    @POST("users/sync")
    suspend fun syncGoogleUser(): Response<AuthResponse>

    // pet operations
    @GET("pets")
    suspend fun getPets(): Response<List<PetDto>>

    //create pet
    @POST("pets")
    suspend fun createPet(
        @Body pet: PetDto
    ): Response<PetDto>

    //delete pet
    @DELETE("pets/{id}")
    suspend fun deletePet(
        @Path("id") petId: String
    ): Response<Unit>
}


data class UserDataWrapper(
    val user: UserDto
)

data class UserDto(
    val id: String,
    val firebaseUid: String,
    val email: String,
    val firstName: String? = null,
    val lastName: String? = null,
    val displayName: String,
    val photoURL: String? = null,
    val authProvider: String
)

data class PetDto(
    val id: String? = null,
    val name: String,
    val petType: String,
    val breed: String? = null,
    val isNeutered: Boolean = false,
    val microchipId: String? = null
)

data class RegisterRequest(
    val email: String,
    val password: String,
    val firstName: String,
    val lastName: String
)

data class LoginRequest(
    val email: String,
    val password: String
)

data class AuthResponse(
    val status: String,
    val token: String? = null,
    val data: UserDataWrapper? = null,
    val message: String? = null
)

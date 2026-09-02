package com.zahraag.pawsitivehabits.data.remote

import com.google.gson.annotations.SerializedName
import com.zahraag.pawsitivehabits.data.models.Pet
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

    @POST("auth/google")
    suspend fun syncGoogleUser(
        @Body request: GoogleAuthRequest
    ): Response<AuthResponse>

    // pet operations
    @GET("pets")
    suspend fun getPets(): Response<PetResponse>

    //create pet
    @POST("pets")
    suspend fun createPet(
        @Body pet: Pet
    ): Response<PetResponse>

    //delete pet
    @DELETE("pets/{id}")
    suspend fun deletePet(
        @Path("id") petId: String
    ): Response<Unit>
}

data class GoogleAuthRequest(
    val idToken: String
)
data class UserDataWrapper(
    val user: UserDto
)

data class UserDto(
    @SerializedName("_id") val id: String,
    @SerializedName("googleId") val firebaseUid: String? = null,
    val email: String,
    val firstName: String? = null,
    val lastName: String? = null,
    val displayName: String,
    @SerializedName("picture") val photoURL: String? = null,
    val authProvider: String
)

data class PetResponse(
    val status: String,
    val data: PetData?
)

data class PetData(
    val pet: Pet?,
    val pets: List<Pet>?
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

package com.zahraag.pawsitivehabits.data.remote

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Path

interface ApiService {
    @POST("users/sync")
    suspend fun syncUser(
        @Body request: SyncUserRequest? = null
    ): Response<SyncUserResponse>

    // fetch User Profile
    @GET("users/profile")
    suspend fun getCurrentUser(): Response<SyncUserResponse>

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

// Data Transfer Objects (DTOs)
data class SyncUserRequest(
    val firstName: String? = null,
    val lastName: String? = null
)

data class SyncUserResponse(
    val status: String,
    val data: UserDataWrapper
)

data class UserDataWrapper(
    val user: UserDto
)

data class UserDto(
    val id: String,
    val firebaseUid: String,
    val email: String,
    val displayName: String,
    val photoURL: String? = null
)

data class PetDto(
    val id: String? = null,
    val name: String,
    val petType: String,
    val breed: String? = null,
    val isNeutered: Boolean = false,
    val microchipId: String? = null
)


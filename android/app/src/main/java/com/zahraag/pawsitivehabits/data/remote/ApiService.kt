package com.zahraag.pawsitivehabits.data.remote

import com.google.gson.annotations.SerializedName
import com.zahraag.pawsitivehabits.data.models.CalendarEvents
import com.zahraag.pawsitivehabits.data.models.Pet
import com.zahraag.pawsitivehabits.data.models.Routine
import com.zahraag.pawsitivehabits.data.models.RoutineLogs
import com.zahraag.pawsitivehabits.data.models.UserSettings
import com.zahraag.pawsitivehabits.data.models.Weight
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.PUT
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

    @GET("auth/profile")
    suspend fun getUserProfile(): Response<ProfileResponseDto>

    // User Settings Operations
    @GET("auth/settings")
    suspend fun getUserSettings(): Response<ApiResponse<UserSettings>>

    @PUT("auth/settings")
    suspend fun updateUserSettings(
        @Body settings: UserSettings
    ): Response<UserSettings>

    // pet operations
    @GET("pets")
    suspend fun getPets(): Response<PetResponse>

    //create pet
    @POST("pets")
    suspend fun createPet(
        @Body pet: Pet
    ): Response<PetResponse>

    @PUT("pets/{id}")
    suspend fun updatePet(
        @Path("id") petId: String,
        @Body pet: Pet
    ): Response<Unit>

    //delete pet
    @DELETE("pets/{id}")
    suspend fun deletePet(
        @Path("id") petId: String
    ): Response<Unit>

    @GET("calendar")
    suspend fun getCalendarEvents(): Response<List<CalendarEvents>>

    @POST("calendar")
    suspend fun createCalendarEvent(@Body event: CalendarEvents): Response<CalendarEvents>

    @DELETE("calendar/{eventId}")
    suspend fun deleteCalendarEvent(@Path("eventId") eventId: String): Response<Unit>

    // routines
    @GET("routines")
    suspend fun getRoutines(): Response<List<Routine>>

    @POST("routines")
    suspend fun createRoutine(@Body routine: Routine): Response<Routine>

    @DELETE("routines/{routineId}")
    suspend fun deleteRoutine(@Path("routineId") routineId: String): Response<Unit>

    // routine logs
    @GET("routines/logs")
    suspend fun getRoutineLogs(): Response<List<RoutineLogs>>

    @POST("routines/logs")
    suspend fun logRoutineCompletion(@Body log: RoutineLogs): Response<RoutineLogs>


    // weight operations
    @GET("weights")
    suspend fun getWeights(): Response<List<Weight>>

    @GET("weights/pet/{petId}")
    suspend fun getWeightsByPet(@Path("petId") petId: String): Response<List<Weight>>

    @POST("weights")
    suspend fun createWeight(@Body weight: Weight): Response<Weight>
    @DELETE("weights/{id}")
    suspend fun deleteWeight(@Path("id") weightId: String): Response<Unit>

}

data class GoogleAuthRequest(
    val idToken: String
)

data class ProfileResponseDto(
    val status: String,
    val data: UserDataWrapper
)

data class UserDataWrapper(
    val user: UserProfileDto
)


data class UserProfileDto(
    @SerializedName("_id") val id: String,
    @SerializedName("googleId") val firebaseUid: String? = null,
    val email: String,
    val firstName: String?,
    val lastName: String?,
    val displayName: String?,
    val photoURL: String?,
    val authProvider: String
) {
    val name: String
        get() = displayName ?: "$firstName $lastName".trim()
}
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


data class UserRequest(
    val status: String,
    val token: String? = null,
    val message: String? = null
)

data class ApiResponse<T>(
    val status: String,
    val data: T? = null,
    val message: String? = null
)


package com.zahraag.pawsitivehabits.helpers
import io.github.jan.supabase.auth.Auth
import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.postgrest.Postgrest
import io.github.jan.supabase.storage.Storage
import io.github.jan.supabase.storage.storage
import java.io.File

object SupabaseClientProvider {
    val client = createSupabaseClient(
        supabaseUrl = "https://rktoswnposatxixloiwk.supabase.co",
        supabaseKey = "sb_publishable_ZNoQ7hOCSkYEYhDXdjxJyA_L4ldjypQ"
          ) {
        install(Postgrest)
        install(Storage)

    }

}


/*
Author: Supabase
Date Accessed:4 September 2026
Link: https://supabase.com/docs/guides/getting-started/tutorials/with-kotlin
Reason: Used official documentation example for adding supabase to android studio
*/

/*
Author: Supabase
Date Accessed:4 September 2026
Link: https://supabase.com/docs/reference/kotlin/explain
Reason:Official documentation by Supabase for usage. Used in all repository layers and client.
*/
package com.zahraag.pawsitivehabits.helpers
import io.github.jan.supabase.auth.Auth
import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.postgrest.Postgrest
import io.github.jan.supabase.storage.Storage

object SupabaseClientProvider {
    val client = createSupabaseClient(
        supabaseUrl = "https://mbsrsfaxgumeqckrlszk.supabase.co",
        //get anonymous public key
        supabaseKey ="eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6Im1ic3JzZmF4Z3VtZXFja3Jsc3prIiwicm9sZSI6ImFub24iLCJpYXQiOjE3ODAxNTE3NzIsImV4cCI6MjA5NTcyNzc3Mn0.WCNofL7AvAjmO7WsDAhzzCK_cgLb02PSZ1KSjpQCrvE"
    ) {
        install(Postgrest)
        install(Storage)
        install(Auth)

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
package com.banksoalpro.app.core.auth

import android.content.Context
import android.content.Intent
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException

object GoogleSignInHelper {

    fun getGoogleSignInClient(context: Context): GoogleSignInClient {
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestEmail()
            .requestProfile()
            .build()
        return GoogleSignIn.getClient(context, gso)
    }

    fun parseSignInResult(data: Intent?): Result<GoogleSignInAccount> {
        return try {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            val account = task.getResult(ApiException::class.java)
            if (account != null) {
                Result.success(account)
            } else {
                Result.failure(Exception("Akun Google tidak ditemukan"))
            }
        } catch (e: ApiException) {
            val friendlyMsg = when (e.statusCode) {
                10 -> "Developer Error (10): SHA-1 keystore belum didaftarkan di Google Cloud Console. Silakan gunakan opsi masuk manual."
                12500 -> "Sign In Failed (12500): Layanan Google Play tidak dapat terhubung. Coba gunakan opsi email manual."
                12501 -> "Login Google dibatalkan oleh pengguna."
                12502 -> "Sedang dalam proses sign-in."
                7 -> "Koneksi jaringan terputus (Network Error)."
                else -> "Gagal (Kode ${e.statusCode}): ${e.localizedMessage ?: "Terjadi kendala Google Play Services"}"
            }
            Result.failure(Exception(friendlyMsg))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}

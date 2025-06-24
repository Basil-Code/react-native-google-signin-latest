package com.googlesigninlatest

import android.net.Uri
import android.util.Log
import androidx.credentials.ClearCredentialStateRequest
import androidx.credentials.CredentialManager
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialRequest
import androidx.credentials.exceptions.GetCredentialCancellationException
import androidx.credentials.exceptions.GetCredentialException
import androidx.credentials.exceptions.NoCredentialException
import com.facebook.react.bridge.Arguments
import com.facebook.react.bridge.Promise
import com.facebook.react.bridge.ReactApplicationContext
import com.facebook.react.bridge.ReactContextBaseJavaModule
import com.facebook.react.bridge.ReactMethod
import com.facebook.react.bridge.ReadableMap
import com.facebook.react.bridge.WritableMap
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GetSignInWithGoogleOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import kotlinx.coroutines.*

class GoogleSigninLatestModule(reactContext: ReactApplicationContext) :
  ReactContextBaseJavaModule(reactContext) {

//  private var webClientId: String? = null

  private val tag = "GoogleSigninLatestModule: "
  private val credentialManager = CredentialManager.create(reactContext)
  private lateinit var getCredRequest: GetCredentialRequest
  private lateinit var getCredWithGoogleOption: GetCredentialRequest

//  private val job = SupervisorJob()
  // Create a custom coroutine scope
  private val myPluginScope = CoroutineScope(SupervisorJob() + Dispatchers.Main.immediate)
//  private lateinit var googleIdOption: GetGoogleIdOption

  override fun getName(): String {
    return NAME
  }

  companion object {
    const val NAME = "GoogleSigninLatest"
  }

  @ReactMethod
  fun configure(
    config: ReadableMap,
    promise: Promise
  ) {
    val webClientId = if (config.hasKey("webClientId")) config.getString("webClientId") else null
    val autoSelectEnabled = if (config.hasKey("autoSelectEnabled")) config.getBoolean("autoSelectEnabled") else false
    val filterByAuthorizedAccounts = if (config.hasKey("filterByAuthorizedAccounts")) config.getBoolean("filterByAuthorizedAccounts") else false
//    val type = if (config.hasKey("type")) config.getString("type") else "siwg"
    Log.i(tag, "webClientId $webClientId")
    Log.i(tag, "autoSelectEnabled $autoSelectEnabled")
    Log.i(tag, "filterByAuthorizedAccounts $filterByAuthorizedAccounts")

    if (webClientId == null) {
      promise.reject("ERROR", "webClientId is required")
      return
    }


    getCredRequest = GetCredentialRequest.Builder()
    .addCredentialOption(
      GetGoogleIdOption.Builder()
        .setFilterByAuthorizedAccounts(filterByAuthorizedAccounts)
        .setServerClientId(webClientId)
        .setAutoSelectEnabled(autoSelectEnabled)
        .build()
    )
    .build()

    //      GetSignInWithGoogleOption.Builder(
    //        serverClientId = webClientId
    //
    //      ).build()
    getCredWithGoogleOption = GetCredentialRequest.Builder()
      .addCredentialOption(
        GetSignInWithGoogleOption.Builder(
          serverClientId = webClientId
        )
          .build()
      )
      .build()

    promise.resolve(true)
  }

  @ReactMethod
  fun signInWithGoogleButton(
    promise: Promise
  ) {
    val activity = currentActivity
    if (activity == null) {
      promise.reject("ACTIVITY_NOT_FOUND", "Could not launch sign-in activity")
      return
    }
    myPluginScope.launch {
      try {
        // val result = credentialManager.getCredential(activity, getCredRequest)
        val result = withContext(Dispatchers.IO) {
          credentialManager.getCredential(activity, getCredWithGoogleOption)
        }
        val credential = result.credential

        if (
          credential is CustomCredential &&
          credential.type == GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL
        ) {
          try {
            val tokenCredential = GoogleIdTokenCredential.createFrom(credential.data)
            val data = getUserProperties(tokenCredential)
            promise.resolve(data)
          } catch (e: Exception) {
            // Handle token retrieval failure (Error 28404)
            Log.e(tag, "Failed to get ID token: ${e.message}")
            // Fallback to traditional sign-in
            promise.reject("TOKEN_ERROR", "Failed to retrieve Google ID token.")
          }
        } else {
          promise.reject("INVALID_CREDENTIAL", "Credential is not GoogleIdTokenCredential")
        }
      } catch (e: GetCredentialCancellationException) {
        // No saved credentials → fallback
        promise.reject("USER_CANCELLED", e.message ?: "User cancelled sign-in")
      } catch (e: NoCredentialException) {
        // No saved credentials → fallback
        promise.reject("NO_CREDENTIALS", e.message ?: "No accounts found")
      } catch (e: GetCredentialException) {
        // Other CredentialManager errors
        Log.e(tag, "CREDENTIAL_ERROR: ${e.message}")
        promise.reject("CREDENTIAL_ERROR", "Failed to get credentials: ${e.message}")
      } catch (e: Exception) {
        // Generic errors
        Log.e(tag, "Unexpected error: ${e.message}")
        promise.reject("UNKNOWN_ERROR", "Sign-in failed: ${e.message}")
      }
    }
  }

  @ReactMethod
  fun signIn(
    config: ReadableMap,
    promise: Promise
  ) {
    val activity = currentActivity
    if (activity == null) {
      promise.reject("ACTIVITY_NOT_FOUND", "Could not launch sign-in activity")
      return
    }
    val fallbackToSignInWithGoogleButton = if (config.hasKey("fallbackToSignInWithGoogleButton")) config.getBoolean("fallbackToSignInWithGoogleButton") else false

    myPluginScope.launch {
      try {
        // val result = credentialManager.getCredential(activity, getCredRequest)
        val result = withContext(Dispatchers.IO) {
          credentialManager.getCredential(activity, getCredRequest)
        }
        val credential = result.credential

        if (
          credential is CustomCredential &&
          credential.type == GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL
        ) {
          try {
            val tokenCredential = GoogleIdTokenCredential.createFrom(credential.data)

            val data = getUserProperties(tokenCredential)
            promise.resolve(data)
          } catch (e: Exception) {
            // Handle token retrieval failure (Error 28404)
            Log.e(tag, "Failed to get ID token: ${e.message}")
            if (fallbackToSignInWithGoogleButton) {
              Log.w(tag, "Falling back to Google Sign-In button due to token error")
              signInWithGoogleButton(promise)
            } else {
              promise.reject("TOKEN_ERROR", "Failed to retrieve Google ID token")
            }
          }
        } else {
          promise.reject("INVALID_CREDENTIAL", "Credential is not GoogleIdTokenCredential")
        }
      } catch (e: GetCredentialCancellationException) {
        // No saved credentials → fallback
        promise.reject("USER_CANCELLED", e.message ?: "User cancelled sign-in")
      } catch (e: NoCredentialException) {
        // No saved credentials → fallback
        Log.w(tag, "No saved credentials found")
        if (fallbackToSignInWithGoogleButton) {
          signInWithGoogleButton(promise)
        } else {
          promise.reject("NO_CREDENTIALS", e.message ?: "No accounts found")
        }
      } catch (e: GetCredentialException) {
        // Other CredentialManager errors
        Log.e(tag, "CREDENTIAL_ERROR: ${e.message}")
        promise.reject("CREDENTIAL_ERROR", "Failed to get credentials: ${e.message}")
      } catch (e: Exception) {
        // Generic errors
        Log.e(tag, "Unexpected error: ${e.message}")
        if (fallbackToSignInWithGoogleButton) {
          signInWithGoogleButton(promise)
        } else {
          promise.reject("UNKNOWN_ERROR", "Sign-in failed: ${e.message}")
        }
      }
    }
  }

  private fun getUserProperties(tokenCredential: GoogleIdTokenCredential): WritableMap {

    val photoUrl: Uri? = tokenCredential.profilePictureUri

    val user: WritableMap = Arguments.createMap()
    user.putString("email", tokenCredential.id)
    user.putString("name", tokenCredential.displayName)
    user.putString("idToken", tokenCredential.idToken)
    user.putString("givenName", tokenCredential.givenName)
    user.putString("familyName", tokenCredential.familyName)
    user.putString("photo", photoUrl?.toString())

    return user
  }

  @ReactMethod
  fun signOut(promise: Promise) {
    myPluginScope.launch {
      try {
        credentialManager.clearCredentialState(ClearCredentialStateRequest())
        promise.resolve(true)
      } catch (e: Exception) {
        e.printStackTrace()
        Log.e(tag, "signOut error: ${e.message}")
        promise.reject(e)
      }
    }
  }

  override fun invalidate() {
    super.invalidate()
    myPluginScope.cancel()
  }
}

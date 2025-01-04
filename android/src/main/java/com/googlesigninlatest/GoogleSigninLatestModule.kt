package com.googlesigninlatest

import android.net.Uri
import androidx.credentials.ClearCredentialStateRequest
import androidx.credentials.CredentialManager
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialRequest
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
    val type = if (config.hasKey("type")) config.getString("type") else "siwg"

    if (webClientId == null) {
      promise.reject("ERROR", "webClientId is required")
      return
    }

//    googleIdOption =

//    getCredRequest = GetCredentialRequest.Builder()
//      .addCredentialOption(
//        GetGoogleIdOption.Builder()
//          .setFilterByAuthorizedAccounts(false)
//          .setServerClientId(webClientId)
//          .setAutoSelectEnabled(false)
//          .build()
//      )
//      .build()

    if (type != null && type == "siwc") {

      getCredRequest = GetCredentialRequest.Builder()
      .addCredentialOption(
        GetGoogleIdOption.Builder()
          .setFilterByAuthorizedAccounts(true)
          .setServerClientId(webClientId)
          .setAutoSelectEnabled(false)
          .build()
      )
      .build()
    } else {
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
    }

    promise.resolve(true)
  }

  @ReactMethod
  fun signUp(promise: Promise) {
    val activity = currentActivity
    if (activity == null) {
      promise.reject("ERROR", "Activity is null")
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
          val tokenCredential = GoogleIdTokenCredential.createFrom(credential.data)

          val data = getUserProperties(tokenCredential)
          promise.resolve(data)

        } else {
          println(tag + "Credential is not GoogleIdTokenCredential")
          promise.reject("ERROR", "Credential is not GoogleIdTokenCredential")
        }

      } catch (e: Exception) {
        e.printStackTrace()
        println(tag + "signIn error: ${e.message}")
        promise.reject("ERROR", e.message ?: "An unknown error occurred")
      }
    }
  }

  @ReactMethod
  fun isSignedIn(
    config: ReadableMap,
    promise: Promise
  ) {

  }

  @ReactMethod
  fun signIn(promise: Promise) {
    val activity = currentActivity
    if (activity == null) {
      promise.reject("ERROR", "Activity is null")
      return
    }
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
          val tokenCredential = GoogleIdTokenCredential.createFrom(credential.data)

          val data = getUserProperties(tokenCredential)
          promise.resolve(data)

        } else {
          println(tag + "Credential is not GoogleIdTokenCredential")
          promise.reject("ERROR", "Credential is not GoogleIdTokenCredential")
        }

      } catch (e: Exception) {
        e.printStackTrace()
        println(tag + "signIn error: ${e.message}")
        promise.reject("ERROR", e.message ?: "An unknown error occurred")
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
        println(tag + "signOut error: ${e.message}")
        promise.reject(e)
      }
    }
  }

  override fun invalidate() {
    super.invalidate()
    myPluginScope.cancel()
  }
}

package com.example.notification

import android.content.Context
import android.util.Log
import com.google.firebase.messaging.FirebaseMessaging

object ImartiFCMHelper {

    private const val TAG = "ImartiFCMHelper"
    private const val PREFS_NAME = "imarti_fcm_prefs"
    private const val KEY_FCM_TOKEN = "fcm_device_token"

    fun saveToken(context: Context, token: String) {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        prefs.edit().putString(KEY_FCM_TOKEN, token).apply()
    }

    fun getCachedToken(context: Context): String? {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        return prefs.getString(KEY_FCM_TOKEN, null)
    }

    fun fetchAndRegisterToken(context: Context, onTokenReady: (String) -> Unit) {
        try {
            FirebaseMessaging.getInstance().token.addOnCompleteListener { task ->
                if (!task.isSuccessful) {
                    Log.w(TAG, "Fetching FCM registration token failed", task.exception)
                    val cached = getCachedToken(context) ?: "fcm_mock_token_for_preview"
                    onTokenReady(cached)
                    return@addOnCompleteListener
                }

                val token = task.result
                Log.d(TAG, "Fetched FCM Token: $token")
                saveToken(context, token)
                onTokenReady(token)

                // Auto subscribe to standard topics
                subscribeToTopics(listOf("building_all", "building_emergency"))
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error obtaining FirebaseMessaging instance: ${e.message}")
            val fallback = getCachedToken(context) ?: "fcm_device_token_demo_${System.currentTimeMillis() % 10000}"
            onTokenReady(fallback)
        }
    }

    fun subscribeToTopics(topics: List<String>) {
        try {
            topics.forEach { topic ->
                FirebaseMessaging.getInstance().subscribeToTopic(topic)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            Log.d(TAG, "Subscribed to FCM topic: $topic")
                        }
                    }
            }
        } catch (e: Exception) {
            Log.w(TAG, "Could not subscribe to topic: ${e.message}")
        }
    }
}

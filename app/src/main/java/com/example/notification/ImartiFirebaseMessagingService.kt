package com.example.notification

import android.util.Log
import com.example.model.NotificationType
import com.example.model.UserRole
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

class ImartiFirebaseMessagingService : FirebaseMessagingService() {

    companion object {
        private const val TAG = "ImartiFCMService"
    }

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        Log.d(TAG, "Refreshed FCM Device Token: $token")
        ImartiFCMHelper.saveToken(this, token)
    }

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)
        Log.d(TAG, "FCM Message received from: ${remoteMessage.from}")

        val data = remoteMessage.data
        val notificationManager = ImartiNotificationManager(applicationContext)

        val typeStr = data["type"] ?: "GENERAL"
        val title = data["title"] ?: remoteMessage.notification?.title ?: "إشعار من تطبيق عمارتي"
        val body = data["body"] ?: data["message"] ?: remoteMessage.notification?.body ?: ""
        val author = data["author"] ?: "مسؤول العمارة"
        val aptNumber = data["apartmentNumber"] ?: ""
        val newStatus = data["status"] ?: ""
        val note = data["note"]

        when (typeStr.uppercase()) {
            "EMERGENCY" -> {
                notificationManager.sendEmergencyAnnouncementNotification(title, body, author)
            }
            "COMPLAINT_UPDATE" -> {
                notificationManager.sendComplaintUpdateNotification(title, newStatus, note, aptNumber)
            }
            "NEW_COMPLAINT" -> {
                notificationManager.sendNewComplaintToManagerNotification(title, aptNumber, author)
            }
            "BILL_UPDATE" -> {
                notificationManager.sendBillProofUploadedNotification(aptNumber, author, data["month"] ?: "أكتوبر")
            }
            else -> {
                remoteMessage.notification?.let { notif ->
                    notificationManager.sendEmergencyAnnouncementNotification(
                        notif.title ?: title,
                        notif.body ?: body,
                        author
                    )
                } ?: run {
                    notificationManager.sendEmergencyAnnouncementNotification(title, body, author)
                }
            }
        }
    }
}

package com.example.notification

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.example.MainActivity
import com.example.R

class ImartiNotificationManager(private val context: Context) {

    companion object {
        const val CHANNEL_EMERGENCY_ID = "channel_imarti_emergency"
        const val CHANNEL_COMPLAINTS_ID = "channel_imarti_complaints"
        const val CHANNEL_GENERAL_ID = "channel_imarti_general"

        private var notificationIdCounter = 1000
    }

    init {
        createNotificationChannels()
    }

    private fun createNotificationChannels() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

            // 1. Emergency Channel (Heads-up, high priority, vibration)
            val emergencyChannel = NotificationChannel(
                CHANNEL_EMERGENCY_ID,
                "الإعلانات الطارئة والعاجلة (Imarti)",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "إشعارات فورية لحالات الطوارئ، تسربات المياه، تعطل المصعد، وإخلاء الممرات"
                enableVibration(true)
                vibrationPattern = longArrayOf(0, 400, 200, 400)
            }

            // 2. Complaints & Maintenance Channel
            val complaintsChannel = NotificationChannel(
                CHANNEL_COMPLAINTS_ID,
                "تحديثات الشكاوى والصيانة (Imarti)",
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = "إشعارات بتغيير حالة الشكاوى، تقارير التقنيين، والملاحظات"
                enableVibration(true)
            }

            // 3. General Channel
            val generalChannel = NotificationChannel(
                CHANNEL_GENERAL_ID,
                "الإعلانات العامة والفواتير (Imarti)",
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = "إشعارات الفواتير، الحجوزات، والتصويتات"
            }

            notificationManager.createNotificationChannels(listOf(emergencyChannel, complaintsChannel, generalChannel))
        }
    }

    private fun getPendingIntent(): PendingIntent {
        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val flags = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        } else {
            PendingIntent.FLAG_UPDATE_CURRENT
        }
        return PendingIntent.getActivity(context, 0, intent, flags)
    }

    fun hasNotificationPermission(): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED
        } else {
            true
        }
    }

    fun sendEmergencyAnnouncementNotification(title: String, content: String, author: String) {
        if (!hasNotificationPermission()) return

        val builder = NotificationCompat.Builder(context, CHANNEL_EMERGENCY_ID)
            .setSmallIcon(android.R.drawable.stat_notify_error)
            .setContentTitle("🚨 إعلان طارئ: $title")
            .setContentText(content)
            .setStyle(
                NotificationCompat.BigTextStyle()
                    .setBigContentTitle("🚨 إعلان طارئ: $title")
                    .bigText("$content\n\nبواسطة: $author")
                    .setSummaryText("عمارة إقامة الأندلس")
            )
            .setPriority(NotificationCompat.PRIORITY_MAX)
            .setCategory(NotificationCompat.CATEGORY_ALARM)
            .setAutoCancel(true)
            .setContentIntent(getPendingIntent())
            .setVibrate(longArrayOf(0, 500, 200, 500))

        with(NotificationManagerCompat.from(context)) {
            try {
                notify(notificationIdCounter++, builder.build())
            } catch (e: SecurityException) {
                // Permission not granted
            }
        }
    }

    fun sendComplaintUpdateNotification(
        complaintTitle: String,
        newStatus: String,
        managerNote: String?,
        aptNumber: String
    ) {
        if (!hasNotificationPermission()) return

        val noteText = if (!managerNote.isNullOrBlank()) "\nملاحظة المسؤول: $managerNote" else ""
        val builder = NotificationCompat.Builder(context, CHANNEL_COMPLAINTS_ID)
            .setSmallIcon(android.R.drawable.stat_notify_sync)
            .setContentTitle("🔧 تحديث شكوى شقة $aptNumber: $newStatus")
            .setContentText(complaintTitle)
            .setStyle(
                NotificationCompat.BigTextStyle()
                    .setBigContentTitle("🔧 تحديث شكوى: $complaintTitle")
                    .bigText("أصبحت حالة الشكوى: $newStatus$noteText")
                    .setSummaryText("متابعة الصيانة")
            )
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setAutoCancel(true)
            .setContentIntent(getPendingIntent())

        with(NotificationManagerCompat.from(context)) {
            try {
                notify(notificationIdCounter++, builder.build())
            } catch (e: SecurityException) {
                // Permission not granted
            }
        }
    }

    fun sendNewComplaintToManagerNotification(
        complaintTitle: String,
        aptNumber: String,
        residentName: String
    ) {
        if (!hasNotificationPermission()) return

        val builder = NotificationCompat.Builder(context, CHANNEL_COMPLAINTS_ID)
            .setSmallIcon(android.R.drawable.stat_sys_warning)
            .setContentTitle("📢 شكوى جديدة من شقة $aptNumber")
            .setContentText("$residentName: $complaintTitle")
            .setStyle(
                NotificationCompat.BigTextStyle()
                    .setBigContentTitle("📢 شكوى جديدة من شقة $aptNumber")
                    .bigText("الساكن $residentName سجّل طلباً جديداً:\n$complaintTitle")
                    .setSummaryText("السنديك ومسؤول العمارة")
            )
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .setContentIntent(getPendingIntent())

        with(NotificationManagerCompat.from(context)) {
            try {
                notify(notificationIdCounter++, builder.build())
            } catch (e: SecurityException) {
                // Permission not granted
            }
        }
    }

    fun sendBillProofUploadedNotification(
        aptNumber: String,
        residentName: String,
        month: String
    ) {
        if (!hasNotificationPermission()) return

        val builder = NotificationCompat.Builder(context, CHANNEL_GENERAL_ID)
            .setSmallIcon(android.R.drawable.stat_notify_more)
            .setContentTitle("📄 إثبات دفع جديد من شقة $aptNumber")
            .setContentText("قام $residentName برفع وصل أداء واجبات شهر $month للمراجعة.")
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setAutoCancel(true)
            .setContentIntent(getPendingIntent())

        with(NotificationManagerCompat.from(context)) {
            try {
                notify(notificationIdCounter++, builder.build())
            } catch (e: SecurityException) {
                // Permission not granted
            }
        }
    }
}

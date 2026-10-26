package com.example.data

import android.content.Context
import android.util.Log
import com.example.model.*
import com.google.firebase.Firebase
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.firestore
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

class FirestoreService(
    private val db: FirebaseFirestore? = resolveFirestore()
) {

    companion object {
        private const val TAG = "FirestoreService"
        private const val BUILDINGS_COLLECTION = "buildings"
        private const val RESIDENTS_COLLECTION = "residents"
        private const val COMPLAINTS_COLLECTION = "complaints"
        private const val ANNOUNCEMENTS_COLLECTION = "announcements"

        private fun resolveFirestore(context: Context? = null): FirebaseFirestore? {
            return try {
                if (context != null) {
                    val resId = context.resources.getIdentifier(
                        "firestore_database_id",
                        "string",
                        context.packageName
                    )
                    if (resId != 0) {
                        val dbId = context.getString(resId)
                        if (dbId.isNotBlank() && dbId != "(default)") {
                            return FirebaseFirestore.getInstance(dbId)
                        }
                    }
                }
                Firebase.firestore
            } catch (e: Exception) {
                Log.w(TAG, "Firebase Firestore not initialized or backend unprovisioned: ${e.message}")
                null
            }
        }
    }

    constructor(context: Context) : this(resolveFirestore(context))

    // Building
    suspend fun saveBuilding(building: Building): Boolean {
        val firestore = db ?: return false
        return try {
            val data = mapOf(
                "id" to building.id,
                "name" to building.name,
                "address" to building.address,
                "city" to building.city,
                "totalApartments" to building.totalApartments,
                "managerId" to building.managerId,
                "managerName" to building.managerName,
                "managerPhone" to building.managerPhone,
                "bankRib" to building.bankRib,
                "isActive" to building.isActive,
                "updatedAt" to FieldValue.serverTimestamp()
            )
            firestore.collection(BUILDINGS_COLLECTION).document(building.id).set(data, SetOptions.merge()).await()
            true
        } catch (e: Exception) {
            Log.e(TAG, "Error saving building: ${e.message}", e)
            false
        }
    }

    // Announcements
    suspend fun saveAnnouncement(buildingId: String, announcement: Announcement): Boolean {
        val firestore = db ?: return false
        return try {
            val data = mapOf(
                "id" to announcement.id,
                "title" to announcement.title,
                "content" to announcement.content,
                "type" to announcement.type.name,
                "date" to announcement.date,
                "startDate" to (announcement.startDate ?: ""),
                "endDate" to (announcement.endDate ?: ""),
                "author" to announcement.author,
                "isPinned" to announcement.isPinned,
                "updatedAt" to FieldValue.serverTimestamp()
            )
            firestore.collection(BUILDINGS_COLLECTION)
                .document(buildingId)
                .collection(ANNOUNCEMENTS_COLLECTION)
                .document(announcement.id)
                .set(data, SetOptions.merge())
                .await()
            true
        } catch (e: Exception) {
            Log.e(TAG, "Error saving announcement: ${e.message}", e)
            false
        }
    }

    // Complaints
    suspend fun saveComplaint(buildingId: String, complaint: Complaint): Boolean {
        val firestore = db ?: return false
        return try {
            val data = mapOf(
                "id" to complaint.id,
                "title" to complaint.title,
                "description" to complaint.description,
                "category" to complaint.category.name,
                "status" to complaint.status.name,
                "apartmentNumber" to complaint.apartmentNumber,
                "residentName" to complaint.residentName,
                "residentPhone" to complaint.residentPhone,
                "date" to complaint.date,
                "photoNote" to (complaint.photoNote ?: ""),
                "managerNote" to (complaint.managerNote ?: ""),
                "updatedAt" to FieldValue.serverTimestamp()
            )
            firestore.collection(BUILDINGS_COLLECTION)
                .document(buildingId)
                .collection(COMPLAINTS_COLLECTION)
                .document(complaint.id)
                .set(data, SetOptions.merge())
                .await()
            true
        } catch (e: Exception) {
            Log.e(TAG, "Error saving complaint: ${e.message}", e)
            false
        }
    }
}

package com.example.model

enum class UserRole(val labelAr: String, val labelEn: String) {
    SUPER_ADMIN("الإدارة العليا (المنصة)", "Super Admin"),
    MANAGER("مسؤول العمارة (السنديك)", "Building Manager"),
    RESIDENT("الساكن (مالك أو مستأجر)", "Resident")
}

enum class ResidentStatus(val labelAr: String) {
    INVITED("مدعو"),
    ACTIVE("مفعّل"),
    SUSPENDED("موقوف")
}

data class ResidentAccount(
    val id: String,
    val name: String,
    val phone: String,
    val apartmentNumber: String,
    val floor: Int,
    val isOwner: Boolean, // true = مالك, false = مستأجر
    val status: ResidentStatus = ResidentStatus.ACTIVE,
    val isPhoneVisibleInDirectory: Boolean = true,
    val monthlyDue: Double = 250.0,
    val lastPaymentDate: String = "2026-10-01"
)

data class Plan(
    val id: String,
    val name: String,
    val maxApartments: Int,
    val monthlyPrice: Double,
    val description: String,
    val hasAnnouncements: Boolean = true,
    val hasComplaints: Boolean = true,
    val hasDirectory: Boolean = true,
    val hasBillsAndReceipts: Boolean = false,
    val hasFacilityBooking: Boolean = false,
    val hasPolls: Boolean = false,
    val hasAdvancedReports: Boolean = false,
    val isCustom: Boolean = false
)

enum class SubscriptionStatus(val labelAr: String) {
    TRIAL("تجربة مجانية (أسبوع)"),
    ACTIVE("نشط"),
    EXPIRING_SOON("ينتهي قريباً"),
    GRACE_PERIOD("مهلة سماح (3 أيام)"),
    READ_ONLY("وضع القراءة فقط"),
    SUSPENDED("موقوف من الإدارة")
}

data class BuildingSubscription(
    val id: String,
    val buildingId: String,
    val managerPhone: String,
    val planId: String,
    val status: SubscriptionStatus,
    val startDate: String,
    val endDate: String,
    val trialUsed: Boolean = false,
    val daysRemaining: Int = 7,
    val inGracePeriod: Boolean = false,
    val notes: String = ""
)

data class PaymentRecord(
    val id: String,
    val buildingId: String,
    val buildingName: String,
    val managerName: String,
    val amount: Double,
    val paymentMethod: String, // تحويل بنكي / نقداً
    val date: String,
    val durationMonths: Int,
    val reference: String,
    val recordedBy: String = "الإدارة العليا"
)

data class Building(
    val id: String,
    val name: String,
    val address: String,
    val city: String,
    val totalApartments: Int,
    val managerId: String,
    val managerName: String,
    val managerPhone: String,
    val bankRib: String = "011 780 0000 1234567890 12",
    val isActive: Boolean = true
)

enum class AnnouncementType(val labelAr: String) {
    EMERGENCY("طارئ وعاجل"),
    TEMPORARY("مؤقت"),
    PERMANENT("دائم وقواعد")
}

data class Announcement(
    val id: String,
    val title: String,
    val content: String,
    val type: AnnouncementType,
    val date: String,
    val startDate: String? = null,
    val endDate: String? = null,
    val author: String,
    val isPinned: Boolean = false,
    val isArchived: Boolean = false
)

enum class ComplaintCategory(val labelAr: String) {
    ELECTRICITY("كهرباء وإنارة"),
    PLUMBING("سباكة ومياه"),
    ELEVATOR("مصعد"),
    CLEANING("نظافة وتطهير"),
    OTHER("أخرى متنوعة")
}

enum class ComplaintStatus(val labelAr: String) {
    NEW("جديد"),
    IN_PROGRESS("قيد المعالجة"),
    COMPLETED("مكتمل")
}

data class Complaint(
    val id: String,
    val title: String,
    val description: String,
    val category: ComplaintCategory,
    val status: ComplaintStatus,
    val apartmentNumber: String,
    val residentName: String,
    val residentPhone: String,
    val date: String,
    val photoNote: String? = null,
    val managerNote: String? = null,
    val updatedAt: String? = null
)

data class BillItem(
    val id: String,
    val apartmentNumber: String,
    val residentName: String,
    val month: String,
    val amount: Double,
    val dueDate: String,
    val description: String,
    val isPaid: Boolean,
    val proofReceiptUploaded: Boolean = false,
    val proofReceiptDate: String? = null,
    val proofReceiptNote: String? = null,
    val confirmedByManager: Boolean = false
)

data class Facility(
    val id: String,
    val name: String,
    val iconKey: String,
    val maxDurationHours: Int,
    val openHours: String,
    val rules: String
)

data class FacilityBooking(
    val id: String,
    val facilityId: String,
    val facilityName: String,
    val apartmentNumber: String,
    val residentName: String,
    val residentPhone: String,
    val date: String,
    val timeSlot: String,
    val status: String = "مؤكد", // مؤكد / ملغى من المسؤول
    val managerCancelReason: String? = null
)

data class PollOption(
    val id: String,
    val title: String,
    val votesCount: Int
)

data class Poll(
    val id: String,
    val question: String,
    val description: String,
    val options: List<PollOption>,
    val votedApartments: Map<String, String> = emptyMap(), // apartmentNumber -> optionId (one vote per apartment)
    val totalVotes: Int = 0,
    val endDate: String,
    val isClosed: Boolean = false
)

data class EmergencyContact(
    val id: String,
    val name: String,
    val phone: String,
    val description: String,
    val iconKey: String
)

data class AuthSession(
    val isLoggedIn: Boolean = false,
    val role: UserRole = UserRole.RESIDENT,
    val phone: String = "",
    val name: String = "",
    val apartmentNumber: String? = null,
    val buildingId: String = "bld-1",
    val isFirstManagerLogin: Boolean = false
)

enum class NotificationType(val labelAr: String) {
    EMERGENCY("إعلان طارئ وعاجل"),
    COMPLAINT_UPDATE("تحديث حالة الشكوى"),
    NEW_COMPLAINT("شكوى جديدة من ساكن"),
    BILL_UPDATE("تحديث الفاتورة"),
    GENERAL("إشعار عام")
}

data class AppNotification(
    val id: String,
    val title: String,
    val message: String,
    val type: NotificationType,
    val timestamp: String,
    val isRead: Boolean = false,
    val targetRole: UserRole? = null,
    val targetApartment: String? = null,
    val relatedEntityId: String? = null
)


package com.example.data

import com.example.model.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.UUID

class ImartiRepository {

    // Current Auth Session (Defaults to Super Admin for oubahimtoto@gmail.com)
    private val _session = MutableStateFlow(
        AuthSession(
            isLoggedIn = true,
            role = UserRole.SUPER_ADMIN,
            phone = "0600000000",
            email = "oubahimtoto@gmail.com",
            name = "الإدارة العليا (أنت)",
            apartmentNumber = null,
            buildingId = "bld-1",
            isFirstManagerLogin = false
        )
    )
    val session: StateFlow<AuthSession> = _session.asStateFlow()

    // Plans
    private val _plans = MutableStateFlow(SampleData.defaultPlans)
    val plans: StateFlow<List<Plan>> = _plans.asStateFlow()

    // Buildings & Subscriptions
    private val _buildings = MutableStateFlow(SampleData.demoBuildings)
    val buildings: StateFlow<List<Building>> = _buildings.asStateFlow()

    private val _subscriptions = MutableStateFlow(SampleData.demoSubscriptions)
    val subscriptions: StateFlow<List<BuildingSubscription>> = _subscriptions.asStateFlow()

    private val _payments = MutableStateFlow(SampleData.demoPayments)
    val payments: StateFlow<List<PaymentRecord>> = _payments.asStateFlow()

    // Residents
    private val _residents = MutableStateFlow(SampleData.demoResidents)
    val residents: StateFlow<List<ResidentAccount>> = _residents.asStateFlow()

    // Announcements
    private val _announcements = MutableStateFlow(SampleData.demoAnnouncements)
    val announcements: StateFlow<List<Announcement>> = _announcements.asStateFlow()

    // Complaints
    private val _complaints = MutableStateFlow(SampleData.demoComplaints)
    val complaints: StateFlow<List<Complaint>> = _complaints.asStateFlow()

    // Bills
    private val _bills = MutableStateFlow(SampleData.demoBills)
    val bills: StateFlow<List<BillItem>> = _bills.asStateFlow()

    // Facilities & Bookings
    private val _facilities = MutableStateFlow(SampleData.demoFacilities)
    val facilities: StateFlow<List<Facility>> = _facilities.asStateFlow()

    private val _bookings = MutableStateFlow(SampleData.demoBookings)
    val bookings: StateFlow<List<FacilityBooking>> = _bookings.asStateFlow()

    // Polls
    private val _polls = MutableStateFlow(SampleData.demoPolls)
    val polls: StateFlow<List<Poll>> = _polls.asStateFlow()

    // Emergency Contacts
    private val _emergencyContacts = MutableStateFlow(SampleData.demoEmergencyContacts)
    val emergencyContacts: StateFlow<List<EmergencyContact>> = _emergencyContacts.asStateFlow()

    // Notifications Center (Push Alerts)
    private val _notifications = MutableStateFlow(SampleData.demoNotifications)
    val notifications: StateFlow<List<AppNotification>> = _notifications.asStateFlow()

    // Subscription Transfer Receipts (Uploaded by Managers)
    private val _subscriptionProofs = MutableStateFlow(SampleData.demoSubscriptionProofs)
    val subscriptionProofs: StateFlow<List<SubscriptionProof>> = _subscriptionProofs.asStateFlow()

    fun pushNotification(
        title: String,
        message: String,
        type: NotificationType,
        targetRole: UserRole? = null,
        targetApartment: String? = null,
        relatedEntityId: String? = null
    ): AppNotification {
        val notif = AppNotification(
            id = "notif-" + UUID.randomUUID().toString().take(6),
            title = title,
            message = message,
            type = type,
            timestamp = "الآن",
            isRead = false,
            targetRole = targetRole,
            targetApartment = targetApartment,
            relatedEntityId = relatedEntityId
        )
        _notifications.value = listOf(notif) + _notifications.value
        return notif
    }

    fun markNotificationAsRead(id: String) {
        _notifications.value = _notifications.value.map {
            if (it.id == id) it.copy(isRead = true) else it
        }
    }

    fun markAllNotificationsAsRead() {
        _notifications.value = _notifications.value.map { it.copy(isRead = true) }
    }

    // Helper: Current Building & Subscription
    fun getCurrentBuilding(): Building {
        val bId = _session.value.buildingId
        return _buildings.value.firstOrNull { it.id == bId } ?: _buildings.value.first()
    }

    fun getCurrentSubscription(): BuildingSubscription {
        val bId = _session.value.buildingId
        return _subscriptions.value.firstOrNull { it.buildingId == bId }
            ?: _subscriptions.value.first()
    }

    fun getCurrentPlan(): Plan {
        val sub = getCurrentSubscription()
        return _plans.value.firstOrNull { it.id == sub.planId } ?: _plans.value.first()
    }

    fun isCurrentBuildingReadOnly(): Boolean {
        val sub = getCurrentSubscription()
        return sub.status == SubscriptionStatus.READ_ONLY || sub.status == SubscriptionStatus.SUSPENDED
    }

    // AUTHENTICATION & LOGIN
    sealed class LoginResult {
        data class Success(val session: AuthSession) : LoginResult()
        data class NotRegistered(val message: String) : LoginResult()
        data class Error(val message: String) : LoginResult()
    }

    fun loginWithPhone(phoneInput: String): LoginResult {
        val cleanInput = phoneInput.trim().lowercase().replace(" ", "").replace("+212", "0")
        
        // 1. Super Admin check (Email or Master Admin Phone)
        if (cleanInput == "oubahimtoto@gmail.com" || cleanInput == "0600000000" || cleanInput == "000000" || cleanInput.contains("oubahimtoto")) {
            val s = AuthSession(
                isLoggedIn = true,
                role = UserRole.SUPER_ADMIN,
                phone = "0600000000",
                email = "oubahimtoto@gmail.com",
                name = "الإدارة العليا (أنت)",
                buildingId = "bld-1",
                isFirstManagerLogin = false
            )
            _session.value = s
            return LoginResult.Success(s)
        }

        // 2. New Manager demo check (needs onboarding/trial)
        if (cleanInput == "0622222222") {
            val s = AuthSession(
                isLoggedIn = true,
                role = UserRole.MANAGER,
                phone = "0622222222",
                name = "يوسف التازي (مسؤول جديد)",
                buildingId = "bld-new",
                isFirstManagerLogin = true
            )
            _session.value = s
            return LoginResult.Success(s)
        }

        // 3. Existing Manager check
        val managerBuilding = _buildings.value.firstOrNull { it.managerPhone.replace(" ", "") == cleanInput }
        if (managerBuilding != null) {
            val sub = _subscriptions.value.firstOrNull { it.buildingId == managerBuilding.id }
            val isFirstTime = sub == null
            val s = AuthSession(
                isLoggedIn = true,
                role = UserRole.MANAGER,
                phone = cleanInput,
                name = managerBuilding.managerName,
                buildingId = managerBuilding.id,
                isFirstManagerLogin = isFirstTime
            )
            _session.value = s
            return LoginResult.Success(s)
        }

        // 4. Resident check
        val resident = _residents.value.firstOrNull { it.phone.replace(" ", "") == cleanInput }
        if (resident != null) {
            val s = AuthSession(
                isLoggedIn = true,
                role = UserRole.RESIDENT,
                phone = cleanInput,
                name = resident.name,
                apartmentNumber = resident.apartmentNumber,
                buildingId = "bld-1",
                isFirstManagerLogin = false
            )
            _session.value = s
            return LoginResult.Success(s)
        }

        // Unregistered
        return LoginResult.NotRegistered("الرقم أو البريد غير مسجّل بالنظام. يُرجى التواصل مع مسؤول عمارتك لإضافتك.")
    }

    fun logout() {
        _session.value = AuthSession(isLoggedIn = false)
    }

    fun switchRoleQuick(role: UserRole) {
        when (role) {
            UserRole.SUPER_ADMIN -> {
                _session.value = AuthSession(
                    isLoggedIn = true,
                    role = UserRole.SUPER_ADMIN,
                    phone = "0600000000",
                    email = "oubahimtoto@gmail.com",
                    name = "الإدارة العليا (أنت)",
                    buildingId = "bld-1"
                )
            }
            UserRole.MANAGER -> {
                _session.value = AuthSession(
                    isLoggedIn = true,
                    role = UserRole.MANAGER,
                    phone = "0611111111",
                    name = "رشيد المنصوري",
                    buildingId = "bld-1",
                    isFirstManagerLogin = false
                )
            }
            UserRole.RESIDENT -> {
                _session.value = AuthSession(
                    isLoggedIn = true,
                    role = UserRole.RESIDENT,
                    phone = "0633333333",
                    name = "محمد السبتي",
                    apartmentNumber = "01",
                    buildingId = "bld-1"
                )
            }
        }
    }

    // ONBOARDING & TRIAL START
    fun startFreeTrial(planId: String, apartmentCount: Int, buildingName: String): Boolean {
        val currentMgrPhone = _session.value.phone
        val existingSub = _subscriptions.value.firstOrNull { it.managerPhone == currentMgrPhone }
        if (existingSub != null && existingSub.trialUsed) {
            return false // Trial only once per manager phone!
        }

        val newBuildingId = "bld-" + UUID.randomUUID().toString().take(6)
        val newBuilding = Building(
            id = newBuildingId,
            name = buildingName.ifBlank { "عمارة $apartmentCount شقة" },
            address = "شارع الحسن الثاني",
            city = "الدار البيضاء",
            totalApartments = apartmentCount,
            managerId = "mgr-" + currentMgrPhone.takeLast(4),
            managerName = _session.value.name,
            managerPhone = currentMgrPhone,
            isActive = true
        )

        val newSub = BuildingSubscription(
            id = "sub-" + UUID.randomUUID().toString().take(6),
            buildingId = newBuildingId,
            managerPhone = currentMgrPhone,
            planId = planId,
            status = SubscriptionStatus.TRIAL,
            startDate = "2026-10-26",
            endDate = "2026-11-02",
            trialUsed = true,
            daysRemaining = 7,
            inGracePeriod = false,
            notes = "تجربة مجانية لمدة أسبوع نشطة"
        )

        _buildings.value = _buildings.value + newBuilding
        _subscriptions.value = _subscriptions.value + newSub

        _session.value = _session.value.copy(
            buildingId = newBuildingId,
            isFirstManagerLogin = false
        )
        return true
    }

    // SUPER ADMIN ACTIONS
    fun addManagerAndBuilding(
        managerName: String,
        managerPhone: String,
        buildingName: String,
        city: String,
        totalApartments: Int,
        planId: String
    ): String {
        val cleanPhone = managerPhone.trim().replace(" ", "")
        val bldId = "bld-" + UUID.randomUUID().toString().take(6)
        val building = Building(
            id = bldId,
            name = buildingName,
            address = "وسط المدينة",
            city = city,
            totalApartments = totalApartments,
            managerId = "mgr-" + cleanPhone.takeLast(4),
            managerName = managerName,
            managerPhone = cleanPhone,
            isActive = true
        )
        val sub = BuildingSubscription(
            id = "sub-" + UUID.randomUUID().toString().take(6),
            buildingId = bldId,
            managerPhone = cleanPhone,
            planId = planId,
            status = SubscriptionStatus.ACTIVE,
            startDate = "2026-10-26",
            endDate = "2026-11-26",
            trialUsed = true,
            daysRemaining = 30,
            inGracePeriod = false,
            notes = "تمت الإضافة من لوحة الإدارة العليا"
        )
        _buildings.value = listOf(building) + _buildings.value
        _subscriptions.value = listOf(sub) + _subscriptions.value
        return bldId
    }

    fun updatePlan(updatedPlan: Plan) {
        _plans.value = _plans.value.map {
            if (it.id == updatedPlan.id) updatedPlan else it
        }
    }

    fun toggleBuildingStatus(buildingId: String, reason: String? = null) {
        _buildings.value = _buildings.value.map { bld ->
            if (bld.id == buildingId) {
                val newActive = !bld.isActive
                // Also reflect in subscription
                _subscriptions.value = _subscriptions.value.map { sub ->
                    if (sub.buildingId == buildingId) {
                        sub.copy(status = if (newActive) SubscriptionStatus.ACTIVE else SubscriptionStatus.SUSPENDED)
                    } else sub
                }
                bld.copy(isActive = newActive, suspensionReason = if (!newActive) (reason ?: "إيقاف إداري مؤقت") else null)
            } else bld
        }
    }

    fun suspendBuilding(buildingId: String, reason: String = "عدم سداد الاشتراك") {
        _buildings.value = _buildings.value.map { bld ->
            if (bld.id == buildingId) {
                bld.copy(isActive = false, suspensionReason = reason)
            } else bld
        }
        _subscriptions.value = _subscriptions.value.map { sub ->
            if (sub.buildingId == buildingId) {
                sub.copy(status = SubscriptionStatus.SUSPENDED, notes = "إيقاف إداري: $reason")
            } else sub
        }
    }

    fun activateBuilding(buildingId: String) {
        _buildings.value = _buildings.value.map { bld ->
            if (bld.id == buildingId) {
                bld.copy(isActive = true, suspensionReason = null)
            } else bld
        }
        _subscriptions.value = _subscriptions.value.map { sub ->
            if (sub.buildingId == buildingId) {
                sub.copy(status = SubscriptionStatus.ACTIVE, notes = "تم التفعيل بنجاح من الإدارة العليا")
            } else sub
        }
    }

    fun recordPayment(
        buildingId: String,
        amount: Double,
        method: String,
        durationMonths: Int,
        reference: String
    ) {
        val building = _buildings.value.firstOrNull { it.id == buildingId } ?: return
        val record = PaymentRecord(
            id = "pay-" + UUID.randomUUID().toString().take(6),
            buildingId = buildingId,
            buildingName = building.name,
            managerName = building.managerName,
            amount = amount,
            paymentMethod = method,
            date = "2026-10-26",
            durationMonths = durationMonths,
            reference = reference.ifBlank { "REC-" + System.currentTimeMillis() % 100000 }
        )
        _payments.value = listOf(record) + _payments.value

        // Renew subscription & restore active status immediately!
        _subscriptions.value = _subscriptions.value.map { sub ->
            if (sub.buildingId == buildingId) {
                sub.copy(
                    status = SubscriptionStatus.ACTIVE,
                    daysRemaining = durationMonths * 30,
                    inGracePeriod = false,
                    notes = "تم تجديد الاشتراك لمدة $durationMonths شهر بنجاح"
                )
            } else sub
        }
    }

    fun updateSubscriptionStatus(subId: String, newStatus: SubscriptionStatus) {
        _subscriptions.value = _subscriptions.value.map { sub ->
            if (sub.id == subId) {
                sub.copy(
                    status = newStatus,
                    inGracePeriod = newStatus == SubscriptionStatus.GRACE_PERIOD
                )
            } else sub
        }
    }

    // Subscription Transfer Proofs (Uploaded by Manager, Verified by Super Admin)
    fun uploadSubscriptionProof(
        buildingId: String,
        amount: Double,
        receiptNote: String
    ): Pair<Boolean, String> {
        val building = _buildings.value.firstOrNull { it.id == buildingId } ?: getCurrentBuilding()
        val proof = SubscriptionProof(
            id = "sub-proof-" + UUID.randomUUID().toString().take(6),
            buildingId = building.id,
            buildingName = building.name,
            managerName = building.managerName,
            managerPhone = building.managerPhone,
            amount = amount,
            date = "اليوم",
            receiptNote = receiptNote.ifBlank { "تم إرفاق إيصال التحويل البنكي للاشتراك" },
            status = ProofStatus.PENDING
        )
        _subscriptionProofs.value = listOf(proof) + _subscriptionProofs.value

        // Notify Super Admin in dashboard
        pushNotification(
            title = "📄 إيصال تحويل اشتراك جديد (بانتظار التحقق)",
            message = "قام المسؤول ${building.managerName} (${building.name}) برفع إيصال تحويل بمبلغ $amount درهم.",
            type = NotificationType.BILL_UPDATE,
            targetRole = UserRole.SUPER_ADMIN,
            relatedEntityId = proof.id
        )

        return Pair(true, "تم رفع إيصال التحويل بنجاح. ستتم مراجعته وتأكيد الاشتراك من الإدارة العليا.")
    }

    fun confirmSubscriptionProof(proofId: String, durationMonths: Int = 1) {
        val proof = _subscriptionProofs.value.firstOrNull { it.id == proofId } ?: return

        // 1. Mark proof as confirmed
        _subscriptionProofs.value = _subscriptionProofs.value.map {
            if (it.id == proofId) it.copy(status = ProofStatus.CONFIRMED, confirmedAt = "اليوم") else it
        }

        // 2. Automatically record offline payment and renew subscription!
        recordPayment(
            buildingId = proof.buildingId,
            amount = proof.amount,
            method = "تحويل بنكي (إيصال مؤكد)",
            durationMonths = durationMonths,
            reference = "VIR-APP-${proof.id.takeLast(5)}"
        )

        // 3. Notify the building manager
        pushNotification(
            title = "✅ تم تأكيد دفعة اشتراككم وتفعيل الحساب",
            message = "تم التحقق من إيصال التحويل البنكي وتفعيل اشتراك ${proof.buildingName} لمدة $durationMonths شهر.",
            type = NotificationType.BILL_UPDATE,
            targetRole = UserRole.MANAGER
        )
    }

    // Manager RIB (Optional & Validated)
    fun updateBuildingRib(buildingId: String, rib: String?): Pair<Boolean, String> {
        if (rib.isNullOrBlank()) {
            _buildings.value = _buildings.value.map {
                if (it.id == buildingId) it.copy(bankRib = null) else it
            }
            return Pair(true, "تم إلغاء الحساب البنكي للعمارة. سيتم إخفاء التحويل البنكي في فواتير السكان.")
        }

        val (isValid, message) = com.example.util.MoroccanRibUtil.validate(rib)
        if (!isValid) {
            return Pair(false, message)
        }

        val formatted = com.example.util.MoroccanRibUtil.format(rib)
        _buildings.value = _buildings.value.map {
            if (it.id == buildingId) it.copy(bankRib = formatted) else it
        }
        val bankName = com.example.util.MoroccanRibUtil.getBankName(rib) ?: "بنك مغربي معتمد"
        return Pair(true, "تم تسجيل وتأكيد رقم RIB بنجاح ($bankName)")
    }

    fun savePlan(plan: Plan) {
        val exists = _plans.value.any { it.id == plan.id }
        if (exists) {
            _plans.value = _plans.value.map { if (it.id == plan.id) plan else it }
        } else {
            _plans.value = _plans.value + plan
        }
    }

    // MANAGER & RESIDENT APARTMENT MANAGEMENT
    fun addResident(
        name: String,
        phone: String,
        apartmentNumber: String,
        floor: Int,
        isOwner: Boolean
    ): Pair<Boolean, String> {
        val currentPlan = getCurrentPlan()
        val currentActiveCount = _residents.value.count { it.status == ResidentStatus.ACTIVE }

        if (currentActiveCount >= currentPlan.maxApartments) {
            return Pair(false, "تم بلوغ الحد الأقصى لباقة ${currentPlan.name} (${currentPlan.maxApartments} شقة). يجب ترقية الباقة لإضافة شقق جديدة.")
        }

        // Check if apartment already has an active resident (single resident per apartment rule)
        val existingInApt = _residents.value.firstOrNull { it.apartmentNumber == apartmentNumber && it.status == ResidentStatus.ACTIVE }
        if (existingInApt != null) {
            return Pair(false, "الشقة رقم $apartmentNumber مسجلة باسم (${existingInApt.name}). يمكن استبدال أو تعديل الساكن الحالي.")
        }

        val newRes = ResidentAccount(
            id = "res-" + UUID.randomUUID().toString().take(6),
            name = name,
            phone = phone.trim().replace(" ", ""),
            apartmentNumber = apartmentNumber,
            floor = floor,
            isOwner = isOwner,
            status = ResidentStatus.INVITED, // Initially invited until first OTP login
            isPhoneVisibleInDirectory = true,
            monthlyDue = 250.0,
            lastPaymentDate = "2026-10-01"
        )
        _residents.value = _residents.value + newRes
        return Pair(true, "تمت دعوة الساكن بنجاح! سيتم تفعيل حسابه تلقائياً عند أول دخول.")
    }

    fun updateResidentStatus(residentId: String, newStatus: ResidentStatus) {
        _residents.value = _residents.value.map {
            if (it.id == residentId) it.copy(status = newStatus) else it
        }
    }

    fun togglePhoneVisibility(residentId: String) {
        _residents.value = _residents.value.map {
            if (it.id == residentId) it.copy(isPhoneVisibleInDirectory = !it.isPhoneVisibleInDirectory) else it
        }
    }

    // ANNOUNCEMENTS
    fun addAnnouncement(
        title: String,
        content: String,
        type: AnnouncementType,
        startDate: String? = null,
        endDate: String? = null
    ): Pair<Boolean, String> {
        if (isCurrentBuildingReadOnly()) {
            return Pair(false, "الاشتراك منتهي وهو الآن في وضع القراءة فقط. يرجى تجديد الاشتراك لإضافة إعلانات.")
        }
        val ann = Announcement(
            id = "ann-" + UUID.randomUUID().toString().take(6),
            title = title,
            content = content,
            type = type,
            date = "اليوم",
            startDate = startDate,
            endDate = endDate,
            author = "مسؤول العمارة",
            isPinned = type == AnnouncementType.EMERGENCY
        )
        _announcements.value = listOf(ann) + _announcements.value

        // If Emergency, trigger instant push notification to all residents & managers!
        if (type == AnnouncementType.EMERGENCY) {
            pushNotification(
                title = "🚨 إعلان طارئ: $title",
                message = content,
                type = NotificationType.EMERGENCY,
                relatedEntityId = ann.id
            )
        }

        return Pair(true, "تم نشر الإعلان بنجاح.")
    }

    fun deleteAnnouncement(id: String) {
        _announcements.value = _announcements.value.filterNot { it.id == id }
    }

    // COMPLAINTS & MAINTENANCE
    fun addComplaint(
        title: String,
        description: String,
        category: ComplaintCategory,
        apartmentNumber: String,
        photoNote: String?
    ): Pair<Boolean, String> {
        if (isCurrentBuildingReadOnly()) {
            return Pair(false, "التطبيق في وضع القراءة فقط لانتهاء الاشتراك. تواصل مع المسؤول.")
        }
        val cmp = Complaint(
            id = "cmp-" + UUID.randomUUID().toString().take(6),
            title = title,
            description = description,
            category = category,
            status = ComplaintStatus.NEW,
            apartmentNumber = apartmentNumber,
            residentName = _session.value.name,
            residentPhone = _session.value.phone,
            date = "اليوم - الآن",
            photoNote = photoNote,
            managerNote = null
        )
        _complaints.value = listOf(cmp) + _complaints.value

        // Trigger push notification to building manager
        pushNotification(
            title = "📢 شكوى جديدة من شقة $apartmentNumber",
            message = "${_session.value.name}: $title",
            type = NotificationType.NEW_COMPLAINT,
            targetRole = UserRole.MANAGER,
            relatedEntityId = cmp.id
        )

        return Pair(true, "تم إرسال طلب الصيانة لمسؤول العمارة بنجاح.")
    }

    fun updateComplaintStatus(complaintId: String, status: ComplaintStatus, managerNote: String?) {
        val target = _complaints.value.firstOrNull { it.id == complaintId }
        _complaints.value = _complaints.value.map {
            if (it.id == complaintId) {
                it.copy(
                    status = status,
                    managerNote = managerNote ?: it.managerNote,
                    updatedAt = "اليوم"
                )
            } else it
        }

        if (target != null) {
            val noteDetails = if (!managerNote.isNullOrBlank()) " ($managerNote)" else ""
            pushNotification(
                title = "🔧 تحديث شكوى: ${target.title}",
                message = "أصبحت حالة الشكوى: ${status.labelAr}$noteDetails",
                type = NotificationType.COMPLAINT_UPDATE,
                targetApartment = target.apartmentNumber,
                relatedEntityId = complaintId
            )
        }
    }

    // BILLS & PROOF OF PAYMENT
    fun addBill(apartmentNumber: String, residentName: String, month: String, amount: Double, desc: String) {
        val bill = BillItem(
            id = "bill-" + UUID.randomUUID().toString().take(6),
            apartmentNumber = apartmentNumber,
            residentName = residentName,
            month = month,
            amount = amount,
            dueDate = "2026-11-10",
            description = desc,
            isPaid = false
        )
        _bills.value = listOf(bill) + _bills.value
    }

    fun uploadProofOfPayment(billId: String, note: String): Pair<Boolean, String> {
        if (isCurrentBuildingReadOnly()) {
            return Pair(false, "التطبيق في وضع القراءة فقط لانتهاء الاشتراك.")
        }
        _bills.value = _bills.value.map { bill ->
            if (bill.id == billId) {
                bill.copy(
                    proofReceiptUploaded = true,
                    proofReceiptDate = "اليوم",
                    proofReceiptNote = note.ifBlank { "تم إرفاق وصل التحويل البنكي" }
                )
            } else bill
        }
        return Pair(true, "تم رفع إثبات الدفع بنجاح. سيقوم المسؤول بمراجعته وتأكيده.")
    }

    fun confirmBillPayment(billId: String) {
        _bills.value = _bills.value.map {
            if (it.id == billId) it.copy(isPaid = true, confirmedByManager = true) else it
        }
    }

    // FACILITY BOOKING
    fun bookFacility(facilityId: String, date: String, timeSlot: String): Pair<Boolean, String> {
        if (isCurrentBuildingReadOnly()) {
            return Pair(false, "التطبيق في وضع القراءة فقط لانتهاء الاشتراك.")
        }
        val fac = _facilities.value.firstOrNull { it.id == facilityId }
            ?: return Pair(false, "المرفق غير موجود.")

        // Conflict check
        val conflict = _bookings.value.any { it.facilityId == facilityId && it.date == date && it.timeSlot == timeSlot && it.status == "مؤكد" }
        if (conflict) {
            return Pair(false, "عذراً، هذا التوقيت محجوز مسبقاً من طرف جار آخر! يُرجى اختيار توقيت آخر.")
        }

        val booking = FacilityBooking(
            id = "bk-" + UUID.randomUUID().toString().take(6),
            facilityId = facilityId,
            facilityName = fac.name,
            apartmentNumber = _session.value.apartmentNumber ?: "01",
            residentName = _session.value.name,
            residentPhone = _session.value.phone,
            date = date,
            timeSlot = timeSlot,
            status = "مؤكد"
        )
        _bookings.value = listOf(booking) + _bookings.value
        return Pair(true, "تم حجز ${fac.name} بنجاح!")
    }

    fun cancelBooking(bookingId: String, reason: String? = null) {
        _bookings.value = _bookings.value.map {
            if (it.id == bookingId) it.copy(status = "ملغى من المسؤول", managerCancelReason = reason ?: "إلغاء لضرورة الصيانة") else it
        }
    }

    // POLLS & VOTING (1 vote per apartment)
    fun voteInPoll(pollId: String, optionId: String, aptNumber: String): Pair<Boolean, String> {
        if (isCurrentBuildingReadOnly()) {
            return Pair(false, "التطبيق في وضع القراءة فقط لانتهاء الاشتراك.")
        }
        val poll = _polls.value.firstOrNull { it.id == pollId }
            ?: return Pair(false, "الاستطلاع غير موجود.")

        if (poll.isClosed) {
            return Pair(false, "هذا الاستطلاع مغلق وانتهت فترة التصويت.")
        }

        if (poll.votedApartments.containsKey(aptNumber)) {
            return Pair(false, "لقد تم تسجيل صوت شقتكم (شقة $aptNumber) مسبقاً في هذا الاستطلاع.")
        }

        val updatedMap = poll.votedApartments + (aptNumber to optionId)
        val updatedOptions = poll.options.map { opt ->
            if (opt.id == optionId) opt.copy(votesCount = opt.votesCount + 1) else opt
        }

        _polls.value = _polls.value.map {
            if (it.id == pollId) {
                it.copy(
                    options = updatedOptions,
                    votedApartments = updatedMap,
                    totalVotes = it.totalVotes + 1
                )
            } else it
        }
        return Pair(true, "تم تسجيل صوت شقة $aptNumber بنجاح! شكراً لمشاركتك.")
    }

    fun addPoll(question: String, desc: String, optionsList: List<String>, endDate: String) {
        val options = optionsList.filter { it.isNotBlank() }.mapIndexed { idx, txt ->
            PollOption("opt-${UUID.randomUUID().toString().take(4)}", txt, 0)
        }
        val poll = Poll(
            id = "poll-" + UUID.randomUUID().toString().take(6),
            question = question,
            description = desc,
            options = options,
            endDate = endDate,
            totalVotes = 0
        )
        _polls.value = listOf(poll) + _polls.value
    }

    // PLAN UPGRADE / DOWNGRADE
    fun changeBuildingPlan(buildingId: String, targetPlanId: String): Pair<Boolean, String> {
        val targetPlan = _plans.value.firstOrNull { it.id == targetPlanId }
            ?: return Pair(false, "الباقة غير موجودة.")

        val activeResidentsCount = _residents.value.count { it.status == ResidentStatus.ACTIVE }

        // Downgrade restriction rule!
        if (activeResidentsCount > targetPlan.maxApartments) {
            return Pair(
                false,
                "لا يمكن التخفيض إلى باقة ${targetPlan.name} لأن عمارتك تضم $activeResidentsCount سكاناً نشطين وحد الباقة هو ${targetPlan.maxApartments} شقة. يجب تقليص عدد السكان أولاً."
            )
        }

        _subscriptions.value = _subscriptions.value.map { sub ->
            if (sub.buildingId == buildingId) {
                sub.copy(planId = targetPlanId, notes = "تمت الترقية إلى باقة ${targetPlan.name}")
            } else sub
        }
        return Pair(true, "تم تغيير الباقة إلى ${targetPlan.name} بنجاح!")
    }
}

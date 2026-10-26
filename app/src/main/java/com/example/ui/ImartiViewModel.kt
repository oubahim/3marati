package com.example.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.ImartiRepository
import com.example.data.SampleData
import com.example.model.*
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class ImartiViewModel(
    val repository: ImartiRepository = ImartiRepository()
) : ViewModel() {

    val session = repository.session
    val plans = repository.plans
    val buildings = repository.buildings
    val subscriptions = repository.subscriptions
    val payments = repository.payments
    val residents = repository.residents
    val announcements = repository.announcements
    val complaints = repository.complaints
    val bills = repository.bills
    val facilities = repository.facilities
    val bookings = repository.bookings
    val polls = repository.polls
    val emergencyContacts = repository.emergencyContacts
    val notifications = repository.notifications

    val unreadNotificationsCount: StateFlow<Int> = notifications.map { list ->
        list.count { !it.isRead }
    }.stateIn(viewModelScope, SharingStarted.Eagerly, 0)

    val subscriptionProofs = repository.subscriptionProofs

    // Language state: ARABIC, FRENCH, ENGLISH
    private val _currentLanguage = MutableStateFlow(com.example.util.AppLanguage.ARABIC)
    val currentLanguage: StateFlow<com.example.util.AppLanguage> = _currentLanguage.asStateFlow()

    // Gentle tip dismissal for RIB
    private val _isRibPromptDismissed = MutableStateFlow(false)
    val isRibPromptDismissed: StateFlow<Boolean> = _isRibPromptDismissed.asStateFlow()

    // Current Navigation Tab (0: Home/Dashboard, 1: Announcements, 2: Complaints, 3: Bills/Facilities, 4: Residents/Settings)
    private val _selectedTab = MutableStateFlow(0)
    val selectedTab: StateFlow<Int> = _selectedTab.asStateFlow()

    // Feedback message (Snackbar/Alert)
    private val _userFeedbackMessage = MutableStateFlow<String?>(null)
    val userFeedbackMessage: StateFlow<String?> = _userFeedbackMessage.asStateFlow()

    // Dialog & Flow States
    private val _showLoginDialog = MutableStateFlow(false)
    val showLoginDialog: StateFlow<Boolean> = _showLoginDialog.asStateFlow()

    private val _showNotificationCenter = MutableStateFlow(false)
    val showNotificationCenter: StateFlow<Boolean> = _showNotificationCenter.asStateFlow()

    private val _showRoleSwitchDialog = MutableStateFlow(false)
    val showRoleSwitchDialog: StateFlow<Boolean> = _showRoleSwitchDialog.asStateFlow()

    private val _showAddManagerDialog = MutableStateFlow(false)
    val showAddManagerDialog: StateFlow<Boolean> = _showAddManagerDialog.asStateFlow()

    private val _showRecordPaymentDialog = MutableStateFlow(false)
    val showRecordPaymentDialog: StateFlow<Boolean> = _showRecordPaymentDialog.asStateFlow()

    private val _showUploadSubProofDialog = MutableStateFlow(false)
    val showUploadSubProofDialog: StateFlow<Boolean> = _showUploadSubProofDialog.asStateFlow()

    private val _showEditRibDialog = MutableStateFlow(false)
    val showEditRibDialog: StateFlow<Boolean> = _showEditRibDialog.asStateFlow()

    private val _showAddResidentDialog = MutableStateFlow(false)
    val showAddResidentDialog: StateFlow<Boolean> = _showAddResidentDialog.asStateFlow()

    private val _showAddAnnouncementDialog = MutableStateFlow(false)
    val showAddAnnouncementDialog: StateFlow<Boolean> = _showAddAnnouncementDialog.asStateFlow()

    private val _showAddComplaintDialog = MutableStateFlow(false)
    val showAddComplaintDialog: StateFlow<Boolean> = _showAddComplaintDialog.asStateFlow()

    private val _showAddPollDialog = MutableStateFlow(false)
    val showAddPollDialog: StateFlow<Boolean> = _showAddPollDialog.asStateFlow()

    private val _selectedFacility = MutableStateFlow<Facility?>(null)
    val selectedFacility: StateFlow<Facility?> = _selectedFacility.asStateFlow()

    private val _selectedBillForProof = MutableStateFlow<BillItem?>(null)
    val selectedBillForProof: StateFlow<BillItem?> = _selectedBillForProof.asStateFlow()

    private val _selectedComplaintForDetail = MutableStateFlow<Complaint?>(null)
    val selectedComplaintForDetail: StateFlow<Complaint?> = _selectedComplaintForDetail.asStateFlow()

    private val _selectedBuildingForPayment = MutableStateFlow<Building?>(null)
    val selectedBuildingForPayment: StateFlow<Building?> = _selectedBuildingForPayment.asStateFlow()

    // Computed Current Building & Subscription
    val currentBuilding: StateFlow<Building> = combine(session, buildings) { s, blds ->
        blds.firstOrNull { it.id == s.buildingId } ?: blds.firstOrNull() ?: SampleData.demoBuildings.first()
    }.stateIn(viewModelScope, SharingStarted.Eagerly, SampleData.demoBuildings.first())

    val currentSubscription: StateFlow<BuildingSubscription> = combine(session, subscriptions) { s, subs ->
        subs.firstOrNull { it.buildingId == s.buildingId } ?: subs.firstOrNull() ?: SampleData.demoSubscriptions.first()
    }.stateIn(viewModelScope, SharingStarted.Eagerly, SampleData.demoSubscriptions.first())

    val currentPlan: StateFlow<Plan> = combine(currentSubscription, plans) { sub, plns ->
        plns.firstOrNull { it.id == sub.planId } ?: plns.firstOrNull() ?: SampleData.defaultPlans.first()
    }.stateIn(viewModelScope, SharingStarted.Eagerly, SampleData.defaultPlans.first())

    val isReadOnly: StateFlow<Boolean> = currentSubscription.map {
        it.status == SubscriptionStatus.READ_ONLY || it.status == SubscriptionStatus.SUSPENDED
    }.stateIn(viewModelScope, SharingStarted.Eagerly, false)

    // Unresolved complaints count
    val openComplaintsCount: StateFlow<Int> = complaints.map { list ->
        list.count { it.status != ComplaintStatus.COMPLETED }
    }.stateIn(viewModelScope, SharingStarted.Eagerly, 0)

    // Unpaid bills count
    val unpaidBillsCount: StateFlow<Int> = bills.map { list ->
        list.count { !it.isPaid }
    }.stateIn(viewModelScope, SharingStarted.Eagerly, 0)

    // Expected Monthly Platform Revenue (Super Admin)
    val totalPlatformRevenue: StateFlow<Double> = payments.map { list ->
        list.sumOf { it.amount }
    }.stateIn(viewModelScope, SharingStarted.Eagerly, 0.0)

    fun selectTab(index: Int) {
        _selectedTab.value = index
    }

    fun showFeedback(msg: String) {
        _userFeedbackMessage.value = msg
    }

    fun clearFeedback() {
        _userFeedbackMessage.value = null
    }

    // AUTH & ROLE
    fun login(phone: String, channel: String): String? {
        val res = repository.loginWithPhone(phone)
        return when (res) {
            is ImartiRepository.LoginResult.Success -> {
                _selectedTab.value = 0
                _showLoginDialog.value = false
                showFeedback("تم تسجيل الدخول بنجاح عبر $channel: ${res.session.name}")
                null
            }
            is ImartiRepository.LoginResult.NotRegistered -> {
                res.message
            }
            is ImartiRepository.LoginResult.Error -> {
                res.message
            }
        }
    }

    fun logout() {
        repository.logout()
        _selectedTab.value = 0
        showFeedback("تم تسجيل الخروج.")
    }

    fun quickSwitchRole(role: UserRole) {
        repository.switchRoleQuick(role)
        _selectedTab.value = 0
        _showRoleSwitchDialog.value = false
        showFeedback("تم التبديل إلى وضع: ${role.labelAr}")
    }

    // ONBOARDING & TRIAL
    fun startTrial(planId: String, apartmentCount: Int, buildingName: String) {
        val success = repository.startFreeTrial(planId, apartmentCount, buildingName)
        if (success) {
            showFeedback("تم تفعيل التجربة المجانية لمدة 7 أيام بنجاح!")
        } else {
            showFeedback("تنبيه: تم استخدام التجربة المجانية سابقاً لهذا الرقم.")
        }
    }

    // SUPER ADMIN ACTIONS
    fun addManagerAndBuilding(
        managerName: String,
        managerPhone: String,
        buildingName: String,
        city: String,
        totalApartments: Int,
        planId: String
    ) {
        repository.addManagerAndBuilding(managerName, managerPhone, buildingName, city, totalApartments, planId)
        _showAddManagerDialog.value = false
        showFeedback("تمت إضافة المسؤول والعمارة وتفعيل الحساب بنجاح!")
    }

    fun toggleBuildingStatus(buildingId: String) {
        repository.toggleBuildingStatus(buildingId)
        showFeedback("تم تحديث حالة العمارة.")
    }

    fun openRecordPayment(building: Building?) {
        _selectedBuildingForPayment.value = building
        _showRecordPaymentDialog.value = true
    }

    fun recordPayment(
        buildingId: String,
        amount: Double,
        method: String,
        durationMonths: Int,
        reference: String
    ) {
        repository.recordPayment(buildingId, amount, method, durationMonths, reference)
        _showRecordPaymentDialog.value = false
        _selectedBuildingForPayment.value = null
        showFeedback("تم تسجيل الدفعة وتمديد الاشتراك بنجاح!")
    }

    fun updateSubscriptionStatus(subId: String, status: SubscriptionStatus) {
        repository.updateSubscriptionStatus(subId, status)
        showFeedback("تم تغيير حالة الاشتراك إلى: ${status.labelAr}")
    }

    // MANAGER ACTIONS
    fun addResident(name: String, phone: String, apt: String, floor: Int, isOwner: Boolean) {
        val (ok, msg) = repository.addResident(name, phone, apt, floor, isOwner)
        showFeedback(msg)
        if (ok) _showAddResidentDialog.value = false
    }

    fun updateResidentStatus(residentId: String, status: ResidentStatus) {
        repository.updateResidentStatus(residentId, status)
        showFeedback("تم تحديث حالة الساكن.")
    }

    fun toggleResidentPhoneVisibility(residentId: String) {
        repository.togglePhoneVisibility(residentId)
        showFeedback("تم تحديث إعداد خصوصية الهاتف في الدليل.")
    }

    fun changePlan(buildingId: String, targetPlanId: String) {
        val (ok, msg) = repository.changeBuildingPlan(buildingId, targetPlanId)
        showFeedback(msg)
    }

    // ANNOUNCEMENTS
    fun addAnnouncement(title: String, content: String, type: AnnouncementType, start: String?, end: String?) {
        val (ok, msg) = repository.addAnnouncement(title, content, type, start, end)
        showFeedback(msg)
        if (ok) _showAddAnnouncementDialog.value = false
    }

    fun deleteAnnouncement(id: String) {
        repository.deleteAnnouncement(id)
        showFeedback("تم حذف الإعلان.")
    }

    // COMPLAINTS
    fun addComplaint(title: String, desc: String, category: ComplaintCategory, apt: String, photoNote: String?) {
        val (ok, msg) = repository.addComplaint(title, desc, category, apt, photoNote)
        showFeedback(msg)
        if (ok) _showAddComplaintDialog.value = false
    }

    fun updateComplaintStatus(id: String, status: ComplaintStatus, note: String?) {
        repository.updateComplaintStatus(id, status, note)
        _selectedComplaintForDetail.value = null
        showFeedback("تم تحديث حالة الطلب وإرسال إشعار للساكن.")
    }

    // BILLS
    fun uploadBillProof(billId: String, note: String) {
        val (ok, msg) = repository.uploadProofOfPayment(billId, note)
        showFeedback(msg)
        _selectedBillForProof.value = null
    }

    fun confirmBill(billId: String) {
        repository.confirmBillPayment(billId)
        showFeedback("تم تأكيد استلام الدفعة بنجاح.")
    }

    // FACILITIES
    fun bookFacility(facilityId: String, date: String, slot: String) {
        val (ok, msg) = repository.bookFacility(facilityId, date, slot)
        showFeedback(msg)
        if (ok) _selectedFacility.value = null
    }

    fun cancelBooking(bookingId: String, reason: String?) {
        repository.cancelBooking(bookingId, reason)
        showFeedback("تم إلغاء الحجز وإشعار الساكن.")
    }

    // POLLS
    fun votePoll(pollId: String, optionId: String, apt: String) {
        val (ok, msg) = repository.voteInPoll(pollId, optionId, apt)
        showFeedback(msg)
    }

    fun addPoll(question: String, desc: String, options: List<String>, endDate: String) {
        repository.addPoll(question, desc, options, endDate)
        _showAddPollDialog.value = false
        showFeedback("تم إنشاء الاستطلاع بنجاح.")
    }

    // Dialog state helpers
    fun setShowLogin(show: Boolean) { _showLoginDialog.value = show }
    fun setShowNotificationCenter(show: Boolean) { _showNotificationCenter.value = show }
    fun setShowRoleSwitch(show: Boolean) { _showRoleSwitchDialog.value = show }
    fun setShowAddManager(show: Boolean) { _showAddManagerDialog.value = show }
    fun setShowRecordPayment(show: Boolean) { _showRecordPaymentDialog.value = show }
    fun setShowAddResident(show: Boolean) { _showAddResidentDialog.value = show }
    fun setShowAddAnnouncement(show: Boolean) { _showAddAnnouncementDialog.value = show }
    fun setShowAddComplaint(show: Boolean) { _showAddComplaintDialog.value = show }
    fun setShowAddPoll(show: Boolean) { _showAddPollDialog.value = show }
    fun setSelectedFacility(fac: Facility?) { _selectedFacility.value = fac }
    fun setSelectedBillForProof(bill: BillItem?) { _selectedBillForProof.value = bill }
    fun setSelectedComplaint(cmp: Complaint?) { _selectedComplaintForDetail.value = cmp }

    fun markNotificationAsRead(id: String) {
        repository.markNotificationAsRead(id)
    }

    fun markAllNotificationsAsRead() {
        repository.markAllNotificationsAsRead()
        showFeedback("تم تعيين جميع الإشعارات كمقروءة.")
    }

    fun dispatchSystemEmergencyNotification(context: android.content.Context, title: String, content: String, author: String) {
        val manager = com.example.notification.ImartiNotificationManager(context)
        manager.sendEmergencyAnnouncementNotification(title, content, author)
    }

    fun dispatchSystemComplaintNotification(
        context: android.content.Context,
        complaintTitle: String,
        newStatus: String,
        note: String?,
        aptNumber: String
    ) {
        val manager = com.example.notification.ImartiNotificationManager(context)
        manager.sendComplaintUpdateNotification(complaintTitle, newStatus, note, aptNumber)
    }

    fun setShowUploadSubProof(show: Boolean) { _showUploadSubProofDialog.value = show }
    fun setShowEditRib(show: Boolean) { _showEditRibDialog.value = show }
    fun dismissRibPrompt() { _isRibPromptDismissed.value = true }

    fun setLanguage(lang: com.example.util.AppLanguage) {
        _currentLanguage.value = lang
        showFeedback(when (lang) {
            com.example.util.AppLanguage.ARABIC -> "تم تغيير لغة التطبيق إلى العربية 🇲🇦"
            com.example.util.AppLanguage.FRENCH -> "Langue changée en Français 🇫🇷"
            com.example.util.AppLanguage.ENGLISH -> "Language changed to English 🇬🇧"
        })
    }

    fun uploadSubscriptionProof(amount: Double, note: String) {
        val (ok, msg) = repository.uploadSubscriptionProof(session.value.buildingId, amount, note)
        showFeedback(msg)
        if (ok) _showUploadSubProofDialog.value = false
    }

    fun confirmSubscriptionProof(proofId: String, durationMonths: Int = 1) {
        repository.confirmSubscriptionProof(proofId, durationMonths)
        showFeedback("تم تأكيد إيصال التحويل البنكي وتفعيل الاشتراك بنجاح!")
    }

    fun updateBuildingRib(rib: String?) {
        val (ok, msg) = repository.updateBuildingRib(session.value.buildingId, rib)
        showFeedback(msg)
        if (ok) _showEditRibDialog.value = false
    }

    fun sendTestPushNotification(context: android.content.Context) {
        val manager = com.example.notification.ImartiNotificationManager(context)
        manager.sendEmergencyAnnouncementNotification(
            "تسرب مياه طارئ عند مدخل المرآب",
            "يرجى توخي الحذر، تم إغلاق المحبس الرئيسي مؤقتاً لحين انتهاء الإصلاح.",
            "مسؤول العمارة (رشيد المنصوري)"
        )
        repository.pushNotification(
            title = "🚨 إشعار تجريبي: تسرب مياه طارئ عند مدخل المرآب",
            message = "تم إرسال هذا الإشعار التجريبي للتأكد من عمل خدمة التنبيهات الفورية (Push Notifications) بنجاح.",
            type = NotificationType.EMERGENCY
        )
        showFeedback("تم إرسال إشعار فوري تجريبي لشريط النظام ومركز الإشعارات ✅")
    }
}

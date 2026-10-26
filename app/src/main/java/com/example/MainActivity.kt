package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.*
import com.example.ui.ImartiViewModel
import com.example.ui.components.*
import com.example.ui.screens.*
import com.example.ui.theme.ImartiTheme
import com.example.util.AppLanguage
import com.example.util.StringsHelper

class MainActivity : ComponentActivity() {

    private val viewModel: ImartiViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val currentLanguage by viewModel.currentLanguage.collectAsState()
            val layoutDir = if (currentLanguage == AppLanguage.ARABIC) LayoutDirection.Rtl else LayoutDirection.Ltr

            ImartiTheme {
                CompositionLocalProvider(LocalLayoutDirection provides layoutDir) {
                    ImartiApp(viewModel = viewModel)
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ImartiApp(viewModel: ImartiViewModel) {
    val selectedTab by viewModel.selectedTab.collectAsState()
    val session by viewModel.session.collectAsState()
    val building by viewModel.currentBuilding.collectAsState()
    val sub by viewModel.currentSubscription.collectAsState()
    val openComplaints by viewModel.openComplaintsCount.collectAsState()
    val feedbackMessage by viewModel.userFeedbackMessage.collectAsState()

    val unreadNotifications by viewModel.unreadNotificationsCount.collectAsState()
    val showNotificationCenter by viewModel.showNotificationCenter.collectAsState()
    val context = androidx.compose.ui.platform.LocalContext.current

    // Dialog collectors
    val currentLanguage by viewModel.currentLanguage.collectAsState()
    val showUploadSubProof by viewModel.showUploadSubProofDialog.collectAsState()
    val showEditRib by viewModel.showEditRibDialog.collectAsState()
    var showLanguageDialog by remember { mutableStateOf(false) }

    val showLogin by viewModel.showLoginDialog.collectAsState()
    val showRoleSwitch by viewModel.showRoleSwitchDialog.collectAsState()
    val showAddManager by viewModel.showAddManagerDialog.collectAsState()
    val showRecordPayment by viewModel.showRecordPaymentDialog.collectAsState()
    val showAddResident by viewModel.showAddResidentDialog.collectAsState()
    val showAddAnnouncement by viewModel.showAddAnnouncementDialog.collectAsState()
    val showAddComplaint by viewModel.showAddComplaintDialog.collectAsState()
    val selectedFacility by viewModel.selectedFacility.collectAsState()
    val selectedBillForProof by viewModel.selectedBillForProof.collectAsState()
    val selectedBuildingForPayment by viewModel.selectedBuildingForPayment.collectAsState()
    val plans by viewModel.plans.collectAsState()
    val buildings by viewModel.buildings.collectAsState()

    // Handle back button smoothly
    BackHandler(
        enabled = selectedTab != 0 || showLogin || showNotificationCenter || showRoleSwitch || showAddManager ||
                showRecordPayment || showAddResident || showAddAnnouncement || showAddComplaint ||
                showUploadSubProof || showEditRib || showLanguageDialog ||
                selectedFacility != null || selectedBillForProof != null
    ) {
        when {
            showLanguageDialog -> showLanguageDialog = false
            showUploadSubProof -> viewModel.setShowUploadSubProof(false)
            showEditRib -> viewModel.setShowEditRib(false)
            showLogin -> viewModel.setShowLogin(false)
            showNotificationCenter -> viewModel.setShowNotificationCenter(false)
            showRoleSwitch -> viewModel.setShowRoleSwitch(false)
            showAddManager -> viewModel.setShowAddManager(false)
            showRecordPayment -> viewModel.setShowRecordPayment(false)
            showAddResident -> viewModel.setShowAddResident(false)
            showAddAnnouncement -> viewModel.setShowAddAnnouncement(false)
            showAddComplaint -> viewModel.setShowAddComplaint(false)
            selectedFacility != null -> viewModel.setSelectedFacility(null)
            selectedBillForProof != null -> viewModel.setSelectedBillForProof(null)
            selectedTab != 0 -> viewModel.selectTab(0)
        }
    }

    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .testTag("imarti_root_scaffold"),
        topBar = {
            TopAppBar(
                title = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(RoundedCornerShape(10.dp))
                                .background(MaterialTheme.colorScheme.primary),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Apartment,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(22.dp)
                            )
                        }
                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = "Imarti",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.primary
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "عمارتي",
                                    style = MaterialTheme.typography.titleSmall,
                                    fontWeight = FontWeight.Normal,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                            Text(
                                text = if (session.role == UserRole.SUPER_ADMIN) "الإدارة العليا (أنت) • oubahimtoto@gmail.com" else building.name,
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.outline,
                                fontSize = 10.sp
                            )
                        }
                    }
                },
                actions = {
                    // Role Switcher / Demo Profile Badge
                    Surface(
                        shape = RoundedCornerShape(16.dp),
                        color = when (session.role) {
                            UserRole.SUPER_ADMIN -> MaterialTheme.colorScheme.tertiaryContainer
                            UserRole.MANAGER -> MaterialTheme.colorScheme.primaryContainer
                            UserRole.RESIDENT -> MaterialTheme.colorScheme.secondaryContainer
                        },
                        modifier = Modifier
                            .clickable { viewModel.setShowRoleSwitch(true) }
                            .padding(end = 4.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = when (session.role) {
                                    UserRole.SUPER_ADMIN -> Icons.Default.Shield
                                    UserRole.MANAGER -> Icons.Default.AdminPanelSettings
                                    UserRole.RESIDENT -> Icons.Default.Person
                                },
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.onSurface,
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = when (session.role) {
                                    UserRole.SUPER_ADMIN -> "الإدارة العليا (أنت)"
                                    UserRole.MANAGER -> "المسؤول"
                                    UserRole.RESIDENT -> "ساكن ${session.apartmentNumber ?: ""}"
                                },
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                fontSize = 11.sp
                            )
                            Icon(
                                imageVector = Icons.Default.ArrowDropDown,
                                contentDescription = "تغيير الوضع",
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }

                    // Push Notifications Bell Icon
                    IconButton(onClick = { viewModel.setShowNotificationCenter(true) }) {
                        BadgedBox(
                            badge = {
                                if (unreadNotifications > 0) {
                                    Badge(
                                        containerColor = MaterialTheme.colorScheme.error,
                                        contentColor = Color.White
                                    ) {
                                        Text("$unreadNotifications")
                                    }
                                }
                            }
                        ) {
                            Icon(
                                imageVector = if (unreadNotifications > 0) Icons.Default.NotificationsActive else Icons.Default.Notifications,
                                contentDescription = "مركز الإشعارات والتنبيهات الفورية",
                                tint = if (unreadNotifications > 0) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    // Language Switcher (العربية / Français / English)
                    IconButton(onClick = { showLanguageDialog = true }) {
                        Surface(
                            shape = CircleShape,
                            color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.8f),
                            modifier = Modifier.size(32.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Text(
                                    text = currentLanguage.flag,
                                    fontSize = 16.sp
                                )
                            }
                        }
                    }

                    // Login Button (OTP & Demo accounts)
                    IconButton(onClick = { viewModel.setShowLogin(true) }) {
                        Icon(
                            imageVector = Icons.Default.LockPerson,
                            contentDescription = "تسجيل الدخول الموحد بـ OTP",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        },
        bottomBar = {
            NavigationBar(
                containerColor = MaterialTheme.colorScheme.surface,
                tonalElevation = 8.dp
            ) {
                if (session.role == UserRole.SUPER_ADMIN) {
                    // Super Admin Exclusive 4 Pillars (فقط)
                    NavigationBarItem(
                        selected = selectedTab == 0,
                        onClick = { viewModel.selectTab(0) },
                        icon = { Icon(Icons.Default.SupervisorAccount, contentDescription = "المسؤولون") },
                        label = { Text("المسؤولون", fontSize = 11.sp, fontWeight = if (selectedTab == 0) FontWeight.Bold else FontWeight.Normal) }
                    )
                    NavigationBarItem(
                        selected = selectedTab == 1,
                        onClick = { viewModel.selectTab(1) },
                        icon = { Icon(Icons.Default.CardMembership, contentDescription = "الباقات والمدفوعات") },
                        label = { Text("الباقات والمدفوعات", fontSize = 11.sp, fontWeight = if (selectedTab == 1) FontWeight.Bold else FontWeight.Normal) }
                    )
                    NavigationBarItem(
                        selected = selectedTab == 2,
                        onClick = { viewModel.selectTab(2) },
                        icon = { Icon(Icons.Default.Analytics, contentDescription = "التقارير") },
                        label = { Text("التقارير", fontSize = 11.sp, fontWeight = if (selectedTab == 2) FontWeight.Bold else FontWeight.Normal) }
                    )
                    NavigationBarItem(
                        selected = selectedTab == 3,
                        onClick = { viewModel.selectTab(3) },
                        icon = { Icon(Icons.Default.PowerSettingsNew, contentDescription = "الإيقاف والتفعيل") },
                        label = { Text("الإيقاف والتفعيل", fontSize = 11.sp, fontWeight = if (selectedTab == 3) FontWeight.Bold else FontWeight.Normal) }
                    )
                } else {
                    // Building syndic & resident tabs
                    NavigationBarItem(
                        selected = selectedTab == 0,
                        onClick = { viewModel.selectTab(0) },
                        icon = { Icon(Icons.Default.Home, contentDescription = "الرئيسية") },
                        label = { Text("الرئيسية", fontSize = 11.sp, fontWeight = if (selectedTab == 0) FontWeight.Bold else FontWeight.Normal) }
                    )
                    NavigationBarItem(
                        selected = selectedTab == 1,
                        onClick = { viewModel.selectTab(1) },
                        icon = { Icon(Icons.Default.Campaign, contentDescription = "الإعلانات") },
                        label = { Text("الإعلانات", fontSize = 11.sp, fontWeight = if (selectedTab == 1) FontWeight.Bold else FontWeight.Normal) }
                    )
                    NavigationBarItem(
                        selected = selectedTab == 2,
                        onClick = { viewModel.selectTab(2) },
                        icon = {
                            BadgedBox(
                                badge = {
                                    if (openComplaints > 0) {
                                        Badge { Text("$openComplaints") }
                                    }
                                }
                            ) {
                                Icon(Icons.Default.Build, contentDescription = "الشكاوى")
                            }
                        },
                        label = { Text("الشكاوى", fontSize = 11.sp, fontWeight = if (selectedTab == 2) FontWeight.Bold else FontWeight.Normal) }
                    )
                    NavigationBarItem(
                        selected = selectedTab == 3,
                        onClick = { viewModel.selectTab(3) },
                        icon = { Icon(Icons.Default.AccountBalanceWallet, contentDescription = "الفواتير والمرافق") },
                        label = { Text("الفواتير والمرافق", fontSize = 11.sp, fontWeight = if (selectedTab == 3) FontWeight.Bold else FontWeight.Normal) }
                    )
                    NavigationBarItem(
                        selected = selectedTab == 4,
                        onClick = { viewModel.selectTab(4) },
                        icon = { Icon(Icons.Default.People, contentDescription = "السكان والدليل") },
                        label = { Text("السكان والدليل", fontSize = 11.sp, fontWeight = if (selectedTab == 4) FontWeight.Bold else FontWeight.Normal) }
                    )
                }
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // Global Feedback Banner
            AnimatedVisibility(visible = feedbackMessage != null) {
                feedbackMessage?.let { msg ->
                    Surface(
                        color = MaterialTheme.colorScheme.primaryContainer,
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { viewModel.clearFeedback() }
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = msg,
                                style = MaterialTheme.typography.bodySmall,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                            Icon(Icons.Default.Close, contentDescription = "إغلاق", modifier = Modifier.size(16.dp))
                        }
                    }
                }
            }

            // Screen Router
            Box(modifier = Modifier.weight(1f)) {
                if (session.role == UserRole.SUPER_ADMIN) {
                    SuperAdminScreen(
                        viewModel = viewModel,
                        activeTab = selectedTab,
                        onTabChanged = { viewModel.selectTab(it) }
                    )
                } else {
                    when (selectedTab) {
                        0 -> DashboardScreen(viewModel = viewModel, onNavigateToTab = { viewModel.selectTab(it) })
                        1 -> AnnouncementsAndWorksScreen(viewModel = viewModel)
                        2 -> IncidentsScreen(viewModel = viewModel)
                        3 -> FinancesScreen(viewModel = viewModel)
                        4 -> ApartmentsScreen(viewModel = viewModel)
                        else -> DashboardScreen(viewModel = viewModel, onNavigateToTab = { viewModel.selectTab(it) })
                    }
                }
            }
        }
    }

    // Dialogs & Modal Sheets
    if (showLogin) {
        LoginDialog(
            onDismiss = { viewModel.setShowLogin(false) },
            onLogin = { phone, channel ->
                viewModel.login(phone, channel)
            }
        )
    }

    if (showRoleSwitch) {
        RoleSwitchDialog(
            currentRole = session.role,
            onRoleSelected = { viewModel.quickSwitchRole(it) },
            onDismiss = { viewModel.setShowRoleSwitch(false) }
        )
    }

    if (showAddManager) {
        AddManagerDialog(
            plans = plans,
            onDismiss = { viewModel.setShowAddManager(false) },
            onConfirm = { name, phone, bldName, city, apts, planId ->
                viewModel.addManagerAndBuilding(name, phone, bldName, city, apts, planId)
            }
        )
    }

    if (showRecordPayment) {
        RecordPaymentDialog(
            building = selectedBuildingForPayment,
            buildings = buildings,
            onDismiss = { viewModel.setShowRecordPayment(false) },
            onConfirm = { bldId, amount, method, months, ref ->
                viewModel.recordPayment(bldId, amount, method, months, ref)
            }
        )
    }

    if (showAddResident) {
        AddResidentDialog(
            onDismiss = { viewModel.setShowAddResident(false) },
            onConfirm = { name, phone, apt, floor, isOwner ->
                viewModel.addResident(name, phone, apt, floor, isOwner)
            }
        )
    }

    if (showNotificationCenter) {
        NotificationCenterDialog(
            viewModel = viewModel,
            onDismiss = { viewModel.setShowNotificationCenter(false) }
        )
    }

    if (showAddAnnouncement) {
        AddAnnouncementDialog(
            onDismiss = { viewModel.setShowAddAnnouncement(false) },
            onConfirm = { title, content, type, start, end ->
                viewModel.addAnnouncement(title, content, type, start, end)
                if (type == AnnouncementType.EMERGENCY) {
                    viewModel.dispatchSystemEmergencyNotification(context, title, content, "مسؤول العمارة")
                }
            }
        )
    }

    if (showAddComplaint) {
        AddComplaintDialog(
            apartmentNumber = session.apartmentNumber ?: "01",
            onDismiss = { viewModel.setShowAddComplaint(false) },
            onConfirm = { title, desc, cat, apt, photo ->
                viewModel.addComplaint(title, desc, cat, apt, photo)
                com.example.notification.ImartiNotificationManager(context)
                    .sendNewComplaintToManagerNotification(title, apt, session.name)
            }
        )
    }

    selectedBillForProof?.let { bill ->
        UploadProofDialog(
            bill = bill,
            onDismiss = { viewModel.setSelectedBillForProof(null) },
            onConfirm = { note ->
                viewModel.uploadBillProof(bill.id, note)
                com.example.notification.ImartiNotificationManager(context)
                    .sendBillProofUploadedNotification(bill.apartmentNumber, session.name, bill.month)
            }
        )
    }

    selectedFacility?.let { fac ->
        BookFacilityDialog(
            facility = fac,
            onDismiss = { viewModel.setSelectedFacility(null) },
            onConfirm = { date, slot ->
                viewModel.bookFacility(fac.id, date, slot)
            }
        )
    }

    if (showUploadSubProof) {
        UploadSubscriptionProofDialog(
            onDismiss = { viewModel.setShowUploadSubProof(false) },
            onConfirm = { amount, _, note ->
                viewModel.uploadSubscriptionProof(amount, note ?: "تحويل بنكي لتجديد الاشتراك")
            }
        )
    }

    if (showEditRib) {
        EditRibDialog(
            initialRib = building.bankRib,
            onDismiss = { viewModel.setShowEditRib(false) },
            onSave = { newRib ->
                viewModel.updateBuildingRib(newRib)
            }
        )
    }

    if (showLanguageDialog) {
        LanguageSelectionDialog(
            currentLang = currentLanguage,
            onDismiss = { showLanguageDialog = false },
            onSelectLanguage = { lang ->
                viewModel.setLanguage(lang)
                showLanguageDialog = false
            }
        )
    }
}

package com.example.ui.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.model.*
import com.example.ui.ImartiViewModel

/**
 * Super Admin Management Console (الإدارة العليا - أنت)
 * الصلاحيات الحصرية الأربع فقط:
 * 1. إضافة المسؤولين
 * 2. إدارة الباقات والاشتراكات والمدفوعات
 * 3. التقارير
 * 4. الإيقاف والتفعيل
 */
@Composable
fun SuperAdminScreen(
    viewModel: ImartiViewModel,
    activeTab: Int = 0,
    onTabChanged: ((Int) -> Unit)? = null
) {
    var internalTab by remember { mutableStateOf(activeTab) }
    val currentTab = onTabChanged?.let { activeTab } ?: internalTab

    val buildings by viewModel.buildings.collectAsState()
    val subscriptions by viewModel.subscriptions.collectAsState()
    val plans by viewModel.plans.collectAsState()
    val payments by viewModel.payments.collectAsState()
    val totalRevenue by viewModel.totalPlatformRevenue.collectAsState()
    val session by viewModel.session.collectAsState()

    var editingPlan by remember { mutableStateOf<Plan?>(null) }
    var suspendingBuilding by remember { mutableStateOf<Building?>(null) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .testTag("super_admin_screen")
    ) {
        // Dedicated Super Admin Header
        Surface(
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 2.dp,
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(38.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(MaterialTheme.colorScheme.primaryContainer),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Shield,
                            contentDescription = "الإدارة العليا",
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(22.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "الإدارة العليا (أنت)",
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Surface(
                                shape = RoundedCornerShape(6.dp),
                                color = MaterialTheme.colorScheme.tertiaryContainer
                            ) {
                                Text(
                                    text = "صلاحيات حصرية فقط",
                                    style = MaterialTheme.typography.labelSmall,
                                    fontSize = 10.sp,
                                    color = MaterialTheme.colorScheme.onTertiaryContainer,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                        }
                        Text(
                            text = if (session.email.isNotBlank()) session.email else "oubahimtoto@gmail.com",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.outline
                        )
                    }
                }
            }
        }

        // The 4 Specific Super Admin Sub-Tabs
        TabRow(
            selectedTabIndex = currentTab.coerceIn(0, 3),
            containerColor = MaterialTheme.colorScheme.surface,
            contentColor = MaterialTheme.colorScheme.primary
        ) {
            Tab(
                selected = currentTab == 0,
                onClick = {
                    if (onTabChanged != null) onTabChanged(0) else internalTab = 0
                },
                text = {
                    Text(
                        "إضافة المسؤولين",
                        fontSize = 11.sp,
                        fontWeight = if (currentTab == 0) FontWeight.Bold else FontWeight.Normal
                    )
                },
                icon = { Icon(Icons.Default.SupervisorAccount, contentDescription = null, modifier = Modifier.size(18.dp)) }
            )
            Tab(
                selected = currentTab == 1,
                onClick = {
                    if (onTabChanged != null) onTabChanged(1) else internalTab = 1
                },
                text = {
                    Text(
                        "الباقات والمدفوعات",
                        fontSize = 11.sp,
                        fontWeight = if (currentTab == 1) FontWeight.Bold else FontWeight.Normal
                    )
                },
                icon = { Icon(Icons.Default.CardMembership, contentDescription = null, modifier = Modifier.size(18.dp)) }
            )
            Tab(
                selected = currentTab == 2,
                onClick = {
                    if (onTabChanged != null) onTabChanged(2) else internalTab = 2
                },
                text = {
                    Text(
                        "التقارير",
                        fontSize = 11.sp,
                        fontWeight = if (currentTab == 2) FontWeight.Bold else FontWeight.Normal
                    )
                },
                icon = { Icon(Icons.Default.Analytics, contentDescription = null, modifier = Modifier.size(18.dp)) }
            )
            Tab(
                selected = currentTab == 3,
                onClick = {
                    if (onTabChanged != null) onTabChanged(3) else internalTab = 3
                },
                text = {
                    Text(
                        "الإيقاف والتفعيل",
                        fontSize = 11.sp,
                        fontWeight = if (currentTab == 3) FontWeight.Bold else FontWeight.Normal
                    )
                },
                icon = { Icon(Icons.Default.PowerSettingsNew, contentDescription = null, modifier = Modifier.size(18.dp)) }
            )
        }

        // Content Area matching the 4 Roles
        when (currentTab) {
            0 -> ManagersAndBuildingsSection(
                buildings = buildings,
                subscriptions = subscriptions,
                plans = plans,
                viewModel = viewModel
            )
            1 -> PlansSubscriptionsAndPaymentsSection(
                plans = plans,
                subscriptions = subscriptions,
                buildings = buildings,
                payments = payments,
                viewModel = viewModel,
                onEditPlan = { editingPlan = it }
            )
            2 -> ReportsSection(
                buildings = buildings,
                subscriptions = subscriptions,
                plans = plans,
                payments = payments,
                totalRevenue = totalRevenue,
                viewModel = viewModel
            )
            3 -> SuspensionAndActivationSection(
                buildings = buildings,
                subscriptions = subscriptions,
                plans = plans,
                viewModel = viewModel,
                onRequestSuspend = { suspendingBuilding = it }
            )
            else -> ManagersAndBuildingsSection(
                buildings = buildings,
                subscriptions = subscriptions,
                plans = plans,
                viewModel = viewModel
            )
        }
    }

    // Modal: Edit Plan Parameters Dialog
    editingPlan?.let { plan ->
        EditPlanDialog(
            plan = plan,
            onDismiss = { editingPlan = null },
            onSave = { updatedPlan ->
                viewModel.updatePlan(updatedPlan)
                editingPlan = null
            }
        )
    }

    // Modal: Suspend Building Confirmation with Reason
    suspendingBuilding?.let { bld ->
        SuspendBuildingDialog(
            building = bld,
            onDismiss = { suspendingBuilding = null },
            onConfirmSuspend = { reason ->
                viewModel.suspendBuilding(bld.id, reason)
                suspendingBuilding = null
            }
        )
    }
}

/**
 * 1. إضافة وتعيين المسؤولين (Add & Manage Managers)
 */
@Composable
fun ManagersAndBuildingsSection(
    buildings: List<Building>,
    subscriptions: List<BuildingSubscription>,
    plans: List<Plan>,
    viewModel: ImartiViewModel
) {
    val context = LocalContext.current
    var searchQuery by remember { mutableStateOf("") }
    val filteredBuildings = if (searchQuery.isBlank()) {
        buildings
    } else {
        buildings.filter {
            it.name.contains(searchQuery, ignoreCase = true) ||
                    it.managerName.contains(searchQuery, ignoreCase = true) ||
                    it.managerPhone.contains(searchQuery) ||
                    it.city.contains(searchQuery, ignoreCase = true)
        }
    }

    Scaffold(
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = { viewModel.setShowAddManager(true) },
                icon = { Icon(Icons.Default.PersonAdd, contentDescription = null) },
                text = { Text("إضافة مسؤول وعمارة") },
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = Color.White
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f))
                ) {
                    Row(
                        modifier = Modifier.padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            Icons.Default.Info,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "إضافة المسؤولين وتفعيل عمائرهم",
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                            Text(
                                text = "تضيف المسؤول (الاسم، الهاتف، العمارة) ← يصله إشعار ورابط التطبيق ← يدخل بـ OTP ← تظهر له الباقات والتجربة المجانية.",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.9f)
                            )
                        }
                    }
                }
            }

            item {
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = { Text("بحث باسم العمارة أو المسؤول أو الهاتف...") },
                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                    trailingIcon = {
                        if (searchQuery.isNotEmpty()) {
                            IconButton(onClick = { searchQuery = "" }) {
                                Icon(Icons.Default.Clear, contentDescription = "مسح")
                            }
                        }
                    },
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp)
                )
            }

            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "قائمة مسؤولي العمارات (${filteredBuildings.size})",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    TextButton(onClick = { viewModel.setShowAddManager(true) }) {
                        Text("+ إضافة جديد")
                    }
                }
            }

            if (filteredBuildings.isEmpty()) {
                item {
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = "لا توجد نتائج مطابقة لبحثك.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.outline,
                            modifier = Modifier.padding(16.dp)
                        )
                    }
                }
            }

            items(filteredBuildings) { bld ->
                val sub = subscriptions.firstOrNull { it.buildingId == bld.id }
                val plan = plans.firstOrNull { it.id == sub?.planId }

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(bld.name, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
                                Text("${bld.city} • ${bld.totalApartments} شقة", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.outline)
                            }
                            Surface(
                                color = if (bld.isActive) Color(0xFFDCFCE7) else Color(0xFFFEE2E2),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Text(
                                    text = if (bld.isActive) "مفعّلة ✅" else "موقوفة ⛔",
                                    color = if (bld.isActive) Color(0xFF16A34A) else Color(0xFFDC2626),
                                    style = MaterialTheme.typography.labelSmall,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Person, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("المسؤول: ${bld.managerName}", style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.SemiBold)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("(${bld.managerPhone})", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.outline)
                        }

                        if (sub != null && plan != null) {
                            Spacer(modifier = Modifier.height(6.dp))
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.CardMembership, contentDescription = null, tint = MaterialTheme.colorScheme.secondary, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("الباقة: ${plan.name} • ${sub.status.labelAr} (متبقي ${sub.daysRemaining} يوم)", style = MaterialTheme.typography.bodySmall, fontSize = 12.sp)
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            // WhatsApp / Share Invite Link
                            OutlinedButton(
                                onClick = {
                                    val inviteMsg = "مرحباً ${bld.managerName}، تم تسجيلك كمسؤول لعمارة ${bld.name} في تطبيق عمارتي. تفضل بالدخول برقم هاتفك (${bld.managerPhone}) عبر رمز التحقق OTP لإكمال الإعداد وتفعيل باقتك."
                                    val sendIntent = Intent().apply {
                                        action = Intent.ACTION_SEND
                                        putExtra(Intent.EXTRA_TEXT, inviteMsg)
                                        type = "text/plain"
                                    }
                                    try {
                                        context.startActivity(Intent.createChooser(sendIntent, "إرسال رابط الدعوة للمسؤول"))
                                    } catch (e: Exception) {
                                        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                                        clipboard.setPrimaryClip(ClipData.newPlainText("دعوة مسؤول", inviteMsg))
                                        viewModel.showFeedback("تم نسخ رسالة الدعوة للحافظة بنجاح!")
                                    }
                                },
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(10.dp)
                            ) {
                                Icon(Icons.Default.Share, contentDescription = null, modifier = Modifier.size(14.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("رابط الدعوة", fontSize = 11.sp)
                            }

                            // Record Payment Action
                            Button(
                                onClick = { viewModel.openRecordPayment(bld) },
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(10.dp)
                            ) {
                                Icon(Icons.Default.Payment, contentDescription = null, modifier = Modifier.size(14.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("تسجيل دفعة", fontSize = 11.sp)
                            }
                        }
                    }
                }
            }
        }
    }
}

/**
 * 2. إدارة الباقات والاشتراكات والمدفوعات
 */
@Composable
fun PlansSubscriptionsAndPaymentsSection(
    plans: List<Plan>,
    subscriptions: List<BuildingSubscription>,
    buildings: List<Building>,
    payments: List<PaymentRecord>,
    viewModel: ImartiViewModel,
    onEditPlan: (Plan) -> Unit
) {
    val proofs by viewModel.subscriptionProofs.collectAsState()
    var subSection by remember { mutableStateOf(0) }
    var proofFilter by remember { mutableStateOf<ProofStatus?>(null) }

    val pendingCount = proofs.count { it.status == ProofStatus.PENDING }
    val filteredProofs = if (proofFilter == null) proofs else proofs.filter { it.status == proofFilter }

    Column(modifier = Modifier.fillMaxSize()) {
        // Inner Tab Bar for Plans, Subscriptions, Transfer Proofs, and Payment Ledger
        TabRow(
            selectedTabIndex = subSection,
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
            contentColor = MaterialTheme.colorScheme.primary
        ) {
            Tab(
                selected = subSection == 0,
                onClick = { subSection = 0 },
                text = { Text("الباقات الشهرية", fontSize = 11.sp, fontWeight = if (subSection == 0) FontWeight.Bold else FontWeight.Normal) }
            )
            Tab(
                selected = subSection == 1,
                onClick = { subSection = 1 },
                text = { Text("الاشتراكات الحالية", fontSize = 11.sp, fontWeight = if (subSection == 1) FontWeight.Bold else FontWeight.Normal) }
            )
            Tab(
                selected = subSection == 2,
                onClick = { subSection = 2 },
                text = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("إيصالات التحويل", fontSize = 11.sp, fontWeight = if (subSection == 2) FontWeight.Bold else FontWeight.Normal)
                        if (pendingCount > 0) {
                            Spacer(modifier = Modifier.width(4.dp))
                            Badge(containerColor = MaterialTheme.colorScheme.error) {
                                Text("$pendingCount", color = Color.White, fontSize = 9.sp)
                            }
                        }
                    }
                }
            )
            Tab(
                selected = subSection == 3,
                onClick = { subSection = 3 },
                text = { Text("سجل الدفعات", fontSize = 11.sp, fontWeight = if (subSection == 3) FontWeight.Bold else FontWeight.Normal) }
            )
        }

        when (subSection) {
            0 -> {
                // Plans Management
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    item {
                        Column {
                            Text(
                                text = "💎 باقات الاشتراك الشهرية للمنصة",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                            Text(
                                text = "تُحدَّد كل باقة بعدد الشقق ومفاتيح تشغيل الميزات. يمكنك تعديل أسعارها وحدودها وميزاتها في أي وقت.",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    items(plans) { plan ->
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column {
                                        Text("باقة ${plan.name}", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
                                        Text("الحد الأقصى: حتى ${plan.maxApartments} شقة", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.outline)
                                    }
                                    Text(
                                        "${plan.monthlyPrice.toInt()} درهم/شهر",
                                        style = MaterialTheme.typography.titleSmall,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.primary
                                    )
                                }

                                Spacer(modifier = Modifier.height(8.dp))
                                Text(plan.description, style = MaterialTheme.typography.bodySmall)

                                Spacer(modifier = Modifier.height(10.dp))
                                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                    AssistChip(
                                        onClick = {},
                                        label = { Text("الإعلانات والدليل", fontSize = 10.sp) },
                                        leadingIcon = { Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(12.dp)) }
                                    )
                                    if (plan.hasBillsAndReceipts) {
                                        AssistChip(
                                            onClick = {},
                                            label = { Text("الفواتير والإيصالات", fontSize = 10.sp) },
                                            leadingIcon = { Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(12.dp)) }
                                        )
                                    }
                                    if (plan.hasFacilityBooking) {
                                        AssistChip(
                                            onClick = {},
                                            label = { Text("حجز المرافق", fontSize = 10.sp) },
                                            leadingIcon = { Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(12.dp)) }
                                        )
                                    }
                                    if (plan.hasPolls) {
                                        AssistChip(
                                            onClick = {},
                                            label = { Text("التصويت", fontSize = 10.sp) },
                                            leadingIcon = { Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(12.dp)) }
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.height(12.dp))
                                OutlinedButton(
                                    onClick = { onEditPlan(plan) },
                                    modifier = Modifier.fillMaxWidth(),
                                    shape = RoundedCornerShape(10.dp)
                                ) {
                                    Icon(Icons.Default.Edit, contentDescription = null, modifier = Modifier.size(14.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("تعديل الباقة (الأسعار، الشقق، الميزات)", fontSize = 12.sp)
                                }
                            }
                        }
                    }
                }
            }

            1 -> {
                // Subscriptions Management
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    item {
                        Text(
                            text = "📑 اشتراكات العمارات وتمديد الصلاحيات",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }

                    items(subscriptions) { sub ->
                        val bld = buildings.firstOrNull { it.id == sub.buildingId }
                        val plan = plans.firstOrNull { it.id == sub.planId }

                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(14.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                        ) {
                            Column(modifier = Modifier.padding(14.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(bld?.name ?: "عمارة", style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold)
                                    Surface(
                                        color = when (sub.status) {
                                            SubscriptionStatus.ACTIVE -> Color(0xFFDCFCE7)
                                            SubscriptionStatus.TRIAL -> Color(0xFFEFF6FF)
                                            SubscriptionStatus.GRACE_PERIOD -> Color(0xFFFEF3C7)
                                            SubscriptionStatus.READ_ONLY -> Color(0xFFF1F5F9)
                                            SubscriptionStatus.SUSPENDED -> Color(0xFFFEE2E2)
                                            else -> Color(0xFFF1F5F9)
                                        },
                                        shape = RoundedCornerShape(8.dp)
                                    ) {
                                        Text(
                                            text = sub.status.labelAr,
                                            style = MaterialTheme.typography.labelSmall,
                                            fontWeight = FontWeight.Bold,
                                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.height(6.dp))
                                Text("الباقة: ${plan?.name ?: ""} • الأيام المتبقية: ${sub.daysRemaining} يوماً", style = MaterialTheme.typography.bodySmall)
                                Text("المسؤول: ${bld?.managerName} (${bld?.managerPhone})", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.outline)
                                if (sub.notes.isNotBlank()) {
                                    Text("ملاحظات: ${sub.notes}", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.outline)
                                }

                                Spacer(modifier = Modifier.height(10.dp))
                                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                    TextButton(onClick = { viewModel.updateSubscriptionStatus(sub.id, SubscriptionStatus.ACTIVE) }) {
                                        Text("تفعيل كامل", fontSize = 11.sp)
                                    }
                                    TextButton(onClick = { viewModel.updateSubscriptionStatus(sub.id, SubscriptionStatus.GRACE_PERIOD) }) {
                                        Text("مهلة سماح (3 أيام)", fontSize = 11.sp)
                                    }
                                    TextButton(onClick = { viewModel.updateSubscriptionStatus(sub.id, SubscriptionStatus.READ_ONLY) }) {
                                        Text("قراءة فقط", fontSize = 11.sp)
                                    }
                                }
                            }
                        }
                    }
                }
            }

            2 -> {
                // Transfer Proofs (RIB Uploads)
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    item {
                        Column {
                            Text(
                                text = "مراجعة وتأكيد إيصالات التحويل البنكي (RIB)",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                            Text(
                                text = "يرفع المسؤولون إيصال التحويل البنكي. عند مطابقة المبلغ يمكنك تأكيد الدفعة وتفعيل الاشتراك فوراً.",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.padding(vertical = 4.dp)
                            )

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                FilterChip(
                                    selected = proofFilter == null,
                                    onClick = { proofFilter = null },
                                    label = { Text("الكل (${proofs.size})", fontSize = 11.sp) }
                                )
                                FilterChip(
                                    selected = proofFilter == ProofStatus.PENDING,
                                    onClick = { proofFilter = ProofStatus.PENDING },
                                    label = { Text("بانتظار التحقق ($pendingCount)", fontSize = 11.sp) },
                                    leadingIcon = {
                                        Icon(Icons.Default.HourglassTop, contentDescription = null, tint = Color(0xFFD97706), modifier = Modifier.size(14.dp))
                                    }
                                )
                                FilterChip(
                                    selected = proofFilter == ProofStatus.CONFIRMED,
                                    onClick = { proofFilter = ProofStatus.CONFIRMED },
                                    label = { Text("مؤكَّدة (${proofs.count { it.status == ProofStatus.CONFIRMED }})", fontSize = 11.sp) },
                                    leadingIcon = {
                                        Icon(Icons.Default.CheckCircle, contentDescription = null, tint = Color(0xFF16A34A), modifier = Modifier.size(14.dp))
                                    }
                                )
                            }
                        }
                    }

                    if (filteredProofs.isEmpty()) {
                        item {
                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text(
                                    text = "لا توجد إيصالات في هذا التصنيف حالياً.",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.outline,
                                    modifier = Modifier.padding(16.dp)
                                )
                            }
                        }
                    }

                    items(filteredProofs) { proof ->
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column {
                                        Text(proof.buildingName, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
                                        Text("المسؤول: ${proof.managerName} (${proof.managerPhone})", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.outline)
                                    }

                                    Surface(
                                        color = if (proof.status == ProofStatus.CONFIRMED) Color(0xFFDCFCE7) else Color(0xFFFEF3C7),
                                        shape = RoundedCornerShape(8.dp)
                                    ) {
                                        Text(
                                            text = if (proof.status == ProofStatus.CONFIRMED) "مؤكَّدة ✅" else "بانتظار التحقق ⏳",
                                            color = if (proof.status == ProofStatus.CONFIRMED) Color(0xFF16A34A) else Color(0xFFD97706),
                                            style = MaterialTheme.typography.labelSmall,
                                            fontWeight = FontWeight.Bold,
                                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.height(10.dp))
                                Text(
                                    text = "${proof.amount.toInt()} درهم (MAD) • تاريخ: ${proof.date}",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.primary
                                )
                                Text("إشعار التحويل: ${proof.receiptNote}", style = MaterialTheme.typography.bodySmall, fontSize = 11.sp)

                                Spacer(modifier = Modifier.height(12.dp))
                                if (proof.status == ProofStatus.PENDING) {
                                    Button(
                                        onClick = { viewModel.confirmSubscriptionProof(proof.id) },
                                        modifier = Modifier.fillMaxWidth(),
                                        shape = RoundedCornerShape(10.dp)
                                    ) {
                                        Icon(Icons.Default.CheckCircle, contentDescription = null, modifier = Modifier.size(16.dp))
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text("تأكيد استلام الدفعة وتفعيل الاشتراك")
                                    }
                                } else {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(Icons.Default.Verified, contentDescription = null, tint = Color(0xFF16A34A), modifier = Modifier.size(16.dp))
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text(
                                            text = "تم التأكيد والتفعيل (${proof.confirmedAt ?: proof.date})",
                                            style = MaterialTheme.typography.labelSmall,
                                            color = Color(0xFF16A34A),
                                            fontWeight = FontWeight.SemiBold
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }

            3 -> {
                // Payment Records (Ledger)
                Scaffold(
                    floatingActionButton = {
                        FloatingActionButton(
                            onClick = { viewModel.openRecordPayment(null) },
                            containerColor = MaterialTheme.colorScheme.primary,
                            contentColor = Color.White
                        ) {
                            Icon(Icons.Default.AddCard, contentDescription = "تسجيل دفعة يدوية")
                        }
                    }
                ) { padding ->
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(padding)
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        item {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "سجل المدفوعات المستلمة خارج التطبيق",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.primary
                                )
                                TextButton(onClick = { viewModel.openRecordPayment(null) }) {
                                    Text("+ تسجيل دفعة")
                                }
                            }
                        }

                        items(payments) { pay ->
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(14.dp),
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                            ) {
                                Row(
                                    modifier = Modifier.padding(14.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(40.dp)
                                            .clip(CircleShape)
                                            .background(Color(0xFFDCFCE7)),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(Icons.Default.Check, contentDescription = null, tint = Color(0xFF16A34A))
                                    }
                                    Spacer(modifier = Modifier.width(12.dp))
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(pay.buildingName, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold)
                                        Text("${pay.paymentMethod} • مرجع: ${pay.reference}", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.outline)
                                        Text("المسؤول: ${pay.managerName} • التمديد: ${pay.durationMonths} شهر", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.outline)
                                    }
                                    Column(horizontalAlignment = Alignment.End) {
                                        Text("${pay.amount.toInt()} درهم", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                                        Text(pay.date, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.outline)
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

/**
 * 3. التقارير والإحصائيات
 */
@Composable
fun ReportsSection(
    buildings: List<Building>,
    subscriptions: List<BuildingSubscription>,
    plans: List<Plan>,
    payments: List<PaymentRecord>,
    totalRevenue: Double,
    viewModel: ImartiViewModel
) {
    val context = LocalContext.current
    val expiringSoon = subscriptions.filter { it.daysRemaining in 1..7 || it.status == SubscriptionStatus.EXPIRING_SOON }
    val endingTrials = subscriptions.filter { it.status == SubscriptionStatus.TRIAL && it.daysRemaining <= 3 }
    val expiredReadOnly = subscriptions.filter { it.status == SubscriptionStatus.READ_ONLY }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // High Level Metrics Grid
        item {
            Text(
                text = "📊 مؤشرات الأداء والدخل المتوقع",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.height(10.dp))

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                AdminMetricCard(
                    title = "إجمالي العمارات",
                    value = "${buildings.size} عمارة",
                    sub = "${buildings.count { it.isActive }} نشطة • ${buildings.count { !it.isActive }} موقوفة",
                    icon = Icons.Default.Apartment,
                    modifier = Modifier.weight(1f)
                )
                AdminMetricCard(
                    title = "المسؤولون المسجلون",
                    value = "${buildings.map { it.managerPhone }.distinct().size} مسؤول",
                    sub = "100% تم التحقق",
                    icon = Icons.Default.SupervisorAccount,
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                AdminMetricCard(
                    title = "الدخل الإجمالي المحصل",
                    value = "${totalRevenue.toInt()} درهم",
                    sub = "مدفوعات خارج التطبيق",
                    icon = Icons.Default.AttachMoney,
                    modifier = Modifier.weight(1f)
                )
                AdminMetricCard(
                    title = "الدخل المتوقع للشهر",
                    value = "${subscriptions.filter { it.status == SubscriptionStatus.ACTIVE }.sumOf { sub -> plans.firstOrNull { it.id == sub.planId }?.monthlyPrice ?: 0.0 }.toInt()} درهم",
                    sub = "حسب الباقات النشطة",
                    icon = Icons.Default.TrendingUp,
                    modifier = Modifier.weight(1f)
                )
            }
        }

        // Subscriptions Health Alert Card
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "🚨 مراقبة الاشتراكات والتجديدات العاجلة",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(10.dp))

                    StatusCountRow("تنتهي خلال 7 أيام القادمة:", "${expiringSoon.size} عمارات", MaterialTheme.colorScheme.tertiary)
                    StatusCountRow("تجارب مجانية قريبة من الانتهاء (3 أيام):", "${endingTrials.size} عمارات", MaterialTheme.colorScheme.error)
                    StatusCountRow("انتهت دون تجديد (وضع القراءة فقط):", "${expiredReadOnly.size} عمارات", MaterialTheme.colorScheme.outline)
                    StatusCountRow("نسبة التحويل من التجربة إلى الاشتراك:", "75% (3 من 4)", MaterialTheme.colorScheme.primary)
                }
            }
        }

        // Action: Export Financial & Platform Report
        item {
            Button(
                onClick = {
                    val reportText = """
                        📋 تقرير منصة عمارتي الشامل (الإدارة العليا)
                        التاريخ: 2026-10-26
                        البريد: oubahimtoto@gmail.com
                        
                        - إجمالي العمارات: ${buildings.size} (${buildings.count { it.isActive }} نشطة، ${buildings.count { !it.isActive }} موقوفة)
                        - إجمالي المسؤولين: ${buildings.map { it.managerPhone }.distinct().size}
                        - الدخل الإجمالي المحصل: ${totalRevenue.toInt()} درهم (MAD)
                        - إجمالي الدفعات المسجلة: ${payments.size} دفعة
                        - الاشتراكات النشطة: ${subscriptions.count { it.status == SubscriptionStatus.ACTIVE }}
                        - تجارب مجانية قيد العمل: ${subscriptions.count { it.status == SubscriptionStatus.TRIAL }}
                    """.trimIndent()

                    val sendIntent = Intent().apply {
                        action = Intent.ACTION_SEND
                        putExtra(Intent.EXTRA_TEXT, reportText)
                        type = "text/plain"
                    }
                    try {
                        context.startActivity(Intent.createChooser(sendIntent, "مشاركة تقرير المنصة"))
                    } catch (e: Exception) {
                        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                        clipboard.setPrimaryClip(ClipData.newPlainText("تقرير المنصة", reportText))
                        viewModel.showFeedback("تم نسخ التقرير المالي والتشغيلي للحافظة بنجاح!")
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
                shape = RoundedCornerShape(12.dp)
            ) {
                Icon(Icons.Default.FileDownload, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("تصدير تقرير المنصة المالي والتشغيلي الشامل")
            }
        }
    }
}

/**
 * 4. الإيقاف والتفعيل (Suspension & Activation Master Center)
 */
@Composable
fun SuspensionAndActivationSection(
    buildings: List<Building>,
    subscriptions: List<BuildingSubscription>,
    plans: List<Plan>,
    viewModel: ImartiViewModel,
    onRequestSuspend: (Building) -> Unit
) {
    var statusFilter by remember { mutableStateOf<Boolean?>(null) }
    val filteredBuildings = if (statusFilter == null) buildings else buildings.filter { it.isActive == statusFilter }

    val activeCount = buildings.count { it.isActive }
    val suspendedCount = buildings.count { !it.isActive }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "⚡ مركز الإيقاف والتفعيل والتحكم بالصلاحيات",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = "بصفتك الإدارة العليا، يمكنك إيقاف أي عمارة ومسؤول مؤقتاً (لعدم السداد أو مخالفة الشروط)، أو تفعيلها فوراً واستعادة كافة الميزات.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(top = 4.dp, bottom = 12.dp)
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        FilterChip(
                            selected = statusFilter == null,
                            onClick = { statusFilter = null },
                            label = { Text("الكل (${buildings.size})", fontSize = 11.sp) }
                        )
                        FilterChip(
                            selected = statusFilter == true,
                            onClick = { statusFilter = true },
                            label = { Text("مفعّلة ($activeCount)", fontSize = 11.sp) },
                            leadingIcon = { Icon(Icons.Default.CheckCircle, contentDescription = null, tint = Color(0xFF16A34A), modifier = Modifier.size(14.dp)) }
                        )
                        FilterChip(
                            selected = statusFilter == false,
                            onClick = { statusFilter = false },
                            label = { Text("موقوفة ($suspendedCount)", fontSize = 11.sp) },
                            leadingIcon = { Icon(Icons.Default.PauseCircle, contentDescription = null, tint = Color(0xFFDC2626), modifier = Modifier.size(14.dp)) }
                        )
                    }
                }
            }
        }

        items(filteredBuildings) { bld ->
            val sub = subscriptions.firstOrNull { it.buildingId == bld.id }
            val plan = plans.firstOrNull { it.id == sub?.planId }

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = if (!bld.isActive) CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(Color(0xFFDC2626))) else null
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(bld.name, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                            Text("${bld.city} • ${bld.totalApartments} شقة • المسؤول: ${bld.managerName}", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.outline)
                        }

                        Switch(
                            checked = bld.isActive,
                            onCheckedChange = { checked ->
                                if (!checked) {
                                    onRequestSuspend(bld)
                                } else {
                                    viewModel.activateBuilding(bld.id)
                                }
                            }
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    if (!bld.isActive) {
                        Surface(
                            color = Color(0xFFFEE2E2),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(Icons.Default.Warning, contentDescription = null, tint = Color(0xFFDC2626), modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "الحالة: موقوفة مؤقتاً • ${bld.suspensionReason ?: "بواسطة الإدارة العليا"}",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = Color(0xFFDC2626),
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    } else {
                        Surface(
                            color = Color(0xFFDCFCE7),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(Icons.Default.CheckCircle, contentDescription = null, tint = Color(0xFF16A34A), modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "الحالة: نشطة ومفعّلة بكامل الميزات (${plan?.name ?: "الباقة"})",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = Color(0xFF16A34A),
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        if (bld.isActive) {
                            OutlinedButton(
                                onClick = { onRequestSuspend(bld) },
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(10.dp),
                                colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFFDC2626))
                            ) {
                                Icon(Icons.Default.Pause, contentDescription = null, modifier = Modifier.size(14.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("إيقاف مؤقت", fontSize = 12.sp)
                            }
                        } else {
                            Button(
                                onClick = { viewModel.activateBuilding(bld.id) },
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(10.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF16A34A))
                            ) {
                                Icon(Icons.Default.PlayArrow, contentDescription = null, modifier = Modifier.size(14.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("إعادة التفعيل الفوري", fontSize = 12.sp)
                            }
                        }

                        OutlinedButton(
                            onClick = {
                                sub?.let { viewModel.updateSubscriptionStatus(it.id, SubscriptionStatus.READ_ONLY) }
                            },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Text("وضع القراءة فقط", fontSize = 12.sp)
                        }
                    }
                }
            }
        }
    }
}

/**
 * Edit Plan Parameters Dialog (Super Admin Exclusive)
 */
@Composable
fun EditPlanDialog(
    plan: Plan,
    onDismiss: () -> Unit,
    onSave: (Plan) -> Unit
) {
    var priceText by remember { mutableStateOf(plan.monthlyPrice.toInt().toString()) }
    var maxAptsText by remember { mutableStateOf(plan.maxApartments.toString()) }
    var hasBills by remember { mutableStateOf(plan.hasBillsAndReceipts) }
    var hasFacilities by remember { mutableStateOf(plan.hasFacilityBooking) }
    var hasPolls by remember { mutableStateOf(plan.hasPolls) }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp)
        ) {
            Column(
                modifier = Modifier
                    .padding(20.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                Text(
                    text = "تعديل إعدادات باقة ${plan.name}",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = priceText,
                    onValueChange = { priceText = it },
                    label = { Text("السعر الشهري (درهم)") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = maxAptsText,
                    onValueChange = { maxAptsText = it },
                    label = { Text("الحد الأقصى لعدد الشقق") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(14.dp))
                Text("مفاتيح تشغيل الميزات:", style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold)

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { hasBills = !hasBills }
                        .padding(vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Checkbox(checked = hasBills, onCheckedChange = { hasBills = it })
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("الفواتير وإيصالات التحويل البنكي")
                }

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { hasFacilities = !hasFacilities }
                        .padding(vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Checkbox(checked = hasFacilities, onCheckedChange = { hasFacilities = it })
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("حجز المرافق المشتركة")
                }

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { hasPolls = !hasPolls }
                        .padding(vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Checkbox(checked = hasPolls, onCheckedChange = { hasPolls = it })
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("التصويت واتخاذ القرارات")
                }

                Spacer(modifier = Modifier.height(16.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedButton(onClick = onDismiss, modifier = Modifier.weight(1f)) {
                        Text("إلغاء")
                    }
                    Button(
                        onClick = {
                            val newPrice = priceText.toDoubleOrNull() ?: plan.monthlyPrice
                            val newMaxApts = maxAptsText.toIntOrNull() ?: plan.maxApartments
                            onSave(
                                plan.copy(
                                    monthlyPrice = newPrice,
                                    maxApartments = newMaxApts,
                                    hasBillsAndReceipts = hasBills,
                                    hasFacilityBooking = hasFacilities,
                                    hasPolls = hasPolls
                                )
                            )
                        },
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("حفظ التغييرات")
                    }
                }
            }
        }
    }
}

/**
 * Suspend Building Confirmation Dialog with Reason
 */
@Composable
fun SuspendBuildingDialog(
    building: Building,
    onDismiss: () -> Unit,
    onConfirmSuspend: (String) -> Unit
) {
    val predefinedReasons = listOf(
        "عدم سداد الاشتراك الشهري",
        "تجاوز عدد الشقق المسموح به في الباقة",
        "صيانة مؤقتة وتحديث بيانات",
        "مخالفة شروط الاستخدام",
        "بناء على طلب مسؤول العمارة"
    )
    var selectedReason by remember { mutableStateOf(predefinedReasons.first()) }
    var customReason by remember { mutableStateOf("") }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp)
        ) {
            Column(
                modifier = Modifier
                    .padding(20.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Warning, contentDescription = null, tint = Color(0xFFDC2626))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "إيقاف عمارة ${building.name}",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFFDC2626)
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "سيؤدي الإيقاف إلى تجميد ميزات العمارة فوراً وإشعار المسؤول. يمكنك إعادة تفعيلها في أي وقت.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(12.dp))

                Text("اختر سبب الإيقاف:", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold)
                predefinedReasons.forEach { reason ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { selectedReason = reason }
                            .padding(vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(selected = selectedReason == reason, onClick = { selectedReason = reason })
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(reason, style = MaterialTheme.typography.bodySmall)
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = customReason,
                    onValueChange = { customReason = it },
                    label = { Text("سبب إضافي أو مخصص (اختياري)") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(16.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedButton(onClick = onDismiss, modifier = Modifier.weight(1f)) {
                        Text("إلغاء")
                    }
                    Button(
                        onClick = {
                            val finalReason = if (customReason.isNotBlank()) customReason else selectedReason
                            onConfirmSuspend(finalReason)
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFDC2626)),
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("تأكيد الإيقاف")
                    }
                }
            }
        }
    }
}

@Composable
fun StatusCountRow(label: String, count: String, color: Color) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(label, style = MaterialTheme.typography.bodySmall)
        Surface(
            color = color.copy(alpha = 0.15f),
            shape = RoundedCornerShape(6.dp)
        ) {
            Text(
                text = count,
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.Bold,
                color = color,
                modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
            )
        }
    }
}

@Composable
fun AdminMetricCard(title: String, value: String, sub: String, icon: androidx.compose.ui.graphics.vector.ImageVector, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(icon, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(20.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text(title, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(value, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
            Text(sub, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.outline, fontSize = 10.sp)
        }
    }
}

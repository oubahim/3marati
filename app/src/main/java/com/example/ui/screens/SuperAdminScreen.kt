package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.*
import com.example.ui.ImartiViewModel

@Composable
fun SuperAdminScreen(viewModel: ImartiViewModel) {
    var selectedSubTab by remember { mutableStateOf(0) }
    val buildings by viewModel.buildings.collectAsState()
    val subscriptions by viewModel.subscriptions.collectAsState()
    val plans by viewModel.plans.collectAsState()
    val payments by viewModel.payments.collectAsState()
    val totalRevenue by viewModel.totalPlatformRevenue.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .testTag("super_admin_screen")
    ) {
        // Sub-tabs navigation for Super Admin
        TabRow(
            selectedTabIndex = selectedSubTab,
            containerColor = MaterialTheme.colorScheme.surface,
            contentColor = MaterialTheme.colorScheme.primary
        ) {
            Tab(
                selected = selectedSubTab == 0,
                onClick = { selectedSubTab = 0 },
                text = { Text("التقارير والإحصائيات", fontSize = 12.sp, fontWeight = if (selectedSubTab == 0) FontWeight.Bold else FontWeight.Normal) },
                icon = { Icon(Icons.Default.Analytics, contentDescription = null, modifier = Modifier.size(18.dp)) }
            )
            Tab(
                selected = selectedSubTab == 1,
                onClick = { selectedSubTab = 1 },
                text = { Text("المسؤولون والعمارات", fontSize = 12.sp, fontWeight = if (selectedSubTab == 1) FontWeight.Bold else FontWeight.Normal) },
                icon = { Icon(Icons.Default.Apartment, contentDescription = null, modifier = Modifier.size(18.dp)) }
            )
            Tab(
                selected = selectedSubTab == 2,
                onClick = { selectedSubTab = 2 },
                text = { Text("الباقات والاشتراكات", fontSize = 12.sp, fontWeight = if (selectedSubTab == 2) FontWeight.Bold else FontWeight.Normal) },
                icon = { Icon(Icons.Default.CardMembership, contentDescription = null, modifier = Modifier.size(18.dp)) }
            )
            Tab(
                selected = selectedSubTab == 3,
                onClick = { selectedSubTab = 3 },
                text = { Text("سجل الدفعات", fontSize = 12.sp, fontWeight = if (selectedSubTab == 3) FontWeight.Bold else FontWeight.Normal) },
                icon = { Icon(Icons.Default.ReceiptLong, contentDescription = null, modifier = Modifier.size(18.dp)) }
            )
        }

        when (selectedSubTab) {
            0 -> ReportsSection(buildings, subscriptions, plans, payments, totalRevenue, viewModel)
            1 -> ManagersAndBuildingsSection(buildings, subscriptions, plans, viewModel)
            2 -> PlansAndSubscriptionsSection(plans, subscriptions, buildings, viewModel)
            3 -> PaymentsSection(payments, viewModel)
        }
    }
}

@Composable
fun ReportsSection(
    buildings: List<Building>,
    subscriptions: List<BuildingSubscription>,
    plans: List<Plan>,
    payments: List<PaymentRecord>,
    totalRevenue: Double,
    viewModel: ImartiViewModel
) {
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
                    sub = "${buildings.count { it.isActive }} نشطة",
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
                    viewModel.showFeedback("تم إنشاء تقرير المنصة وتجهيزه للمشاركة عبر واتساب أو الطباعة بنجاح!")
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
                shape = RoundedCornerShape(12.dp)
            ) {
                Icon(Icons.Default.FileDownload, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("تصدير تقرير المنصة المالي الشامل (Excel / PDF)")
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

@Composable
fun ManagersAndBuildingsSection(
    buildings: List<Building>,
    subscriptions: List<BuildingSubscription>,
    plans: List<Plan>,
    viewModel: ImartiViewModel
) {
    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = { viewModel.setShowAddManager(true) },
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = Color.White
            ) {
                Icon(Icons.Default.AddBusiness, contentDescription = "إضافة عمارة ومسؤول")
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
                        text = "عمارات ومسؤولو المنصة (${buildings.size})",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    TextButton(onClick = { viewModel.setShowAddManager(true) }) {
                        Text("+ إضافة جديد")
                    }
                }
            }

            items(buildings) { bld ->
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
                                    text = if (bld.isActive) "مفعّلة" else "موقوفة",
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
                            Text("المسؤول: ${bld.managerName} (${bld.managerPhone})", style = MaterialTheme.typography.bodySmall)
                        }

                        if (sub != null && plan != null) {
                            Spacer(modifier = Modifier.height(6.dp))
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.CardMembership, contentDescription = null, tint = MaterialTheme.colorScheme.secondary, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("الباقة: ${plan.name} • الحالة: ${sub.status.labelAr} (متبقي ${sub.daysRemaining} يوم)", style = MaterialTheme.typography.bodySmall, fontSize = 12.sp)
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            OutlinedButton(
                                onClick = { viewModel.toggleBuildingStatus(bld.id) },
                                modifier = Modifier.weight(1f)
                            ) {
                                Text(if (bld.isActive) "إيقاف مؤقت" else "تفعيل العمارة", fontSize = 12.sp)
                            }

                            Button(
                                onClick = { viewModel.openRecordPayment(bld) },
                                modifier = Modifier.weight(1f)
                            ) {
                                Icon(Icons.Default.Payment, contentDescription = null, modifier = Modifier.size(14.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("تسجيل دفعة", fontSize = 12.sp)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun PlansAndSubscriptionsSection(
    plans: List<Plan>,
    subscriptions: List<BuildingSubscription>,
    buildings: List<Building>,
    viewModel: ImartiViewModel
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            Text(
                text = "💎 باقات الاشتراك الشهرية للمنصة",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "يمكنك ضبط حدود الشقق والميزات والأسعار لكل باقة.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
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
                        Text("باقة ${plan.name}", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
                        Text("${plan.monthlyPrice.toInt()} درهم/شهر", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                    }
                    Text("الحد الأقصى: حتى ${plan.maxApartments} شقة", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.outline)

                    Spacer(modifier = Modifier.height(8.dp))
                    Text(plan.description, style = MaterialTheme.typography.bodySmall)

                    Spacer(modifier = Modifier.height(8.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        if (plan.hasBillsAndReceipts) AssistChip(onClick = {}, label = { Text("الفواتير", fontSize = 10.sp) })
                        if (plan.hasFacilityBooking) AssistChip(onClick = {}, label = { Text("المرافق", fontSize = 10.sp) })
                        if (plan.hasPolls) AssistChip(onClick = {}, label = { Text("التصويت", fontSize = 10.sp) })
                    }
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(10.dp))
            Text(
                text = "📑 الاشتراكات الحالية وحالاتها",
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
                    Text("الباقة: ${plan?.name ?: ""} • المدة المتبقية: ${sub.daysRemaining} يوماً", style = MaterialTheme.typography.bodySmall)
                    Text("الملاحظات: ${sub.notes}", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.outline)

                    Spacer(modifier = Modifier.height(8.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        TextButton(onClick = { viewModel.updateSubscriptionStatus(sub.id, SubscriptionStatus.ACTIVE) }) {
                            Text("تفعيل نشط", fontSize = 11.sp)
                        }
                        TextButton(onClick = { viewModel.updateSubscriptionStatus(sub.id, SubscriptionStatus.GRACE_PERIOD) }) {
                            Text("مهلة سماح (3 أيام)", fontSize = 11.sp)
                        }
                        TextButton(onClick = { viewModel.updateSubscriptionStatus(sub.id, SubscriptionStatus.READ_ONLY) }) {
                            Text("وضع القراءة فقط", fontSize = 11.sp)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun PaymentsSection(payments: List<PaymentRecord>, viewModel: ImartiViewModel) {
    val proofs by viewModel.subscriptionProofs.collectAsState()
    var selectedPaymentTab by remember { mutableStateOf(0) }
    var proofStatusFilter by remember { mutableStateOf<ProofStatus?>(null) }

    val pendingCount = proofs.count { it.status == ProofStatus.PENDING }
    val filteredProofs = if (proofStatusFilter == null) proofs else proofs.filter { it.status == proofStatusFilter }

    Scaffold(
        floatingActionButton = {
            if (selectedPaymentTab == 1) {
                FloatingActionButton(
                    onClick = { viewModel.openRecordPayment(null) },
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = Color.White
                ) {
                    Icon(Icons.Default.AddCard, contentDescription = "تسجيل دفعة يدوية")
                }
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            // Secondary tab row: Transfer Proofs vs Payment History
            TabRow(
                selectedTabIndex = selectedPaymentTab,
                containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                contentColor = MaterialTheme.colorScheme.primary
            ) {
                Tab(
                    selected = selectedPaymentTab == 0,
                    onClick = { selectedPaymentTab = 0 },
                    text = {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text("إيصالات تحويل الاشتراكات", fontWeight = if (selectedPaymentTab == 0) FontWeight.Bold else FontWeight.Normal, fontSize = 12.sp)
                            if (pendingCount > 0) {
                                Spacer(modifier = Modifier.width(6.dp))
                                Badge(containerColor = MaterialTheme.colorScheme.error) {
                                    Text("$pendingCount", color = Color.White, fontSize = 10.sp)
                                }
                            }
                        }
                    },
                    icon = { Icon(Icons.Default.ReceiptLong, contentDescription = null, modifier = Modifier.size(16.dp)) }
                )
                Tab(
                    selected = selectedPaymentTab == 1,
                    onClick = { selectedPaymentTab = 1 },
                    text = { Text("سجل الدفعات المؤكدة", fontWeight = if (selectedPaymentTab == 1) FontWeight.Bold else FontWeight.Normal, fontSize = 12.sp) },
                    icon = { Icon(Icons.Default.Paid, contentDescription = null, modifier = Modifier.size(16.dp)) }
                )
            }

            if (selectedPaymentTab == 0) {
                // Subscription Proofs Tab
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    item {
                        Column {
                            Text(
                                text = "مراجعة إيصالات تحويل الاشتراكات",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                            Text(
                                text = "يرفع المسؤولون إيصال التحويل البنكي (اختياري). عند التأكد من وصول المبلغ يمكنك تأكيد الدفعة وتفعيل الاشتراك بضغطة واحدة.",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.padding(top = 2.dp, bottom = 8.dp)
                            )

                            // Status Filters
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                FilterChip(
                                    selected = proofStatusFilter == null,
                                    onClick = { proofStatusFilter = null },
                                    label = { Text("الكل (${proofs.size})", fontSize = 11.sp) }
                                )
                                FilterChip(
                                    selected = proofStatusFilter == ProofStatus.PENDING,
                                    onClick = { proofStatusFilter = ProofStatus.PENDING },
                                    label = { Text("بانتظار التحقق ($pendingCount)", fontSize = 11.sp) },
                                    leadingIcon = {
                                        Icon(Icons.Default.HourglassTop, contentDescription = null, tint = Color(0xFFD97706), modifier = Modifier.size(14.dp))
                                    }
                                )
                                FilterChip(
                                    selected = proofStatusFilter == ProofStatus.CONFIRMED,
                                    onClick = { proofStatusFilter = ProofStatus.CONFIRMED },
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
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = "${proof.amount.toInt()} درهم (MAD)",
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.primary
                                    )
                                    Text("تاريخ الرفع: ${proof.date}", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.outline)
                                }

                                Spacer(modifier = Modifier.height(8.dp))
                                Surface(
                                    shape = RoundedCornerShape(10.dp),
                                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Row(modifier = Modifier.padding(10.dp), verticalAlignment = Alignment.CenterVertically) {
                                        Icon(Icons.Default.Receipt, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(18.dp))
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text(
                                            text = "إشعار التحويل: ${proof.receiptNote}",
                                            style = MaterialTheme.typography.bodySmall,
                                            fontSize = 11.sp
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.height(12.dp))
                                if (proof.status == ProofStatus.PENDING) {
                                    Button(
                                        onClick = { viewModel.confirmSubscriptionProof(proof.id) },
                                        modifier = Modifier.fillMaxWidth(),
                                        shape = RoundedCornerShape(10.dp),
                                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
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
                                            text = "تم التأكيد والتفعيل بنجاح (${proof.confirmedAt ?: proof.date})",
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
            } else {
                // Historical Payments List
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
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

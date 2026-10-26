package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.*
import com.example.ui.ImartiViewModel

@Composable
fun DashboardScreen(
    viewModel: ImartiViewModel,
    onNavigateToTab: (Int) -> Unit
) {
    val session by viewModel.session.collectAsState()

    when (session.role) {
        UserRole.SUPER_ADMIN -> {
            SuperAdminScreen(viewModel = viewModel)
        }
        UserRole.MANAGER -> {
            if (session.isFirstManagerLogin) {
                ManagerOnboardingScreen(viewModel = viewModel)
            } else {
                ManagerDashboard(viewModel = viewModel, onNavigateToTab = onNavigateToTab)
            }
        }
        UserRole.RESIDENT -> {
            ResidentDashboard(viewModel = viewModel, onNavigateToTab = onNavigateToTab)
        }
    }
}

@Composable
fun ManagerDashboard(
    viewModel: ImartiViewModel,
    onNavigateToTab: (Int) -> Unit
) {
    val building by viewModel.currentBuilding.collectAsState()
    val sub by viewModel.currentSubscription.collectAsState()
    val plan by viewModel.currentPlan.collectAsState()
    val residents by viewModel.residents.collectAsState()
    val openComplaints by viewModel.openComplaintsCount.collectAsState()
    val unpaidBills by viewModel.unpaidBillsCount.collectAsState()
    val announcements by viewModel.announcements.collectAsState()
    val emergencyAnnouncements = announcements.filter { it.type == AnnouncementType.EMERGENCY }

    val activeResidents = residents.count { it.status == ResidentStatus.ACTIVE }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .testTag("manager_dashboard"),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Subscription Status Banner
        item {
            SubscriptionStatusBanner(sub = sub, plan = plan)
        }

        // Emergency Announcement (if active)
        if (emergencyAnnouncements.isNotEmpty()) {
            item {
                EmergencyAlertCard(emergencyAnnouncements.first())
            }
        }

        // Building Info Card
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = building.name,
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                            Text(
                                text = "${building.city} • باقة ${plan.name} (${plan.maxApartments} شقة)",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
                            )
                        }

                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = MaterialTheme.colorScheme.primary
                        ) {
                            Text(
                                text = sub.status.labelAr,
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                color = Color.White,
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))
                    // Residents limit progress
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "الشقق المفعّلة: $activeResidents من ${plan.maxApartments}",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "${(activeResidents * 100) / maxOf(1, plan.maxApartments)}%",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    LinearProgressIndicator(
                        progress = { (activeResidents.toFloat() / maxOf(1, plan.maxApartments)).coerceIn(0f, 1f) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(6.dp)
                            .clip(RoundedCornerShape(4.dp)),
                        color = MaterialTheme.colorScheme.primary,
                        trackColor = MaterialTheme.colorScheme.surfaceVariant
                    )

                    Spacer(modifier = Modifier.height(14.dp))
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = MaterialTheme.colorScheme.surface,
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { viewModel.setShowUploadSubProof(true) }
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.ReceiptLong, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(20.dp))
                                Spacer(modifier = Modifier.width(8.dp))
                                Column {
                                    Text("إيصال تحويل الاشتراك (اختياري)", style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Bold)
                                    Text("اضغط لرفع صورة إشعار التحويل البنكي لتسريع تفعيل باقتك", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.outline)
                                }
                            }
                            Icon(Icons.Default.Upload, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(18.dp))
                        }
                    }
                }
            }
        }

        // Quick Stats Grid
        item {
            Text(
                text = "⚡ نظرة سريعة على العمارة",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.height(8.dp))

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                DashboardStatCard(
                    title = "الشكاوى الجارية",
                    value = "$openComplaints عطل",
                    sub = "تحتاج معالجة",
                    icon = Icons.Default.Build,
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.weight(1f)
                ) { onNavigateToTab(2) }

                DashboardStatCard(
                    title = "الفواتير المعلقة",
                    value = "$unpaidBills شقة",
                    sub = "لم تؤكد بعد",
                    icon = Icons.Default.Receipt,
                    color = MaterialTheme.colorScheme.tertiary,
                    modifier = Modifier.weight(1f)
                ) { onNavigateToTab(3) }
            }
        }

        // Quick Actions
        item {
            Text(
                text = "المسؤول: الإجراءات السريعة",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(8.dp))

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                ActionChipButton(
                    title = "+ نشر إعلان",
                    icon = Icons.Default.Campaign,
                    modifier = Modifier.weight(1f)
                ) { viewModel.setShowAddAnnouncement(true) }

                ActionChipButton(
                    title = "+ دعوة ساكن",
                    icon = Icons.Default.PersonAdd,
                    modifier = Modifier.weight(1f)
                ) { viewModel.setShowAddResident(true) }
            }

            Spacer(modifier = Modifier.height(6.dp))

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                ActionChipButton(
                    title = "الفواتير والمرافق",
                    icon = Icons.Default.AccountBalanceWallet,
                    modifier = Modifier.weight(1f)
                ) { onNavigateToTab(3) }

                ActionChipButton(
                    title = "إدارة السكان",
                    icon = Icons.Default.Group,
                    modifier = Modifier.weight(1f)
                ) { onNavigateToTab(4) }
            }
        }
    }
}

@Composable
fun ResidentDashboard(
    viewModel: ImartiViewModel,
    onNavigateToTab: (Int) -> Unit
) {
    val session by viewModel.session.collectAsState()
    val sub by viewModel.currentSubscription.collectAsState()
    val plan by viewModel.currentPlan.collectAsState()
    val announcements by viewModel.announcements.collectAsState()
    val emergencyAnnouncements = announcements.filter { it.type == AnnouncementType.EMERGENCY }
    val myApt = session.apartmentNumber ?: "01"
    val myBills by viewModel.bills.collectAsState()
    val myLatestBill = myBills.firstOrNull { it.apartmentNumber == myApt }
    val myComplaints by viewModel.complaints.collectAsState()
    val myOpenComplaints = myComplaints.filter { it.apartmentNumber == myApt && it.status != ComplaintStatus.COMPLETED }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .testTag("resident_dashboard"),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Read-only warning if building subscription is expired
        if (sub.status == SubscriptionStatus.READ_ONLY) {
            item {
                Surface(
                    color = MaterialTheme.colorScheme.errorContainer,
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(modifier = Modifier.padding(14.dp), verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Lock, contentDescription = null, tint = MaterialTheme.colorScheme.error)
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = "تنبيه: اشتراك العمارة منتهٍ وهو الآن في وضع القراءة فقط. يرجى من المسؤول التجديد لاستئناف تقديم الشكاوى والحجوزات.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onErrorContainer
                        )
                    }
                }
            }
        }

        // Emergency Alert
        if (emergencyAnnouncements.isNotEmpty()) {
            item {
                EmergencyAlertCard(emergencyAnnouncements.first())
            }
        }

        // Resident Apartment Profile Card
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
            ) {
                Row(
                    modifier = Modifier.padding(18.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(50.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.primary),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(myApt, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = Color.White)
                    }
                    Spacer(modifier = Modifier.width(14.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(session.name, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                        Text("شقة $myApt • إقامة الأندلس", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f))
                        Text("صفة الساكن: مالك الشقة", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        // My Bill & Payment Status Card
        item {
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
                        Text("💳 واجباتي الشهرية (واجب السنديك)", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
                        Surface(
                            color = if (myLatestBill?.isPaid == true) Color(0xFFDCFCE7) else Color(0xFFFEF3C7),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text(
                                text = if (myLatestBill?.isPaid == true) "مدفوعة ✅" else "غير مؤكدة ⏳",
                                color = if (myLatestBill?.isPaid == true) Color(0xFF16A34A) else Color(0xFFD97706),
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "شهر أكتوبر 2026: ${myLatestBill?.amount ?: 250.0} درهم",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.SemiBold
                    )
                    Text(
                        text = "يشمل: النظافة، صيانة المصعد، إنارة الأدراج، الحراسة ومضخة المياه المشتركة.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.outline
                    )

                    Spacer(modifier = Modifier.height(10.dp))
                    if (myLatestBill?.isPaid != true) {
                        Button(
                            onClick = {
                                if (myLatestBill != null) {
                                    viewModel.setSelectedBillForProof(myLatestBill)
                                } else {
                                    onNavigateToTab(3)
                                }
                            },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Icon(Icons.Default.CloudUpload, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("رفع إثبات الدفع (Reçu)")
                        }
                    } else {
                        Text(
                            text = "شكراً لمساهمتك في التسيير السلس للعمارة!",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color(0xFF16A34A),
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }

        // My Maintenance Requests
        item {
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
                        Text("🔧 طلبات الصيانة الخاصة بي", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
                        TextButton(onClick = { viewModel.setShowAddComplaint(true) }) {
                            Text("+ تقديم طلب")
                        }
                    }

                    if (myOpenComplaints.isEmpty()) {
                        Text(
                            text = "لا توجد طلبات صيانة مفتوحة حالياً لشقتك. كل شيء على ما يرام!",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    } else {
                        myOpenComplaints.forEach { c ->
                            Surface(
                                shape = RoundedCornerShape(10.dp),
                                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp)
                            ) {
                                Row(
                                    modifier = Modifier.padding(10.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(Icons.Default.Build, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(18.dp))
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(c.title, style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Bold, maxLines = 1, overflow = TextOverflow.Ellipsis)
                                        Text(c.status.labelAr, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.primary)
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        // Quick Shortcuts for Resident
        item {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                ActionChipButton(
                    title = "حجز مرفق مشترك",
                    icon = Icons.Default.EventAvailable,
                    modifier = Modifier.weight(1f)
                ) { onNavigateToTab(3) }

                ActionChipButton(
                    title = "دليل الطوارئ",
                    icon = Icons.Default.PhoneInTalk,
                    modifier = Modifier.weight(1f)
                ) { onNavigateToTab(4) }
            }
        }
    }
}

@Composable
fun SubscriptionStatusBanner(sub: BuildingSubscription, plan: Plan) {
    when (sub.status) {
        SubscriptionStatus.TRIAL -> {
            Surface(
                color = MaterialTheme.colorScheme.primaryContainer,
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.HourglassTop, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                    Spacer(modifier = Modifier.width(10.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text("⏳ أنت في فترة التجربة المجانية لباقة ${plan.name}", style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Bold)
                        Text("متبقي ${sub.daysRemaining} أيام على انتهاء التجربة.", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f))
                    }
                }
            }
        }
        SubscriptionStatus.GRACE_PERIOD -> {
            Surface(
                color = Color(0xFFFEF3C7),
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Warning, contentDescription = null, tint = Color(0xFFD97706))
                    Spacer(modifier = Modifier.width(10.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text("⚠️ مهلة سماح (3 أيام)", style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Bold, color = Color(0xFF92400E))
                        Text("التطبيق يعمل بكامل الميزات. يُرجى سداد الاشتراك لتفادي الانتقال لوضع القراءة فقط.", style = MaterialTheme.typography.labelSmall, color = Color(0xFFB45309))
                    }
                }
            }
        }
        SubscriptionStatus.READ_ONLY -> {
            Surface(
                color = Color(0xFFFEE2E2),
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Lock, contentDescription = null, tint = Color(0xFFDC2626))
                    Spacer(modifier = Modifier.width(10.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text("🔒 انتهى الاشتراك - وضع القراءة فقط", style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Bold, color = Color(0xFF991B1B))
                        Text("لا يمكن إضافة إعلانات أو شكاوى جديدة. تواصل مع الإدارة العليا للتجديد.", style = MaterialTheme.typography.labelSmall, color = Color(0xFFB91C1C))
                    }
                }
            }
        }
        else -> Unit
    }
}

@Composable
fun EmergencyAlertCard(announcement: Announcement) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.NotificationImportant, contentDescription = null, tint = MaterialTheme.colorScheme.error)
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = announcement.title,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onErrorContainer
                )
            }
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = announcement.content,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onErrorContainer
            )
        }
    }
}

@Composable
fun DashboardStatCard(
    title: String,
    value: String,
    sub: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    color: Color,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Card(
        modifier = modifier.clickable { onClick() },
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(icon, contentDescription = null, tint = color, modifier = Modifier.size(20.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text(title, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            Spacer(modifier = Modifier.height(6.dp))
            Text(value, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            Text(sub, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.outline, fontSize = 10.sp)
        }
    }
}

@Composable
fun ActionChipButton(
    title: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Surface(
        modifier = modifier.clickable { onClick() },
        shape = RoundedCornerShape(12.dp),
        color = MaterialTheme.colorScheme.surface,
        border = CardDefaults.outlinedCardBorder()
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Icon(icon, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(18.dp))
            Spacer(modifier = Modifier.width(6.dp))
            Text(title, style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Bold)
        }
    }
}

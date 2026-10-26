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
fun ApartmentsScreen(viewModel: ImartiViewModel) {
    val session by viewModel.session.collectAsState()
    val residents by viewModel.residents.collectAsState()
    val plan by viewModel.currentPlan.collectAsState()
    val sub by viewModel.currentSubscription.collectAsState()
    val emergencyContacts by viewModel.emergencyContacts.collectAsState()
    val plans by viewModel.plans.collectAsState()

    var selectedTab by remember { mutableStateOf(if (session.role == UserRole.MANAGER) 0 else 1) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .testTag("apartments_and_directory_screen")
    ) {
        TabRow(
            selectedTabIndex = selectedTab,
            containerColor = MaterialTheme.colorScheme.surface,
            contentColor = MaterialTheme.colorScheme.primary
        ) {
            if (session.role == UserRole.MANAGER || session.role == UserRole.SUPER_ADMIN) {
                Tab(
                    selected = selectedTab == 0,
                    onClick = { selectedTab = 0 },
                    text = { Text("إدارة السكان", fontSize = 11.sp, fontWeight = if (selectedTab == 0) FontWeight.Bold else FontWeight.Normal) }
                )
            }
            Tab(
                selected = selectedTab == 1,
                onClick = { selectedTab = 1 },
                text = { Text("دليل الطوارئ", fontSize = 11.sp, fontWeight = if (selectedTab == 1) FontWeight.Bold else FontWeight.Normal) }
            )
            Tab(
                selected = selectedTab == 2,
                onClick = { selectedTab = 2 },
                text = { Text("دليل السكان", fontSize = 11.sp, fontWeight = if (selectedTab == 2) FontWeight.Bold else FontWeight.Normal) }
            )
            Tab(
                selected = selectedTab == 3,
                onClick = { selectedTab = 3 },
                text = { Text("الباقة والحساب", fontSize = 11.sp, fontWeight = if (selectedTab == 3) FontWeight.Bold else FontWeight.Normal) }
            )
        }

        when (selectedTab) {
            0 -> ResidentsManagementSection(residents, plan, viewModel)
            1 -> EmergencyDirectorySection(emergencyContacts, viewModel)
            2 -> ResidentPhoneDirectorySection(residents, session, viewModel)
            3 -> SubscriptionAndAccountSection(sub, plan, plans, session, residents, viewModel)
        }
    }
}

@Composable
fun ResidentsManagementSection(
    residents: List<ResidentAccount>,
    plan: Plan,
    viewModel: ImartiViewModel
) {
    val activeCount = residents.count { it.status == ResidentStatus.ACTIVE }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = { viewModel.setShowAddResident(true) },
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = Color.White
            ) {
                Icon(Icons.Default.PersonAdd, contentDescription = "إضافة ساكن")
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
            // Plan capacity card
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("سعة الباقة الحالية (${plan.name})", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
                            Text("$activeCount / ${plan.maxApartments} شقة مفعّلة", style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        LinearProgressIndicator(
                            progress = { (activeCount.toFloat() / maxOf(1, plan.maxApartments)).coerceIn(0f, 1f) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(6.dp)
                                .clip(RoundedCornerShape(3.dp))
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "قاعدة المنصة: يسمح بساكن واحد فقط لكل شقة (مالك أو مستأجر). تصله رسالة تفعيل بالرمز السريع OTP.",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
                        )
                    }
                }
            }

            items(residents) { res ->
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
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(36.dp)
                                        .clip(CircleShape)
                                        .background(MaterialTheme.colorScheme.primaryContainer),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(res.apartmentNumber, fontWeight = FontWeight.Bold, fontSize = 13.sp, color = MaterialTheme.colorScheme.primary)
                                }
                                Spacer(modifier = Modifier.width(10.dp))
                                Column {
                                    Text(res.name, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
                                    Text("${if (res.isOwner) "مالك" else "مستأجر"} • الطابق ${res.floor}", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.outline)
                                }
                            }

                            Surface(
                                color = when (res.status) {
                                    ResidentStatus.ACTIVE -> Color(0xFFDCFCE7)
                                    ResidentStatus.INVITED -> Color(0xFFEFF6FF)
                                    ResidentStatus.SUSPENDED -> Color(0xFFFEE2E2)
                                },
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Text(
                                    text = res.status.labelAr,
                                    color = when (res.status) {
                                        ResidentStatus.ACTIVE -> Color(0xFF16A34A)
                                        ResidentStatus.INVITED -> Color(0xFF2563EB)
                                        ResidentStatus.SUSPENDED -> Color(0xFFDC2626)
                                    },
                                    style = MaterialTheme.typography.labelSmall,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Phone, contentDescription = null, modifier = Modifier.size(14.dp), tint = MaterialTheme.colorScheme.primary)
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(res.phone, style = MaterialTheme.typography.bodySmall)
                        }

                        Spacer(modifier = Modifier.height(10.dp))
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            if (res.status != ResidentStatus.ACTIVE) {
                                OutlinedButton(
                                    onClick = { viewModel.updateResidentStatus(res.id, ResidentStatus.ACTIVE) },
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Text("تفعيل", fontSize = 11.sp)
                                }
                            }
                            if (res.status != ResidentStatus.SUSPENDED) {
                                OutlinedButton(
                                    onClick = { viewModel.updateResidentStatus(res.id, ResidentStatus.SUSPENDED) },
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Text("إيقاف الحساب", fontSize = 11.sp)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun EmergencyDirectorySection(contacts: List<EmergencyContact>, viewModel: ImartiViewModel) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Text(
                text = "🚨 أرقام الطوارئ والمصالح المشتركة",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "يمكن لجميع السكان والمسؤولين الوصول المباشر لهذه الأرقام في حالات الاستعجال.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        items(contacts) { c ->
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
                            .size(42.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.primaryContainer),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = when (c.iconKey) {
                                "fire" -> Icons.Default.LocalFireDepartment
                                "ambulance" -> Icons.Default.MedicalServices
                                "police" -> Icons.Default.LocalPolice
                                "utility" -> Icons.Default.Water
                                else -> Icons.Default.Phone
                            },
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(22.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(c.name, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
                        Text(c.description, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.outline)
                    }

                    Surface(
                        color = MaterialTheme.colorScheme.primary,
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.clickable {
                            viewModel.showFeedback("الاتصال بـ ${c.name} على الرقم: ${c.phone}")
                        }
                    ) {
                        Row(modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp), verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Call, contentDescription = null, tint = Color.White, modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(c.phone, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ResidentPhoneDirectorySection(
    residents: List<ResidentAccount>,
    session: AuthSession,
    viewModel: ImartiViewModel
) {
    val myResident = residents.firstOrNull { it.phone == session.phone || it.apartmentNumber == session.apartmentNumber }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Privacy Toggle for the logged-in resident
        if (myResident != null) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.5f))
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text("خصوصية رقم هاتفي في الدليل", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
                            Text(
                                text = if (myResident.isPhoneVisibleInDirectory) "رقمك متاح للجيران في دليل العمارة" else "رقمك مخفي عن الجيران في الدليل",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSecondaryContainer
                            )
                        }
                        Switch(
                            checked = myResident.isPhoneVisibleInDirectory,
                            onCheckedChange = { viewModel.toggleResidentPhoneVisibility(myResident.id) }
                        )
                    }
                }
            }
        }

        item {
            Text(
                text = "👥 دليل الجيران وسكان العمارة",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
        }

        items(residents.filter { it.status == ResidentStatus.ACTIVE }) { res ->
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Row(
                    modifier = Modifier.padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(34.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.surfaceVariant),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(res.apartmentNumber, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(res.name, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold)
                        Text(if (res.isOwner) "مالك" else "مستأجر", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.outline)
                    }

                    if (res.isPhoneVisibleInDirectory || session.role != UserRole.RESIDENT) {
                        Surface(
                            color = MaterialTheme.colorScheme.surfaceVariant,
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.clickable {
                                viewModel.showFeedback("الاتصال بالجار: ${res.name} (${res.phone})")
                            }
                        ) {
                            Row(modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp), verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.Phone, contentDescription = null, modifier = Modifier.size(12.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(res.phone, fontSize = 11.sp)
                            }
                        }
                    } else {
                        Text("مخفي", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.outline)
                    }
                }
            }
        }
    }
}

@Composable
fun SubscriptionAndAccountSection(
    sub: BuildingSubscription,
    plan: Plan,
    allPlans: List<Plan>,
    session: AuthSession,
    residents: List<ResidentAccount>,
    viewModel: ImartiViewModel
) {
    val activeResidentsCount = residents.count { it.status == ResidentStatus.ACTIVE }

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
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("بيانات الحساب الحالي", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("الاسم: ${session.name}", style = MaterialTheme.typography.bodyMedium)
                    Text("رقم الهاتف: ${session.phone}", style = MaterialTheme.typography.bodyMedium)
                    Text("الصلاحية: ${session.role.labelAr}", style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)

                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = "ℹ️ تغيير رقم الهاتف: الساكن يغيره عبر مسؤول العمارة، والمسؤول يغيره بالتواصل مع الإدارة العليا.",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.outline
                    )
                }
            }
        }

        if (session.role == UserRole.MANAGER || session.role == UserRole.SUPER_ADMIN) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("ترقية أو تغيير الباقة", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
                        Text(
                            text = "الباقة الحالية: ${plan.name} (${sub.status.labelAr} - متبقي ${sub.daysRemaining} يوم). التخفيض ممنوع إذا كانت الباقة أصغر من عدد الشقق النشطة ($activeResidentsCount).",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(vertical = 4.dp)
                        )

                        Spacer(modifier = Modifier.height(10.dp))
                        allPlans.forEach { targetPlan ->
                            val isCurrent = targetPlan.id == plan.id
                            val canSelect = activeResidentsCount <= targetPlan.maxApartments

                            Surface(
                                shape = RoundedCornerShape(10.dp),
                                color = if (isCurrent) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp)
                            ) {
                                Row(
                                    modifier = Modifier.padding(12.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Column {
                                        Text("باقة ${targetPlan.name} (${targetPlan.monthlyPrice.toInt()} درهم/شهر)", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.bodyMedium)
                                        Text("حتى ${targetPlan.maxApartments} شقة", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.outline)
                                    }

                                    if (isCurrent) {
                                        Text("الحالية ✅", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary, fontSize = 12.sp)
                                    } else {
                                        Button(
                                            onClick = { viewModel.changePlan(session.buildingId, targetPlan.id) },
                                            enabled = canSelect
                                        ) {
                                            Text(if (targetPlan.maxApartments > plan.maxApartments) "ترقية" else "تخفيض", fontSize = 11.sp)
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        item {
            Button(
                onClick = { viewModel.setShowLogin(true) },
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error),
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp)
            ) {
                Icon(Icons.Default.ExitToApp, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("تسجيل الخروج أو تبديل الحساب")
            }
        }
    }
}

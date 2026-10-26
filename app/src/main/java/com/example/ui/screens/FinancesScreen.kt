package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
fun FinancesScreen(viewModel: ImartiViewModel) {
    val plan by viewModel.currentPlan.collectAsState()
    val session by viewModel.session.collectAsState()
    val isReadOnly by viewModel.isReadOnly.collectAsState()
    var selectedTab by remember { mutableStateOf(0) }

    // If on Basic plan, show Plan Feature Lock State!
    if (!plan.hasBillsAndReceipts && !plan.hasFacilityBooking && session.role != UserRole.SUPER_ADMIN) {
        FeatureLockedCard(
            title = "الفواتير وحجز المرافق المشتركة",
            desc = "هذه الميزات متاحة انطلاقاً من الباقة المتوسطة (حتى 30 شقة) والباقة المتقدمة.",
            onUpgrade = {
                if (session.role == UserRole.MANAGER) {
                    viewModel.changePlan(session.buildingId, "plan_medium")
                } else {
                    viewModel.showFeedback("تواصل مع مسؤول العمارة لترقية الباقة وتفعيل الفواتير والمرافق.")
                }
            }
        )
        return
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .testTag("bills_and_facilities_screen")
    ) {
        TabRow(
            selectedTabIndex = selectedTab,
            containerColor = MaterialTheme.colorScheme.surface,
            contentColor = MaterialTheme.colorScheme.primary
        ) {
            Tab(
                selected = selectedTab == 0,
                onClick = { selectedTab = 0 },
                text = { Text("💳 الفواتير والمساهمات", fontWeight = if (selectedTab == 0) FontWeight.Bold else FontWeight.Normal) },
                icon = { Icon(Icons.Default.Receipt, contentDescription = null, modifier = Modifier.size(18.dp)) }
            )
            Tab(
                selected = selectedTab == 1,
                onClick = { selectedTab = 1 },
                text = { Text("🏡 حجز المرافق المشتركة", fontWeight = if (selectedTab == 1) FontWeight.Bold else FontWeight.Normal) },
                icon = { Icon(Icons.Default.EventSeat, contentDescription = null, modifier = Modifier.size(18.dp)) }
            )
        }

        when (selectedTab) {
            0 -> BillsSection(viewModel)
            1 -> FacilitiesSection(viewModel)
        }
    }
}

@Composable
fun FeatureLockedCard(title: String, desc: String, onUpgrade: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        contentAlignment = Alignment.Center
    ) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    modifier = Modifier
                        .size(60.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.tertiaryContainer),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Lock,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.tertiary,
                        modifier = Modifier.size(32.dp)
                    )
                }
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = desc,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(vertical = 8.dp)
                )
                Spacer(modifier = Modifier.height(16.dp))
                Button(
                    onClick = onUpgrade,
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(Icons.Default.Upgrade, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("ترقية الباقة الآن")
                }
            }
        }
    }
}

@Composable
fun BillsSection(viewModel: ImartiViewModel) {
    val bills by viewModel.bills.collectAsState()
    val session by viewModel.session.collectAsState()
    val isResident = session.role == UserRole.RESIDENT
    val myApt = session.apartmentNumber ?: "01"

    val displayedBills = if (isResident) bills.filter { it.apartmentNumber == myApt } else bills

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Text(
                text = if (isResident) "واجباتي الشهرية وفواتير شقتي" else "فواتير شقق العمارة ومراجعة إثباتات الدفع",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
        }

        items(displayedBills) { bill ->
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
                            Text("شقة ${bill.apartmentNumber} (${bill.residentName})", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
                            Text(bill.month, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.outline)
                        }

                        Surface(
                            color = if (bill.isPaid) Color(0xFFDCFCE7) else if (bill.proofReceiptUploaded) Color(0xFFEFF6FF) else Color(0xFFFEF3C7),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text(
                                text = if (bill.isPaid) "مدفوعة ✅" else if (bill.proofReceiptUploaded) "تم رفع الإثبات 📄" else "في الانتظار ⏳",
                                color = if (bill.isPaid) Color(0xFF16A34A) else if (bill.proofReceiptUploaded) Color(0xFF2563EB) else Color(0xFFD97706),
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))
                    Text(bill.description, style = MaterialTheme.typography.bodySmall)

                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "${bill.amount.toInt()} درهم",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Text(
                            text = "تاريخ الاستحقاق: ${bill.dueDate}",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.outline
                        )
                    }

                    if (bill.proofReceiptUploaded) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Surface(
                            color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(modifier = Modifier.padding(8.dp), verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.Receipt, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = bill.proofReceiptNote ?: "تم إرفاق إثبات الدفع",
                                    style = MaterialTheme.typography.bodySmall,
                                    fontSize = 11.sp
                                )
                            }
                        }
                    }

                    // Actions
                    Spacer(modifier = Modifier.height(12.dp))
                    if (isResident) {
                        if (!bill.isPaid) {
                            Button(
                                onClick = { viewModel.setSelectedBillForProof(bill) },
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(10.dp)
                            ) {
                                Icon(Icons.Default.CloudUpload, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(if (bill.proofReceiptUploaded) "تحديث وصل الدفع" else "رفع إثبات الدفع")
                            }
                        }
                    } else {
                        // Manager actions
                        if (!bill.isPaid) {
                            Button(
                                onClick = { viewModel.confirmBill(bill.id) },
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(10.dp)
                            ) {
                                Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("تأكيد استلام المبلغ (تحويل لحالة مدفوعة)")
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun FacilitiesSection(viewModel: ImartiViewModel) {
    val facilities by viewModel.facilities.collectAsState()
    val bookings by viewModel.bookings.collectAsState()
    val session by viewModel.session.collectAsState()
    val isManager = session.role != UserRole.RESIDENT

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            Text(
                text = "🏡 المرافق المشتركة وقواعد الحجز",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "نظام الحجز يمنع التعارض التلقائي بين السكان لضمان حسن الاستعمال المشترك.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        items(facilities) { fac ->
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
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(40.dp)
                                    .clip(CircleShape)
                                    .background(MaterialTheme.colorScheme.primaryContainer),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(Icons.Default.Apartment, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(fac.name, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
                                Text("أقصى مدة: ${fac.maxDurationHours} ساعات • ${fac.openHours}", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.outline)
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))
                    Text("قواعد الاستعمال: ${fac.rules}", style = MaterialTheme.typography.bodySmall, fontSize = 12.sp)

                    Spacer(modifier = Modifier.height(10.dp))
                    Button(
                        onClick = { viewModel.setSelectedFacility(fac) },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Icon(Icons.Default.CalendarMonth, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("حجز هذا المرفق")
                    }
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(10.dp))
            Text(
                text = "📅 جدول الحجوزات القادمة",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
        }

        items(bookings) { b ->
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
            ) {
                Row(
                    modifier = Modifier.padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Default.Event, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                    Spacer(modifier = Modifier.width(10.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(b.facilityName, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold)
                        Text("${b.date} (${b.timeSlot}) • شقة ${b.apartmentNumber} (${b.residentName})", style = MaterialTheme.typography.bodySmall)
                        if (b.status != "مؤكد") {
                            Text("الحالة: ${b.status} (${b.managerCancelReason})", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.error)
                        }
                    }

                    if (isManager && b.status == "مؤكد") {
                        TextButton(onClick = { viewModel.cancelBooking(b.id, "إلغاء لضرورة الصيانة") }) {
                            Text("إلغاء الحجز", color = MaterialTheme.colorScheme.error, fontSize = 11.sp)
                        }
                    }
                }
            }
        }
    }
}

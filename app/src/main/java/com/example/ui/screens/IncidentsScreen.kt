package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
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
fun IncidentsScreen(viewModel: ImartiViewModel) {
    val session by viewModel.session.collectAsState()
    val complaints by viewModel.complaints.collectAsState()
    val isReadOnly by viewModel.isReadOnly.collectAsState()
    val context = androidx.compose.ui.platform.LocalContext.current
    var selectedCategoryFilter by remember { mutableStateOf<ComplaintCategory?>(null) }
    var selectedStatusFilter by remember { mutableStateOf<ComplaintStatus?>(null) }

    val myApt = session.apartmentNumber ?: "01"
    val isResident = session.role == UserRole.RESIDENT

    val filteredComplaints = complaints.filter { c ->
        // If resident, only show their own complaints
        if (isResident && c.apartmentNumber != myApt) return@filter false
        if (selectedCategoryFilter != null && c.category != selectedCategoryFilter) return@filter false
        if (selectedStatusFilter != null && c.status != selectedStatusFilter) return@filter false
        true
    }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    if (isReadOnly) {
                        viewModel.showFeedback("الاشتراك في وضع القراءة فقط. يرجى التجديد لتقديم شكاوى جديدة.")
                    } else {
                        viewModel.setShowAddComplaint(true)
                    }
                },
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = Color.White
            ) {
                Icon(Icons.Default.Add, contentDescription = "تقديم شكوى أو عطب")
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(MaterialTheme.colorScheme.background)
                .testTag("complaints_screen")
        ) {
            // Category filter chips
            LazyRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                item {
                    FilterChip(
                        selected = selectedCategoryFilter == null,
                        onClick = { selectedCategoryFilter = null },
                        label = { Text("الكل") }
                    )
                }
                items(ComplaintCategory.values()) { cat ->
                    FilterChip(
                        selected = selectedCategoryFilter == cat,
                        onClick = { selectedCategoryFilter = if (selectedCategoryFilter == cat) null else cat },
                        label = { Text(cat.labelAr) }
                    )
                }
            }

            if (filteredComplaints.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            imageVector = Icons.Default.CheckCircleOutline,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(54.dp)
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = if (isResident) "لا توجد شكاوى مسجلة لشقتك حالياً" else "لا توجد شكاوى مطابقة للتصنيف",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "يمكنك الضغط على زر (+) لتقديم طلب صيانة أو إبلاغ عن عطب.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(filteredComplaints) { complaint ->
                        ComplaintCard(
                            complaint = complaint,
                            isManager = session.role != UserRole.RESIDENT,
                            onStatusChange = { newStatus, note ->
                                viewModel.updateComplaintStatus(complaint.id, newStatus, note)
                                viewModel.dispatchSystemComplaintNotification(
                                    context,
                                    complaint.title,
                                    newStatus.labelAr,
                                    note,
                                    complaint.apartmentNumber
                                )
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun ComplaintCard(
    complaint: Complaint,
    isManager: Boolean,
    onStatusChange: (ComplaintStatus, String?) -> Unit
) {
    var showManagerNoteDialog by remember { mutableStateOf(false) }
    var managerNoteInput by remember { mutableStateOf(complaint.managerNote ?: "") }

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
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(
                                when (complaint.category) {
                                    ComplaintCategory.ELEVATOR -> MaterialTheme.colorScheme.primaryContainer
                                    ComplaintCategory.PLUMBING -> MaterialTheme.colorScheme.secondaryContainer
                                    ComplaintCategory.ELECTRICITY -> Color(0xFFFEF3C7)
                                    else -> MaterialTheme.colorScheme.surfaceVariant
                                }
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = when (complaint.category) {
                                ComplaintCategory.ELEVATOR -> Icons.Default.Elevator
                                ComplaintCategory.PLUMBING -> Icons.Default.WaterDrop
                                ComplaintCategory.ELECTRICITY -> Icons.Default.Lightbulb
                                ComplaintCategory.CLEANING -> Icons.Default.CleaningServices
                                else -> Icons.Default.Build
                            },
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(complaint.category.labelAr, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold)
                        Text("شقة ${complaint.apartmentNumber} (${complaint.residentName})", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.outline)
                    }
                }

                Surface(
                    color = when (complaint.status) {
                        ComplaintStatus.NEW -> Color(0xFFFEE2E2)
                        ComplaintStatus.IN_PROGRESS -> Color(0xFFFEF3C7)
                        ComplaintStatus.COMPLETED -> Color(0xFFDCFCE7)
                    },
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        text = complaint.status.labelAr,
                        color = when (complaint.status) {
                            ComplaintStatus.NEW -> Color(0xFFDC2626)
                            ComplaintStatus.IN_PROGRESS -> Color(0xFFD97706)
                            ComplaintStatus.COMPLETED -> Color(0xFF16A34A)
                        },
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))
            Text(complaint.title, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(4.dp))
            Text(complaint.description, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)

            if (complaint.photoNote != null) {
                Spacer(modifier = Modifier.height(8.dp))
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                ) {
                    Row(modifier = Modifier.padding(6.dp), verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.PhotoCamera, contentDescription = null, modifier = Modifier.size(14.dp), tint = MaterialTheme.colorScheme.primary)
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("مرفق صورة توضيحية من الساكن", style = MaterialTheme.typography.labelSmall, fontSize = 11.sp)
                    }
                }
            }

            if (complaint.managerNote != null) {
                Spacer(modifier = Modifier.height(10.dp))
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.4f),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(10.dp)) {
                        Text("ملاحظة وتحديث المسؤول:", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                        Text(complaint.managerNote, style = MaterialTheme.typography.bodySmall, fontSize = 12.sp)
                    }
                }
            }

            if (isManager) {
                Spacer(modifier = Modifier.height(12.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    if (complaint.status != ComplaintStatus.IN_PROGRESS) {
                        OutlinedButton(
                            onClick = { onStatusChange(ComplaintStatus.IN_PROGRESS, "تم تحويل الطلب للتقني المختص للمعاينة") },
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("قيد الإصلاح", fontSize = 11.sp)
                        }
                    }

                    if (complaint.status != ComplaintStatus.COMPLETED) {
                        Button(
                            onClick = { onStatusChange(ComplaintStatus.COMPLETED, "تم إصلاح العطب بنجاح") },
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("تم الحل بنجاح", fontSize = 11.sp)
                        }
                    }
                }
            }
        }
    }
}

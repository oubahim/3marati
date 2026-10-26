package com.example.ui.screens

import androidx.compose.foundation.background
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
fun AnnouncementsAndWorksScreen(viewModel: ImartiViewModel) {
    var selectedTab by remember { mutableStateOf(0) }
    val announcements by viewModel.announcements.collectAsState()
    val session by viewModel.session.collectAsState()
    val isReadOnly by viewModel.isReadOnly.collectAsState()

    val currentAnnouncements = announcements.filter { it.type != AnnouncementType.PERMANENT }
    val permanentRules = announcements.filter { it.type == AnnouncementType.PERMANENT }

    Scaffold(
        floatingActionButton = {
            if (session.role == UserRole.MANAGER || session.role == UserRole.SUPER_ADMIN) {
                FloatingActionButton(
                    onClick = {
                        if (isReadOnly) {
                            viewModel.showFeedback("الاشتراك في وضع القراءة فقط. يرجى التجديد لإضافة إعلانات جديدة.")
                        } else {
                            viewModel.setShowAddAnnouncement(true)
                        }
                    },
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = Color.White
                ) {
                    Icon(Icons.Default.AddComment, contentDescription = "نشر إعلان جديد")
                }
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(MaterialTheme.colorScheme.background)
                .testTag("announcements_screen")
        ) {
            // Tabs: الإعلانات الحالية vs القواعد والمعلومات
            TabRow(
                selectedTabIndex = selectedTab,
                containerColor = MaterialTheme.colorScheme.surface,
                contentColor = MaterialTheme.colorScheme.primary
            ) {
                Tab(
                    selected = selectedTab == 0,
                    onClick = { selectedTab = 0 },
                    text = { Text("📢 الإعلانات الحالية (${currentAnnouncements.size})", fontWeight = if (selectedTab == 0) FontWeight.Bold else FontWeight.Normal) }
                )
                Tab(
                    selected = selectedTab == 1,
                    onClick = { selectedTab = 1 },
                    text = { Text("📜 القواعد والمعلومات (${permanentRules.size})", fontWeight = if (selectedTab == 1) FontWeight.Bold else FontWeight.Normal) }
                )
            }

            if (selectedTab == 0) {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(currentAnnouncements) { ann ->
                        AnnouncementItemCard(
                            announcement = ann,
                            canDelete = session.role != UserRole.RESIDENT,
                            onDelete = { viewModel.deleteAnnouncement(ann.id) }
                        )
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(permanentRules) { rule ->
                        AnnouncementItemCard(
                            announcement = rule,
                            canDelete = session.role != UserRole.RESIDENT,
                            onDelete = { viewModel.deleteAnnouncement(rule.id) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun AnnouncementItemCard(
    announcement: Announcement,
    canDelete: Boolean,
    onDelete: () -> Unit
) {
    val isEmergency = announcement.type == AnnouncementType.EMERGENCY

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isEmergency) MaterialTheme.colorScheme.errorContainer else MaterialTheme.colorScheme.surface
        )
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(
                                when (announcement.type) {
                                    AnnouncementType.EMERGENCY -> MaterialTheme.colorScheme.error
                                    AnnouncementType.TEMPORARY -> MaterialTheme.colorScheme.primary
                                    AnnouncementType.PERMANENT -> MaterialTheme.colorScheme.secondary
                                }
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = when (announcement.type) {
                                AnnouncementType.EMERGENCY -> Icons.Default.Warning
                                AnnouncementType.TEMPORARY -> Icons.Default.Campaign
                                AnnouncementType.PERMANENT -> Icons.Default.MenuBook
                            },
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = announcement.title,
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            color = if (isEmergency) MaterialTheme.colorScheme.onErrorContainer else MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "${announcement.author} • ${announcement.date}",
                            style = MaterialTheme.typography.labelSmall,
                            color = if (isEmergency) MaterialTheme.colorScheme.onErrorContainer.copy(alpha = 0.8f) else MaterialTheme.colorScheme.outline
                        )
                    }
                }

                if (canDelete) {
                    IconButton(onClick = onDelete) {
                        Icon(Icons.Default.DeleteOutline, contentDescription = "حذف الإعلان", tint = MaterialTheme.colorScheme.outline)
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))
            Text(
                text = announcement.content,
                style = MaterialTheme.typography.bodyMedium,
                color = if (isEmergency) MaterialTheme.colorScheme.onErrorContainer else MaterialTheme.colorScheme.onSurfaceVariant
            )

            if (announcement.startDate != null && announcement.endDate != null) {
                Spacer(modifier = Modifier.height(8.dp))
                Surface(
                    color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.6f),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        text = "📅 ساري من ${announcement.startDate} إلى ${announcement.endDate}",
                        style = MaterialTheme.typography.labelSmall,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                }
            }
        }
    }
}

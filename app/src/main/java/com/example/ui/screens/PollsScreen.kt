package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
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
fun PollsScreen(viewModel: ImartiViewModel) {
    val plan by viewModel.currentPlan.collectAsState()
    val session by viewModel.session.collectAsState()
    val polls by viewModel.polls.collectAsState()
    val isReadOnly by viewModel.isReadOnly.collectAsState()
    val myApt = session.apartmentNumber ?: "01"
    val isManager = session.role != UserRole.RESIDENT

    // If plan does not include polls, show locked feature state!
    if (!plan.hasPolls && session.role != UserRole.SUPER_ADMIN) {
        FeatureLockedCard(
            title = "التصويت واستطلاعات الرأي الرسمية",
            desc = "هذه الميزة متاحة في الباقة المتقدمة (حتى 100 شقة) للتصويت الشفاف بصوت واحد لكل شقة.",
            onUpgrade = {
                if (session.role == UserRole.MANAGER) {
                    viewModel.changePlan(session.buildingId, "plan_advanced")
                } else {
                    viewModel.showFeedback("تواصل مع مسؤول العمارة للترقية إلى الباقة المتقدمة.")
                }
            }
        )
        return
    }

    Scaffold(
        floatingActionButton = {
            if (isManager) {
                FloatingActionButton(
                    onClick = {
                        if (isReadOnly) {
                            viewModel.showFeedback("التطبيق في وضع القراءة فقط. يرجى التجديد لإنشاء استطلاع.")
                        } else {
                            viewModel.setShowAddPoll(true)
                        }
                    },
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = Color.White
                ) {
                    Icon(Icons.Default.HowToVote, contentDescription = "إنشاء استطلاع جديد")
                }
            }
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
                .background(MaterialTheme.colorScheme.background)
                .testTag("polls_screen"),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "🗳️ التصويت والقرارات المشتركة",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Text(
                            text = "قاعدة التصويت: صوت واحد فقط لكل شقة لضمان العدالة والشفافية.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.outline
                        )
                    }

                    if (isManager) {
                        TextButton(onClick = { viewModel.setShowAddPoll(true) }) {
                            Text("+ استطلاع جديد")
                        }
                    }
                }
            }

            items(polls) { poll ->
                PollCard(
                    poll = poll,
                    myApartmentNumber = myApt,
                    onVote = { optionId ->
                        viewModel.votePoll(poll.id, optionId, myApt)
                    }
                )
            }
        }
    }
}

@Composable
fun PollCard(
    poll: Poll,
    myApartmentNumber: String,
    onVote: (optionId: String) -> Unit
) {
    val myVotedOptionId = poll.votedApartments[myApartmentNumber]
    val hasVoted = myVotedOptionId != null

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
                Surface(
                    color = MaterialTheme.colorScheme.primaryContainer,
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        text = if (poll.isClosed) "مكتمل ومغلق" else "تصويت نشط",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                    )
                }

                Text(
                    text = "ينتهي: ${poll.endDate}",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.outline
                )
            }

            Spacer(modifier = Modifier.height(10.dp))
            Text(poll.question, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(4.dp))
            Text(poll.description, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)

            Spacer(modifier = Modifier.height(12.dp))

            val totalVotesCount = maxOf(1, poll.options.sumOf { it.votesCount })

            poll.options.forEach { opt ->
                val isMyVote = myVotedOptionId == opt.id
                val percentage = (opt.votesCount * 100) / totalVotesCount

                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = if (isMyVote) MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f)
                    else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f),
                    border = if (isMyVote) BorderStroke(1.5.dp, MaterialTheme.colorScheme.primary) else null,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp)
                        .clickable(enabled = !hasVoted && !poll.isClosed) {
                            onVote(opt.id)
                        }
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                if (isMyVote) {
                                    Icon(Icons.Default.CheckCircle, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                }
                                Text(
                                    text = opt.title,
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontWeight = if (isMyVote) FontWeight.Bold else FontWeight.Normal
                                )
                            }
                            Text(
                                text = "$percentage% (${opt.votesCount} صوت)",
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        LinearProgressIndicator(
                            progress = { percentage.toFloat() / 100f },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(6.dp)
                                .clip(RoundedCornerShape(3.dp)),
                            color = MaterialTheme.colorScheme.primary,
                            trackColor = MaterialTheme.colorScheme.surfaceVariant
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))
            if (hasVoted) {
                Text(
                    text = "✅ تم تسجيل صوت شقتكم (شقة $myApartmentNumber) في هذا الاستطلاع.",
                    style = MaterialTheme.typography.labelSmall,
                    color = Color(0xFF16A34A),
                    fontWeight = FontWeight.Bold
                )
            } else {
                Text(
                    text = "اضغط على أي خيار أعلاه لتسجيل صوت شقتك.",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}

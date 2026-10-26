package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.Plan
import com.example.ui.ImartiViewModel

@Composable
fun ManagerOnboardingScreen(viewModel: ImartiViewModel) {
    val plans by viewModel.plans.collectAsState()
    val session by viewModel.session.collectAsState()

    var apartmentCountInput by remember { mutableStateOf("18") }
    var buildingNameInput by remember { mutableStateOf("عمارة النور والأمل") }

    // Feature requirements checklist
    var reqAnnouncements by remember { mutableStateOf(true) }
    var reqComplaints by remember { mutableStateOf(true) }
    var reqBills by remember { mutableStateOf(true) }
    var reqFacilities by remember { mutableStateOf(true) }
    var reqPolls by remember { mutableStateOf(false) }

    val apartments = apartmentCountInput.toIntOrNull() ?: 18

    // Recommendation logic:
    // Basic <= 10 apts and no bills/facilities/polls
    // Medium <= 30 apts and no polls
    // Advanced > 30 apts or has polls
    val recommendedPlanId = when {
        apartments > 30 || reqPolls -> "plan_advanced"
        apartments > 10 || reqBills || reqFacilities -> "plan_medium"
        else -> "plan_basic"
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
            .testTag("manager_onboarding_screen")
    ) {
        // Welcome Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(44.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.primary),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.Celebration, contentDescription = null, tint = Color.White)
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = "مرحباً بك يا مسؤول العمارة!",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                        Text(
                            text = session.name,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
                        )
                    }
                }
                Spacer(modifier = Modifier.height(10.dp))
                Text(
                    text = "خطوة أخيرة لإطلاق عمارتك: حدد عدد الشقق والميزات المطلوبة لتجربة الباقة المناسبة لك مجاناً لمدة أسبوع.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Question 1: Building Details & Apartment count
        Text(
            text = "1. بيانات العمارة وعدد الشقق:",
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedTextField(
            value = buildingNameInput,
            onValueChange = { buildingNameInput = it },
            label = { Text("اسم العمارة أو الإقامة السكنية") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedTextField(
            value = apartmentCountInput,
            onValueChange = { apartmentCountInput = it },
            label = { Text("كم عدد الشقق في عمارتك؟") },
            supportingText = { Text("الحد الأقصى للشقق يحدد الباقة المؤهلة") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Question 2: Needed Features
        Text(
            text = "2. ما هي الميزات التي تحتاجها في عمارتك؟",
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )
        Spacer(modifier = Modifier.height(8.dp))

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(14.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
            Column(modifier = Modifier.padding(12.dp)) {
                FeatureCheckRow("الإعلانات والقواعد (طارئ، مؤقت، دائم)", true, enabled = false)
                FeatureCheckRow("الشكاوى ومتابعة الصيانة والأعطاب", true, enabled = false)
                FeatureCheckRow("الفواتير ومساهمات السكان ورفع إثباتات الدفع", reqBills) { reqBills = it }
                FeatureCheckRow("حجز المرافق المشتركة (السطح، القاعة، الشواء)", reqFacilities) { reqFacilities = it }
                FeatureCheckRow("التصويت واستطلاعات الرأي الرسمية (صوت لكل شقة)", reqPolls) { reqPolls = it }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Section 3: Available Plans & Free Trial
        Text(
            text = "3. اختر باقتك وابدأ التجربة المجانية (7 أيام):",
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = "التجربة متاحة مرة واحدة لرقمك بحدود الباقة وميزاتها الكاملة دون الحاجة لبطاقة بنكية.",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(modifier = Modifier.height(12.dp))

        plans.forEach { plan ->
            val isRecommended = plan.id == recommendedPlanId
            val isTooSmall = apartments > plan.maxApartments

            // Check missing features
            val missingFeatures = mutableListOf<String>()
            if (reqBills && !plan.hasBillsAndReceipts) missingFeatures.add("الفواتير وإثباتات الدفع")
            if (reqFacilities && !plan.hasFacilityBooking) missingFeatures.add("حجز المرافق المشتركة")
            if (reqPolls && !plan.hasPolls) missingFeatures.add("التصويت والاستطلاعات")

            PlanCardItem(
                plan = plan,
                isRecommended = isRecommended,
                isTooSmall = isTooSmall,
                missingFeatures = missingFeatures,
                onStartTrial = {
                    viewModel.startTrial(plan.id, apartments, buildingNameInput)
                }
            )
            Spacer(modifier = Modifier.height(12.dp))
        }

        Spacer(modifier = Modifier.height(40.dp))
    }
}

@Composable
fun FeatureCheckRow(title: String, isChecked: Boolean, enabled: Boolean = true, onToggle: (Boolean) -> Unit = {}) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(enabled = enabled) { onToggle(!isChecked) }
            .padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Checkbox(checked = isChecked, onCheckedChange = if (enabled) onToggle else null, enabled = enabled)
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = title,
            style = MaterialTheme.typography.bodySmall,
            color = if (enabled) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.outline
        )
    }
}

@Composable
fun PlanCardItem(
    plan: Plan,
    isRecommended: Boolean,
    isTooSmall: Boolean,
    missingFeatures: List<String>,
    onStartTrial: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .then(
                if (isRecommended && !isTooSmall) Modifier.border(2.dp, MaterialTheme.colorScheme.primary, RoundedCornerShape(16.dp))
                else Modifier
            ),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isTooSmall) MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
            else MaterialTheme.colorScheme.surface
        )
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "باقة ${plan.name}",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = if (isTooSmall) MaterialTheme.colorScheme.outline else MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Surface(
                        color = MaterialTheme.colorScheme.surfaceVariant,
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(
                            text = "حتى ${plan.maxApartments} شقة",
                            style = MaterialTheme.typography.labelSmall,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                        )
                    }
                }

                if (isRecommended && !isTooSmall) {
                    Surface(
                        color = MaterialTheme.colorScheme.primary,
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text(
                            text = "✨ مقترحة لك",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = Color.White,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = plan.description,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(10.dp))
            Row(verticalAlignment = Alignment.Bottom) {
                Text(
                    text = "${plan.monthlyPrice.toInt()} درهم",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = " / شهرياً بعد التجربة",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.outline,
                    modifier = Modifier.padding(bottom = 2.dp, start = 4.dp)
                )
            }

            // Feature indicators
            Spacer(modifier = Modifier.height(10.dp))
            Column(verticalArrangement = Arrangement.spacedBy(3.dp)) {
                PlanFeatureBullet("نشر الإعلانات والقواعد التوجيهية", plan.hasAnnouncements)
                PlanFeatureBullet("استقبال الشكاوى ومتابعة الصيانة", plan.hasComplaints)
                PlanFeatureBullet("دليل الطوارئ والسكان المشترك", plan.hasDirectory)
                PlanFeatureBullet("الفواتير ومساهمات السكان وإثباتات الدفع", plan.hasBillsAndReceipts)
                PlanFeatureBullet("تنظيم وحجز المرافق المشتركة", plan.hasFacilityBooking)
                PlanFeatureBullet("التصويت واستطلاعات الرأي الرسمية", plan.hasPolls)
            }

            // Warnings if too small or missing features
            if (isTooSmall) {
                Spacer(modifier = Modifier.height(10.dp))
                Surface(
                    color = MaterialTheme.colorScheme.errorContainer,
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "⚠️ غير متسعة لعدد شقق عمارتك (تتسع حتى ${plan.maxApartments} شقة فقط).",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onErrorContainer,
                        modifier = Modifier.padding(8.dp)
                    )
                }
            } else if (missingFeatures.isNotEmpty()) {
                Spacer(modifier = Modifier.height(8.dp))
                Surface(
                    color = MaterialTheme.colorScheme.tertiaryContainer.copy(alpha = 0.5f),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "ℹ️ لا تتضمن ميزات طلبتها: ${missingFeatures.joinToString("، ")}.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onTertiaryContainer,
                        modifier = Modifier.padding(8.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            Button(
                onClick = onStartTrial,
                enabled = !isTooSmall,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(46.dp),
                shape = RoundedCornerShape(12.dp)
            ) {
                Icon(Icons.Default.RocketLaunch, contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "ابدأ تجربة مجانية لمدة أسبوع",
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
fun PlanFeatureBullet(title: String, isIncluded: Boolean) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(
            imageVector = if (isIncluded) Icons.Default.Check else Icons.Default.Close,
            contentDescription = null,
            tint = if (isIncluded) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline.copy(alpha = 0.5f),
            modifier = Modifier.size(16.dp)
        )
        Spacer(modifier = Modifier.width(6.dp))
        Text(
            text = title,
            style = MaterialTheme.typography.bodySmall,
            fontSize = 12.sp,
            color = if (isIncluded) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.outline.copy(alpha = 0.7f)
        )
    }
}

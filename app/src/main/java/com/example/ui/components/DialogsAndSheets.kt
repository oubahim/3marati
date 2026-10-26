package com.example.ui.components

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
import androidx.compose.ui.window.Dialog
import com.example.model.*
import com.example.ui.ImartiViewModel
import com.example.util.AppLanguage
import com.example.util.MoroccanRibUtil
import com.example.util.StringsHelper

@Composable
fun LoginDialog(
    onDismiss: () -> Unit,
    onLogin: (phone: String, channel: String) -> String?
) {
    var phoneInput by remember { mutableStateOf("") }
    var selectedChannel by remember { mutableStateOf("واتساب") } // واتساب أو SMS
    var isOtpSent by remember { mutableStateOf(false) }
    var otpInput by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
                .testTag("login_dialog_card"),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
            Column(
                modifier = Modifier
                    .padding(20.dp)
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Header
                Box(
                    modifier = Modifier
                        .size(54.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.primaryContainer),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.LockPerson,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(30.dp)
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = "تسجيل الدخول الموحد",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = "أدخل رقم هاتفك لتسجيل الدخول برمز التحقق السريع (OTP) دون الحاجة لكلمة مرور.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(top = 4.dp, bottom = 16.dp)
                )

                // Phone Input
                OutlinedTextField(
                    value = phoneInput,
                    onValueChange = {
                        phoneInput = it
                        errorMessage = null
                    },
                    label = { Text("رقم الهاتف (مثال: 0611111111)") },
                    leadingIcon = {
                        Icon(Icons.Default.Phone, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                    },
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("phone_input_field")
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Channel selector (SMS vs WhatsApp)
                Text(
                    text = "طريقة استلام رمز التحقق (OTP):",
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(6.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    FilterChip(
                        selected = selectedChannel == "واتساب",
                        onClick = { selectedChannel = "واتساب" },
                        label = { Text("واتساب (WhatsApp)") },
                        leadingIcon = { Icon(Icons.Default.Chat, contentDescription = null, modifier = Modifier.size(16.dp)) },
                        modifier = Modifier.weight(1f)
                    )
                    FilterChip(
                        selected = selectedChannel == "رسالة SMS",
                        onClick = { selectedChannel = "رسالة SMS" },
                        label = { Text("رسالة SMS") },
                        leadingIcon = { Icon(Icons.Default.Sms, contentDescription = null, modifier = Modifier.size(16.dp)) },
                        modifier = Modifier.weight(1f)
                    )
                }

                if (isOtpSent) {
                    Spacer(modifier = Modifier.height(14.dp))
                    Surface(
                        color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Text(
                                text = "تم إرسال رمز التحقق التجريبي إلى هاتفك عبر $selectedChannel: 123456",
                                style = MaterialTheme.typography.bodySmall,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            OutlinedTextField(
                                value = otpInput,
                                onValueChange = { otpInput = it },
                                label = { Text("رمز التحقق (أدخل 123456)") },
                                singleLine = true,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("otp_input_field")
                            )
                        }
                    }
                }

                // Error Message if any
                errorMessage?.let { err ->
                    Spacer(modifier = Modifier.height(10.dp))
                    Surface(
                        color = MaterialTheme.colorScheme.errorContainer,
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(10.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Icons.Default.ErrorOutline, contentDescription = null, tint = MaterialTheme.colorScheme.error)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = err,
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onErrorContainer
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Main Action Button
                Button(
                    onClick = {
                        if (!isOtpSent) {
                            if (phoneInput.isBlank()) {
                                errorMessage = "يرجى كتابة رقم الهاتف أولاً"
                            } else {
                                isOtpSent = true
                                otpInput = "123456" // Auto-fill demo OTP for convenience
                            }
                        } else {
                            val err = onLogin(phoneInput, selectedChannel)
                            if (err != null) {
                                errorMessage = err
                            }
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .testTag("submit_login_button"),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(
                        text = if (!isOtpSent) "إرسال رمز التحقق (OTP)" else "تأكيد الدخول",
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(modifier = Modifier.height(20.dp))
                HorizontalDivider()
                Spacer(modifier = Modifier.height(12.dp))

                // Quick Demo Accounts section
                Text(
                    text = "⚡ تجربة سريعة (اضغط للتعبئة والدخول مباشرة):",
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.height(8.dp))

                DemoAccountChip("👑 الإدارة العليا (Super Admin)", "0600000000") {
                    phoneInput = "0600000000"
                    isOtpSent = true
                    otpInput = "123456"
                    onLogin("0600000000", "واتساب")
                }
                DemoAccountChip("🏢 مسؤول العمارة - باقة نشطة (رشيد)", "0611111111") {
                    phoneInput = "0611111111"
                    isOtpSent = true
                    otpInput = "123456"
                    onLogin("0611111111", "واتساب")
                }
                DemoAccountChip("🏢 مسؤول جديد - تجربة الباقات والتجربة", "0622222222") {
                    phoneInput = "0622222222"
                    isOtpSent = true
                    otpInput = "123456"
                    onLogin("0622222222", "واتساب")
                }
                DemoAccountChip("🏠 ساكن شقة 01 - مالك (محمد السبتي)", "0633333333") {
                    phoneInput = "0633333333"
                    isOtpSent = true
                    otpInput = "123456"
                    onLogin("0633333333", "واتساب")
                }
                DemoAccountChip("🏠 ساكنة شقة 08 - مستأجرة (سناء)", "0644444444") {
                    phoneInput = "0644444444"
                    isOtpSent = true
                    otpInput = "123456"
                    onLogin("0644444444", "واتساب")
                }

                Spacer(modifier = Modifier.height(8.dp))
                TextButton(onClick = onDismiss) {
                    Text("إلغاء")
                }
            }
        }
    }
}

@Composable
fun DemoAccountChip(title: String, phone: String, onClick: () -> Unit) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 3.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(10.dp),
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f),
        border = CardDefaults.outlinedCardBorder()
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(title, style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.SemiBold)
            Text(phone, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.primary)
        }
    }
}

@Composable
fun RoleSwitchDialog(
    currentRole: UserRole,
    onRoleSelected: (UserRole) -> Unit,
    onDismiss: () -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Text(
                    text = "تبديل الصلاحيات والأدوار",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "يمكنك التبديل الفوري لمعاينة التطبيق كما يراه كل مستوى:",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(vertical = 8.dp)
                )

                RoleOptionCard(
                    title = "الإدارة العليا (أنت)",
                    desc = "إدارة المسؤولين، الباقات، تسجيل الدفعات خارج التطبيق، والتقارير الشاملة.",
                    isSelected = currentRole == UserRole.SUPER_ADMIN,
                    icon = Icons.Default.Shield
                ) { onRoleSelected(UserRole.SUPER_ADMIN) }

                RoleOptionCard(
                    title = "المسؤول (السنديك المتطوع)",
                    desc = "إدارة العمارة، السكان، نشر الإعلانات، معالجة الأعطاب، مراجعة الفواتير وحجوزات المرافق.",
                    isSelected = currentRole == UserRole.MANAGER,
                    icon = Icons.Default.AdminPanelSettings
                ) { onRoleSelected(UserRole.MANAGER) }

                RoleOptionCard(
                    title = "الساكن (مالك أو مستأجر)",
                    desc = "عرض الإعلانات، تقديم الشكاوى، رفع وصل إثبات الدفع، حجز المرافق، والتصويت.",
                    isSelected = currentRole == UserRole.RESIDENT,
                    icon = Icons.Default.Home
                ) { onRoleSelected(UserRole.RESIDENT) }

                Spacer(modifier = Modifier.height(12.dp))
                Button(
                    onClick = onDismiss,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("إغلاق")
                }
            }
        }
    }
}

@Composable
fun RoleOptionCard(
    title: String,
    desc: String,
    isSelected: Boolean,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    onClick: () -> Unit
) {
    Surface(
        shape = RoundedCornerShape(12.dp),
        color = if (isSelected) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f),
        border = if (isSelected) CardDefaults.outlinedCardBorder().copy(width = 2.dp) else null,
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
            .clickable { onClick() }
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.size(28.dp)
            )
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold,
                    color = if (isSelected) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = desc,
                    style = MaterialTheme.typography.bodySmall,
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            if (isSelected) {
                Icon(
                    imageVector = Icons.Default.CheckCircle,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}

// Add Manager Dialog (Super Admin)
@Composable
fun AddManagerDialog(
    plans: List<Plan>,
    onDismiss: () -> Unit,
    onConfirm: (name: String, phone: String, bldName: String, city: String, apts: Int, planId: String) -> Unit
) {
    var name by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var bldName by remember { mutableStateOf("") }
    var city by remember { mutableStateOf("الدار البيضاء") }
    var aptsText by remember { mutableStateOf("16") }
    var selectedPlanId by remember { mutableStateOf(plans.firstOrNull()?.id ?: "plan_basic") }

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
                Text("إضافة مسؤول وعمارة جديدة", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("اسم المسؤول المتطوع") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = phone,
                    onValueChange = { phone = it },
                    label = { Text("رقم هاتف المسؤول (لتسجيل الدخول)") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = bldName,
                    onValueChange = { bldName = it },
                    label = { Text("اسم العمارة أو الإقامة") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
                Spacer(modifier = Modifier.height(8.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = city,
                        onValueChange = { city = it },
                        label = { Text("المدينة") },
                        modifier = Modifier.weight(1f),
                        singleLine = true
                    )
                    OutlinedTextField(
                        value = aptsText,
                        onValueChange = { aptsText = it },
                        label = { Text("عدد الشقق") },
                        modifier = Modifier.weight(1f),
                        singleLine = true
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))
                Text("اختر الباقة المعينة للعمارة:", style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.SemiBold)
                Spacer(modifier = Modifier.height(6.dp))
                plans.forEach { p ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { selectedPlanId = p.id }
                            .padding(vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(selected = selectedPlanId == p.id, onClick = { selectedPlanId = p.id })
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "${p.name} (حتى ${p.maxApartments} شقة - ${p.monthlyPrice} درهم/شهر)",
                            style = MaterialTheme.typography.bodySmall,
                            fontWeight = if (selectedPlanId == p.id) FontWeight.Bold else FontWeight.Normal
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedButton(onClick = onDismiss, modifier = Modifier.weight(1f)) { Text("إلغاء") }
                    Button(
                        onClick = {
                            if (name.isNotBlank() && phone.isNotBlank() && bldName.isNotBlank()) {
                                onConfirm(name, phone, bldName, city, aptsText.toIntOrNull() ?: 10, selectedPlanId)
                            }
                        },
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("إضافة وتفعيل")
                    }
                }
            }
        }
    }
}

// Record Payment Dialog (Super Admin)
@Composable
fun RecordPaymentDialog(
    building: Building?,
    buildings: List<Building>,
    onDismiss: () -> Unit,
    onConfirm: (buildingId: String, amount: Double, method: String, months: Int, ref: String) -> Unit
) {
    var selectedBldId by remember { mutableStateOf(building?.id ?: buildings.firstOrNull()?.id ?: "") }
    var amountText by remember { mutableStateOf("299.0") }
    var method by remember { mutableStateOf("تحويل بنكي") }
    var monthsText by remember { mutableStateOf("1") }
    var reference by remember { mutableStateOf("") }

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
                Text("تسجيل دفعة اشتراك يدوية", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                Text(
                    text = "يتم تسجيل الدفع بعد استلام التحويل البنكي أو المبلغ نقداً، فيتم تمديد أو استعادة الاشتراك فوراً.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(vertical = 4.dp)
                )
                Spacer(modifier = Modifier.height(10.dp))

                Text("العمارة المستفيدة:", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold)
                buildings.forEach { b ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { selectedBldId = b.id }
                            .padding(vertical = 2.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(selected = selectedBldId == b.id, onClick = { selectedBldId = b.id })
                        Text(b.name, style = MaterialTheme.typography.bodySmall)
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = amountText,
                    onValueChange = { amountText = it },
                    label = { Text("المبلغ المستلم (درهم)") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
                Spacer(modifier = Modifier.height(8.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    FilterChip(
                        selected = method == "تحويل بنكي",
                        onClick = { method = "تحويل بنكي" },
                        label = { Text("تحويل بنكي") },
                        modifier = Modifier.weight(1f)
                    )
                    FilterChip(
                        selected = method == "نقداً (وصل)",
                        onClick = { method = "نقداً (وصل)" },
                        label = { Text("نقداً") },
                        modifier = Modifier.weight(1f)
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = monthsText,
                    onValueChange = { monthsText = it },
                    label = { Text("مدة التجديد (بالشهور)") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = reference,
                    onValueChange = { reference = it },
                    label = { Text("رقم العملية البنكية أو الوصل") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(16.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedButton(onClick = onDismiss, modifier = Modifier.weight(1f)) { Text("إلغاء") }
                    Button(
                        onClick = {
                            val amt = amountText.toDoubleOrNull() ?: 0.0
                            val m = monthsText.toIntOrNull() ?: 1
                            if (amt > 0 && selectedBldId.isNotBlank()) {
                                onConfirm(selectedBldId, amt, method, m, reference)
                            }
                        },
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("تأكيد وتجديد")
                    }
                }
            }
        }
    }
}

// Add Resident Dialog (Manager)
@Composable
fun AddResidentDialog(
    onDismiss: () -> Unit,
    onConfirm: (name: String, phone: String, apt: String, floor: Int, isOwner: Boolean) -> Unit
) {
    var name by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var aptNumber by remember { mutableStateOf("") }
    var floorText by remember { mutableStateOf("1") }
    var isOwner by remember { mutableStateOf(true) }

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
                Text("إضافة ساكن جديد للشقة", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                Text(
                    text = "يسمح بساكن واحد لكل شقة. يصله إشعار لدخول التطبيق برقم هاتفه وOTP.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(vertical = 4.dp)
                )
                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("اسم الساكن الكامل") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = phone,
                    onValueChange = { phone = it },
                    label = { Text("رقم الهاتف (للدخول بـ OTP)") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
                Spacer(modifier = Modifier.height(8.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = aptNumber,
                        onValueChange = { aptNumber = it },
                        label = { Text("رقم الشقة") },
                        modifier = Modifier.weight(1f),
                        singleLine = true
                    )
                    OutlinedTextField(
                        value = floorText,
                        onValueChange = { floorText = it },
                        label = { Text("الطابق") },
                        modifier = Modifier.weight(1f),
                        singleLine = true
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))
                Text("صفة الساكن:", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold)
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.padding(top = 4.dp)) {
                    FilterChip(
                        selected = isOwner,
                        onClick = { isOwner = true },
                        label = { Text("مالك الشقة") },
                        modifier = Modifier.weight(1f)
                    )
                    FilterChip(
                        selected = !isOwner,
                        onClick = { isOwner = false },
                        label = { Text("مستأجر") },
                        modifier = Modifier.weight(1f)
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedButton(onClick = onDismiss, modifier = Modifier.weight(1f)) { Text("إلغاء") }
                    Button(
                        onClick = {
                            if (name.isNotBlank() && phone.isNotBlank() && aptNumber.isNotBlank()) {
                                onConfirm(name, phone, aptNumber, floorText.toIntOrNull() ?: 1, isOwner)
                            }
                        },
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("إرسال الدعوة")
                    }
                }
            }
        }
    }
}

// Add Announcement Dialog (Manager)
@Composable
fun AddAnnouncementDialog(
    onDismiss: () -> Unit,
    onConfirm: (title: String, content: String, type: AnnouncementType, start: String?, end: String?) -> Unit
) {
    var title by remember { mutableStateOf("") }
    var content by remember { mutableStateOf("") }
    var selectedType by remember { mutableStateOf(AnnouncementType.TEMPORARY) }
    var startDate by remember { mutableStateOf("2026-10-26") }
    var endDate by remember { mutableStateOf("2026-10-29") }

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
                Text("نشر إعلان أو قواعد جديدة", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(12.dp))

                Text("نوع الإعلان:", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold)
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp), modifier = Modifier.padding(vertical = 4.dp)) {
                    FilterChip(
                        selected = selectedType == AnnouncementType.EMERGENCY,
                        onClick = { selectedType = AnnouncementType.EMERGENCY },
                        label = { Text("🚨 طارئ") },
                        modifier = Modifier.weight(1f)
                    )
                    FilterChip(
                        selected = selectedType == AnnouncementType.TEMPORARY,
                        onClick = { selectedType = AnnouncementType.TEMPORARY },
                        label = { Text("📅 مؤقت") },
                        modifier = Modifier.weight(1f)
                    )
                    FilterChip(
                        selected = selectedType == AnnouncementType.PERMANENT,
                        onClick = { selectedType = AnnouncementType.PERMANENT },
                        label = { Text("📜 دائم / قواعد") },
                        modifier = Modifier.weight(1f)
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("عنوان الإعلان") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = content,
                    onValueChange = { content = it },
                    label = { Text("نص الإعلان أو التعليمات") },
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 3
                )

                if (selectedType == AnnouncementType.TEMPORARY) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(
                            value = startDate,
                            onValueChange = { startDate = it },
                            label = { Text("تاريخ البداية") },
                            modifier = Modifier.weight(1f),
                            singleLine = true
                        )
                        OutlinedTextField(
                            value = endDate,
                            onValueChange = { endDate = it },
                            label = { Text("تاريخ النهاية") },
                            modifier = Modifier.weight(1f),
                            singleLine = true
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedButton(onClick = onDismiss, modifier = Modifier.weight(1f)) { Text("إلغاء") }
                    Button(
                        onClick = {
                            if (title.isNotBlank() && content.isNotBlank()) {
                                onConfirm(
                                    title,
                                    content,
                                    selectedType,
                                    if (selectedType == AnnouncementType.TEMPORARY) startDate else null,
                                    if (selectedType == AnnouncementType.TEMPORARY) endDate else null
                                )
                            }
                        },
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("نشر الآن")
                    }
                }
            }
        }
    }
}

// Add Complaint Dialog (Resident)
@Composable
fun AddComplaintDialog(
    apartmentNumber: String,
    onDismiss: () -> Unit,
    onConfirm: (title: String, desc: String, category: ComplaintCategory, apt: String, photoNote: String?) -> Unit
) {
    var title by remember { mutableStateOf("") }
    var desc by remember { mutableStateOf("") }
    var category by remember { mutableStateOf(ComplaintCategory.ELEVATOR) }
    var hasPhoto by remember { mutableStateOf(false) }

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
                Text("تقديم شكوى أو طلب صيانة", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                Text(
                    text = "شقتك: $apartmentNumber - ستصل رسالة مباشرة لمسؤول العمارة لمتابعة العطب.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(vertical = 4.dp)
                )
                Spacer(modifier = Modifier.height(12.dp))

                Text("تصنيف العطب:", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold)
                Column(verticalArrangement = Arrangement.spacedBy(4.dp), modifier = Modifier.padding(top = 4.dp)) {
                    ComplaintCategory.values().forEach { cat ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { category = cat }
                                .padding(vertical = 2.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            RadioButton(selected = category == cat, onClick = { category = cat })
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(cat.labelAr, style = MaterialTheme.typography.bodySmall)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("عنوان المشكل (مثال: تسرب ماء، مصعد معطل)") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = desc,
                    onValueChange = { desc = it },
                    label = { Text("وصف دقيق للمشكل ومكانه") },
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 3
                )

                Spacer(modifier = Modifier.height(8.dp))
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { hasPhoto = !hasPhoto }
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = if (hasPhoto) Icons.Default.CheckCircle else Icons.Default.AddPhotoAlternate,
                            contentDescription = null,
                            tint = if (hasPhoto) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = if (hasPhoto) "تم إرفاق صورة العطب بنجاح ✅" else "إرفاق صورة توضيحية (اختياري)",
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedButton(onClick = onDismiss, modifier = Modifier.weight(1f)) { Text("إلغاء") }
                    Button(
                        onClick = {
                            if (title.isNotBlank() && desc.isNotBlank()) {
                                onConfirm(
                                    title,
                                    desc,
                                    category,
                                    apartmentNumber,
                                    if (hasPhoto) "صورة عطب مرفقة" else null
                                )
                            }
                        },
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("إرسال الطلب")
                    }
                }
            }
        }
    }
}

// Upload Proof of Payment Dialog (Resident)
@Composable
fun UploadProofDialog(
    bill: BillItem,
    onDismiss: () -> Unit,
    onConfirm: (note: String) -> Unit
) {
    var note by remember { mutableStateOf("تم التحويل عبر التطبيق البنكي") }

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
                Text("رفع إثبات دفع الفاتورة", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(8.dp))

                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text("فاتورة: ${bill.description}", style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Bold)
                        Text("المبلغ المطلوب: ${bill.amount} درهم (${bill.month})", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.primary)
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))
                OutlinedTextField(
                    value = note,
                    onValueChange = { note = it },
                    label = { Text("تفاصيل التحويل أو رقم العملية") },
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 2
                )

                Spacer(modifier = Modifier.height(12.dp))
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.4f),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.Receipt, contentDescription = null, tint = MaterialTheme.colorScheme.secondary)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("تم اختيار ملف صورة الوصل البنكي (Reçu bancaire)", style = MaterialTheme.typography.bodySmall)
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedButton(onClick = onDismiss, modifier = Modifier.weight(1f)) { Text("إلغاء") }
                    Button(
                        onClick = { onConfirm(note) },
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("تأكيد ورفع الإثبات")
                    }
                }
            }
        }
    }
}

// Book Facility Dialog
@Composable
fun BookFacilityDialog(
    facility: Facility,
    onDismiss: () -> Unit,
    onConfirm: (date: String, slot: String) -> Unit
) {
    var selectedDate by remember { mutableStateOf("2026-10-28") }
    var selectedSlot by remember { mutableStateOf("16:00 - 18:00") }

    val availableSlots = listOf(
        "10:00 - 12:00",
        "14:00 - 16:00",
        "16:00 - 18:00",
        "18:00 - 20:00"
    )

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
                Text("حجز مرفق مشترك", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                Text(
                    text = facility.name,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.padding(top = 2.dp)
                )

                Spacer(modifier = Modifier.height(10.dp))
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(10.dp)) {
                        Text("قواعد الاستخدام: ${facility.rules}", style = MaterialTheme.typography.bodySmall, fontSize = 11.sp)
                        Text("ساعات العمل: ${facility.openHours}", style = MaterialTheme.typography.bodySmall, fontSize = 11.sp)
                        Text("أقصى مدة للحجز: ${facility.maxDurationHours} ساعات", style = MaterialTheme.typography.bodySmall, fontSize = 11.sp)
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))
                OutlinedTextField(
                    value = selectedDate,
                    onValueChange = { selectedDate = it },
                    label = { Text("تاريخ الحجز (YYYY-MM-DD)") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(10.dp))
                Text("الفترة الزمنية المطلوبة:", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(4.dp))
                availableSlots.forEach { slot ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { selectedSlot = slot }
                            .padding(vertical = 3.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(selected = selectedSlot == slot, onClick = { selectedSlot = slot })
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(slot, style = MaterialTheme.typography.bodySmall)
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedButton(onClick = onDismiss, modifier = Modifier.weight(1f)) { Text("إلغاء") }
                    Button(
                        onClick = { onConfirm(selectedDate, selectedSlot) },
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("تأكيد الحجز")
                    }
                }
            }
        }
    }
}

@Composable
fun UploadSubscriptionProofDialog(
    onDismiss: () -> Unit,
    onConfirm: (amount: Double, receiptUrl: String?, note: String?) -> Unit
) {
    var amountInput by remember { mutableStateOf("150") }
    var noteInput by remember { mutableStateOf("") }
    var hasSimulatedPhoto by remember { mutableStateOf(false) }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(20.dp),
            modifier = Modifier.fillMaxWidth().padding(12.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
            Column(
                modifier = Modifier
                    .padding(20.dp)
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    modifier = Modifier
                        .size(50.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.primaryContainer),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Default.ReceiptLong, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                }

                Spacer(modifier = Modifier.height(10.dp))
                Text(
                    text = "رفع إيصال تحويل الاشتراك",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = "رفع الإيصال اختياري. يمكنك رفع صورة الإشعار البنكي أو إرساله مباشرة للإدارة للتأكيد الفوري.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(top = 4.dp, bottom = 12.dp)
                )

                OutlinedTextField(
                    value = amountInput,
                    onValueChange = { amountInput = it },
                    label = { Text("المبلغ المحول (درهم مغربي MAD)") },
                    leadingIcon = { Icon(Icons.Default.Paid, contentDescription = null) },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(10.dp))
                OutlinedTextField(
                    value = noteInput,
                    onValueChange = { noteInput = it },
                    label = { Text("ملاحظة أو رقم مرجع العملية (اختياري)") },
                    leadingIcon = { Icon(Icons.Default.Notes, contentDescription = null) },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(14.dp))

                // Simulated receipt upload button
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = if (hasSimulatedPhoto) MaterialTheme.colorScheme.secondaryContainer else MaterialTheme.colorScheme.surfaceVariant,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { hasSimulatedPhoto = !hasSimulatedPhoto }
                        .padding(vertical = 4.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            imageVector = if (hasSimulatedPhoto) Icons.Default.CheckCircle else Icons.Default.AddPhotoAlternate,
                            contentDescription = null,
                            tint = if (hasSimulatedPhoto) MaterialTheme.colorScheme.secondary else MaterialTheme.colorScheme.primary
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = if (hasSimulatedPhoto) "تم إرفاق صورة إيصال التحويل ✓" else "إرفاق صورة إيصال التحويل (اختياري)",
                            style = MaterialTheme.typography.bodySmall,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedButton(onClick = onDismiss, modifier = Modifier.weight(1f)) {
                        Text("إلغاء")
                    }
                    Button(
                        onClick = {
                            val parsedAmount = amountInput.toDoubleOrNull() ?: 0.0
                            val receipt = if (hasSimulatedPhoto) "receipt_virement_img.jpg" else null
                            onConfirm(parsedAmount, receipt, noteInput.ifBlank { null })
                        },
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("إرسال الإيصال")
                    }
                }
            }
        }
    }
}

@Composable
fun EditRibDialog(
    initialRib: String?,
    onDismiss: () -> Unit,
    onSave: (rib: String?) -> Unit
) {
    var ribInput by remember { mutableStateOf(initialRib ?: "") }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    val cleanDigits = MoroccanRibUtil.clean(ribInput)
    val detectedBank = MoroccanRibUtil.getBankName(cleanDigits)

    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(20.dp),
            modifier = Modifier.fillMaxWidth().padding(12.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
            Column(
                modifier = Modifier
                    .padding(20.dp)
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.primaryContainer),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Default.AccountBalance, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                }

                Spacer(modifier = Modifier.height(10.dp))
                Text(
                    text = "الحساب البنكي للعمارة (RIB)",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "تسجيل RIB اختياري لتسهيل تحويلات السكان البنكية. يتكون RIB المغربي من 24 رقماً حصراً.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(top = 4.dp, bottom = 12.dp)
                )

                OutlinedTextField(
                    value = ribInput,
                    onValueChange = {
                        val numeric = it.filter { char -> char.isDigit() || char.isWhitespace() }
                        ribInput = numeric
                        errorMessage = null
                    },
                    label = { Text("رقم RIB (24 رقماً)") },
                    placeholder = { Text("011 780 0000123456789012 96") },
                    leadingIcon = { Icon(Icons.Default.CreditCard, contentDescription = null) },
                    isError = errorMessage != null,
                    supportingText = {
                        if (errorMessage != null) {
                            Text(errorMessage!!, color = MaterialTheme.colorScheme.error)
                        } else {
                            Text("الأرقام المُدخلة: ${cleanDigits.length} / 24")
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                )

                if (detectedBank != null) {
                    Spacer(modifier = Modifier.height(6.dp))
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.5f),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(modifier = Modifier.padding(8.dp), verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Verified, contentDescription = null, tint = MaterialTheme.colorScheme.secondary, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("البنك المكتشف: $detectedBank", style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.SemiBold)
                        }
                    }
                }

                if (cleanDigits.length == 24) {
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "الشكل المعروض: ${MoroccanRibUtil.format(cleanDigits)}",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedButton(onClick = onDismiss, modifier = Modifier.weight(1f)) {
                        Text("إلغاء")
                    }
                    Button(
                        onClick = {
                            if (cleanDigits.isEmpty()) {
                                // Empty is allowed as RIB is optional!
                                onSave(null)
                                return@Button
                            }
                            val validation = MoroccanRibUtil.validate(cleanDigits)
                            if (validation.first) {
                                onSave(MoroccanRibUtil.format(cleanDigits))
                            } else {
                                errorMessage = "رقم RIB غير صحيح، تحقق منه"
                            }
                        },
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("حفظ")
                    }
                }

                if (initialRib != null) {
                    Spacer(modifier = Modifier.height(8.dp))
                    TextButton(
                        onClick = { onSave(null) }
                    ) {
                        Text("حذف الحساب البنكي (جعل الدفع نقداً فقط)", color = MaterialTheme.colorScheme.error)
                    }
                }
            }
        }
    }
}

@Composable
fun LanguageSelectionDialog(
    currentLang: AppLanguage,
    onDismiss: () -> Unit,
    onSelectLanguage: (AppLanguage) -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(20.dp),
            modifier = Modifier.fillMaxWidth().padding(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
            Column(
                modifier = Modifier.padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.primaryContainer),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Default.Language, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                }

                Spacer(modifier = Modifier.height(10.dp))
                Text(
                    text = "اختيار لغة التطبيق / Langue / Language",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(14.dp))

                AppLanguage.values().forEach { lang ->
                    val isSelected = lang == currentLang
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = if (isSelected) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                        border = if (isSelected) androidx.compose.foundation.BorderStroke(1.5.dp, MaterialTheme.colorScheme.primary) else null,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp)
                            .clickable { onSelectLanguage(lang) }
                    ) {
                        Row(
                            modifier = Modifier.padding(14.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(lang.flag, fontSize = 22.sp)
                            Spacer(modifier = Modifier.width(12.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(lang.title, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.bodyMedium)
                                Text(
                                    when (lang) {
                                        AppLanguage.ARABIC -> "المملكة المغربية"
                                        AppLanguage.FRENCH -> "Maroc - Français"
                                        AppLanguage.ENGLISH -> "International English"
                                    },
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                            if (isSelected) {
                                Icon(Icons.Default.CheckCircle, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))
                OutlinedButton(onClick = onDismiss, modifier = Modifier.fillMaxWidth()) {
                    Text("إغلاق / Fermer / Close")
                }
            }
        }
    }
}


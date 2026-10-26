package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.data.ImartiRepository
import com.example.model.ProofStatus
import com.example.model.UserRole
import com.example.util.MoroccanRibUtil
import org.junit.Assert.*
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class ExampleRobolectricTest {

    @Test
    fun `read string from context`() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val appName = context.getString(R.string.app_name)
        assertNotNull(appName)
        assertTrue(appName.isNotBlank())
    }

    @Test
    fun `test moroccan rib validation and checksum`() {
        // Valid 24-digit Moroccan RIB (Bank of Africa - BMCE)
        val validRib = "011 780 0000123456789012 96"
        val (isValid, message) = MoroccanRibUtil.validate(validRib)
        assertTrue("Expected valid RIB to pass", isValid)
        assertEquals("011 780 0000123456789012 96", MoroccanRibUtil.format(validRib))
        assertNotNull(MoroccanRibUtil.getBankName(validRib))

        // Invalid key
        val invalidKeyRib = "011 780 0000123456789012 99"
        val (isInvalid, err) = MoroccanRibUtil.validate(invalidKeyRib)
        assertFalse("Expected invalid key to fail", isInvalid)
        assertTrue("Should contain error message", err.contains("رقم RIB غير صحيح، تحقق منه"))

        // Short length
        val shortRib = "011 780 1234"
        val (isShortValid, _) = MoroccanRibUtil.validate(shortRib)
        assertFalse("Expected short RIB to fail", isShortValid)
    }

    @Test
    fun `test repository session and subscription proof workflow`() {
        val repo = ImartiRepository()
        assertNotNull(repo.session.value)

        // Login as manager
        val loginResult = repo.loginWithPhone("0611111111")
        assertTrue(loginResult is ImartiRepository.LoginResult.Success)
        assertEquals(UserRole.MANAGER, repo.session.value.role)

        // Upload subscription proof
        val initialProofCount = repo.subscriptionProofs.value.size
        val (ok, msg) = repo.uploadSubscriptionProof(
            buildingId = "bld-1",
            amount = 150.0,
            receiptNote = "إشعار تحويل بنكي تجريبي رقم REF-1234"
        )
        assertTrue(ok)
        assertEquals(initialProofCount + 1, repo.subscriptionProofs.value.size)

        val latestProof = repo.subscriptionProofs.value.first()
        assertEquals(ProofStatus.PENDING, latestProof.status)

        // Confirm proof by Super Admin
        repo.confirmSubscriptionProof(latestProof.id, durationMonths = 1)
        val confirmedProof = repo.subscriptionProofs.value.first { it.id == latestProof.id }
        assertEquals(ProofStatus.CONFIRMED, confirmedProof.status)
        assertNotNull(confirmedProof.confirmedAt)
    }
}

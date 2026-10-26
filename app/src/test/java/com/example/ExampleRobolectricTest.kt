package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.data.ImartiRepository
import com.example.model.ExpenseCategory
import com.example.model.UserRole
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
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
        assertEquals("Imarti", appName)
    }

    @Test
    fun `test repository calculations and report generation`() {
        val repo = ImartiRepository()
        assertEquals(UserRole.RESIDENT, repo.currentRole.value)

        repo.setRole(UserRole.SYNDIC)
        assertEquals(UserRole.SYNDIC, repo.currentRole.value)

        val initialExpenseCount = repo.expenses.value.size
        repo.addExpense(
            title = "إصلاح مصباح المدخل",
            amount = 120.0,
            category = ExpenseCategory.ELECTRICITY,
            invoiceNumber = "FACT-01",
            paidTo = "كهربائي",
            notes = "تركيب مصباح LED"
        )
        assertEquals(initialExpenseCount + 1, repo.expenses.value.size)

        val report = repo.generateWhatsAppReport(4500.0, 5000.0, repo.expenses.value, 6000.0)
        assertTrue(report.contains("التقرير المالي الشهري"))
        assertTrue(report.contains("Imarti"))
    }
}

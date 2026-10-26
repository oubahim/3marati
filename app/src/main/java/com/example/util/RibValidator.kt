package com.example.util

import java.math.BigInteger

object MoroccanRibUtil {

    // Common Moroccan Bank Codes (3 digits)
    val knownMoroccanBanks = mapOf(
        "007" to "التجاري وفا بنك (Attijariwafa Bank)",
        "011" to "بنك أفريقيا (Bank of Africa - BMCE)",
        "013" to "البنك الشعبي (Banque Populaire)",
        "021" to "القرض الفلاحي للمغرب (Crédit Agricole du Maroc)",
        "022" to "الشركة العامة المغربية للأبناك (Société Générale)",
        "028" to "مصرف المغرب (Crédit du Maroc)",
        "230" to "بنك سياش (CIH Bank)",
        "350" to "البريد بنك (Al Barid Bank)",
        "182" to "البنك المغربي للتجارة والصناعة (BMCI)",
        "050" to "بنك المغرب (Bank Al-Maghrib)"
    )

    fun clean(rib: String?): String {
        if (rib == null) return ""
        return rib.replace("[^0-9]".toRegex(), "")
    }

    fun format(rib: String?): String {
        val cleaned = clean(rib)
        if (cleaned.length != 24) return rib ?: ""
        // Formatted as: 3 digits (bank) - 3 digits (agency) - 16 digits (account) - 2 digits (key)
        val bank = cleaned.substring(0, 3)
        val agency = cleaned.substring(3, 6)
        val account = cleaned.substring(6, 22)
        val key = cleaned.substring(22, 24)
        return "$bank $agency $account $key"
    }

    fun getBankName(rib: String?): String? {
        val cleaned = clean(rib)
        if (cleaned.length < 3) return null
        val bankCode = cleaned.substring(0, 3)
        return knownMoroccanBanks[bankCode]
    }

    /**
     * Standard Moroccan RIB Checksum validation:
     * Length = 24 digits
     * Key = 97 - ((BigInteger(22 digits) * 100) % 97)
     */
    fun validate(rib: String?): Pair<Boolean, String> {
        val cleaned = clean(rib)
        if (cleaned.isEmpty()) {
            return Pair(false, "رقم RIB فارغ")
        }
        if (cleaned.length != 24) {
            return Pair(false, "رقم RIB يجب أن يتكون من 24 رقماً بالضبط (المُدخل حالياً: ${cleaned.length} رقماً)")
        }

        return try {
            val base22 = cleaned.substring(0, 22)
            val providedKey = cleaned.substring(22, 24).toInt()

            val baseInt = BigInteger(base22)
            val remainder = baseInt.multiply(BigInteger.valueOf(100)).remainder(BigInteger.valueOf(97)).toInt()
            var calculatedKey = 97 - remainder
            if (calculatedKey == 0) calculatedKey = 97

            if (calculatedKey == providedKey) {
                Pair(true, "رقم RIB صحيح ومطابق للمواصفات البنكية المغربية")
            } else {
                Pair(false, "رقم RIB غير صحيح، تحقق منه (مفتاح الحساب غير مطابق)")
            }
        } catch (e: Exception) {
            Pair(false, "رقم RIB غير صحيح، تحقق منه")
        }
    }

    fun computeKey(base22: String): String {
        return try {
            val clean = base22.replace("[^0-9]".toRegex(), "").take(22)
            if (clean.length < 22) return "00"
            val baseInt = BigInteger(clean)
            val remainder = baseInt.multiply(BigInteger.valueOf(100)).remainder(BigInteger.valueOf(97)).toInt()
            var calculatedKey = 97 - remainder
            if (calculatedKey == 0) calculatedKey = 97
            String.format("%02d", calculatedKey)
        } catch (e: Exception) {
            "00"
        }
    }
}

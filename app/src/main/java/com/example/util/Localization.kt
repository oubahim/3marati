package com.example.util

enum class AppLanguage(val code: String, val title: String, val flag: String) {
    ARABIC("ar", "العربية", "🇲🇦"),
    FRENCH("fr", "Français", "🇫🇷"),
    ENGLISH("en", "English", "🇬🇧")
}

object StringsHelper {

    fun get(key: String, lang: AppLanguage): String {
        return when (lang) {
            AppLanguage.ARABIC -> arabicStrings[key] ?: key
            AppLanguage.FRENCH -> frenchStrings[key] ?: arabicStrings[key] ?: key
            AppLanguage.ENGLISH -> englishStrings[key] ?: arabicStrings[key] ?: key
        }
    }

    private val arabicStrings = mapOf(
        "app_title" to "عمارتي",
        "home" to "الرئيسية",
        "announcements" to "الإعلانات",
        "complaints" to "الشكاوى",
        "finances" to "الفواتير والمرافق",
        "residents" to "السكان والدليل",
        "super_admin" to "الإدارة العليا",
        "manager" to "المسؤول",
        "resident" to "الساكن",
        "bank_transfer" to "تحويل بنكي",
        "cash" to "نقداً",
        "rib_number" to "رقم الحساب البنكي (RIB)",
        "copy_rib" to "نسخ رقم RIB كاملاً",
        "rib_copied" to "تم نسخ رقم RIB إلى الحافظة بنجاح",
        "no_rib_configured" to "لم يتم تسجيل حساب بنكي للعمارة بعد (الدفع نقداً أو حسب الاتفاق مع المسؤول)",
        "upload_subscription_receipt" to "رفع إيصال تحويل الاشتراك (اختياري)",
        "pending_verification" to "بانتظار التحقق",
        "confirmed" to "مؤكَّدة",
        "rejected" to "مرفوضة",
        "add_rib_gentle_tip" to "💡 أضف حسابك البنكي ليسهل على السكان الدفع عبر التحويل البنكي.",
        "ignore" to "تجاهل",
        "add_rib_now" to "إضافة RIB الآن",
        "save_rib" to "حفظ وتأكيد RIB",
        "remove_rib" to "حذف الحساب البنكي",
        "enter_24_digits_rib" to "أدخل 24 رقماً للـ RIB المغربي",
        "rib_invalid_error" to "رقم RIB غير صحيح، تحقق منه",
        "status" to "الحالة",
        "amount" to "المبلغ",
        "date" to "التاريخ",
        "proof_receipt" to "إيصال التحويل",
        "confirm_payment" to "تأكيد الدفعة وتفعيل الاشتراك",
        "switch_language" to "تغيير لغة التطبيق"
    )

    private val frenchStrings = mapOf(
        "app_title" to "Imarti",
        "home" to "Accueil",
        "announcements" to "Annonces",
        "complaints" to "Réclamations",
        "finances" to "Charges & Espaces",
        "residents" to "Résidents & Répertoire",
        "super_admin" to "Super Admin",
        "manager" to "Syndic / Gestionnaire",
        "resident" to "Résident",
        "bank_transfer" to "Virement bancaire",
        "cash" to "Espèces",
        "rib_number" to "Relevé d'Identité Bancaire (RIB)",
        "copy_rib" to "Copier le RIB complet",
        "rib_copied" to "RIB copié dans le presse-papier avec succès",
        "no_rib_configured" to "Aucun RIB bancaire configuré (Paiement en espèces ou convenu avec le syndic)",
        "upload_subscription_receipt" to "Téléverser le reçu de virement (facultatif)",
        "pending_verification" to "En attente de vérification",
        "confirmed" to "Confirmée",
        "rejected" to "Rejetée",
        "add_rib_gentle_tip" to "💡 Ajoutez votre RIB bancaire pour simplifier le paiement des résidents par virement.",
        "ignore" to "Ignorer",
        "add_rib_now" to "Ajouter le RIB",
        "save_rib" to "Enregistrer le RIB",
        "remove_rib" to "Supprimer le RIB",
        "enter_24_digits_rib" to "Saisissez les 24 chiffres du RIB marocain",
        "rib_invalid_error" to "Numéro de RIB invalide, veuillez vérifier",
        "status" to "Statut",
        "amount" to "Montant",
        "date" to "Date",
        "proof_receipt" to "Reçu de virement",
        "confirm_payment" to "Confirmer et activer l'abonnement",
        "switch_language" to "Changer de langue"
    )

    private val englishStrings = mapOf(
        "app_title" to "Imarti",
        "home" to "Home",
        "announcements" to "Announcements",
        "complaints" to "Maintenance",
        "finances" to "Dues & Facilities",
        "residents" to "Residents & Directory",
        "super_admin" to "Super Admin",
        "manager" to "Building Manager",
        "resident" to "Resident",
        "bank_transfer" to "Bank Transfer",
        "cash" to "Cash",
        "rib_number" to "Bank Account Number (RIB)",
        "copy_rib" to "Copy full RIB",
        "rib_copied" to "RIB copied to clipboard successfully",
        "no_rib_configured" to "No bank RIB registered yet (Cash payment or per manager agreement)",
        "upload_subscription_receipt" to "Upload subscription transfer receipt (Optional)",
        "pending_verification" to "Pending Verification",
        "confirmed" to "Confirmed",
        "rejected" to "Rejected",
        "add_rib_gentle_tip" to "💡 Add your bank RIB to make it easier for residents to pay dues via bank transfer.",
        "ignore" to "Dismiss",
        "add_rib_now" to "Add RIB Now",
        "save_rib" to "Save & Verify RIB",
        "remove_rib" to "Remove Bank Account",
        "enter_24_digits_rib" to "Enter the 24-digit Moroccan RIB",
        "rib_invalid_error" to "Invalid RIB number, please verify",
        "status" to "Status",
        "amount" to "Amount",
        "date" to "Date",
        "proof_receipt" to "Transfer Receipt",
        "confirm_payment" to "Confirm and activate subscription",
        "switch_language" to "Change Language"
    )
}

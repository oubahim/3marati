package com.example.data

import com.example.model.*

object SampleData {

    val defaultPlans = listOf(
        Plan(
            id = "plan_basic",
            name = "أساسية",
            maxApartments = 10,
            monthlyPrice = 149.0,
            description = "مناسبة للعمارات الصغيرة، تشمل الإعلانات والشكاوى ودليل الطوارئ والسكان.",
            hasAnnouncements = true,
            hasComplaints = true,
            hasDirectory = true,
            hasBillsAndReceipts = false,
            hasFacilityBooking = false,
            hasPolls = false,
            hasAdvancedReports = false
        ),
        Plan(
            id = "plan_medium",
            name = "متوسطة",
            maxApartments = 30,
            monthlyPrice = 299.0,
            description = "الأكثر طلباً للعمارات المتوسطة: تشمل كل ميزات الأساسية + الفواتير وإثباتات الدفع وحجز المرافق المشتركة.",
            hasAnnouncements = true,
            hasComplaints = true,
            hasDirectory = true,
            hasBillsAndReceipts = true,
            hasFacilityBooking = true,
            hasPolls = false,
            hasAdvancedReports = false
        ),
        Plan(
            id = "plan_advanced",
            name = "متقدمة",
            maxApartments = 100,
            monthlyPrice = 549.0,
            description = "الحل الشامل للإقامات الكبرى: جميع الميزات + التصويت والاستطلاعات الرسمية والتقارير المالية المتقدمة.",
            hasAnnouncements = true,
            hasComplaints = true,
            hasDirectory = true,
            hasBillsAndReceipts = true,
            hasFacilityBooking = true,
            hasPolls = true,
            hasAdvancedReports = true
        )
    )

    val demoBuildings = listOf(
        Building(
            id = "bld-1",
            name = "إقامة الأندلس - عمارة B",
            address = "زنقة ابن بطوطة، حي المعاريف",
            city = "الدار البيضاء",
            totalApartments = 24,
            managerId = "mgr-1",
            managerName = "رشيد المنصوري",
            managerPhone = "0611111111",
            bankRib = "011 780 0000 1234567890 12",
            isActive = true
        ),
        Building(
            id = "bld-2",
            name = "عمارة الياسمين",
            address = "شارع الحسن الثاني، حي أكدال",
            city = "الرباط",
            totalApartments = 8,
            managerId = "mgr-2",
            managerName = "حمزة الشريف",
            managerPhone = "0655555555",
            bankRib = "022 560 0000 9876543210 34",
            isActive = true
        ),
        Building(
            id = "bld-3",
            name = "برج النخيل السكني",
            address = "شارع محمد السادس، جليز",
            city = "مراكش",
            totalApartments = 40,
            managerId = "mgr-3",
            managerName = "عمر العلمي",
            managerPhone = "0677777777",
            bankRib = "033 110 0000 5544332211 56",
            isActive = true
        )
    )

    val demoSubscriptions = listOf(
        BuildingSubscription(
            id = "sub-1",
            buildingId = "bld-1",
            managerPhone = "0611111111",
            planId = "plan_medium",
            status = SubscriptionStatus.ACTIVE,
            startDate = "2026-10-01",
            endDate = "2026-11-01",
            trialUsed = true,
            daysRemaining = 21,
            inGracePeriod = false,
            notes = "اشتراك سنوي مدفوع عبر تحويل بنكي"
        ),
        BuildingSubscription(
            id = "sub-2",
            buildingId = "bld-2",
            managerPhone = "0655555555",
            planId = "plan_basic",
            status = SubscriptionStatus.GRACE_PERIOD,
            startDate = "2026-09-20",
            endDate = "2026-10-23",
            trialUsed = true,
            daysRemaining = 2,
            inGracePeriod = true,
            notes = "مهلة سماح لمدة 3 أيام، التذكير قيد المتابعة"
        ),
        BuildingSubscription(
            id = "sub-3",
            buildingId = "bld-3",
            managerPhone = "0677777777",
            planId = "plan_advanced",
            status = SubscriptionStatus.READ_ONLY,
            startDate = "2026-10-10",
            endDate = "2026-10-17",
            trialUsed = true,
            daysRemaining = 0,
            inGracePeriod = false,
            notes = "انتهت فترة التجربة المجانية ولم يُسجَّل اشتراك بعد"
        )
    )

    val demoPayments = listOf(
        PaymentRecord(
            id = "pay-1",
            buildingId = "bld-1",
            buildingName = "إقامة الأندلس - عمارة B",
            managerName = "رشيد المنصوري",
            amount = 2990.0,
            paymentMethod = "تحويل بنكي (Tirage CIH)",
            date = "2026-10-01",
            durationMonths = 12,
            reference = "VIR-CIH-889021",
            recordedBy = "الإدارة العليا"
        ),
        PaymentRecord(
            id = "pay-2",
            buildingId = "bld-2",
            buildingName = "عمارة الياسمين",
            managerName = "حمزة الشريف",
            amount = 447.0,
            paymentMethod = "نقداً (وصل استلام)",
            date = "2026-07-20",
            durationMonths = 3,
            reference = "REC-CASH-104",
            recordedBy = "الإدارة العليا"
        )
    )

    val demoResidents = listOf(
        ResidentAccount("res-1", "محمد السبتي", "0633333333", "01", 1, isOwner = true, ResidentStatus.ACTIVE, isPhoneVisibleInDirectory = true, 250.0, "2026-10-01"),
        ResidentAccount("res-2", "فاطمة الزهراء بناني", "0663223344", "02", 1, isOwner = true, ResidentStatus.ACTIVE, isPhoneVisibleInDirectory = true, 250.0, "2026-10-02"),
        ResidentAccount("res-3", "كريم العلمي", "0664334455", "03", 1, isOwner = false, ResidentStatus.ACTIVE, isPhoneVisibleInDirectory = false, 250.0, "2026-10-03"),
        ResidentAccount("res-4", "عمر الفاسي", "0665445566", "04", 1, isOwner = true, ResidentStatus.ACTIVE, isPhoneVisibleInDirectory = true, 250.0, "2026-09-02"),
        ResidentAccount("res-5", "أمينة التازي", "0666556677", "05", 2, isOwner = true, ResidentStatus.ACTIVE, isPhoneVisibleInDirectory = true, 250.0, "2026-10-04"),
        ResidentAccount("res-6", "يوسف بن جلون", "0667667788", "06", 2, isOwner = true, ResidentStatus.ACTIVE, isPhoneVisibleInDirectory = true, 250.0, "2026-10-02"),
        ResidentAccount("res-7", "طارق الشرايبي", "0668778899", "07", 2, isOwner = true, ResidentStatus.ACTIVE, isPhoneVisibleInDirectory = true, 250.0, "2026-08-01"),
        ResidentAccount("res-8", "سناء برادة", "0644444444", "08", 2, isOwner = false, ResidentStatus.ACTIVE, isPhoneVisibleInDirectory = true, 250.0, "2026-10-05"),
        ResidentAccount("res-9", "عزيز الحداوي", "0670123456", "09", 3, isOwner = true, ResidentStatus.ACTIVE, isPhoneVisibleInDirectory = true, 250.0, "2026-10-06"),
        ResidentAccount("res-10", "مريم الصقلي", "0671234567", "10", 3, isOwner = true, ResidentStatus.ACTIVE, isPhoneVisibleInDirectory = false, 250.0, "2026-10-01"),
        ResidentAccount("res-11", "حسن الوزاني", "0672345678", "11", 3, isOwner = true, ResidentStatus.ACTIVE, isPhoneVisibleInDirectory = true, 250.0, "2026-07-01"),
        ResidentAccount("res-12", "ليلى الشرقاوي", "0673456789", "12", 3, isOwner = false, ResidentStatus.ACTIVE, isPhoneVisibleInDirectory = true, 250.0, "2026-10-02"),
        ResidentAccount("res-13", "حميد الناصري", "0674567890", "13", 4, isOwner = true, ResidentStatus.INVITED, isPhoneVisibleInDirectory = true, 250.0, "2026-10-03"),
        ResidentAccount("res-14", "نادية الإدريسي", "0675678901", "14", 4, isOwner = true, ResidentStatus.ACTIVE, isPhoneVisibleInDirectory = true, 250.0, "2026-10-07"),
        ResidentAccount("res-15", "سمير المرابط", "0676789012", "15", 4, isOwner = true, ResidentStatus.ACTIVE, isPhoneVisibleInDirectory = true, 250.0, "2026-09-01"),
        ResidentAccount("res-16", "زينب القادري", "0677890123", "16", 4, isOwner = false, ResidentStatus.ACTIVE, isPhoneVisibleInDirectory = false, 250.0, "2026-10-01"),
        ResidentAccount("res-17", "مصطفى اليعقوبي", "0678901234", "17", 5, isOwner = true, ResidentStatus.ACTIVE, isPhoneVisibleInDirectory = true, 250.0, "2026-10-04"),
        ResidentAccount("res-18", "خديجة الكتاني", "0679012345", "18", 5, isOwner = true, ResidentStatus.ACTIVE, isPhoneVisibleInDirectory = true, 250.0, "2026-10-02")
    )

    val demoAnnouncements = listOf(
        Announcement(
            id = "ann-urgent-1",
            title = "⚠️ طارئ: انقطاع الماء الساخن وتسرب بماسورة السقف بالمرآب",
            content = "نرجو من جميع القاطنين توخي الحيطة. تم إغلاق المحبس الرئيسي مؤقتاً لتفادي تجمع المياه بالمرآب السفلي، وفريق السباكة يصل خلال 30 دقيقة للإصلاح الفوري.",
            type = AnnouncementType.EMERGENCY,
            date = "اليوم - 10:15 صباحاً",
            startDate = "2026-10-26",
            endDate = "2026-10-26",
            author = "مسؤول العمارة (رشيد المنصوري)",
            isPinned = true
        ),
        Announcement(
            id = "ann-temp-1",
            title = "صيانة وتنظيف خزانات المياه العلوية والمضخات",
            content = "سيتم إجراء التنظيف الدوري والتعقيم لخزانات مياه الشرب يوم الخميس القادم من 09:00 إلى 13:00. يرجى ملء الاحتياجات المنزلية مسبقاً.",
            type = AnnouncementType.TEMPORARY,
            date = "2026-10-24",
            startDate = "2026-10-28",
            endDate = "2026-10-29",
            author = "مكتب العمارة",
            isPinned = false
        ),
        Announcement(
            id = "ann-perm-1",
            title = "📜 ميثاق حسن الجوار وساعات الهدوء المقررة",
            content = "يُرجى الالتزام بساعات الهدوء التام من الساعة 22:00 ليلاً حتى 08:00 صباحاً. يُمنع منعاً باتاً وضع أكياس القمامة أمام أبواب الشقق في الممرات المشتركة.",
            type = AnnouncementType.PERMANENT,
            date = "قواعد دائمة",
            author = "جمعية الملاك المشتركين",
            isPinned = true
        ),
        Announcement(
            id = "ann-perm-2",
            title = "قواعد استخدام المصعد الكهربائي وتفريغ الأثاث",
            content = "المصعد مخصص للأفراد فقط، يمنع نقل مواد البناء أو الأثاث الثقيل دون إشعار مسبق وتغطية الكابينة لحمايتها من الخدوش.",
            type = AnnouncementType.PERMANENT,
            date = "قواعد دائمة",
            author = "مكتب العمارة",
            isPinned = false
        )
    )

    val demoComplaints = listOf(
        Complaint(
            id = "cmp-1",
            title = "صوت احتكاك قوي في باب المصعد بالطابق الثاني",
            description = "عند فتح الباب الخارجي للمصعد يوجد احتكاك وتوقف جزئي قبل الإغلاق.",
            category = ComplaintCategory.ELEVATOR,
            status = ComplaintStatus.IN_PROGRESS,
            apartmentNumber = "05",
            residentName = "أمينة التازي",
            residentPhone = "0666556677",
            date = "اليوم 09:30",
            photoNote = "صورة مرفقة لحافة الباب",
            managerNote = "تم حجز موعد تقني شركة الصيانة اليوم الساعة 15:00",
            updatedAt = "اليوم 10:00"
        ),
        Complaint(
            id = "cmp-2",
            title = "انقطاع مصباح LED التلقائي بممر الطابق الأول",
            description = "الممر مظلم تماماً والمستشعر لا يستجيب للحركة عند الخروج من شقتي.",
            category = ComplaintCategory.ELECTRICITY,
            status = ComplaintStatus.NEW,
            apartmentNumber = "01",
            residentName = "محمد السبتي",
            residentPhone = "0633333333",
            date = "أمس 20:15",
            photoNote = null,
            managerNote = "سيتم التغيير بمصباح جديد من طرف الحارس صباحاً",
            updatedAt = null
        ),
        Complaint(
            id = "cmp-3",
            title = "تسرب قطرات ماء عند سقف المرآب تحت الشقة 03",
            description = "لوحظ تجمع مياه خفيف عند موقف السيارة، يرجى فحص القنوات المشتركة.",
            category = ComplaintCategory.PLUMBING,
            status = ComplaintStatus.COMPLETED,
            apartmentNumber = "03",
            residentName = "كريم العلمي",
            residentPhone = "0664334455",
            date = "2026-10-20",
            photoNote = "صورة التسرب",
            managerNote = "تم إصلاح الصمام المشترك واستبدال العازل النحاسي بنجاح.",
            updatedAt = "2026-10-21"
        )
    )

    val demoBills = listOf(
        BillItem(
            id = "bill-1",
            apartmentNumber = "01",
            residentName = "محمد السبتي",
            month = "أكتوبر 2026",
            amount = 250.0,
            dueDate = "2026-10-10",
            description = "واجبات السنديك الشهرية (النظافة، المصعد، الحراسة، الإنارة المشتركة)",
            isPaid = true,
            proofReceiptUploaded = true,
            proofReceiptDate = "2026-10-02",
            proofReceiptNote = "تحويل عبر التطبيق البنكي الشعبي رقم #889211",
            confirmedByManager = true
        ),
        BillItem(
            id = "bill-4",
            apartmentNumber = "04",
            residentName = "عمر الفاسي",
            month = "أكتوبر 2026",
            amount = 250.0,
            dueDate = "2026-10-10",
            description = "واجبات السنديك الشهرية لشهر أكتوبر",
            isPaid = false,
            proofReceiptUploaded = true,
            proofReceiptDate = "2026-10-25",
            proofReceiptNote = "تم رفع وصل الدفع البنكي بانتظار التأكيد",
            confirmedByManager = false
        ),
        BillItem(
            id = "bill-8",
            apartmentNumber = "08",
            residentName = "سناء برادة",
            month = "أكتوبر 2026",
            amount = 250.0,
            dueDate = "2026-10-10",
            description = "واجبات السنديك الشهرية لشهر أكتوبر",
            isPaid = false,
            proofReceiptUploaded = false,
            proofReceiptDate = null,
            proofReceiptNote = null,
            confirmedByManager = false
        )
    )

    val demoFacilities = listOf(
        Facility(
            id = "fac-1",
            name = "السطح المشترك ومساحة التجفيف",
            iconKey = "roof",
            maxDurationHours = 4,
            openHours = "من 08:00 صباحاً إلى 20:00 مساءً",
            rules = "يُمنع إشعال النيران، ويجب تنظيف المكان وإعادة ترتيب الكراسي بعد الانتهاء."
        ),
        Facility(
            id = "fac-2",
            name = "قاعة الاجتماعات والمناسبات العائلية الصغرى",
            iconKey = "meeting",
            maxDurationHours = 6,
            openHours = "من 10:00 صباحاً إلى 22:00 ليلاً",
            rules = "الحجز المسبق قبل 24 ساعة، يُمنع تشغيل مكبرات الصوت المزعجة بعد 21:00."
        ),
        Facility(
            id = "fac-3",
            name = "ركن الشواء والحديقة الداخلية",
            iconKey = "bbq",
            maxDurationHours = 3,
            openHours = "من 12:00 ظهراً إلى 21:00 مساءً",
            rules = "التأكد من إخماد الفحم بالكامل، والمحافظة على النظافة والهدوء."
        )
    )

    val demoBookings = listOf(
        FacilityBooking(
            id = "bk-1",
            facilityId = "fac-2",
            facilityName = "قاعة الاجتماعات والمناسبات العائلية الصغرى",
            apartmentNumber = "02",
            residentName = "فاطمة الزهراء بناني",
            residentPhone = "0663223344",
            date = "2026-10-28",
            timeSlot = "16:00 - 20:00",
            status = "مؤكد"
        ),
        FacilityBooking(
            id = "bk-2",
            facilityId = "fac-1",
            facilityName = "السطح المشترك ومساحة التجفيف",
            apartmentNumber = "01",
            residentName = "محمد السبتي",
            residentPhone = "0633333333",
            date = "2026-10-27",
            timeSlot = "10:00 - 14:00",
            status = "مؤكد"
        )
    )

    val demoPolls = listOf(
        Poll(
            id = "poll-1",
            question = "اختيار لون صباغة وتجديد مدخل العمارة الرئيسي",
            description = "في إطار خطة صيانة البهو والمداخل، يرجى من كل شقة التصويت مرة واحدة على اللون المفضل:",
            options = listOf(
                PollOption("opt-1", "الأبيض الناصع الكلاسيكي", 6),
                PollOption("opt-2", "الرمادي العصري الأنيق", 10),
                PollOption("opt-3", "البيج الدافئ مع تشطيب خشبي", 4)
            ),
            votedApartments = mapOf("01" to "opt-2", "02" to "opt-1", "05" to "opt-2"),
            totalVotes = 20,
            endDate = "2026-10-31",
            isClosed = false
        ),
        Poll(
            id = "poll-2",
            question = "تركيب كاميرات مراقبة IP إضافية لمرآب السيارات",
            description = "مقترح لتعزيز حماية السيارات والممتلكات المشتركة بتكلفة إجمالية 3500 درهم من الرصيد الاحتياطي.",
            options = listOf(
                PollOption("opt-2-1", "نعم، موافق وبشدة", 15),
                PollOption("opt-2-2", "غير موافق، توجد أولويات أخرى", 3)
            ),
            votedApartments = mapOf("01" to "opt-2-1"),
            totalVotes = 18,
            endDate = "2026-11-05",
            isClosed = false
        )
    )

    val demoEmergencyContacts = listOf(
        EmergencyContact("emg-1", "الوقاية المدنية (المطافئ)", "15", "للطوارئ والحرائق والإنقاذ", "fire"),
        EmergencyContact("emg-2", "الإسعاف الصحي (SAMU)", "150", "حالات الإسعاف والنقل الطبي المستعجل", "ambulance"),
        EmergencyContact("emg-3", "الشرطة والأمن الوطني", "19", "الأمن وحالات التعدي أو السرقة", "police"),
        EmergencyContact("emg-4", "طوارئ الماء والكهرباء (ليدك / راديما)", "0522545555", "تسربات المياه العامة والإنقطاعات", "utility"),
        EmergencyContact("emg-5", "شركة صيانة المصعد (أوتيس)", "0522408080", "أعطاب المصعد والطوارئ 24/7", "elevator"),
        EmergencyContact("emg-6", "مسؤول العمارة (السنديك)", "0611111111", "السيد رشيد المنصوري - شقة 10", "manager")
    )

    val demoNotifications = listOf(
        AppNotification(
            id = "notif-1",
            title = "🚨 إعلان طارئ: انقطاع الماء الساخن وتسرب بمرآب السيارات",
            message = "تم إغلاق المحبس الرئيسي مؤقتاً لتفادي تجمع المياه، وفريق السباكة في طريقه للإصلاح.",
            type = NotificationType.EMERGENCY,
            timestamp = "منذ 15 دقيقة",
            isRead = false,
            targetRole = null
        ),
        AppNotification(
            id = "notif-2",
            title = "🔧 تحديث على شكوى المصعد (شقة 05)",
            message = "تم حجز موعد تقني شركة أوتيس للصيانة وسيحضر اليوم عند 15:00.",
            type = NotificationType.COMPLAINT_UPDATE,
            timestamp = "منذ ساعة",
            isRead = false,
            targetApartment = "05",
            relatedEntityId = "cmp-1"
        ),
        AppNotification(
            id = "notif-3",
            title = "📢 شكوى جديدة من ساكن شقة 01",
            message = "محمد السبتي سجّل طلباً جديداً بخصوص انقطاع مصباح LED بالطابق الأول.",
            type = NotificationType.NEW_COMPLAINT,
            timestamp = "أمس 20:15",
            isRead = true,
            targetRole = UserRole.MANAGER,
            relatedEntityId = "cmp-2"
        ),
        AppNotification(
            id = "notif-4",
            title = "📄 إثبات دفع جديد لشهر أكتوبر (شقة 04)",
            message = "قام عمر الفاسي برفع وصل التحويل البنكي بانتظار تأكيد المسؤول.",
            type = NotificationType.BILL_UPDATE,
            timestamp = "منذ 3 ساعات",
            isRead = false,
            targetRole = UserRole.MANAGER,
            relatedEntityId = "bill-4"
        )
    )
}


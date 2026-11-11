package com.example.data.model

import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.IgnoreExtraProperties
import com.google.firebase.firestore.ServerTimestamp

@IgnoreExtraProperties
data class WalletAccount(
    @DocumentId val id: String = "",
    val userId: String = "",
    val fullName: String = "",
    val phoneNumber: String = "",
    val ccpAccount: String = "",
    val ripNumber: String = "",
    val cardMaskedNumber: String = "",
    val cardExpiry: String = "",
    val balanceDzd: Long = 0L,
    val cardFrozen: Boolean = false,
    val onlinePaymentsEnabled: Boolean = true,
    val contactlessEnabled: Boolean = true,
    val biometricEnabled: Boolean = true,
    val twoFactorEnabled: Boolean = true,
    val dailyTransferLimitDzd: Long = 200000L,
    val dailyWithdrawalLimitDzd: Long = 50000L,
    @ServerTimestamp val createdAt: Timestamp? = null,
    @ServerTimestamp val updatedAt: Timestamp? = null
)

@IgnoreExtraProperties
data class WalletTransaction(
    @DocumentId val id: String = "",
    val userId: String = "",
    val type: String = "transfer",
    val title: String = "",
    val recipientName: String = "",
    val recipientRip: String = "",
    val operatorOrBiller: String = "",
    val referenceCode: String = "",
    val amountDzd: Long = 0L,
    val feeDzd: Long = 0L,
    val status: String = "completed",
    val note: String = "",
    @ServerTimestamp val createdAt: Timestamp? = null,
    @ServerTimestamp val updatedAt: Timestamp? = null
)

@IgnoreExtraProperties
data class Beneficiary(
    @DocumentId val id: String = "",
    val userId: String = "",
    val fullName: String = "",
    val ripNumber: String = "",
    val nickname: String = "",
    val isFavorite: Boolean = false,
    @ServerTimestamp val createdAt: Timestamp? = null,
    @ServerTimestamp val updatedAt: Timestamp? = null
)

@IgnoreExtraProperties
data class SupportTicket(
    @DocumentId val id: String = "",
    val userId: String = "",
    val subject: String = "",
    val category: String = "transfer_issue",
    val priority: String = "normal",
    val status: String = "answered",
    val userMessage: String = "",
    val supportReply: String = "",
    @ServerTimestamp val createdAt: Timestamp? = null,
    @ServerTimestamp val updatedAt: Timestamp? = null
)

@IgnoreExtraProperties
data class PublicDirectoryPeer(
    @DocumentId val id: String = "",
    val userId: String = "",
    val fullName: String = "",
    val phoneNumber: String = "",
    val ripNumber: String = "",
    @ServerTimestamp val updatedAt: Timestamp? = null
)

@IgnoreExtraProperties
data class P2PTransfer(
    @DocumentId val id: String = "",
    val senderUid: String = "",
    val senderName: String = "",
    val senderRip: String = "",
    val recipientUid: String = "",
    val recipientName: String = "",
    val recipientRip: String = "",
    val amountDzd: Long = 0L,
    val referenceCode: String = "",
    val note: String = "",
    val status: String = "pending_claim",
    @ServerTimestamp val createdAt: Timestamp? = null,
    @ServerTimestamp val updatedAt: Timestamp? = null
)

data class MobileOperatorInfo(
    val id: String,
    val nameAr: String,
    val prefix: String,
    val colorHex: Long,
    val plans: List<MobilePlanOption>
)

data class MobilePlanOption(
    val titleAr: String,
    val subtitleAr: String,
    val amountDzd: Long
)

data class UtilityBillerInfo(
    val id: String,
    val nameAr: String,
    val categoryAr: String,
    val codePrefix: String,
    val sampleAmountDzd: Long
)

data class FaqEntry(
    val questionAr: String,
    val answerAr: String,
    val categoryAr: String
)

object AlgerianServicesCatalog {
    val mobileOperators = listOf(
        MobileOperatorInfo(
            id = "mobilis",
            nameAr = "موبيليس (Mobilis)",
            prefix = "06",
            colorHex = 0xFF00A651,
            plans = listOf(
                MobilePlanOption("بيكس PixX 500", "مكالمات + 5 جيغا إنترنت", 500L),
                MobilePlanOption("بيكس PixX 1000", "مكالمات غير محدودة + 15 جيغا", 1000L),
                MobilePlanOption("سما Sama 2000", "مكالمات شاملة + 40 جيغا شهرياً", 2000L),
                MobilePlanOption("تعبئة حرة فليكسي", "رصيد أساسي صالح لكل الشبكات", 200L)
            )
        ),
        MobileOperatorInfo(
            id = "djezzy",
            nameAr = "جازي (Djezzy)",
            prefix = "07",
            colorHex = 0xFFE30613,
            plans = listOf(
                MobilePlanOption("هايلة ماكس 500", "6 جيغا إنترنت + مكالمات", 500L),
                MobilePlanOption("هايلة بزاف 1000", "20 جيغا إنترنت + مكالمات", 1000L),
                MobilePlanOption("هايلة بزاف 1500", "35 جيغا إنترنت + رصيد مجاني", 1500L),
                MobilePlanOption("هايلة 2000", "60 جيغا إنترنت + مكالمات بلا حدود", 2000L)
            )
        ),
        MobileOperatorInfo(
            id = "ooredoo",
            nameAr = "أوريدو (Ooredoo)",
            prefix = "05",
            colorHex = 0xFFD9001B,
            plans = listOf(
                MobilePlanOption("يوز Yooz 500", "إنترنت مخصص + تطبيقات مجانية", 500L),
                MobilePlanOption("هيا! Haya 1000", "18 جيغا + مكالمات نحو الكل", 1000L),
                MobilePlanOption("هيا! Haya 1500", "32 جيغا + يوتيوب غير محدود", 1500L),
                MobilePlanOption("هيا! ماكس 2500", "70 جيغا + مكالمات غير محدودة", 2500L)
            )
        )
    )

    val utilityBillers = listOf(
        UtilityBillerInfo(
            id = "sonelgaz",
            nameAr = "سونلغاز (Sonelgaz)",
            categoryAr = "فواتير الكهرباء والغاز",
            codePrefix = "SNG-",
            sampleAmountDzd = 4850L
        ),
        UtilityBillerInfo(
            id = "algerie_telecom",
            nameAr = "اتصالات الجزائر (Idoom / FTTH)",
            categoryAr = "تعبئة الإنترنت الثابت والألياف البصرية",
            codePrefix = "AT-",
            sampleAmountDzd = 2600L
        ),
        UtilityBillerInfo(
            id = "seaal_ade",
            nameAr = "سيال / الجزائرية للمياه (SEAAL / ADE)",
            categoryAr = "فواتير المياه والتطهير",
            codePrefix = "ADE-",
            sampleAmountDzd = 1950L
        ),
        UtilityBillerInfo(
            id = "aadl_opgi",
            nameAr = "عدل / ديوان الترقية (AADL / OPGI)",
            categoryAr = "مستحقات الإيجار والأقساط الشهرية",
            codePrefix = "ADL-",
            sampleAmountDzd = 8500L
        ),
        UtilityBillerInfo(
            id = "cnas_casnos",
            nameAr = "الضمان الاجتماعي (CNAS / CASNOS)",
            categoryAr = "اشتراكات التغطية الاجتماعية",
            codePrefix = "CNS-",
            sampleAmountDzd = 12000L
        )
    )

    val faqList = listOf(
        FaqEntry(
            questionAr = "كيف أتحقق من رقم RIP الصحيح قبل التحويل؟",
            answerAr = "يتكون رقم RIP من 20 رقماً يبدأ بـ 00799999 متبوعاً برقم الحساب الجاري ومفتاح الأمان. يتحقق التطبيق تلقائياً من بنية الرقم قبل خصم أي مبلغ.",
            categoryAr = "التحويلات"
        ),
        FaqEntry(
            questionAr = "ماذا أفعل في حال ضياع البطاقة الذهبية؟",
            answerAr = "يمكنك تجميد البطاقة الذهبية فوراً بضغطة واحدة من تبويب 'البطاقة الذهبية'، مما يوقف جميع عمليات السحب والدفع الإلكتروني في نفس اللحظة.",
            categoryAr = "أمان البطاقة"
        ),
        FaqEntry(
            questionAr = "كيف تعمل خدمة السحب بدون بطاقة (Cardless ATM)؟",
            answerAr = "اختر 'سحب بدون بطاقة'، حدد المبلغ، وسيصدر لك التطبيق رمزاً سرياً مؤقتاً (OTP) صالحاً لمدة 24 ساعة يمكنك إدخاله في أي موزع آلي متوافق لسحب أموالك نقداً.",
            categoryAr = "السحب الآلي"
        ),
        FaqEntry(
            questionAr = "كيف تتم حماية بياناتي الشخصية والمصرفية؟",
            answerAr = "جميع البيانات مشفرة ومعزولة كلياً حسب حسابك الموثق، مع تفعيل التحقق الثنائي (2FA) وتحديد سقف التحويل والسحب اليومي لمنع أي عملية غير مصرح بها.",
            categoryAr = "الأمان والخصوصية"
        )
    )
}

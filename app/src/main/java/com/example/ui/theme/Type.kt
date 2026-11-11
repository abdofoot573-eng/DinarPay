package com.example.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.example.R

/**
 * Cairo Font Family (Arabic & General UI)
 * Includes all 6 static weights (300 Light, 400 Regular, 500 Medium, 600 SemiBold, 700 Bold, 800 ExtraBold)
 */
val CairoFontFamily = FontFamily(
    Font(R.font.cairo_300, FontWeight.Light),
    Font(R.font.cairo_400, FontWeight.Normal),
    Font(R.font.cairo_500, FontWeight.Medium),
    Font(R.font.cairo_600, FontWeight.SemiBold),
    Font(R.font.cairo_700, FontWeight.Bold),
    Font(R.font.cairo_800, FontWeight.ExtraBold)
)

/**
 * Poppins Font Family (English Brand "PayDz" & Financial Numbers)
 * Includes all 6 static weights (300 Light, 400 Regular, 500 Medium, 600 SemiBold, 700 Bold, 800 ExtraBold)
 */
val PoppinsFontFamily = FontFamily(
    Font(R.font.poppins_300, FontWeight.Light),
    Font(R.font.poppins_400, FontWeight.Normal),
    Font(R.font.poppins_500, FontWeight.Medium),
    Font(R.font.poppins_600, FontWeight.SemiBold),
    Font(R.font.poppins_700, FontWeight.Bold),
    Font(R.font.poppins_800, FontWeight.ExtraBold)
)

val RobotoMonoFontFamily = FontFamily(
    Font(R.font.roboto_mono, FontWeight.Normal),
    Font(R.font.roboto_mono, FontWeight.Bold)
)

val Typography = Typography(
    // أ) الخط الخشن جداً والكبير (ExtraBold / Bold: font-weight: 800, size: 26sp)
    displayLarge = TextStyle(
        fontFamily = CairoFontFamily,
        fontWeight = FontWeight.ExtraBold,
        fontSize = 26.sp,
        lineHeight = 34.sp
    ),
    // العناوين الترحيبية وعناوين الحالة ("مرحباً بك"، "تمت العملية بنجاح!"): حجم 21sp، وزن 700
    headlineLarge = TextStyle(
        fontFamily = CairoFontFamily,
        fontWeight = FontWeight.Bold,
        fontSize = 21.sp,
        lineHeight = 28.sp
    ),
    // عناوين أعلى الشاشة AppBar ("جميع الخدمات"، "شحن الهاتف"، "دفع الفواتير"، "تحويل الأموال"، "سجل المعاملات"): حجم 17sp، وزن 700
    headlineMedium = TextStyle(
        fontFamily = CairoFontFamily,
        fontWeight = FontWeight.Bold,
        fontSize = 17.sp,
        lineHeight = 24.sp
    ),
    // نصوص الأزرار الرئيسية ("تسجيل الدخول"، "متابعة"، "التالي"، "حسناً"، "الدخول بالبصمة"): حجم 15sp، وزن 700
    titleLarge = TextStyle(
        fontFamily = CairoFontFamily,
        fontWeight = FontWeight.Bold,
        fontSize = 15.sp,
        lineHeight = 22.sp
    ),
    // عناوين الأقسام الفرعية ("آخر العمليات"، "اختر المتعامل"، "اختر نوع الفاتورة"، "أدخل معلومات التحويل") والأرقام المالية: حجم 14sp، وزن 700
    titleMedium = TextStyle(
        fontFamily = CairoFontFamily,
        fontWeight = FontWeight.Bold,
        fontSize = 14.sp,
        lineHeight = 20.sp
    ),
    // نصوص بطاقات الخدمات المربعة ("شحن الهاتف"، "تحويل أموال"، "دفع الفواتير"): حجم 13sp، وزن 700
    titleSmall = TextStyle(
        fontFamily = CairoFontFamily,
        fontWeight = FontWeight.Bold,
        fontSize = 13.sp,
        lineHeight = 18.sp
    ),
    // ج) الخط الرقيق والعادي للعبارات التوضيحية الفرعية ("كل خدماتك المالية في مكان واحد"، "سجل دخولك للمتابعة"، "صباح الخير"، "رصيد حسابك"): حجم 13sp، وزن 400
    bodyLarge = TextStyle(
        fontFamily = CairoFontFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 13.sp,
        lineHeight = 20.sp
    ),
    // نصوص حقول الإدخال والبحث Placeholders والتسميات الجانبية والروابط: حجم 12sp، وزن 400
    bodyMedium = TextStyle(
        fontFamily = CairoFontFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 12.sp,
        lineHeight = 18.sp
    ),
    // د) الخط الصغير الدقيق للتواريخ والأوقات وتفاصيل المعاملات ("اليوم - 14:30"، "07/11"): حجم 11sp، وزن 400
    bodySmall = TextStyle(
        fontFamily = CairoFontFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 11.sp,
        lineHeight = 15.sp
    ),
    labelLarge = TextStyle(
        fontFamily = CairoFontFamily,
        fontWeight = FontWeight.Bold,
        fontSize = 14.sp,
        lineHeight = 20.sp
    ),
    // شريط التنقل السفلي ("الرئيسية"، "المعاملات"، "الخدمات"، "المزيد"): حجم 10sp، وزن 400/600
    labelMedium = TextStyle(
        fontFamily = CairoFontFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 10.sp,
        lineHeight = 14.sp
    ),
    labelSmall = TextStyle(
        fontFamily = CairoFontFamily,
        fontWeight = FontWeight.Light,
        fontSize = 10.sp,
        lineHeight = 14.sp
    )
)

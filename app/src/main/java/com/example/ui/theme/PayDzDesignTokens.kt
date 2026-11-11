package com.example.ui.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/**
 * PayDz Design System & Visual Tokens
 * Extracted directly from the reference mockup image (1000648354.png).
 * Strictly enforces Light Mode, Royal/Cyan Blue gradients, soft porcelain backgrounds,
 * rounded pill inputs/buttons, and balanced Arabic RTL typography.
 */
object PayDzDesignTokens {
    // 1. Primary & Secondary Brand Colors
    val PrimaryBlue = Color(0xFF0057D9)
    val PrimaryBlueDark = Color(0xFF003EA8)
    val PrimaryBlueBright = Color(0xFF0E87F2)
    val CyanAccent = Color(0xFF22B8F0)

    val AccentGold = Color(0xFFFFB800)
    val AccentGoldDeep = Color(0xFFE09F00)
    val AccentGoldSoft = Color(0xFFFFF4D6)

    // 2. Backgrounds, Cards & Borders (Light Mode Only — Dark Mode Prohibited)
    val ScreenBackground = Color(0xFFF2F6FC)
    val CardWhite = Color(0xFFFFFFFF)
    val CardBorder = Color(0xFFE5EEF9)
    val PillInputBackground = Color(0xFFFFFFFF)
    val PillInputBorder = Color(0xFFDCE5F5)
    val SearchBarBackground = Color(0xFFE4ECF8)

    // 3. Semantic & Operator Colors
    val SuccessGreen = Color(0xFF16A34A)
    val SuccessGreenBright = Color(0xFF22C55E)
    val SuccessGreenSoft = Color(0xFFE2FBE8)

    val DangerRed = Color(0xFFEF4444)
    val DangerRedSoft = Color(0xFFFEE2E2)

    val MobilisBrandGreen = Color(0xFF16A34A)
    val DjezzyBrandRed = Color(0xFFDC2626)
    val OoredooBrandRed = Color(0xFFE11D48)

    // 4. Typography Colors
    val TextNavyPrimary = Color(0xFF0F172A)
    val TextCardTitle = Color(0xFF1E293B)
    val TextSlateSecondary = Color(0xFF64748B)
    val TextPlaceholder = Color(0xFF64748B)

    // 5. Standard Gradients
    val SplashBackgroundGradient = Brush.verticalGradient(
        colors = listOf(
            Color(0xFF003EA8),
            Color(0xFF0057D9),
            Color(0xFF1577E6)
        )
    )

    val HomeHeaderGradient = Brush.verticalGradient(
        colors = listOf(
            Color(0xFF003FA8),
            Color(0xFF0058DA)
        )
    )

    val BalanceCardGradient = Brush.horizontalGradient(
        colors = listOf(
            Color(0xFF0052D4),
            Color(0xFF0E87F2),
            Color(0xFF22B8F0)
        )
    )

    val VirtualCardGradient = Brush.linearGradient(
        colors = listOf(
            Color(0xFF002F87),
            Color(0xFF0057D9),
            Color(0xFF38BDF8)
        )
    )

    // 6. Corner Radii & Shapes
    val PillShape = RoundedCornerShape(50)
    val CardShapeLarge = RoundedCornerShape(26.dp)
    val CardShapeMedium = RoundedCornerShape(22.dp)
    val CardShapeSmall = RoundedCornerShape(18.dp)
    val HeaderBottomCurvedShape = RoundedCornerShape(bottomStart = 38.dp, bottomEnd = 38.dp)
    val BottomNavShape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)

    // 7. Component Dimensions & Spacing
    val ScreenHorizontalPadding = 20.dp
    val GridSpacing = 12.dp
    val SectionSpacing = 16.dp
    val PrimaryButtonHeight = 56.dp
    val PillInputHeight = 56.dp
    val SearchBarHeight = 50.dp
    val QuickActionCardHeight = 104.dp
    val ServiceGridCardHeight = 124.dp
    val BillerGridCardHeight = 116.dp
    val OperatorCardHeight = 68.dp

    // 8. Font Sizes (Exact Cairo & Poppins Specification)
    val BalanceDisplaySize = 26.sp
    val WelcomeTitleSize = 21.sp
    val ScreenTitleSize = 17.sp
    val SectionTitleSize = 14.sp
    val ServiceCardTitleSize = 13.sp
    val ButtonTextSize = 15.sp
    val BodyTextSize = 13.sp
    val PlaceholderTextSize = 12.sp
    val CaptionTextSize = 11.sp
    val NavBarTextSize = 10.sp
}

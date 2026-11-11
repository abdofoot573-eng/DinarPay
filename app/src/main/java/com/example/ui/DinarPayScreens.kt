package com.example.ui

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.CompareArrows
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.Contactless
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.CreditCard
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.HeadsetMic
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.MoreHoriz
import androidx.compose.material.icons.filled.NotificationsNone
import androidx.compose.material.icons.filled.PhoneAndroid
import androidx.compose.material.icons.filled.QrCodeScanner
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material.icons.filled.Wifi
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.text.AnnotatedString
import kotlinx.coroutines.launch
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.model.Beneficiary
import com.example.data.model.SupportTicket
import com.example.data.model.WalletAccount
import com.example.data.model.WalletTransaction
import com.example.ui.theme.CairoFontFamily
import com.example.ui.theme.PayDzBgSoft
import com.example.ui.theme.PayDzBlueBright
import com.example.ui.theme.PayDzBlueCyan
import com.example.ui.theme.PayDzBlueDark
import com.example.ui.theme.PayDzBluePrimary
import com.example.ui.theme.PayDzCardTitleText
import com.example.ui.theme.PayDzGold
import com.example.ui.theme.PayDzGoldSoft
import com.example.ui.theme.PayDzGreen
import com.example.ui.theme.PayDzGreenSoft
import com.example.ui.theme.PayDzInputBorder
import com.example.ui.theme.PayDzNavyText
import com.example.ui.theme.PayDzRed
import com.example.ui.theme.PayDzRedSoft
import com.example.ui.theme.PayDzSlateText
import com.example.ui.theme.PoppinsFontFamily
import com.example.ui.theme.RobotoMonoFontFamily
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Locale

fun formatDzd(amount: Long, isPrivacy: Boolean): String {
    if (isPrivacy) return "•••••• دج"
    val formatted = NumberFormat.getNumberInstance(Locale.US).format(amount)
    return "$formatted.00 دج"
}

/**
 * SCREEN 3 OF PAYDZ MOCKUP: الصفحة الرئيسية (Home Dashboard)
 */
@Composable
fun HomeDashboardScreen(
    account: WalletAccount?,
    transactions: List<WalletTransaction>,
    beneficiaries: List<Beneficiary>,
    isPrivacyMode: Boolean,
    isAr: Boolean,
    onTogglePrivacy: () -> Unit,
    onOpenModal: (ActiveModal) -> Unit,
    onSelectTransaction: (WalletTransaction) -> Unit,
    onDeleteBeneficiary: (String) -> Unit,
    onNavigateToServices: () -> Unit = {},
    onNavigateToHistory: () -> Unit = {},
    unreadNotificationsCount: Int = 0,
    onToggleCardFrozen: () -> Unit = {},
    onToggleContactless: () -> Unit = {},
    isBiometricEnrolled: Boolean = false,
    onOpenBiometricEnroll: () -> Unit = {}
) {
    val clipboard = LocalClipboardManager.current
    val coroutineScope = rememberCoroutineScope()
    val balanceDzd = account?.balanceDzd ?: 125750L
    val userName = account?.fullName?.ifBlank { "أحمد محمد" } ?: "أحمد محمد"
    val fullRipNumber = account?.ripNumber?.ifBlank { "00799999004125898421" } ?: "00799999004125898421"
    val lastFourAccount = fullRipNumber.takeLast(4)
    val ccpFormatted = account?.ccpAccount?.ifBlank { "00412589 / 84" } ?: "00412589 / 84"
    val cardExpiry = account?.cardExpiry?.ifBlank { "12/28" } ?: "12/28"
    val isCardFrozen = account?.cardFrozen ?: false
    val isContactlessEnabled = account?.contactlessEnabled ?: true

    var showFullAccountRip by remember { mutableStateOf(false) }
    var showFullCardNumbers by remember { mutableStateOf(false) }
    var copiedFeedback by remember { mutableStateOf<String?>(null) }
    val pagerState = rememberPagerState(initialPage = 0, pageCount = { 3 })

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(PayDzBgSoft),
        contentPadding = PaddingValues(bottom = 28.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // 1. Curved Royal Blue Top Header + Glossy Balance Card
        item {
            Box(
                modifier = Modifier.fillMaxWidth()
            ) {
                // Curved deep blue background
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(225.dp)
                        .clip(
                            RoundedCornerShape(
                                bottomStart = 38.dp,
                                bottomEnd = 38.dp
                            )
                        )
                        .background(
                            Brush.verticalGradient(
                                listOf(PayDzBlueDark, PayDzBluePrimary)
                            )
                        )
                )

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 14.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    // Greeting Row: Avatar + صباح الخير / أحمد محمد on right (start in RTL), Golden Bell on left (end in RTL)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(12.dp),
                            modifier = Modifier
                                .clip(RoundedCornerShape(50))
                                .clickable { onOpenModal(ActiveModal.ACCOUNT_PROFILE) }
                                .padding(end = 8.dp)
                                .testTag("home_user_profile_button")
                        ) {
                            Image(
                                painter = painterResource(id = R.drawable.canva_user_avatar),
                                contentDescription = "User Avatar",
                                contentScale = ContentScale.Crop,
                                modifier = Modifier
                                    .size(48.dp)
                                    .clip(CircleShape)
                                    .border(2.dp, Color.White, CircleShape)
                            )
                            Column {
                                Text(
                                    text = if (isAr) "صباح الخير" else "Bonjour",
                                    fontFamily = CairoFontFamily,
                                    color = Color.White.copy(alpha = 0.9f),
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Normal
                                )
                                Text(
                                    text = userName,
                                    fontFamily = CairoFontFamily,
                                    color = Color.White,
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }

                        // Golden Notification Bell with Unread Badge
                        Box {
                            IconButton(
                                onClick = { onOpenModal(ActiveModal.NOTIFICATIONS_CENTER) },
                                modifier = Modifier
                                    .size(44.dp)
                                    .clip(CircleShape)
                                    .background(Color.White.copy(alpha = 0.12f))
                                    .testTag("home_notification_bell")
                            ) {
                                Image(
                                    painter = painterResource(id = R.drawable.canva_bell_notif),
                                    contentDescription = "Notifications",
                                    contentScale = ContentScale.Fit,
                                    modifier = Modifier.size(28.dp)
                                )
                            }
                            if (unreadNotificationsCount > 0) {
                                Box(
                                    modifier = Modifier
                                        .align(Alignment.TopEnd)
                                        .size(18.dp)
                                        .clip(CircleShape)
                                        .background(PayDzRed)
                                        .border(1.5.dp, Color.White, CircleShape),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = unreadNotificationsCount.coerceAtMost(9).toString(),
                                        color = Color.White,
                                        fontFamily = PoppinsFontFamily,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 10.sp
                                    )
                                }
                            }
                        }
                    }

                    // Glossy Blue Balance Card (رصيد حسابك | 125,750.00 دج | **** 8421 / Full RIP)
                    Card(
                        shape = RoundedCornerShape(26.dp),
                        elevation = CardDefaults.cardElevation(defaultElevation = 10.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(
                                    Brush.horizontalGradient(
                                        listOf(
                                            Color(0xFF0052D4),
                                            PayDzBlueBright,
                                            PayDzBlueCyan
                                        )
                                    )
                                )
                        ) {
                            Image(
                                painter = painterResource(id = R.drawable.canva_balance_card_bg),
                                contentDescription = null,
                                contentScale = ContentScale.Crop,
                                modifier = Modifier.matchParentSize()
                            )

                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 18.dp, vertical = 16.dp)
                            ) {
                                // Eye Privacy Toggle in Top-Right (Alignment.TopStart in RTL, at exact SVG scale)
                                IconButton(
                                    onClick = onTogglePrivacy,
                                    modifier = Modifier
                                        .align(Alignment.TopStart)
                                        .size(38.dp)
                                        .testTag("privacy_mode_toggle")
                                ) {
                                    if (isPrivacyMode) {
                                        Icon(
                                            imageVector = Icons.Default.VisibilityOff,
                                            contentDescription = "Toggle Balance Visibility",
                                            tint = Color.White,
                                            modifier = Modifier.size(22.dp)
                                        )
                                    } else {
                                        Image(
                                            painter = painterResource(id = R.drawable.canva_eye_toggle),
                                            contentDescription = "Toggle Balance Visibility",
                                            contentScale = ContentScale.Fit,
                                            modifier = Modifier.size(width = 34.dp, height = 28.dp)
                                        )
                                    }
                                }

                                Column(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    verticalArrangement = Arrangement.spacedBy(4.dp)
                                ) {
                                    Text(
                                        text = if (isAr) "رصيد حسابك" else "Solde de votre compte",
                                        fontFamily = CairoFontFamily,
                                        color = Color.White,
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Normal
                                    )

                                    Row(
                                        verticalAlignment = Alignment.Bottom,
                                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                                    ) {
                                        val formattedNum = if (isPrivacyMode) {
                                            "••••••"
                                        } else {
                                            "${NumberFormat.getNumberInstance(Locale.US).format(balanceDzd)}.00"
                                        }
                                        Text(
                                            text = formattedNum,
                                            fontFamily = PoppinsFontFamily,
                                            color = Color.White,
                                            fontSize = 26.sp,
                                            fontWeight = FontWeight.ExtraBold,
                                            letterSpacing = 0.5.sp,
                                            modifier = Modifier.testTag("wallet_balance_text")
                                        )
                                        Text(
                                            text = if (isAr) "دج" else "DZD",
                                            fontFamily = CairoFontFamily,
                                            color = Color.White,
                                            fontSize = 18.sp,
                                            fontWeight = FontWeight.ExtraBold,
                                            modifier = Modifier.padding(bottom = 2.dp)
                                        )
                                    }

                                    // High-contrast interactive Account / RIP Pill (click to reveal full 20-digit RIP or copy)
                                    Surface(
                                        shape = RoundedCornerShape(50),
                                        color = Color.White.copy(alpha = 0.18f),
                                        modifier = Modifier
                                            .clickable { showFullAccountRip = !showFullAccountRip }
                                            .testTag("balance_account_rip_toggle")
                                    ) {
                                        Row(
                                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp),
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                                        ) {
                                            Text(
                                                text = if (isPrivacyMode) {
                                                    "**** ••••"
                                                } else if (showFullAccountRip) {
                                                    "RIP: $fullRipNumber"
                                                } else {
                                                    "**** $lastFourAccount"
                                                },
                                                color = Color.White,
                                                fontFamily = PoppinsFontFamily,
                                                fontSize = 13.sp,
                                                fontWeight = FontWeight.Bold
                                            )
                                            Icon(
                                                imageVector = if (showFullAccountRip) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                                                contentDescription = "إظهار رقم الحساب الكامل",
                                                tint = Color.White,
                                                modifier = Modifier.size(15.dp)
                                            )
                                            Icon(
                                                imageVector = Icons.Default.ContentCopy,
                                                contentDescription = "نسخ RIP",
                                                tint = Color.White,
                                                modifier = Modifier
                                                    .size(14.dp)
                                                    .clickable {
                                                        clipboard.setText(AnnotatedString(fullRipNumber))
                                                        copiedFeedback = "تم نسخ رقم حسابك RIP ($fullRipNumber)"
                                                    }
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        // 2. Interactive 3-Card Horizontal Carousel (HorizontalPager + 3 Active Pagination Dots + Quick Card Controls)
        item {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                HorizontalPager(
                    state = pagerState,
                    contentPadding = PaddingValues(start = 20.dp, end = 52.dp),
                    pageSpacing = 12.dp,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(162.dp)
                        .testTag("home_cards_pager")
                ) { page ->
                    when (page) {
                        0 -> {
                            // CARD 1: Main Blue PayDz Virtual Card (Exact SVG Proportions for Logo Mark & NFC Waves)
                            Card(
                                shape = RoundedCornerShape(24.dp),
                                elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
                                modifier = Modifier
                                    .fillMaxSize()
                                    .clickable { showFullCardNumbers = !showFullCardNumbers }
                            ) {
                                Box(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .background(
                                            Brush.linearGradient(
                                                colors = listOf(
                                                    Color(0xFF38BDF8),
                                                    Color(0xFF0057D9),
                                                    Color(0xFF002F87)
                                                )
                                            )
                                        )
                                ) {
                                    Image(
                                        painter = painterResource(id = R.drawable.canva_virtual_card_bg),
                                        contentDescription = null,
                                        contentScale = ContentScale.Crop,
                                        modifier = Modifier.matchParentSize()
                                    )

                                    // Use LTR layout inside the card so Left = PayDz Logo & Card Number, Right = NFC Waves & Expiry, matching the exact SVG coordinates!
                                    androidx.compose.runtime.CompositionLocalProvider(
                                        androidx.compose.ui.platform.LocalLayoutDirection provides androidx.compose.ui.unit.LayoutDirection.Ltr
                                    ) {
                                        Box(
                                            modifier = Modifier
                                                .fillMaxSize()
                                                .padding(horizontal = 20.dp, vertical = 16.dp)
                                        ) {
                                            // Top-Left: PayDz Card Logo Mark at exact SVG scale (44dp x 32dp) + PayDz title
                                            Column(
                                                modifier = Modifier.align(Alignment.TopStart),
                                                horizontalAlignment = Alignment.Start
                                            ) {
                                                Image(
                                                    painter = painterResource(id = R.drawable.canva_card_logo_mark),
                                                    contentDescription = "PayDz Card Mark",
                                                    contentScale = ContentScale.Fit,
                                                    modifier = Modifier.size(width = 44.dp, height = 32.dp)
                                                )
                                                Spacer(modifier = Modifier.height(2.dp))
                                                Row(
                                                    verticalAlignment = Alignment.CenterVertically,
                                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                                ) {
                                                    Text(
                                                        text = "PayDz",
                                                        fontFamily = PoppinsFontFamily,
                                                        color = Color.White,
                                                        fontWeight = FontWeight.ExtraBold,
                                                        fontSize = 19.sp
                                                    )
                                                    if (isCardFrozen) {
                                                        Surface(
                                                            shape = RoundedCornerShape(50),
                                                            color = PayDzRed
                                                        ) {
                                                            Text(
                                                                text = if (isAr) "مجمدة" else "Gelée",
                                                                fontFamily = CairoFontFamily,
                                                                color = Color.White,
                                                                fontWeight = FontWeight.Bold,
                                                                fontSize = 10.sp,
                                                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                                                            )
                                                        }
                                                    }
                                                }
                                            }

                                            // Middle-Right: NFC Contactless Waves at exact SVG size & vertical position (36dp x 48dp)
                                            Image(
                                                painter = painterResource(id = R.drawable.canva_card_nfc_waves),
                                                contentDescription = "NFC Contactless",
                                                contentScale = ContentScale.Fit,
                                                alpha = if (isContactlessEnabled) 1f else 0.35f,
                                                modifier = Modifier
                                                    .align(Alignment.CenterEnd)
                                                    .offset(y = (-6).dp)
                                                    .size(width = 36.dp, height = 48.dp)
                                                    .clickable { onToggleContactless() }
                                            )

                                            // Bottom Row: Left = Full/Masked Card Number + Eye button | Right = Expiry Date
                                            Row(
                                                modifier = Modifier
                                                    .align(Alignment.BottomCenter)
                                                    .fillMaxWidth(),
                                                horizontalArrangement = Arrangement.SpaceBetween,
                                                verticalAlignment = Alignment.CenterVertically
                                            ) {
                                                Row(
                                                    verticalAlignment = Alignment.CenterVertically,
                                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                                ) {
                                                    Text(
                                                        text = if (isPrivacyMode) {
                                                            "•••• •••• •••• $lastFourAccount"
                                                        } else if (showFullCardNumbers) {
                                                            "8092 4510 7391 $lastFourAccount  •  CVV 842"
                                                        } else {
                                                            "8092 •••• •••• $lastFourAccount"
                                                        },
                                                        color = Color.White,
                                                        fontFamily = PoppinsFontFamily,
                                                        fontSize = if (showFullCardNumbers) 12.5.sp else 14.5.sp,
                                                        fontWeight = FontWeight.ExtraBold
                                                    )
                                                    Icon(
                                                        imageVector = if (showFullCardNumbers) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                                                        contentDescription = "Show Card Number",
                                                        tint = Color.White,
                                                        modifier = Modifier
                                                            .size(16.dp)
                                                            .clickable { showFullCardNumbers = !showFullCardNumbers }
                                                    )
                                                }

                                                Text(
                                                    text = cardExpiry,
                                                    color = Color.White,
                                                    fontFamily = PoppinsFontFamily,
                                                    fontSize = 13.5.sp,
                                                    fontWeight = FontWeight.Bold
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }

                        1 -> {
                            // CARD 2: Full Gold / Navy Edahabia Card (البطاقة الذهبية الكاملة بدلاً من المستطيل المقطوع)
                            Card(
                                shape = RoundedCornerShape(24.dp),
                                elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
                                modifier = Modifier
                                    .fillMaxSize()
                                    .clickable { showFullCardNumbers = !showFullCardNumbers }
                            ) {
                                Box(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .background(
                                            Brush.linearGradient(
                                                colors = listOf(
                                                    Color(0xFF1E3A8A),
                                                    Color(0xFF0F296B),
                                                    Color(0xFFD97706)
                                                )
                                            )
                                        )
                                ) {
                                    Image(
                                        painter = painterResource(id = R.drawable.canva_gold_card_accent),
                                        contentDescription = "Gold Card Background",
                                        contentScale = ContentScale.Crop,
                                        alpha = 0.88f,
                                        modifier = Modifier.matchParentSize()
                                    )
                                    Box(
                                        modifier = Modifier
                                            .matchParentSize()
                                            .background(
                                                Brush.horizontalGradient(
                                                    listOf(
                                                        Color(0xFF0A2463).copy(alpha = 0.78f),
                                                        Color(0xFF1E3A8A).copy(alpha = 0.45f)
                                                    )
                                                )
                                            )
                                    )

                                    androidx.compose.runtime.CompositionLocalProvider(
                                        androidx.compose.ui.platform.LocalLayoutDirection provides androidx.compose.ui.unit.LayoutDirection.Ltr
                                    ) {
                                        Box(
                                            modifier = Modifier
                                                .fillMaxSize()
                                                .padding(horizontal = 20.dp, vertical = 16.dp)
                                        ) {
                                            Column(modifier = Modifier.align(Alignment.TopStart)) {
                                                Text(
                                                    text = "EDAHABIA • الذهبية",
                                                    fontFamily = CairoFontFamily,
                                                    color = Color(0xFFFDE047),
                                                    fontWeight = FontWeight.ExtraBold,
                                                    fontSize = 16.sp
                                                )
                                                Text(
                                                    text = "CCP: $ccpFormatted",
                                                    fontFamily = PoppinsFontFamily,
                                                    color = Color.White,
                                                    fontWeight = FontWeight.SemiBold,
                                                    fontSize = 12.sp
                                                )
                                            }

                                            Image(
                                                painter = painterResource(id = R.drawable.canva_card_nfc_waves),
                                                contentDescription = "NFC Contactless",
                                                contentScale = ContentScale.Fit,
                                                modifier = Modifier
                                                    .align(Alignment.CenterEnd)
                                                    .size(width = 34.dp, height = 46.dp)
                                            )

                                            Row(
                                                modifier = Modifier
                                                    .align(Alignment.BottomCenter)
                                                    .fillMaxWidth(),
                                                horizontalArrangement = Arrangement.SpaceBetween,
                                                verticalAlignment = Alignment.CenterVertically
                                            ) {
                                                Text(
                                                    text = if (showFullCardNumbers && !isPrivacyMode) {
                                                        "6280 7999 0041 $lastFourAccount  •  CVV 519"
                                                    } else {
                                                        "6280 •••• •••• $lastFourAccount"
                                                    },
                                                    color = Color.White,
                                                    fontFamily = PoppinsFontFamily,
                                                    fontSize = if (showFullCardNumbers) 12.5.sp else 14.5.sp,
                                                    fontWeight = FontWeight.ExtraBold
                                                )
                                                Text(
                                                    text = "11/29",
                                                    color = Color(0xFFFDE047),
                                                    fontFamily = PoppinsFontFamily,
                                                    fontSize = 13.5.sp,
                                                    fontWeight = FontWeight.Bold
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }

                        else -> {
                            // CARD 3: PayDz Platinum QR & Online Card (البطاقة الثالثة للدفع عبر الإنترنت والـ QR)
                            Card(
                                shape = RoundedCornerShape(24.dp),
                                elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
                                modifier = Modifier
                                    .fillMaxSize()
                                    .clickable { onOpenModal(ActiveModal.QR_PAYMENT) }
                            ) {
                                Box(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .background(
                                            Brush.linearGradient(
                                                colors = listOf(
                                                    Color(0xFF0F172A),
                                                    Color(0xFF0284C7),
                                                    Color(0xFF10B981)
                                                )
                                            )
                                        )
                                        .padding(horizontal = 20.dp, vertical = 16.dp)
                                ) {
                                    Column(modifier = Modifier.align(Alignment.TopStart)) {
                                        Text(
                                            text = if (isAr) "بطاقة الدفع الذكي QR & NFC" else "Carte PayDz QR & NFC",
                                            fontFamily = CairoFontFamily,
                                            color = Color.White,
                                            fontWeight = FontWeight.ExtraBold,
                                            fontSize = 15.sp
                                        )
                                        Text(
                                            text = if (isAr) "السقف اليومي: ${NumberFormat.getNumberInstance(Locale.US).format(account?.dailyTransferLimitDzd ?: 200000L)} دج" else "Plafond : 200,000 DZD",
                                            fontFamily = CairoFontFamily,
                                            color = Color.White.copy(alpha = 0.9f),
                                            fontWeight = FontWeight.SemiBold,
                                            fontSize = 12.sp
                                        )
                                    }

                                    Icon(
                                        imageVector = Icons.Default.QrCodeScanner,
                                        contentDescription = "QR Pay",
                                        tint = Color.White,
                                        modifier = Modifier
                                            .align(Alignment.CenterEnd)
                                            .size(42.dp)
                                    )

                                    Row(
                                        modifier = Modifier
                                            .align(Alignment.BottomCenter)
                                            .fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(
                                            text = if (showFullCardNumbers && !isPrivacyMode) {
                                                "4567 8901 2345 $lastFourAccount  •  CVV 307"
                                            } else {
                                                "4567 •••• •••• $lastFourAccount"
                                            },
                                            color = Color.White,
                                            fontFamily = PoppinsFontFamily,
                                            fontSize = 13.5.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                        Text(
                                            text = if (isAr) "اضغط للدفع بـ QR" else "Payer par QR",
                                            fontFamily = CairoFontFamily,
                                            color = Color(0xFFA7F3D0),
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 11.sp
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                // 3 Interactive Carousel Pager Dots (Click or Swipe between the 3 Cards!)
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    repeat(3) { pageIndex ->
                        val isSelected = pagerState.currentPage == pageIndex
                        Box(
                            modifier = Modifier
                                .width(if (isSelected) 20.dp else 8.dp)
                                .height(8.dp)
                                .clip(RoundedCornerShape(50))
                                .background(if (isSelected) PayDzBluePrimary else Color(0xFFCBD5E1))
                                .clickable {
                                    coroutineScope.launch {
                                        pagerState.animateScrollToPage(pageIndex)
                                    }
                                }
                        )
                    }
                }

                // Interactive Quick Card Action Buttons (إظهار الأرقام / CVV | NFC باللمس | تجميد البطاقة | شحن البطاقة)
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // 1. Show/Hide Full Card Numbers & CVV
                    Surface(
                        shape = RoundedCornerShape(14.dp),
                        color = if (showFullCardNumbers) PayDzBluePrimary else Color.White,
                        shadowElevation = 2.dp,
                        modifier = Modifier
                            .weight(1f)
                            .height(38.dp)
                            .border(
                                1.dp,
                                if (showFullCardNumbers) PayDzBluePrimary else PayDzInputBorder,
                                RoundedCornerShape(14.dp)
                            )
                            .clickable {
                                showFullCardNumbers = !showFullCardNumbers
                                showFullAccountRip = showFullCardNumbers
                            }
                            .testTag("card_action_reveal_numbers")
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 8.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Icon(
                                imageVector = if (showFullCardNumbers) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                                contentDescription = null,
                                tint = if (showFullCardNumbers) Color.White else PayDzBluePrimary,
                                modifier = Modifier.size(15.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = if (isAr) (if (showFullCardNumbers) "إخفاء الأرقام" else "إظهار الأرقام") else "Numéros",
                                fontFamily = CairoFontFamily,
                                color = if (showFullCardNumbers) Color.White else PayDzNavyText,
                                fontWeight = FontWeight.Bold,
                                fontSize = 11.sp
                            )
                        }
                    }

                    // 2. Toggle Contactless NFC Payment
                    Surface(
                        shape = RoundedCornerShape(14.dp),
                        color = if (isContactlessEnabled) PayDzGreenSoft else Color.White,
                        shadowElevation = 2.dp,
                        modifier = Modifier
                            .weight(1f)
                            .height(38.dp)
                            .border(
                                1.dp,
                                if (isContactlessEnabled) PayDzGreen else PayDzInputBorder,
                                RoundedCornerShape(14.dp)
                            )
                            .clickable { onToggleContactless() }
                            .testTag("card_action_toggle_nfc")
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 8.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Contactless,
                                contentDescription = null,
                                tint = if (isContactlessEnabled) PayDzGreen else PayDzSlateText,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = if (isAr) (if (isContactlessEnabled) "NFC مفعل" else "تفعيل NFC") else "NFC",
                                fontFamily = CairoFontFamily,
                                color = if (isContactlessEnabled) PayDzGreen else PayDzNavyText,
                                fontWeight = FontWeight.Bold,
                                fontSize = 11.sp
                            )
                        }
                    }

                    // 3. Freeze / Unfreeze Card
                    Surface(
                        shape = RoundedCornerShape(14.dp),
                        color = if (isCardFrozen) PayDzRedSoft else Color.White,
                        shadowElevation = 2.dp,
                        modifier = Modifier
                            .weight(1f)
                            .height(38.dp)
                            .border(
                                1.dp,
                                if (isCardFrozen) PayDzRed else PayDzInputBorder,
                                RoundedCornerShape(14.dp)
                            )
                            .clickable { onToggleCardFrozen() }
                            .testTag("card_action_toggle_freeze")
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 8.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Lock,
                                contentDescription = null,
                                tint = if (isCardFrozen) PayDzRed else PayDzBluePrimary,
                                modifier = Modifier.size(15.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = if (isAr) (if (isCardFrozen) "إلغاء التجميد" else "تجميد البطاقة") else "Geler",
                                fontFamily = CairoFontFamily,
                                color = if (isCardFrozen) PayDzRed else PayDzNavyText,
                                fontWeight = FontWeight.Bold,
                                fontSize = 11.sp
                            )
                        }
                    }
                }

                if (copiedFeedback != null) {
                    Text(
                        text = copiedFeedback.orEmpty(),
                        fontFamily = CairoFontFamily,
                        color = PayDzGreen,
                        fontWeight = FontWeight.Bold,
                        fontSize = 11.sp
                    )
                }

                // Post-Account-Login Biometric Fingerprint Setup Card (appears after account login if fingerprint not added yet)
                if (!isBiometricEnrolled) {
                    Surface(
                        shape = RoundedCornerShape(18.dp),
                        color = Color.White,
                        shadowElevation = 3.dp,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 20.dp)
                            .border(1.dp, PayDzBluePrimary.copy(alpha = 0.3f), RoundedCornerShape(18.dp))
                            .clickable { onOpenBiometricEnroll() }
                            .testTag("home_enroll_fingerprint_banner")
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 14.dp, vertical = 10.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(10.dp),
                                modifier = Modifier.weight(1f)
                            ) {
                                Image(
                                    painter = painterResource(id = R.drawable.canva_ic_fingerprint),
                                    contentDescription = null,
                                    contentScale = ContentScale.Fit,
                                    modifier = Modifier.size(30.dp)
                                )
                                Column {
                                    Text(
                                        text = if (isAr) "إضافة بصمة الإصبع لهذا الحساب" else "Activer l'empreinte biométrique",
                                        fontFamily = CairoFontFamily,
                                        color = PayDzNavyText,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 12.5.sp
                                    )
                                    Text(
                                        text = if (isAr) "فعّل البصمة الآن لتسجيل الدخول السريع لحسابك" else "Connexion rapide par empreinte",
                                        fontFamily = CairoFontFamily,
                                        color = PayDzSlateText,
                                        fontSize = 11.sp
                                    )
                                }
                            }
                            Surface(
                                shape = RoundedCornerShape(50),
                                color = PayDzBluePrimary
                            ) {
                                Text(
                                    text = if (isAr) "تفعيل البصمة" else "Activer",
                                    fontFamily = CairoFontFamily,
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 11.sp,
                                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 5.dp)
                                )
                            }
                        }
                    }
                }
            }
        }

        // 3. 2x2 Quick Action Cards (Right: شحن الهاتف | Left: تحويل أموال / Right: الحوالات المالية | Left: دفع الفواتير)
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    PayDzQuickActionCard(
                        title = if (isAr) "شحن\nالهاتف" else "Recharge\nMobile",
                        iconRes = R.drawable.canva_ic_mobile_topup,
                        onClick = { onOpenModal(ActiveModal.MOBILE_TOPUP) },
                        testTag = "action_mobile_topup",
                        modifier = Modifier.weight(1f)
                    )
                    PayDzQuickActionCard(
                        title = if (isAr) "تحويل\nأموال" else "Transfert\nd'Argent",
                        iconRes = R.drawable.canva_ic_send_plane,
                        onClick = { onOpenModal(ActiveModal.RIP_TRANSFER) },
                        testTag = "action_rip_transfer",
                        modifier = Modifier.weight(1f)
                    )
                }
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    PayDzQuickActionCard(
                        title = if (isAr) "الحوالات\nالمالية" else "Mandats\nFinanciers",
                        iconRes = R.drawable.canva_ic_remittance_arrows,
                        onClick = { onOpenModal(ActiveModal.CARDLESS_ATM) },
                        testTag = "action_cardless_atm",
                        modifier = Modifier.weight(1f)
                    )
                    PayDzQuickActionCard(
                        title = if (isAr) "دفع\nالفواتير" else "Paiement\nFactures",
                        iconRes = R.drawable.canva_ic_bill_invoice,
                        onClick = { onOpenModal(ActiveModal.BILL_PAYMENT) },
                        testTag = "action_bill_payment",
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }

        // 4. آخر العمليات (Recent Operations Header: 14sp, Bold 700 + عرض الكل: 12sp, Regular/Medium 400-500)
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 22.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = if (isAr) "آخر العمليات" else "Dernières opérations",
                    fontFamily = CairoFontFamily,
                    color = PayDzNavyText,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = if (isAr) "عرض الكل" else "Voir tout",
                    fontFamily = CairoFontFamily,
                    color = PayDzBluePrimary,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier
                        .clickable { onNavigateToHistory() }
                        .padding(vertical = 4.dp)
                        .testTag("home_view_all_transactions")
                )
            }
        }

        // Recent Transactions List
        val recentTxs = transactions.take(3)
        if (recentTxs.isEmpty()) {
            item {
                PayDzHomeTransactionCard(
                    title = if (isAr) "تحويل إلى صديق" else "Transfert à un ami",
                    subtitle = if (isAr) "اليوم · 14:30" else "Aujourd'hui · 14:30",
                    amountText = "-5,000.00 دج",
                    isPositive = false,
                    onClick = {},
                    modifier = Modifier.padding(horizontal = 20.dp)
                )
            }
        } else {
            items(recentTxs, key = { it.id.ifEmpty { it.referenceCode } }) { tx ->
                val isPositive = tx.type == "deposit"
                val formattedNum = NumberFormat.getNumberInstance(Locale.US).format(tx.amountDzd)
                val sign = if (isPositive) "+" else "-"
                val amountLabel = if (isPrivacyMode) "•••• دج" else "$sign$formattedNum.00 دج"
                val displayTitle = when {
                    isPositive -> "من: \u2068${tx.recipientName.ifBlank { tx.title }}\u2069"
                    tx.type == "transfer" -> "إلى: \u2068${tx.recipientName.ifBlank { tx.title }}\u2069"
                    else -> tx.title
                }
                val badgeVariant = when {
                    tx.type == "bill" -> "transfer_blue"
                    else -> "transfer_green"
                }
                PayDzHomeTransactionCard(
                    title = displayTitle,
                    subtitle = tx.note.ifBlank { "اليوم · 14:30" },
                    amountText = amountLabel,
                    isPositive = isPositive,
                    badgeVariant = badgeVariant,
                    onClick = { onSelectTransaction(tx) },
                    modifier = Modifier.padding(horizontal = 20.dp)
                )
            }
        }

        // 5. PayDz 5-Feature Strip at bottom
        item {
            PayDzFiveFeatureStrip(
                isAr = isAr,
                modifier = Modifier.padding(horizontal = 20.dp, vertical = 4.dp)
            )
        }
    }
}

@Composable
private fun PayDzQuickActionCard(
    title: String,
    iconRes: Int,
    onClick: () -> Unit,
    testTag: String,
    modifier: Modifier = Modifier
) {
    Surface(
        shape = RoundedCornerShape(24.dp),
        color = Color.White,
        shadowElevation = 4.dp,
        modifier = modifier
            .height(106.dp)
            .border(1.dp, Color(0xFFE6EEF9), RoundedCornerShape(24.dp))
            .clickable(onClick = onClick)
            .testTag(testTag)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Image(
                painter = painterResource(id = iconRes),
                contentDescription = title,
                contentScale = ContentScale.Fit,
                modifier = Modifier.size(34.dp)
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = title,
                fontFamily = CairoFontFamily,
                color = PayDzCardTitleText,
                fontSize = 13.sp,
                lineHeight = 16.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
private fun PayDzHomeTransactionCard(
    title: String,
    subtitle: String,
    amountText: String,
    isPositive: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    badgeVariant: String = "transfer_green"
) {
    Surface(
        shape = RoundedCornerShape(22.dp),
        color = Color.White,
        shadowElevation = 3.dp,
        modifier = modifier
            .fillMaxWidth()
            .border(1.dp, Color(0xFFE6EEF9), RoundedCornerShape(22.dp))
            .clickable(onClick = onClick)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 14.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.weight(1f)
            ) {
                PayDzTransactionBadgeIcon(
                    variant = badgeVariant,
                    size = 44.dp
                )
                Column {
                    Text(
                        text = title,
                        fontFamily = CairoFontFamily,
                        color = PayDzNavyText,
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        text = subtitle,
                        fontFamily = CairoFontFamily,
                        color = PayDzSlateText,
                        fontWeight = FontWeight.Normal,
                        fontSize = 11.sp
                    )
                }
            }

            Text(
                text = amountText,
                fontFamily = PoppinsFontFamily,
                color = if (isPositive) PayDzGreen else PayDzRed,
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp
            )
        }
    }
}

/**
 * SCREEN 4 OF PAYDZ MOCKUP: جميع الخدمات (All Services Screen)
 */
@Composable
fun AllServicesScreen(
    isAr: Boolean,
    onBack: () -> Unit,
    onOpenModal: (ActiveModal) -> Unit,
    onNavigateToCardTab: () -> Unit,
    onNavigateToBeneficiaries: () -> Unit,
    onNavigateToSupport: () -> Unit
) {
    var query by remember { mutableStateOf("") }

    data class PayDzServiceItem(
        val titleAr: String,
        val titleFr: String,
        val iconRes: Int,
        val testTag: String,
        val onClick: () -> Unit
    )

    // Exact 2x4 order matching Screen 4 in RTL (Item 1 = Right column, Item 2 = Left column):
    // Row 1: Right = شحن الهاتف | Left = إنترنت منزلي Idoom
    // Row 2: Right = تحويل الأموال | Left = دفع الفواتير
    // Row 3: Right = بطاقة الدفع | Left = الحوالات المالية
    // Row 4: Right = إدارة المستفيدين | Left = خدمات أخرى
    val allServices = listOf(
        PayDzServiceItem(
            titleAr = "شحن الهاتف",
            titleFr = "Recharge Mobile",
            iconRes = R.drawable.canva_ic_mobile_topup,
            testTag = "service_mobile_topup",
            onClick = { onOpenModal(ActiveModal.MOBILE_TOPUP) }
        ),
        PayDzServiceItem(
            titleAr = "إنترنت منزلي\nIdoom",
            titleFr = "Internet Idoom\nADSL / Fibre",
            iconRes = R.drawable.canva_ic_idoom_wifi,
            testTag = "service_idoom_internet",
            onClick = { onOpenModal(ActiveModal.IDOOM_INTERNET) }
        ),
        PayDzServiceItem(
            titleAr = "تحويل الأموال",
            titleFr = "Transfert d'Argent",
            iconRes = R.drawable.canva_ic_send_plane,
            testTag = "service_money_transfer",
            onClick = { onOpenModal(ActiveModal.RIP_TRANSFER) }
        ),
        PayDzServiceItem(
            titleAr = "دفع الفواتير",
            titleFr = "Paiement Factures",
            iconRes = R.drawable.canva_ic_bill_invoice,
            testTag = "service_bill_payment",
            onClick = { onOpenModal(ActiveModal.BILL_PAYMENT) }
        ),
        PayDzServiceItem(
            titleAr = "بطاقة الدفع",
            titleFr = "Carte PayDz",
            iconRes = R.drawable.canva_ic_payment_card,
            testTag = "service_payment_card",
            onClick = onNavigateToCardTab
        ),
        PayDzServiceItem(
            titleAr = "الحوالات المالية",
            titleFr = "Mandats Financiers",
            iconRes = R.drawable.canva_ic_remittance_arrows,
            testTag = "service_money_orders",
            onClick = { onOpenModal(ActiveModal.CARDLESS_ATM) }
        ),
        PayDzServiceItem(
            titleAr = "إدارة المستفيدين",
            titleFr = "Bénéficiaires",
            iconRes = R.drawable.canva_ic_beneficiaries,
            testTag = "service_beneficiaries",
            onClick = { onOpenModal(ActiveModal.ADD_BENEFICIARY) }
        ),
        PayDzServiceItem(
            titleAr = "خدمات أخرى",
            titleFr = "Autres Services",
            iconRes = R.drawable.canva_ic_more_dots,
            testTag = "service_other",
            onClick = { onOpenModal(ActiveModal.QR_PAYMENT) }
        )
    )

    val filtered = remember(query, isAr) {
        if (query.isBlank()) allServices
        else allServices.filter {
            it.titleAr.contains(query, ignoreCase = true) ||
                it.titleFr.contains(query, ignoreCase = true)
        }
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(PayDzBgSoft)
            .padding(horizontal = 20.dp),
        contentPadding = PaddingValues(top = 12.dp, bottom = 28.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Header: < جميع الخدمات (17sp, Bold 700, #0F172A)
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp)
            ) {
                Text(
                    text = if (isAr) "جميع الخدمات" else "Tous les Services",
                    fontFamily = CairoFontFamily,
                    color = PayDzNavyText,
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.align(Alignment.Center)
                )
                IconButton(
                    onClick = onBack,
                    modifier = Modifier
                        .align(Alignment.CenterEnd)
                        .size(40.dp)
                        .testTag("services_back_button")
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.KeyboardArrowLeft,
                        contentDescription = "Back",
                        tint = PayDzNavyText,
                        modifier = Modifier.size(32.dp)
                    )
                }
            }
        }

        // Rounded Search Bar: ابحث عن خدمة...
        item {
            PayDzSearchBar(
                query = query,
                onQueryChange = { query = it },
                placeholder = if (isAr) "ابحث عن خدمة..." else "Rechercher un service...",
                testTag = "services_search_input"
            )
        }

        // 2x4 Grid of Service Cards matching Screen 4 (13sp, Bold 700, #1E293B)
        val pairs = filtered.chunked(2)
        items(pairs) { rowItems ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                rowItems.forEach { item ->
                    Surface(
                        shape = RoundedCornerShape(26.dp),
                        color = Color.White,
                        shadowElevation = 4.dp,
                        modifier = Modifier
                            .weight(1f)
                            .height(126.dp)
                            .border(1.dp, Color(0xFFE6EEF9), RoundedCornerShape(26.dp))
                            .clickable(onClick = item.onClick)
                            .testTag(item.testTag)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(14.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Image(
                                painter = painterResource(id = item.iconRes),
                                contentDescription = item.titleAr,
                                contentScale = ContentScale.Fit,
                                modifier = Modifier.size(50.dp)
                            )
                            Spacer(modifier = Modifier.height(10.dp))
                            Text(
                                text = if (isAr) item.titleAr else item.titleFr,
                                fontFamily = CairoFontFamily,
                                color = PayDzCardTitleText,
                                fontSize = 13.sp,
                                lineHeight = 16.sp,
                                fontWeight = FontWeight.Bold,
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                }
                if (rowItems.size == 1) {
                    Spacer(modifier = Modifier.weight(1f))
                }
            }
        }
    }
}

@Composable
fun PayDzSearchBar(
    query: String,
    onQueryChange: (String) -> Unit,
    placeholder: String,
    testTag: String
) {
    Surface(
        shape = RoundedCornerShape(50),
        color = Color(0xFFE6EEF8),
        modifier = Modifier
            .fillMaxWidth()
            .height(50.dp)
            .testTag(testTag)
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 18.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Search,
                contentDescription = null,
                tint = PayDzSlateText,
                modifier = Modifier.size(22.dp)
            )
            Box(
                modifier = Modifier.weight(1f),
                contentAlignment = Alignment.CenterStart
            ) {
                if (query.isEmpty()) {
                    Text(
                        text = placeholder,
                        fontFamily = CairoFontFamily,
                        color = PayDzSlateText,
                        fontWeight = FontWeight.Normal,
                        fontSize = 12.sp
                    )
                }
                BasicTextField(
                    value = query,
                    onValueChange = onQueryChange,
                    singleLine = true,
                    textStyle = TextStyle(
                        fontFamily = CairoFontFamily,
                        color = PayDzNavyText,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold
                    ),
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}

/**
 * SCREEN 9 OF PAYDZ MOCKUP: سجل المعاملات (Transactions History Screen)
 */
@Composable
fun TransactionsHistoryScreen(
    transactions: List<WalletTransaction>,
    searchQuery: String,
    selectedCategory: String,
    isPrivacyMode: Boolean,
    isAr: Boolean,
    onSearchChange: (String) -> Unit,
    onCategoryChange: (String) -> Unit,
    onSelectTransaction: (WalletTransaction) -> Unit,
    onBack: () -> Unit = {}
) {
    val filters = listOf(
        "all" to (if (isAr) "الكل" else "Tout"),
        "incoming" to (if (isAr) "الواردة" else "Entrantes"),
        "outgoing" to (if (isAr) "الصادرة" else "Sortantes")
    )

    val filtered = remember(transactions, searchQuery, selectedCategory) {
        transactions.filter { tx ->
            val matchesFilter = when (selectedCategory) {
                "incoming" -> tx.type == "deposit"
                "outgoing" -> tx.type != "deposit"
                else -> true
            }
            val matchesSearch = searchQuery.isBlank() ||
                tx.title.contains(searchQuery, ignoreCase = true) ||
                tx.recipientName.contains(searchQuery, ignoreCase = true) ||
                tx.referenceCode.contains(searchQuery, ignoreCase = true)
            matchesFilter && matchesSearch
        }
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(PayDzBgSoft)
            .padding(horizontal = 20.dp),
        contentPadding = PaddingValues(top = 12.dp, bottom = 28.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Header: < سجل المعاملات (17sp, Bold 700, #0F172A)
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp)
            ) {
                Text(
                    text = if (isAr) "سجل المعاملات" else "Historique des Transactions",
                    fontFamily = CairoFontFamily,
                    color = PayDzNavyText,
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.align(Alignment.Center)
                )
                IconButton(
                    onClick = onBack,
                    modifier = Modifier
                        .align(Alignment.CenterEnd)
                        .size(40.dp)
                        .testTag("history_back_button")
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.KeyboardArrowLeft,
                        contentDescription = "Back",
                        tint = PayDzNavyText,
                        modifier = Modifier.size(32.dp)
                    )
                }
            }
        }

        // Search Bar: ابحث عن معاملة...
        item {
            PayDzSearchBar(
                query = searchQuery,
                onQueryChange = onSearchChange,
                placeholder = if (isAr) "ابحث عن معاملة..." else "Rechercher une transaction...",
                testTag = "history_search_input"
            )
        }

        // 3 Filter Pills: الكل | الواردة | الصادرة
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                filters.forEach { (key, label) ->
                    val isSelected = selectedCategory == key ||
                        (selectedCategory !in listOf("all", "incoming", "outgoing") && key == "all")
                    Surface(
                        shape = RoundedCornerShape(50),
                        color = if (isSelected) PayDzBluePrimary else Color.White,
                        shadowElevation = if (isSelected) 4.dp else 1.dp,
                        modifier = Modifier
                            .weight(1f)
                            .height(42.dp)
                            .border(
                                width = 1.dp,
                                color = if (isSelected) PayDzBluePrimary else PayDzInputBorder,
                                shape = RoundedCornerShape(50)
                            )
                            .clickable { onCategoryChange(key) }
                            .testTag("filter_chip_$key")
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Text(
                                text = label,
                                fontFamily = CairoFontFamily,
                                color = if (isSelected) Color.White else PayDzNavyText,
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp
                            )
                        }
                    }
                }
            }
        }

        // Transaction Cards matching Screen 9:
        // In RTL: Right (Start) = Date (07/11), Center = Amount (+10,000.00 دج) + Title (من: فاطمة), Left (End) = Circular Badge
        items(filtered, key = { it.id.ifEmpty { it.referenceCode } }) { tx ->
            val isIncoming = tx.type == "deposit"
            val formattedNum = NumberFormat.getNumberInstance(Locale.US).format(tx.amountDzd)
            val sign = if (isIncoming) "+" else "-"
            val amountDisplay = if (isPrivacyMode) "•••• دج" else "$sign$formattedNum.00 دج"

            val dateShort = remember(tx.createdAt, tx.note) {
                when {
                    tx.note.startsWith("07/11") -> "07/11"
                    tx.note.startsWith("06/11") -> "06/11"
                    tx.note.startsWith("05/11") -> "05/11"
                    else -> tx.createdAt?.toDate()?.let {
                        SimpleDateFormat("dd/MM", Locale.US).format(it)
                    } ?: "07/11"
                }
            }

            val subtitleLabel = when {
                isIncoming -> "من: \u2068${tx.recipientName.ifBlank { tx.title }}\u2069"
                tx.type == "transfer" -> "إلى: \u2068${tx.recipientName.ifBlank { tx.title }}\u2069"
                else -> tx.title
            }

            val badgeVariant = when {
                isIncoming -> "transfer_green"
                tx.type == "bill" -> "transfer_blue"
                else -> "bolt_orange"
            }

            Surface(
                shape = RoundedCornerShape(22.dp),
                color = Color.White,
                shadowElevation = 3.dp,
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, Color(0xFFE6EEF9), RoundedCornerShape(22.dp))
                    .clickable { onSelectTransaction(tx) }
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 14.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Right side (Start in RTL): Date (07/11) - 11sp, Regular 400, #64748B
                    Text(
                        text = dateShort,
                        fontFamily = PoppinsFontFamily,
                        color = PayDzSlateText,
                        fontWeight = FontWeight.Normal,
                        fontSize = 11.sp
                    )

                    // Center: Amount (+10,000.00 دج, 14sp Bold 700) on top + Subtitle (من: فاطمة, 11sp Regular 400 #64748B) below
                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .padding(horizontal = 14.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(2.dp)
                    ) {
                        Text(
                            text = amountDisplay,
                            fontFamily = PoppinsFontFamily,
                            color = if (isIncoming) PayDzGreen else PayDzRed,
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
                        )
                        Text(
                            text = subtitleLabel,
                            fontFamily = CairoFontFamily,
                            color = PayDzSlateText,
                            fontWeight = FontWeight.Normal,
                            fontSize = 11.sp,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }

                    // Left side (End in RTL): Crisp Vector Circular Icon Badge
                    PayDzTransactionBadgeIcon(
                        variant = badgeVariant,
                        size = 44.dp
                    )
                }
            }
        }

        // Bottom 5-Feature Strip matching the mockup footer
        item {
            Spacer(modifier = Modifier.height(6.dp))
            PayDzFiveFeatureStrip(isAr = isAr)
        }
    }
}

/**
 * المزيد (More Hub: Card Management, Beneficiaries, 24/7 Support, Language, Sign Out)
 */
@Composable
fun MoreHubScreen(
    account: WalletAccount?,
    beneficiaries: List<Beneficiary>,
    supportTickets: List<SupportTicket>,
    isPrivacyMode: Boolean,
    isAr: Boolean,
    onToggleLanguage: () -> Unit,
    onOpenModal: (ActiveModal) -> Unit,
    onDeleteBeneficiary: (String) -> Unit,
    onResolveTicket: (String) -> Unit,
    onUpdateSettings: (
        cardFrozen: Boolean,
        onlinePayments: Boolean,
        contactless: Boolean,
        biometric: Boolean,
        twoFactor: Boolean,
        transferLimit: Long,
        withdrawalLimit: Long
    ) -> Unit,
    onSignOut: () -> Unit,
    isBiometricEnrolled: Boolean = false,
    onOpenBiometricEnroll: () -> Unit = {},
    onDisableBiometricEnroll: () -> Unit = {}
) {
    var selectedSection by remember { mutableIntStateOf(0) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(PayDzBgSoft)
    ) {
        // Top Header + Action Pills
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = if (isAr) "المزيد وإعدادات PayDz" else "Plus & Paramètres PayDz",
                    color = PayDzNavyText,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.ExtraBold
                )
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Surface(
                        shape = RoundedCornerShape(50),
                        color = Color.White,
                        modifier = Modifier
                            .border(1.dp, PayDzInputBorder, RoundedCornerShape(50))
                            .clickable { onToggleLanguage() }
                            .testTag("language_switch_button")
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Icon(Icons.Default.Language, contentDescription = null, tint = PayDzBluePrimary, modifier = Modifier.size(16.dp))
                            Text(
                                text = if (isAr) "FR" else "عربي",
                                color = PayDzNavyText,
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp
                            )
                        }
                    }
                    Surface(
                        shape = RoundedCornerShape(50),
                        color = PayDzRedSoft,
                        modifier = Modifier
                            .clickable { onSignOut() }
                            .testTag("sign_out_button")
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Icon(Icons.AutoMirrored.Filled.Logout, contentDescription = "Sign Out", tint = PayDzRed, modifier = Modifier.size(16.dp))
                            Text(
                                text = if (isAr) "خروج" else "Sortir",
                                color = PayDzRed,
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp
                            )
                        }
                    }
                }
            }

            // Section Sub-Tabs: بطاقة الدفع | المستفيدون | الدعم 24/7
            val tabs = listOf(
                if (isAr) "بطاقة PayDz والأمان" else "Carte & Sécurité",
                if (isAr) "المستفيدون" else "Bénéficiaires",
                if (isAr) "الدعم الفني 24/7" else "Support 24/7"
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                tabs.forEachIndexed { idx, label ->
                    val active = selectedSection == idx
                    Surface(
                        shape = RoundedCornerShape(50),
                        color = if (active) PayDzBluePrimary else Color.White,
                        modifier = Modifier
                            .weight(1f)
                            .height(40.dp)
                            .border(1.dp, if (active) PayDzBluePrimary else PayDzInputBorder, RoundedCornerShape(50))
                            .clickable { selectedSection = idx }
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Text(
                                text = label,
                                color = if (active) Color.White else PayDzNavyText,
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp
                            )
                        }
                    }
                }
            }
        }

        when (selectedSection) {
            0 -> GoldCardAndSecurityScreen(
                account = account,
                isPrivacyMode = isPrivacyMode,
                isAr = isAr,
                onOpenModal = onOpenModal,
                onUpdateSettings = onUpdateSettings,
                isBiometricEnrolled = isBiometricEnrolled,
                onOpenBiometricEnroll = onOpenBiometricEnroll,
                onDisableBiometricEnroll = onDisableBiometricEnroll
            )
            1 -> BeneficiariesManagementSection(
                beneficiaries = beneficiaries,
                isAr = isAr,
                onAddBeneficiary = { onOpenModal(ActiveModal.ADD_BENEFICIARY) },
                onDeleteBeneficiary = onDeleteBeneficiary
            )
            2 -> SupportCenterScreen(
                tickets = supportTickets,
                isAr = isAr,
                onOpenNewTicket = { onOpenModal(ActiveModal.NEW_SUPPORT_TICKET) },
                onResolveTicket = onResolveTicket
            )
        }
    }
}

@Composable
private fun BeneficiariesManagementSection(
    beneficiaries: List<Beneficiary>,
    isAr: Boolean,
    onAddBeneficiary: () -> Unit,
    onDeleteBeneficiary: (String) -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(20.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Button(
                onClick = onAddBeneficiary,
                shape = RoundedCornerShape(50),
                colors = ButtonDefaults.buttonColors(containerColor = PayDzBluePrimary),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
            ) {
                Icon(Icons.Default.AddCircle, contentDescription = null)
                Spacer(Modifier.width(8.dp))
                Text(
                    text = if (isAr) "إضافة مستفيد جديد" else "Ajouter un bénéficiaire",
                    fontWeight = FontWeight.Bold
                )
            }
        }
        if (beneficiaries.isEmpty()) {
            item {
                Surface(
                    shape = RoundedCornerShape(20.dp),
                    color = Color.White,
                    shadowElevation = 2.dp,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(64.dp)
                                .clip(CircleShape)
                                .background(Color(0xFFF1F6FF)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Lock,
                                contentDescription = null,
                                tint = PayDzBluePrimary,
                                modifier = Modifier.size(32.dp)
                            )
                        }
                        Text(
                            text = if (isAr) "قائمتك خالية من المستفيدين" else "Aucun bénéficiaire enregistré",
                            fontFamily = CairoFontFamily,
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp,
                            color = PayDzNavyText
                        )
                        Text(
                            text = if (isAr) {
                                "حفاظاً على خصوصيتك، لا يقترح التطبيق أي مستخدمين آخرين. عندما يزودك شخص برقم RIP الخاص به (20 رقماً)، يمكنك إضافته هنا للتحويل السريع."
                            } else {
                                "Pour préserver votre vie privée, l'application ne propose aucun utilisateur public. Vous pouvez ajouter un bénéficiaire manuellement avec son RIP."
                            },
                            fontFamily = CairoFontFamily,
                            color = PayDzSlateText,
                            fontSize = 12.5.sp,
                            lineHeight = 18.sp,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }
        }
        items(beneficiaries, key = { it.id.ifEmpty { it.ripNumber } }) { ben ->
            Surface(
                shape = RoundedCornerShape(20.dp),
                color = Color.White,
                shadowElevation = 2.dp,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(ben.fullName, fontWeight = FontWeight.ExtraBold, color = PayDzNavyText)
                        Text(ben.ripNumber, fontFamily = RobotoMonoFontFamily, color = PayDzSlateText, fontSize = 12.sp)
                    }
                    if (ben.id.isNotEmpty()) {
                        IconButton(onClick = { onDeleteBeneficiary(ben.id) }) {
                            Icon(Icons.Default.Delete, contentDescription = "Delete", tint = PayDzRed)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun GoldCardAndSecurityScreen(
    account: WalletAccount?,
    isPrivacyMode: Boolean,
    isAr: Boolean,
    onOpenModal: (ActiveModal) -> Unit = {},
    onUpdateSettings: (
        cardFrozen: Boolean,
        onlinePayments: Boolean,
        contactless: Boolean,
        biometric: Boolean,
        twoFactor: Boolean,
        transferLimit: Long,
        withdrawalLimit: Long
    ) -> Unit,
    isBiometricEnrolled: Boolean = false,
    onOpenBiometricEnroll: () -> Unit = {},
    onDisableBiometricEnroll: () -> Unit = {}
) {
    val isFrozen = account?.cardFrozen ?: false
    val onlineEnabled = account?.onlinePaymentsEnabled ?: true
    val contactlessEnabled = account?.contactlessEnabled ?: true
    val biometricEnabled = account?.biometricEnabled ?: true
    val twoFactorEnabled = account?.twoFactorEnabled ?: true
    val currentTransferLimit = account?.dailyTransferLimitDzd ?: 200000L
    val currentWithdrawalLimit = account?.dailyWithdrawalLimitDzd ?: 50000L

    var transferSlider by remember(currentTransferLimit) { mutableFloatStateOf(currentTransferLimit.toFloat()) }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(horizontal = 20.dp, vertical = 12.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            Card(
                shape = RoundedCornerShape(22.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(18.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Text(
                        text = if (isAr) "حسابي والمحفظة التجريبية (PayDz Sandbox)" else "Mon Compte & Portefeuille Sandbox",
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 16.sp,
                        color = PayDzNavyText
                    )
                    Text(
                        text = if (isAr) "الاسم: ${account?.fullName ?: "أحمد محمد"} • الهاتف: ${account?.phoneNumber ?: "0612345678"}\nرقم RIP: ${account?.ripNumber ?: "00799999004125898421"}"
                        else "RIP: ${account?.ripNumber ?: "00799999004125898421"}",
                        color = PayDzSlateText,
                        fontSize = 13.sp,
                        lineHeight = 19.sp
                    )
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Button(
                            onClick = { onOpenModal(ActiveModal.ACCOUNT_PROFILE) },
                            shape = RoundedCornerShape(50),
                            colors = ButtonDefaults.buttonColors(containerColor = PayDzBluePrimary),
                            modifier = Modifier
                                .weight(1f)
                                .testTag("open_account_profile_button")
                        ) {
                            Text(
                                text = if (isAr) "حسابي وتعديل البيانات" else "Mon Profil",
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp
                            )
                        }
                        Button(
                            onClick = { onOpenModal(ActiveModal.RECHARGE_SANDBOX) },
                            shape = RoundedCornerShape(50),
                            colors = ButtonDefaults.buttonColors(containerColor = PayDzGreen),
                            modifier = Modifier
                                .weight(1f)
                                .testTag("open_sandbox_recharge_button")
                        ) {
                            Text(
                                text = if (isAr) "شحن المحفظة التجريبية" else "Recharger",
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp
                            )
                        }
                    }
                }
            }
        }
        item {
            Card(
                shape = RoundedCornerShape(22.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(18.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(10.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Image(
                                painter = painterResource(id = R.drawable.canva_ic_fingerprint),
                                contentDescription = null,
                                contentScale = ContentScale.Fit,
                                modifier = Modifier.size(34.dp)
                            )
                            Column {
                                Text(
                                    text = if (isAr) "الدخول ببصمة الإصبع (Biometric)" else "Connexion par empreinte",
                                    fontWeight = FontWeight.ExtraBold,
                                    fontSize = 15.sp,
                                    color = PayDzNavyText
                                )
                                Text(
                                    text = if (isBiometricEnrolled) {
                                        if (isAr) "مفعّلة ومرتبطة بهذا الحساب ✓" else "Activée pour ce compte ✓"
                                    } else {
                                        if (isAr) "غير مضافة بعد — فعّلها للدخول السريع" else "Non configurée"
                                    },
                                    color = if (isBiometricEnrolled) PayDzGreen else PayDzSlateText,
                                    fontWeight = if (isBiometricEnrolled) FontWeight.Bold else FontWeight.Normal,
                                    fontSize = 12.sp
                                )
                            }
                        }

                        Button(
                            onClick = {
                                if (isBiometricEnrolled) {
                                    onDisableBiometricEnroll()
                                } else {
                                    onOpenBiometricEnroll()
                                }
                            },
                            shape = RoundedCornerShape(50),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (isBiometricEnrolled) PayDzRedSoft else PayDzBluePrimary,
                                contentColor = if (isBiometricEnrolled) PayDzRed else Color.White
                            ),
                            modifier = Modifier.testTag("settings_toggle_biometric_enrollment_button")
                        ) {
                            Text(
                                text = if (isBiometricEnrolled) {
                                    if (isAr) "إلغاء البصمة" else "Désactiver"
                                } else {
                                    if (isAr) "تفعيل البصمة" else "Activer"
                                },
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp
                            )
                        }
                    }
                }
            }
        }
        item {
            Card(
                shape = RoundedCornerShape(22.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White)
            ) {
                Column(
                    modifier = Modifier.padding(18.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = if (isAr) "تجميد بطاقة PayDz مؤقتاً" else "Verrouiller la carte PayDz",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.ExtraBold,
                                color = PayDzNavyText
                            )
                            Text(
                                text = if (isAr) "إيقاف فوري لجميع السحوبات والمدفوعات بلمسة واحدة" else "Bloquer instantanément les paiements",
                                style = MaterialTheme.typography.bodyMedium,
                                color = PayDzSlateText
                            )
                        }
                        Switch(
                            checked = isFrozen,
                            onCheckedChange = { frozen ->
                                onUpdateSettings(
                                    frozen,
                                    onlineEnabled,
                                    contactlessEnabled,
                                    biometricEnabled,
                                    twoFactorEnabled,
                                    currentTransferLimit,
                                    currentWithdrawalLimit
                                )
                            },
                            modifier = Modifier.testTag("switch_freeze_card")
                        )
                    }

                    HorizontalDivider()

                    SettingSwitchRow(
                        title = if (isAr) "الدفع الإلكتروني عبر الإنترنت" else "Paiements en ligne",
                        subtitle = if (isAr) "تفعيل الشراء وتسديد الفواتير" else "Activer le paiement de factures",
                        checked = onlineEnabled,
                        tag = "switch_online_pay",
                        onCheckedChange = {
                            onUpdateSettings(
                                isFrozen,
                                it,
                                contactlessEnabled,
                                biometricEnabled,
                                twoFactorEnabled,
                                currentTransferLimit,
                                currentWithdrawalLimit
                            )
                        }
                    )

                    HorizontalDivider()

                    SettingSwitchRow(
                        title = if (isAr) "الدفع اللاتلامسي NFC ورمز QR" else "Paiement sans contact NFC & QR",
                        subtitle = if (isAr) "السماح بالدفع الفوري لدى التجار" else "Autoriser les paiements QR",
                        checked = contactlessEnabled,
                        tag = "switch_contactless",
                        onCheckedChange = {
                            onUpdateSettings(
                                isFrozen,
                                onlineEnabled,
                                it,
                                biometricEnabled,
                                twoFactorEnabled,
                                currentTransferLimit,
                                currentWithdrawalLimit
                            )
                        }
                    )

                    HorizontalDivider()

                    Text(
                        text = if (isAr) "سقف التحويل اليومي: ${formatDzd(transferSlider.toLong(), isPrivacyMode)}"
                        else "Plafond de virement : ${formatDzd(transferSlider.toLong(), isPrivacyMode)}",
                        fontWeight = FontWeight.Bold,
                        color = PayDzNavyText
                    )
                    Slider(
                        value = transferSlider,
                        onValueChange = { transferSlider = it },
                        valueRange = 10000f..500000f,
                        steps = 9,
                        onValueChangeFinished = {
                            onUpdateSettings(
                                isFrozen,
                                onlineEnabled,
                                contactlessEnabled,
                                biometricEnabled,
                                twoFactorEnabled,
                                transferSlider.toLong(),
                                currentWithdrawalLimit
                            )
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun SettingSwitchRow(
    title: String,
    subtitle: String,
    checked: Boolean,
    tag: String,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(text = title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = PayDzNavyText)
            Text(text = subtitle, style = MaterialTheme.typography.bodyMedium, color = PayDzSlateText)
        }
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            modifier = Modifier.testTag(tag)
        )
    }
}

@Composable
fun SupportCenterScreen(
    tickets: List<SupportTicket>,
    isAr: Boolean,
    onOpenNewTicket: () -> Unit,
    onResolveTicket: (String) -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(20.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Card(
                shape = RoundedCornerShape(22.dp),
                colors = CardDefaults.cardColors(containerColor = PayDzBluePrimary)
            ) {
                Column(
                    modifier = Modifier.padding(18.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Icon(Icons.Default.HeadsetMic, contentDescription = null, tint = PayDzGold, modifier = Modifier.size(30.dp))
                        Text(
                            text = if (isAr) "دعم فني على مدار الساعة 24/7" else "Assistance Client 24/7",
                            color = Color.White,
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 17.sp
                        )
                    }
                    Button(
                        onClick = onOpenNewTicket,
                        colors = ButtonDefaults.buttonColors(containerColor = PayDzGold, contentColor = PayDzNavyText),
                        shape = RoundedCornerShape(50),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("open_new_ticket_button")
                    ) {
                        Text(
                            text = if (isAr) "فتح تذكرة دعم فوري" else "Nouveau ticket",
                            fontWeight = FontWeight.ExtraBold
                        )
                    }
                }
            }
        }

        items(tickets, key = { it.id.ifEmpty { it.subject } }) { ticket ->
            Surface(
                shape = RoundedCornerShape(20.dp),
                color = Color.White,
                shadowElevation = 2.dp,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(ticket.subject, fontWeight = FontWeight.ExtraBold, color = PayDzNavyText)
                    Text(ticket.userMessage, color = PayDzSlateText, fontSize = 13.sp)
                    Surface(
                        color = PayDzBgSoft,
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = ticket.supportReply,
                            color = PayDzBluePrimary,
                            fontSize = 12.sp,
                            modifier = Modifier.padding(10.dp)
                        )
                    }
                }
            }
        }
    }
}

package com.example.ui

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.border
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import com.example.R
import com.example.ui.theme.CairoFontFamily
import com.example.ui.theme.PayDzBlueBright
import com.example.ui.theme.PayDzBlueDark
import com.example.ui.theme.PayDzBluePrimary
import com.example.ui.theme.PayDzGold
import com.example.ui.theme.PayDzNavyText
import com.example.ui.theme.PoppinsFontFamily

/**
 * Crisp 100% Vector Circular Transaction Badge Icon (replaces blurry low-res raster image).
 * Matches the PayDz reference design:
 * - "transfer_green": Soft mint circle (#DCFCE7) + crisp double curved exchange arrows (#16A34A)
 * - "bolt_orange": Soft warm amber circle (#FEF3C7) + crisp golden-orange bolt (#F59E0B)
 * - "transfer_blue": Soft sky-blue circle (#E0F2FE) + crisp blue exchange arrows (#0284C7)
 */
@Composable
fun PayDzTransactionBadgeIcon(
    variant: String,
    modifier: Modifier = Modifier,
    size: Dp = 44.dp
) {
    val bgColor = when (variant) {
        "bolt_orange" -> Color(0xFFFEF3C7)
        "transfer_blue" -> Color(0xFFE0F2FE)
        else -> Color(0xFFDCFCE7)
    }
    val borderColor = when (variant) {
        "bolt_orange" -> Color(0xFFFDE68A)
        "transfer_blue" -> Color(0xFFBAE6FD)
        else -> Color(0xFFBBF7D0)
    }
    val iconColor = when (variant) {
        "bolt_orange" -> Color(0xFFF59E0B)
        "transfer_blue" -> Color(0xFF0284C7)
        else -> Color(0xFF16A34A)
    }

    Box(
        modifier = modifier
            .size(size)
            .clip(CircleShape)
            .background(bgColor)
            .border(1.dp, borderColor, CircleShape),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.size(size * 0.52f)) {
            val w = this.size.width
            val h = this.size.height
            val strokeW = w * 0.125f

            if (variant == "bolt_orange") {
                val boltPath = Path().apply {
                    moveTo(w * 0.58f, h * 0.06f)
                    lineTo(w * 0.20f, h * 0.54f)
                    lineTo(w * 0.48f, h * 0.54f)
                    lineTo(w * 0.38f, h * 0.94f)
                    lineTo(w * 0.80f, h * 0.44f)
                    lineTo(w * 0.52f, h * 0.44f)
                    close()
                }
                drawPath(path = boltPath, color = iconColor)
            } else {
                // Crisp double curved exchange arrows (matching PayDz reference icon)
                val topArrowShaft = Path().apply {
                    moveTo(w * 0.16f, h * 0.40f)
                    cubicTo(
                        w * 0.24f, h * 0.18f,
                        w * 0.56f, h * 0.16f,
                        w * 0.82f, h * 0.30f
                    )
                }
                drawPath(
                    path = topArrowShaft,
                    color = iconColor,
                    style = Stroke(width = strokeW, cap = StrokeCap.Round, join = StrokeJoin.Round)
                )
                val topArrowHead = Path().apply {
                    moveTo(w * 0.66f, h * 0.14f)
                    lineTo(w * 0.85f, h * 0.31f)
                    lineTo(w * 0.63f, h * 0.42f)
                }
                drawPath(
                    path = topArrowHead,
                    color = iconColor,
                    style = Stroke(width = strokeW, cap = StrokeCap.Round, join = StrokeJoin.Round)
                )

                val bottomArrowShaft = Path().apply {
                    moveTo(w * 0.84f, h * 0.60f)
                    cubicTo(
                        w * 0.76f, h * 0.82f,
                        w * 0.44f, h * 0.84f,
                        w * 0.18f, h * 0.70f
                    )
                }
                drawPath(
                    path = bottomArrowShaft,
                    color = iconColor,
                    style = Stroke(width = strokeW, cap = StrokeCap.Round, join = StrokeJoin.Round)
                )
                val bottomArrowHead = Path().apply {
                    moveTo(w * 0.34f, h * 0.86f)
                    lineTo(w * 0.15f, h * 0.69f)
                    lineTo(w * 0.37f, h * 0.58f)
                }
                drawPath(
                    path = bottomArrowHead,
                    color = iconColor,
                    style = Stroke(width = strokeW, cap = StrokeCap.Round, join = StrokeJoin.Round)
                )
            }
        }
    }
}

@Composable
fun PayDzSpeedWalletLogo(
    isDarkBackground: Boolean,
    modifier: Modifier = Modifier,
    iconSize: Dp = 96.dp,
    titleFontSize: androidx.compose.ui.unit.TextUnit = if (iconSize >= 115.dp) 40.sp else if (iconSize >= 95.dp) 34.sp else 28.sp
) {
    val logoRes = if (isDarkBackground) {
        R.drawable.canva_logo_white
    } else {
        R.drawable.canva_logo_blue
    }
    val textColor = if (isDarkBackground) Color.White else PayDzNavyText

    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(
            painter = painterResource(id = logoRes),
            contentDescription = "PayDz Speed Wallet Logo",
            contentScale = ContentScale.Fit,
            modifier = Modifier.size(width = iconSize * 1.32f, height = iconSize)
        )
        Spacer(modifier = Modifier.height(6.dp))
        Text(
            text = "PayDz",
            fontFamily = PoppinsFontFamily,
            color = textColor,
            fontSize = titleFontSize,
            fontWeight = FontWeight.ExtraBold,
            letterSpacing = 0.6.sp
        )
    }
}

@Composable
fun PayDzTopUpHeroIllustration(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(120.dp),
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier
                .width(176.dp)
                .height(84.dp)
                .clip(RoundedCornerShape(50))
                .background(
                    Brush.verticalGradient(
                        listOf(Color(0xFFDCEAFE), Color(0xFFEAF2FF))
                    )
                )
        )
        Image(
            painter = painterResource(id = R.drawable.canva_illus_topup_phone),
            contentDescription = "Mobile Recharge Illustration",
            contentScale = ContentScale.Fit,
            modifier = Modifier.size(width = 104.dp, height = 112.dp)
        )
    }
}

@Composable
fun PayDzBillPaymentHeroIllustration(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(120.dp),
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier
                .width(176.dp)
                .height(84.dp)
                .clip(RoundedCornerShape(50))
                .background(
                    Brush.verticalGradient(
                        listOf(Color(0xFFDCEAFE), Color(0xFFEAF2FF))
                    )
                )
        )
        Image(
            painter = painterResource(id = R.drawable.canva_illus_bill_payment),
            contentDescription = "Bill Payment Illustration",
            contentScale = ContentScale.Fit,
            modifier = Modifier.size(width = 122.dp, height = 108.dp)
        )
    }
}

@Composable
fun PayDzMoneyTransferHeroIllustration(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(120.dp),
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier
                .width(192.dp)
                .height(84.dp)
                .clip(RoundedCornerShape(50))
                .background(
                    Brush.verticalGradient(
                        listOf(Color(0xFFDCEAFE), Color(0xFFEAF2FF))
                    )
                )
        )
        Image(
            painter = painterResource(id = R.drawable.canva_illus_p2p_phones),
            contentDescription = "Money Transfer Illustration",
            contentScale = ContentScale.Fit,
            modifier = Modifier.size(width = 156.dp, height = 96.dp)
        )
    }
}

@Composable
fun PayDzSuccessConfettiHeroIllustration(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(155.dp),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.size(220.dp, 150.dp)) {
            val cx = size.width / 2f
            val cy = size.height / 2f
            val confettiSpecs = listOf(
                Triple(Offset(cx - 78f, cy - 35f), Color(0xFF3B82F6), -25f),
                Triple(Offset(cx - 52f, cy - 55f), Color(0xFF10B981), 20f),
                Triple(Offset(cx + 55f, cy - 52f), Color(0xFF2563EB), 35f),
                Triple(Offset(cx + 82f, cy - 22f), Color(0xFFF59E0B), -30f),
                Triple(Offset(cx - 86f, cy + 12f), Color(0xFFFBBF24), 40f),
                Triple(Offset(cx + 86f, cy + 18f), Color(0xFF34D399), -18f),
                Triple(Offset(cx - 62f, cy + 44f), Color(0xFF60A5FA), 15f),
                Triple(Offset(cx + 64f, cy + 46f), Color(0xFFFBBF24), -25f)
            )
            confettiSpecs.forEach { (pos, color, angle) ->
                rotate(degrees = angle, pivot = pos) {
                    drawRoundRect(
                        color = color,
                        topLeft = pos,
                        size = Size(14f, 8f),
                        cornerRadius = CornerRadius(3f, 3f)
                    )
                }
            }
        }

        Image(
            painter = painterResource(id = R.drawable.canva_illus_success_check),
            contentDescription = "Success Checkmark",
            contentScale = ContentScale.Fit,
            modifier = Modifier.size(112.dp)
        )
    }
}

@Composable
fun PayDzFiveFeatureStrip(
    isAr: Boolean,
    modifier: Modifier = Modifier
) {
    val features = listOf(
        Pair(R.drawable.canva_feat_star, if (isAr) "واجهة سهلة\nومريحة" else "Interface\nIntuitive"),
        Pair(R.drawable.canva_feat_bolt, if (isAr) "سرعة\nفي التنفيذ" else "Exécution\nRapide"),
        Pair(R.drawable.canva_feat_chart, if (isAr) "إدارة ذكية\nلمصاريفك" else "Gestion\nIntelligente"),
        Pair(R.drawable.canva_feat_support, if (isAr) "دعم فني\nعلى مدار الساعة" else "Support\nClient 24/7"),
        Pair(R.drawable.canva_feat_shield, if (isAr) "أمان عالي\nوحماية متقدمة" else "Sécurité\nAvancée")
    )

    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Surface(
            shape = RoundedCornerShape(22.dp),
            color = Color.White,
            shadowElevation = 3.dp,
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState())
                    .padding(horizontal = 10.dp, vertical = 12.dp),
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                features.forEachIndexed { index, (iconRes, label) ->
                    FeatureBadgeItem(iconRes = iconRes, label = label)
                    if (index < features.lastIndex) {
                        Box(
                            modifier = Modifier
                                .width(1.dp)
                                .height(42.dp)
                                .background(Color(0xFFE2E8F0))
                        )
                    }
                }
            }
        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(20.dp))
                .background(
                    Brush.horizontalGradient(
                        listOf(PayDzBlueDark, PayDzBluePrimary, PayDzBlueBright)
                    )
                )
                .padding(horizontal = 18.dp, vertical = 12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = if (isAr) "رفيقك المالي الذكي • كل خدماتك المالية في مكان واحد" else "Votre compagnon financier intelligent",
                    color = Color.White.copy(alpha = 0.92f),
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    text = "PayDz",
                    color = PayDzGold,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.ExtraBold,
                    fontStyle = FontStyle.Italic
                )
            }
        }
    }
}

@Composable
private fun FeatureBadgeItem(iconRes: Int, label: String) {
    Column(
        modifier = Modifier
            .width(88.dp)
            .padding(horizontal = 4.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Image(
            painter = painterResource(id = iconRes),
            contentDescription = null,
            contentScale = ContentScale.Fit,
            modifier = Modifier.size(26.dp)
        )
        Text(
            text = label,
            color = PayDzNavyText,
            fontSize = 11.sp,
            lineHeight = 14.sp,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center
        )
    }
}

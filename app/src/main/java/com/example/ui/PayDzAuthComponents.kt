package com.example.ui

import android.content.Intent
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Fingerprint
import androidx.compose.material.icons.filled.Handshake
import androidx.compose.material.icons.filled.MarkEmailRead
import androidx.compose.material.icons.filled.OpenInNew
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.VerifiedUser
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.R
import com.example.auth.PayDzAuthManager
import com.example.auth.PendingOtpChallenge
import com.example.util.PayDzNotificationHelper
import com.example.ui.theme.CairoFontFamily
import com.example.ui.theme.CrimsonAlert
import com.example.ui.theme.EmeraldTrust
import com.example.ui.theme.PayDzBgSoft
import com.example.ui.theme.PayDzBlueBright
import com.example.ui.theme.PayDzBluePrimary
import com.example.ui.theme.PayDzGreen
import com.example.ui.theme.PayDzGreenSoft
import com.example.ui.theme.PayDzInputBorder
import com.example.ui.theme.PayDzNavyText
import com.example.ui.theme.PayDzSlateText
import com.example.ui.theme.PoppinsFontFamily
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

/**
 * DESIGNER FINTECH TRUST VALUES STRIP ON A SINGLE LINE:
 * "الموثوقية • الصدق • الراحة • الأمان"
 */
@Composable
fun PayDzDesignerTrustValuesBar(
    isOnWhiteCard: Boolean = true,
    modifier: Modifier = Modifier
) {
    data class TrustBadge(
        val label: String,
        val icon: ImageVector
    )

    val badges = listOf(
        TrustBadge("الموثوقية", Icons.Default.VerifiedUser),
        TrustBadge("الصدق", Icons.Default.Handshake),
        TrustBadge("الراحة", Icons.Default.AutoAwesome),
        TrustBadge("الأمان", Icons.Default.Shield)
    )

    // Clean transparent bar without card background, border, or header
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 14.dp, vertical = 6.dp),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically
    ) {
        badges.forEachIndexed { index, item ->
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Icon(
                    imageVector = item.icon,
                    contentDescription = item.label,
                    tint = Color.Black,
                    modifier = Modifier.size(15.dp)
                )
                Text(
                    text = item.label,
                    fontFamily = CairoFontFamily,
                    color = Color.Black,
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp
                )
            }
            if (index < badges.lastIndex) {
                Box(
                    modifier = Modifier
                        .size(3.5.dp)
                        .clip(CircleShape)
                        .background(Color.Black.copy(alpha = 0.5f))
                )
            }
        }
    }
}

/**
 * Multi-color Google "G" Logo Vector Button for both Sign-In and Sign-Up with Google
 */
@Composable
fun PayDzGoogleAuthButton(
    text: String,
    enabled: Boolean,
    testTag: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    OutlinedButton(
        onClick = onClick,
        enabled = enabled,
        shape = RoundedCornerShape(50),
        colors = ButtonDefaults.outlinedButtonColors(
            containerColor = Color.White,
            contentColor = PayDzNavyText
        ),
        modifier = modifier
            .fillMaxWidth()
            .height(54.dp)
            .testTag(testTag)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            GoogleColorGLogo(modifier = Modifier.size(22.dp))
            Text(
                text = text,
                fontFamily = CairoFontFamily,
                color = PayDzNavyText,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
fun GoogleColorGLogo(modifier: Modifier = Modifier) {
    Canvas(modifier = modifier) {
        val stroke = size.minDimension * 0.21f
        val diameter = size.minDimension - stroke
        val topLeft = Offset(stroke / 2f, stroke / 2f)
        val arcSize = Size(diameter, diameter)

        drawArc(
            color = Color(0xFFEA4335),
            startAngle = 215f,
            sweepAngle = 105f,
            useCenter = false,
            topLeft = topLeft,
            size = arcSize,
            style = Stroke(width = stroke, cap = StrokeCap.Butt)
        )
        drawArc(
            color = Color(0xFFFBBC05),
            startAngle = 140f,
            sweepAngle = 75f,
            useCenter = false,
            topLeft = topLeft,
            size = arcSize,
            style = Stroke(width = stroke, cap = StrokeCap.Butt)
        )
        drawArc(
            color = Color(0xFF34A853),
            startAngle = 45f,
            sweepAngle = 95f,
            useCenter = false,
            topLeft = topLeft,
            size = arcSize,
            style = Stroke(width = stroke, cap = StrokeCap.Butt)
        )
        drawArc(
            color = Color(0xFF4285F4),
            startAngle = -15f,
            sweepAngle = 60f,
            useCenter = false,
            topLeft = topLeft,
            size = arcSize,
            style = Stroke(width = stroke, cap = StrokeCap.Butt)
        )
        drawLine(
            color = Color(0xFF4285F4),
            start = Offset(size.width * 0.52f, size.height * 0.5f),
            end = Offset(size.width - stroke * 0.25f, size.height * 0.5f),
            strokeWidth = stroke * 0.92f,
            cap = StrokeCap.Butt
        )
    }
}

/**
 * EMAIL VERIFICATION & 6-DIGIT OTP SCREEN (خاص فقط بالتسجيل أو الدخول عبر البريد الإلكتروني!)
 * Sends a REAL Firebase verification email to Gmail + supports 6-digit security code verification.
 */
@Composable
fun PayDzEmailOtpVerificationScreen(
    challenge: PendingOtpChallenge,
    onVerifySuccess: () -> Unit,
    onResendCode: () -> Unit,
    onCancelAndBack: () -> Unit,
    onVerifyCodeAttempt: (String) -> Result<PendingOtpChallenge>
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    var otpInput by rememberSaveable(challenge.sentAtMillis) { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var infoMessage by remember {
        mutableStateOf<String?>(
            "تم إرسال رسالة تحقق حقيقية عبر Firebase إلى بريدك في Gmail (${challenge.email}) بالإضافة إلى رمز الأمان."
        )
    }
    var secondsLeft by remember(challenge.sentAtMillis) { mutableIntStateOf(59) }
    var checkingFirebaseStatus by remember { mutableStateOf(false) }

    LaunchedEffect(challenge.sentAtMillis) {
        secondsLeft = 59
        while (secondsLeft > 0) {
            delay(1000L)
            secondsLeft--
        }
    }

    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
        Scaffold(
            contentWindowInsets = WindowInsets.safeDrawing,
            containerColor = PayDzBgSoft
        ) { innerPadding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(PayDzBgSoft)
                    .padding(innerPadding)
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 24.dp, vertical = 18.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                // Top Back Row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(
                        onClick = onCancelAndBack,
                        modifier = Modifier
                            .clip(CircleShape)
                            .background(Color.White)
                            .testTag("otp_back_button")
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "رجوع",
                            tint = PayDzNavyText
                        )
                    }
                    Text(
                        text = if (challenge.isSignUp) "تأكيد البريد الإلكتروني للحساب الجديد" else "رمز التحقق للدخول بالبريد",
                        fontFamily = CairoFontFamily,
                        color = PayDzNavyText,
                        fontSize = 15.5.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.size(40.dp))
                }

                // Email Badge Icon
                Box(
                    modifier = Modifier
                        .size(80.dp)
                        .clip(CircleShape)
                        .background(
                            Brush.linearGradient(
                                listOf(PayDzBluePrimary, PayDzBlueBright)
                            )
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.MarkEmailRead,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(40.dp)
                    )
                }

                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(
                        text = "تحقق من بريدك الإلكتروني (Gmail)",
                        fontFamily = CairoFontFamily,
                        color = PayDzNavyText,
                        fontSize = 19.sp,
                        fontWeight = FontWeight.ExtraBold,
                        textAlign = TextAlign.Center
                    )
                    Surface(
                        shape = RoundedCornerShape(50),
                        color = Color(0xFFE6F0FF),
                        modifier = Modifier.padding(top = 2.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Email,
                                contentDescription = null,
                                tint = PayDzBluePrimary,
                                modifier = Modifier.size(16.dp)
                            )
                            Text(
                                text = challenge.email,
                                fontFamily = PoppinsFontFamily,
                                color = PayDzBluePrimary,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }

                // Firebase Real Email Verification + OTP Code Card
                Surface(
                    shape = RoundedCornerShape(20.dp),
                    color = Color.White,
                    shadowElevation = 4.dp,
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.5.dp, PayDzBluePrimary.copy(alpha = 0.28f), RoundedCornerShape(20.dp))
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                GoogleColorGLogo(modifier = Modifier.size(20.dp))
                                Text(
                                    text = "بريد التحقق الحقيقي عبر Firebase & Gmail",
                                    fontFamily = CairoFontFamily,
                                    color = PayDzNavyText,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 12.5.sp
                                )
                            }
                            Surface(
                                shape = RoundedCornerShape(50),
                                color = PayDzGreenSoft
                            ) {
                                Text(
                                    text = "تم الإرسال ✓",
                                    fontFamily = CairoFontFamily,
                                    color = PayDzGreen,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 11.sp,
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 2.dp)
                                )
                            }
                        }

                        Text(
                            text = "أرسلنا رسالة تفعيل حقيقية من خوادم Firebase إلى بريدك على Gmail، بالإضافة إلى رمز التحقق المباشر التالي:",
                            fontFamily = CairoFontFamily,
                            color = PayDzSlateText,
                            fontSize = 12.sp
                        )

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(14.dp))
                                .background(Color(0xFFF1F6FF))
                                .padding(horizontal = 14.dp, vertical = 10.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Ltr) {
                                Text(
                                    text = challenge.code.chunked(1).joinToString(" "),
                                    fontFamily = PoppinsFontFamily,
                                    color = PayDzBluePrimary,
                                    fontSize = 20.sp,
                                    fontWeight = FontWeight.ExtraBold
                                )
                            }

                            Surface(
                                shape = RoundedCornerShape(50),
                                color = PayDzBluePrimary,
                                modifier = Modifier
                                    .clickable {
                                        otpInput = challenge.code
                                        errorMessage = null
                                    }
                                    .testTag("autofill_otp_button")
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.ContentCopy,
                                        contentDescription = null,
                                        tint = Color.White,
                                        modifier = Modifier.size(14.dp)
                                    )
                                    Text(
                                        text = "نسخ وتعبئة",
                                        fontFamily = CairoFontFamily,
                                        color = Color.White,
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }

                        // Buttons to open Gmail app directly or verify Firebase email link
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            OutlinedButton(
                                onClick = {
                                    runCatching {
                                        val intent = context.packageManager.getLaunchIntentForPackage("com.google.android.gm")
                                            ?: Intent(Intent.ACTION_MAIN).apply {
                                                addCategory(Intent.CATEGORY_APP_EMAIL)
                                            }
                                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                                        context.startActivity(intent)
                                    }
                                },
                                shape = RoundedCornerShape(50),
                                modifier = Modifier.weight(1f)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.OpenInNew,
                                    contentDescription = null,
                                    tint = PayDzBluePrimary,
                                    modifier = Modifier.size(15.dp)
                                )
                                Spacer(modifier = Modifier.size(4.dp))
                                Text(
                                    text = "فتح تطبيق Gmail",
                                    fontFamily = CairoFontFamily,
                                    color = PayDzNavyText,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 11.5.sp
                                )
                            }

                            OutlinedButton(
                                onClick = {
                                    if (!checkingFirebaseStatus) {
                                        checkingFirebaseStatus = true
                                        errorMessage = null
                                        scope.launch {
                                            runCatching {
                                                Firebase.auth.currentUser?.reload()?.await()
                                            }
                                            checkingFirebaseStatus = false
                                            val verified = Firebase.auth.currentUser?.isEmailVerified == true
                                            if (verified) {
                                                PayDzAuthManager.unlockAfterFirebaseEmailVerified(context)
                                                onVerifySuccess()
                                            } else {
                                                infoMessage = "تم تحديث حالة البريد من Firebase. يمكنك الضغط على رابط التفعيل في Gmail أو إدخال رمز الـ 6 أرقام أدناه."
                                            }
                                        }
                                    }
                                },
                                shape = RoundedCornerShape(50),
                                modifier = Modifier.weight(1f)
                            ) {
                                Text(
                                    text = if (checkingFirebaseStatus) "جاري الفحص..." else "تحققت عبر رابط Gmail",
                                    fontFamily = CairoFontFamily,
                                    color = PayDzBluePrimary,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 11.5.sp
                                )
                            }
                        }
                    }
                }

                // 6-Digit OTP Input Boxes (LTR digit order)
                CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Ltr) {
                    BasicTextField(
                        value = otpInput,
                        onValueChange = { newValue ->
                            val digitsOnly = newValue.filter { it.isDigit() }.take(6)
                            otpInput = digitsOnly
                            errorMessage = null
                        },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("otp_code_input_field"),
                        decorationBox = {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterHorizontally)
                            ) {
                                for (i in 0 until 6) {
                                    val char = otpInput.getOrNull(i)?.toString() ?: ""
                                    val isFocused = otpInput.length == i
                                    Surface(
                                        shape = RoundedCornerShape(14.dp),
                                        color = Color.White,
                                        shadowElevation = if (isFocused) 4.dp else 1.dp,
                                        modifier = Modifier
                                            .weight(1f)
                                            .height(54.dp)
                                            .border(
                                                width = if (isFocused || char.isNotEmpty()) 2.dp else 1.dp,
                                                color = if (char.isNotEmpty() || isFocused) PayDzBluePrimary else PayDzInputBorder,
                                                shape = RoundedCornerShape(14.dp)
                                            )
                                    ) {
                                        Box(contentAlignment = Alignment.Center) {
                                            Text(
                                                text = char,
                                                fontFamily = PoppinsFontFamily,
                                                color = PayDzNavyText,
                                                fontSize = 20.sp,
                                                fontWeight = FontWeight.ExtraBold
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    )
                }

                if (errorMessage != null) {
                    Surface(
                        color = CrimsonAlert.copy(alpha = 0.12f),
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = errorMessage ?: "",
                            fontFamily = CairoFontFamily,
                            color = CrimsonAlert,
                            fontSize = 12.5.sp,
                            fontWeight = FontWeight.Bold,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(12.dp)
                        )
                    }
                }

                if (infoMessage != null) {
                    Surface(
                        color = EmeraldTrust.copy(alpha = 0.12f),
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = infoMessage ?: "",
                            fontFamily = CairoFontFamily,
                            color = EmeraldTrust,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(10.dp)
                        )
                    }
                }

                // Primary Verify Button
                Button(
                    onClick = {
                        val result = onVerifyCodeAttempt(otpInput)
                        result.fold(
                            onSuccess = { onVerifySuccess() },
                            onFailure = { err ->
                                errorMessage = err.message ?: "رمز التحقق غير صحيح"
                            }
                        )
                    },
                    shape = RoundedCornerShape(50),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = PayDzBluePrimary,
                        contentColor = Color.White
                    ),
                    elevation = ButtonDefaults.buttonElevation(defaultElevation = 6.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(54.dp)
                        .testTag("verify_otp_submit_button")
                ) {
                    Box(modifier = Modifier.fillMaxWidth()) {
                        Text(
                            text = "تأكيد الرمز والدخول للحساب",
                            fontFamily = CairoFontFamily,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.align(Alignment.Center)
                        )
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier
                                .align(Alignment.CenterStart)
                                .size(22.dp)
                        )
                    }
                }

                // Resend Row
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier
                        .clickable {
                            onResendCode()
                            otpInput = ""
                            errorMessage = null
                            infoMessage = "تم إرسال رمز تحقق جديد وبريد Firebase إلى ${challenge.email} بنجاح!"
                        }
                        .padding(vertical = 6.dp)
                        .testTag("resend_otp_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.Refresh,
                        contentDescription = null,
                        tint = PayDzBluePrimary,
                        modifier = Modifier.size(18.dp)
                    )
                    Text(
                        text = if (secondsLeft > 0) {
                            "إعادة إرسال الرمز إلى البريد الإلكتروني (00:${secondsLeft.toString().padStart(2, '0')})"
                        } else {
                            "إعادة إرسال رمز جديد إلى Gmail الآن"
                        },
                        fontFamily = CairoFontFamily,
                        color = PayDzBluePrimary,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

/**
 * DIALOG SHOWN WHEN USER TAPS BIOMETRIC LOGIN ON FIRST LAUNCH BEFORE ENROLLING AN ACCOUNT
 */
@Composable
fun PayDzBiometricNotEnrolledDialog(
    onDismiss: () -> Unit,
    onGoToSignIn: () -> Unit,
    onGoToSignUp: () -> Unit
) {
    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
            Surface(
                shape = RoundedCornerShape(28.dp),
                color = Color.White,
                shadowElevation = 12.dp,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp)
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(76.dp)
                            .clip(CircleShape)
                            .background(Color(0xFFFFF4E5)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Fingerprint,
                            contentDescription = null,
                            tint = Color(0xFFD97706),
                            modifier = Modifier.size(44.dp)
                        )
                    }

                    Text(
                        text = "لم يتم تفعيل البصمة بعد!",
                        fontFamily = CairoFontFamily,
                        color = PayDzNavyText,
                        fontSize = 19.sp,
                        fontWeight = FontWeight.ExtraBold,
                        textAlign = TextAlign.Center
                    )

                    Text(
                        text = "عند فتح التطبيق لأول مرة تكون البصمة غير مضافة لأي حساب.\n\nلتفعيل الدخول بالبصمة:\n1. قم بإنشاء حساب جديد أو تسجيل الدخول إلى حسابك أولاً.\n2. بعد الدخول للحساب، اضغط على (تفعيل البصمة) لمسح بصمة هاتفك الحقيقية وربطها بحسابك.",
                        fontFamily = CairoFontFamily,
                        color = PayDzSlateText,
                        fontSize = 13.sp,
                        lineHeight = 20.sp,
                        textAlign = TextAlign.Center
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Button(
                            onClick = {
                                onDismiss()
                                onGoToSignUp()
                            },
                            shape = RoundedCornerShape(50),
                            colors = ButtonDefaults.buttonColors(containerColor = PayDzBluePrimary),
                            modifier = Modifier
                                .weight(1f)
                                .height(48.dp)
                        ) {
                            Text(
                                text = "إنشاء حساب",
                                fontFamily = CairoFontFamily,
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp
                            )
                        }

                        OutlinedButton(
                            onClick = {
                                onDismiss()
                                onGoToSignIn()
                            },
                            shape = RoundedCornerShape(50),
                            modifier = Modifier
                                .weight(1f)
                                .height(48.dp)
                        ) {
                            Text(
                                text = "تسجيل الدخول",
                                fontFamily = CairoFontFamily,
                                color = PayDzNavyText,
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp
                            )
                        }
                    }
                }
            }
        }
    }
}

/**
 * REAL HARDWARE BIOMETRIC FINGERPRINT SCANNER DIALOG
 * Automatically triggers the phone's real physical fingerprint sensor via `android.hardware.biometrics.BiometricPrompt`!
 * Supports fallback to secret App Passcode ("كود التطبيق / السر") as requested by the user.
 */
@Composable
fun PayDzBiometricScannerDialog(
    isEnrollmentMode: Boolean,
    accountName: String,
    accountEmail: String,
    onScanSuccess: () -> Unit,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    var isPinMode by rememberSaveable { mutableStateOf(false) }
    var enteredPin by rememberSaveable { mutableStateOf("") }
    var enrollmentPin by rememberSaveable { mutableStateOf(PayDzAuthManager.getAppSecurityPin(context)) }
    var showResetPinDialog by rememberSaveable { mutableStateOf(false) }
    var statusMessage by remember {
        mutableStateOf(
            if (isEnrollmentMode) {
                "ضع إصبعك على مستشعر البصمة الحقيقي في هاتفك لتسجيل بصمتك وربطها بهذا الحساب"
            } else {
                "ضع إصبعك على مستشعر البصمة الحقيقي في هاتفك لتسجيل الدخول الفوري"
            }
        )
    }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var scanCompleted by remember { mutableStateOf(false) }

    val infiniteTransition = rememberInfiniteTransition(label = "fingerprint_pulse")
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 0.96f,
        targetValue = 1.07f,
        animationSpec = infiniteRepeatable(
            animation = tween(900, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulseScale"
    )

    val triggerRealHardwareSensor = {
        errorMessage = null
        PayDzAuthManager.launchHardwareBiometricPrompt(
            context = context,
            title = if (isEnrollmentMode) "تسجيل بصمة الإصبع لحساب PayDz" else "الدخول بالبصمة إلى PayDz",
            subtitle = accountName.ifBlank { "مستخدم PayDz" },
            description = if (isEnrollmentMode) {
                "امسح بصمة إصبعك على مستشعر الهاتف لتفعيل الدخول السريع"
            } else {
                "امسح بصمة إصبعك على مستشعر الهاتف لفتح محفظتك الرقمية"
            },
            onSuccess = {
                scanCompleted = true
                if (isEnrollmentMode) {
                    PayDzAuthManager.setAppSecurityPin(context, enrollmentPin)
                }
                statusMessage = "تم التحقق من بصمة هاتفك الحقيقية بنجاح ✓"
                onScanSuccess()
            },
            onError = { err ->
                errorMessage = err
            }
        )
    }

    // Automatically launch the real Android OS hardware fingerprint prompt when opened in biometric mode
    LaunchedEffect(isPinMode) {
        if (!isPinMode && !scanCompleted) {
            delay(200L)
            triggerRealHardwareSensor()
        }
    }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
            Surface(
                shape = RoundedCornerShape(30.dp),
                color = Color.White,
                shadowElevation = 16.dp,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp)
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Security,
                            contentDescription = null,
                            tint = PayDzBluePrimary,
                            modifier = Modifier.size(22.dp)
                        )
                        Text(
                            text = if (isEnrollmentMode) {
                                "تفعيل البصمة وكود سر التطبيق"
                            } else if (isPinMode) {
                                "الدخول بكود سر التطبيق"
                            } else {
                                "الدخول عبر مستشعر بصمة الهاتف"
                            },
                            fontFamily = CairoFontFamily,
                            color = PayDzNavyText,
                            fontSize = 17.sp,
                            fontWeight = FontWeight.ExtraBold
                        )
                    }

                    // Target Account Chip
                    Surface(
                        shape = RoundedCornerShape(16.dp),
                        color = Color(0xFFF1F6FF),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = accountName.ifBlank { "مستخدم PayDz" },
                                fontFamily = CairoFontFamily,
                                color = PayDzNavyText,
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp
                            )
                            if (accountEmail.isNotBlank()) {
                                Text(
                                    text = accountEmail,
                                    fontFamily = PoppinsFontFamily,
                                    color = PayDzBluePrimary,
                                    fontSize = 12.sp
                                )
                            }
                        }
                    }

                    if (errorMessage != null) {
                        Surface(
                            color = CrimsonAlert.copy(alpha = 0.12f),
                            shape = RoundedCornerShape(14.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = errorMessage.orEmpty(),
                                fontFamily = CairoFontFamily,
                                color = CrimsonAlert,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                textAlign = TextAlign.Center,
                                modifier = Modifier.padding(10.dp)
                            )
                        }
                    }

                    if (isEnrollmentMode) {
                        // In enrollment mode: user sets or confirms their secret 4-digit App PIN fallback
                        Column(
                            modifier = Modifier.fillMaxWidth(),
                            verticalArrangement = Arrangement.spacedBy(8.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "عيّن كود سر التطبيق (4 أرقام) كبديل في حال تعذر قراءة البصمة:",
                                fontFamily = CairoFontFamily,
                                color = PayDzSlateText,
                                fontSize = 12.sp,
                                textAlign = TextAlign.Center
                            )

                            CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Ltr) {
                                BasicTextField(
                                    value = enrollmentPin,
                                    onValueChange = {
                                        val digits = it.filter { ch -> ch.isDigit() }.take(4)
                                        enrollmentPin = digits
                                    },
                                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword),
                                    modifier = Modifier.fillMaxWidth(),
                                    decorationBox = {
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.spacedBy(10.dp, Alignment.CenterHorizontally)
                                        ) {
                                            for (i in 0 until 4) {
                                                val char = enrollmentPin.getOrNull(i)?.toString() ?: ""
                                                val isFilled = char.isNotEmpty()
                                                Surface(
                                                    shape = RoundedCornerShape(12.dp),
                                                    color = if (isFilled) Color(0xFFEBF3FF) else Color(0xFFF8FAFC),
                                                    modifier = Modifier
                                                        .size(46.dp)
                                                        .border(
                                                            width = if (isFilled) 2.dp else 1.dp,
                                                            color = if (isFilled) PayDzBluePrimary else PayDzInputBorder,
                                                            shape = RoundedCornerShape(12.dp)
                                                        )
                                                ) {
                                                    Box(contentAlignment = Alignment.Center) {
                                                        Text(
                                                            text = if (isFilled) "●" else "",
                                                            fontSize = 18.sp,
                                                            color = PayDzBluePrimary,
                                                            fontWeight = FontWeight.Bold
                                                        )
                                                    }
                                                }
                                            }
                                        }
                                    }
                                )
                            }
                        }

                        // Fingerprint Sensor Trigger Pad
                        Box(
                            modifier = Modifier
                                .size(96.dp)
                                .scale(pulseScale)
                                .clip(CircleShape)
                                .background(if (scanCompleted) PayDzGreenSoft else Color(0xFFF1F6FF))
                                .border(
                                    width = 3.dp,
                                    color = if (scanCompleted) PayDzGreen else PayDzBluePrimary,
                                    shape = CircleShape
                                )
                                .clickable {
                                    triggerRealHardwareSensor()
                                }
                                .testTag("fingerprint_sensor_pad"),
                            contentAlignment = Alignment.Center
                        ) {
                            if (scanCompleted) {
                                Icon(
                                    imageVector = Icons.Default.CheckCircle,
                                    contentDescription = "تم التحقق",
                                    tint = PayDzGreen,
                                    modifier = Modifier.size(54.dp)
                                )
                            } else {
                                Image(
                                    painter = painterResource(id = R.drawable.canva_ic_fingerprint),
                                    contentDescription = "مسح البصمة",
                                    contentScale = ContentScale.Fit,
                                    modifier = Modifier.size(54.dp)
                                )
                            }
                        }

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Button(
                                onClick = {
                                    if (enrollmentPin.length < 4) {
                                        errorMessage = "يرجى تعيين كود سر التطبيق المكون من 4 أرقام أولاً"
                                    } else {
                                        PayDzAuthManager.setAppSecurityPin(context, enrollmentPin)
                                        triggerRealHardwareSensor()
                                    }
                                },
                                shape = RoundedCornerShape(50),
                                colors = ButtonDefaults.buttonColors(containerColor = PayDzBluePrimary),
                                modifier = Modifier
                                    .weight(1f)
                                    .height(48.dp)
                            ) {
                                Text(
                                    text = "تأكيد ومسح البصمة",
                                    fontFamily = CairoFontFamily,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.sp
                                )
                            }

                            OutlinedButton(
                                onClick = onDismiss,
                                shape = RoundedCornerShape(50),
                                modifier = Modifier.height(48.dp)
                            ) {
                                Text(
                                    text = "إلغاء",
                                    fontFamily = CairoFontFamily,
                                    color = PayDzNavyText,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.sp
                                )
                            }
                        }
                    } else if (isPinMode) {
                        // Fallback: PIN pad mode ("الدخول بكود التطبيق")
                        Column(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Text(
                                text = "أدخل كود سر التطبيق (4 أرقام) الذي عيّنته سابقاً:",
                                fontFamily = CairoFontFamily,
                                color = PayDzSlateText,
                                fontSize = 12.5.sp,
                                textAlign = TextAlign.Center
                            )

                            // 4 PIN Dots
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(14.dp),
                                modifier = Modifier.padding(vertical = 8.dp)
                            ) {
                                for (i in 0 until 4) {
                                    val isFilled = i < enteredPin.length
                                    Box(
                                        modifier = Modifier
                                            .size(18.dp)
                                            .clip(CircleShape)
                                            .background(if (isFilled) PayDzBluePrimary else Color(0xFFD1D5DB))
                                    )
                                }
                            }

                            // Numeric Keypad Grid
                            val digits = listOf(
                                listOf("1", "2", "3"),
                                listOf("4", "5", "6"),
                                listOf("7", "8", "9"),
                                listOf("cancel", "0", "delete")
                            )

                            digits.forEach { row ->
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(14.dp, Alignment.CenterHorizontally)
                                ) {
                                    row.forEach { key ->
                                        when (key) {
                                            "cancel" -> {
                                                Surface(
                                                    shape = CircleShape,
                                                    color = Color.Transparent,
                                                    modifier = Modifier
                                                        .size(54.dp)
                                                        .clickable {
                                                            isPinMode = false
                                                            enteredPin = ""
                                                            errorMessage = null
                                                        }
                                                ) {
                                                    Box(contentAlignment = Alignment.Center) {
                                                        Icon(
                                                            imageVector = Icons.Default.Fingerprint,
                                                            contentDescription = "العودة للبصمة",
                                                            tint = PayDzBluePrimary,
                                                            modifier = Modifier.size(26.dp)
                                                        )
                                                    }
                                                }
                                            }
                                            "delete" -> {
                                                Surface(
                                                    shape = CircleShape,
                                                    color = Color(0xFFF1F5F9),
                                                    modifier = Modifier
                                                        .size(54.dp)
                                                        .clickable {
                                                            if (enteredPin.isNotEmpty()) {
                                                                enteredPin = enteredPin.dropLast(1)
                                                                errorMessage = null
                                                            }
                                                        }
                                                ) {
                                                    Box(contentAlignment = Alignment.Center) {
                                                        Icon(
                                                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                                            contentDescription = "حذف",
                                                            tint = PayDzNavyText,
                                                            modifier = Modifier.size(20.dp)
                                                        )
                                                    }
                                                }
                                            }
                                            else -> {
                                                Surface(
                                                    shape = CircleShape,
                                                    color = Color(0xFFF8FAFC),
                                                    shadowElevation = 1.dp,
                                                    modifier = Modifier
                                                        .size(54.dp)
                                                        .clickable {
                                                            if (enteredPin.length < 4) {
                                                                val next = enteredPin + key
                                                                enteredPin = next
                                                                errorMessage = null
                                                                if (next.length == 4) {
                                                                    if (PayDzAuthManager.verifyAppSecurityPin(context, next)) {
                                                                        scanCompleted = true
                                                                        onScanSuccess()
                                                                    } else {
                                                                        errorMessage = "كود التطبيق غير صحيح! حاول مجدداً."
                                                                        enteredPin = ""
                                                                    }
                                                                }
                                                            }
                                                        }
                                                ) {
                                                    Box(contentAlignment = Alignment.Center) {
                                                        Text(
                                                            text = key,
                                                            fontFamily = PoppinsFontFamily,
                                                            fontWeight = FontWeight.Bold,
                                                            fontSize = 20.sp,
                                                            color = PayDzNavyText
                                                        )
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "العودة لمسح البصمة",
                                    fontFamily = CairoFontFamily,
                                    color = PayDzBluePrimary,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.sp,
                                    modifier = Modifier
                                        .clickable {
                                            isPinMode = false
                                            enteredPin = ""
                                            errorMessage = null
                                        }
                                        .padding(vertical = 4.dp)
                                )

                                Text(
                                    text = "نسيت كود التطبيق؟",
                                    fontFamily = CairoFontFamily,
                                    color = CrimsonAlert,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 12.5.sp,
                                    modifier = Modifier
                                        .clickable { showResetPinDialog = true }
                                        .padding(vertical = 4.dp)
                                )
                            }
                        }
                    } else {
                        // Standard Biometric prompt mode with fallback button
                        Text(
                            text = statusMessage,
                            fontFamily = CairoFontFamily,
                            color = if (scanCompleted) PayDzGreen else PayDzSlateText,
                            fontSize = 13.sp,
                            fontWeight = if (scanCompleted) FontWeight.Bold else FontWeight.Normal,
                            textAlign = TextAlign.Center
                        )

                        // Hardware Sensor Trigger Pad
                        Box(
                            modifier = Modifier
                                .size(112.dp)
                                .scale(pulseScale)
                                .clip(CircleShape)
                                .background(if (scanCompleted) PayDzGreenSoft else Color(0xFFF1F6FF))
                                .border(
                                    width = 3.dp,
                                    color = if (scanCompleted) PayDzGreen else PayDzBluePrimary,
                                    shape = CircleShape
                                )
                                .clickable {
                                    triggerRealHardwareSensor()
                                }
                                .testTag("fingerprint_sensor_pad"),
                            contentAlignment = Alignment.Center
                        ) {
                            if (scanCompleted) {
                                Icon(
                                    imageVector = Icons.Default.CheckCircle,
                                    contentDescription = "تم التحقق",
                                    tint = PayDzGreen,
                                    modifier = Modifier.size(64.dp)
                                )
                            } else {
                                Image(
                                    painter = painterResource(id = R.drawable.canva_ic_fingerprint),
                                    contentDescription = "مسح البصمة",
                                    contentScale = ContentScale.Fit,
                                    modifier = Modifier.size(64.dp)
                                )
                            }
                        }

                        // Button to switch to PIN passcode fallback
                        OutlinedButton(
                            onClick = {
                                isPinMode = true
                                enteredPin = ""
                                errorMessage = null
                            },
                            shape = RoundedCornerShape(50),
                            modifier = Modifier.fillMaxWidth().height(46.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Security,
                                contentDescription = null,
                                tint = PayDzBluePrimary,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(Modifier.size(8.dp))
                            Text(
                                text = "الدخول برمز الأمان (كود التطبيق / السر)",
                                fontFamily = CairoFontFamily,
                                color = PayDzNavyText,
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.5.sp
                            )
                        }

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Button(
                                onClick = { triggerRealHardwareSensor() },
                                shape = RoundedCornerShape(50),
                                colors = ButtonDefaults.buttonColors(containerColor = PayDzBluePrimary),
                                modifier = Modifier
                                    .weight(1f)
                                    .height(48.dp)
                                    .testTag("confirm_fingerprint_scan_button")
                            ) {
                                Text(
                                    text = "تشغيل مستشعر البصمة",
                                    fontFamily = CairoFontFamily,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.sp
                                )
                            }

                            OutlinedButton(
                                onClick = onDismiss,
                                shape = RoundedCornerShape(50),
                                modifier = Modifier.height(48.dp)
                            ) {
                                Text(
                                    text = "إلغاء",
                                    fontFamily = CairoFontFamily,
                                    color = PayDzNavyText,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.sp
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    if (showResetPinDialog) {
        PayDzResetPinDialog(
            accountEmail = accountEmail,
            accountName = accountName,
            onDismiss = { showResetPinDialog = false },
            onPinResetSuccess = {
                showResetPinDialog = false
                scanCompleted = true
                onScanSuccess()
            }
        )
    }
}

/**
 * FULL SCREEN BIOMETRIC & SECRET APP PIN LOCK SCREEN
 * Shown on app start whenever fingerprint is enrolled:
 * "اجعل البصمة عند إضافتها، يعني التطبيق لا يفتح إلا بالبصمة. وإذا لم تكن بصمتك، يمكنك الدخول بالكود، بالسر الذي وضعته في الأول. كود التطبيق يسموه."
 */
@Composable
fun PayDzBiometricLockScreen(
    accountName: String,
    accountEmail: String,
    onUnlockSuccess: () -> Unit,
    onSignOut: () -> Unit
) {
    val context = LocalContext.current
    var isPinMode by rememberSaveable { mutableStateOf(false) }
    var enteredPin by rememberSaveable { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var statusText by remember { mutableStateOf("المس مستشعر البصمة في هاتفك لفتح المحفظة") }
    var showResetPinDialog by rememberSaveable { mutableStateOf(false) }

    val infiniteTransition = rememberInfiniteTransition(label = "lock_fingerprint_pulse")
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 0.95f,
        targetValue = 1.08f,
        animationSpec = infiniteRepeatable(
            animation = tween(900, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "lockPulseScale"
    )

    val triggerHardwareSensor = {
        errorMessage = null
        PayDzAuthManager.launchHardwareBiometricPrompt(
            context = context,
            title = "فتح محفظة PayDz بالبصمة",
            subtitle = accountName.ifBlank { "مستخدم PayDz" },
            description = "امسح بصمة إصبعك لفتح المحفظة واستخدام حسابك بأمان",
            onSuccess = {
                statusText = "تم التحقق من البصمة بنجاح ✓"
                onUnlockSuccess()
            },
            onError = { err ->
                errorMessage = err
            }
        )
    }

    LaunchedEffect(isPinMode) {
        if (!isPinMode) {
            delay(300L)
            triggerHardwareSensor()
        }
    }

    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
        Scaffold(
            contentWindowInsets = WindowInsets.safeDrawing,
            containerColor = PayDzBgSoft
        ) { innerPadding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(PayDzBgSoft)
                    .padding(innerPadding)
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 24.dp, vertical = 20.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                // Top Brand Section
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    PayDzSpeedWalletLogo(
                        isDarkBackground = false,
                        iconSize = 92.dp,
                        titleFontSize = 32.sp
                    )

                    Surface(
                        shape = RoundedCornerShape(50),
                        color = Color(0xFFFEF3C7),
                        modifier = Modifier.padding(top = 4.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Shield,
                                contentDescription = null,
                                tint = Color(0xFFD97706),
                                modifier = Modifier.size(16.dp)
                            )
                            Text(
                                text = "التطبيق مقفل لحماية محفظتك الرقمية",
                                fontFamily = CairoFontFamily,
                                color = Color(0xFF92400E),
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp
                            )
                        }
                    }

                    Text(
                        text = "مرحباً بك، ${accountName.ifBlank { "مستخدم PayDz" }}",
                        fontFamily = CairoFontFamily,
                        color = PayDzNavyText,
                        fontSize = 19.sp,
                        fontWeight = FontWeight.ExtraBold,
                        textAlign = TextAlign.Center
                    )

                    if (accountEmail.isNotBlank()) {
                        Text(
                            text = accountEmail,
                            fontFamily = PoppinsFontFamily,
                            color = PayDzSlateText,
                            fontSize = 13.sp
                        )
                    }
                }

                // Center Unlock Pad (Biometric OR App Passcode PIN)
                Surface(
                    shape = RoundedCornerShape(26.dp),
                    color = Color.White,
                    shadowElevation = 6.dp,
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.5.dp, PayDzBluePrimary.copy(alpha = 0.2f), RoundedCornerShape(26.dp))
                ) {
                    Column(
                        modifier = Modifier.padding(20.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        if (errorMessage != null) {
                            Surface(
                                color = CrimsonAlert.copy(alpha = 0.12f),
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text(
                                    text = errorMessage.orEmpty(),
                                    fontFamily = CairoFontFamily,
                                    color = CrimsonAlert,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    textAlign = TextAlign.Center,
                                    modifier = Modifier.padding(8.dp)
                                )
                            }
                        }

                        if (!isPinMode) {
                            // Biometric Sensor Pad
                            Text(
                                text = statusText,
                                fontFamily = CairoFontFamily,
                                color = PayDzSlateText,
                                fontSize = 13.sp,
                                textAlign = TextAlign.Center
                            )

                            Box(
                                modifier = Modifier
                                    .size(118.dp)
                                    .scale(pulseScale)
                                    .clip(CircleShape)
                                    .background(Color(0xFFF1F6FF))
                                    .border(3.5.dp, PayDzBluePrimary, CircleShape)
                                    .clickable { triggerHardwareSensor() }
                                    .testTag("lock_screen_sensor_pad"),
                                contentAlignment = Alignment.Center
                            ) {
                                Image(
                                    painter = painterResource(id = R.drawable.canva_ic_fingerprint),
                                    contentDescription = "بصمة الإصبع",
                                    contentScale = ContentScale.Fit,
                                    modifier = Modifier.size(68.dp)
                                )
                            }

                            Button(
                                onClick = { triggerHardwareSensor() },
                                shape = RoundedCornerShape(50),
                                colors = ButtonDefaults.buttonColors(containerColor = PayDzBluePrimary),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(50.dp)
                            ) {
                                Text(
                                    text = "تشغيل مستشعر بصمة الهاتف",
                                    fontFamily = CairoFontFamily,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp
                                )
                            }

                            // Switch to App PIN code fallback
                            OutlinedButton(
                                onClick = {
                                    isPinMode = true
                                    enteredPin = ""
                                    errorMessage = null
                                },
                                shape = RoundedCornerShape(50),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(50.dp)
                                    .testTag("unlock_with_pin_button")
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Security,
                                    contentDescription = null,
                                    tint = PayDzBluePrimary,
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(Modifier.size(8.dp))
                                Text(
                                    text = "الدخول برمز الأمان (كود التطبيق / السر)",
                                    fontFamily = CairoFontFamily,
                                    color = PayDzNavyText,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.sp
                                )
                            }

                            Text(
                                text = "نسيت كود سر التطبيق؟ إعادة التعيين",
                                fontFamily = CairoFontFamily,
                                color = CrimsonAlert,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier
                                    .clickable { showResetPinDialog = true }
                                    .padding(top = 4.dp)
                                    .testTag("lock_forgot_pin_bio_button")
                            )
                        } else {
                            // App PIN Keypad Mode
                            Text(
                                text = "أدخل كود سر التطبيق (4 أرقام):",
                                fontFamily = CairoFontFamily,
                                color = PayDzNavyText,
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp,
                                textAlign = TextAlign.Center
                            )

                            // 4 PIN Dots
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(16.dp),
                                modifier = Modifier.padding(vertical = 4.dp)
                            ) {
                                for (i in 0 until 4) {
                                    val isFilled = i < enteredPin.length
                                    Box(
                                        modifier = Modifier
                                            .size(20.dp)
                                            .clip(CircleShape)
                                            .background(if (isFilled) PayDzBluePrimary else Color(0xFFD1D5DB))
                                    )
                                }
                            }

                            // Numeric Keypad Grid
                            val digits = listOf(
                                listOf("1", "2", "3"),
                                listOf("4", "5", "6"),
                                listOf("7", "8", "9"),
                                listOf("bio", "0", "del")
                            )

                            digits.forEach { row ->
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(16.dp, Alignment.CenterHorizontally)
                                ) {
                                    row.forEach { key ->
                                        when (key) {
                                            "bio" -> {
                                                Surface(
                                                    shape = CircleShape,
                                                    color = Color.Transparent,
                                                    modifier = Modifier
                                                        .size(56.dp)
                                                        .clickable {
                                                            isPinMode = false
                                                            enteredPin = ""
                                                            errorMessage = null
                                                        }
                                                ) {
                                                    Box(contentAlignment = Alignment.Center) {
                                                        Icon(
                                                            imageVector = Icons.Default.Fingerprint,
                                                            contentDescription = "العودة للبصمة",
                                                            tint = PayDzBluePrimary,
                                                            modifier = Modifier.size(28.dp)
                                                        )
                                                    }
                                                }
                                            }
                                            "del" -> {
                                                Surface(
                                                    shape = CircleShape,
                                                    color = Color(0xFFF1F5F9),
                                                    modifier = Modifier
                                                        .size(56.dp)
                                                        .clickable {
                                                            if (enteredPin.isNotEmpty()) {
                                                                enteredPin = enteredPin.dropLast(1)
                                                                errorMessage = null
                                                            }
                                                        }
                                                ) {
                                                    Box(contentAlignment = Alignment.Center) {
                                                        Icon(
                                                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                                            contentDescription = "حذف",
                                                            tint = PayDzNavyText,
                                                            modifier = Modifier.size(22.dp)
                                                        )
                                                    }
                                                }
                                            }
                                            else -> {
                                                Surface(
                                                    shape = CircleShape,
                                                    color = Color(0xFFF8FAFC),
                                                    shadowElevation = 1.dp,
                                                    modifier = Modifier
                                                        .size(56.dp)
                                                        .clickable {
                                                            if (enteredPin.length < 4) {
                                                                val next = enteredPin + key
                                                                enteredPin = next
                                                                errorMessage = null
                                                                if (next.length == 4) {
                                                                    if (PayDzAuthManager.verifyAppSecurityPin(context, next)) {
                                                                        onUnlockSuccess()
                                                                    } else {
                                                                        errorMessage = "كود سر التطبيق غير صحيح! حاول مجدداً."
                                                                        enteredPin = ""
                                                                    }
                                                                }
                                                            }
                                                        }
                                                ) {
                                                    Box(contentAlignment = Alignment.Center) {
                                                        Text(
                                                            text = key,
                                                            fontFamily = PoppinsFontFamily,
                                                            fontWeight = FontWeight.Bold,
                                                            fontSize = 22.sp,
                                                            color = PayDzNavyText
                                                        )
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "العودة لمسح البصمة",
                                    fontFamily = CairoFontFamily,
                                    color = PayDzBluePrimary,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.5.sp,
                                    modifier = Modifier
                                        .clickable {
                                            isPinMode = false
                                            enteredPin = ""
                                            errorMessage = null
                                        }
                                        .padding(vertical = 4.dp)
                                )

                                Text(
                                    text = "هل نسيت كود التطبيق؟",
                                    fontFamily = CairoFontFamily,
                                    color = CrimsonAlert,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.sp,
                                    modifier = Modifier
                                        .clickable { showResetPinDialog = true }
                                        .padding(vertical = 4.dp)
                                        .testTag("lock_forgot_pin_keypad_button")
                                )
                            }
                        }
                    }
                }

                // Bottom Sign Out Action
                Text(
                    text = "تبديل الحساب أو تسجيل الخروج من PayDz",
                    fontFamily = CairoFontFamily,
                    color = PayDzSlateText,
                    fontSize = 12.5.sp,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier
                        .clickable { onSignOut() }
                        .padding(top = 12.dp)
                )
            }
        }
    }

    if (showResetPinDialog) {
        PayDzResetPinDialog(
            accountEmail = accountEmail,
            accountName = accountName,
            onDismiss = { showResetPinDialog = false },
            onPinResetSuccess = {
                showResetPinDialog = false
                onUnlockSuccess()
            }
        )
    }
}

/**
 * DIALOG TO RESET FORGOTTEN APP SECURITY CODE ("كود سر التطبيق")
 * Verifies user identity via 6-digit OTP sent to their registered account email & phone notifications,
 * then allows setting and confirming a brand new 4-digit App PIN!
 */
@Composable
fun PayDzResetPinDialog(
    accountEmail: String,
    accountName: String,
    onDismiss: () -> Unit,
    onPinResetSuccess: (newPin: String) -> Unit
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    var currentStep by rememberSaveable { mutableIntStateOf(1) } // 1: Verify OTP, 2: Set New PIN
    var otpInput by rememberSaveable { mutableStateOf("") }
    var sentCode by rememberSaveable { mutableStateOf("") }
    var newPinInput by rememberSaveable { mutableStateOf("") }
    var confirmPinInput by rememberSaveable { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var infoMessage by remember { mutableStateOf<String?>(null) }

    val dispatchCode = {
        errorMessage = null
        val targetEmail = accountEmail.ifBlank { "islamboghazi2003@gmail.com" }
        val targetName = accountName.ifBlank { "مستخدم PayDz" }
        val code = PayDzAuthManager.startResetPinChallenge(
            context = context,
            scope = scope,
            email = targetEmail,
            fullName = targetName
        )
        sentCode = code
        infoMessage = "تم إرسال رمز الأمان المكون من 6 أرقام إلى بريدك ($targetEmail) وإشعار هاتفك."
    }

    LaunchedEffect(Unit) {
        dispatchCode()
    }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
            Surface(
                shape = RoundedCornerShape(28.dp),
                color = Color.White,
                shadowElevation = 14.dp,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp)
            ) {
                Column(
                    modifier = Modifier
                        .padding(24.dp)
                        .verticalScroll(rememberScrollState()),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(68.dp)
                            .clip(CircleShape)
                            .background(Color(0xFFEFF6FF)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Security,
                            contentDescription = null,
                            tint = PayDzBluePrimary,
                            modifier = Modifier.size(34.dp)
                        )
                    }

                    Text(
                        text = if (currentStep == 1) "إعادة تعيين كود سر التطبيق" else "تعيين كود سر جديد",
                        fontFamily = CairoFontFamily,
                        color = PayDzNavyText,
                        fontSize = 18.5.sp,
                        fontWeight = FontWeight.ExtraBold,
                        textAlign = TextAlign.Center
                    )

                    if (errorMessage != null) {
                        Surface(
                            color = CrimsonAlert.copy(alpha = 0.12f),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = errorMessage.orEmpty(),
                                fontFamily = CairoFontFamily,
                                color = CrimsonAlert,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                textAlign = TextAlign.Center,
                                modifier = Modifier.padding(10.dp)
                            )
                        }
                    }

                    if (infoMessage != null) {
                        Surface(
                            color = EmeraldTrust.copy(alpha = 0.12f),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = infoMessage.orEmpty(),
                                fontFamily = CairoFontFamily,
                                color = EmeraldTrust,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                textAlign = TextAlign.Center,
                                modifier = Modifier.padding(10.dp)
                            )
                        }
                    }

                    if (currentStep == 1) {
                        // Step 1: Verify 6-digit OTP code sent to user email / notification
                        Text(
                            text = "لتأكيد هويتك وحماية أموالك، أدخل رمز الأمان المكون من 6 أرقام المرسل إلى بريدك وإشعار هاتفك:",
                            fontFamily = CairoFontFamily,
                            color = PayDzSlateText,
                            fontSize = 12.5.sp,
                            lineHeight = 18.sp,
                            textAlign = TextAlign.Center
                        )

                        // Code display & Autofill button
                        if (sentCode.isNotBlank()) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(Color(0xFFF1F6FF))
                                    .padding(horizontal = 12.dp, vertical = 8.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "رمز الأمان: $sentCode",
                                    fontFamily = PoppinsFontFamily,
                                    color = PayDzBluePrimary,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 15.sp
                                )
                                Surface(
                                    shape = RoundedCornerShape(50),
                                    color = PayDzBluePrimary,
                                    modifier = Modifier.clickable {
                                        otpInput = sentCode
                                        errorMessage = null
                                    }
                                ) {
                                    Text(
                                        text = "نسخ وتعبئة",
                                        fontFamily = CairoFontFamily,
                                        color = Color.White,
                                        fontSize = 11.5.sp,
                                        fontWeight = FontWeight.Bold,
                                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                                    )
                                }
                            }
                        }

                        // 6-digit OTP Input Boxes
                        CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Ltr) {
                            BasicTextField(
                                value = otpInput,
                                onValueChange = {
                                    val digits = it.filter { ch -> ch.isDigit() }.take(6)
                                    otpInput = digits
                                    errorMessage = null
                                },
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword),
                                modifier = Modifier.fillMaxWidth(),
                                decorationBox = {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.spacedBy(6.dp, Alignment.CenterHorizontally)
                                    ) {
                                        for (i in 0 until 6) {
                                            val char = otpInput.getOrNull(i)?.toString() ?: ""
                                            val isFilled = char.isNotEmpty()
                                            Surface(
                                                shape = RoundedCornerShape(10.dp),
                                                color = if (isFilled) Color(0xFFEBF3FF) else Color(0xFFF8FAFC),
                                                modifier = Modifier
                                                    .size(44.dp)
                                                    .border(
                                                        width = if (isFilled) 2.dp else 1.dp,
                                                        color = if (isFilled) PayDzBluePrimary else PayDzInputBorder,
                                                        shape = RoundedCornerShape(10.dp)
                                                    )
                                            ) {
                                                Box(contentAlignment = Alignment.Center) {
                                                    Text(
                                                        text = char,
                                                        fontFamily = PoppinsFontFamily,
                                                        color = PayDzNavyText,
                                                        fontSize = 18.sp,
                                                        fontWeight = FontWeight.Bold
                                                    )
                                                }
                                            }
                                        }
                                    }
                                }
                            )
                        }

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Button(
                                onClick = {
                                    if (otpInput.length < 6) {
                                        errorMessage = "يرجى إدخال الرمز المكون من 6 أرقام بالكامل"
                                    } else if (PayDzAuthManager.verifyResetPinOtp(context, otpInput) || otpInput == sentCode) {
                                        currentStep = 2
                                        errorMessage = null
                                        infoMessage = "تم تأكيد هويتك بنجاح ✓ يمكنك الآن كتابة كود السر الجديد."
                                    } else {
                                        errorMessage = "رمز التحقق غير صحيح! يرجى التأكد من الرمز."
                                    }
                                },
                                shape = RoundedCornerShape(50),
                                colors = ButtonDefaults.buttonColors(containerColor = PayDzBluePrimary),
                                modifier = Modifier
                                    .weight(1f)
                                    .height(48.dp)
                            ) {
                                Text(
                                    text = "تأكيد الرمز والمتابعة",
                                    fontFamily = CairoFontFamily,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.sp
                                )
                            }

                            OutlinedButton(
                                onClick = onDismiss,
                                shape = RoundedCornerShape(50),
                                modifier = Modifier.height(48.dp)
                            ) {
                                Text(
                                    text = "إلغاء",
                                    fontFamily = CairoFontFamily,
                                    color = PayDzNavyText,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.sp
                                )
                            }
                        }

                        Text(
                            text = "إعادة إرسال رمز الأمان مرة أخرى",
                            fontFamily = CairoFontFamily,
                            color = PayDzBluePrimary,
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp,
                            modifier = Modifier
                                .clickable { dispatchCode() }
                                .padding(vertical = 4.dp)
                        )
                    } else {
                        // Step 2: Set and Confirm New 4-digit App PIN
                        Text(
                            text = "أدخل كود سر التطبيق الجديد (4 أرقام):",
                            fontFamily = CairoFontFamily,
                            color = PayDzSlateText,
                            fontSize = 12.5.sp,
                            textAlign = TextAlign.Center
                        )

                        Column(
                            modifier = Modifier.fillMaxWidth(),
                            verticalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Text(
                                text = "الكود الجديد (4 أرقام):",
                                fontFamily = CairoFontFamily,
                                color = PayDzNavyText,
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.5.sp
                            )
                            CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Ltr) {
                                BasicTextField(
                                    value = newPinInput,
                                    onValueChange = {
                                        val digits = it.filter { ch -> ch.isDigit() }.take(4)
                                        newPinInput = digits
                                        errorMessage = null
                                    },
                                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword),
                                    modifier = Modifier.fillMaxWidth(),
                                    decorationBox = {
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.spacedBy(10.dp, Alignment.CenterHorizontally)
                                        ) {
                                            for (i in 0 until 4) {
                                                val char = newPinInput.getOrNull(i)?.toString() ?: ""
                                                val isFilled = char.isNotEmpty()
                                                Surface(
                                                    shape = RoundedCornerShape(12.dp),
                                                    color = if (isFilled) Color(0xFFEBF3FF) else Color(0xFFF8FAFC),
                                                    modifier = Modifier
                                                        .size(46.dp)
                                                        .border(
                                                            width = if (isFilled) 2.dp else 1.dp,
                                                            color = if (isFilled) PayDzBluePrimary else PayDzInputBorder,
                                                            shape = RoundedCornerShape(12.dp)
                                                        )
                                                ) {
                                                    Box(contentAlignment = Alignment.Center) {
                                                        Text(
                                                            text = if (isFilled) "●" else "",
                                                            fontSize = 18.sp,
                                                            color = PayDzBluePrimary,
                                                            fontWeight = FontWeight.Bold
                                                        )
                                                    }
                                                }
                                            }
                                        }
                                    }
                                )
                            }

                            Text(
                                text = "تأكيد الكود الجديد (4 أرقام):",
                                fontFamily = CairoFontFamily,
                                color = PayDzNavyText,
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.5.sp
                            )
                            CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Ltr) {
                                BasicTextField(
                                    value = confirmPinInput,
                                    onValueChange = {
                                        val digits = it.filter { ch -> ch.isDigit() }.take(4)
                                        confirmPinInput = digits
                                        errorMessage = null
                                    },
                                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword),
                                    modifier = Modifier.fillMaxWidth(),
                                    decorationBox = {
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.spacedBy(10.dp, Alignment.CenterHorizontally)
                                        ) {
                                            for (i in 0 until 4) {
                                                val char = confirmPinInput.getOrNull(i)?.toString() ?: ""
                                                val isFilled = char.isNotEmpty()
                                                Surface(
                                                    shape = RoundedCornerShape(12.dp),
                                                    color = if (isFilled) Color(0xFFEBF3FF) else Color(0xFFF8FAFC),
                                                    modifier = Modifier
                                                        .size(46.dp)
                                                        .border(
                                                            width = if (isFilled) 2.dp else 1.dp,
                                                            color = if (isFilled) PayDzBluePrimary else PayDzInputBorder,
                                                            shape = RoundedCornerShape(12.dp)
                                                        )
                                                ) {
                                                    Box(contentAlignment = Alignment.Center) {
                                                        Text(
                                                            text = if (isFilled) "●" else "",
                                                            fontSize = 18.sp,
                                                            color = PayDzBluePrimary,
                                                            fontWeight = FontWeight.Bold
                                                        )
                                                    }
                                                }
                                            }
                                        }
                                    }
                                )
                            }
                        }

                        Button(
                            onClick = {
                                if (newPinInput.length != 4) {
                                    errorMessage = "يجب أن يتكون الكود الجديد من 4 أرقام"
                                } else if (newPinInput != confirmPinInput) {
                                    errorMessage = "الكودان غير متطابقين! يرجى التأكد منهما"
                                } else {
                                    PayDzAuthManager.setAppSecurityPin(context, newPinInput)
                                    PayDzNotificationHelper.showSystemNotification(
                                        context = context,
                                        title = "تم تعيين كود سر التطبيق الجديد بنجاح ✓",
                                        body = "تم تحديث كود أمان تطبيق PayDz الخاص بك. كودك الجديد هو: $newPinInput"
                                    )
                                    onPinResetSuccess(newPinInput)
                                }
                            },
                            shape = RoundedCornerShape(50),
                            colors = ButtonDefaults.buttonColors(containerColor = PayDzBluePrimary),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(50.dp)
                        ) {
                            Text(
                                text = "حفظ الكود الجديد وفتح التطبيق",
                                fontFamily = CairoFontFamily,
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp
                            )
                        }
                    }
                }
            }
        }
    }
}

package com.example

import android.Manifest
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Apps
import androidx.compose.material.icons.filled.CheckBox
import androidx.compose.material.icons.filled.CheckBoxOutlineBlank
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Fingerprint
import androidx.compose.material.icons.filled.GridView
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material.icons.filled.PhoneAndroid
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.credentials.CredentialManager
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.auth.PayDzAuthManager
import com.example.auth.authStateFlow
import com.example.auth.onGoogleSignInClicked
import com.example.auth.signOut
import com.example.data.repository.DinarPayRepository
import com.example.ui.AccountProfileDialog
import com.example.ui.ActiveModal
import com.example.ui.AddBeneficiaryDialog
import com.example.ui.AllServicesScreen
import com.example.ui.AppLanguage
import com.example.ui.BillPaymentDialog
import com.example.ui.CardlessAtmDialog
import com.example.ui.DigitalReceiptDialog
import com.example.ui.DinarPayViewModel
import com.example.ui.HomeDashboardScreen
import com.example.ui.MobileTopUpDialog
import com.example.ui.MoreHubScreen
import com.example.ui.NewSupportTicketDialog
import com.example.ui.NotificationsCenterDialog
import com.example.ui.OperationSuccessFullScreenDialog
import com.example.ui.PayDzBiometricLockScreen
import com.example.ui.PayDzBiometricNotEnrolledDialog
import com.example.ui.PayDzBiometricScannerDialog
import com.example.ui.PayDzDesignerTrustValuesBar
import com.example.ui.PayDzEmailOtpVerificationScreen
import com.example.ui.GoogleColorGLogo
import com.example.ui.PayDzGoogleAuthButton
import com.example.ui.PayDzSpeedWalletLogo
import com.example.ui.QrPaymentDialog
import com.example.ui.RipTransferDialog
import com.example.ui.SandboxRechargeDialog
import com.example.ui.TransactionsHistoryScreen
import com.example.ui.UiState
import com.example.ui.theme.ApplicationTheme
import com.example.ui.theme.CairoFontFamily
import com.example.ui.theme.CrimsonAlert
import com.example.ui.theme.EmeraldTrust
import com.example.ui.theme.PayDzBgSoft
import com.example.ui.theme.PayDzBlueBright
import com.example.ui.theme.PayDzBlueDark
import com.example.ui.theme.PayDzBluePrimary
import com.example.ui.theme.PayDzGreen
import com.example.ui.theme.PayDzGreenSoft
import com.example.ui.theme.PayDzInputBorder
import com.example.ui.theme.PayDzNavActiveBlue
import com.example.ui.theme.PayDzNavyText
import com.example.ui.theme.PayDzSlateText
import com.example.util.PayDzNotificationHelper
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.delay

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        PayDzAuthManager.init(applicationContext)
        enableEdgeToEdge()
        setContent {
            ApplicationTheme {
                AppNavigation()
            }
        }
    }
}

@Composable
fun AppNavigation(auth: FirebaseAuth = Firebase.auth) {
    val context = LocalContext.current
    LaunchedEffect(Unit) {
        PayDzAuthManager.init(context)
    }

    val currentUser by auth.authStateFlow().collectAsStateWithLifecycle(initialValue = auth.currentUser)
    val sessionUnlocked by PayDzAuthManager.sessionUnlockedFlow.collectAsStateWithLifecycle()
    val isBioEnrolled = PayDzAuthManager.isFingerprintEnrolled(context)
    var splashStage by rememberSaveable { mutableIntStateOf(0) } // 0 = Logo Splash, 1 = Maqam Echahid Splash, 2 = Main App

    LaunchedEffect(Unit) {
        delay(1200L)
        splashStage = 1
        delay(1600L)
        splashStage = 2
    }

    if (splashStage < 2) {
        if (splashStage == 0) {
            PayDzInitialLogoSplashScreen(onDismiss = { splashStage = 1 })
        } else {
            PayDzSplashScreen(onDismissSplash = { splashStage = 2 })
        }
    } else {
        val user = currentUser
        if (user == null) {
            AuthGateScreen(onReplaySplash = { splashStage = 0 })
        } else if (isBioEnrolled && !sessionUnlocked) {
            // Once biometric fingerprint is enrolled, THE APP DOES NOT OPEN EXCEPT WITH BIOMETRIC OR SECRET APP PIN!
            val activeName = PayDzAuthManager.getActiveNameOverride(context)
                ?: user.displayName
                ?: "مستخدم PayDz"
            val activeEmail = PayDzAuthManager.getActiveEmailOverride(context)
                ?: user.email
                ?: ""
            PayDzBiometricLockScreen(
                accountName = activeName,
                accountEmail = activeEmail,
                onUnlockSuccess = {
                    PayDzAuthManager.setSessionUnlocked(context, true)
                },
                onSignOut = {
                    auth.signOut()
                    PayDzAuthManager.setSessionUnlocked(context, false)
                }
            )
        } else if (!sessionUnlocked) {
            AuthGateScreen(onReplaySplash = { splashStage = 0 })
        } else {
            val activeName = PayDzAuthManager.getActiveNameOverride(context)
                ?: user.displayName
                ?: "مستخدم PayDz"
            val activeEmail = PayDzAuthManager.getActiveEmailOverride(context)
                ?: user.email
                ?: "0612345678"
            key(user.uid, activeName, activeEmail) {
                DinarPayAuthenticatedApp(
                    currentUserId = user.uid,
                    userDisplayName = activeName,
                    userEmail = activeEmail
                )
            }
        }
    }
}

/**
 * SCREEN 0 OF PAYDZ MOCKUP: شاشة الشعار الأولى (Initial Logo & Branding Splash Screen)
 * Displays large white PayDz logo on deep royal blue background + animated spinner before Maqam Echahid splash screen
 */
@Composable
fun PayDzInitialLogoSplashScreen(onDismiss: () -> Unit) {
    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        listOf(
                            PayDzBlueDark,
                            PayDzBluePrimary,
                            Color(0xFF002F87)
                        )
                    )
                )
                .clickable { onDismiss() }
                .testTag("paydz_initial_logo_splash"),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(24.dp)
            ) {
                PayDzSpeedWalletLogo(
                    isDarkBackground = true,
                    iconSize = 135.dp
                )

                Image(
                    painter = painterResource(id = R.drawable.canva_splash_spinner),
                    contentDescription = "Loading Spinner",
                    contentScale = ContentScale.Fit,
                    modifier = Modifier.size(38.dp)
                )
            }
        }
    }
}

/**
 * SCREEN 1 OF PAYDZ MOCKUP: شاشة البداية الاحترافية (World-Class FinTech Splash Screen)
 * HD Maqam Echahid Background + Centered White Hero Showcase with Large PayDz Logo & Name +
 * Single-Line Designer Trust Strip: "الموثوقية • الصدق • الراحة • الأمان"
 */
@Composable
fun PayDzSplashScreen(onDismissSplash: () -> Unit) {
    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
        BoxWithConstraints(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        listOf(
                            PayDzBlueDark,
                            PayDzBluePrimary,
                            PayDzBlueBright
                        )
                    )
                )
                .clickable { onDismissSplash() }
                .testTag("paydz_splash_screen")
        ) {
            val logoTopOffset = maxHeight * 0.165f
            val logoIconHeight = (maxWidth * 0.364f * 0.783f).coerceAtLeast(108.dp)

            // HD Splash Background Layer (Algiers Maqam Echahid + Curved Wave)
            Image(
                painter = painterResource(id = R.drawable.canva_splash_bg),
                contentDescription = "PayDz Algiers Cityscape Background",
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(top = logoTopOffset, bottom = 36.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                // Top HD Logo + Tagline at exact coordinates
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier.padding(horizontal = 24.dp)
                ) {
                    PayDzSpeedWalletLogo(
                        isDarkBackground = true,
                        iconSize = logoIconHeight
                    )
                    Text(
                        text = "كل خدماتك المالية\nفي مكان واحد",
                        fontFamily = CairoFontFamily,
                        color = Color.White,
                        fontSize = 14.sp,
                        lineHeight = 21.sp,
                        fontWeight = FontWeight.Normal,
                        textAlign = TextAlign.Center
                    )
                }

                // Single-Line Designer Trust Strip: "الموثوقية • الصدق • الراحة • الأمان"
                PayDzDesignerTrustValuesBar(
                    isOnWhiteCard = false,
                    modifier = Modifier.padding(horizontal = 24.dp)
                )
            }
        }
    }
}

/**
 * SCREEN 2 OF PAYDZ MOCKUP: شاشة تسجيل الدخول وإنشاء الحساب
 * - التسجيل والدخول حصرياً وفورياً عبر حساب Google (بدون الحاجة لكلمات مرور أو بريد يدوي أو رموز)
 * - مسح البصمة الحقيقي للهاتف للدخول السريع (بعد إضافة الحساب)
 */
@Composable
fun AuthGateScreen(onReplaySplash: () -> Unit = {}) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val credentialManager = remember { CredentialManager.create(context) }

    val enrolledBiometric by PayDzAuthManager.biometricEnrolledFlow.collectAsStateWithLifecycle()

    var isSignUpMode by rememberSaveable { mutableStateOf(false) }
    var isLoading by remember { mutableStateOf(false) }
    var authError by remember { mutableStateOf<String?>(null) }

    // Biometric dialogs
    var showBiometricNotEnrolledDialog by remember { mutableStateOf(false) }
    var showBiometricLoginScannerDialog by remember { mutableStateOf(false) }

    val triggerGoogleAuth = { forceAccountPicker: Boolean ->
        if (!isLoading) {
            isLoading = true
            authError = null
            onGoogleSignInClicked(
                context = context,
                credentialManager = credentialManager,
                forceAccountPicker = forceAccountPicker,
                onAuthSuccess = {
                    isLoading = false
                    val fbUser = Firebase.auth.currentUser
                    val resolvedEmail = fbUser?.email?.takeIf { it.isNotBlank() } ?: "user@gmail.com"
                    val resolvedName = fbUser?.displayName?.takeIf { it.isNotBlank() } ?: "مستخدم PayDz"
                    PayDzAuthManager.registerOrUpdateGoogleAccountLocally(
                        context = context,
                        email = resolvedEmail,
                        fullName = resolvedName,
                        phoneNumber = "0612345678"
                    )
                    PayDzAuthManager.setSessionUnlocked(context, true)
                },
                onAuthError = { msg ->
                    isLoading = false
                    authError = msg
                },
                scope = scope,
                onAuthCancelled = { isLoading = false }
            )
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
                    .padding(horizontal = 24.dp, vertical = 16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Top
            ) {
                // Top spacing so the logo is positioned lower and well-balanced on the screen
                Spacer(modifier = Modifier.height(28.dp))

                // Centered Brand Identity: Logo on top, with text right above the tabs
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(6.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    PayDzSpeedWalletLogo(
                        isDarkBackground = false,
                        iconSize = 106.dp,
                        titleFontSize = 35.sp,
                        modifier = Modifier.clickable { onReplaySplash() }
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        text = if (isSignUpMode) "إنشاء حساب جديد في PayDz" else "مرحباً بك في PayDz",
                        fontFamily = CairoFontFamily,
                        color = PayDzNavyText,
                        fontSize = 19.5.sp,
                        fontWeight = FontWeight.ExtraBold,
                        textAlign = TextAlign.Center
                    )

                    Text(
                        text = if (isSignUpMode) {
                            "أنشئ حسابك فوراً وبأمان بلمسة واحدة باستخدام Google"
                        } else {
                            "سجل دخولك السريع والمباشر عبر حساب Google أو البصمة"
                        },
                        fontFamily = CairoFontFamily,
                        color = PayDzSlateText,
                        fontSize = 12.5.sp,
                        fontWeight = FontWeight.Normal,
                        textAlign = TextAlign.Center
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Mode Switcher Tabs: تسجيل الدخول | إنشاء حساب جديد
                Surface(
                    shape = RoundedCornerShape(50),
                    color = Color(0xFFE6EEF8),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .padding(2.dp)
                ) {
                    Row(modifier = Modifier.fillMaxSize()) {
                        Surface(
                            shape = RoundedCornerShape(50),
                            color = if (!isSignUpMode) PayDzBluePrimary else Color.Transparent,
                            shadowElevation = if (!isSignUpMode) 3.dp else 0.dp,
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxSize()
                                .clickable {
                                    isSignUpMode = false
                                    authError = null
                                }
                                .testTag("tab_sign_in_mode")
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Text(
                                    text = "تسجيل الدخول",
                                    fontFamily = CairoFontFamily,
                                    color = if (!isSignUpMode) Color.White else PayDzNavyText,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.5.sp
                                )
                            }
                        }

                        Surface(
                            shape = RoundedCornerShape(50),
                            color = if (isSignUpMode) PayDzBluePrimary else Color.Transparent,
                            shadowElevation = if (isSignUpMode) 3.dp else 0.dp,
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxSize()
                                .clickable {
                                    isSignUpMode = true
                                    authError = null
                                }
                                .testTag("tab_sign_up_mode")
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Text(
                                    text = "إنشاء حساب جديد",
                                    fontFamily = CairoFontFamily,
                                    color = if (isSignUpMode) Color.White else PayDzNavyText,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.5.sp
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Main Action Container (Google Auth + Biometrics)
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    if (authError != null) {
                        AuthErrorBanner(message = authError.orEmpty())
                    }

                    // Primary Google Button (Sign-In or Sign-Up)
                    Button(
                        onClick = { triggerGoogleAuth(isSignUpMode) },
                        enabled = !isLoading,
                        shape = RoundedCornerShape(50),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = PayDzBluePrimary,
                            contentColor = Color.White
                        ),
                        elevation = ButtonDefaults.buttonElevation(defaultElevation = 6.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp)
                            .testTag("google_primary_auth_button")
                    ) {
                        if (isLoading) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(24.dp),
                                color = Color.White,
                                strokeWidth = 2.5.dp
                            )
                        } else {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.Center
                            ) {
                                Surface(
                                    shape = CircleShape,
                                    color = Color.White,
                                    modifier = Modifier.size(28.dp)
                                ) {
                                    Box(contentAlignment = Alignment.Center) {
                                        GoogleColorGLogo(modifier = Modifier.size(18.dp))
                                    }
                                }
                                Spacer(modifier = Modifier.width(12.dp))
                                Text(
                                    text = if (isSignUpMode) "إنشاء حساب جديد باستخدام Google" else "تسجيل الدخول باستخدام Google",
                                    fontFamily = CairoFontFamily,
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }

                    // Divider: ─── أو الدخول بالبصمة ───
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        HorizontalDivider(modifier = Modifier.weight(1f), color = PayDzInputBorder)
                        Text(
                            text = "أو الدخول البيومتري",
                            fontFamily = CairoFontFamily,
                            color = PayDzSlateText,
                            fontWeight = FontWeight.Normal,
                            fontSize = 12.sp
                        )
                        HorizontalDivider(modifier = Modifier.weight(1f), color = PayDzInputBorder)
                    }

                    // Biometric Fingerprint Button (only active after account enrollment)
                    val bioAccount = enrolledBiometric
                    val isBioReady = bioAccount != null
                    OutlinedButton(
                        onClick = {
                            if (!isBioReady) {
                                showBiometricNotEnrolledDialog = true
                            } else {
                                showBiometricLoginScannerDialog = true
                            }
                        },
                        enabled = !isLoading,
                        shape = RoundedCornerShape(50),
                        colors = ButtonDefaults.outlinedButtonColors(
                            containerColor = if (isBioReady) Color.White else Color(0xFFF1F5F9),
                            contentColor = if (isBioReady) PayDzNavyText else PayDzSlateText
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp)
                            .testTag("biometric_sign_in_button")
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Image(
                                painter = painterResource(id = R.drawable.canva_ic_fingerprint),
                                contentDescription = null,
                                contentScale = ContentScale.Fit,
                                alpha = if (isBioReady) 1f else 0.45f,
                                modifier = Modifier.size(28.dp)
                            )
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(
                                    text = if (isBioReady) {
                                        "الدخول ببصمة الهاتف (${bioAccount?.fullName})"
                                    } else {
                                        "الدخول بالبصمة"
                                    },
                                    fontFamily = CairoFontFamily,
                                    color = if (isBioReady) PayDzNavyText else PayDzSlateText,
                                    fontSize = 14.5.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = if (isBioReady) {
                                        "مفعّلة • المس المستشعر للدخول الفوري"
                                    } else {
                                        "تُفعّل تلقائياً بعد إضافة الحساب من الداخل"
                                    },
                                    fontFamily = CairoFontFamily,
                                    color = if (isBioReady) PayDzGreen else PayDzSlateText.copy(alpha = 0.8f),
                                    fontSize = 10.5.sp,
                                    fontWeight = if (isBioReady) FontWeight.Bold else FontWeight.Normal
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Bottom link toggling between Sign-In and Sign-Up
                Text(
                    text = buildAnnotatedString {
                        withStyle(
                            SpanStyle(
                                fontFamily = CairoFontFamily,
                                color = PayDzSlateText,
                                fontWeight = FontWeight.Normal,
                                fontSize = 12.5.sp
                            )
                        ) {
                            append(if (isSignUpMode) "لديك حساب بالفعل؟ " else "ليس لديك حساب؟ ")
                        }
                        withStyle(
                            SpanStyle(
                                fontFamily = CairoFontFamily,
                                color = PayDzBluePrimary,
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp
                            )
                        ) {
                            append(if (isSignUpMode) "تسجيل الدخول" else "إنشاء حساب جديد")
                        }
                    },
                    fontSize = 13.sp,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .clickable {
                            isSignUpMode = !isSignUpMode
                            authError = null
                        }
                        .padding(vertical = 10.dp)
                        .testTag("toggle_auth_mode_bottom_link")
                )
            }
        }
    }

    // Dialog 1: Fingerprint Not Enrolled Yet (First launch before adding account)
    if (showBiometricNotEnrolledDialog) {
        PayDzBiometricNotEnrolledDialog(
            onDismiss = { showBiometricNotEnrolledDialog = false },
            onGoToSignIn = { isSignUpMode = false },
            onGoToSignUp = { isSignUpMode = true }
        )
    }

    // Dialog 2: Fingerprint Login Scanner (Only accessible AFTER an account has enrolled its fingerprint)
    val enrolledAcc = enrolledBiometric
    if (showBiometricLoginScannerDialog && enrolledAcc != null) {
        PayDzBiometricScannerDialog(
            isEnrollmentMode = false,
            accountName = enrolledAcc.fullName,
            accountEmail = enrolledAcc.email,
            onDismiss = { showBiometricLoginScannerDialog = false },
            onScanSuccess = {
                showBiometricLoginScannerDialog = false
                PayDzAuthManager.saveActiveProfileOverride(
                    context = context,
                    email = enrolledAcc.email,
                    fullName = enrolledAcc.fullName,
                    phoneNumber = PayDzAuthManager.getActivePhoneOverride(context) ?: "0612345678"
                )
                if (Firebase.auth.currentUser != null) {
                    PayDzAuthManager.setSessionUnlocked(context, true)
                } else {
                    isLoading = true
                    onGoogleSignInClicked(
                        context = context,
                        credentialManager = credentialManager,
                        forceAccountPicker = false,
                        onAuthSuccess = {
                            isLoading = false
                            PayDzAuthManager.setSessionUnlocked(context, true)
                        },
                        onAuthError = { msg ->
                            isLoading = false
                            authError = msg
                        },
                        scope = scope,
                        onAuthCancelled = { isLoading = false }
                    )
                }
            }
        )
    }
}

@Composable
private fun PayDzRoundedAuthField(
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    leadingIcon: ImageVector,
    testTag: String
) {
    Surface(
        shape = RoundedCornerShape(50),
        color = Color.White,
        shadowElevation = 2.dp,
        modifier = Modifier
            .fillMaxWidth()
            .height(54.dp)
            .border(1.dp, PayDzInputBorder, RoundedCornerShape(50))
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
                imageVector = leadingIcon,
                contentDescription = null,
                tint = PayDzSlateText,
                modifier = Modifier.size(20.dp)
            )
            Box(
                modifier = Modifier.weight(1f),
                contentAlignment = Alignment.CenterStart
            ) {
                if (value.isEmpty()) {
                    Text(
                        text = placeholder,
                        fontFamily = CairoFontFamily,
                        color = PayDzSlateText,
                        fontWeight = FontWeight.Normal,
                        fontSize = 12.sp
                    )
                }
                BasicTextField(
                    value = value,
                    onValueChange = onValueChange,
                    singleLine = true,
                    textStyle = TextStyle(
                        fontFamily = CairoFontFamily,
                        color = PayDzNavyText,
                        fontSize = 13.5.sp,
                        fontWeight = FontWeight.Bold
                    ),
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}

@Composable
private fun PayDzRoundedPasswordField(
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    passwordVisible: Boolean,
    onToggleVisibility: () -> Unit,
    testTag: String
) {
    Surface(
        shape = RoundedCornerShape(50),
        color = Color.White,
        shadowElevation = 2.dp,
        modifier = Modifier
            .fillMaxWidth()
            .height(54.dp)
            .border(1.dp, PayDzInputBorder, RoundedCornerShape(50))
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
                imageVector = Icons.Default.Lock,
                contentDescription = null,
                tint = PayDzSlateText,
                modifier = Modifier.size(20.dp)
            )
            Box(
                modifier = Modifier.weight(1f),
                contentAlignment = Alignment.CenterStart
            ) {
                if (value.isEmpty()) {
                    Text(
                        text = placeholder,
                        fontFamily = CairoFontFamily,
                        color = PayDzSlateText,
                        fontWeight = FontWeight.Normal,
                        fontSize = 12.sp
                    )
                }
                BasicTextField(
                    value = value,
                    onValueChange = onValueChange,
                    singleLine = true,
                    visualTransformation = if (passwordVisible) {
                        VisualTransformation.None
                    } else {
                        PasswordVisualTransformation()
                    },
                    textStyle = TextStyle(
                        fontFamily = CairoFontFamily,
                        color = PayDzNavyText,
                        fontSize = 13.5.sp,
                        fontWeight = FontWeight.Bold
                    ),
                    modifier = Modifier.fillMaxWidth()
                )
            }
            IconButton(
                onClick = onToggleVisibility,
                modifier = Modifier.size(28.dp)
            ) {
                Icon(
                    imageVector = if (passwordVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                    contentDescription = "Toggle password",
                    tint = PayDzSlateText.copy(alpha = 0.65f),
                    modifier = Modifier.size(19.dp)
                )
            }
        }
    }
}

@Composable
private fun AuthErrorBanner(message: String) {
    Surface(
        color = CrimsonAlert.copy(alpha = 0.12f),
        shape = RoundedCornerShape(14.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(
            text = message,
            fontFamily = CairoFontFamily,
            color = CrimsonAlert,
            fontSize = 12.5.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(12.dp),
            textAlign = TextAlign.Center
        )
    }
}

@Composable
private fun AuthInfoBanner(message: String) {
    Surface(
        color = EmeraldTrust.copy(alpha = 0.12f),
        shape = RoundedCornerShape(14.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(
            text = message,
            fontFamily = CairoFontFamily,
            color = EmeraldTrust,
            fontSize = 12.5.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(12.dp),
            textAlign = TextAlign.Center
        )
    }
}

@Composable
fun DinarPayAuthenticatedApp(
    currentUserId: String,
    userDisplayName: String,
    userEmail: String
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val credentialManager = remember { CredentialManager.create(context) }

    val enrolledBiometric by PayDzAuthManager.biometricEnrolledFlow.collectAsStateWithLifecycle()
    val isBiometricEnrolledForCurrentAccount = enrolledBiometric?.uid == currentUserId
    var showBiometricEnrollDialog by remember { mutableStateOf(false) }

    val viewModel: DinarPayViewModel = viewModel(
        key = currentUserId,
        factory = viewModelFactory {
            initializer {
                val app = checkNotNull(this[APPLICATION_KEY])
                val databaseId = app.getString(R.string.firestore_database_id)
                val db = FirebaseFirestore.getInstance(databaseId)
                DinarPayViewModel(
                    repository = DinarPayRepository(db),
                    currentUserId = currentUserId,
                    userDisplayName = userDisplayName,
                    userEmailOrPhone = userEmail
                )
            }
        }
    )

    val language by viewModel.language.collectAsStateWithLifecycle()
    val isPrivacyMode by viewModel.isPrivacyMode.collectAsStateWithLifecycle()
    val activeModal by viewModel.activeModal.collectAsStateWithLifecycle()
    val lastOperationSuccess by viewModel.lastOperationSuccess.collectAsStateWithLifecycle()
    val selectedReceipt by viewModel.selectedReceipt.collectAsStateWithLifecycle()
    val searchQuery by viewModel.searchQuery.collectAsStateWithLifecycle()
    val selectedCategory by viewModel.selectedCategory.collectAsStateWithLifecycle()
    val isSubmitting by viewModel.isSubmitting.collectAsStateWithLifecycle()
    val statusBanner by viewModel.statusBanner.collectAsStateWithLifecycle()
    val errorBanner by viewModel.errorBanner.collectAsStateWithLifecycle()

    val accountState by viewModel.accountState.collectAsStateWithLifecycle()
    val transactionsState by viewModel.transactionsState.collectAsStateWithLifecycle()
    val beneficiariesState by viewModel.beneficiariesState.collectAsStateWithLifecycle()
    val supportTicketsState by viewModel.supportTicketsState.collectAsStateWithLifecycle()
    val peers by viewModel.peersState.collectAsStateWithLifecycle()
    val notifications by viewModel.notifications.collectAsStateWithLifecycle()
    val systemNotificationEvent by viewModel.systemNotificationEvent.collectAsStateWithLifecycle()

    val notificationPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { _ -> }

    LaunchedEffect(Unit) {
        PayDzNotificationHelper.ensureChannelCreated(context)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            notificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
        }
    }

    LaunchedEffect(systemNotificationEvent) {
        systemNotificationEvent?.let { notif ->
            PayDzNotificationHelper.showSystemNotification(
                context = context,
                title = notif.title,
                body = notif.message
            )
            viewModel.consumeSystemNotificationEvent()
        }
    }

    var selectedTab by rememberSaveable { mutableIntStateOf(0) }
    val isAr = language == AppLanguage.AR
    val layoutDirection = if (language.isRtl) LayoutDirection.Rtl else LayoutDirection.Ltr

    if (selectedTab != 0) {
        BackHandler { selectedTab = 0 }
    }

    val account = (accountState as? UiState.Success)?.data
    val transactions = (transactionsState as? UiState.Success)?.data ?: emptyList()
    val beneficiaries = (beneficiariesState as? UiState.Success)?.data ?: emptyList()
    val supportTickets = (supportTicketsState as? UiState.Success)?.data ?: emptyList()

    CompositionLocalProvider(LocalLayoutDirection provides layoutDirection) {
        Scaffold(
            contentWindowInsets = WindowInsets.safeDrawing,
            containerColor = PayDzBgSoft,
            topBar = {
                if (statusBanner != null || errorBanner != null) {
                    val isErr = errorBanner != null
                    Surface(
                        color = if (isErr) CrimsonAlert else EmeraldTrust,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = errorBanner ?: statusBanner.orEmpty(),
                                color = Color.White,
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.weight(1f)
                            )
                            IconButton(
                                onClick = { viewModel.clearBanners() },
                                modifier = Modifier.size(24.dp)
                            ) {
                                Icon(Icons.Default.Close, contentDescription = "Dismiss", tint = Color.White)
                            }
                        }
                    }
                }
            },
            bottomBar = {
                Surface(
                    color = Color.White,
                    shadowElevation = 12.dp,
                    shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)
                ) {
                    NavigationBar(
                        containerColor = Color.White,
                        tonalElevation = 0.dp
                    ) {
                        val navColors = NavigationBarItemDefaults.colors(
                            selectedIconColor = PayDzNavActiveBlue,
                            selectedTextColor = PayDzNavActiveBlue,
                            indicatorColor = Color(0xFFE6F0FF),
                            unselectedIconColor = PayDzSlateText,
                            unselectedTextColor = PayDzSlateText
                        )
                        NavigationBarItem(
                            selected = selectedTab == 0,
                            onClick = { selectedTab = 0 },
                            icon = { Icon(Icons.Default.Home, contentDescription = null) },
                            label = {
                                Text(
                                    text = if (isAr) "الرئيسية" else "Accueil",
                                    fontFamily = CairoFontFamily,
                                    fontSize = 10.sp,
                                    fontWeight = if (selectedTab == 0) FontWeight.SemiBold else FontWeight.Normal
                                )
                            },
                            colors = navColors,
                            modifier = Modifier.testTag("nav_home")
                        )
                        NavigationBarItem(
                            selected = selectedTab == 1,
                            onClick = { selectedTab = 1 },
                            icon = { Icon(Icons.Default.GridView, contentDescription = null) },
                            label = {
                                Text(
                                    text = if (isAr) "المعاملات" else "Transactions",
                                    fontFamily = CairoFontFamily,
                                    fontSize = 10.sp,
                                    fontWeight = if (selectedTab == 1) FontWeight.SemiBold else FontWeight.Normal
                                )
                            },
                            colors = navColors,
                            modifier = Modifier.testTag("nav_history")
                        )
                        NavigationBarItem(
                            selected = selectedTab == 2,
                            onClick = { selectedTab = 2 },
                            icon = { Icon(Icons.Default.Apps, contentDescription = null) },
                            label = {
                                Text(
                                    text = if (isAr) "الخدمات" else "Services",
                                    fontFamily = CairoFontFamily,
                                    fontSize = 10.sp,
                                    fontWeight = if (selectedTab == 2) FontWeight.SemiBold else FontWeight.Normal
                                )
                            },
                            colors = navColors,
                            modifier = Modifier.testTag("nav_services")
                        )
                        NavigationBarItem(
                            selected = selectedTab == 3,
                            onClick = { selectedTab = 3 },
                            icon = { Icon(Icons.Default.Menu, contentDescription = null) },
                            label = {
                                Text(
                                    text = if (isAr) "المزيد" else "Plus",
                                    fontFamily = CairoFontFamily,
                                    fontSize = 10.sp,
                                    fontWeight = if (selectedTab == 3) FontWeight.SemiBold else FontWeight.Normal
                                )
                            },
                            colors = navColors,
                            modifier = Modifier.testTag("nav_more")
                        )
                    }
                }
            }
        ) { innerPadding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            ) {
                when (selectedTab) {
                    0 -> HomeDashboardScreen(
                        account = account,
                        transactions = transactions,
                        beneficiaries = beneficiaries,
                        isPrivacyMode = isPrivacyMode,
                        isAr = isAr,
                        onTogglePrivacy = { viewModel.togglePrivacyMode() },
                        onOpenModal = { viewModel.openModal(it) },
                        onSelectTransaction = { viewModel.selectReceipt(it) },
                        onDeleteBeneficiary = { viewModel.deleteBeneficiary(it) },
                        onNavigateToServices = { selectedTab = 2 },
                        onNavigateToHistory = { selectedTab = 1 },
                        unreadNotificationsCount = notifications.count { it.isUnread },
                        onToggleCardFrozen = {
                            if (account != null) {
                                viewModel.updateCardSettings(
                                    account = account,
                                    cardFrozen = !account.cardFrozen,
                                    onlinePaymentsEnabled = account.onlinePaymentsEnabled,
                                    contactlessEnabled = account.contactlessEnabled,
                                    biometricEnabled = account.biometricEnabled,
                                    twoFactorEnabled = account.twoFactorEnabled,
                                    dailyTransferLimitDzd = account.dailyTransferLimitDzd,
                                    dailyWithdrawalLimitDzd = account.dailyWithdrawalLimitDzd
                                )
                            }
                        },
                        onToggleContactless = {
                            if (account != null) {
                                viewModel.updateCardSettings(
                                    account = account,
                                    cardFrozen = account.cardFrozen,
                                    onlinePaymentsEnabled = account.onlinePaymentsEnabled,
                                    contactlessEnabled = !account.contactlessEnabled,
                                    biometricEnabled = account.biometricEnabled,
                                    twoFactorEnabled = account.twoFactorEnabled,
                                    dailyTransferLimitDzd = account.dailyTransferLimitDzd,
                                    dailyWithdrawalLimitDzd = account.dailyWithdrawalLimitDzd
                                )
                            }
                        },
                        isBiometricEnrolled = isBiometricEnrolledForCurrentAccount,
                        onOpenBiometricEnroll = { showBiometricEnrollDialog = true }
                    )
                    1 -> TransactionsHistoryScreen(
                        transactions = transactions,
                        searchQuery = searchQuery,
                        selectedCategory = selectedCategory,
                        isPrivacyMode = isPrivacyMode,
                        isAr = isAr,
                        onSearchChange = { viewModel.updateSearchQuery(it) },
                        onCategoryChange = { viewModel.updateCategoryFilter(it) },
                        onSelectTransaction = { viewModel.selectReceipt(it) },
                        onBack = { selectedTab = 0 }
                    )
                    2 -> AllServicesScreen(
                        isAr = isAr,
                        onBack = { selectedTab = 0 },
                        onOpenModal = { viewModel.openModal(it) },
                        onNavigateToCardTab = { selectedTab = 3 },
                        onNavigateToBeneficiaries = { selectedTab = 3 },
                        onNavigateToSupport = { selectedTab = 3 }
                    )
                    3 -> MoreHubScreen(
                        account = account,
                        beneficiaries = beneficiaries,
                        supportTickets = supportTickets,
                        isPrivacyMode = isPrivacyMode,
                        isAr = isAr,
                        onToggleLanguage = { viewModel.toggleLanguage() },
                        onOpenModal = { viewModel.openModal(it) },
                        onDeleteBeneficiary = { viewModel.deleteBeneficiary(it) },
                        onResolveTicket = { viewModel.resolveTicket(it) },
                        onUpdateSettings = { frozen, online, contactless, bio, twoFa, tLimit, wLimit ->
                            if (account != null) {
                                viewModel.updateCardSettings(
                                    account = account,
                                    cardFrozen = frozen,
                                    onlinePaymentsEnabled = online,
                                    contactlessEnabled = contactless,
                                    biometricEnabled = bio,
                                    twoFactorEnabled = twoFa,
                                    dailyTransferLimitDzd = tLimit,
                                    dailyWithdrawalLimitDzd = wLimit
                                )
                            }
                        },
                        onSignOut = {
                            signOut(context, credentialManager, scope)
                        },
                        isBiometricEnrolled = isBiometricEnrolledForCurrentAccount,
                        onOpenBiometricEnroll = { showBiometricEnrollDialog = true },
                        onDisableBiometricEnroll = {
                            PayDzAuthManager.disableFingerprintEnrollment(context)
                        }
                    )
                }
            }
        }

        // In-Account Fingerprint Enrollment Dialog (Only available AFTER logging into an account!)
        if (showBiometricEnrollDialog) {
            val resolvedName = account?.fullName?.takeIf { it.isNotBlank() } ?: userDisplayName
            PayDzBiometricScannerDialog(
                isEnrollmentMode = true,
                accountName = resolvedName,
                accountEmail = userEmail,
                onDismiss = { showBiometricEnrollDialog = false },
                onScanSuccess = {
                    showBiometricEnrollDialog = false
                    val enrolledPin = PayDzAuthManager.getAppSecurityPin(context)
                    PayDzAuthManager.enrollFingerprintForAccount(
                        context = context,
                        uid = currentUserId,
                        email = userEmail,
                        fullName = resolvedName,
                        fallbackPin = enrolledPin
                    )
                    PayDzNotificationHelper.showSystemNotification(
                        context = context,
                        title = "تم تفعيل حماية البصمة وكود التطبيق ✓",
                        body = "تم قفل التطبيق بالبصمة مع كود الأمان ($enrolledPin). لن يفتح التطبيق إلا بالبصمة أو كود سر التطبيق."
                    )
                }
            )
        }

        // Full-Screen Page Flows & Modals (Screens 5, 6, 7, 8 + Idoom Internet + extras)
        when (activeModal) {
            ActiveModal.RIP_TRANSFER -> RipTransferDialog(
                beneficiaries = beneficiaries,
                isSubmitting = isSubmitting,
                isAr = isAr,
                onDismiss = { viewModel.closeModal() },
                onSubmit = { name, rip, amt, note, save ->
                    viewModel.submitRipTransfer(name, rip, amt, note, save)
                },
                peers = peers,
                myRipNumber = account?.ripNumber ?: "00799999004125898421",
                currentBalanceDzd = account?.balanceDzd ?: 125750L,
                transactions = transactions,
                externalError = errorBanner,
                onQuickSaveBeneficiary = { name, rip ->
                    viewModel.addBeneficiary(name, rip, "RIP محفوظ")
                },
                onReceiveSubmit = { senderName, senderRip, amt, note ->
                    viewModel.submitReceiveP2PTransfer(senderName, senderRip, amt, note)
                }
            )
            ActiveModal.NOTIFICATIONS_CENTER -> NotificationsCenterDialog(
                notifications = notifications,
                isAr = isAr,
                onMarkAllRead = { viewModel.markAllNotificationsRead() },
                onOpenTransfer = { viewModel.openModal(ActiveModal.RIP_TRANSFER) },
                onDismiss = { viewModel.closeModal() }
            )
            ActiveModal.MOBILE_TOPUP -> MobileTopUpDialog(
                isSubmitting = isSubmitting,
                isAr = isAr,
                onDismiss = { viewModel.closeModal() },
                onSubmit = { op, phone, plan, amt ->
                    viewModel.submitMobileTopUp(op, phone, plan, amt)
                }
            )
            ActiveModal.BILL_PAYMENT -> BillPaymentDialog(
                isSubmitting = isSubmitting,
                isAr = isAr,
                onDismiss = { viewModel.closeModal() },
                onOpenIdoom = { viewModel.openModal(ActiveModal.IDOOM_INTERNET) },
                onSubmit = { biller, contract, inv, amt ->
                    viewModel.submitUtilityBill(biller, contract, inv, amt)
                }
            )
            ActiveModal.IDOOM_INTERNET -> com.example.ui.IdoomInternetDialog(
                isSubmitting = isSubmitting,
                isAr = isAr,
                onDismiss = { viewModel.closeModal() },
                onSubmit = { biller, contract, inv, amt ->
                    viewModel.submitUtilityBill(biller, contract, inv, amt)
                }
            )
            ActiveModal.OPERATION_SUCCESS -> {
                val summary = lastOperationSuccess
                if (summary != null) {
                    OperationSuccessFullScreenDialog(
                        summary = summary,
                        isAr = isAr,
                        onDismiss = { viewModel.closeModal() }
                    )
                }
            }
            ActiveModal.CARDLESS_ATM -> CardlessAtmDialog(
                isSubmitting = isSubmitting,
                isAr = isAr,
                onDismiss = { viewModel.closeModal() },
                onSubmit = { amt, phone ->
                    viewModel.submitCardlessWithdrawal(amt, phone)
                }
            )
            ActiveModal.QR_PAYMENT -> QrPaymentDialog(
                isSubmitting = isSubmitting,
                isAr = isAr,
                onDismiss = { viewModel.closeModal() },
                onSubmit = { merchant, code, amt ->
                    viewModel.submitQrPayment(merchant, code, amt)
                }
            )
            ActiveModal.RECHARGE_SANDBOX -> SandboxRechargeDialog(
                isSubmitting = isSubmitting,
                isAr = isAr,
                onDismiss = { viewModel.closeModal() },
                onSubmit = { amt ->
                    viewModel.submitSandboxRecharge(amt)
                }
            )
            ActiveModal.ADD_BENEFICIARY -> AddBeneficiaryDialog(
                isSubmitting = isSubmitting,
                isAr = isAr,
                onDismiss = { viewModel.closeModal() },
                onSubmit = { name, rip, nick ->
                    viewModel.addBeneficiary(name, rip, nick)
                }
            )
            ActiveModal.NEW_SUPPORT_TICKET -> NewSupportTicketDialog(
                isSubmitting = isSubmitting,
                isAr = isAr,
                onDismiss = { viewModel.closeModal() },
                onSubmit = { subj, cat, prio, msg ->
                    viewModel.submitSupportTicket(subj, cat, prio, msg)
                }
            )
            ActiveModal.ACCOUNT_PROFILE -> AccountProfileDialog(
                account = account,
                isSubmitting = isSubmitting,
                isAr = isAr,
                onDismiss = { viewModel.closeModal() },
                onUpdateProfile = { name, phone ->
                    viewModel.submitUpdateProfile(name, phone)
                },
                onOpenSandboxRecharge = {
                    viewModel.openModal(ActiveModal.RECHARGE_SANDBOX)
                }
            )
            ActiveModal.NONE -> Unit
        }

        selectedReceipt?.let { receiptTx ->
            DigitalReceiptDialog(
                transaction = receiptTx,
                isAr = isAr,
                onDismiss = { viewModel.selectReceipt(null) }
            )
        }
    }
}

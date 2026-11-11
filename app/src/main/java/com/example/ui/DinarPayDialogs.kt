package com.example.ui

import android.content.Intent
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import com.example.R
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Apartment
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ChevronLeft
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.MonetizationOn
import androidx.compose.material.icons.filled.PersonOutline
import androidx.compose.material.icons.filled.PhoneAndroid
import androidx.compose.material.icons.filled.QrCode2
import androidx.compose.material.icons.filled.Router
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.material.icons.filled.Wifi
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilterChip
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.data.model.Beneficiary
import com.example.data.model.PublicDirectoryPeer
import com.example.data.model.WalletAccount
import com.example.data.model.WalletTransaction
import com.example.ui.theme.DjezzyRed
import com.example.ui.theme.MobilisGreen
import com.example.ui.theme.OoredooRed
import com.example.ui.theme.PayDzBgSoft
import com.example.ui.theme.PayDzBlueBright
import com.example.ui.theme.PayDzBluePrimary
import com.example.ui.theme.PayDzDesignTokens
import com.example.ui.theme.PayDzGold
import com.example.ui.theme.PayDzGreen
import com.example.ui.theme.PayDzInputBorder
import androidx.compose.material.icons.filled.BookmarkAdd
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import com.example.ui.theme.CairoFontFamily
import com.example.ui.theme.PayDzCardTitleText
import com.example.ui.theme.PayDzNavyText
import com.example.ui.theme.PayDzRed
import com.example.ui.theme.PayDzRedSoft
import com.example.ui.theme.PayDzSlateText
import com.example.ui.theme.PoppinsFontFamily
import com.example.ui.theme.RobotoMonoFontFamily
import com.example.util.PayDzAppNotification
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Locale

@Composable
private fun PayDzFullScreenHeader(
    title: String,
    onBack: () -> Unit,
    testTag: String
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp)
    ) {
        Text(
            text = title,
            color = PayDzNavyText,
            fontFamily = CairoFontFamily,
            fontSize = 17.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.align(Alignment.Center)
        )
        // Left-side '<' chevron matching the reference image (Alignment.CenterEnd in RTL = Left side)
        IconButton(
            onClick = onBack,
            modifier = Modifier
                .align(Alignment.CenterEnd)
                .size(42.dp)
                .testTag(testTag)
        ) {
            Icon(
                imageVector = Icons.Default.ChevronLeft,
                contentDescription = "رجوع",
                tint = PayDzNavyText,
                modifier = Modifier.size(32.dp)
            )
        }
    }
}

@Composable
fun PayDzPillInputField(
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    leadingIcon: ImageVector,
    leadingIconTint: Color = PayDzSlateText,
    keyboardType: KeyboardType = KeyboardType.Text,
    ltrDigitsAlignLeft: Boolean = false,
    testTag: String = ""
) {
    Surface(
        shape = PayDzDesignTokens.PillShape,
        color = PayDzDesignTokens.PillInputBackground,
        shadowElevation = 2.dp,
        modifier = Modifier
            .fillMaxWidth()
            .height(PayDzDesignTokens.PillInputHeight)
            .border(1.dp, PayDzDesignTokens.PillInputBorder, PayDzDesignTokens.PillShape)
            .let { if (testTag.isNotEmpty()) it.testTag(testTag) else it }
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 20.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Right-side icon (Start in RTL)
            Icon(
                imageVector = leadingIcon,
                contentDescription = null,
                tint = leadingIconTint,
                modifier = Modifier.size(22.dp)
            )
            Box(
                modifier = Modifier.weight(1f),
                contentAlignment = if (ltrDigitsAlignLeft) Alignment.CenterEnd else Alignment.CenterStart
            ) {
                if (value.isEmpty()) {
                    Text(
                        text = placeholder,
                        color = PayDzSlateText,
                        fontFamily = CairoFontFamily,
                        fontWeight = FontWeight.Normal,
                        fontSize = 12.sp,
                        textAlign = if (ltrDigitsAlignLeft) TextAlign.End else TextAlign.Start,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
                BasicTextField(
                    value = value,
                    onValueChange = onValueChange,
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
                    textStyle = TextStyle(
                        color = PayDzNavyText,
                        fontFamily = CairoFontFamily,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        textAlign = if (ltrDigitsAlignLeft) TextAlign.End else TextAlign.Start
                    ),
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}

@Composable
fun PayDzPrimaryPillButton(
    text: String,
    isLoading: Boolean,
    showRightArrow: Boolean = true,
    onClick: () -> Unit,
    testTag: String
) {
    Button(
        onClick = onClick,
        enabled = !isLoading,
        shape = PayDzDesignTokens.PillShape,
        colors = ButtonDefaults.buttonColors(
            containerColor = PayDzDesignTokens.PrimaryBlue,
            contentColor = Color.White
        ),
        elevation = ButtonDefaults.buttonElevation(defaultElevation = 6.dp),
        modifier = Modifier
            .fillMaxWidth()
            .height(PayDzDesignTokens.PrimaryButtonHeight)
            .testTag(testTag)
    ) {
        if (isLoading) {
            CircularProgressIndicator(
                modifier = Modifier.size(24.dp),
                color = Color.White,
                strokeWidth = 2.5.dp
            )
        } else {
            Box(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = text,
                    fontFamily = CairoFontFamily,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.align(Alignment.Center)
                )
                // In the reference image, the arrow '->' is on the RIGHT side of the button (Alignment.CenterStart in RTL)
                if (showRightArrow) {
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
        }
    }
}

/**
 * SCREEN 5 OF PAYDZ MOCKUP: شحن الهاتف (Mobile Recharge Full Screen)
 */
@Composable
fun MobileTopUpDialog(
    isSubmitting: Boolean,
    isAr: Boolean,
    onDismiss: () -> Unit,
    onSubmit: (operatorName: String, phoneNumber: String, planTitle: String, amountDzd: Long) -> Unit
) {
    BackHandler { onDismiss() }
    var selectedOperator by remember { mutableStateOf("Mobilis") }
    var phoneNumber by remember { mutableStateOf("06 12 34 56 78") }
    var selectedAmount by remember { mutableLongStateOf(1000L) }
    var showCustomInput by remember { mutableStateOf(false) }
    var customAmountText by remember { mutableStateOf("1000") }
    var validationError by remember { mutableStateOf<String?>(null) }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(
            usePlatformDefaultWidth = false,
            decorFitsSystemWindows = false
        )
    ) {
        CompositionLocalProvider(LocalLayoutDirection provides if (isAr) LayoutDirection.Rtl else LayoutDirection.Ltr) {
            Surface(
                modifier = Modifier.fillMaxSize(),
                color = PayDzBgSoft
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .windowInsetsPadding(WindowInsets.safeDrawing)
                        .verticalScroll(rememberScrollState())
                        .padding(bottom = 24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    PayDzFullScreenHeader(
                        title = if (isAr) "شحن الهاتف" else "Recharge Mobile",
                        onBack = onDismiss,
                        testTag = "close_topup_dialog"
                    )

                    PayDzTopUpHeroIllustration()

                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 22.dp),
                        verticalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        Text(
                            text = if (isAr) "اختر المتعامل" else "Choisir l'opérateur",
                            color = PayDzNavyText,
                            fontFamily = CairoFontFamily,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.fillMaxWidth()
                        )

                        // In RTL: First item is Right (ooredoo), Middle is DJEZZY, Last is Left (mobilis),
                        // matching the exact left-to-right visual order [mobilis | DJEZZY | ooredoo] in the mockup!
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            OperatorBrandCard(
                                title = "ooredoo",
                                subtitle = "أوريدو",
                                bgColor = Color.White,
                                contentColor = OoredooRed,
                                isSelected = selectedOperator == "Ooredoo",
                                onClick = { selectedOperator = "Ooredoo" },
                                modifier = Modifier
                                    .weight(1f)
                                    .testTag("operator_ooredoo")
                            )
                            OperatorBrandCard(
                                title = "DJEZZY",
                                subtitle = "جيزي",
                                bgColor = DjezzyRed,
                                contentColor = Color.White,
                                isSelected = selectedOperator == "Djezzy",
                                onClick = { selectedOperator = "Djezzy" },
                                modifier = Modifier
                                    .weight(1f)
                                    .testTag("operator_djezzy")
                            )
                            OperatorBrandCard(
                                title = "mobilis",
                                subtitle = "موبيليس",
                                bgColor = MobilisGreen,
                                contentColor = Color.White,
                                isSelected = selectedOperator == "Mobilis",
                                onClick = { selectedOperator = "Mobilis" },
                                modifier = Modifier
                                    .weight(1f)
                                    .testTag("operator_mobilis")
                            )
                        }

                        Text(
                            text = if (isAr) "رقم الهاتف" else "Numéro de téléphone",
                            color = PayDzNavyText,
                            fontFamily = CairoFontFamily,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.fillMaxWidth()
                        )

                        PayDzPillInputField(
                            value = phoneNumber,
                            onValueChange = {
                                phoneNumber = it
                                validationError = null
                            },
                            placeholder = "06 12 34 56 78",
                            leadingIcon = Icons.Default.PhoneAndroid,
                            keyboardType = KeyboardType.Phone,
                            ltrDigitsAlignLeft = true,
                            testTag = "topup_phone_input"
                        )

                        Text(
                            text = if (isAr) "اختر المبلغ" else "Choisir le montant",
                            color = PayDzNavyText,
                            fontFamily = CairoFontFamily,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.fillMaxWidth()
                        )

                        // Preset Amount Pills: Right = 1,000 دج | Middle = 500 دج | Left = 200 دج
                        val amounts = listOf(1000L, 500L, 200L)
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            amounts.forEach { amt ->
                                val selected = !showCustomInput && selectedAmount == amt
                                Surface(
                                    shape = PayDzDesignTokens.PillShape,
                                    color = if (selected) Color(0xFFDCEAFE) else Color.White,
                                    shadowElevation = if (selected) 3.dp else 1.dp,
                                    modifier = Modifier
                                        .weight(1f)
                                        .height(46.dp)
                                        .border(
                                            width = if (selected) 2.dp else 1.dp,
                                            color = if (selected) PayDzBluePrimary else PayDzInputBorder,
                                            shape = PayDzDesignTokens.PillShape
                                        )
                                        .clickable {
                                            showCustomInput = false
                                            selectedAmount = amt
                                        }
                                        .testTag("topup_amount_$amt")
                                ) {
                                    Box(contentAlignment = Alignment.Center) {
                                        val formatted = NumberFormat.getNumberInstance(Locale.US).format(amt)
                                        Text(
                                            text = "$formatted دج",
                                            color = if (selected) PayDzBluePrimary else PayDzNavyText,
                                            fontFamily = CairoFontFamily,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 13.sp
                                        )
                                    }
                                }
                            }
                        }

                        // Custom Amount Row: مبلغ مخصص + gold coins icon on the right (Start in RTL)
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { showCustomInput = !showCustomInput }
                                .padding(vertical = 4.dp)
                                .testTag("topup_custom_amount_toggle"),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Image(
                                painter = painterResource(id = R.drawable.canva_ic_coins_gold),
                                contentDescription = null,
                                contentScale = ContentScale.Fit,
                                modifier = Modifier.size(24.dp)
                            )
                            Text(
                                text = if (isAr) "مبلغ مخصص" else "Montant personnalisé",
                                color = PayDzSlateText,
                                fontFamily = CairoFontFamily,
                                fontWeight = FontWeight.Normal,
                                fontSize = 12.sp
                            )
                        }

                        if (showCustomInput) {
                            PayDzPillInputField(
                                value = customAmountText,
                                onValueChange = {
                                    customAmountText = it.filter { ch -> ch.isDigit() }
                                    selectedAmount = customAmountText.toLongOrNull() ?: 200L
                                },
                                placeholder = if (isAr) "أدخل المبلغ المخصص (دج)" else "Montant (DZD)",
                                leadingIcon = Icons.Default.MonetizationOn,
                                leadingIconTint = PayDzGold,
                                keyboardType = KeyboardType.Number,
                                testTag = "topup_custom_amount_input"
                            )
                        }

                        if (validationError != null) {
                            Text(
                                text = validationError.orEmpty(),
                                color = PayDzRed,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        Spacer(modifier = Modifier.height(6.dp))

                        PayDzPrimaryPillButton(
                            text = if (isAr) "متابعة" else "Continuer",
                            isLoading = isSubmitting,
                            showRightArrow = true,
                            onClick = {
                                val digits = phoneNumber.filter { it.isDigit() }
                                if (digits.length < 9) {
                                    validationError = "يرجى إدخال رقم هاتف صحيح مكون من 10 أرقام"
                                } else {
                                    onSubmit(
                                        selectedOperator,
                                        digits,
                                        "شحن فوري $selectedOperator",
                                        selectedAmount.coerceAtLeast(100L)
                                    )
                                }
                            },
                            testTag = "confirm_topup_button"
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun OperatorBrandCard(
    title: String,
    subtitle: String,
    bgColor: Color,
    contentColor: Color,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        shape = PayDzDesignTokens.CardShapeSmall,
        color = bgColor,
        shadowElevation = if (isSelected) 8.dp else 2.dp,
        modifier = modifier
            .height(PayDzDesignTokens.OperatorCardHeight)
            .border(
                width = if (isSelected) 3.dp else 1.dp,
                color = if (isSelected) PayDzBluePrimary else PayDzInputBorder,
                shape = PayDzDesignTokens.CardShapeSmall
            )
            .clickable(onClick = onClick)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = title,
                color = contentColor,
                fontWeight = FontWeight.ExtraBold,
                fontSize = 16.sp
            )
            Text(
                text = subtitle,
                color = contentColor.copy(alpha = 0.85f),
                fontSize = 11.sp,
                fontWeight = FontWeight.Medium
            )
        }
    }
}

/**
 * SCREEN 6 OF PAYDZ MOCKUP: دفع الفواتير (Bill Payment Full Screen)
 */
@Composable
fun BillPaymentDialog(
    isSubmitting: Boolean,
    isAr: Boolean,
    onDismiss: () -> Unit,
    onOpenIdoom: () -> Unit = {},
    onSubmit: (billerName: String, contractNumber: String, invoiceRef: String, amountDzd: Long) -> Unit
) {
    BackHandler { onDismiss() }
    val billers = listOf(
        "سونلغاز الكهرباء والغاز",
        "الماء",
        "اتصالات وإنترنت",
        "AADL"
    )
    var selectedBiller by remember { mutableStateOf(billers[0]) }
    var contractNumber by remember { mutableStateOf("94820154") }
    var invoiceRef by remember { mutableStateOf("FAC-2026-11") }
    var amountText by remember { mutableStateOf("2500") }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(
            usePlatformDefaultWidth = false,
            decorFitsSystemWindows = false
        )
    ) {
        CompositionLocalProvider(LocalLayoutDirection provides if (isAr) LayoutDirection.Rtl else LayoutDirection.Ltr) {
            Surface(
                modifier = Modifier.fillMaxSize(),
                color = PayDzBgSoft
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .windowInsetsPadding(WindowInsets.safeDrawing)
                        .verticalScroll(rememberScrollState())
                        .padding(bottom = 24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    PayDzFullScreenHeader(
                        title = if (isAr) "دفع الفواتير" else "Paiement de Factures",
                        onBack = onDismiss,
                        testTag = "close_bill_dialog"
                    )

                    PayDzBillPaymentHeroIllustration()

                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 22.dp),
                        verticalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        Text(
                            text = if (isAr) "اختر نوع الفاتورة" else "Choisir le type de facture",
                            color = PayDzNavyText,
                            fontFamily = CairoFontFamily,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.fillMaxWidth()
                        )

                        // Row 1 in RTL: Right = الماء | Left = سونلغاز الكهرباء والغاز (matching Screen 6 image!)
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            BillerGridCard(
                                title = if (isAr) "الماء" else "SEAAL / ADE\nEau",
                                iconRes = R.drawable.canva_ic_water_seall,
                                selected = selectedBiller == billers[1],
                                onClick = { selectedBiller = billers[1] },
                                modifier = Modifier
                                    .weight(1f)
                                    .testTag("biller_water")
                            )
                            BillerGridCard(
                                title = if (isAr) "سونلغاز\nالكهرباء والغاز" else "Sonelgaz\nÉlectricité & Gaz",
                                iconRes = R.drawable.canva_ic_sonelgaz,
                                selected = selectedBiller == billers[0],
                                onClick = { selectedBiller = billers[0] },
                                modifier = Modifier
                                    .weight(1f)
                                    .testTag("biller_sonelgaz")
                            )
                        }

                        // Row 2 in RTL: Right = اتصالات وإنترنت | Left = AADL (matching Screen 6 image!)
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            BillerGridCard(
                                title = if (isAr) "اتصالات\nوإنترنت" else "Algérie Télécom\n& Internet",
                                iconRes = R.drawable.canva_ic_telecom_internet,
                                selected = selectedBiller == billers[2],
                                onClick = { selectedBiller = billers[2] },
                                modifier = Modifier
                                    .weight(1f)
                                    .testTag("biller_telecom")
                            )
                            BillerGridCard(
                                title = "AADL",
                                iconRes = R.drawable.canva_ic_aadl_housing,
                                selected = selectedBiller == billers[3],
                                onClick = { selectedBiller = billers[3] },
                                modifier = Modifier
                                    .weight(1f)
                                    .testTag("biller_aadl")
                            )
                        }

                        PayDzPillInputField(
                            value = contractNumber,
                            onValueChange = { contractNumber = it },
                            placeholder = if (isAr) "رقم العقد أو الاشتراك" else "Numéro de contrat",
                            leadingIcon = Icons.Default.Edit,
                            testTag = "bill_contract_input"
                        )

                        PayDzPillInputField(
                            value = amountText,
                            onValueChange = { amountText = it.filter { ch -> ch.isDigit() } },
                            placeholder = if (isAr) "المبلغ (د.ج)" else "Montant (DZD)",
                            leadingIcon = Icons.Default.MonetizationOn,
                            leadingIconTint = PayDzGold,
                            keyboardType = KeyboardType.Number,
                            testTag = "bill_amount_input"
                        )

                        Spacer(modifier = Modifier.height(6.dp))

                        PayDzPrimaryPillButton(
                            text = if (isAr) "متابعة" else "Continuer",
                            isLoading = isSubmitting,
                            showRightArrow = false,
                            onClick = {
                                val amt = amountText.toLongOrNull() ?: 2500L
                                onSubmit(selectedBiller, contractNumber, invoiceRef, amt)
                            },
                            testTag = "confirm_bill_button"
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun BillerGridCard(
    title: String,
    iconRes: Int,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        shape = PayDzDesignTokens.CardShapeMedium,
        color = Color.White,
        shadowElevation = if (selected) 6.dp else 2.dp,
        modifier = modifier
            .height(PayDzDesignTokens.BillerGridCardHeight)
            .border(
                width = if (selected) 2.5.dp else 1.dp,
                color = if (selected) PayDzBluePrimary else PayDzInputBorder,
                shape = PayDzDesignTokens.CardShapeMedium
            )
            .clickable(onClick = onClick)
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
                modifier = Modifier.size(46.dp)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = title,
                color = PayDzCardTitleText,
                fontFamily = CairoFontFamily,
                fontWeight = FontWeight.Bold,
                fontSize = 13.sp,
                lineHeight = 16.sp,
                textAlign = TextAlign.Center
            )
        }
    }
}

/**
 * DEDICATED IDOOM INTERNET SCREEN: إنترنت منزلي Idoom (ADSL / Fibre)
 */
@Composable
fun IdoomInternetDialog(
    isSubmitting: Boolean,
    isAr: Boolean,
    onDismiss: () -> Unit,
    onSubmit: (billerName: String, contractNumber: String, invoiceRef: String, amountDzd: Long) -> Unit
) {
    BackHandler { onDismiss() }
    var selectedType by remember { mutableStateOf("Idoom Fibre") }
    var subscriptionNumber by remember { mutableStateOf("021 74 85 96") }
    var selectedAmount by remember { mutableLongStateOf(2000L) }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(
            usePlatformDefaultWidth = false,
            decorFitsSystemWindows = false
        )
    ) {
        CompositionLocalProvider(LocalLayoutDirection provides if (isAr) LayoutDirection.Rtl else LayoutDirection.Ltr) {
            Surface(
                modifier = Modifier.fillMaxSize(),
                color = PayDzBgSoft
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .windowInsetsPadding(WindowInsets.safeDrawing)
                        .verticalScroll(rememberScrollState())
                        .padding(bottom = 24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    PayDzFullScreenHeader(
                        title = if (isAr) "إنترنت منزلي Idoom" else "Internet Idoom",
                        onBack = onDismiss,
                        testTag = "close_idoom_dialog"
                    )

                    // Idoom Hero Logo Banner
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(115.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Surface(
                            shape = RoundedCornerShape(24.dp),
                            color = Color.White,
                            shadowElevation = 4.dp,
                            modifier = Modifier
                                .width(190.dp)
                                .height(86.dp)
                                .border(1.5.dp, PayDzBluePrimary.copy(alpha = 0.25f), RoundedCornerShape(24.dp))
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(horizontal = 16.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Wifi,
                                    contentDescription = null,
                                    tint = PayDzBluePrimary,
                                    modifier = Modifier.size(38.dp)
                                )
                                Spacer(modifier = Modifier.width(10.dp))
                                Column {
                                    Text(
                                        text = "IDOOM",
                                        color = PayDzBluePrimary,
                                        fontWeight = FontWeight.ExtraBold,
                                        fontSize = 22.sp
                                    )
                                    Text(
                                        text = "Algérie Télécom",
                                        color = PayDzGreen,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 11.sp
                                    )
                                }
                            }
                        }
                    }

                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 22.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Text(
                            text = if (isAr) "اختر نوع الاشتراك (ADSL أو Fibre)" else "Type d'abonnement",
                            color = PayDzNavyText,
                            fontSize = PayDzDesignTokens.SectionTitleSize,
                            fontWeight = FontWeight.Bold,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.fillMaxWidth()
                        )

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            BillerGridCard(
                                title = "Idoom Fibre\nالألياف البصرية",
                                iconRes = R.drawable.canva_ic_idoom_wifi,
                                selected = selectedType == "Idoom Fibre",
                                onClick = { selectedType = "Idoom Fibre" },
                                modifier = Modifier
                                    .weight(1f)
                                    .testTag("idoom_type_fibre")
                            )
                            BillerGridCard(
                                title = "Idoom ADSL\nالإنترنت الثابت",
                                iconRes = R.drawable.canva_ic_telecom_internet,
                                selected = selectedType == "Idoom ADSL",
                                onClick = { selectedType = "Idoom ADSL" },
                                modifier = Modifier
                                    .weight(1f)
                                    .testTag("idoom_type_adsl")
                            )
                        }

                        Text(
                            text = if (isAr) "رقم الاشتراك أو الهاتف الثابت" else "Numéro de ligne / abonnement",
                            color = PayDzNavyText,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold
                        )

                        PayDzPillInputField(
                            value = subscriptionNumber,
                            onValueChange = { subscriptionNumber = it },
                            placeholder = "021 74 85 96",
                            leadingIcon = Icons.Default.PhoneAndroid,
                            keyboardType = KeyboardType.Phone,
                            ltrDigitsAlignLeft = true,
                            testTag = "idoom_subscription_input"
                        )

                        Text(
                            text = if (isAr) "اختر باقة الشحن الشهرية" else "Choisir le forfait",
                            color = PayDzNavyText,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold
                        )

                        val tiers = listOf(1600L, 2000L, 2500L)
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            tiers.forEach { amt ->
                                val selected = selectedAmount == amt
                                Surface(
                                    shape = PayDzDesignTokens.PillShape,
                                    color = if (selected) Color(0xFFDCEAFE) else Color.White,
                                    modifier = Modifier
                                        .weight(1f)
                                        .height(46.dp)
                                        .border(
                                            width = if (selected) 2.dp else 1.dp,
                                            color = if (selected) PayDzBluePrimary else PayDzInputBorder,
                                            shape = PayDzDesignTokens.PillShape
                                        )
                                        .clickable { selectedAmount = amt }
                                ) {
                                    Box(contentAlignment = Alignment.Center) {
                                        val formatted = NumberFormat.getNumberInstance(Locale.US).format(amt)
                                        Text(
                                            text = "$formatted دج",
                                            color = if (selected) PayDzBluePrimary else PayDzNavyText,
                                            fontWeight = FontWeight.ExtraBold,
                                            fontSize = 15.sp
                                        )
                                    }
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        PayDzPrimaryPillButton(
                            text = if (isAr) "متابعة ودفع الفاتورة" else "Continuer et payer",
                            isLoading = isSubmitting,
                            showRightArrow = true,
                            onClick = {
                                val cleanSub = subscriptionNumber.filter { it.isDigit() }.ifEmpty { "021748596" }
                                onSubmit(
                                    "إنترنت منزلي $selectedType",
                                    cleanSub,
                                    "IDOOM-2026",
                                    selectedAmount
                                )
                            },
                            testTag = "confirm_idoom_button"
                        )
                    }
                }
            }
        }
    }
}

private data class SuggestedRipContact(
    val fullName: String,
    val ripNumber: String,
    val tagLabel: String,
    val isLiveUser: Boolean
)

/**
 * SCREEN 7 OF PAYDZ MOCKUP: تحويل الأموال (Money Transfer Full Screen — P2P Send & Receive)
 */
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun RipTransferDialog(
    beneficiaries: List<Beneficiary>,
    isSubmitting: Boolean,
    isAr: Boolean,
    onDismiss: () -> Unit,
    onSubmit: (recipientName: String, recipientRip: String, amountDzd: Long, note: String, saveBeneficiary: Boolean) -> Unit,
    peers: List<PublicDirectoryPeer> = emptyList(),
    myRipNumber: String = "00799999004125898421",
    currentBalanceDzd: Long = 125750L,
    transactions: List<WalletTransaction> = emptyList(),
    externalError: String? = null,
    onQuickSaveBeneficiary: (fullName: String, ripNumber: String) -> Unit = { _, _ -> },
    onReceiveSubmit: (senderName: String, senderRip: String, amountDzd: Long, note: String) -> Unit = { _, _, _, _ -> }
) {
    BackHandler { onDismiss() }
    val clipboard = LocalClipboardManager.current
    var isReceiveMode by remember { mutableStateOf(false) }

    // PRIVACY ENFORCEMENT: ONLY include contacts explicitly saved in the user's own beneficiaries list!
    // NEVER expose other users or strangers from any public directory.
    val mySavedContacts = remember(beneficiaries, myRipNumber) {
        val map = linkedMapOf<String, SuggestedRipContact>()
        beneficiaries.forEach { ben ->
            val cleanRip = ben.ripNumber.filter { it.isDigit() }
            if (cleanRip.length == 20 && cleanRip != myRipNumber && !map.containsKey(cleanRip)) {
                map[cleanRip] = SuggestedRipContact(
                    fullName = ben.fullName.ifBlank { "مستفيد محفوظ" },
                    ripNumber = cleanRip,
                    tagLabel = ben.nickname.ifBlank { "RIP محفوظ لديك" },
                    isLiveUser = false
                )
            }
        }
        map.values.toList()
    }

    var recipientInput by remember { mutableStateOf("") }
    var recipientName by remember { mutableStateOf("") }
    var amountText by remember { mutableStateOf("5000") }
    var note by remember { mutableStateOf("") }
    var saveRipAutomatically by remember { mutableStateOf(true) }
    var localValidationError by remember { mutableStateOf<String?>(null) }
    var savedToastMessage by remember { mutableStateOf<String?>(null) }

    val formattedBalance = NumberFormat.getNumberInstance(Locale.US).format(currentBalanceDzd)
    val enteredAmount = amountText.toLongOrNull() ?: 0L
    val transferFee = 15L
    val isExceedingWalletBalance = !isReceiveMode && (enteredAmount > currentBalanceDzd || (enteredAmount > 0 && enteredAmount + transferFee > currentBalanceDzd))

    // Filter suggestions matching typed name or RIP digits strictly among saved beneficiaries
    val matchingSuggestions = remember(mySavedContacts, recipientInput, recipientName) {
        val digitQuery = recipientInput.filter { it.isDigit() }
        val nameQuery = recipientName.trim()
        if (digitQuery.isEmpty() && nameQuery.isEmpty()) {
            mySavedContacts
        } else {
            mySavedContacts.filter { contact ->
                (digitQuery.isNotEmpty() && (contact.ripNumber.contains(digitQuery) || digitQuery == contact.ripNumber)) ||
                    (nameQuery.isNotEmpty() && contact.fullName.contains(nameQuery, ignoreCase = true))
            }
        }
    }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(
            usePlatformDefaultWidth = false,
            decorFitsSystemWindows = false
        )
    ) {
        CompositionLocalProvider(LocalLayoutDirection provides if (isAr) LayoutDirection.Rtl else LayoutDirection.Ltr) {
            Surface(
                modifier = Modifier.fillMaxSize(),
                color = PayDzBgSoft
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .windowInsetsPadding(WindowInsets.safeDrawing)
                        .verticalScroll(rememberScrollState())
                        .padding(bottom = 24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    PayDzFullScreenHeader(
                        title = if (isAr) "تحويل الأموال" else "Transfert d'Argent",
                        onBack = onDismiss,
                        testTag = "close_transfer_dialog"
                    )

                    PayDzMoneyTransferHeroIllustration()

                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 22.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        // Available Wallet Balance Banner inside Transfer Screen
                        Surface(
                            shape = RoundedCornerShape(16.dp),
                            color = if (isExceedingWalletBalance) PayDzRedSoft else Color(0xFFE6F0FF),
                            modifier = Modifier
                                .fillMaxWidth()
                                .border(
                                    1.dp,
                                    if (isExceedingWalletBalance) PayDzRed else PayDzBluePrimary.copy(alpha = 0.3f),
                                    RoundedCornerShape(16.dp)
                                )
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 14.dp, vertical = 10.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = if (isAr) "رصيد محفظتك المتاح:" else "Solde disponible :",
                                    fontFamily = CairoFontFamily,
                                    color = PayDzNavyText,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 12.sp
                                )
                                Text(
                                    text = "$formattedBalance.00 دج",
                                    fontFamily = PoppinsFontFamily,
                                    color = if (isExceedingWalletBalance) PayDzRed else PayDzBluePrimary,
                                    fontWeight = FontWeight.ExtraBold,
                                    fontSize = 14.sp
                                )
                            }
                        }

                        // P2P Mode Selector: إرسال أموال | استلام أموال
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Surface(
                                shape = PayDzDesignTokens.PillShape,
                                color = if (!isReceiveMode) PayDzBluePrimary else Color.White,
                                modifier = Modifier
                                    .weight(1f)
                                    .height(42.dp)
                                    .border(
                                        1.dp,
                                        if (!isReceiveMode) PayDzBluePrimary else PayDzInputBorder,
                                        PayDzDesignTokens.PillShape
                                    )
                                    .clickable {
                                        isReceiveMode = false
                                        localValidationError = null
                                    }
                                    .testTag("transfer_mode_send")
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Text(
                                        text = if (isAr) "إرسال أموال" else "Envoyer",
                                        fontFamily = CairoFontFamily,
                                        color = if (!isReceiveMode) Color.White else PayDzNavyText,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 14.sp
                                    )
                                }
                            }
                            Surface(
                                shape = PayDzDesignTokens.PillShape,
                                color = if (isReceiveMode) PayDzGreen else Color.White,
                                modifier = Modifier
                                    .weight(1f)
                                    .height(42.dp)
                                    .border(
                                        1.dp,
                                        if (isReceiveMode) PayDzGreen else PayDzInputBorder,
                                        PayDzDesignTokens.PillShape
                                    )
                                    .clickable {
                                        isReceiveMode = true
                                        localValidationError = null
                                    }
                                    .testTag("transfer_mode_receive")
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Text(
                                        text = if (isAr) "استلام أموال" else "Recevoir",
                                        fontFamily = CairoFontFamily,
                                        color = if (isReceiveMode) Color.White else PayDzNavyText,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 14.sp
                                    )
                                }
                            }
                        }

                        if (isReceiveMode) {
                            Surface(
                                shape = RoundedCornerShape(18.dp),
                                color = Color.White,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .border(1.dp, PayDzInputBorder, RoundedCornerShape(18.dp))
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(horizontal = 14.dp, vertical = 10.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            text = if (isAr) "رقم حسابك RIP لاستقبال التحويلات:" else "Votre RIP pour recevoir :",
                                            fontFamily = CairoFontFamily,
                                            color = PayDzSlateText,
                                            fontSize = 12.sp
                                        )
                                        Text(
                                            text = myRipNumber,
                                            color = PayDzBluePrimary,
                                            fontFamily = RobotoMonoFontFamily,
                                            fontWeight = FontWeight.ExtraBold,
                                            fontSize = 13.sp
                                        )
                                    }
                                    IconButton(onClick = { clipboard.setText(AnnotatedString(myRipNumber)) }) {
                                        Icon(Icons.Default.ContentCopy, contentDescription = "Copy RIP", tint = PayDzBluePrimary)
                                    }
                                }
                            }
                        }

                        Text(
                            text = if (isAr) "أدخل معلومات التحويل" else "Entrez les détails du transfert",
                            color = PayDzNavyText,
                            fontFamily = CairoFontFamily,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.fillMaxWidth()
                        )

                        // Beneficiaries Section: Privacy Notice if empty, or Saved Beneficiaries chips if non-empty
                        if (mySavedContacts.isEmpty()) {
                            Surface(
                                shape = RoundedCornerShape(14.dp),
                                color = Color(0xFFF1F6FF),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .border(1.dp, PayDzBluePrimary.copy(alpha = 0.2f), RoundedCornerShape(14.dp))
                            ) {
                                Row(
                                    modifier = Modifier.padding(12.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Lock,
                                        contentDescription = null,
                                        tint = PayDzBluePrimary,
                                        modifier = Modifier.size(20.dp)
                                    )
                                    Text(
                                        text = if (isAr) {
                                            "حماية الخصوصية: لا توجد حسابات RIP محفوظة لديك بعد. لا يقترح التطبيق أي مستخدمين آخرين حمايةً للخصوصية. أدخل رقم RIP المكون من 20 رقماً الذي زوّدك به المستلم شخصياً."
                                        } else {
                                            "Confidentialité : Aucun RIP enregistré. L'application ne propose aucun utilisateur public pour protéger la vie privée."
                                        },
                                        fontFamily = CairoFontFamily,
                                        color = PayDzNavyText,
                                        fontSize = 11.5.sp,
                                        lineHeight = 17.sp
                                    )
                                }
                            }
                        } else if (matchingSuggestions.isNotEmpty()) {
                            Column(
                                modifier = Modifier.fillMaxWidth(),
                                verticalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Text(
                                    text = if (isAr) "المستفيدون المحفوظون في قائمتك:" else "Bénéficiaires enregistrés dans votre liste :",
                                    fontFamily = CairoFontFamily,
                                    color = PayDzSlateText,
                                    fontWeight = FontWeight.SemiBold,
                                    fontSize = 11.sp
                                )
                                FlowRow(
                                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                                    verticalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    matchingSuggestions.take(6).forEach { contact ->
                                        val isSelected = recipientInput == contact.ripNumber
                                        Surface(
                                            shape = RoundedCornerShape(14.dp),
                                            color = if (isSelected) Color(0xFFDCEAFE) else Color.White,
                                            shadowElevation = if (isSelected) 3.dp else 1.dp,
                                            modifier = Modifier
                                                .border(
                                                    width = if (isSelected) 1.5.dp else 1.dp,
                                                    color = if (isSelected) PayDzBluePrimary else PayDzInputBorder,
                                                    shape = RoundedCornerShape(14.dp)
                                                )
                                                .clickable {
                                                    recipientName = contact.fullName
                                                    recipientInput = contact.ripNumber
                                                    localValidationError = null
                                                }
                                        ) {
                                            Row(
                                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                                verticalAlignment = Alignment.CenterVertically,
                                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                                            ) {
                                                Column {
                                                    Text(
                                                        text = contact.fullName,
                                                        fontFamily = CairoFontFamily,
                                                        color = PayDzNavyText,
                                                        fontWeight = FontWeight.Bold,
                                                        fontSize = 12.sp
                                                    )
                                                    Text(
                                                        text = "RIP: **** ${contact.ripNumber.takeLast(4)} • ${contact.tagLabel}",
                                                        fontFamily = CairoFontFamily,
                                                        color = PayDzSlateText,
                                                        fontWeight = FontWeight.Normal,
                                                        fontSize = 10.sp
                                                    )
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }

                        // Input 0: اسم المستفيد / المرسل
                        PayDzPillInputField(
                            value = recipientName,
                            onValueChange = {
                                recipientName = it
                                localValidationError = null
                            },
                            placeholder = if (isReceiveMode) {
                                if (isAr) "اسم المرسل" else "Nom de l'expéditeur"
                            } else {
                                if (isAr) "اسم المستفيد" else "Nom du bénéficiaire"
                            },
                            leadingIcon = Icons.Default.PersonOutline,
                            testTag = "transfer_recipient_name_input"
                        )

                         // Input 1: رقم المستفيد / المرسل (20-digit RIP or Phone Number)
                        PayDzPillInputField(
                            value = recipientInput,
                            onValueChange = {
                                val digits = it.filter { ch -> ch.isDigit() }.take(20)
                                recipientInput = digits
                                localValidationError = null
                                // Auto-fill name ONLY if RIP matches a saved beneficiary in user's private list
                                mySavedContacts.firstOrNull { c -> c.ripNumber == digits }?.let { matched ->
                                    recipientName = matched.fullName
                                }
                            },
                            placeholder = if (isAr) "رقم المستفيد (20 رقم RIP)" else "Numéro RIP (20 chiffres)",
                            leadingIcon = Icons.Default.PersonOutline,
                            keyboardType = KeyboardType.Number,
                            testTag = "transfer_rip_input"
                        )

                        // Auto-save RIP toggle + Quick Save button
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp),
                                modifier = Modifier.weight(1f)
                            ) {
                                Switch(
                                    checked = saveRipAutomatically,
                                    onCheckedChange = { saveRipAutomatically = it },
                                    colors = SwitchDefaults.colors(
                                        checkedThumbColor = Color.White,
                                        checkedTrackColor = PayDzBluePrimary
                                    )
                                )
                                Text(
                                    text = if (isAr) "حفظ الـ RIP تلقائياً في الاقتراحات" else "Enregistrer le RIP automatiquement",
                                    fontFamily = CairoFontFamily,
                                    color = PayDzNavyText,
                                    fontWeight = FontWeight.SemiBold,
                                    fontSize = 11.sp
                                )
                            }

                            Surface(
                                shape = RoundedCornerShape(50),
                                color = Color(0xFFE6F0FF),
                                modifier = Modifier
                                    .clickable {
                                        val raw = recipientInput.filter { it.isDigit() }
                                        val normalizedRip = if (raw.length == 20) raw else ("00799999" + raw.padStart(12, '0')).take(20)
                                        val cleanName = recipientName.trim().ifBlank { "مستفيد PayDz" }
                                        onQuickSaveBeneficiary(cleanName, normalizedRip)
                                        savedToastMessage = "تم حفظ $cleanName ($normalizedRip) في اقتراحات الـ RIP!"
                                    }
                                    .testTag("quick_save_rip_button")
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.BookmarkAdd,
                                        contentDescription = null,
                                        tint = PayDzBluePrimary,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Text(
                                        text = if (isAr) "حفظ الـ RIP" else "Enregistrer RIP",
                                        fontFamily = CairoFontFamily,
                                        color = PayDzBluePrimary,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 11.sp
                                    )
                                }
                            }
                        }

                        if (savedToastMessage != null) {
                            Text(
                                text = savedToastMessage.orEmpty(),
                                fontFamily = CairoFontFamily,
                                color = PayDzGreen,
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp
                            )
                        }

                        // Input 2: المبلغ (د.ج)
                        PayDzPillInputField(
                            value = amountText,
                            onValueChange = {
                                amountText = it.filter { ch -> ch.isDigit() }
                                localValidationError = null
                            },
                            placeholder = if (isAr) "المبلغ (د.ج)" else "Montant (DZD)",
                            leadingIcon = Icons.Default.MonetizationOn,
                            leadingIconTint = PayDzGold,
                            keyboardType = KeyboardType.Number,
                            testTag = "transfer_amount_input"
                        )

                        // Live Insufficient Wallet Balance Warning (Rule requested by User)
                        if (isExceedingWalletBalance) {
                            Surface(
                                shape = RoundedCornerShape(16.dp),
                                color = PayDzRedSoft,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .border(1.dp, PayDzRed, RoundedCornerShape(16.dp))
                                    .testTag("insufficient_balance_warning")
                            ) {
                                Text(
                                    text = if (isAr) {
                                        "لا يمكنك إرسال هذا المبلغ! المبلغ المطلوب أكبر من رصيدك الحالي لأن محفظتك لا تحتوي على هذا المال الكبير (رصيدك المتاح: $formattedBalance.00 دج)."
                                    } else {
                                        "Impossible d'envoyer ce montant ! Votre portefeuille ne contient pas ce montant élevé (Solde : $formattedBalance.00 DZD)."
                                    },
                                    fontFamily = CairoFontFamily,
                                    color = PayDzRed,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 12.sp,
                                    modifier = Modifier.padding(12.dp)
                                )
                            }
                        }

                        // Input 3: ملاحظة (اختيارية)
                        PayDzPillInputField(
                            value = note,
                            onValueChange = { note = it },
                            placeholder = if (isAr) "ملاحظة (اختيارية)" else "Note (optionnel)",
                            leadingIcon = Icons.Default.Edit,
                            testTag = "transfer_note_input"
                        )

                        val displayedError = localValidationError ?: externalError
                        if (!displayedError.isNullOrBlank() && !isExceedingWalletBalance) {
                            Surface(
                                shape = RoundedCornerShape(16.dp),
                                color = PayDzRedSoft,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .border(1.dp, PayDzRed, RoundedCornerShape(16.dp))
                            ) {
                                Text(
                                    text = displayedError,
                                    fontFamily = CairoFontFamily,
                                    color = PayDzRed,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 12.sp,
                                    modifier = Modifier.padding(12.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(4.dp))

                        // Bottom Button: التالي ->
                        PayDzPrimaryPillButton(
                            text = if (isReceiveMode) {
                                if (isAr) "استلام المبلغ فوراً" else "Recevoir maintenant"
                            } else {
                                if (isAr) "التالي" else "Suivant"
                            },
                            isLoading = isSubmitting,
                            showRightArrow = true,
                            onClick = {
                                val amt = amountText.toLongOrNull() ?: 0L
                                if (amt < 100L) {
                                    localValidationError = "الحد الأدنى للتحويل هو 100 د.ج"
                                    return@PayDzPrimaryPillButton
                                }
                                if (!isReceiveMode && (amt > currentBalanceDzd || amt + transferFee > currentBalanceDzd)) {
                                    localValidationError = "لا يمكنك إرسال هذا المبلغ لأن محفظتك لا تحتوي على هذا المال الكبير! رصيدك المتاح حالياً هو $formattedBalance.00 دج فقط."
                                    return@PayDzPrimaryPillButton
                                }
                                val rawInputDigits = recipientInput.filter { it.isDigit() }
                                    .ifBlank { "00799999004125898421" }
                                if (isReceiveMode) {
                                    val normalizedRip = if (rawInputDigits.length == 20) rawInputDigits else ("00799999" + rawInputDigits.padStart(12, '0')).take(20)
                                    onReceiveSubmit(
                                        recipientName.ifBlank { "فاطمة بن علي" },
                                        normalizedRip,
                                        amt,
                                        note
                                    )
                                } else {
                                    onSubmit(
                                        recipientName.ifBlank { "أحمد محمد" },
                                        rawInputDigits,
                                        amt,
                                        note,
                                        saveRipAutomatically
                                    )
                                }
                            },
                            testTag = "confirm_transfer_button"
                        )
                    }
                }
            }
        }
    }
}

/**
 * SCREEN 8 OF PAYDZ MOCKUP: تمت العملية بنجاح! (Operation Success Full Screen)
 */
@Composable
fun OperationSuccessFullScreenDialog(
    summary: OperationSuccessSummary,
    isAr: Boolean,
    onDismiss: () -> Unit
) {
    BackHandler { onDismiss() }
    val context = LocalContext.current
    val formattedAmount = NumberFormat.getNumberInstance(Locale.US).format(summary.amountDzd)

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(
            usePlatformDefaultWidth = false,
            decorFitsSystemWindows = false
        )
    ) {
        CompositionLocalProvider(LocalLayoutDirection provides if (isAr) LayoutDirection.Rtl else LayoutDirection.Ltr) {
            Surface(
                modifier = Modifier.fillMaxSize(),
                color = PayDzBgSoft
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .windowInsetsPadding(WindowInsets.safeDrawing)
                        .verticalScroll(rememberScrollState())
                        .padding(horizontal = 24.dp, vertical = 24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.SpaceBetween
                ) {
                    Spacer(modifier = Modifier.height(8.dp))

                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(10.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        PayDzSuccessConfettiHeroIllustration()

                        Text(
                            text = summary.title,
                            color = PayDzNavyText,
                            fontFamily = CairoFontFamily,
                            fontSize = 21.sp,
                            fontWeight = FontWeight.Bold,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.testTag("operation_success_title")
                        )

                        Text(
                            text = summary.subtitle,
                            color = PayDzSlateText,
                            fontFamily = CairoFontFamily,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Normal,
                            textAlign = TextAlign.Center
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        // White Summary Card matching Screen 8
                        Surface(
                            shape = PayDzDesignTokens.CardShapeLarge,
                            color = Color.White,
                            shadowElevation = 4.dp,
                            modifier = Modifier
                                .fillMaxWidth()
                                .border(1.dp, PayDzInputBorder, PayDzDesignTokens.CardShapeLarge)
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 20.dp, vertical = 18.dp),
                                verticalArrangement = Arrangement.spacedBy(14.dp)
                            ) {
                                SuccessSummaryRow(
                                    label = if (isAr) "المبلغ" else "Montant",
                                    value = "$formattedAmount.00 دج",
                                    isBoldValue = true
                                )
                                HorizontalDivider(color = Color(0xFFE9EFF8))
                                SuccessSummaryRow(
                                    label = if (isAr) "إلى" else "Bénéficiaire",
                                    value = summary.recipientLabel,
                                    isBoldValue = true
                                )
                                HorizontalDivider(color = Color(0xFFE9EFF8))
                                SuccessSummaryRow(
                                    label = if (isAr) "رقم العملية" else "N° Opération",
                                    value = summary.referenceCode,
                                    isBoldValue = false
                                )
                                HorizontalDivider(color = Color(0xFFE9EFF8))
                                SuccessSummaryRow(
                                    label = if (isAr) "التاريخ" else "Date",
                                    value = summary.formattedDate,
                                    isBoldValue = false
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(18.dp))

                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        // Primary Button matching Screen 8: حسناً <- (with '<-' arrow on the left side)
                        Button(
                            onClick = onDismiss,
                            shape = PayDzDesignTokens.PillShape,
                            colors = ButtonDefaults.buttonColors(
                                containerColor = PayDzBluePrimary,
                                contentColor = Color.White
                            ),
                            elevation = ButtonDefaults.buttonElevation(defaultElevation = 6.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(PayDzDesignTokens.PrimaryButtonHeight)
                                .testTag("operation_success_ok_button")
                        ) {
                            Box(modifier = Modifier.fillMaxWidth()) {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                    contentDescription = null,
                                    tint = Color.White,
                                    modifier = Modifier
                                        .align(Alignment.CenterEnd)
                                        .size(22.dp)
                                )
                                Text(
                                    text = if (isAr) "حسناً" else "Terminé",
                                    fontFamily = CairoFontFamily,
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.align(Alignment.Center)
                                )
                            }
                        }

                        // Share Receipt Button (مشاركة الإيصال)
                        OutlinedButton(
                            onClick = {
                                val shareText = "PayDz — ${summary.title}\nالمبلغ: $formattedAmount.00 دج\nإلى: ${summary.recipientLabel}\nرقم العملية: ${summary.referenceCode}\nالتاريخ: ${summary.formattedDate}"
                                val intent = Intent(Intent.ACTION_SEND).apply {
                                    type = "text/plain"
                                    putExtra(Intent.EXTRA_TEXT, shareText)
                                }
                                context.startActivity(Intent.createChooser(intent, "مشاركة الإيصال"))
                            },
                            shape = PayDzDesignTokens.PillShape,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(48.dp)
                                .testTag("operation_success_share_button")
                        ) {
                            Icon(Icons.Default.Share, contentDescription = null, tint = PayDzBluePrimary, modifier = Modifier.size(18.dp))
                            Spacer(Modifier.width(8.dp))
                            Text(
                                text = if (isAr) "مشاركة الإيصال" else "Partager le reçu",
                                color = PayDzBluePrimary,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun SuccessSummaryRow(
    label: String,
    value: String,
    isBoldValue: Boolean
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            color = PayDzSlateText,
            fontFamily = CairoFontFamily,
            fontSize = 12.sp,
            fontWeight = FontWeight.Normal
        )
        Text(
            text = value,
            color = PayDzNavyText,
            fontFamily = CairoFontFamily,
            fontSize = if (isBoldValue) 14.sp else 12.sp,
            fontWeight = if (isBoldValue) FontWeight.Bold else FontWeight.SemiBold
        )
    }
}

/**
 * الحوالات المالية / سحب بدون بطاقة (Money Orders & Cardless ATM)
 */
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun CardlessAtmDialog(
    isSubmitting: Boolean,
    isAr: Boolean,
    onDismiss: () -> Unit,
    onSubmit: (amountDzd: Long, recipientPhone: String) -> Unit
) {
    BackHandler { onDismiss() }
    val amounts = listOf(2000L, 5000L, 10000L, 20000L)
    var selectedAmount by remember { mutableLongStateOf(5000L) }
    var recipientPhone by remember { mutableStateOf("0612345678") }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth(0.94f)
                .padding(vertical = 16.dp),
            shape = RoundedCornerShape(26.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White)
        ) {
            Column(
                modifier = Modifier
                    .padding(22.dp)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = if (isAr) "الحوالات المالية والسحب السريع" else "Mandats & Retrait Sans Carte",
                        style = MaterialTheme.typography.titleLarge,
                        color = PayDzNavyText,
                        fontWeight = FontWeight.ExtraBold
                    )
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, contentDescription = "Close", tint = PayDzNavyText)
                    }
                }

                Text(
                    text = if (isAr) "اختر المبلغ لإصدار حوالة مالية فورية أو رمز سحب نقدي:" else "Choisissez le montant du mandat instantané :",
                    color = PayDzSlateText
                )

                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    amounts.forEach { amt ->
                        FilterChip(
                            selected = selectedAmount == amt,
                            onClick = { selectedAmount = amt },
                            label = { Text("$amt دج") }
                        )
                    }
                }

                PayDzPillInputField(
                    value = recipientPhone,
                    onValueChange = { recipientPhone = it },
                    placeholder = if (isAr) "رقم هاتف المستفيد" else "Téléphone du bénéficiaire",
                    leadingIcon = Icons.Default.PhoneAndroid,
                    keyboardType = KeyboardType.Phone
                )

                PayDzPrimaryPillButton(
                    text = if (isAr) "إرسال الحوالة" else "Envoyer le mandat",
                    isLoading = isSubmitting,
                    onClick = { onSubmit(selectedAmount, recipientPhone) },
                    testTag = "confirm_cardless_button"
                )
            }
        }
    }
}

@Composable
fun QrPaymentDialog(
    isSubmitting: Boolean,
    isAr: Boolean,
    onDismiss: () -> Unit,
    onSubmit: (merchantName: String, merchantCode: String, amountDzd: Long) -> Unit
) {
    BackHandler { onDismiss() }
    var merchantName by remember { mutableStateOf("متجر PayDz المعتمد") }
    var merchantCode by remember { mutableStateOf("PDZ-QR-4567") }
    var amountText by remember { mutableStateOf("1500") }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Card(
            modifier = Modifier.fillMaxWidth(0.94f),
            shape = RoundedCornerShape(26.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White)
        ) {
            Column(
                modifier = Modifier
                    .padding(22.dp)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = if (isAr) "الدفع السريع عبر رمز QR" else "Paiement QR PayDz",
                        style = MaterialTheme.typography.titleLarge,
                        color = PayDzNavyText,
                        fontWeight = FontWeight.ExtraBold
                    )
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, contentDescription = "Close")
                    }
                }

                Box(
                    modifier = Modifier
                        .size(110.dp)
                        .clip(RoundedCornerShape(22.dp))
                        .background(PayDzBgSoft),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        Icons.Default.QrCode2,
                        contentDescription = null,
                        tint = PayDzBluePrimary,
                        modifier = Modifier.size(76.dp)
                    )
                }

                PayDzPillInputField(
                    value = merchantName,
                    onValueChange = { merchantName = it },
                    placeholder = if (isAr) "اسم التاجر أو نقطة البيع" else "Nom du commerçant",
                    leadingIcon = Icons.Default.Edit
                )

                PayDzPillInputField(
                    value = amountText,
                    onValueChange = { amountText = it.filter { ch -> ch.isDigit() } },
                    placeholder = if (isAr) "المبلغ (د.ج)" else "Montant (DZD)",
                    leadingIcon = Icons.Default.MonetizationOn,
                    leadingIconTint = PayDzGold,
                    keyboardType = KeyboardType.Number
                )

                PayDzPrimaryPillButton(
                    text = if (isAr) "تأكيد الدفع الفوري" else "Payer maintenant",
                    isLoading = isSubmitting,
                    onClick = {
                        val amt = amountText.toLongOrNull() ?: 0L
                        onSubmit(merchantName, merchantCode, amt)
                    },
                    testTag = "confirm_qr_button"
                )
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun SandboxRechargeDialog(
    isSubmitting: Boolean,
    isAr: Boolean,
    onDismiss: () -> Unit,
    onSubmit: (amountDzd: Long) -> Unit
) {
    BackHandler { onDismiss() }
    val presets = listOf(10000L, 25000L, 50000L, 100000L)
    var selected by remember { mutableLongStateOf(25000L) }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(26.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White)
        ) {
            Column(
                modifier = Modifier.padding(22.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                Text(
                    text = if (isAr) "شحن محفظة PayDz (Sandbox)" else "Recharger le portefeuille PayDz",
                    style = MaterialTheme.typography.titleLarge,
                    color = PayDzNavyText,
                    fontWeight = FontWeight.ExtraBold
                )
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    presets.forEach { amt ->
                        FilterChip(
                            selected = selected == amt,
                            onClick = { selected = amt },
                            label = { Text("+ $amt دج") }
                        )
                    }
                }
                PayDzPrimaryPillButton(
                    text = if (isAr) "إيداع فوري ($selected دج)" else "Créditer ($selected DZD)",
                    isLoading = isSubmitting,
                    onClick = { onSubmit(selected) },
                    testTag = "confirm_recharge_button"
                )
            }
        }
    }
}

@Composable
fun AddBeneficiaryDialog(
    isSubmitting: Boolean,
    isAr: Boolean,
    onDismiss: () -> Unit,
    onSubmit: (fullName: String, ripNumber: String, nickname: String) -> Unit
) {
    BackHandler { onDismiss() }
    var fullName by remember { mutableStateOf("") }
    var ripNumber by remember { mutableStateOf("00799999004125898421") }
    var nickname by remember { mutableStateOf("") }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(26.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White)
        ) {
            Column(
                modifier = Modifier.padding(22.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = if (isAr) "إضافة مستفيد جديد" else "Nouveau bénéficiaire",
                        style = MaterialTheme.typography.titleLarge,
                        color = PayDzNavyText,
                        fontWeight = FontWeight.ExtraBold
                    )
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, contentDescription = "Close")
                    }
                }
                PayDzPillInputField(
                    value = fullName,
                    onValueChange = { fullName = it },
                    placeholder = if (isAr) "الاسم الكامل للمستفيد" else "Nom complet",
                    leadingIcon = Icons.Default.PersonOutline,
                    testTag = "ben_name_input"
                )
                PayDzPillInputField(
                    value = ripNumber,
                    onValueChange = { ripNumber = it.filter { ch -> ch.isDigit() }.take(20) },
                    placeholder = if (isAr) "رقم المستفيد (20 رقماً RIP)" else "RIP (20 chiffres)",
                    leadingIcon = Icons.Default.MonetizationOn,
                    leadingIconTint = PayDzGold,
                    keyboardType = KeyboardType.Number,
                    testTag = "ben_rip_input"
                )
                PayDzPillInputField(
                    value = nickname,
                    onValueChange = { nickname = it },
                    placeholder = if (isAr) "الصفة (صديق / عائلة / عمل)" else "Libellé",
                    leadingIcon = Icons.Default.Edit,
                    testTag = "ben_nick_input"
                )
                PayDzPrimaryPillButton(
                    text = if (isAr) "حفظ المستفيد" else "Enregistrer",
                    isLoading = isSubmitting,
                    onClick = { onSubmit(fullName, ripNumber, nickname) },
                    testTag = "confirm_add_ben_button"
                )
            }
        }
    }
}

@Composable
fun NewSupportTicketDialog(
    isSubmitting: Boolean,
    isAr: Boolean,
    onDismiss: () -> Unit,
    onSubmit: (subject: String, category: String, priority: String, message: String) -> Unit
) {
    BackHandler { onDismiss() }
    var subject by remember { mutableStateOf("") }
    var message by remember { mutableStateOf("") }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Card(
            modifier = Modifier.fillMaxWidth(0.94f),
            shape = RoundedCornerShape(26.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White)
        ) {
            Column(
                modifier = Modifier
                    .padding(22.dp)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = if (isAr) "تذكرة دعم فني فوري 24/7" else "Ticket Support 24/7",
                        style = MaterialTheme.typography.titleLarge,
                        color = PayDzNavyText,
                        fontWeight = FontWeight.ExtraBold
                    )
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, contentDescription = "Close")
                    }
                }
                OutlinedTextField(
                    value = subject,
                    onValueChange = { subject = it },
                    label = { Text(if (isAr) "عنوان التذكرة" else "Sujet") },
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = message,
                    onValueChange = { message = it },
                    label = { Text(if (isAr) "تفاصيل الاستفسار" else "Détails") },
                    minLines = 3,
                    modifier = Modifier.fillMaxWidth()
                )
                PayDzPrimaryPillButton(
                    text = if (isAr) "إرسال التذكرة" else "Envoyer",
                    isLoading = isSubmitting,
                    onClick = { onSubmit(subject, "transfer_issue", "high", message) },
                    testTag = "confirm_ticket_button"
                )
            }
        }
    }
}

@Composable
fun DigitalReceiptDialog(
    transaction: WalletTransaction,
    isAr: Boolean,
    onDismiss: () -> Unit
) {
    BackHandler { onDismiss() }
    val context = LocalContext.current
    val clipboard = LocalClipboardManager.current
    val dateStr = remember(transaction.createdAt) {
        transaction.createdAt?.toDate()?.let {
            SimpleDateFormat("dd/MM/yyyy · HH:mm", Locale.US).format(it)
        } ?: "07/11/2026 · 14:30"
    }
    val formattedAmt = NumberFormat.getNumberInstance(Locale.US).format(transaction.amountDzd)

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth(0.92f)
                .padding(vertical = 16.dp),
            shape = RoundedCornerShape(28.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White)
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(64.dp)
                        .clip(CircleShape)
                        .background(PayDzGreen.copy(alpha = 0.14f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        Icons.Default.CheckCircle,
                        contentDescription = null,
                        tint = PayDzGreen,
                        modifier = Modifier.size(40.dp)
                    )
                }

                Text(
                    text = if (isAr) "تمت العملية بنجاح!" else "Opération réussie !",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.ExtraBold,
                    color = PayDzNavyText
                )

                Text(
                    text = "$formattedAmt.00 دج",
                    fontSize = 26.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = PayDzBluePrimary
                )

                HorizontalDivider()

                SuccessSummaryRow(
                    label = if (isAr) "العملية" else "Opération",
                    value = transaction.title,
                    isBoldValue = true
                )
                SuccessSummaryRow(
                    label = if (isAr) "إلى / من" else "Destinataire",
                    value = transaction.recipientName,
                    isBoldValue = true
                )
                SuccessSummaryRow(
                    label = if (isAr) "المرجع" else "Référence",
                    value = transaction.referenceCode,
                    isBoldValue = false
                )
                SuccessSummaryRow(
                    label = if (isAr) "التاريخ" else "Date",
                    value = dateStr,
                    isBoldValue = false
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    OutlinedButton(
                        onClick = { clipboard.setText(AnnotatedString(transaction.referenceCode)) },
                        shape = RoundedCornerShape(50),
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(Icons.Default.ContentCopy, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(Modifier.width(6.dp))
                        Text(if (isAr) "نسخ" else "Copier")
                    }
                    Button(
                        onClick = {
                            val shareText = "PayDz — ${transaction.title}\nالمبلغ: $formattedAmt.00 دج\nالتاريخ: $dateStr\nالمرجع: ${transaction.referenceCode}"
                            val intent = Intent(Intent.ACTION_SEND).apply {
                                type = "text/plain"
                                putExtra(Intent.EXTRA_TEXT, shareText)
                            }
                            context.startActivity(Intent.createChooser(intent, "Share Receipt"))
                        },
                        shape = RoundedCornerShape(50),
                        colors = ButtonDefaults.buttonColors(containerColor = PayDzBluePrimary),
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(Icons.Default.Share, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(Modifier.width(6.dp))
                        Text(if (isAr) "مشاركة" else "Partager")
                    }
                }

                Text(
                    text = if (isAr) "حسناً" else "Fermer",
                    color = PayDzSlateText,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier
                        .clickable { onDismiss() }
                        .padding(top = 6.dp)
                )
            }
        }
    }
}

@Composable
fun AccountProfileDialog(
    account: WalletAccount?,
    isSubmitting: Boolean,
    isAr: Boolean,
    onDismiss: () -> Unit,
    onUpdateProfile: (fullName: String, phoneNumber: String) -> Unit,
    onOpenSandboxRecharge: () -> Unit
) {
    BackHandler { onDismiss() }
    val clipboard = LocalClipboardManager.current
    var fullName by remember(account?.fullName) { mutableStateOf(account?.fullName ?: "أحمد محمد") }
    var phoneNumber by remember(account?.phoneNumber) { mutableStateOf(account?.phoneNumber ?: "0612345678") }
    val rip = account?.ripNumber ?: "00799999004125898421"
    val ccp = account?.ccpAccount ?: "1234568421 / 84"
    val balanceFormatted = NumberFormat.getNumberInstance(Locale.US).format(account?.balanceDzd ?: 125750L)

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        CompositionLocalProvider(LocalLayoutDirection provides if (isAr) LayoutDirection.Rtl else LayoutDirection.Ltr) {
            Card(
                modifier = Modifier
                    .fillMaxWidth(0.94f)
                    .padding(vertical = 16.dp),
                shape = RoundedCornerShape(28.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White)
            ) {
                Column(
                    modifier = Modifier
                        .padding(22.dp)
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = if (isAr) "حسابي والمحفظة التجريبية" else "Mon Compte PayDz",
                            style = MaterialTheme.typography.titleLarge,
                            color = PayDzNavyText,
                            fontWeight = FontWeight.ExtraBold
                        )
                        IconButton(onClick = onDismiss) {
                            Icon(Icons.Default.Close, contentDescription = "Close", tint = PayDzNavyText)
                        }
                    }

                    Surface(
                        shape = RoundedCornerShape(20.dp),
                        color = PayDzBgSoft,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Text(
                                text = if (isAr) "الرصيد التجريبي الحالي: $balanceFormatted.00 دج" else "Solde: $balanceFormatted.00 DZD",
                                color = PayDzBluePrimary,
                                fontWeight = FontWeight.ExtraBold,
                                fontSize = 16.sp
                            )
                            Text(
                                text = "CCP: $ccp",
                                color = PayDzSlateText,
                                fontFamily = RobotoMonoFontFamily,
                                fontSize = 13.sp
                            )
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "RIP: $rip",
                                    color = PayDzNavyText,
                                    fontFamily = RobotoMonoFontFamily,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.sp
                                )
                                IconButton(
                                    onClick = { clipboard.setText(AnnotatedString(rip)) },
                                    modifier = Modifier.size(28.dp)
                                ) {
                                    Icon(Icons.Default.ContentCopy, contentDescription = "Copy RIP", tint = PayDzBluePrimary, modifier = Modifier.size(18.dp))
                                }
                            }
                        }
                    }

                    PayDzPillInputField(
                        value = fullName,
                        onValueChange = { fullName = it },
                        placeholder = if (isAr) "الاسم الكامل" else "Nom complet",
                        leadingIcon = Icons.Default.PersonOutline,
                        testTag = "profile_name_input"
                    )

                    PayDzPillInputField(
                        value = phoneNumber,
                        onValueChange = { phoneNumber = it },
                        placeholder = if (isAr) "رقم الهاتف" else "Téléphone",
                        leadingIcon = Icons.Default.PhoneAndroid,
                        keyboardType = KeyboardType.Phone,
                        testTag = "profile_phone_input"
                    )

                    PayDzPrimaryPillButton(
                        text = if (isAr) "حفظ بيانات الحساب" else "Enregistrer le profil",
                        isLoading = isSubmitting,
                        showRightArrow = false,
                        onClick = { onUpdateProfile(fullName, phoneNumber) },
                        testTag = "confirm_profile_button"
                    )

                    OutlinedButton(
                        onClick = onOpenSandboxRecharge,
                        shape = PayDzDesignTokens.PillShape,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp)
                            .testTag("profile_recharge_wallet_button")
                    ) {
                        Text(
                            text = if (isAr) "+ شحن المحفظة بأموال تجريبية" else "+ Recharger le portefeuille Sandbox",
                            color = PayDzGreen,
                            fontWeight = FontWeight.ExtraBold
                        )
                    }
                }
            }
        }
    }
}

/**
 * مركز الإشعارات الفورية (Notifications Center Full Screen / Sheet)
 */
@Composable
fun NotificationsCenterDialog(
    notifications: List<PayDzAppNotification>,
    isAr: Boolean,
    onMarkAllRead: () -> Unit,
    onOpenTransfer: () -> Unit,
    onDismiss: () -> Unit
) {
    BackHandler { onDismiss() }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(
            usePlatformDefaultWidth = false,
            decorFitsSystemWindows = false
        )
    ) {
        CompositionLocalProvider(LocalLayoutDirection provides if (isAr) LayoutDirection.Rtl else LayoutDirection.Ltr) {
            Surface(
                modifier = Modifier.fillMaxSize(),
                color = PayDzBgSoft
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .windowInsetsPadding(WindowInsets.safeDrawing)
                        .verticalScroll(rememberScrollState())
                        .padding(bottom = 24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    PayDzFullScreenHeader(
                        title = if (isAr) "مركز الإشعارات الفورية" else "Centre de Notifications",
                        onBack = {
                            onMarkAllRead()
                            onDismiss()
                        },
                        testTag = "close_notifications_dialog"
                    )

                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 20.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Surface(
                            shape = RoundedCornerShape(18.dp),
                            color = Color(0xFFE6F0FF),
                            modifier = Modifier
                                .fillMaxWidth()
                                .border(1.dp, PayDzBluePrimary.copy(alpha = 0.25f), RoundedCornerShape(18.dp))
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 16.dp, vertical = 12.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.NotificationsActive,
                                        contentDescription = null,
                                        tint = PayDzBluePrimary,
                                        modifier = Modifier.size(24.dp)
                                    )
                                    Column {
                                        Text(
                                            text = if (isAr) "الإشعارات الفورية مفعلة" else "Notifications en direct activées",
                                            fontFamily = CairoFontFamily,
                                            color = PayDzNavyText,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 13.sp
                                        )
                                        Text(
                                            text = if (isAr) "يصلك تنبيه فوري عند استلام أو إرسال أي مبلغ" else "Alertes instantanées à chaque transfert",
                                            fontFamily = CairoFontFamily,
                                            color = PayDzSlateText,
                                            fontWeight = FontWeight.Normal,
                                            fontSize = 11.sp
                                        )
                                    }
                                }
                                Text(
                                    text = if (isAr) "تحديد كمقروء" else "Tout lire",
                                    fontFamily = CairoFontFamily,
                                    color = PayDzBluePrimary,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 12.sp,
                                    modifier = Modifier
                                        .clickable { onMarkAllRead() }
                                        .padding(4.dp)
                                )
                            }
                        }

                        if (notifications.isEmpty()) {
                            Surface(
                                shape = RoundedCornerShape(22.dp),
                                color = Color.White,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 16.dp)
                            ) {
                                Column(
                                    modifier = Modifier.padding(24.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    verticalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Text(
                                        text = if (isAr) "لا توجد إشعارات جديدة حالياً" else "Aucune notification",
                                        fontFamily = CairoFontFamily,
                                        color = PayDzNavyText,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 14.sp
                                    )
                                }
                            }
                        } else {
                            notifications.forEach { item ->
                                val badgeVariant = when (item.type) {
                                    "incoming" -> "transfer_green"
                                    "outgoing" -> "bolt_orange"
                                    else -> "transfer_blue"
                                }
                                Surface(
                                    shape = RoundedCornerShape(20.dp),
                                    color = if (item.isUnread) Color.White else Color(0xFFF8FAFC),
                                    shadowElevation = if (item.isUnread) 3.dp else 1.dp,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .border(
                                            width = if (item.isUnread) 1.5.dp else 1.dp,
                                            color = if (item.isUnread) PayDzBluePrimary.copy(alpha = 0.4f) else PayDzInputBorder,
                                            shape = RoundedCornerShape(20.dp)
                                        )
                                ) {
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(14.dp),
                                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        PayDzTransactionBadgeIcon(
                                            variant = badgeVariant,
                                            size = 42.dp
                                        )
                                        Column(
                                            modifier = Modifier.weight(1f),
                                            verticalArrangement = Arrangement.spacedBy(4.dp)
                                        ) {
                                            Text(
                                                text = item.title,
                                                fontFamily = CairoFontFamily,
                                                color = PayDzNavyText,
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 13.sp
                                            )
                                            Text(
                                                text = item.message,
                                                fontFamily = CairoFontFamily,
                                                color = PayDzSlateText,
                                                fontWeight = FontWeight.Normal,
                                                fontSize = 12.sp
                                            )
                                            Text(
                                                text = item.timestampLabel,
                                                fontFamily = PoppinsFontFamily,
                                                color = PayDzSlateText,
                                                fontWeight = FontWeight.Normal,
                                                fontSize = 10.sp
                                            )
                                        }
                                    }
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        PayDzPrimaryPillButton(
                            text = if (isAr) "إجراء تحويل أو استلام أموال" else "Effectuer un transfert",
                            isLoading = false,
                            showRightArrow = true,
                            onClick = {
                                onMarkAllRead()
                                onOpenTransfer()
                            },
                            testTag = "notifications_open_transfer_button"
                        )
                    }
                }
            }
        }
    }
}

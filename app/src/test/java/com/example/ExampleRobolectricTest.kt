package com.example

import android.content.Context
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollTo
import androidx.test.core.app.ApplicationProvider
import com.example.data.model.WalletAccount
import com.example.data.model.WalletTransaction
import com.example.ui.AllServicesScreen
import com.example.ui.BillPaymentDialog
import com.example.ui.HomeDashboardScreen
import com.example.ui.IdoomInternetDialog
import com.example.ui.MobileTopUpDialog
import com.example.ui.OperationSuccessFullScreenDialog
import com.example.ui.OperationSuccessSummary
import com.example.ui.RipTransferDialog
import com.example.ui.TransactionsHistoryScreen
import com.example.ui.theme.ApplicationTheme
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36], qualifiers = "w411dp-h915dp-xxhdpi")
class ExampleRobolectricTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun `read string from context`() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val appName = context.getString(R.string.app_name)
        assertEquals("DinarPay", appName)
    }

    @Test
    fun `splash screen renders PayDz elements and dismisses on click`() {
        var splashDismissed by mutableStateOf(false)
        composeTestRule.setContent {
            ApplicationTheme {
                PayDzSplashScreen(onDismissSplash = { splashDismissed = true })
            }
        }
        composeTestRule.waitForIdle()
        composeTestRule.onNodeWithTag("paydz_splash_screen").assertIsDisplayed()
        composeTestRule.onNodeWithTag("paydz_splash_screen").performClick()
        composeTestRule.waitForIdle()
        assertTrue(splashDismissed)
    }

    @Test
    fun `home dashboard renders PayDz balance card and quick actions`() {
        val sampleAccount = WalletAccount(
            userId = "u1",
            fullName = "أحمد محمد",
            ripNumber = "00799999001234568421",
            cardMaskedNumber = "**** 4567",
            cardExpiry = "12/28",
            balanceDzd = 125750L
        )
        var privacy by mutableStateOf(false)
        composeTestRule.setContent {
            ApplicationTheme {
                HomeDashboardScreen(
                    account = sampleAccount,
                    transactions = emptyList(),
                    beneficiaries = emptyList(),
                    isPrivacyMode = privacy,
                    isAr = true,
                    onTogglePrivacy = { privacy = !privacy },
                    onOpenModal = {},
                    onSelectTransaction = {},
                    onDeleteBeneficiary = {}
                )
            }
        }
        composeTestRule.waitForIdle()
        composeTestRule.onNodeWithText("أحمد محمد").assertIsDisplayed()
        composeTestRule.onNodeWithTag("wallet_balance_text").assertIsDisplayed()
        composeTestRule.onNodeWithTag("action_mobile_topup").assertIsDisplayed()
        composeTestRule.onNodeWithTag("action_rip_transfer").assertIsDisplayed()
        composeTestRule.onNodeWithTag("action_cardless_atm").assertIsDisplayed()
        composeTestRule.onNodeWithTag("action_bill_payment").assertIsDisplayed()
        composeTestRule.onNodeWithTag("privacy_mode_toggle").performClick()
        composeTestRule.waitForIdle()
        assertTrue(privacy)
    }

    @Test
    fun `all services and transaction history screens render and filter`() {
        val sampleTxs = listOf(
            WalletTransaction(
                id = "1",
                type = "deposit",
                title = "من: فاطمة",
                recipientName = "فاطمة",
                amountDzd = 10000L,
                referenceCode = "PDZ-1",
                note = "07/11"
            ),
            WalletTransaction(
                id = "2",
                type = "transfer",
                title = "إلى: أحمد محمد",
                recipientName = "أحمد محمد",
                amountDzd = 5000L,
                referenceCode = "PDZ-2",
                note = "07/11"
            )
        )
        var showHistory by mutableStateOf(false)
        var selectedCat by mutableStateOf("all")
        composeTestRule.setContent {
            ApplicationTheme {
                if (!showHistory) {
                    AllServicesScreen(
                        isAr = true,
                        onBack = { showHistory = true },
                        onOpenModal = {},
                        onNavigateToCardTab = {},
                        onNavigateToBeneficiaries = {},
                        onNavigateToSupport = {}
                    )
                } else {
                    TransactionsHistoryScreen(
                        transactions = sampleTxs,
                        searchQuery = "",
                        selectedCategory = selectedCat,
                        isPrivacyMode = false,
                        isAr = true,
                        onSearchChange = {},
                        onCategoryChange = { selectedCat = it },
                        onSelectTransaction = {}
                    )
                }
            }
        }
        composeTestRule.waitForIdle()
        composeTestRule.onNodeWithText("جميع الخدمات").assertIsDisplayed()
        composeTestRule.onNodeWithTag("service_mobile_topup").assertIsDisplayed()
        composeTestRule.onNodeWithTag("service_idoom_internet").assertIsDisplayed()
        composeTestRule.onNodeWithTag("services_back_button").performClick()
        composeTestRule.waitForIdle()

        composeTestRule.onNodeWithText("سجل المعاملات").assertIsDisplayed()
        composeTestRule.onNodeWithText("من: فاطمة").assertIsDisplayed()
        composeTestRule.onNodeWithTag("filter_chip_incoming").performClick()
        composeTestRule.waitForIdle()
        assertEquals("incoming", selectedCat)
    }

    @Test
    fun `topup, bill, idoom, transfer, and success screens work interactively`() {
        var step by mutableIntStateOf(0)
        composeTestRule.setContent {
            ApplicationTheme {
                when (step) {
                    0 -> MobileTopUpDialog(
                        isSubmitting = false,
                        isAr = true,
                        onDismiss = {},
                        onSubmit = { _, _, _, _ -> step = 1 }
                    )
                    1 -> BillPaymentDialog(
                        isSubmitting = false,
                        isAr = true,
                        onDismiss = {},
                        onSubmit = { _, _, _, _ -> step = 2 }
                    )
                    2 -> IdoomInternetDialog(
                        isSubmitting = false,
                        isAr = true,
                        onDismiss = {},
                        onSubmit = { _, _, _, _ -> step = 3 }
                    )
                    3 -> RipTransferDialog(
                        beneficiaries = emptyList(),
                        isSubmitting = false,
                        isAr = true,
                        onDismiss = {},
                        onSubmit = { _, _, _, _, _ -> step = 4 }
                    )
                    4 -> OperationSuccessFullScreenDialog(
                        summary = OperationSuccessSummary(
                            title = "تمت العملية بنجاح!",
                            subtitle = "تم تحويل المبلغ بنجاح",
                            amountDzd = 5000L,
                            recipientLabel = "أحمد محمد",
                            formattedDate = "07/11/2026 · 14:30",
                            referenceCode = "PDZ-784512"
                        ),
                        isAr = true,
                        onDismiss = { step = 5 }
                    )
                }
            }
        }
        composeTestRule.waitForIdle()
        // Step 0: Mobile TopUp
        composeTestRule.onNodeWithText("شحن الهاتف").assertIsDisplayed()
        composeTestRule.onNodeWithTag("operator_djezzy").performClick()
        composeTestRule.onNodeWithTag("confirm_topup_button").performScrollTo().performClick()
        composeTestRule.waitForIdle()

        // Step 1: Bill Payment
        composeTestRule.onNodeWithText("دفع الفواتير").assertIsDisplayed()
        composeTestRule.onNodeWithTag("confirm_bill_button").performScrollTo().performClick()
        composeTestRule.waitForIdle()

        // Step 2: Idoom Internet
        composeTestRule.onNodeWithText("إنترنت منزلي Idoom").assertIsDisplayed()
        composeTestRule.onNodeWithTag("confirm_idoom_button").performScrollTo().performClick()
        composeTestRule.waitForIdle()

        // Step 3: Money Transfer
        composeTestRule.onNodeWithText("تحويل الأموال").assertIsDisplayed()
        composeTestRule.onNodeWithTag("confirm_transfer_button").performScrollTo().performClick()
        composeTestRule.waitForIdle()

        // Step 4: Operation Success
        composeTestRule.onNodeWithTag("operation_success_title").assertIsDisplayed()
        composeTestRule.onNodeWithTag("operation_success_ok_button").performScrollTo().performClick()
        composeTestRule.waitForIdle()
        assertEquals(5, step)
    }
}

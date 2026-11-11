package com.example.ui

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.model.Beneficiary
import com.example.data.model.PublicDirectoryPeer
import com.example.data.model.SupportTicket
import com.example.data.model.WalletAccount
import com.example.data.model.WalletTransaction
import com.example.data.repository.DinarPayRepository
import com.example.util.PayDzAppNotification
import com.google.firebase.firestore.FirebaseFirestoreException
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

sealed interface UiState<out T> {
    data object Loading : UiState<Nothing>
    data class Success<T>(val data: T) : UiState<T>
    data class Error(val message: String) : UiState<Nothing>
}

enum class AppLanguage(val code: String, val isRtl: Boolean) {
    AR("ar", true),
    FR("fr", false)
}

enum class ActiveModal {
    NONE,
    RIP_TRANSFER,
    MOBILE_TOPUP,
    BILL_PAYMENT,
    IDOOM_INTERNET,
    CARDLESS_ATM,
    QR_PAYMENT,
    RECHARGE_SANDBOX,
    ADD_BENEFICIARY,
    NEW_SUPPORT_TICKET,
    ACCOUNT_PROFILE,
    NOTIFICATIONS_CENTER,
    OPERATION_SUCCESS
}

data class OperationSuccessSummary(
    val title: String,
    val subtitle: String,
    val amountDzd: Long,
    val recipientLabel: String,
    val formattedDate: String,
    val referenceCode: String
)

class DinarPayViewModel(
    private val repository: DinarPayRepository,
    private val currentUserId: String,
    userDisplayName: String,
    userEmailOrPhone: String
) : ViewModel() {

    private val _language = MutableStateFlow(AppLanguage.AR)
    val language: StateFlow<AppLanguage> = _language.asStateFlow()

    private val _isPrivacyMode = MutableStateFlow(false)
    val isPrivacyMode: StateFlow<Boolean> = _isPrivacyMode.asStateFlow()

    private val _activeModal = MutableStateFlow(ActiveModal.NONE)
    val activeModal: StateFlow<ActiveModal> = _activeModal.asStateFlow()

    private val _lastOperationSuccess = MutableStateFlow<OperationSuccessSummary?>(null)
    val lastOperationSuccess: StateFlow<OperationSuccessSummary?> = _lastOperationSuccess.asStateFlow()

    private val _selectedReceipt = MutableStateFlow<WalletTransaction?>(null)
    val selectedReceipt: StateFlow<WalletTransaction?> = _selectedReceipt.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _selectedCategory = MutableStateFlow("all")
    val selectedCategory: StateFlow<String> = _selectedCategory.asStateFlow()

    private val _isSubmitting = MutableStateFlow(false)
    val isSubmitting: StateFlow<Boolean> = _isSubmitting.asStateFlow()

    private val _statusBanner = MutableStateFlow<String?>(null)
    val statusBanner: StateFlow<String?> = _statusBanner.asStateFlow()

    private val _errorBanner = MutableStateFlow<String?>(null)
    val errorBanner: StateFlow<String?> = _errorBanner.asStateFlow()

    private val _notifications = MutableStateFlow<List<PayDzAppNotification>>(emptyList())
    val notifications: StateFlow<List<PayDzAppNotification>> = _notifications.asStateFlow()

    private val _systemNotificationEvent = MutableStateFlow<PayDzAppNotification?>(null)
    val systemNotificationEvent: StateFlow<PayDzAppNotification?> = _systemNotificationEvent.asStateFlow()

    val peersState: StateFlow<List<PublicDirectoryPeer>> =
        repository.observePublicPeers(currentUserId)
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(STOP_TIMEOUT_MILLIS),
                initialValue = emptyList()
            )

    val accountState: StateFlow<UiState<WalletAccount?>> = repository.observeAccount(currentUserId)
        .map<WalletAccount?, UiState<WalletAccount?>> { UiState.Success(it) }
        .catch { error ->
            val firestoreError = generateSequence(error) { it.cause }
                .filterIsInstance<FirebaseFirestoreException>()
                .firstOrNull()
            if (firestoreError == null && error is CancellationException) throw error
            val rootError = firestoreError ?: error
            Log.w(TAG, "Error observing account", rootError)
            emit(UiState.Error(rootError.message ?: "تعذر تحميل بيانات الحساب"))
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(STOP_TIMEOUT_MILLIS),
            initialValue = UiState.Loading
        )

    val transactionsState: StateFlow<UiState<List<WalletTransaction>>> =
        repository.observeTransactions(currentUserId)
            .map<List<WalletTransaction>, UiState<List<WalletTransaction>>> { UiState.Success(it) }
            .catch { error ->
                val firestoreError = generateSequence(error) { it.cause }
                    .filterIsInstance<FirebaseFirestoreException>()
                    .firstOrNull()
                if (firestoreError == null && error is CancellationException) throw error
                val rootError = firestoreError ?: error
                Log.w(TAG, "Error observing transactions", rootError)
                emit(UiState.Error(rootError.message ?: "تعذر تحميل سجل المعاملات"))
            }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(STOP_TIMEOUT_MILLIS),
                initialValue = UiState.Loading
            )

    val beneficiariesState: StateFlow<UiState<List<Beneficiary>>> =
        repository.observeBeneficiaries(currentUserId)
            .map<List<Beneficiary>, UiState<List<Beneficiary>>> { UiState.Success(it) }
            .catch { error ->
                val firestoreError = generateSequence(error) { it.cause }
                    .filterIsInstance<FirebaseFirestoreException>()
                    .firstOrNull()
                if (firestoreError == null && error is CancellationException) throw error
                val rootError = firestoreError ?: error
                Log.w(TAG, "Error observing beneficiaries", rootError)
                emit(UiState.Error(rootError.message ?: "تعذر تحميل قائمة المستفيدين"))
            }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(STOP_TIMEOUT_MILLIS),
                initialValue = UiState.Loading
            )

    val supportTicketsState: StateFlow<UiState<List<SupportTicket>>> =
        repository.observeSupportTickets(currentUserId)
            .map<List<SupportTicket>, UiState<List<SupportTicket>>> { UiState.Success(it) }
            .catch { error ->
                val firestoreError = generateSequence(error) { it.cause }
                    .filterIsInstance<FirebaseFirestoreException>()
                    .firstOrNull()
                if (firestoreError == null && error is CancellationException) throw error
                val rootError = firestoreError ?: error
                Log.w(TAG, "Error observing support tickets", rootError)
                emit(UiState.Error(rootError.message ?: "تعذر تحميل تذاكر الدعم"))
            }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(STOP_TIMEOUT_MILLIS),
                initialValue = UiState.Loading
            )

    init {
        viewModelScope.launch {
            val initResult = repository.ensureAccountInitialized(
                displayName = userDisplayName.ifBlank { "أحمد محمد" },
                phoneNumber = userEmailOrPhone.ifBlank { "0612345678" }
            )
            initResult.onSuccess { account ->
                pushNotification(
                    title = "مرحباً بك في PayDz 🎉",
                    message = "حسابك (${account.fullName}) نشط ومؤمّن. الإشعارات الفورية مفعلة لاستقبال وإرسال الأموال.",
                    type = "welcome"
                )
                claimAndNotifyPendingTransfers()
                // Real-time listener for incoming P2P transfers from any user in the app
                viewModelScope.launch {
                    repository.observePendingIncomingTransfers(currentUserId, account.ripNumber)
                        .collectLatest { pendingList ->
                            if (pendingList.isNotEmpty()) {
                                claimAndNotifyPendingTransfers()
                            }
                        }
                }
            }
        }
    }

    private suspend fun claimAndNotifyPendingTransfers() {
        repository.claimPendingP2PTransfers().onSuccess { claimedTransfers ->
            if (claimedTransfers.isNotEmpty()) {
                claimedTransfers.forEach { tx ->
                    val formatted = NumberFormat.getNumberInstance(Locale.US).format(tx.amountDzd)
                    pushNotification(
                        title = "وصلتك حوالة مالية واردة 💰",
                        message = "استلمت +$formatted.00 دج من ${tx.senderName} وتم حفظ رقم RIP الخاص به في اقتراحاتك.",
                        type = "incoming",
                        amountDzd = tx.amountDzd
                    )
                }
                _statusBanner.value = "تم استلام ${claimedTransfers.size} حوالة واردة وإضافتها إلى رصيدك تلقائياً!"
            }
        }
    }

    fun pushNotification(
        title: String,
        message: String,
        type: String,
        amountDzd: Long = 0L
    ) {
        val item = PayDzAppNotification(
            id = "notif_${System.currentTimeMillis()}_${(100..999).random()}",
            title = title,
            message = message,
            timestampLabel = currentFormattedTimestamp(),
            type = type,
            amountDzd = amountDzd,
            isUnread = true
        )
        _notifications.value = listOf(item) + _notifications.value.take(29)
        _systemNotificationEvent.value = item
    }

    fun consumeSystemNotificationEvent() {
        _systemNotificationEvent.value = null
    }

    fun markAllNotificationsRead() {
        _notifications.value = _notifications.value.map { it.copy(isUnread = false) }
    }

    private fun currentFormattedTimestamp(): String {
        val sdf = SimpleDateFormat("dd/MM/yyyy · HH:mm", Locale.US)
        return sdf.format(Date())
    }

    fun toggleLanguage() {
        _language.value = if (_language.value == AppLanguage.AR) AppLanguage.FR else AppLanguage.AR
    }

    fun togglePrivacyMode() {
        _isPrivacyMode.value = !_isPrivacyMode.value
    }

    fun openModal(modal: ActiveModal) {
        _errorBanner.value = null
        _activeModal.value = modal
    }

    fun closeModal() {
        _activeModal.value = ActiveModal.NONE
        _lastOperationSuccess.value = null
    }

    fun selectReceipt(tx: WalletTransaction?) {
        _selectedReceipt.value = tx
    }

    fun updateSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun updateCategoryFilter(category: String) {
        _selectedCategory.value = category
    }

    fun clearBanners() {
        _statusBanner.value = null
        _errorBanner.value = null
    }

    fun submitRipTransfer(
        recipientName: String,
        recipientRip: String,
        amountDzd: Long,
        note: String,
        saveBeneficiary: Boolean
    ) {
        if (_isSubmitting.value) return
        _isSubmitting.value = true
        _errorBanner.value = null
        viewModelScope.launch {
            val res = repository.sendRipTransfer(
                recipientName = recipientName,
                recipientRip = recipientRip,
                amountDzd = amountDzd,
                note = note,
                saveBeneficiary = saveBeneficiary
            )
            _isSubmitting.value = false
            res.onSuccess { refCode ->
                val cleanRecipient = recipientName.ifBlank { "أحمد محمد" }
                val formatted = NumberFormat.getNumberInstance(Locale.US).format(amountDzd)
                pushNotification(
                    title = "تم إرسال التحويل بنجاح ✅",
                    message = "تم تحويل $formatted.00 دج إلى $cleanRecipient وحفظ رقم RIP للتعامل السريع.",
                    type = "outgoing",
                    amountDzd = amountDzd
                )
                _lastOperationSuccess.value = OperationSuccessSummary(
                    title = "تمت العملية بنجاح!",
                    subtitle = "تم تحويل المبلغ بنجاح",
                    amountDzd = amountDzd,
                    recipientLabel = cleanRecipient,
                    formattedDate = currentFormattedTimestamp(),
                    referenceCode = refCode
                )
                _activeModal.value = ActiveModal.OPERATION_SUCCESS
                _statusBanner.value = null
            }.onFailure { err ->
                _errorBanner.value = err.localizedMessage ?: "فشل تنفيذ التحويل"
            }
        }
    }

    fun submitReceiveP2PTransfer(
        senderName: String,
        senderRip: String,
        amountDzd: Long,
        note: String
    ) {
        if (_isSubmitting.value) return
        _isSubmitting.value = true
        _errorBanner.value = null
        viewModelScope.launch {
            claimAndNotifyPendingTransfers()
            val res = repository.receiveP2PTransfer(
                senderName = senderName,
                senderRip = senderRip,
                amountDzd = amountDzd,
                note = note,
                saveBeneficiary = true
            )
            _isSubmitting.value = false
            res.onSuccess { refCode ->
                val cleanSender = senderName.ifBlank { "فاطمة بن علي" }
                val formatted = NumberFormat.getNumberInstance(Locale.US).format(amountDzd)
                pushNotification(
                    title = "وصلتك حوالة مالية واردة 💰",
                    message = "تم استلام +$formatted.00 دج من $cleanSender وحفظ رقم RIP في جهات التعامل.",
                    type = "incoming",
                    amountDzd = amountDzd
                )
                _lastOperationSuccess.value = OperationSuccessSummary(
                    title = "تمت العملية بنجاح!",
                    subtitle = "تم استلام التحويل المالي وإضافته إلى محفظتك",
                    amountDzd = amountDzd,
                    recipientLabel = "من: $cleanSender",
                    formattedDate = currentFormattedTimestamp(),
                    referenceCode = refCode
                )
                _activeModal.value = ActiveModal.OPERATION_SUCCESS
                _statusBanner.value = null
            }.onFailure { err ->
                _errorBanner.value = err.localizedMessage ?: "فشل استلام التحويل"
            }
        }
    }

    fun submitUpdateProfile(fullName: String, phoneNumber: String) {
        if (_isSubmitting.value) return
        _isSubmitting.value = true
        _errorBanner.value = null
        viewModelScope.launch {
            val res = repository.updateUserProfile(fullName, phoneNumber)
            _isSubmitting.value = false
            res.onSuccess {
                _activeModal.value = ActiveModal.NONE
                _statusBanner.value = "تم تحديث بيانات حسابك ($fullName) بنجاح"
            }.onFailure { err ->
                _errorBanner.value = err.localizedMessage ?: "تعذر تحديث بيانات الحساب"
            }
        }
    }

    fun submitMobileTopUp(
        operatorName: String,
        phoneNumber: String,
        planTitle: String,
        amountDzd: Long
    ) {
        if (_isSubmitting.value) return
        _isSubmitting.value = true
        _errorBanner.value = null
        viewModelScope.launch {
            val res = repository.payMobileTopUp(
                operatorName = operatorName,
                phoneNumber = phoneNumber,
                planTitle = planTitle,
                amountDzd = amountDzd
            )
            _isSubmitting.value = false
            res.onSuccess { refCode ->
                _lastOperationSuccess.value = OperationSuccessSummary(
                    title = "تمت العملية بنجاح!",
                    subtitle = "تم شحن رصيد الهاتف ($operatorName) بنجاح",
                    amountDzd = amountDzd,
                    recipientLabel = "$operatorName • $phoneNumber",
                    formattedDate = currentFormattedTimestamp(),
                    referenceCode = refCode
                )
                _activeModal.value = ActiveModal.OPERATION_SUCCESS
            }.onFailure { err ->
                _errorBanner.value = err.localizedMessage ?: "فشل تعبئة الرصيد"
            }
        }
    }

    fun submitUtilityBill(
        billerName: String,
        contractNumber: String,
        invoiceRef: String,
        amountDzd: Long
    ) {
        if (_isSubmitting.value) return
        _isSubmitting.value = true
        _errorBanner.value = null
        viewModelScope.launch {
            val res = repository.payUtilityBill(
                billerName = billerName,
                contractNumber = contractNumber,
                invoiceRef = invoiceRef,
                amountDzd = amountDzd
            )
            _isSubmitting.value = false
            res.onSuccess { refCode ->
                _lastOperationSuccess.value = OperationSuccessSummary(
                    title = "تمت العملية بنجاح!",
                    subtitle = "تم تسديد فاتورة $billerName بنجاح",
                    amountDzd = amountDzd,
                    recipientLabel = "$billerName (#$contractNumber)",
                    formattedDate = currentFormattedTimestamp(),
                    referenceCode = refCode
                )
                _activeModal.value = ActiveModal.OPERATION_SUCCESS
            }.onFailure { err ->
                _errorBanner.value = err.localizedMessage ?: "فشل تسديد الفاتورة"
            }
        }
    }

    fun submitCardlessWithdrawal(amountDzd: Long, recipientPhone: String) {
        if (_isSubmitting.value) return
        _isSubmitting.value = true
        _errorBanner.value = null
        viewModelScope.launch {
            val res = repository.createCardlessWithdrawal(amountDzd, recipientPhone)
            _isSubmitting.value = false
            res.onSuccess { refCode ->
                _lastOperationSuccess.value = OperationSuccessSummary(
                    title = "تمت العملية بنجاح!",
                    subtitle = "تم إصدار رمز الحوالة / السحب الفوري بنجاح",
                    amountDzd = amountDzd,
                    recipientLabel = "حوالة سريعة • $recipientPhone",
                    formattedDate = currentFormattedTimestamp(),
                    referenceCode = refCode
                )
                _activeModal.value = ActiveModal.OPERATION_SUCCESS
            }.onFailure { err ->
                _errorBanner.value = err.localizedMessage ?: "فشل إصدار السحب بدون بطاقة"
            }
        }
    }

    fun submitQrPayment(merchantName: String, merchantCode: String, amountDzd: Long) {
        if (_isSubmitting.value) return
        _isSubmitting.value = true
        _errorBanner.value = null
        viewModelScope.launch {
            val res = repository.payMerchantQr(merchantName, merchantCode, amountDzd)
            _isSubmitting.value = false
            res.onSuccess { refCode ->
                _lastOperationSuccess.value = OperationSuccessSummary(
                    title = "تمت العملية بنجاح!",
                    subtitle = "تم الدفع عبر رمز QR بنجاح",
                    amountDzd = amountDzd,
                    recipientLabel = merchantName,
                    formattedDate = currentFormattedTimestamp(),
                    referenceCode = refCode
                )
                _activeModal.value = ActiveModal.OPERATION_SUCCESS
            }.onFailure { err ->
                _errorBanner.value = err.localizedMessage ?: "فشل الدفع عبر QR"
            }
        }
    }

    fun submitSandboxRecharge(amountDzd: Long) {
        if (_isSubmitting.value) return
        _isSubmitting.value = true
        _errorBanner.value = null
        viewModelScope.launch {
            val res = repository.rechargeWalletBalance(amountDzd)
            _isSubmitting.value = false
            res.onSuccess { newBal ->
                _activeModal.value = ActiveModal.NONE
                _statusBanner.value = "تمت تغذية الرصيد بنجاح • الرصيد الجديد: $newBal دج"
            }.onFailure { err ->
                _errorBanner.value = err.localizedMessage ?: "فشل تغذية الرصيد"
            }
        }
    }

    fun updateCardSettings(
        account: WalletAccount,
        cardFrozen: Boolean = account.cardFrozen,
        onlinePaymentsEnabled: Boolean = account.onlinePaymentsEnabled,
        contactlessEnabled: Boolean = account.contactlessEnabled,
        biometricEnabled: Boolean = account.biometricEnabled,
        twoFactorEnabled: Boolean = account.twoFactorEnabled,
        dailyTransferLimitDzd: Long = account.dailyTransferLimitDzd,
        dailyWithdrawalLimitDzd: Long = account.dailyWithdrawalLimitDzd
    ) {
        viewModelScope.launch {
            val res = repository.updateCardAndSecuritySettings(
                cardFrozen = cardFrozen,
                onlinePaymentsEnabled = onlinePaymentsEnabled,
                contactlessEnabled = contactlessEnabled,
                biometricEnabled = biometricEnabled,
                twoFactorEnabled = twoFactorEnabled,
                dailyTransferLimitDzd = dailyTransferLimitDzd,
                dailyWithdrawalLimitDzd = dailyWithdrawalLimitDzd
            )
            res.onSuccess {
                _statusBanner.value = "تم تحديث إعدادات بطاقة PayDz والأمان بنجاح"
            }.onFailure { err ->
                _errorBanner.value = err.localizedMessage ?: "تعذر تحديث إعدادات البطاقة"
            }
        }
    }

    fun addBeneficiary(fullName: String, ripNumber: String, nickname: String) {
        if (_isSubmitting.value) return
        _isSubmitting.value = true
        viewModelScope.launch {
            val res = repository.addBeneficiary(fullName, ripNumber, nickname, isFavorite = true)
            _isSubmitting.value = false
            res.onSuccess {
                _activeModal.value = ActiveModal.NONE
                _statusBanner.value = "تمت إضافة المستفيد ($fullName) إلى القائمة المحفوظة"
            }.onFailure { err ->
                _errorBanner.value = err.localizedMessage ?: "فشل إضافة المستفيد"
            }
        }
    }

    fun deleteBeneficiary(id: String) {
        viewModelScope.launch {
            repository.deleteBeneficiary(id)
        }
    }

    fun submitSupportTicket(
        subject: String,
        category: String,
        priority: String,
        message: String
    ) {
        if (_isSubmitting.value) return
        _isSubmitting.value = true
        viewModelScope.launch {
            val res = repository.createSupportTicket(subject, category, priority, message)
            _isSubmitting.value = false
            res.onSuccess {
                _activeModal.value = ActiveModal.NONE
                _statusBanner.value = "تم تسجيل تذكرة الدعم والرد الفوري من مركز 24/7"
            }.onFailure { err ->
                _errorBanner.value = err.localizedMessage ?: "تعذر إرسال تذكرة الدعم"
            }
        }
    }

    fun resolveTicket(ticketId: String) {
        viewModelScope.launch {
            repository.resolveSupportTicket(ticketId)
        }
    }

    private companion object {
        const val TAG = "DinarPayVM"
        const val STOP_TIMEOUT_MILLIS = 5000L
    }
}

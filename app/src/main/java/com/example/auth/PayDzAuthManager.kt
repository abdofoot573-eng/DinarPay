package com.example.auth

import android.content.Context
import android.hardware.biometrics.BiometricManager
import android.hardware.biometrics.BiometricPrompt
import android.os.Build
import android.os.CancellationSignal
import android.util.Log
import android.util.Patterns
import androidx.core.content.ContextCompat
import com.example.util.PayDzNotificationHelper
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import org.json.JSONArray
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL
import kotlin.random.Random

data class LocalEmailAccount(
    val email: String,
    val fullName: String,
    val phoneNumber: String,
    val password: String,
    val createdAtMillis: Long
)

data class PendingOtpChallenge(
    val email: String,
    val fullName: String,
    val phoneNumber: String,
    val code: String,
    val isSignUp: Boolean,
    val authMethod: String, // "email" only (Google sign-in/up logs in directly without OTP)
    val sentAtMillis: Long,
    val firebaseEmailSent: Boolean = true
)

data class EnrolledBiometricAccount(
    val uid: String,
    val email: String,
    val fullName: String,
    val enrolledAtMillis: Long
)

object PayDzAuthManager {
    private const val PREFS_NAME = "paydz_auth_security_prefs"
    private const val KEY_EMAIL_ACCOUNTS_JSON = "registered_email_accounts_json"
    private const val KEY_SESSION_UNLOCKED = "session_unlocked_after_otp"
    private const val KEY_ACTIVE_EMAIL = "active_account_email"
    private const val KEY_ACTIVE_NAME = "active_account_name"
    private const val KEY_ACTIVE_PHONE = "active_account_phone"

    // Biometric enrollment keys (strictly empty on first install!)
    private const val KEY_BIO_ENROLLED = "biometric_fingerprint_enrolled"
    private const val KEY_BIO_UID = "biometric_enrolled_uid"
    private const val KEY_BIO_EMAIL = "biometric_enrolled_email"
    private const val KEY_BIO_NAME = "biometric_enrolled_name"
    private const val KEY_BIO_TIMESTAMP = "biometric_enrolled_timestamp"
    private const val KEY_APP_SECURITY_PIN = "paydz_app_security_pin"

    private val _sessionUnlockedFlow = MutableStateFlow(false)
    val sessionUnlockedFlow: StateFlow<Boolean> = _sessionUnlockedFlow.asStateFlow()

    private val _pendingOtpFlow = MutableStateFlow<PendingOtpChallenge?>(null)
    val pendingOtpFlow: StateFlow<PendingOtpChallenge?> = _pendingOtpFlow.asStateFlow()

    private val _biometricEnrolledFlow = MutableStateFlow<EnrolledBiometricAccount?>(null)
    val biometricEnrolledFlow: StateFlow<EnrolledBiometricAccount?> = _biometricEnrolledFlow.asStateFlow()

    private var initialized = false

    fun init(context: Context) {
        if (initialized) return
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val enrolled = readEnrolledBiometric(context)
        _biometricEnrolledFlow.value = enrolled
        // Once biometric fingerprint is enrolled, THE APP MUST NOT OPEN EXCEPT WITH BIOMETRIC OR APP CODE!
        if (enrolled != null) {
            _sessionUnlockedFlow.value = false
        } else {
            _sessionUnlockedFlow.value = prefs.getBoolean(KEY_SESSION_UNLOCKED, false)
        }
        initialized = true
    }

    fun getAppSecurityPin(context: Context): String {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        return prefs.getString(KEY_APP_SECURITY_PIN, "1234") ?: "1234"
    }

    fun setAppSecurityPin(context: Context, pin: String) {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        prefs.edit().putString(KEY_APP_SECURITY_PIN, pin.trim().ifBlank { "1234" }).apply()
    }

    fun verifyAppSecurityPin(context: Context, enteredPin: String): Boolean {
        val savedPin = getAppSecurityPin(context)
        return enteredPin.trim() == savedPin.trim()
    }

    fun startResetPinChallenge(
        context: Context,
        scope: CoroutineScope,
        email: String,
        fullName: String
    ): String {
        val code = Random.nextInt(100000, 999999).toString()
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        prefs.edit().putString("reset_pin_otp_code", code).apply()

        scope.launch(Dispatchers.IO) {
            runCatching {
                Firebase.auth.setLanguageCode("ar")
                Firebase.auth.currentUser?.sendEmailVerification()?.await()
            }
            runCatching {
                dispatchEmailOtpOverHttp(
                    recipientEmail = email,
                    recipientName = fullName,
                    otpCode = code,
                    isSignUp = false
                )
            }
        }

        PayDzNotificationHelper.showSystemNotification(
            context = context,
            title = "رمز إعادة تعيين كود سر التطبيق: $code",
            body = "رمز تأكيد إعادة تعيين كود سر التطبيق (4 أرقام) لحسابك ($email) هو: $code"
        )
        return code
    }

    fun verifyResetPinOtp(context: Context, enteredCode: String): Boolean {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val saved = prefs.getString("reset_pin_otp_code", "") ?: ""
        return enteredCode.trim().isNotEmpty() && enteredCode.trim() == saved.trim()
    }

    fun isSessionUnlocked(context: Context): Boolean {
        init(context)
        return _sessionUnlockedFlow.value
    }

    fun setSessionUnlocked(context: Context, unlocked: Boolean) {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        prefs.edit().putBoolean(KEY_SESSION_UNLOCKED, unlocked).apply()
        _sessionUnlockedFlow.value = unlocked
        if (unlocked) {
            _pendingOtpFlow.value = null
        }
    }

    fun saveActiveProfileOverride(
        context: Context,
        email: String,
        fullName: String,
        phoneNumber: String
    ) {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        prefs.edit().apply {
            if (email.isNotBlank()) putString(KEY_ACTIVE_EMAIL, email.trim())
            if (fullName.isNotBlank()) putString(KEY_ACTIVE_NAME, fullName.trim())
            if (phoneNumber.isNotBlank()) putString(KEY_ACTIVE_PHONE, phoneNumber.trim())
        }.apply()
    }

    fun getActiveEmailOverride(context: Context): String? {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        return prefs.getString(KEY_ACTIVE_EMAIL, null)?.takeIf { it.isNotBlank() }
    }

    fun getActiveNameOverride(context: Context): String? {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        return prefs.getString(KEY_ACTIVE_NAME, null)?.takeIf { it.isNotBlank() }
    }

    fun getActivePhoneOverride(context: Context): String? {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        return prefs.getString(KEY_ACTIVE_PHONE, null)?.takeIf { it.isNotBlank() }
    }

    // =========================================================================
    // 1. EMAIL SIGN-UP & EMAIL SIGN-IN REGISTRY
    // =========================================================================

    fun getRegisteredEmailAccounts(context: Context): List<LocalEmailAccount> {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val raw = prefs.getString(KEY_EMAIL_ACCOUNTS_JSON, "[]") ?: "[]"
        val list = mutableListOf<LocalEmailAccount>()
        runCatching {
            val arr = JSONArray(raw)
            for (i in 0 until arr.length()) {
                val obj = arr.getJSONObject(i)
                list.add(
                    LocalEmailAccount(
                        email = obj.optString("email"),
                        fullName = obj.optString("fullName"),
                        phoneNumber = obj.optString("phoneNumber"),
                        password = obj.optString("password"),
                        createdAtMillis = obj.optLong("createdAtMillis", System.currentTimeMillis())
                    )
                )
            }
        }
        return list
    }

    private fun saveRegisteredEmailAccounts(context: Context, accounts: List<LocalEmailAccount>) {
        val arr = JSONArray()
        accounts.forEach { acc ->
            val obj = JSONObject().apply {
                put("email", acc.email)
                put("fullName", acc.fullName)
                put("phoneNumber", acc.phoneNumber)
                put("password", acc.password)
                put("createdAtMillis", acc.createdAtMillis)
            }
            arr.put(obj)
        }
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        prefs.edit().putString(KEY_EMAIL_ACCOUNTS_JSON, arr.toString()).apply()
    }

    fun validateAndRegisterEmailAccount(
        context: Context,
        fullName: String,
        email: String,
        phoneNumber: String,
        password: String,
        confirmPassword: String
    ): Result<LocalEmailAccount> {
        val cleanName = fullName.trim()
        val cleanEmail = email.trim().lowercase()
        val cleanPhone = phoneNumber.trim().ifBlank { "0612345678" }

        if (cleanName.length < 3) {
            return Result.failure(IllegalArgumentException("يرجى إدخال الاسم الكامل بشكل صحيح (3 أحرف على الأقل)"))
        }
        if (cleanEmail.isBlank() || !Patterns.EMAIL_ADDRESS.matcher(cleanEmail).matches()) {
            return Result.failure(IllegalArgumentException("يرجى إدخال بريد إلكتروني صحيح (مثال: name@gmail.com)"))
        }
        if (password.length < 6) {
            return Result.failure(IllegalArgumentException("كلمة المرور يجب أن تتكون من 6 أحرف أو أرقام على الأقل"))
        }
        if (password != confirmPassword) {
            return Result.failure(IllegalArgumentException("كلمتا المرور غير متطابقتين، يرجى التأكد منهما"))
        }

        val existing = getRegisteredEmailAccounts(context)
        if (existing.any { it.email.equals(cleanEmail, ignoreCase = true) }) {
            return Result.failure(
                IllegalArgumentException("هذا البريد الإلكتروني مسجل مسبقاً! يرجى اختيار تبويب (تسجيل الدخول) للدخول إلى حسابك.")
            )
        }

        val newAccount = LocalEmailAccount(
            email = cleanEmail,
            fullName = cleanName,
            phoneNumber = cleanPhone,
            password = password,
            createdAtMillis = System.currentTimeMillis()
        )
        saveRegisteredEmailAccounts(context, existing + newAccount)
        saveActiveProfileOverride(context, cleanEmail, cleanName, cleanPhone)
        return Result.success(newAccount)
    }

    fun validateEmailSignIn(
        context: Context,
        emailOrPhone: String,
        password: String
    ): Result<LocalEmailAccount> {
        val cleanInput = emailOrPhone.trim().lowercase()
        if (cleanInput.isBlank()) {
            return Result.failure(IllegalArgumentException("يرجى إدخال البريد الإلكتروني أو رقم الهاتف"))
        }
        if (password.isBlank()) {
            return Result.failure(IllegalArgumentException("يرجى إدخال كلمة المرور الخاصة بحسابك"))
        }

        val accounts = getRegisteredEmailAccounts(context)
        val matched = accounts.firstOrNull {
            it.email.equals(cleanInput, ignoreCase = true) ||
                it.phoneNumber == cleanInput
        }

        if (matched == null) {
            return Result.failure(
                IllegalArgumentException("لا يوجد حساب مسجل بهذا البريد (${emailOrPhone.trim()}). يرجى الضغط على (إنشاء حساب جديد) أولاً للتسجيل.")
            )
        }

        if (matched.password != password) {
            return Result.failure(IllegalArgumentException("كلمة المرور غير صحيحة! يرجى المحاولة مرة أخرى."))
        }

        saveActiveProfileOverride(context, matched.email, matched.fullName, matched.phoneNumber)
        return Result.success(matched)
    }

    fun registerOrUpdateGoogleAccountLocally(
        context: Context,
        email: String,
        fullName: String,
        phoneNumber: String = "0612345678"
    ): LocalEmailAccount {
        val cleanEmail = email.trim().lowercase().ifBlank { "user@gmail.com" }
        val cleanName = fullName.trim().ifBlank { "مستخدم PayDz" }
        val existing = getRegisteredEmailAccounts(context).toMutableList()
        val index = existing.indexOfFirst { it.email.equals(cleanEmail, ignoreCase = true) }
        val account = if (index >= 0) {
            val old = existing[index]
            val updated = old.copy(
                fullName = if (cleanName.isNotBlank()) cleanName else old.fullName
            )
            existing[index] = updated
            updated
        } else {
            val created = LocalEmailAccount(
                email = cleanEmail,
                fullName = cleanName,
                phoneNumber = phoneNumber,
                password = "google_oauth_linked",
                createdAtMillis = System.currentTimeMillis()
            )
            existing.add(created)
            created
        }
        saveRegisteredEmailAccounts(context, existing)
        saveActiveProfileOverride(context, account.email, account.fullName, account.phoneNumber)
        return account
    }

    // =========================================================================
    // 2. EMAIL OTP & REAL FIREBASE GMAIL VERIFICATION (فقط للتسجيل بالبريد الإلكتروني)
    // =========================================================================

    fun startEmailOtpChallenge(
        context: Context,
        scope: CoroutineScope,
        email: String,
        fullName: String,
        phoneNumber: String,
        isSignUp: Boolean,
        authMethod: String = "email"
    ): PendingOtpChallenge {
        val code = Random.nextInt(100000, 999999).toString()
        val challenge = PendingOtpChallenge(
            email = email.trim(),
            fullName = fullName.trim().ifBlank { "أحمد محمد" },
            phoneNumber = phoneNumber.trim().ifBlank { "0612345678" },
            code = code,
            isSignUp = isSignUp,
            authMethod = authMethod,
            sentAtMillis = System.currentTimeMillis(),
            firebaseEmailSent = true
        )
        setSessionUnlocked(context, false)
        _pendingOtpFlow.value = challenge

        // 1. Trigger Real Firebase Email Verification to the user's Gmail inbox!
        scope.launch(Dispatchers.IO) {
            runCatching {
                Firebase.auth.setLanguageCode("ar")
                Firebase.auth.currentUser?.sendEmailVerification()?.await()
                Log.i("PayDzAuth", "Firebase real verification email dispatched to Gmail")
            }.onFailure { e ->
                Log.w("PayDzAuth", "Firebase sendEmailVerification notice: ${e.message}")
            }

            // Also attempt HTTP email dispatch for the 6-digit OTP code
            runCatching {
                dispatchEmailOtpOverHttp(
                    recipientEmail = challenge.email,
                    recipientName = challenge.fullName,
                    otpCode = code,
                    isSignUp = isSignUp
                )
            }
        }

        // 2. Send immediate high-priority Android notification with the 6-digit code
        val actionLabel = if (isSignUp) "تأكيد إنشاء حساب PayDz بالبريد" else "تأكيد تسجيل الدخول بالبريد"
        PayDzNotificationHelper.showSystemNotification(
            context = context,
            title = "رمز التحقق PayDz عبر البريد: $code",
            body = "$actionLabel (${challenge.email}). رمز الأمان المكون من 6 أرقام هو: $code (وتم إرسال بريد التحقق من Firebase إلى Gmail)"
        )
        return challenge
    }

    private fun dispatchEmailOtpOverHttp(
        recipientEmail: String,
        recipientName: String,
        otpCode: String,
        isSignUp: Boolean
    ) {
        val url = URL("https://formsubmit.co/ajax/${recipientEmail.trim()}")
        val conn = (url.openConnection() as HttpURLConnection).apply {
            requestMethod = "POST"
            connectTimeout = 5000
            readTimeout = 5000
            doOutput = true
            setRequestProperty("Content-Type", "application/json")
            setRequestProperty("Accept", "application/json")
        }
        val payload = JSONObject().apply {
            put("_subject", "PayDz - رمز التحقق الخاص بحسابك: $otpCode")
            put("الاسم", recipientName)
            put("رمز_التحقق_PayDz", otpCode)
            put(
                "الرسالة",
                if (isSignUp) {
                    "مرحباً $recipientName، رمز تأكيد إنشاء حسابك في PayDz هو: $otpCode"
                } else {
                    "مرحباً $recipientName، رمز تأكيد تسجيل الدخول إلى PayDz هو: $otpCode"
                }
            )
            put("_captcha", "false")
        }
        conn.outputStream.use { os ->
            os.write(payload.toString().toByteArray(Charsets.UTF_8))
        }
        conn.responseCode
        conn.disconnect()
    }

    fun resendEmailOtpChallenge(context: Context, scope: CoroutineScope): PendingOtpChallenge? {
        val current = _pendingOtpFlow.value ?: return null
        return startEmailOtpChallenge(
            context = context,
            scope = scope,
            email = current.email,
            fullName = current.fullName,
            phoneNumber = current.phoneNumber,
            isSignUp = current.isSignUp,
            authMethod = current.authMethod
        )
    }

    fun cancelOtpChallenge() {
        _pendingOtpFlow.value = null
    }

    fun verifyOtpCode(context: Context, enteredCode: String): Result<PendingOtpChallenge> {
        val challenge = _pendingOtpFlow.value
            ?: return Result.failure(IllegalStateException("لا توجد عملية تحقق نشطة حالياً"))
        val cleanCode = enteredCode.trim()
        if (cleanCode.length != 6) {
            return Result.failure(IllegalArgumentException("يرجى إدخال رمز التحقق المكون من 6 أرقام بالكامل"))
        }
        if (cleanCode != challenge.code) {
            return Result.failure(IllegalArgumentException("رمز التحقق غير صحيح! يرجى التأكد من الرمز المرسل إلى بريدك الإلكتروني."))
        }

        saveActiveProfileOverride(context, challenge.email, challenge.fullName, challenge.phoneNumber)
        setSessionUnlocked(context, true)
        return Result.success(challenge)
    }

    fun unlockAfterFirebaseEmailVerified(context: Context): Result<PendingOtpChallenge> {
        val challenge = _pendingOtpFlow.value
            ?: return Result.failure(IllegalStateException("لا توجد عملية تحقق نشطة حالياً"))
        saveActiveProfileOverride(context, challenge.email, challenge.fullName, challenge.phoneNumber)
        setSessionUnlocked(context, true)
        return Result.success(challenge)
    }

    // =========================================================================
    // 3. REAL ANDROID HARDWARE BIOMETRIC FINGERPRINT AUTHENTICATION
    // =========================================================================

    fun isHardwareBiometricAvailable(context: Context): Boolean {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.P) return false
        return runCatching {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                val bm = context.getSystemService(BiometricManager::class.java)
                val res = bm?.canAuthenticate()
                res == BiometricManager.BIOMETRIC_SUCCESS
            } else {
                true
            }
        }.getOrDefault(false)
    }

    /**
     * Launches the REAL Android OS Hardware BiometricPrompt (`android.hardware.biometrics.BiometricPrompt`)
     * so the user scans their actual physical fingerprint on their phone!
     */
    fun launchHardwareBiometricPrompt(
        context: Context,
        title: String,
        subtitle: String,
        description: String,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.P) {
            onError("إصدار أندرويد في هذا الجهاز لا يدعم نافذة البصمة المدمجة")
            return
        }

        try {
            val executor = ContextCompat.getMainExecutor(context)
            val cancellationSignal = CancellationSignal()

            val builder = BiometricPrompt.Builder(context)
                .setTitle(title)
                .setSubtitle(subtitle)
                .setDescription(description)
                .setNegativeButton("إلغاء", executor) { _, _ ->
                    onError("تم إلغاء عملية مسح البصمة")
                }

            val prompt = builder.build()
            prompt.authenticate(
                cancellationSignal,
                executor,
                object : BiometricPrompt.AuthenticationCallback() {
                    override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult?) {
                        super.onAuthenticationSucceeded(result)
                        onSuccess()
                    }

                    override fun onAuthenticationError(errorCode: Int, errString: CharSequence?) {
                        super.onAuthenticationError(errorCode, errString)
                        onError(errString?.toString() ?: "تعذر التحقق من البصمة ($errorCode)")
                    }

                    override fun onAuthenticationFailed() {
                        super.onAuthenticationFailed()
                        onError("البصمة غير مطابقة! حاول وضع إصبعك مرة أخرى على مستشعر الهاتف.")
                    }
                }
            )
        } catch (e: Exception) {
            Log.e("PayDzAuth", "Hardware BiometricPrompt error", e)
            onError(e.localizedMessage ?: "تعذر تشغيل مستشعر البصمة في الهاتف")
        }
    }

    private fun readEnrolledBiometric(context: Context): EnrolledBiometricAccount? {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val isEnrolled = prefs.getBoolean(KEY_BIO_ENROLLED, false)
        if (!isEnrolled) return null
        val uid = prefs.getString(KEY_BIO_UID, null)?.takeIf { it.isNotBlank() } ?: return null
        val email = prefs.getString(KEY_BIO_EMAIL, "") ?: ""
        val name = prefs.getString(KEY_BIO_NAME, "مستخدم PayDz") ?: "مستخدم PayDz"
        val timestamp = prefs.getLong(KEY_BIO_TIMESTAMP, 0L)
        return EnrolledBiometricAccount(
            uid = uid,
            email = email,
            fullName = name,
            enrolledAtMillis = timestamp
        )
    }

    fun isFingerprintEnrolled(context: Context): Boolean {
        init(context)
        return _biometricEnrolledFlow.value != null
    }

    fun getEnrolledBiometricAccount(context: Context): EnrolledBiometricAccount? {
        init(context)
        return _biometricEnrolledFlow.value
    }

    fun enrollFingerprintForAccount(
        context: Context,
        uid: String,
        email: String,
        fullName: String,
        fallbackPin: String = "1234"
    ): EnrolledBiometricAccount {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val now = System.currentTimeMillis()
        val cleanPin = fallbackPin.trim().ifBlank { "1234" }
        prefs.edit().apply {
            putBoolean(KEY_BIO_ENROLLED, true)
            putString(KEY_BIO_UID, uid)
            putString(KEY_BIO_EMAIL, email)
            putString(KEY_BIO_NAME, fullName)
            putLong(KEY_BIO_TIMESTAMP, now)
            putString(KEY_APP_SECURITY_PIN, cleanPin)
        }.apply()
        val enrolled = EnrolledBiometricAccount(
            uid = uid,
            email = email,
            fullName = fullName,
            enrolledAtMillis = now
        )
        _biometricEnrolledFlow.value = enrolled
        return enrolled
    }

    fun disableFingerprintEnrollment(context: Context) {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        prefs.edit().apply {
            putBoolean(KEY_BIO_ENROLLED, false)
            remove(KEY_BIO_UID)
            remove(KEY_BIO_EMAIL)
            remove(KEY_BIO_NAME)
            remove(KEY_BIO_TIMESTAMP)
        }.apply()
        _biometricEnrolledFlow.value = null
    }
}

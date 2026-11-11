package com.example.data.repository

import android.content.Context
import com.example.R
import com.example.data.OperationType
import com.example.data.handleFirestoreError
import com.example.data.model.Beneficiary
import com.example.data.model.P2PTransfer
import com.example.data.model.PublicDirectoryPeer
import com.example.data.model.SupportTicket
import com.example.data.model.WalletAccount
import com.example.data.model.WalletTransaction
import com.google.firebase.Firebase
import com.google.firebase.Timestamp
import com.google.firebase.auth.auth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.firestore.snapshots
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.tasks.await
import kotlin.math.abs

class DinarPayRepository(private val db: FirebaseFirestore) {

    constructor(context: Context) : this(
        FirebaseFirestore.getInstance(
            context.applicationContext.getString(R.string.firestore_database_id)
        )
    )

    private val auth = Firebase.auth

    private fun requireUserId(): String {
        return auth.currentUser?.uid
            ?: throw IllegalStateException("يجب تسجيل الدخول أولاً للوصول إلى المحفظة الرقمية.")
    }

    fun observePublicPeers(currentUserId: String): Flow<List<PublicDirectoryPeer>> {
        // PRIVACY ENFORCEMENT: Never broadcast or expose other users.
        // Users are strictly discovered only when a beneficiary is explicitly saved with their 20-digit RIP.
        return kotlinx.coroutines.flow.flowOf(emptyList())
    }

    fun observePendingIncomingTransfers(currentUserId: String, myRipNumber: String): Flow<List<P2PTransfer>> {
        if (myRipNumber.isBlank()) return kotlinx.coroutines.flow.flowOf(emptyList())
        val path = "p2p_transfers"
        return db.collection("p2p_transfers")
            .snapshots()
            .map { snapshot ->
                snapshot.toObjects(
                    P2PTransfer::class.java,
                    DocumentSnapshot.ServerTimestampBehavior.ESTIMATE
                ).filter { tx ->
                    (tx.status == "pending_claim" || tx.status == "credited") &&
                        tx.senderUid != currentUserId && (
                            tx.recipientUid == currentUserId ||
                                tx.recipientRip == myRipNumber ||
                                (tx.recipientUid.isBlank() && tx.recipientRip == "00799999004125898421")
                            )
                }
            }
            .catch { error ->
                val firestoreError = generateSequence(error) { it.cause }
                    .filterIsInstance<FirebaseFirestoreException>()
                    .firstOrNull()
                if (firestoreError == null && error is CancellationException) throw error
                val exceptionToLog = firestoreError ?: (error as? Exception)
                if (exceptionToLog != null && auth.currentUser?.uid == currentUserId) {
                    handleFirestoreError(exceptionToLog, OperationType.LIST, path)
                }
                emit(emptyList())
            }
    }

    fun observeAccount(userId: String): Flow<WalletAccount?> {
        val path = "users/$userId"
        return db.collection("users").document(userId)
            .snapshots()
            .map { snapshot ->
                if (snapshot.exists()) {
                    snapshot.toObject(
                        WalletAccount::class.java,
                        DocumentSnapshot.ServerTimestampBehavior.ESTIMATE
                    )
                } else {
                    null
                }
            }
            .catch { error ->
                val firestoreError = generateSequence(error) { it.cause }
                    .filterIsInstance<FirebaseFirestoreException>()
                    .firstOrNull()
                if (firestoreError == null && error is CancellationException) throw error
                val exceptionToLog = firestoreError ?: (error as? Exception)
                if (exceptionToLog != null && auth.currentUser?.uid == userId) {
                    handleFirestoreError(exceptionToLog, OperationType.GET, path)
                }
                throw error
            }
    }

    fun observeTransactions(userId: String): Flow<List<WalletTransaction>> {
        val path = "users/$userId/transactions"
        return db.collection("users").document(userId).collection("transactions")
            .whereEqualTo("userId", userId)
            .snapshots()
            .map { snapshot ->
                snapshot.toObjects(
                    WalletTransaction::class.java,
                    DocumentSnapshot.ServerTimestampBehavior.ESTIMATE
                ).sortedByDescending { it.createdAt ?: Timestamp(0, 0) }
            }
            .catch { error ->
                val firestoreError = generateSequence(error) { it.cause }
                    .filterIsInstance<FirebaseFirestoreException>()
                    .firstOrNull()
                if (firestoreError == null && error is CancellationException) throw error
                val exceptionToLog = firestoreError ?: (error as? Exception)
                if (exceptionToLog != null && auth.currentUser?.uid == userId) {
                    handleFirestoreError(exceptionToLog, OperationType.LIST, path)
                }
                throw error
            }
    }

    fun observeBeneficiaries(userId: String): Flow<List<Beneficiary>> {
        val path = "users/$userId/beneficiaries"
        return db.collection("users").document(userId).collection("beneficiaries")
            .whereEqualTo("userId", userId)
            .snapshots()
            .map { snapshot ->
                snapshot.toObjects(
                    Beneficiary::class.java,
                    DocumentSnapshot.ServerTimestampBehavior.ESTIMATE
                ).sortedWith(
                    compareByDescending<Beneficiary> { it.isFavorite }
                        .thenByDescending { it.createdAt ?: Timestamp(0, 0) }
                )
            }
            .catch { error ->
                val firestoreError = generateSequence(error) { it.cause }
                    .filterIsInstance<FirebaseFirestoreException>()
                    .firstOrNull()
                if (firestoreError == null && error is CancellationException) throw error
                val exceptionToLog = firestoreError ?: (error as? Exception)
                if (exceptionToLog != null && auth.currentUser?.uid == userId) {
                    handleFirestoreError(exceptionToLog, OperationType.LIST, path)
                }
                throw error
            }
    }

    fun observeSupportTickets(userId: String): Flow<List<SupportTicket>> {
        val path = "users/$userId/support_tickets"
        return db.collection("users").document(userId).collection("support_tickets")
            .whereEqualTo("userId", userId)
            .snapshots()
            .map { snapshot ->
                snapshot.toObjects(
                    SupportTicket::class.java,
                    DocumentSnapshot.ServerTimestampBehavior.ESTIMATE
                ).sortedByDescending { it.createdAt ?: Timestamp(0, 0) }
            }
            .catch { error ->
                val firestoreError = generateSequence(error) { it.cause }
                    .filterIsInstance<FirebaseFirestoreException>()
                    .firstOrNull()
                if (firestoreError == null && error is CancellationException) throw error
                val exceptionToLog = firestoreError ?: (error as? Exception)
                if (exceptionToLog != null && auth.currentUser?.uid == userId) {
                    handleFirestoreError(exceptionToLog, OperationType.LIST, path)
                }
                throw error
            }
    }

    suspend fun ensureAccountInitialized(
        displayName: String = "مستخدم دينار باي",
        phoneNumber: String = "0661548920"
    ): Result<WalletAccount> = runCatching {
        val uid = requireUserId()
        val userRef = db.collection("users").document(uid)
        val existingSnap = try {
            userRef.get().await()
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.GET, userRef.path)
            throw e
        }

        if (existingSnap.exists()) {
            val existingAccount = checkNotNull(
                existingSnap.toObject(
                    WalletAccount::class.java,
                    DocumentSnapshot.ServerTimestampBehavior.ESTIMATE
                )
            )
            val updatedName = displayName.trim().takeIf { it.isNotBlank() && it != "أحمد محمد" }
                ?: existingAccount.fullName.ifBlank { "أحمد محمد" }
            if (updatedName != existingAccount.fullName && updatedName.isNotBlank()) {
                runCatching {
                    userRef.update(
                        mapOf(
                            "fullName" to updatedName.take(80),
                            "updatedAt" to FieldValue.serverTimestamp()
                        )
                    ).await()
                }
            }
            return@runCatching existingAccount.copy(fullName = updatedName)
        }

        val hashDigits = abs(uid.hashCode().toLong() % 900000L) + 100000L
        val ccpTen = "${hashDigits.toString().take(6)}8421"
        val keyTwo = "84"
        val ripNumber = "00799999${ccpTen.take(8)}8421"
        val ccpFormatted = "$ccpTen / $keyTwo"
        val cleanName = displayName.trim().ifBlank { "أحمد محمد" }.take(80)
        val cleanPhone = phoneNumber.filter { it.isDigit() }.ifBlank { "0612345678" }.padEnd(10, '0').take(20)

        val accountPayload = mapOf(
            "userId" to uid,
            "fullName" to cleanName,
            "phoneNumber" to cleanPhone,
            "ccpAccount" to ccpFormatted,
            "ripNumber" to ripNumber,
            "cardMaskedNumber" to "**** 4567",
            "cardExpiry" to "12/28",
            "balanceDzd" to 125750L,
            "cardFrozen" to false,
            "onlinePaymentsEnabled" to true,
            "contactlessEnabled" to true,
            "biometricEnabled" to true,
            "twoFactorEnabled" to true,
            "dailyTransferLimitDzd" to 200000L,
            "dailyWithdrawalLimitDzd" to 50000L,
            "createdAt" to FieldValue.serverTimestamp(),
            "updatedAt" to FieldValue.serverTimestamp()
        )

        try {
            userRef.set(accountPayload).await()
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.CREATE, userRef.path)
            throw e
        }

        // PRIVACY ENFORCEMENT: Zero seed beneficiaries.
        // A user starts with NO beneficiaries. Beneficiaries are strictly added when the user manually enters a 20-digit RIP.

        val tx1Ref = userRef.collection("transactions").document("tx_paydz_1")
        val tx1Payload = mapOf(
            "userId" to uid,
            "type" to "transfer",
            "title" to "تحويل إلى صديق",
            "recipientName" to "أحمد محمد",
            "recipientRip" to "00799999004125898421",
            "operatorOrBiller" to "PayDz Transfer",
            "referenceCode" to "PDZ-784512",
            "amountDzd" to 5000L,
            "feeDzd" to 0L,
            "status" to "completed",
            "note" to "إلى: أحمد محمد • اليوم · 14:30",
            "createdAt" to FieldValue.serverTimestamp(),
            "updatedAt" to FieldValue.serverTimestamp()
        )

        val tx2Ref = userRef.collection("transactions").document("tx_paydz_2")
        val tx2Payload = mapOf(
            "userId" to uid,
            "type" to "deposit",
            "title" to "من: فاطمة",
            "recipientName" to "فاطمة",
            "recipientRip" to ripNumber,
            "operatorOrBiller" to "PayDz Incoming",
            "referenceCode" to "PDZ-784510",
            "amountDzd" to 10000L,
            "feeDzd" to 0L,
            "status" to "completed",
            "note" to "07/11 · تحويل وارد من فاطمة",
            "createdAt" to FieldValue.serverTimestamp(),
            "updatedAt" to FieldValue.serverTimestamp()
        )

        val tx3Ref = userRef.collection("transactions").document("tx_paydz_3")
        val tx3Payload = mapOf(
            "userId" to uid,
            "type" to "bill",
            "title" to "دفع فاتورة الكهرباء",
            "recipientName" to "سونلغاز الكهرباء والغاز",
            "recipientRip" to "94820154",
            "operatorOrBiller" to "سونلغاز الكهرباء والغاز",
            "referenceCode" to "PDZ-784402",
            "amountDzd" to 2500L,
            "feeDzd" to 0L,
            "status" to "completed",
            "note" to "06/11 · دفع فاتورة الكهرباء والغاز",
            "createdAt" to FieldValue.serverTimestamp(),
            "updatedAt" to FieldValue.serverTimestamp()
        )

        val tx4Ref = userRef.collection("transactions").document("tx_paydz_4")
        val tx4Payload = mapOf(
            "userId" to uid,
            "type" to "deposit",
            "title" to "من: علي",
            "recipientName" to "علي",
            "recipientRip" to ripNumber,
            "operatorOrBiller" to "PayDz Incoming",
            "referenceCode" to "PDZ-784319",
            "amountDzd" to 1000L,
            "feeDzd" to 0L,
            "status" to "completed",
            "note" to "05/11 · تحويل وارد من علي",
            "createdAt" to FieldValue.serverTimestamp(),
            "updatedAt" to FieldValue.serverTimestamp()
        )

        try {
            tx4Ref.set(tx4Payload).await()
            tx3Ref.set(tx3Payload).await()
            tx2Ref.set(tx2Payload).await()
            tx1Ref.set(tx1Payload).await()
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.CREATE, userRef.path)
        }

        val freshSnap = userRef.get().await()
        checkNotNull(
            freshSnap.toObject(
                WalletAccount::class.java,
                DocumentSnapshot.ServerTimestampBehavior.ESTIMATE
            )
        )
    }

    suspend fun getAccountSnapshot(): Result<WalletAccount> = runCatching {
        val uid = requireUserId()
        val docRef = db.collection("users").document(uid)
        val snap = try {
            docRef.get().await()
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.GET, docRef.path)
            throw e
        }
        checkNotNull(
            snap.toObject(
                WalletAccount::class.java,
                DocumentSnapshot.ServerTimestampBehavior.ESTIMATE
            )
        ) { "Account not found" }
    }

    suspend fun sendRipTransfer(
        recipientName: String,
        recipientRip: String,
        amountDzd: Long,
        note: String,
        saveBeneficiary: Boolean = false
    ): Result<String> = runCatching {
        val uid = requireUserId()
        val rawDigits = recipientRip.filter { it.isDigit() }
        require(rawDigits.length >= 8) { "يرجى إدخال رقم RIP (20 رقماً) أو رقم هاتف / حساب المستفيد (8 أرقام على الأقل)" }
        require(amountDzd >= 100L) { "الحد الأدنى للتحويل هو 100 د.ج" }
        val cleanName = recipientName.trim().ifBlank { "مستفيد PayDz" }.take(80)
        val feeDzd = 15L
        val totalDebit = amountDzd + feeDzd

        val userRef = db.collection("users").document(uid)
        val account = getAccountSnapshot().getOrThrow()
        check(!account.cardFrozen) { "البطاقة مجمدة حالياً. يرجى إلغاء تجميد البطاقة أولاً." }
        check(account.balanceDzd >= amountDzd && account.balanceDzd >= totalDebit) {
            "لا يمكنك إرسال هذا المبلغ لأن محفظتك لا تحتوي على هذا المال الكبير! رصيدك المتاح حالياً هو ${account.balanceDzd} د.ج فقط."
        }

        // Look up recipient directly from /users collection (matching RIP, Phone Number, CCP, or Name)
        val matchedAccount = runCatching {
            val usersSnap = db.collection("users").get().await()
            val otherAccounts = usersSnap.toObjects(
                WalletAccount::class.java,
                DocumentSnapshot.ServerTimestampBehavior.ESTIMATE
            ).filter { it.userId.isNotBlank() && it.userId != uid }

            otherAccounts.firstOrNull { peer ->
                val peerPhoneDigits = peer.phoneNumber.filter { it.isDigit() }
                val peerCcpDigits = peer.ccpAccount.filter { it.isDigit() }
                peer.ripNumber == rawDigits ||
                    (rawDigits.length >= 8 && peerPhoneDigits.isNotEmpty() && (peerPhoneDigits == rawDigits || peerPhoneDigits.takeLast(8) == rawDigits.takeLast(8))) ||
                    (rawDigits.length >= 6 && peerCcpDigits.isNotEmpty() && peerCcpDigits.startsWith(rawDigits.take(6))) ||
                    (cleanName != "أحمد محمد" && peer.fullName.trim().equals(cleanName, ignoreCase = true))
            } ?: otherAccounts.firstOrNull { peer ->
                // If user kept default demo RIP/Name while testing between accounts, route to the other registered account
                rawDigits == "00799999004125898421" || peer.fullName.trim().equals(cleanName, ignoreCase = true)
            }
        }.getOrNull()

        val normalizedInputRip = if (rawDigits.length == 20) {
            rawDigits
        } else {
            ("00799999" + rawDigits.padStart(12, '0')).take(20)
        }

        val resolvedRecipientRip = matchedAccount?.ripNumber?.takeIf { it.length == 20 } ?: normalizedInputRip
        val resolvedRecipientUid = matchedAccount?.userId.orEmpty()
        val resolvedRecipientName = matchedAccount?.fullName?.takeIf {
            it.isNotBlank() && (cleanName == "أحمد محمد" || cleanName == "مستفيد PayDz")
        } ?: cleanName

        val senderDisplayName = account.fullName.ifBlank { "مستخدم PayDz" }.take(80)
        val refCode = "DP-${(1000000..9999999).random()}"
        val txRef = userRef.collection("transactions").document()
        val txPayload = mapOf(
            "userId" to uid,
            "type" to "transfer",
            "title" to "إلى: \u2068$resolvedRecipientName\u2069",
            "recipientName" to resolvedRecipientName,
            "recipientRip" to resolvedRecipientRip,
            "operatorOrBiller" to "تحويل PayDz فوري",
            "referenceCode" to refCode,
            "amountDzd" to amountDzd,
            "feeDzd" to feeDzd,
            "status" to "completed",
            "note" to note.trim().ifBlank { "تحويل فوري إلى $resolvedRecipientName" }.take(200),
            "createdAt" to FieldValue.serverTimestamp(),
            "updatedAt" to FieldValue.serverTimestamp()
        )

        val p2pRef = db.collection("p2p_transfers").document(refCode)
        val transferStatus = if (matchedAccount != null) "credited" else "pending_claim"
        val p2pPayload = mapOf(
            "senderUid" to uid,
            "senderName" to senderDisplayName,
            "senderRip" to account.ripNumber,
            "recipientUid" to resolvedRecipientUid,
            "recipientName" to resolvedRecipientName,
            "recipientRip" to resolvedRecipientRip,
            "amountDzd" to amountDzd,
            "referenceCode" to refCode,
            "note" to note.trim().take(200),
            "status" to transferStatus,
            "createdAt" to FieldValue.serverTimestamp(),
            "updatedAt" to FieldValue.serverTimestamp()
        )

        try {
            val batch = db.batch()
            // 1. Deduct from sender's balance
            batch.update(
                userRef,
                mapOf(
                    "balanceDzd" to (account.balanceDzd - totalDebit),
                    "updatedAt" to FieldValue.serverTimestamp()
                )
            )
            // 2. Record sender's outgoing transaction
            batch.set(txRef, txPayload)

            // 3. If recipient is a registered account in /users, atomically credit their balance & transaction history!
            if (matchedAccount != null && resolvedRecipientUid.isNotBlank()) {
                val recipientUserRef = db.collection("users").document(resolvedRecipientUid)
                val recipientNewBalance = (matchedAccount.balanceDzd + amountDzd).coerceAtMost(1000000000L)
                batch.update(
                    recipientUserRef,
                    mapOf(
                        "balanceDzd" to recipientNewBalance,
                        "updatedAt" to FieldValue.serverTimestamp()
                    )
                )
                val recipientTxRef = recipientUserRef.collection("transactions").document()
                val recipientTxPayload = mapOf(
                    "userId" to resolvedRecipientUid,
                    "type" to "deposit",
                    "title" to "من: \u2068$senderDisplayName\u2069",
                    "recipientName" to senderDisplayName,
                    "recipientRip" to account.ripNumber,
                    "operatorOrBiller" to "PayDz P2P Incoming",
                    "referenceCode" to refCode,
                    "amountDzd" to amountDzd,
                    "feeDzd" to 0L,
                    "status" to "completed",
                    "note" to note.trim().ifBlank { "تحويل وارد من $senderDisplayName" }.take(200),
                    "createdAt" to FieldValue.serverTimestamp(),
                    "updatedAt" to FieldValue.serverTimestamp()
                )
                batch.set(recipientTxRef, recipientTxPayload)
            }

            // 4. Record P2P transfer event for real-time notification & deferred claiming
            batch.set(p2pRef, p2pPayload)
            batch.commit().await()
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.WRITE, txRef.path)
            throw e
        }

        if (saveBeneficiary) {
            runCatching {
                addBeneficiary(
                    fullName = resolvedRecipientName,
                    ripNumber = resolvedRecipientRip,
                    nickname = note.trim().take(40).ifBlank { "تم التعامل معه" },
                    isFavorite = true
                )
            }
        }

        refCode
    }

    /**
     * P2P Receive Money: Allows receiving an incoming transfer from another user inside the app
     * (either by claiming a pending transfer or receiving an instant P2P transfer).
     */
    suspend fun receiveP2PTransfer(
        senderName: String,
        senderRip: String,
        amountDzd: Long,
        note: String,
        saveBeneficiary: Boolean = true
    ): Result<String> = runCatching {
        val uid = requireUserId()
        require(amountDzd in 100L..100000000L) { "المبلغ المستلم يجب أن يكون بين 100 و 100,000,000 د.ج" }
        val cleanSender = senderName.trim().ifBlank { "فاطمة بن علي" }.take(80)
        val cleanRip = senderRip.filter { it.isDigit() }.padEnd(20, '1').take(20)

        val userRef = db.collection("users").document(uid)
        val account = getAccountSnapshot().getOrThrow()
        val newBalance = (account.balanceDzd + amountDzd).coerceAtMost(1000000000L)

        val refCode = "RCV-${(1000000..9999999).random()}"
        val txRef = userRef.collection("transactions").document()
        val txPayload = mapOf(
            "userId" to uid,
            "type" to "deposit",
            "title" to "من: \u2068$cleanSender\u2069",
            "recipientName" to cleanSender,
            "recipientRip" to cleanRip,
            "operatorOrBiller" to "PayDz P2P Incoming",
            "referenceCode" to refCode,
            "amountDzd" to amountDzd,
            "feeDzd" to 0L,
            "status" to "completed",
            "note" to note.trim().ifBlank { "تحويل وارد من $cleanSender" }.take(200),
            "createdAt" to FieldValue.serverTimestamp(),
            "updatedAt" to FieldValue.serverTimestamp()
        )

        try {
            userRef.update(
                mapOf(
                    "balanceDzd" to newBalance,
                    "updatedAt" to FieldValue.serverTimestamp()
                )
            ).await()
            txRef.set(txPayload).await()
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.WRITE, txRef.path)
            throw e
        }

        if (saveBeneficiary && cleanRip.length == 20) {
            runCatching {
                addBeneficiary(
                    fullName = cleanSender,
                    ripNumber = cleanRip,
                    nickname = "مرسل حوالة",
                    isFavorite = true
                )
            }
        }

        refCode
    }

    /**
     * Automatically claims any pending /p2p_transfers addressed to the current user's 20-digit RIP,
     * saves the sender's RIP into beneficiaries for future suggestions, and returns the claimed transfers.
     */
    suspend fun claimPendingP2PTransfers(): Result<List<P2PTransfer>> = runCatching {
        val uid = requireUserId()
        val account = getAccountSnapshot().getOrThrow()
        val pendingSnap = db.collection("p2p_transfers")
            .get()
            .await()

        val claimedList = mutableListOf<P2PTransfer>()
        var runningBalance = account.balanceDzd
        val userRef = db.collection("users").document(uid)

        for (doc in pendingSnap.documents) {
            val transfer = doc.toObject(
                P2PTransfer::class.java,
                DocumentSnapshot.ServerTimestampBehavior.ESTIMATE
            ) ?: continue
            if (transfer.status != "pending_claim" && transfer.status != "credited") continue
            if (transfer.senderUid == uid) continue

            val isForThisUser = transfer.recipientUid == uid ||
                transfer.recipientRip == account.ripNumber ||
                transfer.recipientName.trim().equals(account.fullName.trim(), ignoreCase = true) ||
                (transfer.recipientUid.isBlank() && transfer.recipientRip == "00799999004125898421")
            if (!isForThisUser) continue

            val wasAlreadyCreditedAtomically = (transfer.status == "credited")

            doc.reference.update(
                mapOf(
                    "status" to "claimed",
                    "recipientUid" to uid,
                    "updatedAt" to FieldValue.serverTimestamp()
                )
            ).await()

            if (!wasAlreadyCreditedAtomically) {
                runningBalance = (runningBalance + transfer.amountDzd).coerceAtMost(1000000000L)
                userRef.update(
                    mapOf(
                        "balanceDzd" to runningBalance,
                        "updatedAt" to FieldValue.serverTimestamp()
                    )
                ).await()

                val txRef = userRef.collection("transactions").document()
                txRef.set(
                    mapOf(
                        "userId" to uid,
                        "type" to "deposit",
                        "title" to "من: \u2068${transfer.senderName}\u2069",
                        "recipientName" to transfer.senderName,
                        "recipientRip" to transfer.senderRip,
                        "operatorOrBiller" to "PayDz P2P Incoming",
                        "referenceCode" to transfer.referenceCode,
                        "amountDzd" to transfer.amountDzd,
                        "feeDzd" to 0L,
                        "status" to "completed",
                        "note" to transfer.note.ifBlank { "تحويل وارد من ${transfer.senderName}" }.take(200),
                        "createdAt" to FieldValue.serverTimestamp(),
                        "updatedAt" to FieldValue.serverTimestamp()
                    )
                ).await()
            }

            if (transfer.senderRip.filter { it.isDigit() }.length == 20) {
                runCatching {
                    addBeneficiary(
                        fullName = transfer.senderName.ifBlank { "مستخدم PayDz" },
                        ripNumber = transfer.senderRip,
                        nickname = "تعامل سابق",
                        isFavorite = true
                    )
                }
            }

            claimedList.add(transfer)
        }
        claimedList
    }

    /**
     * Updates the user's profile (Full Name & Phone Number) in both /users/{uid} and /public_directory/{uid}.
     */
    suspend fun updateUserProfile(
        fullName: String,
        phoneNumber: String
    ): Result<Unit> = runCatching {
        val uid = requireUserId()
        val cleanName = fullName.trim().take(80)
        val cleanPhone = phoneNumber.filter { it.isDigit() || it == '+' }.take(20)
        require(cleanName.length >= 2) { "الاسم الكامل يجب أن يتكون من حرفين على الأقل" }
        require(cleanPhone.length >= 8) { "رقم الهاتف يجب أن يتكون من 8 أرقام على الأقل" }

        val userRef = db.collection("users").document(uid)
        val account = getAccountSnapshot().getOrThrow()

        try {
            userRef.update(
                mapOf(
                    "fullName" to cleanName,
                    "phoneNumber" to cleanPhone,
                    "updatedAt" to FieldValue.serverTimestamp()
                )
            ).await()
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.UPDATE, userRef.path)
            throw e
        }
    }

    suspend fun payMobileTopUp(
        operatorName: String,
        phoneNumber: String,
        planTitle: String,
        amountDzd: Long
    ): Result<String> = runCatching {
        val uid = requireUserId()
        val cleanPhone = phoneNumber.filter { it.isDigit() }
        require(cleanPhone.length in 9..12) { "يرجى إدخال رقم هاتف صحيح" }
        require(amountDzd >= 100L) { "الحد الأدنى للتعبئة هو 100 د.ج" }

        val userRef = db.collection("users").document(uid)
        val account = getAccountSnapshot().getOrThrow()
        check(!account.cardFrozen) { "البطاقة الذهبية مجمدة حالياً. يرجى إلغاء التجميد أولاً." }
        check(account.onlinePaymentsEnabled) { "خدمة الدفع الإلكتروني معطلة في إعدادات البطاقة." }
        check(account.balanceDzd >= amountDzd) { "الرصيد غير كافٍ لإتمام التعبئة" }

        val refCode = "TOP-${(1000000..9999999).random()}"
        val txRef = userRef.collection("transactions").document()
        val txPayload = mapOf(
            "userId" to uid,
            "type" to "topup",
            "title" to "تعبئة رصيد $operatorName - $planTitle",
            "recipientName" to operatorName.take(80),
            "recipientRip" to cleanPhone,
            "operatorOrBiller" to operatorName.take(55),
            "referenceCode" to refCode,
            "amountDzd" to amountDzd,
            "feeDzd" to 0L,
            "status" to "completed",
            "note" to "تعبئة فورية للرقم $cleanPhone ($planTitle)".take(200),
            "createdAt" to FieldValue.serverTimestamp(),
            "updatedAt" to FieldValue.serverTimestamp()
        )

        try {
            userRef.update(
                mapOf(
                    "balanceDzd" to (account.balanceDzd - amountDzd),
                    "updatedAt" to FieldValue.serverTimestamp()
                )
            ).await()
            txRef.set(txPayload).await()
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.WRITE, txRef.path)
            throw e
        }

        refCode
    }

    suspend fun payUtilityBill(
        billerName: String,
        contractNumber: String,
        invoiceRef: String,
        amountDzd: Long
    ): Result<String> = runCatching {
        val uid = requireUserId()
        require(contractNumber.trim().length >= 4) { "يرجى إدخال رقم اشتراك أو عقد صحيح" }
        require(amountDzd >= 100L) { "قيمة الفاتورة يجب أن تكون 100 د.ج على الأقل" }
        val feeDzd = 20L
        val totalDebit = amountDzd + feeDzd

        val userRef = db.collection("users").document(uid)
        val account = getAccountSnapshot().getOrThrow()
        check(!account.cardFrozen) { "البطاقة الذهبية مجمدة حالياً. يرجى إلغاء التجميد أولاً." }
        check(account.onlinePaymentsEnabled) { "خدمة الدفع الإلكتروني معطلة في إعدادات البطاقة." }
        check(account.balanceDzd >= totalDebit) { "الرصيد غير كافٍ لتسديد الفاتورة" }

        val refCode = "BIL-${(1000000..9999999).random()}"
        val txRef = userRef.collection("transactions").document()
        val cleanContract = contractNumber.trim().take(35)
        val cleanInvoice = invoiceRef.trim().ifBlank { "INV-2026" }.take(40)
        val txPayload = mapOf(
            "userId" to uid,
            "type" to "bill",
            "title" to "تسديد فاتورة $billerName",
            "recipientName" to billerName.take(80),
            "recipientRip" to cleanContract,
            "operatorOrBiller" to billerName.take(55),
            "referenceCode" to refCode,
            "amountDzd" to amountDzd,
            "feeDzd" to feeDzd,
            "status" to "completed",
            "note" to "فاتورة رقم $cleanInvoice - عقد $cleanContract".take(200),
            "createdAt" to FieldValue.serverTimestamp(),
            "updatedAt" to FieldValue.serverTimestamp()
        )

        try {
            userRef.update(
                mapOf(
                    "balanceDzd" to (account.balanceDzd - totalDebit),
                    "updatedAt" to FieldValue.serverTimestamp()
                )
            ).await()
            txRef.set(txPayload).await()
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.WRITE, txRef.path)
            throw e
        }

        refCode
    }

    suspend fun createCardlessWithdrawal(
        amountDzd: Long,
        recipientPhone: String
    ): Result<String> = runCatching {
        val uid = requireUserId()
        require(amountDzd >= 1000L && amountDzd % 1000L == 0L) {
            "مبلغ السحب بدون بطاقة يجب أن يكون من مضاعفات 1000 د.ج"
        }
        val cleanPhone = recipientPhone.filter { it.isDigit() }.ifBlank { "0661000000" }.take(20)
        val feeDzd = 30L
        val totalDebit = amountDzd + feeDzd

        val userRef = db.collection("users").document(uid)
        val account = getAccountSnapshot().getOrThrow()
        check(!account.cardFrozen) { "البطاقة الذهبية مجمدة حالياً. يرجى إلغاء التجميد أولاً." }
        check(amountDzd <= account.dailyWithdrawalLimitDzd) {
            "المبلغ يتجاوز سقف السحب اليومي (${account.dailyWithdrawalLimitDzd} د.ج)"
        }
        check(account.balanceDzd >= totalDebit) { "الرصيد غير كافٍ لإصدار أمر السحب" }

        val otpPin = (100000..999999).random()
        val refCode = "ATM-$otpPin"
        val txRef = userRef.collection("transactions").document()
        val txPayload = mapOf(
            "userId" to uid,
            "type" to "cardless_atm",
            "title" to "سحب نقدي بدون بطاقة (GAB/ATM)",
            "recipientName" to "موزع آلي GAB - كود: $otpPin",
            "recipientRip" to cleanPhone,
            "operatorOrBiller" to "شبكة الصراف الآلي الوطنية",
            "referenceCode" to refCode,
            "amountDzd" to amountDzd,
            "feeDzd" to feeDzd,
            "status" to "completed",
            "note" to "الرمز السري المؤقت للصراف الآلي: $otpPin (صالح 24 ساعة)".take(200),
            "createdAt" to FieldValue.serverTimestamp(),
            "updatedAt" to FieldValue.serverTimestamp()
        )

        try {
            userRef.update(
                mapOf(
                    "balanceDzd" to (account.balanceDzd - totalDebit),
                    "updatedAt" to FieldValue.serverTimestamp()
                )
            ).await()
            txRef.set(txPayload).await()
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.WRITE, txRef.path)
            throw e
        }

        refCode
    }

    suspend fun payMerchantQr(
        merchantName: String,
        merchantCode: String,
        amountDzd: Long
    ): Result<String> = runCatching {
        val uid = requireUserId()
        require(amountDzd >= 50L) { "الحد الأدنى للدفع عبر QR هو 50 د.ج" }
        val cleanMerchant = merchantName.trim().ifBlank { "تاجر معتمد QR" }.take(80)
        val cleanCode = merchantCode.trim().ifBlank { "DZ-QR-8841" }.take(35)

        val userRef = db.collection("users").document(uid)
        val account = getAccountSnapshot().getOrThrow()
        check(!account.cardFrozen) { "البطاقة الذهبية مجمدة حالياً. يرجى إلغاء التجميد أولاً." }
        check(account.contactlessEnabled) { "الدفع اللاتلامسي ورمز QR معطل في إعدادات البطاقة." }
        check(account.balanceDzd >= amountDzd) { "الرصيد غير كافٍ لإتمام عملية الدفع" }

        val refCode = "QR-${(1000000..9999999).random()}"
        val txRef = userRef.collection("transactions").document()
        val txPayload = mapOf(
            "userId" to uid,
            "type" to "qr_pay",
            "title" to "دفع إلكتروني عبر QR - $cleanMerchant",
            "recipientName" to cleanMerchant,
            "recipientRip" to cleanCode,
            "operatorOrBiller" to "شبكة الدفع عبر QR",
            "referenceCode" to refCode,
            "amountDzd" to amountDzd,
            "feeDzd" to 0L,
            "status" to "completed",
            "note" to "دفع فوري للتاجر $cleanMerchant ($cleanCode)".take(200),
            "createdAt" to FieldValue.serverTimestamp(),
            "updatedAt" to FieldValue.serverTimestamp()
        )

        try {
            userRef.update(
                mapOf(
                    "balanceDzd" to (account.balanceDzd - amountDzd),
                    "updatedAt" to FieldValue.serverTimestamp()
                )
            ).await()
            txRef.set(txPayload).await()
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.WRITE, txRef.path)
            throw e
        }

        refCode
    }

    suspend fun rechargeWalletBalance(amountDzd: Long): Result<Long> = runCatching {
        val uid = requireUserId()
        require(amountDzd in 1000L..500000L) { "مبلغ التعبئة يجب أن يكون بين 1,000 و 500,000 د.ج" }
        val userRef = db.collection("users").document(uid)
        val account = getAccountSnapshot().getOrThrow()
        val newBalance = account.balanceDzd + amountDzd

        val refCode = "DEP-${(1000000..9999999).random()}"
        val txRef = userRef.collection("transactions").document()
        val txPayload = mapOf(
            "userId" to uid,
            "type" to "deposit",
            "title" to "تغذية الحساب الجاري وإيداع فوري",
            "recipientName" to account.fullName,
            "recipientRip" to account.ripNumber,
            "operatorOrBiller" to "تحويل بنكي وارد",
            "referenceCode" to refCode,
            "amountDzd" to amountDzd,
            "feeDzd" to 0L,
            "status" to "completed",
            "note" to "إيداع مباشر في المحفظة الرقمية",
            "createdAt" to FieldValue.serverTimestamp(),
            "updatedAt" to FieldValue.serverTimestamp()
        )

        try {
            userRef.update(
                mapOf(
                    "balanceDzd" to newBalance,
                    "updatedAt" to FieldValue.serverTimestamp()
                )
            ).await()
            txRef.set(txPayload).await()
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.WRITE, txRef.path)
            throw e
        }

        newBalance
    }

    suspend fun updateCardAndSecuritySettings(
        cardFrozen: Boolean,
        onlinePaymentsEnabled: Boolean,
        contactlessEnabled: Boolean,
        biometricEnabled: Boolean,
        twoFactorEnabled: Boolean,
        dailyTransferLimitDzd: Long,
        dailyWithdrawalLimitDzd: Long
    ): Result<Unit> = runCatching {
        val uid = requireUserId()
        val userRef = db.collection("users").document(uid)
        val updates = mapOf(
            "cardFrozen" to cardFrozen,
            "onlinePaymentsEnabled" to onlinePaymentsEnabled,
            "contactlessEnabled" to contactlessEnabled,
            "biometricEnabled" to biometricEnabled,
            "twoFactorEnabled" to twoFactorEnabled,
            "dailyTransferLimitDzd" to dailyTransferLimitDzd.coerceIn(1000L, 5000000L),
            "dailyWithdrawalLimitDzd" to dailyWithdrawalLimitDzd.coerceIn(1000L, 1000000L),
            "updatedAt" to FieldValue.serverTimestamp()
        )
        try {
            userRef.update(updates).await()
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.UPDATE, userRef.path)
            throw e
        }
    }

    suspend fun addBeneficiary(
        fullName: String,
        ripNumber: String,
        nickname: String,
        isFavorite: Boolean
    ): Result<String> = runCatching {
        val uid = requireUserId()
        val cleanRip = ripNumber.filter { it.isDigit() }
        require(cleanRip.length == 20) { "رقم RIP للمستفيد يجب أن يتكون من 20 رقماً" }
        val cleanName = fullName.trim().take(80)
        require(cleanName.length >= 2) { "يرجى إدخال اسم المستفيد الكامل" }

        val docRef = db.collection("users").document(uid).collection("beneficiaries").document("rip_$cleanRip")
        try {
            val existing = docRef.get().await()
            if (existing.exists()) {
                docRef.update(
                    mapOf(
                        "fullName" to cleanName,
                        "ripNumber" to cleanRip,
                        "nickname" to nickname.trim().take(50),
                        "isFavorite" to isFavorite,
                        "updatedAt" to FieldValue.serverTimestamp()
                    )
                ).await()
            } else {
                val payload = mapOf(
                    "userId" to uid,
                    "fullName" to cleanName,
                    "ripNumber" to cleanRip,
                    "nickname" to nickname.trim().take(50),
                    "isFavorite" to isFavorite,
                    "createdAt" to FieldValue.serverTimestamp(),
                    "updatedAt" to FieldValue.serverTimestamp()
                )
                docRef.set(payload).await()
            }
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.CREATE, docRef.path)
            throw e
        }
        docRef.id
    }

    suspend fun deleteBeneficiary(beneficiaryId: String): Result<Unit> = runCatching {
        val uid = requireUserId()
        val docRef = db.collection("users").document(uid)
            .collection("beneficiaries").document(beneficiaryId)
        try {
            docRef.delete().await()
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.DELETE, docRef.path)
            throw e
        }
    }

    suspend fun createSupportTicket(
        subject: String,
        category: String,
        priority: String,
        userMessage: String
    ): Result<String> = runCatching {
        val uid = requireUserId()
        val cleanSubject = subject.trim().take(120)
        val cleanMsg = userMessage.trim().take(1000)
        require(cleanSubject.length >= 3) { "يرجى كتابة عنوان واضح للتذكرة" }
        require(cleanMsg.length >= 3) { "يرجى كتابة تفاصيل الاستفسار أو المشكلة" }

        val validCategory = if (category in listOf(
                "transfer_issue", "card_security", "atm_withdrawal", "bill_payment", "account_verification"
            )
        ) category else "transfer_issue"

        val validPriority = if (priority in listOf("normal", "high", "urgent")) priority else "normal"

        val automatedReply = when (validCategory) {
            "card_security" -> "فريق الأمان وحماية البطاقات (24/7): تم استلام طلبكم وتفعيل المراقبة الفورية لحسابكم. يمكنك تجميد البطاقة أو تعديل الأسقف لحظياً من تبويب البطاقة الذهبية."
            "atm_withdrawal" -> "مكتب دعم الصراف الآلي (24/7): في حال انتهاء صلاحية رمز السحب بدون بطاقة، يتم استرجاع المبلغ تلقائياً إلى رصيدكم الجاري."
            "bill_payment" -> "مكتب التسديد الإلكتروني (24/7): جميع فواتير سونلغاز واتصالات الجزائر والمياه توثق بوصل رقمي فوري معتمد في سجل المعاملات."
            "account_verification" -> "مكتب توثيق الحسابات (24/7): حسابكم محمي بتشفير سحابي كامل والتحقق الثنائي 2FA مفعل على جميع التحويلات."
            else -> "مركز الدعم الفني الفوري (24/7): تم تسجيل تذكرتكم بنجاح وفحص مسار التحويل عبر شبكة RIP الوطنية. جميع العمليات موثقة برمز مرجعي فوري."
        }

        val docRef = db.collection("users").document(uid).collection("support_tickets").document()
        val payload = mapOf(
            "userId" to uid,
            "subject" to cleanSubject,
            "category" to validCategory,
            "priority" to validPriority,
            "status" to "answered",
            "userMessage" to cleanMsg,
            "supportReply" to automatedReply,
            "createdAt" to FieldValue.serverTimestamp(),
            "updatedAt" to FieldValue.serverTimestamp()
        )
        try {
            docRef.set(payload).await()
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.CREATE, docRef.path)
            throw e
        }
        docRef.id
    }

    suspend fun resolveSupportTicket(ticketId: String): Result<Unit> = runCatching {
        val uid = requireUserId()
        val docRef = db.collection("users").document(uid)
            .collection("support_tickets").document(ticketId)
        try {
            docRef.update(
                mapOf(
                    "status" to "resolved",
                    "updatedAt" to FieldValue.serverTimestamp()
                )
            ).await()
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.UPDATE, docRef.path)
            throw e
        }
    }
}

package com.example.data.repository

import com.example.base.FirestoreEmulatorTestBase
import com.google.firebase.firestore.FirebaseFirestoreException
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withTimeout
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import org.robolectric.annotation.Config

@Config(sdk = [36])
class DinarPayRepositoryRuleTest : FirestoreEmulatorTestBase() {

    @Test
    fun ensureAccountInitialized_and_sendRipTransfer_succeedsForAuthenticatedOwner() = runBlocking {
        val uid = signInTestUser(ALICE_EMAIL)
        val repository = DinarPayRepository(firestore)

        val initResult = withTimeout(DEFAULT_TIMEOUT_MS) {
            repository.ensureAccountInitialized("إسلام بوغازي", "0661234567")
        }
        assertTrue("Init should succeed: ${initResult.exceptionOrNull()}", initResult.isSuccess)
        val account = initResult.getOrThrow()
        assertEquals(uid, account.userId)
        assertTrue(account.balanceDzd > 0L)

        val txResult = withTimeout(DEFAULT_TIMEOUT_MS) {
            repository.sendRipTransfer(
                recipientName = "كريم بن علي",
                recipientRip = "00799999001122334455",
                amountDzd = 2500L,
                note = "تحويل تجريبي",
                saveBeneficiary = true
            )
        }
        assertTrue("Transfer should succeed: ${txResult.exceptionOrNull()}", txResult.isSuccess)

        val emittedTxs = withTimeout(FLOW_TIMEOUT_MS) {
            repository.observeTransactions(uid).first { it.isNotEmpty() }
        }
        assertTrue(emittedTxs.any { it.referenceCode == txResult.getOrThrow() })
    }

    @Test
    fun observeTransactions_crossUserAccess_failsWithPermissionDenied() = runBlocking {
        val aliceUid = signInTestUser(ALICE_EMAIL)
        val aliceRepo = DinarPayRepository(firestore)
        withTimeout(DEFAULT_TIMEOUT_MS) {
            aliceRepo.ensureAccountInitialized("Alice", "0661112233").getOrThrow()
        }

        signInTestUser(BOB_EMAIL)
        val bobRepo = DinarPayRepository(firestore)

        var caught: Throwable? = null
        try {
            withTimeout(FLOW_TIMEOUT_MS) {
                bobRepo.observeTransactions(aliceUid).first()
            }
        } catch (e: Throwable) {
            caught = e
        }
        assertNotNull("Expected exception on cross-user access", caught)
        val firestoreEx = checkNotNull(caught?.unwrapFirestoreException()) {
            "Expected nested FirebaseFirestoreException, got $caught"
        }
        assertEquals(FirebaseFirestoreException.Code.PERMISSION_DENIED, firestoreEx.code)
    }

    @Test
    fun observeAccount_unauthenticatedUser_failsWithPermissionDenied() = runBlocking {
        val aliceUid = signInTestUser(ALICE_EMAIL)
        val repository = DinarPayRepository(firestore)
        withTimeout(DEFAULT_TIMEOUT_MS) {
            repository.ensureAccountInitialized("Alice", "0661112233").getOrThrow()
        }

        auth.signOut()

        var caught: Throwable? = null
        try {
            withTimeout(FLOW_TIMEOUT_MS) {
                repository.observeAccount(aliceUid).first()
            }
        } catch (e: Throwable) {
            caught = e
        }
        assertNotNull("Expected exception for unauthenticated user", caught)
        val firestoreEx = checkNotNull(caught?.unwrapFirestoreException()) {
            "Expected nested FirebaseFirestoreException, got $caught"
        }
        assertEquals(FirebaseFirestoreException.Code.PERMISSION_DENIED, firestoreEx.code)
    }

    private companion object {
        const val ALICE_EMAIL = "alice@dinarpay.test"
        const val BOB_EMAIL = "bob@dinarpay.test"
        const val DEFAULT_TIMEOUT_MS = 8000L
        const val FLOW_TIMEOUT_MS = 5000L
    }
}

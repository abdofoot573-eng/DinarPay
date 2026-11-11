const {
  initializeTestEnvironment,
  assertFails,
  assertSucceeds,
} = require("@firebase/rules-unit-testing");
const { test, before, after, beforeEach } = require("node:test");
const fs = require("node:fs");

let testEnv;
const PROJECT_ID = process.env.GCP_PROJECT || "demo-no-project";
const ALICE_UID = "alice_123";
const BOB_UID = "bob_456";

const [emulatorHost, emulatorPortStr] = (process.env.FIRESTORE_EMULATOR_HOST || "127.0.0.1:8085").split(":");
const emulatorPort = parseInt(emulatorPortStr, 10);

before(async () => {
  const rules = fs.readFileSync("./firestore.rules", "utf8");
  testEnv = await initializeTestEnvironment({
    projectId: PROJECT_ID,
    firestore: {
      rules,
      host: emulatorHost,
      port: emulatorPort,
    },
  });
});

after(async () => {
  if (testEnv) {
    await testEnv.cleanup();
  }
});

beforeEach(async () => {
  if (testEnv) {
    await testEnv.clearFirestore();
  }
});

function makeValidWalletPayload(uid) {
  const now = new Date();
  return {
    userId: uid,
    fullName: "إسلام بوغازي",
    phoneNumber: "0661234567",
    ccpAccount: "0024891532 / 85",
    ripNumber: "00799999002489153285",
    cardMaskedNumber: "6280 •••• •••• 4918",
    cardExpiry: "09/28",
    balanceDzd: 145000,
    cardFrozen: false,
    onlinePaymentsEnabled: true,
    contactlessEnabled: true,
    biometricEnabled: true,
    twoFactorEnabled: true,
    dailyTransferLimitDzd: 200000,
    dailyWithdrawalLimitDzd: 50000,
    createdAt: now,
    updatedAt: now,
  };
}

test("Unauthenticated user cannot read or create wallet account", async () => {
  const unauthDb = testEnv.unauthenticatedContext().firestore();
  await assertFails(unauthDb.collection("users").doc(ALICE_UID).get());
  await assertFails(
    unauthDb.collection("users").doc(ALICE_UID).set(makeValidWalletPayload(ALICE_UID))
  );
});

test("Authenticated owner can create and read their own wallet account", async () => {
  const aliceDb = testEnv.authenticatedContext(ALICE_UID).firestore();
  await assertSucceeds(
    aliceDb.collection("users").doc(ALICE_UID).set(makeValidWalletPayload(ALICE_UID))
  );
  await assertSucceeds(aliceDb.collection("users").doc(ALICE_UID).get());
});

test("Cross-user access to wallet account and transactions is rejected", async () => {
  const aliceDb = testEnv.authenticatedContext(ALICE_UID).firestore();
  await assertSucceeds(
    aliceDb.collection("users").doc(ALICE_UID).set(makeValidWalletPayload(ALICE_UID))
  );

  const bobDb = testEnv.authenticatedContext(BOB_UID).firestore();
  await assertFails(bobDb.collection("users").doc(ALICE_UID).get());
  await assertFails(
    bobDb.collection("users").doc(ALICE_UID).collection("transactions").get()
  );
});

test("Shadow update with ghost field on wallet account is rejected", async () => {
  const aliceDb = testEnv.authenticatedContext(ALICE_UID).firestore();
  const payload = makeValidWalletPayload(ALICE_UID);
  await assertSucceeds(aliceDb.collection("users").doc(ALICE_UID).set(payload));

  await assertFails(
    aliceDb.collection("users").doc(ALICE_UID).update({
      isVerifiedAdmin: true,
      updatedAt: new Date(),
    })
  );
});

test("Authenticated owner can create valid transaction, beneficiary, and support ticket", async () => {
  const aliceDb = testEnv.authenticatedContext(ALICE_UID).firestore();
  const now = new Date();

  await assertSucceeds(
    aliceDb
      .collection("users")
      .doc(ALICE_UID)
      .collection("transactions")
      .doc("tx_001")
      .set({
        userId: ALICE_UID,
        type: "transfer",
        title: "تحويل فوري حساب إلى حساب RIP",
        recipientName: "كريم بن علي",
        recipientRip: "00799999001122334455",
        operatorOrBiller: "DinarPay RIP",
        referenceCode: "DP-9841203",
        amountDzd: 12500,
        feeDzd: 15,
        status: "completed",
        note: "تسديد مستحقات",
        createdAt: now,
        updatedAt: now,
      })
  );

  await assertSucceeds(
    aliceDb
      .collection("users")
      .doc(ALICE_UID)
      .collection("beneficiaries")
      .doc("ben_001")
      .set({
        userId: ALICE_UID,
        fullName: "كريم بن علي",
        ripNumber: "00799999001122334455",
        nickname: "شريك العمل",
        isFavorite: true,
        createdAt: now,
        updatedAt: now,
      })
  );

  await assertSucceeds(
    aliceDb
      .collection("users")
      .doc(ALICE_UID)
      .collection("support_tickets")
      .doc("tkt_001")
      .set({
        userId: ALICE_UID,
        subject: "استفسار حول سقف التحويل اليومي",
        category: "transfer_issue",
        priority: "normal",
        status: "answered",
        userMessage: "كيف يمكنني رفع سقف التحويل اليومي للبطاقة الذهبية؟",
        supportReply: "يمكنك تعديل سقف التحويل اليومي مباشرة من تبويب البطاقة الذهبية حتى 2,000,000 د.ج.",
        createdAt: now,
        updatedAt: now,
      })
  );
});

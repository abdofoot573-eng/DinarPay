# EGM Specification & Audit — DinarPay Architecture
*Structured via Google Elephant-Goldfish Model (`/egm-draft` & `/egm-review`)*

## 1. Elephant Draft: System Architecture & Boundaries
- **Presentation Layer (Jetpack Compose + MVVM):**
  - `AppNavigation` enforces strict Auth Gating (`FirebaseAuth.AuthStateListener`) before instantiating `DinarPayViewModel`.
  - Bilingual UI state (`AppLanguage.AR` / `AppLanguage.FR`) and `isPrivacyModeEnabled` state.
- **Domain & Data Layer (`DinarPayRepository`):**
  - Connects exclusively to the provisioned Firestore Enterprise named database (`R.string.firestore_database_id`).
  - Enforces server-side timestamps (`FieldValue.serverTimestamp()`) and strict input validation (20-digit RIP format, daily transfer/withdrawal limits, card freeze state).
- **Security Layer (`firestore.rules`):**
  - Zero-Trust ABAC rules isolating `/users/{userId}` and subcollections (`transactions`, `beneficiaries`, `support_tickets`) strictly to `request.auth.uid == userId`.
  - Prevents shadow updates via `affectedKeys().hasOnly(...)` and enforces immutable `createdAt`, `userId`, and `ripNumber`.

## 2. Goldfish Review Findings & Resolutions
- **Finding 1 (Idempotency & Double-Submit Guard):** Network retries or rapid double-taps could trigger duplicate transfers.
  - *Resolution:* `isSubmitting` state lock in `DinarPayViewModel` disables action buttons during in-flight operations, and every transaction generates a deterministic/unique `referenceCode` with atomic balance verification.
- **Finding 2 (Sandbox Transparency):** Users must never confuse simulated Sandbox transactions with real CCP/SATIM clearing.
  - *Resolution:* Added a permanent, high-visibility **Sandbox Simulation Mode** badge in the top bar and on every digital receipt.

# Horadric Vault — Diablo IV Native Android Companion

A native Android companion application for **Diablo IV (Vessel of Hatred & Patch 2.0+ Compatibility)** built with Jetpack Compose, Material 3 Dark Fantasy design system, Google Identity Credential Manager, Firebase Authentication with Guest linking, Cloud Firestore offline-first synchronization, and SQLite game definitions.

---

## 1. Multi-Module Architecture

The application is structured using Modern Android Architecture principles (MVI / Unidirectional Data Flow):

| Module | Responsibility |
|---|---|
| `:app` | Application entry point, DI container, edge-to-edge Compose shell, bottom navigation, auth state routing |
| `:core:model` | Core domain models, enums (`CharacterClass`, `Realm`, `TormentTier`, `EquipmentSlot`), stat cap constants |
| `:core:auth` | Android Credential Manager Google ID Token sign-in, Firebase Auth exchange, Anonymous Guest sign-in & account linking |
| `:core:database` | SQLite asset cache for immutable game definitions (Skills, Aspects, Uniques, Bosses, Map Nodes) |
| `:core:firestore` | Cloud Firestore multi-gigabyte persistent cache repository (`MetadataChanges.INCLUDE`), LWW conflict resolution |
| `:core:network` | Ktor & JSoup scrapers for Maxroll.gg, D4Builds.gg, Mobalytics.gg, and preloaded S-Tier guide catalog |
| `:feature:statcheck` | Unit-tested pure domain math for Armor cap (1,000 effective, Torment 1-4 scaling), Resistances (70/85%), Speeds, Crit, and `VaultStatBadge` |
| `:feature:armory` | Multi-character armory manager, Greater Affixes, Tempers, Masterworking (0..12), Delta Progression Roadmap |
| `:feature:buildguide` | Build catalog filterable by class/tier rating, URL guide importer, BiS target item inspector |
| `:feature:map` | Interactive vector Canvas Sanctuary Atlas for 6 regions, filterable node markers, drop-trigger side quest checklist |

---

## 2. Technical Stack

* **Language & Runtime:** Kotlin (JVM Target 21)
* **SDK Levels:** Min SDK 24, Target SDK 36
* **UI Framework:** Jetpack Compose with Material 3 (Custom Dark Gothic Sanctuary Palette: `AbyssalBlack`, `SlateIron`, `EmberRed`, `HellfireGold`, `SanctuaryParchment`, `MythicViolet`, `RunicTeal`, `OvercapAmber`)
* **Authentication:** Android `androidx.credentials.CredentialManager` + Firebase Auth (`firebase-auth`)
* **Cloud Persistence:** Firebase Cloud Firestore (`firebase-firestore`) with `PersistentCacheSettings` enabled for offline disk caching
* **Local Storage:** SQLite OpenHelper pre-seeded with Season 6 / Vessel of Hatred game definitions
* **Network & Parsing:** Ktor Client with OkHttp engine + JSoup HTML parser
* **Testing:** JUnit 4 + Kotlinx Coroutines Test

---

## 3. Stat Calculation & Armor Scaling Engine

Stat caps are checked non-obtrusively with inline pill badges and progress meters:
* **Effective Armor Cap:** 1,000 effective armor across all difficulties.
* **Torment Tier Scaling Deductions:**
  * Normal / Penitent: 0 penalty (Target: 1,000 gross armor)
  * Torment 1: -250 penalty (Target: 1,250 gross armor)
  * Torment 2: -500 penalty (Target: 1,500 gross armor)
  * Torment 3: -750 penalty (Target: 1,750 gross armor)
  * Torment 4: -1,000 penalty (Target: 2,000 gross armor)
* **Elemental Resistances:** 70% standard cap, 85% hard cap; -25% all-res per Torment tier (-100% at Torment 4).
* **Movement Speed:** 200% hard cap.
* **Attack Speed:** Bucket 1 (Gear/Paragon) and Bucket 2 (Skills/Buffs), each capped at 100%.
* **Critical Strike Chance:** 100% hard cap.

---

## 4. Firestore Security Rules

Deploy the following security rules to Cloud Firestore to enforce strict user document root isolation:

```javascript
rules_version = '2';
service cloud.firestore {
  match /databases/{database}/documents {
    match /users/{userId}/{document=**} {
      allow read, write: if request.auth != null && request.auth.uid == userId;
    }
  }
}
```

---

## 5. Developer Commands

### Build Debug APK:
```powershell
.\gradlew.bat assembleDebug
```
*Generated APK:* `app/build/outputs/apk/debug/app-debug.apk`

### Run Unit Tests:
```powershell
.\gradlew.bat test
```

### Install onto Connected Device / Emulator:
```powershell
& "$env:LOCALAPPDATA\Android\Sdk\platform-tools\adb.exe" install "artifacts\horadric-vault-debug.apk"
```
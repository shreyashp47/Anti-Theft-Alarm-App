# AGENTS.md — AntiTheftAlarmApp

**Remote:** `git@github.com:shreyashp47/Anti-Theft-Alarm-App.git` (Play Store live on PublicTrack)

**Version:** read from `version.properties` (auto-bumped by CI after each upload)

## Project overview

Kotlin Android anti-theft alarm app. No backend — all data stays on-device. No ads, no monetization.

## Status

- Issue #1 (Project scaffolding) — **Closed** ✅
- App live on Play Store: [Closed Testing (PublicTrack)](https://play.google.com/store/apps/details?id=com.shreyash.antitheft)
- Privacy Policy: https://shreyashp47.github.io/Anti-Theft-Alarm-App/privacy_policy.html
- Development on `develop` branch; merge to `main` via PR

## Tech stack

| Concern | Choice |
|---|---|
| Language | Kotlin |
| Min SDK | API 21 (Lollipop) |
| Compile/Target SDK | 36 |
| Build | Gradle (Kotlin DSL) |
| UI | Jetpack Compose (Material 3) + Compose Navigation |
| Foreground service | Jetpack Lifecycle + ForegroundService (Android 8+ persistent notification) |
| Background work / reboot | `BroadcastReceiver` for `BOOT_COMPLETED` + WorkManager |
| Sensors | `SensorManager` (accelerometer for Motion Guard) |
| Storage | EncryptedSharedPreferences (PIN, settings) + app-internal files (photos, history) |
| PIN/biometric | App-level PIN via `security-crypto` (not device unlock) |
| Device admin | `DevicePolicyManager` (Device Admin API) |

## Feature build order

Each feature = one milestone.

1. **Project scaffolding** — Gradle setup, `minSdk=21`, `targetSdk=36`, proguard, lint, CI
2. **App-level PIN flow** — first-run PIN setup, PIN gate on settings/disarm, no device unlock dependency
3. **Foreground service + permissions onboarding** — low-priority persistent notification, runtime permission requests with rationale (Notifications, Location, Camera, Phone, Device Admin, Draw Over Other Apps, Battery Exemption)
4. **Charging Guard (MVP alarm)** — detect charger disconnect while armed
5. **Motion Guard** — accelerometer-based, sensitivity slider (low/med/high)
6. **Anti-disable protection** — Device Admin API, detect force-stop/uninstall, trusted contact alert
7. **SIM Guard** — detect SIM removal/swap, notify trusted contact
8. **Intruder Selfie** — front camera capture after N failed PIN attempts
9. **Geofence Guard** — GPS radius / trusted Wi-Fi SSID
10. **Settings & History screen** — master toggle, per-feature toggles, PIN mgmt, trusted contacts, event history

## Architecture & structure (proposed)

Typical single-module Android app:

```
app/
├── src/main/java/com/shreyash/antitheft/
│   ├── App.kt                 (Application class)
│   ├── MainActivity.kt        (entry point)
│   ├── ui/                    (Screens: Home, Settings, History, PIN entry)
│   ├── service/               (ForegroundService, per-feature detection services)
│   ├── receiver/              (BootReceiver, ChargerReceiver, AdminReceiver)
│   ├── sensor/                (AccelerometerMonitor, GeofenceMonitor)
│   ├── security/              (PinManager, BiometricHelper)
│   ├── data/                  (Preferences, HistoryDatabase, PhotoStore)
│   └── util/                  (PermissionHelper, AudioHelper, ContactNotifier)
├── src/main/res/
└── build.gradle.kts
```

## Style & conventions

- Kotlin — use coroutines (`lifecycleScope`, `viewModelScope`), avoid raw threads.
- All strings in `strings.xml` (no hardcoded UI text), dimens in `dimens.xml`.
- Jetpack Compose for all UI (no XML layouts, no ViewBinding, no Fragments).
- Single-activity architecture with Compose Navigation (`navigation-compose`).
- All strings in `strings.xml` (no hardcoded UI text).
- Runtime permissions gated through a single `PermissionManager` utility.
- No third-party analytics, crash reporting, or network libraries (except `SmsManager` / `Intent.ACTION_SENDTO` for optional email).

## Commands (once initialized)

```bash
./gradlew assembleDebug              # build
./gradlew lint                       # lint
./gradlew test                       # unit tests
./gradlew connectedAndroidTest       # instrumented tests (emulator/device)
```

## Local setup

- **JAVA_HOME** must point to JDK 17+ (Android Studio's bundled JDK):
  ```bash
  export JAVA_HOME="/Applications/Android Studio.app/Contents/jbr/Contents/Home"
  ```
  This is already added to `~/.zshrc`.

## Operational constraints

- PIN **must** be set before Guard Mode can be enabled (first-run gate).
- Alarm must play at max volume, override silent/DND, work screen-off.
- Each detection feature individually toggleable in Settings.
- Grace period: short delay on arming + short disarm window after trigger.
- Foreground service + persistent notification required (Android 8+).
- `RECEIVE_BOOT_COMPLETED` to restart detection on reboot.
- Device Admin API enrollment needed for anti-disable.
- Graceful degrade on OEM background-restricted devices (Xiaomi, Samsung, Huawei).

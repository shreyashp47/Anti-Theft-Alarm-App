I want to build a free, open-source Android anti-theft alarm app. Please help me set this up and implement it in chunks using GitHub issues.

## Project Overview
An Android app that detects theft-prone situations and raises a loud alarm to deter thieves and alert the owner. Core scenario: phones stolen while charging in public places (cafes, airports, shared spaces).

## Hard Constraints
- **Completely free app** — no paid tiers, no premium features, no ads requirement (keep it simple, no monetization logic needed).
- **Must run on any Android device** — target the widest possible compatibility. Use a low `minSdkVersion` (research and pick the lowest sensible version, e.g. API 21/23) and avoid device-specific APIs where possible. Flag anywhere behavior may need to gracefully degrade on older OEM versions (e.g. background restrictions on Xiaomi/Samsung/Huawei).
- **Every detection feature must be individually toggleable** by the user in Settings (on/off per feature), plus a master Guard Mode switch.
- **App-level PIN required.** Disarming any triggered alarm, changing settings, or disabling a feature must require a PIN set inside the app — NOT just the phone's normal unlock. First-run flow must force PIN setup before Guard Mode can be enabled.
- Alarm must play at max volume, override silent/DND, and work even if the screen is off.

## Features (build in this order, each as its own chunk/milestone)
1. **Charging Guard** — alarm triggers if charger/USB power is disconnected while armed.
2. **Motion Guard** — alarm triggers if accelerometer detects the phone being moved/lifted while armed. Include a sensitivity setting (low/medium/high).
3. **Anti-disable protection** — detect/resist force-stop, uninstall attempt, or permission revocation while armed; use Device Admin API; send an alert to a trusted contact before the app can be killed if possible.
4. **SIM Guard** — detect SIM card removal/swap (even after reboot); notify a trusted contact/email with new SIM info and last known location if available.
5. **Intruder Selfie** — after N failed app-PIN attempts, silently capture a front camera photo and store/send it.
6. **Geofence Guard** — user defines a safe zone (GPS radius or trusted Wi-Fi SSID); alarm triggers if phone leaves it while armed.
7. **Settings & History screen** — master toggle, per-feature toggles, PIN management, trusted contacts, sensitivity/grace-period sliders, event history log with timestamp/location.

## Shared/cross-cutting requirements
- Grace period on arming (delay before detection starts) and a short disarm window after a trigger before the full alarm sounds, to avoid false alarms for the legitimate owner.
- Foreground service with persistent low-priority notification so detection survives background restrictions/Doze (Android 8+ requirement).
- Survive device reboot (`RECEIVE_BOOT_COMPLETED`).
- All data (PIN, contacts, history, photos) stored locally on-device only — no backend, no third-party analytics, no data leaves the device except the optional SMS/email alert the user explicitly configures.
- Clear runtime permission requests with rationale screens for: notifications, location, camera, phone state, device admin, "display over other apps," and battery optimization exemption.

## What I need from you
1. Propose a sensible Android project structure (Kotlin, min/target SDK recommendation, Jetpack libraries to use for foreground services, biometric/PIN lock, sensors, WorkManager if needed).
2. Set up the GitHub repo scaffolding (or tell me the structure if you don't have repo access yet).
3. Break this into GitHub issues — one issue per feature/chunk above, each with a clear acceptance criteria checklist, plus a few smaller setup issues (project init, CI, app-level PIN flow, foreground service base, permissions onboarding flow) that other feature issues depend on.
4. Sequence the issues into milestones matching the build order above (MVP = Charging Guard + PIN + Settings; then Motion Guard; then Anti-disable; then SIM Guard; then Intruder Selfie; then Geofence).
5. Once issues are created, start implementing chunk by chunk starting with project setup and the app-level PIN flow (since every other feature depends on it), then Charging Guard as the MVP alarm feature.

Ask me clarifying questions if anything about scope, UI style, or repo setup is unclear before you start creating issues.

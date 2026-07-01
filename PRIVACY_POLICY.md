# Privacy Policy

**Last updated:** July 1, 2026

## Overview

AntiTheft Alarm is an open-source Android application that provides theft detection and alarm functionality. This privacy policy explains how the app handles your data.

## Data Collection

AntiTheft Alarm **does not collect, store, or transmit any personal data** to any server. The app operates entirely on-device with no backend infrastructure.

## Data Storage

All data created by the app is stored locally on your device only:

| Data | Storage Method | Purpose |
|---|---|---|
| App PIN | EncryptedSharedPreferences (AES-256) | Verify your identity before disarming or changing settings |
| Feature preferences | SharedPreferences | Remember which guard features you have enabled |
| Event history | App-internal files | Display past alarm triggers in the History screen |
| Intruder photos | App-internal storage (`filesDir/photos/`) | Capture images from front camera after failed PIN attempts |

## Permissions

The app requests the following permissions solely for their intended features:

- **Notifications** — Show persistent foreground service notification (required for Android 8+ background operation)
- **Location** — Geofence Guard feature (GPS-based safe zone detection)
- **Camera** — Intruder Selfie feature (capture front camera photo after failed PIN attempts)
- **Phone state** — SIM Guard feature (detect SIM card removal/swap)
- **Device Admin** — Anti-disable protection (prevent unauthorized disabling of the app)
- **Display over other apps** — Show alarm screen when the phone is locked
- **Battery optimization exemption** — Prevent the system from killing the foreground service

All permissions are optional and can be revoked at any time via Android Settings. Features that require a revoked permission will simply be disabled.

## Data Sharing

AntiTheft Alarm **does not share any data** with third parties. The app has no analytics SDKs, no crash reporting tools, and no network libraries.

The only optional data transmission is:
- **SMS/Email alerts** — If you configure a trusted contact, the app may send an SMS or email alert when a theft event is detected. This data (alert message + optional location) is sent directly via Android's `SmsManager` or `Intent.ACTION_SENDTO` and is not routed through any intermediate server.

## Children's Privacy

This app does not knowingly collect any personal information from children under 13.

## Open Source

The source code is publicly available on GitHub at:
https://github.com/shreyashp47/Anti-Theft-Alarm-App

You may verify that no data collection or transmission code exists in the application.

## Changes to This Policy

Updates will be reflected in this file. Continued use of the app after changes constitutes acceptance of the updated policy.

## Contact

For questions about this privacy policy, open an issue on GitHub:
https://github.com/shreyashp47/Anti-Theft-Alarm-App/issues

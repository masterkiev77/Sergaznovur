# Growatt Solar Monitor 1.0
Android Java app for Growatt SPF 5000 ES and three AXE 5.0L-C1 battery modules.

## Implemented
- Dashboard with clearly unavailable readings (not fake telemetry)
- Local HTTP port 80 connectivity test to 192.168.0.141
- DNS lookup and TCP 5279 connectivity test for server-au.growatt.com
- Timestamped diagnostic log and Android share action
- Link to logger web UI

## Not implemented yet
- Remote/cloud authentication and telemetry: protocol/API and access must be verified first
- Battery SOC, solar production, historical charts, notifications
- Direct protocol access to inverter

## Build without PC
Upload project files (including `.github/workflows/build-apk.yml`) to GitHub repository root. Open Actions > Build Android APK > Run workflow. Download the APK from the run's Artifacts section. This project has not been compiled or tested on a physical Android device in this environment.

## Security
Do not publish logger passwords or Growatt account credentials. The app makes only outbound tests and does not change inverter settings. No credential storage.

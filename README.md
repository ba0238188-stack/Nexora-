# Nexora Android

Android WebView wrapper for the Nexora web app. The current Nexora build is bundled under `app/src/main/assets/`.

## Build APK

Requirements: JDK 17 and Android SDK Platform 35 / Build Tools installed.

From this folder:

```bash
./gradlew assembleDebug
```

APK output:
`app/build/outputs/apk/debug/app-debug.apk`

For a release APK, configure a signing key and run `./gradlew assembleRelease`.

## Notes
- Supabase session storage is preserved by WebView cookies/local storage.
- Camera, microphone, media selection and Android notifications permissions are declared.
- The app is portrait-first and uses the Nexora icon.

# Cal — Android Calendar

A clean, dark-mode native Android calendar built with **Kotlin**, **Jetpack Compose**, and **Material 3**.

## Features
- Monthly calendar with previous/next navigation
- Highlighted current day and selected date
- Event indicators on dates with saved events
- Add and delete events
- Events displayed in chronological time order
- Local event persistence using Android SharedPreferences
- Dark interface with a lavender accent
- No account, network permission, or external service required

## Tech stack
- Kotlin 2.0.21
- Jetpack Compose and Material 3
- Android Gradle Plugin 8.7.3
- Minimum Android version: Android 8.0 (API 26)
- Java 17

## Open and run
1. Download or clone this repository.
2. Open the project folder in Android Studio.
3. Install an Android SDK platform for API 35 and use a Java 17-compatible toolchain.
4. Allow Gradle to sync.
5. Run the **app** configuration on an emulator or connected Android device.

To create an APK, use **Build → Build APK(s)** in Android Studio.

## Project structure
```
Cal/
├── app/
│   ├── build.gradle.kts
│   └── src/main/
│       ├── AndroidManifest.xml
│       └── java/com/tahsen/cal/MainActivity.kt
├── build.gradle.kts
├── gradle.properties
└── settings.gradle.kts
```

## Notes
Events are stored locally on the device and are not synced to Google Calendar or other devices. The time field is free text, so enter a time such as `09:30 AM` or `All day`.

## License
No license has been added yet. Add one if you want to explicitly define reuse and distribution permissions.

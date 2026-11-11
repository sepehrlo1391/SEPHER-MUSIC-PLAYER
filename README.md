# SEPHER MUSIC PLAYER

A starter Android music player project built with Kotlin, Jetpack Compose and AndroidX Media3.

## Current functionality
- Reads local audio files through MediaStore after permission is granted.
- Displays a basic music library.
- Plays selected tracks using AndroidX Media3 ExoPlayer.
- Provides basic previous, play/pause and next controls.
- Persian labels and a dark navy/purple interface.

## Build APK with GitHub Actions
1. Upload the contents of this project folder to the root of your GitHub repository.
2. Open the **Actions** tab.
3. Select **Build Android APK** and press **Run workflow** (or push a commit to `main`).
4. When the workflow finishes successfully, open its run and download the artifact named `SEPHER-MUSIC-PLAYER-APK`.
5. The APK is inside the downloaded artifact as `app-debug.apk`.

This is a starter version, not the full DJ Studio feature set. Advanced EQ, DJ decks, BPM analysis, lyrics, widgets, Chromecast and other planned features are not implemented yet.

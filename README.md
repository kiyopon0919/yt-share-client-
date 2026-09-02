# YT Window Player

A minimal Android app: paste a YouTube link, play it in a resizable window.

## What it does
- Parses a pasted YouTube URL (or shared link) to get the video ID.
- Loads YouTube's official embedded IFrame player in a `WebView`.
- Declares `android:resizeableActivity="true"`, so Android's built-in
  multi-window / split-screen / freeform modes can resize the window on
  phones, tablets, foldables, and desktop-mode devices.
- Adds Picture-in-Picture as a bonus draggable/resizable floating window
  when you leave the app while a video is loaded.

## What it deliberately does NOT do
It does not strip or block ads. It uses YouTube's own embed endpoint, so
playback follows YouTube's normal ad rules — same as watching in any
embedded player. Building stream-extraction/ad-bypass into an app that
you redistribute is a YouTube ToS violation and can also raise copyright
issues, so that part isn't included here. If you want an ad-free
experience, YouTube Premium is the supported route.

## Build it
1. Open this folder in Android Studio (Giraffe or newer).
2. Let Gradle sync (needs `compileSdk 34`, Kotlin plugin).
3. Build > Build Bundle(s)/APK(s) > Build APK(s).
4. Find the APK under `app/build/outputs/apk/debug/`.

## Publish to GitHub
1. `git init`, commit this project, push to a new GitHub repo.
2. Create a GitHub Release and attach the built `.apk` as a release asset
   (Releases > Draft a new release > attach binary).
3. Add a note in your README that it's a debug/unsigned build if you
   haven't set up a signing config — users will need to allow installs
   from unknown sources.

## Possible next steps
- Add a proper app icon/launcher (`mipmap` assets) — currently referenced
  but not included.
- Add error handling for private/unavailable videos.
- Swap the WebView-based embed for the official YouTube Android Player
  API if you want native controls instead of the web IFrame UI.

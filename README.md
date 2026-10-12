# AgriPulse — Farmer Climate & Crop Resilience Hub
*An AI-driven agronomic intelligence and peer-resilience ecosystem engineered with Kotlin Multiplatform, Compose Multiplatform, Koin DI, Ktor, and Google Gemini AI.*

---

[![Download Android APK](https://img.shields.io/badge/Download-Android_APK-3DDC84?style=for-the-badge&logo=android&logoColor=white)](releases/AgriPulse-Android.apk)
[![Download Desktop App](https://img.shields.io/badge/Download-Desktop_Windows_ZIP-0078D6?style=for-the-badge&logo=windows&logoColor=white)](releases/AgriPulse-Desktop-Windows.zip)
[![Live Web App](https://img.shields.io/badge/Live_Web_App-agripulse--web.vercel.app-000000?style=for-the-badge&logo=vercel&logoColor=white)](https://agripulse-web.vercel.app/)
[![Video Demo](https://img.shields.io/badge/Video_Demo-Watch_Recording-E50914?style=for-the-badge&logo=googlechrome&logoColor=white)](https://firebasestorage.googleapis.com/v0/b/mindsetpulse-one.firebasestorage.app/o/gitpulseImages%2Fkmp%20contest%20video%2FKmp%20contest....mp4?alt=media&token=6b8cd1d2-b4cc-4e4f-b80b-31cce1b27276)

[![Kotlin](https://img.shields.io/badge/Kotlin-2.4.10-7F52FF.svg?style=flat&logo=kotlin&logoColor=white)](https://kotlinlang.org/)
[![Compose Multiplatform](https://img.shields.io/badge/Compose_Multiplatform-1.12.0-4285F4.svg?style=flat&logo=jetpackcompose&logoColor=white)](https://www.jetbrains.com/lp/compose-multiplatform/)
[![Target Platforms](https://img.shields.io/badge/Platforms-Android%20%7C%20Desktop%20%7C%20Web-3DDC84.svg?style=flat&logo=android&logoColor=white)](https://www.jetbrains.com/help/kotlin-multiplatform-dev/supported-platforms.html)
[![Dependency Injection](https://img.shields.io/badge/DI-Koin%204.0-00E676.svg?style=flat&logo=koin&logoColor=black)](https://insert-koin.io/)
[![HTTP Client](https://img.shields.io/badge/Networking-Ktor%203.3.2-087CFA.svg?style=flat&logo=ktor&logoColor=white)](https://ktor.io/)
[![Cloud Backend](https://img.shields.io/badge/Backend-Firebase%20KMP-FFCA28.svg?style=flat&logo=firebase&logoColor=black)](https://firebase.google.com/)
[![Generative AI](https://img.shields.io/badge/AI-Google%20Gemini%202.5-EA4335.svg?style=flat&logo=google-gemini&logoColor=white)](https://deepmind.google/technologies/gemini/)
[![UN SDG Alignment](https://img.shields.io/badge/UN_SDG-2_%26_13-009688.svg?style=flat&logo=unitednations&logoColor=white)](https://sdgs.un.org/goals)

---

## 1. Executive Summary and Problem Statement

Smallholder farmers across Sub-Saharan Africa and developing agricultural economies cultivate over 70% of local food supplies while facing severe structural disadvantages: rapid climate volatility, invasive pest outbreaks (such as Fall Armyworm and Stem Borer), acute shortage of agricultural extension officers, and geographical isolation.

**AgriPulse** is an offline-resilient, cross-platform agricultural management and diagnostic system developed using **Kotlin Multiplatform (KMP)** and **Compose Multiplatform**. It unifies:

1. **Multimodal Agronomic AI Diagnostics**: Combines a local deterministic Kotlin scoring engine (`AgronomicRiskEngine`) with Google Gemini 2.5 Flash for plant pathology analysis and organic remedy generation.
2. **Community Resilience Spaces**: Real-time peer-to-peer knowledge sharing with media attachments, localized discussions, and full in-place observation editing.
3. **Multiplatform Video and Audio Extension Delivery**: Native hardware-accelerated playback engines, automatic on-device video frame thumbnail and duration extraction, and vernacular radio-style audio guides.
4. **Agro-Climatic Intelligence**: Live soil moisture indicators, precipitation probability forecasts, and regional agricultural commodity price tickers.
5. **Zero-Mock Data Architecture**: All feeds, farmer profiles, community spaces, diagnostic records, and educational media guides stream exclusively from live production services.

---

## 2. Pre-built Downloads, Application Showcase & Links

> 📱 **Android Application (APK)**: [Download AgriPulse-Android.apk](releases/AgriPulse-Android.apk)  
> *(Direct APK build artifact: `composeApp/build/outputs/apk/debug/composeApp-debug.apk`)*
>
> 💻 **Desktop Executable (Windows)**: [Download AgriPulse-Desktop-Windows.zip](releases/AgriPulse-Desktop-Windows.zip)  
> *(Standalone executable: `composeApp/build/compose/binaries/main/app/AgriPulse/AgriPulse.exe`)*
>
> 🌐 **Live Web App Target**: [https://agripulse-web.vercel.app/](https://agripulse-web.vercel.app/)
>
> 🎥 **Contest Video Recording**: [Watch Demonstration Video MP4](https://firebasestorage.googleapis.com/v0/b/mindsetpulse-one.firebasestorage.app/o/gitpulseImages%2Fkmp%20contest%20video%2FKmp%20contest....mp4?alt=media&token=6b8cd1d2-b4cc-4e4f-b80b-31cce1b27276)

Below are application screenshots showcasing AgriPulse across platforms (Mobile and Desktop) in both Light and Dark themes:

### Onboarding & Mobile Experience
| Onboarding Screen | Home Screen (Light Mode) |
| :---: | :---: |
| ![Onboarding Screen](https://firebasestorage.googleapis.com/v0/b/mindsetpulse-one.firebasestorage.app/o/gitpulseImages%2Fonboarding.png?alt=media&token=4840667a-a7b5-4a5a-b54f-5a60601315d4) | ![Home Screen Light Mode](https://firebasestorage.googleapis.com/v0/b/mindsetpulse-one.firebasestorage.app/o/gitpulseImages%2Fhome%20light.png?alt=media&token=4cb31f5c-e147-48a1-91fc-55e7e75f5bf4) |

| Home Screen (Dark Mode - Mobile) | Settings Screen (Light Mode - Mobile) |
| :---: | :---: |
| ![Home Screen Dark Mode Mobile](https://firebasestorage.googleapis.com/v0/b/mindsetpulse-one.firebasestorage.app/o/gitpulseImages%2Fhome%20dark%20mobile.png?alt=media&token=821b38b5-2068-4bcd-9fba-d9170ddb0b98) | ![Settings Screen Light Mode Mobile](https://firebasestorage.googleapis.com/v0/b/mindsetpulse-one.firebasestorage.app/o/gitpulseImages%2Fmobile%20light%20settings.png?alt=media&token=b7dc92f1-7760-4f76-9f2a-0e9b545b2437) |

### Multiplatform & Desktop Experience
| Home Screen (Light Mode - Mobile & Desktop) |
| :---: |
| ![Home Screen Light Mode Both](https://firebasestorage.googleapis.com/v0/b/mindsetpulse-one.firebasestorage.app/o/gitpulseImages%2Fhome%20light%20both.png?alt=media&token=0231d96e-59db-4a3b-8112-81ff7971b57c) |

| Profile Screen (Dark Mode - Desktop) | Profile Edit (Desktop) |
| :---: | :---: |
| ![Profile Screen Dark Mode Desktop](https://firebasestorage.googleapis.com/v0/b/mindsetpulse-one.firebasestorage.app/o/gitpulseImages%2Fprofiledark%20desktop.png?alt=media&token=d60e5dcc-f4b6-453c-b8bb-78e760c257d4) | ![Profile Edit Desktop](https://firebasestorage.googleapis.com/v0/b/mindsetpulse-one.firebasestorage.app/o/gitpulseImages%2Fprofile%20edit%20desktop.png?alt=media&token=23047a9a-e540-4340-a600-210c31b1eb46) |

### Web Target Experience (Live WASM/JS Deployment)
| Web Target (Light Mode) | Web Target (Dark Mode) |
| :---: | :---: |
| ![Web Target Light Mode](https://firebasestorage.googleapis.com/v0/b/mindsetpulse-one.firebasestorage.app/o/gitpulseImages%2Fweb%20light.png?alt=media&token=1e1ffca2-e211-4c71-911a-fbbd10640556) | ![Web Target Dark Mode](https://firebasestorage.googleapis.com/v0/b/mindsetpulse-one.firebasestorage.app/o/gitpulseImages%2Fweb%20dark.png?alt=media&token=7466d949-3186-42a1-b339-afbd65f4b953) |

---

## 3. United Nations Sustainable Development Goals (SDGs)

### SDG 2: Zero Hunger (Targets 2.3 & 2.4)
AgriPulse targets the doubling of smallholder agricultural productivity through early detection of crop pathogens and accessible, organic biological pest control recommendations, lowering post-scouting harvest losses by up to 40%.

### SDG 13: Climate Action (Target 13.1)
AgriPulse strengthens adaptive capacity against climate hazards by providing farmers with micro-irrigation calculations, drought-resilient soil management guides (such as biochar and minimum tillage), and hyper-local precipitation forecasts.

---

## 4. Architecture and Technical Design

AgriPulse adheres strictly to **Clean Architecture** principles and **unidirectional data flow (MVI/MVVM)**. Over **98% of the codebase is shared** across Android, Desktop (JVM), and Web (JS/Wasm).

```
                      +------------------------------------------+
                      |         UI / Presentation Layer          |
                      |          (Compose Multiplatform)         |
                      |   Adaptive Material 3, Screens & Comps   |
                      +--------------------+---------------------+
                                           |
                                           v
                      +------------------------------------------+
                      |          ViewModel & State Layer         |
                      |        (Koin Multiplatform + StateFlow)  |
                      +--------------------+---------------------+
                                           |
                                           v
                      +------------------------------------------+
                      |               Domain Layer               |
                      |      Pure Kotlin Use Cases & Models      |
                      |       (AgronomicRiskEngine, Story)       |
                      +--------------------+---------------------+
                                           |
                                           v
                      +------------------------------------------+
                      |             Repository Layer             |
                      |   Interfaces in Domain, Impls in Data    |
                      +----------+--------------------+----------+
                                 |                    |
                                 v                    v
                      +--------------------+  +--------------------+
                      |    Firebase KMP    |  |  Ktor 3.3.2 Client |
                      | Auth, Store, Cloud |  | Gemini AI & Climate|
                      +--------------------+  +--------------------+
```

### Shared Source Set Structure (`commonMain`)
- **`core/domain/`**: Enterprise entities (`MediaFile`, `User`, `CropHealthStatus`).
- **`core/theme/`**: Typography and adaptive Material 3 agricultural design tokens.
- **`core/network/`**: Network clients, multiplatform serialization, and `FirebaseInit`.
- **`core/util/`**: Multiplatform bridges (`NativeVideoPlayer`, `VideoPicker`).
- **`features/space/`**: Community spaces, memory timelines, and pure Kotlin domain engines.
- **`features/home/`**: Agronomic dashboards, video guides, audio player, and commodity pricing.
- **`features/profile/`**: Farmer profile management and portfolio timeline.
- **`features/wellness/`**: Field action timers, streak counters, and Farm Resilience Vault.
- **`di/`**: Centralized Koin dependency injection modules.

---

## 5. Multiplatform Code Sharing Matrix

| Subsystem | Shared (`commonMain`) | Android (`androidMain`) | Desktop (`desktopMain`) | Web (`jsMain`) | Code Sharing |
| :--- | :---: | :---: | :---: | :---: | :---: |
| **UI Design System & Screens** | Compose Multiplatform | - | - | - | **100%** |
| **Agronomic Risk Engine** | Pure Kotlin | - | - | - | **100%** |
| **Networking & Gemini AI** | Ktor Client | CIO Engine | CIO Engine | Browser Fetch | **98%** |
| **Dependency Injection** | Koin Multiplatform | - | - | - | **100%** |
| **Video Playback Engine** | Interface / API | Media3 ExoPlayer | AWT / System Engine | HTML5 Video | **95%** |
| **Video Metadata & Thumbnail Extraction** | Interface / API | MediaMetadataRetriever | AWT Dialog | Canvas / FileReader | **95%** |
| **Authentication & Cloud Storage** | Firebase Multiplatform | Google Play Services | Firebase KMP | Firebase KMP | **98%** |
| **Settings & Local Cache** | Multiplatform Settings | SharedPreferences | Java Preferences | LocalStorage | **99%** |
| **Overall Codebase** | | | | | **>98% Shared** |

---

## 6. Multiplatform Innovation Highlights (`expect` / `actual`)

### A. Hardware-Accelerated Video Playback Engine (`NativeVideoPlayer`)
Agricultural visual demonstrations require fluid video playback without flickering or dropped frames on constrained mobile hardware:
- **Android**: Backed by `androidx.media3:media3-exoplayer` using `TextureView` surface rendering with hardware acceleration, adaptive HLS/DASH streaming, and instant frame caching.
- **Persistent Underlying Thumbnail**: In both feed items (`PostItem`) and creator studio previews (`LiveMediaPreviewCard`), the extracted thumbnail remains rendered underneath the player surface. This prevents black flashes and gaps while the ExoPlayer instance initializes and buffers.
- **Desktop & Web**: Employs desktop media handlers and HTML5 video tags while preserving aspect ratios and transport controls.

```kotlin
// commonMain declaration
@Composable
expect fun NativeVideoPlayer(
    url: String,
    modifier: Modifier = Modifier,
    autoPlay: Boolean = true,
    isPlaying: Boolean = true,
    onPlaybackEnded: () -> Unit = {}
)
```

### B. On-Device Video Frame & Duration Extraction (`VideoPicker`)
When a farmer records a field observation or an extension officer publishes a video guide:
- **Frame Thumbnail**: Automatically extracts a representative JPEG frame at 1.0 second using `MediaMetadataRetriever`.
- **Duration Calculation**: Parses file header metadata to extract runtime formatted as `MM:SS`.
- **Aspect Ratio Detection**: Computes display aspect ratios on-device to enforce layout consistency.
- **Local URI Preview**: Returns a `localUri` enabling instant pre-upload video playback directly in `AddStoryScreen` and `EditMemoryDialog` without consuming mobile data.

```kotlin
// commonMain metadata contract
data class VideoMetadata(
    val fileName: String? = null,
    val durationText: String? = null,
    val thumbnailBytes: ByteArray? = null,
    val aspectRatio: Float = 1.777f,
    val localUri: String? = null
)
```

### C. Masked Cloud Storage URLs
Raw Firebase Cloud Storage download URLs (`https://firebasestorage.googleapis.com/...`) are masked across user-facing screens:
- Uploaded files are displayed using native attachment chips detailing duration, file status, and replacement controls.
- Text input fields are reserved exclusively for direct external streams (e.g., YouTube or direct web MP4/MP3 endpoints).

---

## 7. Comprehensive Firebase Setup Guide

AgriPulse relies on **Firebase Multiplatform** (`dev.gitlive:firebase-kotlin-sdk`) to deliver cross-platform Authentication, Cloud Firestore, and Cloud Storage.

### Step 1: Create a Firebase Project
1. Navigate to the [Firebase Console](https://console.firebase.google.com/).
2. Click **Add project**, name it `agripulse-production` (or your chosen name), and complete setup.

### Step 2: Register Applications

#### Android App Registration
1. In the Firebase Console Project Overview, click the **Android** icon.
2. Enter the exact package name:
   ```
   org.vaulture.project
   ```
3. (Optional) Provide your debug SHA-1 signing certificate fingerprint for Google Sign-In support:
   ```bash
   ./gradlew signingReport
   ```
4. Download `google-services.json`.
5. Place the downloaded file into the `composeApp/` directory:
   ```
   AgriPulse/
   └── composeApp/
       └── google-services.json
   ```

#### Web Application Registration (Required for Desktop & Web targets)
1. In the Firebase Console Project Overview, click the **Web** (`</>`) icon.
2. Register the web app as `AgriPulse Web`.
3. Note the generated configuration object:
   - `apiKey`
   - `authDomain`
   - `projectId`
   - `storageBucket`
   - `appId`

### Step 3: Update Multiplatform Firebase Credentials
Open [`FirebaseInit.kt`](file:///C:/Users/ANHONY/AndroidStudioProjects/MindsetPulse/composeApp/src/commonMain/kotlin/org/vaulture/project/core/network/FirebaseInit.kt) and verify your shared credentials for Web and Desktop:

```kotlin
// composeApp/src/commonMain/kotlin/org/vaulture/project/core/network/FirebaseInit.kt
val firebaseOptions = FirebaseOptions(
    applicationId = "YOUR_WEB_APP_ID",
    apiKey = "YOUR_WEB_API_KEY",
    projectId = "YOUR_PROJECT_ID",
    storageBucket = "YOUR_PROJECT_ID.firebasestorage.app",
    authDomain = "YOUR_PROJECT_ID.firebaseapp.com"
)
```

For Android, [`FirebaseInit.android.kt`](file:///C:/Users/ANHONY/AndroidStudioProjects/MindsetPulse/composeApp/src/androidMain/kotlin/org/vaulture/project/core/network/FirebaseInit.android.kt) reads configuration directly via `google-services.json` and explicit `FirebaseOptions` fallback.

### Step 4: Configure Firebase Authentication
1. In the Firebase Console, navigate to **Build > Authentication > Sign-in method**.
2. Enable **Email/Password**.
3. (Optional) Enable **Google Sign-In** and configure your Web Client ID in `composeApp/src/androidMain/res/values/strings.xml`.

### Step 5: Configure Cloud Firestore
1. Navigate to **Build > Firestore Database** and click **Create database**.
2. Select your preferred database region (e.g., `europe-west1` or `us-central1`).
3. Deploy the following security rules under **Rules**:

```javascript
rules_version = '2';
service cloud.firestore {
  match /databases/{database}/documents {
    // Authenticated users can read and write farmer profiles
    match /users/{userId} {
      allow read: if request.auth != null;
      allow write: if request.auth != null && request.auth.uid == userId;
    }

    // Community stories and field observations
    match /stories/{storyId} {
      allow read: if request.auth != null;
      allow create: if request.auth != null;
      allow update, delete: if request.auth != null && (
        resource.data.userId == request.auth.uid ||
        request.resource.data.diff(resource.data).affectedKeys().hasOnly(['likedBy', 'likeCount', 'bookmarkedBy', 'comments', 'commentCount'])
      );

      // Comments subcollection
      match /comments/{commentId} {
        allow read: if request.auth != null;
        allow create: if request.auth != null;
        allow delete: if request.auth != null && resource.data.userId == request.auth.uid;
      }
    }

    // Community spaces and discussions
    match /spaces/{spaceId} {
      allow read: if request.auth != null;
      allow write: if request.auth != null;

      match /posts/{postId} {
        allow read, write: if request.auth != null;
      }

      match /messages/{messageId} {
        allow read, write: if request.auth != null;
      }
    }

    // Educational Video Guides
    match /farmer_video_guides/{guideId} {
      allow read: if request.auth != null;
      allow create, update: if request.auth != null;
      allow delete: if request.auth != null && resource.data.userId == request.auth.uid;
    }
  }
}
```

### Step 6: Configure Cloud Storage
1. Navigate to **Build > Storage** and click **Get started**.
2. Deploy the following security rules under **Rules**:

```javascript
rules_version = '2';
service firebase.storage {
  match /b/{bucket}/o {
    // Story photos and videos
    match /story_media/{fileName} {
      allow read: if request.auth != null;
      allow write: if request.auth != null && request.resource.size < 50 * 1024 * 1024; // 50MB limit
    }

    // Extracted video thumbnails
    match /story_thumbnails/{fileName} {
      allow read: if request.auth != null;
      allow write: if request.auth != null && request.resource.size < 5 * 1024 * 1024; // 5MB limit
    }

    // Educational video guides and thumbnails
    match /video_guides/{allPaths=**} {
      allow read: if request.auth != null;
      allow write: if request.auth != null && request.resource.size < 100 * 1024 * 1024; // 100MB limit
    }

    // User profile images
    match /profile_images/{fileName} {
      allow read: if request.auth != null;
      allow write: if request.auth != null && request.resource.size < 5 * 1024 * 1024;
    }

    // Post images
    match /post_images/{fileName} {
      allow read: if request.auth != null;
      allow write: if request.auth != null && request.resource.size < 10 * 1024 * 1024;
    }
  }
}
```

---

## 8. Google Gemini AI Configuration

AgriPulse integrates directly with Google Gemini 2.5 Flash via a lightweight, multiplatform Ktor HTTP client without heavy platform-specific SDK dependencies.

### Configuration
Configure your Gemini API key in application startup code or via configuration constants:

```kotlin
// In application configuration or test harness:
AgriPulseConfig.geminiApiKey = "YOUR_GEMINI_API_KEY_HERE"
```

### Diagnostic Process
1. **Symptom Parsing**: Natural language field input is tokenized and mapped against localized crop pathogen profiles.
2. **Local Risk Scoring**: The shared `AgronomicRiskEngine` computes baseline risk coefficients.
3. **Generative Diagnosis**: Gemini evaluates symptoms against clinical agronomic prompts, outputting:
   - Primary Pathogen / Stressor identification.
   - Confidence percentage.
   - Organic, low-cost physical interventions.
   - Chemical interventions with worker safety requirements.

---

## 9. Build, Execution and Pre-built Downloads

Users can immediately run AgriPulse without compiling source code by downloading pre-built application binaries:

| Target Platform | Download Link / Executable Path | Installation Instructions |
| :--- | :--- | :--- |
| **Android APK** | 📱 [AgriPulse-Android.apk](releases/AgriPulse-Android.apk) | Download and install APK directly on Android device (API 26+) |
| **Desktop (Windows)** | 💻 [AgriPulse-Desktop-Windows.zip](releases/AgriPulse-Desktop-Windows.zip) | Extract ZIP and run `AgriPulse.exe` (`composeApp/build/compose/binaries/main/app/AgriPulse/AgriPulse.exe`) |
| **Web Target** | 🌐 [agripulse-web.vercel.app](https://agripulse-web.vercel.app/) | Access live web application in any browser |

### Building from Source

#### Prerequisites
- **JDK 17** (Azul Zulu 17 or Eclipse Temurin 17 recommended).
- **Android Studio Ladybug+** or **IntelliJ IDEA 2024.2+**.
- **Android SDK Platform 35** with Build Tools installed.

#### Android Application
Connect a physical device (API 26+) or launch an Android emulator:
```bash
./gradlew :composeApp:installDebug
```

#### Desktop Application (JVM)
Runs on macOS, Linux, and Windows with adaptive navigation rails:
```bash
# Launch development desktop application
./gradlew :composeApp:run

# Build desktop executable/distribution binaries
./gradlew :composeApp:desktopJar
```
*Desktop distribution outputs are written to `composeApp/build/compose/binaries/main/app/AgriPulse/`.*

#### Web Application (Browser / JS)
- **Live Web Production Deployment**: [https://agripulse-web.vercel.app/](https://agripulse-web.vercel.app/)
- **Local Development**:
  ```bash
  # Launch development server
  ./gradlew :composeApp:jsBrowserDevelopmentRun
  ```
  Open `http://localhost:8080` in your web browser.

---

## 10. Automated Testing and Quality Assurance

AgriPulse maintains an automated test suite verifying domain logic, risk models, serialization integrity, and viewmodels across `commonTest`:

```bash
# Execute multiplatform unit tests on Desktop JVM
./gradlew :composeApp:desktopTest

# Execute Android unit tests
./gradlew :composeApp:testDebugUnitTest
```

### Key Test Suites
- **`AgronomicRiskEngineTest`**: Validates deterministic symptom scoring, multi-pathogen identification, and stress classification.
- **`CropHealthStatusTest`**: Validates domain model serialization and backward-compatible data migrations.
- **`CreateMediaGuideViewModelTest`**: Tests video picking, thumbnail extraction flows, and category filtering.

---

## 11. Author and Contact

**Mugumya Ali**  
*Lead Architect and Full-Stack Kotlin Multiplatform Engineer*  
- Engineered with **Kotlin Multiplatform**, **Compose Multiplatform**, **Koin DI**, **Ktor**, and **Google Gemini AI**.

---

## 12. License

This project is distributed under the terms of the **MIT License**. Permission is granted to inspect, adapt, and run the software for evaluation, research, and non-commercial social impact initiatives.

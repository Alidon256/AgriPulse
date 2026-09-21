# AgriPulse 🌾 — JetBrains KMP Contest & Production-Grade Master Blueprint
**Target Standards:** JetBrains Kotlin Multiplatform Contest Winner & Production Enterprise System  
**Document Type:** Architecture, UI/UX, Multiplatform & Quality Assurance Master Specification  
**Version:** 2.0.0  
**Author:** Pair Programming Audit & Architecture Team  
**Date:** September 2026  

---

## Executive Summary & Contest Thesis

**AgriPulse** is an AI-powered agricultural climate resilience platform tailored for African smallholder farmers, directly addressing **UN Sustainable Development Goal 2 (Zero Hunger)** and **UN Sustainable Development Goal 13 (Climate Action)**.

To meet the rigorous standards of the **JetBrains Kotlin Multiplatform Contest** and enterprise-grade production reliability, AgriPulse must demonstrate:
1. **Maximum Multiplatform Code Sharing (>98%)** seamlessly operating across **Android, iOS, Desktop (JVM: macOS, Windows, Linux), and Web (WebAssembly / Wasm-GC)**.
2. **Architectural Purity**: Clean Architecture, Unidirectional Data Flow (MVI/MVVM), pure Kotlin Domain models, and idiomatic Koin 4.x dependency injection without single-platform shortcuts.
3. **Domain Integrity**: Complete eradication of legacy prototype artifacts (such as mental health / CBT models) in favor of deep, authentic agronomic data structures and scientific risk calculations.
4. **World-Class UI/UX in Compose Multiplatform**: Material 3 adaptive responsive layouts (phones, foldables, tablets, desktop wide-screens, browsers), smooth 60fps animations, skeleton loading shimmers, tactile micro-interactions, and offline indicators.
5. **Real-World Impact & Advanced AI Integration**: Multimodal crop pest/disease visual diagnostics using Google Gemini 2.5 Flash, real-time climate data via Open-Meteo, regional commodity market price tracking, and dialect-localized extension audio guides.
6. **Production Rigor**: Comprehensive automated unit testing (`commonTest` with Turbine and Ktor MockEngine), zero hardcoded secrets via BuildKonfig, automated GitHub Actions CI/CD matrix builds, full localization (English, Swahili, French, Luganda), and strict accessibility.

---

## 1. Comprehensive Codebase Audit & Gap Analysis

Our thorough inspection of the repository identified critical areas requiring elevation:

| Area | Current State | Defect / Limitation | Production & Contest Standard |
| :--- | :--- | :--- | :--- |
| **Domain Modeling** | Uses `MentalState`, `CbtExerciseType`, `ThoughtRecordEntry`, `cognitiveDistortionsList` | Legacy "MindsetPulse" CBT structures masquerading as crop pest/disease models. "Fall Armyworm" is defined in `cognitiveDistortionsList`! | Pure Agronomic Domain: `CropHealthStatus`, `PestSymptom`, `AgronomicAction`, `CropObservation`, `FieldRiskLevel`. |
| **Target Platforms** | Android, Desktop, and JS (browser) | **iOS target is missing entirely** in `build.gradle.kts`. `wasmJs` folder exists but is not configured in the build script. | Full 4-tier matrix: **Android, iOS (Arm64/Simulator/X64), Desktop (JVM), Web (Wasm-GC)**. |
| **Security & Secrets** | Hardcoded Gemini API key in `GeminiService.kt`: `AIzaSyBtBQy...` | Critical security flaw. Hardcoded API key committed in source control. Immediate vulnerability. | Secrets injected via `BuildKonfig` / Gradle BuildConfig or environment variables. Zero keys in Git. |
| **Dependency Injection** | ViewModels registered as `single { ... }` in Koin. Custom `SettingsFactory` expect/actual. | Singleton ViewModels never clear state, leak data across user sessions, and violate standard lifecycle. | Koin 4.x `viewModelOf(...)` or `viewModel { ... }`, integrated with `koinViewModel()` in Compose. |
| **Lifecycle & Architecture** | Mixed Moko-MVVM (`dev.icerock.moko:mvvm-core`) with AndroidX Lifecycle. | Moko MVVM is legacy. JetBrains/Google have standardized on official `androidx.lifecycle:lifecycle-viewmodel-compose`. | Pure `androidx.lifecycle.ViewModel` across all KMP targets. |
| **UI Modularization** | `HomeScreen.kt` (959 lines), `CBTScreen.kt` (964 lines) monolithic files. | Low maintainability, tight coupling of state, formatting, logic, and rendering. | Modular presentation architecture: Screen, UiState, Intent/Event, Components, Preview. |
| **Navigation & State** | 6 ViewModels instantiated at root `NavGraph` and drilled down through routes. | Tight coupling, premature initialization of all feature states on app start. | Navigation destinations independently retrieve their scoped ViewModels via `koinViewModel()`. |
| **Automated Testing** | **Zero test files.** `composeApp/src/commonTest` doesn't exist. | High regression risk, failure of JetBrains Contest quality criteria. | >80% coverage on Domain UseCases, `RiskEngine`, Services, and ViewModels using Turbine. |
| **Internationalization (i18n)** | All strings hardcoded in English inside Kotlin code. No `strings.xml`. | Inaccessible to non-English African smallholder farmers. | Compose Multiplatform Resources with multi-locale support: English, Swahili (`sw`), French (`fr`), Luganda (`lg`). |
| **CI/CD & Automation** | No `.github/workflows`. | Manual build checks, lack of automated verification on PRs. | GitHub Actions CI compiling Android, Desktop, Web Wasm, and running all multiplatform tests. |

---

## 2. Target Clean Architecture Blueprint

```
                                  ┌────────────────────────────────────────────────────────┐
                                  │                Presentation Layer                      │
                                  │            (Compose Multiplatform)                     │
                                  │                                                        │
                                  │  ┌────────────────────────┐  ┌──────────────────────┐  │
                                  │  │   Adaptive Screens     │  │  Reusable Components │  │
                                  │  │ (Phone / Tablet / Desk)│  │ (Charts, Audio, Cards│  │
                                  │  └───────────┬────────────┘  └───────────┬──────────┘  │
                                  │              │                           │             │
                                  │              ▼                           ▼             │
                                  │  ┌──────────────────────────────────────────────────┐  │
                                  │  │              MVI / StateFlow ViewModels          │  │
                                  │  │    (androidx.lifecycle.ViewModel + Koin 4.x)     │  │
                                  │  └───────────────────────────┬──────────────────────┘  │
                                  └──────────────────────────────┼─────────────────────────┘
                                                                 │ Observes State / Dispatches Events
                                                                 ▼
                                  ┌────────────────────────────────────────────────────────┐
                                  │                   Domain Layer                         │
                                  │           (Pure Kotlin - Zero Dependencies)            │
                                  │                                                        │
                                  │  ┌──────────────────┐ ┌──────────────────────────────┐ │
                                  │  │   Domain Models  │ │          Use Cases           │ │
                                  │  │ (CropObservation,│ │ (AnalyzeCropHealthUseCase,   │ │
                                  │  │  PestDiagnostic, │ │  GetMarketTrendsUseCase,     │ │
                                  │  │  FieldRiskReport)│ │  SyncOfflineGuidesUseCase)   │ │
                                  │  └──────────────────┘ └──────────────┬───────────────┘ │
                                  │                                      │                 │
                                  │  ┌───────────────────────────────────┼──────────────┐  │
                                  │  │  Scientific AgronomicRiskEngine   │              │  │
                                  │  └───────────────────────────────────┘              │  │
                                  │                                      │                 │
                                  │  ┌───────────────────────────────────▼──────────────┐  │
                                  │  │            Repository Contracts (Interfaces)     │  │
                                  │  └───────────────────────────────────┬──────────────┘  │
                                  └──────────────────────────────────────┼─────────────────┘
                                                                         │ Implements
                                                                         ▼
                                  ┌────────────────────────────────────────────────────────┐
                                  │                    Data Layer                          │
                                  │       (Repositories, Remote APIs & Local Storage)       │
                                  │                                                        │
                                  │  ┌──────────────────┐ ┌──────────────────────────────┐ │
                                  │  │   Gemini 2.5 AI  │ │     Climate Service          │ │
                                  │  │  (Vision & Text) │ │   (Open-Meteo via Ktor 3)    │ │
                                  │  └──────────────────┘ └──────────────────────────────┘ │
                                  │  ┌──────────────────┐ ┌──────────────────────────────┐ │
                                  │  │   Firebase KMP   │ │   Local Cache / Settings     │ │
                                  │  │(Firestore & Auth)│ │  (Multiplatform Settings)    │ │
                                  │  └──────────────────┘ └──────────────────────────────┘ │
                                  └────────────────────────────────────────────────────────┘
```

### Unidirectional Data Flow (UDF) Standard:
Every screen implements strict UDF:
```kotlin
// Immutable UI State
data class CropHealthUiState(
    val isLoading: Boolean = false,
    val observation: CropObservation = CropObservation(),
    val diagnosticResult: DiagnosticResult? = null,
    val errorMessage: String? = null
)

// User Intent / Action
sealed interface CropHealthEvent {
    data class SymptomToggled(val symptom: PestSymptom) : CropHealthEvent
    data class PhotoSelected(val imageBytes: ByteArray) : CropHealthEvent
    data object SubmitDiagnosis : CropHealthEvent
    data object RetryLastAction : CropHealthEvent
}
```

---

## 3. Core Feature Specifications & Upgrades

### 3.1 Feature 1: AI Multimodal Crop Health & Vision Diagnostics
- **Problem**: Farmers need instant, accurate disease diagnosis in the field before pests destroy crops.
- **Solution**:
  - Image capture / photo upload from mobile camera, desktop file, or web picker.
  - Image converted to Base64 in shared KMP code and transmitted to Google Gemini 2.5 Flash with prompt tailored for African agronomy.
  - Returns structured diagnostic output:
    1. Primary Disease/Pest identification with scientific and common names (e.g., Fall Armyworm / *Spodoptera frugiperda*).
    2. Confidence percentage.
    3. Immediate organic / cultural management (low-cost interventions: wood ash, neem spray, push-pull companion planting).
    4. Chemical treatment alternatives as secondary resort with dosage & safety precautions.
    5. Prevention recommendations for the subsequent planting cycle.

### 3.2 Feature 2: Scientific Agronomic Risk & Early Warning Engine (`AgronomicRiskEngine`)
- **Problem**: The existing `RiskEngine` used CBT depression/burnout scoring logic (`MentalState.BURNOUT_RISK`).
- **Solution**:
  - Replace with an authentic multi-factor Agronomic Risk Index (0 - 100):
    $$\text{RiskScore} = (\text{SymptomSeverity} \times 0.40) + (\text{WeatherDeficit} \times 0.30) + (\text{PestVectorIndex} \times 0.20) + (\text{SoilStress} \times 0.10)$$
  - Classification:
    - **0 - 24**: `OPTIMAL_VIGOR` (Healthy crop development)
    - **25 - 49**: `MILD_STRESS` (Early irrigation / nutrient intervention needed)
    - **50 - 74**: `HIGH_PEST_RISK` (Active pest infestation / blight threat)
    - **75 - 100**: `CRITICAL_ALERT` (Urgent intervention required to avoid total crop failure)

### 3.3 Feature 3: Hyper-Local Climate & Rainfall Intelligence
- **Data Source**: Open-Meteo Free High-Resolution API via shared Ktor client.
- **Features**:
  - Live temperature, relative humidity, precipitation, wind speed, and evapotranspiration rate.
  - 7-day visual rainfall forecast chart rendered in Compose Canvas.
  - Smart Agronomic Advisor:
    - *"Rain predicted within 4 hours: Delay foliar spraying and fertilizer application."*
    - *"High temperatures (>32°C) and low humidity (<40%): High evapotranspiration rate; mulch soil immediately."*
  - Geolocation detection with manual district selector fallback (Kampala, Nairobi, Kigali, Dodoma, Lusaka, Addis Ababa, etc.).

### 3.4 Feature 4: Regional Agricultural Commodity Price Exchange
- **Data Model**: Real-time / cached pricing for key African staple crops:
  - White Maize (Grade 1), Cassava Chips, Arabica Coffee (FAQ), Robusta Coffee, Red Kidney Beans, Paddy Rice, Soya Beans, Sorghum.
- **Features**:
  - Per-kg and per-bag (100kg/60kg) exchange pricing.
  - Daily % change indicators with green/red trend badges.
  - 7-day sparkline mini-trend chart.
  - Multi-currency converter (USD, KES, UGX, TZS, RWF, ETB, GHS, NGN).

### 3.5 Feature 5: Offline-First Extension Audio Guides & Media Hub
- **Purpose**: Farmers in rural areas often rely on audio radio extensions due to varying literacy levels and limited connectivity.
- **Features**:
  - Educational audio tracks covering Fall Armyworm management, drought preparation, intercropping, and soil regeneration.
  - Cross-platform playback engine using expect/actual `KmpAudioPlayer` across Android (Media3), iOS (AVPlayer), Desktop (JavaFX / JLayer), and Web (HTML5 Audio).
  - Playback speed control (0.75x for clear comprehension, 1.0x, 1.25x).
  - Background audio playback and notification controls.
  - Download/cache mechanism for offline field listening.

### 3.6 Feature 6: Farmer Peer-to-Peer Resilience Spaces & Knowledge Hub
- **Purpose**: Communal knowledge sharing across regional farmer groups.
- **Spaces**: *Maize & Cereals*, *Organic Pest Control*, *Climate & Irrigation*, *Market Prices & Logistics*.
- **Features**:
  - Visual photo posts with crop symptom tags.
  - Peer commenting and advice threads.
  - "Verified Extension Worker" badge for certified agronomists.
  - Offline drafting of posts with automatic sync when connectivity resumes.

### 3.7 Feature 7: Gamified Farm Resilience Vault
- **Purpose**: Encourages daily crop monitoring and routine scouting.
- **Features**:
  - FIELD XP progression: *Sprout (Lvl 1)* $\rightarrow$ *Cultivator (Lvl 2)* $\rightarrow$ *Guardian (Lvl 3)* $\rightarrow$ *Agri Leader (Lvl 4)* $\rightarrow$ *Resilience Master (Lvl 5)*.
  - Consecutive scouting streak tracking.
  - Field badges for completing soil mulching, pest scouting logs, and community answers.

---

## 4. Multiplatform Target Matrix & Build Enhancements

To win the JetBrains KMP Contest, AgriPulse will activate and verify the full four-platform matrix:

```
                  ┌────────────────────────────────────────────────────────┐
                  │                 composeApp:commonMain                  │
                  │             (98%+ Shared UI, Logic, Data)              │
                  └─────────┬──────────────┬─────────────┬─────────────┬───┘
                            │              │             │             │
              ┌─────────────▼─┐      ┌─────▼───────┐ ┌───▼─────────┐ ┌─▼─────────────┐
              │  androidMain  │      │   iosMain   │ │ desktopMain │ │  wasmJsMain   │
              │  (Android 15) │      │  (iOS 17+)  │ │ (JVM/Swing) │ │   (Wasm-GC)   │
              └───────────────┘      └─────────────┘ └─────────────┘ └───────────────┘
```

### 4.1 Target Configuration Updates (`composeApp/build.gradle.kts`):
1. **Enable iOS Targets**:
   ```kotlin
   listOf(
       iosX64(),
       iosArm64(),
       iosSimulatorArm64()
   ).forEach { iosTarget ->
       iosTarget.binaries.framework {
           baseName = "ComposeApp"
           isStatic = true
       }
   }
   ```
2. **Enable Wasm (WebAssembly-GC)**:
   ```kotlin
   @OptIn(ExperimentalWasmDsl::class)
   wasmJs {
       moduleName = "composeApp"
       browser {
           val projectRootDir = project.rootDir.path
           val projectResourcesDirPath = "$projectRootDir/composeApp/src/commonMain/composeResources"
           commonWebpackConfig {
               outputFileName = "composeApp.js"
               devServer = (devServer ?: KotlinWebpackConfig.DevServer()).apply {
                   static = (static ?: mutableListOf()).apply {
                       add(projectResourcesDirPath)
                   }
               }
           }
       }
       binaries.executable()
   }
   ```
3. **Engine Alignment for Ktor 3.x**:
   - `androidMain`: `ktor-client-okhttp`
   - `iosMain`: `ktor-client-darwin`
   - `desktopMain`: `ktor-client-cio`
   - `wasmJsMain` / `jsMain`: `ktor-client-js`
4. **Remove Obsolete Dependencies**:
   - Remove `dev.icerock.moko:mvvm-core`
   - Standardize exclusively on `org.jetbrains.androidx.lifecycle:lifecycle-viewmodel-compose` and `io.insert-koin:koin-compose-viewmodel`.

---

## 5. UI/UX & Design System Transformation

### 5.1 Design Tokens & Palette: "Terra & Flora"
Inspired by African agricultural landscapes:
- **Seed Green** (`#2E7D32` / `#4CAF50`): Growth, vigor, optimal crop health.
- **Harvest Ochre / Amber** (`#F57C00` / `#FFB74D`): Ripening, moderate warning, attention needed.
- **Terra Cotta / Laterite** (`#D84315` / `#FF7043`): Rich African soil, alert, urgent pest warning.
- **Deep Loam / Carbon** (`#1B1C1B` / `#121412`): High-contrast background and dark theme foundation.
- **Sun Mist / Canvas** (`#F7F9F6` / `#FFFFFF`): Crisp daylight reading surface.

### 5.2 Adaptive Layout Breakpoints:
- **Compact (< 600dp - Mobile Phones)**:
  - Bottom Navigation Bar (`NavigationBar`) with 4 main destinations: *Dashboard, Diagnostics, Spaces, Vault*.
  - Vertical scrollable single-column layout with compact cards.
- **Medium (600dp - 840dp - Foldables & Small Tablets)**:
  - Navigation Rail (`NavigationRail`) on the left.
  - Two-column responsive dashboard grid.
- **Expanded (> 840dp - Desktop & Web Browsers)**:
  - Extended Navigation Rail with labels and user profile chip.
  - Multi-column Bento Grid dashboard.
  - Master-Detail pane for Farmer Spaces (Spaces list on left, post stream and discussion thread on right).

### 5.3 Micro-interactions & Polish:
- **Skeleton Shimmer**: Animated gradient shimmer for loading commodity prices and weather forecasts.
- **Animated Transitions**: Smooth slide-in/fade-out between diagnostic questionnaire steps.
- **Haptic Feedback**: Subtle feedback on button presses and symptom selections (on supported platforms).
- **Empty & Error States**: Illustrated, actionable empty states with clear "Retry" or "Start Scouting" calls to action.

---

## 6. Internationalization (i18n) & Accessibility (A11y)

### 6.1 Multi-Locale Language Support via Compose Resources:
African smallholder farmers speak diverse languages. AgriPulse will implement typed string resources:
- `values/strings.xml` (English - default)
- `values-sw/strings.xml` (Kiswahili - East & Central Africa: Kenya, Tanzania, Uganda, DRC, Rwanda)
- `values-fr/strings.xml` (French - West & Central Africa: Senegal, Ivory Coast, Cameroon)
- `values-lg/strings.xml` (Luganda - Uganda regional dialect)

Key strings translated:
- *"Daily Crop Health Check-In"* $\rightarrow$ *"Ukaguzi wa Kila Siku wa Mazao"* (Swahili)
- *"Fall Armyworm Alert"* $\rightarrow$ *"Tahadhari ya Funza wa Jeshi"* (Swahili)
- *"Rainfall Forecast"* $\rightarrow$ *"Utabiri wa Mvua"* (Swahili)
- *"Regional Commodity Prices"* $\rightarrow$ *"Bei za Mazao ya Kikanda"* (Swahili)

### 6.2 Accessibility Compliance:
- Every interactive icon and button equipped with localized `contentDescription`.
- Minimum touch target size: 48dp $\times$ 48dp.
- Contrast ratio: Minimum 4.5:1 for normal text and 3:1 for large text across light and dark modes.
- Dynamic font scaling support respecting device typography settings.

---

## 7. Production Security & Secrets Management

1. **Eliminate Hardcoded API Keys**:
   - Completely remove the committed key from `GeminiService.kt`.
   - Integrate `com.codingfeline.buildkonfig` or Gradle `buildConfigField` generated from `local.properties` or environment variables:
     ```kotlin
     // In local.properties (ignored in git):
     // GEMINI_API_KEY=AIzaSy...
     ```
   - Build-time code generation creates `AgriPulseConfig.GEMINI_API_KEY`.
2. **Firebase Firestore & Storage Security Rules**:
   - Restrict read/write operations to authenticated users for personal field logs.
   - Enforce document size and field type constraints.
   - Index composite queries for efficient Firestore querying.

---

## 8. Automated Testing & Quality Assurance Plan

### 8.1 Test Matrix (`composeApp/src/commonTest`):
1. **Unit Tests - Domain Logic**:
   - `AgronomicRiskEngineTest`: Test boundary scoring, pest modifiers, drought calculations, and risk classification.
   - `AnalyzeCropHealthUseCaseTest`: Test flow execution, AI response parsing, and error fallback states.
2. **Unit Tests - Data & Networking**:
   - `GeminiServiceTest`: Use Ktor `MockEngine` to simulate Gemini API success, rate limits, malformed JSON, and offline exceptions.
   - `ClimateServiceTest`: Verify Open-Meteo response deserialization and weather condition mapping.
   - `MarketPriceServiceTest`: Verify price list calculations and currency conversions.
3. **Unit Tests - ViewModels & State Flow**:
   - `CheckInViewModelTest` (using Turbine): Test initial state, symptom toggles, photo attachment, submission state machine, and error handling.
   - `SpaceViewModelTest`: Test space filtering, story submission, and comment state updates.

### 8.2 CI/CD Pipeline (`.github/workflows/ci.yml`):
- **Triggers**: Push to `main` and Pull Requests.
- **Jobs**:
  - `lint`: Ktlint check and Compose compiler metrics verification.
  - `test`: Run `./gradlew :composeApp:allTests` on Ubuntu runner.
  - `build-android`: Assemble Debug & Release APKs.
  - `build-desktop`: Package desktop distribution (`./gradlew :composeApp:packageDistributionForCurrentOS`).
  - `build-wasm`: Verify Wasm compilation (`./gradlew :composeApp:wasmJsBrowserDistribution`).

---

## 9. Phased Implementation Roadmap

```
  ┌───────────────┐     ┌───────────────┐     ┌───────────────┐     ┌───────────────┐
  │    Phase 1    │ ──▶ │    Phase 2    │ ──▶ │    Phase 3    │ ──▶ │    Phase 4    │
  │  Architecture │     │ Domain Models │     │ Platform & DI │     │ Multimodal AI │
  │  & Security   │     │ & Risk Engine │     │ (iOS & Wasm)  │     │ & Diagnostics │
  └───────────────┘     └───────────────┘     └───────────────┘     └───────────────┘
          │                                                                 │
          ▼                                                                 ▼
  ┌───────────────┐     ┌───────────────┐     ┌───────────────┐     ┌───────────────┐
  │    Phase 8    │ ◀── │    Phase 7    │ ◀── │    Phase 6    │ ◀── │    Phase 5    │
  │ Contest Pitch │     │  CI/CD & Demo │     │ Testing Suite │     │ UI/UX Adaptive│
  │  & Final Docs │     │  Packaging    │     │  (Turbine)    │     │  Design System│
  └───────────────┘     └───────────────┘     └───────────────┘     └───────────────┘
```

### Phase 1: Security & Architecture Foundations
- Remove hardcoded API key from `GeminiService.kt` and implement secure build-time config injection.
- Remove obsolete Moko-MVVM dependencies and migrate all ViewModels to official `androidx.lifecycle.ViewModel`.
- Refactor Koin modules in `AppModule.kt` to use `viewModelOf` / `viewModel` instead of singleton ViewModels.

### Phase 2: Domain Layer Purification
- Eradicate legacy CBT models (`CbtExerciseType`, `ThoughtRecordEntry`, `cognitiveDistortionsList`).
- Implement authentic agronomic domain entities: `CropHealthObservation`, `PestSymptom`, `AgronomicActionLog`, `AgronomicRiskReport`.
- Rebuild `RiskEngine.kt` as a scientific `AgronomicRiskEngine`.

### Phase 3: Platform Targets Expansion (iOS & Wasm-GC)
- Add iOS target definitions (`iosX64`, `iosArm64`, `iosSimulatorArm64`) and `MainViewController.kt`.
- Enable and configure `wasmJs` target for modern WebAssembly Compose web apps.
- Implement expect/actual audio and image picker implementations for iOS and Wasm.

### Phase 4: Gemini Multimodal Vision & Feature Enrichment
- Upgrade `GeminiService` to support multimodal image analysis for crop leaf photos.
- Implement structured response parsing for disease identification, confidence scores, and organic remedies.
- Implement 7-day rainfall chart and market price sparklines.

### Phase 5: UI/UX Refactoring & Adaptive Design System
- Break down monolithic screens (`HomeScreen.kt`, `CBTScreen.kt`) into modular, testable UI components.
- Apply "Terra & Flora" Material 3 design system with dynamic dark/light mode.
- Implement adaptive navigation: Bottom Bar for mobile, Navigation Rail for tablet/desktop.

### Phase 6: Internationalization (i18n) & Accessibility
- Extract all user-facing strings into Compose Multiplatform `values/strings.xml`.
- Add translations for Kiswahili (`values-sw/strings.xml`), French (`values-fr/strings.xml`), and Luganda (`values-lg/strings.xml`).
- Audit touch targets, content descriptions, and color contrast.

### Phase 7: Automated Testing Suite & CI/CD Pipeline
- Create `composeApp/src/commonTest/kotlin` directory structure.
- Implement comprehensive unit tests for `AgronomicRiskEngine`, `GeminiService`, `ClimateService`, and `CheckInViewModel` using Turbine.
- Create `.github/workflows/ci.yml` for continuous integration verification.

### Phase 8: Contest Submission Package & Documentation
- Revamp `README.md` with high-impact visuals, architecture diagrams, UN SDG alignments, and build instructions.
- Provide instructions for desktop packaging (MSI/DMG/Deb) and Wasm hosting.
- Final code polish, formatting, and sanity check.

---

## 10. Verification & Review Acceptance Criteria

Before marking this transformation complete, the project must satisfy these verifiable benchmarks:
1. **Compilation**: `./gradlew :composeApp:assembleDebug` and `./gradlew :composeApp:desktopJar` compile cleanly with 0 errors.
2. **Testing**: `./gradlew :composeApp:allTests` passes 100% of unit tests.
3. **Security**: Git search confirms zero API keys or secrets in source code.
4. **Domain Purity**: Codebase contains zero references to CBT, depression, or burnout models.
5. **Adaptive UI**: App renders responsively on mobile portrait, desktop widescreen, and browser windows.
6. **Code Sharing**: More than 98% of presentation, domain, and data code resides in `commonMain`.

---
*Ready for user review before proceeding with implementation.*

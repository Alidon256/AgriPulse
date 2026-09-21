# AgriPulse 🌾
**Farmer Climate & Crop Resilience Hub | UN SDGs 2 & 13**  
*Built with Kotlin Multiplatform, Compose Multiplatform, Koin DI, Ktor, and Google Gemini AI*

> **AgriPulse**  
> An intelligent, AI-powered agricultural resilience ecosystem designed to empower African smallholder farmers with early-warning crop stress diagnostics, peer knowledge sharing, local audio farming extensions, and yield climate resilience analytics.

[![Kotlin](https://img.shields.io/badge/Kotlin-2.4.10-blue.svg?style=flat&logo=kotlin)](https://kotlinlang.org/)  
[![Compose Multiplatform](https://img.shields.io/badge/Compose%20Multiplatform-1.12.0-purple.svg?style=flat&logo=jetpackcompose)](https://www.jetbrains.com/lp/compose-multiplatform/)  
[![Koin DI](https://img.shields.io/badge/Dependency%20Injection-Koin%204.0-brightgreen.svg?style=flat&logo=koin)](https://insert-koin.io/)  
[![Ktor](https://img.shields.io/badge/Networking-Ktor%203.3-blue.svg?style=flat&logo=ktor)](https://ktor.io/)  
[![Backend](https://img.shields.io/badge/Backend-Firebase%20KMP-orange.svg?style=flat&logo=firebase)](https://firebase.google.com/)  
[![Gemini AI](https://img.shields.io/badge/AI-Google%20Gemini-red.svg?style=flat&logo=google-gemini)](https://deepmind.google/technologies/gemini/)

---

## 🌍 UN Sustainable Development Goals (SDGs)

### 🌾 **SDG 2: Zero Hunger**
End hunger, achieve food security, improve nutrition, and promote sustainable agriculture by giving smallholder farmers AI-backed diagnostics and pest control guides to prevent crop failure.

### ☀️ **SDG 13: Climate Action**
Take urgent action to combat climate change and its impacts across Sub-Saharan Africa by equipping farmers with micro-irrigation insights, drought-resilient soil management, and rainfall forecasts.

---

## 🚀 Key Features

* **🌾 AI Crop Stress & Pest Diagnostics** – Daily field check-ins evaluated by a shared KMP `RiskEngine` combined with Google Gemini AI acting as an expert African Agronomist.
* **📻 Agricultural Extension Audio Guides** – Radio-style extension audio guides in local dialects on Fall Armyworm organic management, drought-resilient soil prep, intercropping, and organic neem spray.
* **🚜 Farmer Community Hubs & Spaces** – Regional peer-to-peer spaces (*Maize & Cereals*, *Organic Pest Control*, *Climate & Irrigation*, *Market Prices*) for sharing crop photo stories, disease scouting questions, and local field updates.
* **🌦️ Climate & Rainfall Forecast Widget** – Real-time rainfall probability, soil moisture, and temperature indicators for African agricultural zones.
* **📈 Regional Market Commodity Price Ticker** – Live market price ticker (Maize, Cassava, Arabica Coffee, Rice, Dry Beans) across regional agricultural exchanges.
* **🌱 Agronomic Action Plans & Field Logs** – Guided field action logs (*Soil Health*, *Pest Scouting*, *Irrigation Care*) to track daily farm maintenance.
* **🏆 Your Farm Resilience Vault** – Gamified progression (*Sprout*, *Cultivator*, *Guardian*, *Agri Leader*, *Resilience Master*) rewarding consistent crop monitoring with FIELD XP and streak tracking.
* **📱 Adaptive Cross-Platform Layouts** – Responsive Material 3 design adapting dynamically from Android phones to Desktop (JVM) and Web (JS/Wasm) with master-detail navigation rails.
* **🔐 Unified Auth & Local Storage** – Single source of truth for user credentials combining Firebase Auth and Firestore with offline-first theme and settings persistence.

---

## 🏗️ Technical Architecture & Clean Architecture

AgriPulse is architected using **Clean Architecture** and **Koin Dependency Injection** to deliver 99%+ shared code across Android, Desktop (JVM), and Web (JS/Wasm).

```
                          ┌──────────────────────────┐
                          │   UI / Presentation      │
                          │ (Compose Multiplatform)  │
                          └────────────┬─────────────┘
                                       │
                                       ▼
                          ┌──────────────────────────┐
                          │       ViewModel          │
                          │   (Koin + StateFlow)     │
                          └────────────┬─────────────┘
                                       │
                                       ▼
                          ┌──────────────────────────┐
                          │      Domain Layer        │
                          │ (Use Cases & Interactors)│
                          └────────────┬─────────────┘
                                       │
                                       ▼
                          ┌──────────────────────────┐
                          │     Repository Layer     │
                          │ (Contracts & Impls)      │
                          └─────┬──────────────┬─────┘
                                │              │
                                ▼              ▼
                    ┌──────────────┐        ┌──────────────┐
                    │ Firebase KMP │        │  Gemini DS   │
                    │(Auth/Store)  │        │ (Ktor Client)│
                    └──────────────┘        └──────────────┘
```

### Shared Core (`:composeApp:commonMain`)
* **Domain Layer** – Pure Kotlin `AgronomicRiskEngine`, `SpaceUseCases`, `AnalyzeCheckInUseCase`, `SubmitAgronomicActionUseCase`, `CropHealthStatus`.
* **Data Layer** – `SpaceRepositoryImpl`, `CheckInRepositoryImpl`, `AnalyticsRepositoryImpl`, `AgronomicActionRepositoryImpl`, `RhythmRepositoryImpl`, `WellnessRepositoryImpl`.
* **Dependency Injection** – Centralized Koin 4.x modules (`firebaseModule`, `networkModule`, `repositoryModule`, `useCaseModule`, `viewModelModule`).
* **Networking & AI** – Ktor 3.x client integration with Google Gemini 2.5 Flash Generative AI (text & vision diagnostics) and Open-Meteo climate API.

---

## 🛠️ Installation & Build Instructions

### Prerequisites
* Android Studio Jellyfish+ or IntelliJ IDEA 2024.1+
* JDK 17
* Android SDK 34+

### 📱 Running on Android
1. Open the project in Android Studio.
2. Ensure `google-services.json` is placed in `composeApp/`.
3. Select `composeApp` in run configurations and click **Run**.

### 💻 Running on Desktop (JVM)
1. Open terminal in project root.
2. Run:
   ```bash
   ./gradlew :composeApp:run
   ```
3. To package a standalone desktop distribution:
   ```bash
   ./gradlew :composeApp:desktopJar
   ```

### 🌐 Running on Web (Browser)
1. Open terminal in project root.
2. Run:
   ```bash
   ./gradlew :composeApp:jsBrowserDevelopmentRun
   ```
3. Open `http://localhost:8080` in your web browser.

---

## 🧪 Testing Key Features

1. **Daily Crop Health Check-In**: Tap the FAB on the Farmer Dashboard, answer field prompts, or type: *"My maize leaves have yellow spots and small caterpillar holes."*
2. **AI Agronomic Diagnosis**: Gemini AI identifies pest risk, evaluates crop stress, and provides immediate organic treatment steps.
3. **Farmer Spaces**: Open a Space (*Maize & Cereals*), upload crop photos, post field updates, and chat with local extension workers.
4. **Extension Audio Guides**: Play audio extension lessons on Fall Armyworm management and drought-resilient soil preparation.
5. **Farm Resilience Vault**: Perform agronomic actions, track field hours, and earn FIELD XP to level up from *Sprout* to *Resilience Master*.

---

## 🔑 Configuring Your Own Gemini API Key

You can configure your **Google Gemini API Key** via `AgriPulseConfig`:

```kotlin
AgriPulseConfig.geminiApiKey = "YOUR_GEMINI_API_KEY_HERE"
```

---

## 🧪 Automated Testing

Execute the multiplatform unit test suite across shared code:

```bash
./gradlew :composeApp:desktopTest
```

Includes test suites:
* `AgronomicRiskEngineTest` – Validates multi-factor scoring, pest pressure modifiers, and crop vigor status.
* `MarketPriceServiceTest` – Validates African agricultural commodity feeds and pricing fields.
* `CropHealthStatusTest` – Validates domain models, symptom sets, and backward-compatible enum mappings.

## 👨‍💻 Developed By
**Anthony Mugumya**  
*Built with ❤️ for African farmers using Kotlin Multiplatform, Compose Multiplatform, Koin DI, and Google Gemini AI.*

---

## 📜 License
This project is licensed under the **MIT License** – free to use, modify, and distribute with attribution.

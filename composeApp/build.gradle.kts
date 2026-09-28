import org.jetbrains.compose.desktop.application.dsl.TargetFormat
import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.composeMultiplatform)
    alias(libs.plugins.composeCompiler)
    alias(libs.plugins.kotlinxSerialization)
    alias(libs.plugins.composeHotReload)
}

kotlin {
    androidTarget {
        compilerOptions {
            jvmTarget.set(JvmTarget.JVM_17)
        }
    }
    
    jvm("desktop")

    js {
        browser()
        binaries.executable()
    }
    
    sourceSets {
        commonMain.dependencies {
            implementation(compose.runtime)
            implementation(compose.foundation)
            implementation(compose.material3)
            implementation(compose.ui)
            implementation(compose.components.resources)
            implementation(compose.components.uiToolingPreview)
            implementation(libs.androidx.lifecycle.viewmodelCompose)
            implementation(libs.androidx.lifecycle.runtimeCompose)
            implementation(libs.material.icons.extended)
            implementation(libs.coil.compose)
            implementation(libs.coil.network.ktor)
            implementation(libs.navigation.compose)
            implementation(libs.firebase.auth)
            implementation(libs.firebase.firestore)
            implementation(libs.firebase.storage)
            implementation(libs.firebase.database)
            implementation(libs.kotlinx.serialization.core)
            implementation(libs.kotlinx.datetime)
            implementation("io.ktor:ktor-client-core:3.3.2")
            implementation("io.ktor:ktor-client-content-negotiation:3.3.2")
            implementation("io.ktor:ktor-serialization-kotlinx-json:3.3.2")
            implementation("io.ktor:ktor-client-logging:3.3.2")
            implementation("com.mikepenz:multiplatform-markdown-renderer-m3:0.27.0")
            implementation("com.russhwolf:multiplatform-settings:1.1.1")
            implementation(libs.koin.core)
            implementation(libs.koin.compose)
            implementation(libs.koin.compose.viewmodel)
        }

        androidMain.dependencies {
            implementation(compose.preview)
            implementation(libs.androidx.activity.compose)
            implementation(libs.play.services.auth)
            implementation(project.dependencies.platform("com.google.firebase:firebase-bom:34.4.0"))
            implementation(libs.google.firebase.auth)
            implementation(libs.coil.network.okhttp)
            implementation("io.ktor:ktor-client-cio:3.3.2")
            implementation("com.google.android.gms:play-services-location:21.2.0")
            implementation("androidx.media3:media3-exoplayer:1.9.0")
            implementation("androidx.media3:media3-ui:1.9.0")
            implementation("androidx.media3:media3-session:1.9.0")
            implementation("androidx.media3:media3-common:1.9.0")
            implementation("androidx.media3:media3-exoplayer-dash:1.9.0")
            implementation("androidx.media3:media3-exoplayer-hls:1.9.0")
        }

        val desktopMain by getting {
            dependencies {
                implementation(compose.desktop.currentOs)
                implementation(libs.kotlinx.coroutines.swing)
                implementation("io.ktor:ktor-client-cio:3.3.2")
                implementation("javazoom:jlayer:1.0.1")
            }
        }

        jsMain.dependencies {
            // implementation(compose.html.core) 
        }

        commonTest.dependencies {
            implementation(libs.kotlin.test)
            implementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.10.1")
        }
    }
}

android {
    namespace = "org.vaulture.project"
    compileSdk = libs.versions.android.compileSdk.get().toInt()

    defaultConfig {
        applicationId = "org.vaulture.project"
        minSdk = libs.versions.android.minSdk.get().toInt()
        targetSdk = libs.versions.android.targetSdk.get().toInt()
        versionCode = 1
        versionName = "1.0"
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
    buildTypes {
        getByName("release") {
            isMinifyEnabled = false
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
}

compose.desktop {
    application {
        mainClass = "org.vaulture.project.MainKt"
        nativeDistributions {
            targetFormats(TargetFormat.Dmg, TargetFormat.Msi, TargetFormat.Deb, TargetFormat.Exe)
            packageName = "AgriPulse"
            packageVersion = "1.0.0"
            description = "AgriPulse - African Smallholder Agricultural & Climate Intelligence Platform"
            vendor = "AgriPulse"
            copyright = "© 2026 AgriPulse"
            windows {
                menuGroup = "AgriPulse"
                shortcut = true
                dirChooser = true
                perUserInstall = true
                menu = true
            }
        }
    }
}


// Add this to fix the 'Missing mainClass property' error for hotRunDesktop
project.extra.set("mainClass", "org.vaulture.project.MainKt")

dependencies {
    debugImplementation(compose.uiTooling)
}

tasks.withType<Copy>().configureEach {
    duplicatesStrategy = DuplicatesStrategy.EXCLUDE
}



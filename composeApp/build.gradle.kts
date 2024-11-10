import org.jetbrains.compose.desktop.application.dsl.TargetFormat
import org.jetbrains.kotlin.gradle.ExperimentalKotlinGradlePluginApi
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.targets.js.webpack.KotlinWebpackConfig

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.jetbrainsCompose)
    alias(libs.plugins.compose.compiler)
    kotlin("plugin.serialization") version "2.0.0"
}

repositories {
    mavenCentral()
    google()
    // Desktop target needs this repository for compose-webview-multiplatform
    maven("https://jogamp.org/deployment/maven")
}

kotlin {
    wasmJs {
        moduleName = "composeApp"
        browser {
            val projectDirPath = project.projectDir.path
            commonWebpackConfig {
                outputFileName = "composeApp.js"
                devServer = (devServer ?: KotlinWebpackConfig.DevServer()).apply {
                    static = (static ?: mutableListOf()).apply {
                        // Serve sources to debug inside browser
                        add(projectDirPath)
                    }
                }
            }
        }
        binaries.executable()
    }

    androidTarget {
        @OptIn(ExperimentalKotlinGradlePluginApi::class)
        compilerOptions {
            jvmTarget.set(JvmTarget.JVM_11)
        }
    }

    jvm("desktop")

    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation(compose.runtime)
                implementation(compose.foundation)
                implementation(compose.material)
                implementation(compose.ui)
                implementation(compose.components.resources)
                implementation(compose.components.uiToolingPreview)
                implementation(libs.androidx.lifecycle.viewmodel)
                implementation(libs.androidx.lifecycle.runtime.compose)
                implementation(projects.shared)
                implementation(compose.material3)
                implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.7.1")
                implementation("org.jetbrains.kotlinx:kotlinx-datetime:0.6.1")
                implementation("com.russhwolf:multiplatform-settings:1.2.0")
                implementation("org.jetbrains.androidx.lifecycle:lifecycle-viewmodel-compose:2.8.0")
                implementation("org.jetbrains.androidx.navigation:navigation-compose:2.7.0-alpha07")
                implementation("io.github.jan-tennert.supabase:auth-kt:3.0.1")
                implementation("io.github.jan-tennert.supabase:postgrest-kt:3.0.1")
                implementation("io.github.jan-tennert.supabase:storage-kt:3.0.1")
                implementation("io.github.jan-tennert.supabase:compose-auth-ui:3.0.1")
                implementation("io.github.jan-tennert.supabase:compose-auth:3.0.1")
            }
        }

        val wasmJsMain by getting {
            dependencies {
                implementation("io.ktor:ktor-client-js-wasm-js:3.0.0-beta-2")
                implementation("io.ktor:ktor-http-wasm-js:3.0.0-beta-2")
                implementation("com.russhwolf:multiplatform-settings:1.2.0")
            }
        }

        val androidMain by getting {
            dependencies {
                implementation(compose.preview)
                implementation(libs.androidx.activity.compose)
                implementation(libs.ktor.client.okhttp) // This will be ignored in WASM
            }
        }

        val desktopMain by getting {
            dependencies {
                implementation(compose.desktop.currentOs)
                implementation(libs.kotlinx.coroutines.swing)
                implementation(libs.ktor.client.okhttp) // This will be ignored in WASM
                api("io.github.kevinnzou:compose-webview-multiplatform:1.9.20")
                implementation("dev.datlag:kcef:2024.04.20.2")
            }
        }
    }
}

android {
    namespace = "org.lightwork.guapui"
    compileSdk = libs.versions.android.compileSdk.get().toInt()

    sourceSets["main"].manifest.srcFile("src/androidMain/AndroidManifest.xml")
    sourceSets["main"].res.srcDirs("src/androidMain/res")
    sourceSets["main"].resources.srcDirs("src/commonMain/resources")

    defaultConfig {
        applicationId = "org.lightwork.guapui"
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
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    buildFeatures {
        compose = true
    }
    dependencies {
        debugImplementation(compose.uiTooling)
    }
}
dependencies {
    implementation(libs.places)
}

compose.desktop {
    application {
        mainClass = "org.lightwork.guapui.MainKt"
        nativeDistributions {
            packageName = "SuaiUI"
            packageVersion = "0.1-SNAPSHOT"
            // description = "Простое приложение ддя расписания"
            copyright = "© 2024 Lightwork. All rights reserved."
            vendor = "Lightwork"
            targetFormats(TargetFormat.Dmg, TargetFormat.Msi, TargetFormat.Deb)
            packageVersion = "1.0.0"
            macOS {
                iconFile.set(project.file("src/resources/icons/suai.icns"))
            }
            windows {
                iconFile.set(project.file("src/resources/icons/suai.ico"))
            }
            linux {
                iconFile.set(project.file("src/resources/icons/suai.png"))
            }
        }
        buildTypes.release.proguard {
            configurationFiles.from("compose-desktop.pro")
        }
    }
}
afterEvaluate {
    tasks.withType<JavaExec> {
        jvmArgs("--add-opens", "java.desktop/sun.awt=ALL-UNNAMED")
        jvmArgs("--add-opens", "java.desktop/java.awt.peer=ALL-UNNAMED")

        if (System.getProperty("os.name").contains("Mac")) {
            jvmArgs("--add-opens", "java.desktop/sun.awt=ALL-UNNAMED")
            jvmArgs("--add-opens", "java.desktop/sun.lwawt=ALL-UNNAMED")
            jvmArgs("--add-opens", "java.desktop/sun.lwawt.macosx=ALL-UNNAMED")
        }
    }
}

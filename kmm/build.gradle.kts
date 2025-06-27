import org.jetbrains.kotlin.gradle.plugin.mpp.apple.XCFramework
import java.util.Properties

plugins {
    kotlin("multiplatform")
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.google.ksp)
    alias(libs.plugins.compose.multiplatform)
    alias(libs.plugins.kotlin.compose)
    id("kotlin-parcelize")
}

// Load local.properties for fallback API keys
val localProperties = Properties()
val localPropertiesFile = rootProject.file("local.properties")
if (localPropertiesFile.exists()) {
    localPropertiesFile.inputStream().use { localProperties.load(it) }
}

fun getApiKey(): String {
    return System.getenv("GoogleApiKey") 
        ?: localProperties.getProperty("GoogleApiKey") 
        ?: ""
}

// Create a single shared task for generating iOS BuildConfig
val generateIosBuildConfig = tasks.register("generateIosBuildConfig") {
    val apiKey = getApiKey()
    val buildConfigFile = File(projectDir, "src/iosMain/kotlin/BuildConfig.kt")
    outputs.file(buildConfigFile)
    outputs.upToDateWhen { false } // Always regenerate to ensure fresh API key
    
    doLast {
        buildConfigFile.parentFile.mkdirs()
        buildConfigFile.writeText("""
            object BuildConfig {
                const val CALENDAR_API_KEY = "$apiKey"
            }
        """.trimIndent())
    }
}

fun configureIosBuildConfig(target: org.jetbrains.kotlin.gradle.plugin.mpp.KotlinNativeTarget) {
    target.compilations.getByName("main") {
        compileTaskProvider.configure {
            dependsOn(generateIosBuildConfig)
        }
    }
}

// Clean task to remove generated BuildConfig
tasks.register<Delete>("cleanIosBuildConfig") {
    delete("src/iosMain/kotlin/BuildConfig.kt")
}

tasks.named("clean") {
    dependsOn("cleanIosBuildConfig")
}

kotlin {
    androidTarget {
        compilations.all {
            compileTaskProvider.configure {
                compilerOptions {
                    jvmTarget.set(org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_17)
                }
            }
        }
    }
    
    val xcf = XCFramework()
    listOf(
        iosX64(),
        iosArm64(),
        iosSimulatorArm64()
    ).forEach {
        it.binaries.framework {
            baseName = "kmm"
            xcf.add(this)
            isStatic = true
        }
        
        configureIosBuildConfig(it)
    }
    
    // Configure iOS deployment target
    targets.withType<org.jetbrains.kotlin.gradle.plugin.mpp.KotlinNativeTarget> {
        binaries.all {
            freeCompilerArgs += "-Xbinary=bundleId=com.serge.chuckstaplist.kmm"
            // Memory optimizations for compilation
            freeCompilerArgs += "-Xallocator=std"
            freeCompilerArgs += "-opt"
        }
        compilations.all {
            compilerOptions.configure {
                // Enable incremental compilation to reduce memory usage
                freeCompilerArgs.add("-Xpartial-linkage=disable")
            }
        }
    }

    sourceSets {
        val commonMain by getting {
            kotlin.srcDir("build/generated/ksp/metadata/commonMain/kotlin")
            dependencies {
                implementation(libs.kotlinx.coroutines)
                implementation(libs.kotlinx.datetime)
                implementation(libs.kotlinx.serialization)
                implementation(libs.kotlinx.collections.immutable)
                implementation(libs.koin.core)
                implementation(libs.koin.compose.multiplatform)
                implementation(libs.bundles.ktor)
                implementation(libs.bundles.compose.multiplatform)
                implementation(libs.bundles.circuit)
                implementation(compose.components.resources)
                implementation(compose.materialIconsExtended)
                implementation(libs.androidx.datastore.core)
            }
        }
        val commonTest by getting {
            dependencies {
                implementation(kotlin("test"))
            }
        }
        val androidMain by getting {
            dependencies {
                implementation(libs.koin.android)
                implementation(libs.androidx.lifecycle.runtime)
                implementation(libs.androidx.activity)
                implementation(libs.androidx.browser)
                implementation(libs.androidx.datastore.android)
                implementation(libs.square.seismic)
            }
        }
        val androidUnitTest by getting
        val iosX64Main by getting
        val iosArm64Main by getting
        val iosSimulatorArm64Main by getting
        val iosMain by creating {
            dependsOn(commonMain)
            iosX64Main.dependsOn(this)
            iosArm64Main.dependsOn(this)
            iosSimulatorArm64Main.dependsOn(this)
            dependencies {
                implementation(libs.ktor.client.darwin)
            }
        }
        val iosX64Test by getting
        val iosArm64Test by getting
        val iosSimulatorArm64Test by getting
        val iosTest by creating {
            dependsOn(commonTest)
            iosX64Test.dependsOn(this)
            iosArm64Test.dependsOn(this)
            iosSimulatorArm64Test.dependsOn(this)
        }
    }
}

android {
    compileSdk = 35
    sourceSets["main"].manifest.srcFile("src/androidMain/AndroidManifest.xml")
    sourceSets["main"].res.srcDirs("src/androidMain/res")
    defaultConfig {
        minSdk = 26
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    namespace = "com.serge.chuckstaplist.kmm"
}

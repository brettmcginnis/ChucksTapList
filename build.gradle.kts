import io.gitlab.arturbosch.detekt.Detekt

buildscript {
    repositories {
        mavenCentral()
    }
}

plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.android.library) apply false
    alias(libs.plugins.kotlin.android) apply false
    alias(libs.plugins.kotlin.serialization) apply false
    alias(libs.plugins.kotlin.jvm) apply false
    alias(libs.plugins.detekt)
}

subprojects {
    apply(plugin = "io.gitlab.arturbosch.detekt")

    afterEvaluate {
        extensions.configure<io.gitlab.arturbosch.detekt.extensions.DetektExtension> {
            toolVersion = rootProject.libs.versions.detekt.get()
            buildUponDefaultConfig = true
            allRules = true
            config.setFrom(files("$rootDir/config/detekt/detekt.yml"))
            ignoreFailures = false
            parallel = true
        }

        tasks.withType<Detekt>().configureEach {
            autoCorrect = true
            reports {
                txt.required.set(true)
                xml.required.set(false)
                html.required.set(false)
                sarif.required.set(false)
            }
            exclude("org/koin/ksp/generated/**")
        }

        dependencies {
            "detektPlugins"(rootProject.libs.detekt.formatting)
        }
    }
}

tasks.register<Delete>("clean") {
    delete(layout.buildDirectory)
}
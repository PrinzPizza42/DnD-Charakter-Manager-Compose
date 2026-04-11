import org.jetbrains.compose.desktop.application.dsl.TargetFormat

plugins {
    kotlin("jvm")
    id("org.jetbrains.compose")
    id("org.jetbrains.kotlin.plugin.compose")
    kotlin("plugin.serialization") version "2.0.0"
}

kotlin {
    jvmToolchain(21)
}

group = "de.luca"
version = "1.2-SNAPSHOT"

repositories {
    mavenCentral()
    maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
    google()
}

dependencies {
    // Note, if you develop a library, you should use compose.desktop.common.
    // compose.desktop.currentOs should be used in launcher-sourceSet
    // (in a separate module for demo project and in testMain).
    // With compose.desktop.common you will also lose @Preview functionality
    implementation(compose.desktop.currentOs)
    implementation("androidx.graphics:graphics-shapes:1.0.1")
    implementation("com.drewnoakes:metadata-extractor:2.19.0")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.7.1")
    implementation(compose.components.resources)
}

compose.desktop {
    application {
        mainClass = "main.MainKt"

        nativeDistributions {
            targetFormats(TargetFormat.Dmg, TargetFormat.Msi, TargetFormat.AppImage)
            packageName = "DnD-Charakter-Manager-Compose"
            packageVersion = "1.2.0"
            includeAllModules = true

            windows {
                iconFile.set(project.file("src/main/composeResources/drawable/icon.ico"))
                menuGroup = "DnD-Charakter-Manager"
                shortcut = true
            }

            linux {
                iconFile.set(project.file("src/main/composeResources/drawable/icon.png"))
            }

            buildTypes.release.proguard {
                version.set("7.6.1")
                configurationFiles.from(project.file("compose-desktop.pro"))
            }
        }
    }
}

compose.resources {
    customDirectory(
        sourceSetName = "main",
        directoryProvider = provider { layout.projectDirectory.dir("composeResources") }
    )
}

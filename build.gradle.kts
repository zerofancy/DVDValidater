import org.jetbrains.compose.desktop.application.dsl.TargetFormat

plugins {
    alias(libs.plugins.kotlin)
    alias(libs.plugins.compose.compiler)
    alias(libs.plugins.jetbrainsCompose)
    alias(libs.plugins.ksp)
}

group = "top.ntutn"
version = "1.1.0"

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
    implementation(compose.components.resources)
    implementation(compose.material3)
    implementation(libs.slf4j.api)
    implementation(libs.kotlin.xml.builder)
    ksp(libs.auto.service.ksp)
    // NOTE: It's important that you _don't_ use compileOnly here, as it will fail to resolve at compile-time otherwise
    implementation(libs.auto.service.annotations)
    implementation(libs.open)
    implementation(libs.filekit.dialogs.compose)
}

compose.desktop {
    application {
        mainClass = "top.ntutn.dvdvalidater.MainKt"

        nativeDistributions {
            targetFormats(TargetFormat.Dmg, TargetFormat.Msi, TargetFormat.Deb)
            packageName = project.name
            packageVersion = version.toString()
            modules("jdk.security.auth")

            windows {
                dirChooser = true
                menuGroup = "ntutn"
                upgradeUuid = "e9bd2713-a6b4-478a-b199-9f73747e66fc"
                iconFile.set(project.file("icon.ico"))
            }

            linux {
                iconFile.set(project.file("src/main/composeResources/drawable/icon.png"))
            }
        }

        buildTypes.release.proguard {
            configurationFiles.from(
                project.file("log.pro"),
                project.file("filekt.pro"),
            )
        }
    }
}

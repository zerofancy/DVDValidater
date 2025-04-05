import org.jetbrains.compose.desktop.application.dsl.TargetFormat
import java.io.FileOutputStream
import java.io.InputStream
import java.net.URI
import java.net.URL

plugins {
    kotlin("jvm")
    id("org.jetbrains.compose")
    id("org.jetbrains.kotlin.plugin.compose")
    id("com.google.devtools.ksp") version "2.1.0-1.0.29"
}

group = "top.ntutn"
version = "1.0.3"

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

    // https://mvnrepository.com/artifact/org.slf4j/slf4j-api
    implementation("org.slf4j:slf4j-api:2.0.16")
    // https://mvnrepository.com/artifact/org.redundent/kotlin-xml-builder
    implementation("org.redundent:kotlin-xml-builder:1.9.1")

    // autoservice
    ksp("dev.zacsweers.autoservice:auto-service-ksp:1.2.0")
    // NOTE: It's important that you _don't_ use compileOnly here, as it will fail to resolve at compile-time otherwise
    implementation("com.google.auto.service:auto-service-annotations:1.1.1")

    // https://mvnrepository.com/artifact/com.vaadin/open
    implementation("com.vaadin:open:8.5.0.4")
}

compose.desktop {
    application {
        mainClass = "top.ntutn.dvdvalidater.MainKt"

        nativeDistributions {
            targetFormats(TargetFormat.Dmg, TargetFormat.Msi, TargetFormat.Deb)
            packageName = project.name
            packageVersion = version.toString()

            windows {
                dirChooser = true
                menuGroup = "ntutn"
                upgradeUuid = "e9bd2713-a6b4-478a-b199-9f73747e66fc"
                iconFile.set(project.file("icon.ico"))
            }
        }

        buildTypes.release.proguard {
            version.set("7.4.0")
            configurationFiles.from(project.file("log.pro"))
        }
    }
}

tasks.create("packageDebWithoutJVM") {
    dependsOn("packageUberJarForCurrentOS")
    doLast {
        println("[${project.name}]Package deb with uber jar...")
        val checkDpkgProc = Runtime.getRuntime().exec("dpkg --help")
        checkDpkgProc.waitFor()
        if (checkDpkgProc.exitValue() != 0) {
            println("dpkg not found!")
            checkDpkgProc.errorStream.readAllBytes().decodeToString().let(::println)
            return@doLast
        }
        val jarDir = layout.buildDirectory.file("compose/jars").get().asFile
        val jarFile = jarDir.listFiles { file: File ->
            file.extension == "jar" && file.name.startsWith(project.name)
        }?.first()
        if (jarFile == null) {
            println("output jar not found")
            return@doLast
        }
        println("found jar file $jarFile")
        val outputDir = layout.buildDirectory.file("compose/jar2deb/${project.name}").get().asFile
        println("output dir $outputDir")
        if (outputDir.exists()) {
            outputDir.deleteRecursively()
        }
        outputDir.mkdirs()
        val debianDir = File(outputDir, "DEBIAN").also(File::mkdir)
        val installDir = File(outputDir, "opt/${project.name}").also(File::mkdir)
        val destJarFile = File(installDir, "${project.name}.jar")
        jarFile.copyTo(destJarFile)
        // todo generate shell script to launch it
        val debianControlFile = File(debianDir, "control")
        """
            Package: ${project.name}
            Version: 1.0
            Section: custom
            Priority: optional
            Architecture: all
            Depends: openjdk-17-jre | java17-runtime | java17-runtime-headless | openjdk-17-jre-headless
            Maintainer: zerofancy <ntutn.top@gmail.com>
            Description: This is a compose app package without jvm
            
        """.trimIndent().let {
            debianControlFile.writeText(it)
        }
        println("all files ready, start building deb: $outputDir")
        val debProc = Runtime.getRuntime().exec("fakeroot dpkg-deb --build ${outputDir.name}", arrayOf(), outputDir.parentFile)
        debProc.waitFor()
        debProc.inputStream.readAllBytes().decodeToString().let(::println)
        if (debProc.exitValue() != 0) {
            debProc.errorStream.readAllBytes().decodeToString().let(::println)
        } else {
            println("$outputDir.jar built success")
        }
    }
}
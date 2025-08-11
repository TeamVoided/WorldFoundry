@file:Suppress("PropertyName", "VariableNaming")

import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    alias(libs.plugins.fabric.loom)
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.kotlinx.serialization)
    alias(libs.plugins.iridium)
    alias(libs.plugins.iridium.publish)
    alias(libs.plugins.iridium.upload)
}

group = property("maven_group")!!
version = property("mod_version")!!

val modrinth_id: String? by project
val curse_id: String? by project

repositories {
    maven("https://teamvoided.org/releases") { content { includeGroup("org.teamvoided") } }
    maven("https://teamvoided.org/snapshots") { content { includeGroup("org.teamvoided") } }
    maven("https://maven.fzzyhmstrs.me/") { name = "FzzyMaven"; content { includeGroup("me.fzzyhmstrs") } }
    maven("https://maven.terraformersmc.com/") {
        name = "Terraformers"
        content {
            includeGroup("com.terraformersmc")
            includeGroup("dev.emi")
        }
    }
    maven("https://api.modrinth.com/maven") { content { includeGroup("maven.modrinth") } }
    mavenCentral()
}

modSettings {
    entrypoint("main", "org.teamvoided.world_foundry.WorldFoundry::commonInit")
    entrypoint("fabric-datagen", "org.teamvoided.world_foundry.data.gen.WorldFoundryData")

    mixinFile("${modId()}.mixins.json")
//    accessWidener("${modId()}.accesswidener")
}

dependencies {
    modImplementation(fileTree("libs"))
//    modImplementation(libs.farrow)
    modImplementation(libs.modmenu)

    // Dependencies
    modImplementation(libs.fzzy.config)

}

loom {
    accessWidenerPath = file("src/main/resources/${modSettings.modId()}.accesswidener")
    runs {
        create("DataGen") {
            client()
            ideConfigGenerated(true)
            vmArg("-Dfabric-api.datagen")
            vmArg("-Dfabric-api.datagen.output-dir=${file("src/main/generated")}")
            vmArg("-Dfabric-api.datagen.modid=${modSettings.modId()}")
            runDir("build/datagen")
        }

        create("TestWorld") {
            client()
            ideConfigGenerated(true)
            runDir("run")
            programArgs("--quickPlaySingleplayer", "test")
        }
    }
}

sourceSets["main"].resources.srcDir("src/main/generated")

tasks {
    val targetJavaVersion = 21
    withType<JavaCompile> {
        options.encoding = "UTF-8"
        options.release.set(targetJavaVersion)
    }

    withType<KotlinCompile>().all {
        compilerOptions.jvmTarget = JvmTarget.JVM_21
    }

    java {
        toolchain.languageVersion.set(JavaLanguageVersion.of(JavaVersion.toVersion(targetJavaVersion).toString()))
        withSourcesJar()
    }

    jar {
        val valTaskNames = gradle.startParameter.taskNames
        if (!valTaskNames.contains("runDataGen")) {
            exclude("org/teamvoided/${modSettings.modId()}/data/gen/**")
        } else {
            println("Running datagen for task ${valTaskNames.joinToString(" ")}")
        }
    }
}

publishScript {
    releaseRepository("TeamVoided", "https://maven.teamvoided.org/releases")
    publication(modSettings.modId(), false)
    publishSources(true)
}

uploadConfig {
//    debugMode = true
    modrinthId = modrinth_id
    curseId = curse_id

    changeLog = "- initial release"
    // FabricApi
    modrinthDependency("P7dR8mSH", uploadConfig.REQUIRED)
    curseDependency("fabric-api", uploadConfig.REQUIRED)
    // Fabric Language Kotlin
    modrinthDependency("Ha28R6CL", uploadConfig.REQUIRED)
    curseDependency("fabric-language-kotlin", uploadConfig.REQUIRED)
}

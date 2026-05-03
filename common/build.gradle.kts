import umpaz.brewinandchewin.gradle.Properties
import umpaz.brewinandchewin.gradle.Versions

plugins {
    id("conventions.common")
    id("net.neoforged.moddev")
    id("me.modmuss50.mod-publish-plugin")
}

sourceSets {
    create("generated") {
        resources {
            srcDir("src/generated/resources")
        }
    }
}

neoForge {
    neoFormVersion = Versions.NEOFORM
    parchment {
        minecraftVersion = Versions.PARCHMENT_MINECRAFT
        mappingsVersion = Versions.PARCHMENT
    }
    addModdingDependenciesTo(sourceSets["test"])

    val at = file("src/main/resources/${Properties.MOD_ID}.cfg")
    if (at.exists())
        setAccessTransformers(at)
    validateAccessTransformers = true
}

dependencies {
    compileOnly("io.github.llamalad7:mixinextras-common:${Versions.MIXIN_EXTRAS}")
    annotationProcessor("io.github.llamalad7:mixinextras-common:${Versions.MIXIN_EXTRAS}")
    compileOnly("net.fabricmc:sponge-mixin:${Versions.FABRIC_MIXIN}")

    compileOnly("house.greenhouse:greenhouseconfig:${Versions.GREENHOUSE_CONFIG}-common-mojmap")
    compileOnly("house.greenhouse:greenhouseconfig_toml:${Versions.GREENHOUSE_CONFIG_TOML}")

    compileOnly("maven.modrinth:farmers-delight:${Versions.FARMERS_DELIGHT}")

    compileOnly("mezz.jei:jei-${Versions.MINECRAFT}-common-api:${Versions.JEI}")
    compileOnly("dev.emi:emi-xplat-mojmap:${Versions.EMI}:api")

    compileOnly("squeek.appleskin:appleskin-neoforge:${Versions.APPLESKIN}")

    compileOnly("com.simibubi.create:create-${Versions.MINECRAFT}:${Versions.CREATE}")
    compileOnly("com.tterrag.registrate:Registrate:${Versions.REGISTRATE}")
}

configurations {
    register("commonJava") {
        isCanBeResolved = false
        isCanBeConsumed = true
    }
    register("commonResources") {
        isCanBeResolved = false
        isCanBeConsumed = true
    }
    register("commonTestResources") {
        isCanBeResolved = false
        isCanBeConsumed = true
    }
}

artifacts {
    add("commonJava", sourceSets["main"].java.sourceDirectories.singleFile)
    add("commonResources", sourceSets["main"].resources.sourceDirectories.singleFile)
    add("commonResources", sourceSets["generated"].resources.sourceDirectories.singleFile)
    add("commonTestResources", sourceSets["test"].resources.sourceDirectories.singleFile)
}

publishMods {
    changelog = rootProject.file("CHANGELOG.md").readText()
    displayName = "v${Versions.MOD} (Minecraft ${Versions.MINECRAFT})"
    version = "${Versions.MOD}+${Versions.MINECRAFT}"
    type = STABLE

    github {
        accessToken = providers.environmentVariable("GITHUB_TOKEN")
        repository = Properties.GITHUB_REPO
        tagName = "${Versions.MOD}+${Versions.MINECRAFT}"
        commitish = Properties.GITHUB_COMMITISH

        file(project(":fabric"))
        additionalFile(project(":neoforge"))
    }
}
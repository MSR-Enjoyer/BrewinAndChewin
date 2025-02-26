import umpaz.brewinandchewin.gradle.Properties

plugins {
    id("conventions.common")
}

configurations {
    register("commonJava") {
        isCanBeResolved = true
    }
    register("commonResources") {
        isCanBeResolved = true
    }
    register("commonTestResources") {
        isCanBeResolved = true
    }
}

dependencies {
    compileOnly(project(":common"))
    "commonJava"(project(":common", "commonJava"))
    "commonResources"(project(":common", "commonResources"))
    "commonTestResources"(project(":common", "commonTestResources"))
}

tasks {
    named<JavaCompile>("compileJava").configure {
        dependsOn(configurations.getByName("commonJava"))
        source(configurations.getByName("commonJava"))
    }
    named<ProcessResources>("processResources").configure {
        dependsOn(configurations.getByName("commonResources"))
        from(configurations.getByName("commonResources"))
        from(configurations.getByName("commonResources"))
    }
    named<ProcessResources>("processTestResources").configure {
        dependsOn(configurations.getByName("commonTestResources"))
        from(configurations.getByName("commonTestResources"))
        from(configurations.getByName("commonTestResources"))
    }
    named<Javadoc>("javadoc").configure {
        dependsOn(configurations.getByName("commonJava"))
        source(configurations.getByName("commonJava"))
    }
    named<Jar>("sourcesJar").configure {
        dependsOn(configurations.getByName("commonJava"))
        from(configurations.getByName("commonJava"))
        dependsOn(configurations.getByName("commonResources"))
        from(configurations.getByName("commonResources"))
    }
}
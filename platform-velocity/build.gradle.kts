plugins {
    `java-library`
    alias(libs.plugins.shadow)
    alias(libs.plugins.minotaur)
}

dependencies {
    implementation(project(":shared"))
    compileOnly(libs.velocity.api)
    annotationProcessor(libs.velocity.api)
}

base {
    archivesName.set("${rootProject.name}-velocity")
}

tasks {
    jar {
        archiveClassifier.set("plain")
    }

    shadowJar {
        archiveClassifier.set("")
        relocate("org.java_websocket", "space.chunks.msmp.libs.org.java_websocket")
        relocate("com.fasterxml.jackson", "space.chunks.msmp.libs.com.fasterxml.jackson")
    }

    build {
        dependsOn(shadowJar)
    }
}

modrinth {
    token.set(project.findProperty("modrinthToken") as String? ?: System.getenv("MODRINTH_TOKEN"))
    projectId.set("minecraft-server-management-protocol-legacy-support")
    versionNumber.set("${project.version}-velocity")
    versionName.set("${project.version}-velocity")
    versionType.set("release")
    uploadFile.set(tasks.shadowJar)
    gameVersions.addAll(rootProject.extra["modrinthGameVersions"] as List<String>)
    loaders.add("velocity")
    changelog.set(providers.environmentVariable("MODRINTH_CHANGELOG").orElse("Published from main."))
    syncBodyFrom.set(rootProject.file("README.md").readText())
    detectLoaders.set(false)
}

tasks.withType<JavaCompile>().configureEach {
    options.release.set(17)
}

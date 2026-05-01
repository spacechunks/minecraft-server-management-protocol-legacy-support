plugins {
    `java-library`
    alias(libs.plugins.shadow)
    alias(libs.plugins.minotaur)
}

dependencies {
    implementation(project(":shared"))
    compileOnly(libs.bungeecord.api)
}

val pluginVersion = project.version.toString()
base {
    archivesName.set("${rootProject.name}-bungeecord")
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

    processResources {
        filteringCharset = "UTF-8"
        filesMatching("bungee.yml") {
            expand("version" to pluginVersion)
        }
    }

    build {
        dependsOn(shadowJar)
    }
}

modrinth {
    token.set(project.findProperty("modrinthToken") as String? ?: System.getenv("MODRINTH_TOKEN"))
    projectId.set("minecraft-server-management-protocol-legacy-support")
    versionNumber.set("${project.version}-bungeecord")
    versionName.set("${project.version} - BungeeCord")
    versionType.set("release")
    uploadFile.set(tasks.shadowJar)
    gameVersions.addAll(rootProject.extra["modrinthGameVersions"] as List<String>)
    loaders.add("bungeecord")
    loaders.add("waterfall")
    changelog.set(providers.environmentVariable("MODRINTH_CHANGELOG").orElse("Published from main."))
    syncBodyFrom.set(rootProject.file("README.md").readText())
    detectLoaders.set(false)
}

tasks.withType<JavaCompile>().configureEach {
    options.release.set(8)
    options.compilerArgs.add("-Xlint:-options")
}

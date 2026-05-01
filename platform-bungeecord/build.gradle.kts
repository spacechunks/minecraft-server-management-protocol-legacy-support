plugins {
    `java-library`
    alias(libs.plugins.shadow)
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

tasks.withType<JavaCompile>().configureEach {
    options.release.set(8)
    options.compilerArgs.add("-Xlint:-options")
}

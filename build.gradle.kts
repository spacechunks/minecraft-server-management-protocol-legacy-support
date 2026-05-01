plugins {
    base
    alias(libs.plugins.shadow) apply false
    alias(libs.plugins.run.paper) apply false
    alias(libs.plugins.minotaur) apply false
}

allprojects {
    group = "space.chunks"
    version = "1.0.0"
}

subprojects {
    plugins.apply("java-library")

    extensions.configure<JavaPluginExtension> {
        withSourcesJar()
    }

    tasks.withType<JavaCompile>().configureEach {
        options.encoding = "UTF-8"
    }
}

extra["modrinthGameVersions"] = listOf(
    "1.8.8",
    "1.9.4",
    "1.10.2",
    "1.11.2",
    "1.12.2",
    "1.13.2",
    "1.14.4",
    "1.15.2",
    "1.16.5",
    "1.17.1",
    "1.18.2",
    "1.19.4",
    "1.20.6",
    "1.21.9",
)

tasks.register("publishModrinth") {
    group = "publishing"
    description = "Publish all platform artifacts to Modrinth."
    dependsOn(
        ":platform-spigot:modrinth",
        ":platform-velocity:modrinth",
        ":platform-bungeecord:modrinth",
    )
}

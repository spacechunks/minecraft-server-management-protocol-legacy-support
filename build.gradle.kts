plugins {
    alias(libs.plugins.shadow) apply false
    alias(libs.plugins.run.paper) apply false
}

allprojects {
    group = "space.chunks"
    version = "0.1.0-SNAPSHOT"
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

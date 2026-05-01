plugins {
    `java-library`
    alias(libs.plugins.shadow)
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
    shadowJar {
        archiveClassifier.set("")
        relocate("org.java_websocket", "space.chunks.msmp.libs.org.java_websocket")
        relocate("com.fasterxml.jackson", "space.chunks.msmp.libs.com.fasterxml.jackson")
    }

    build {
        dependsOn(shadowJar)
    }
}

tasks.withType<JavaCompile>().configureEach {
    options.release.set(17)
}

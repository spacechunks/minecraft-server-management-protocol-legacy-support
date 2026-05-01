plugins {
    `java-library`
}

dependencies {
    api(libs.java.websocket)
    api(libs.jackson.databind)
    api(libs.jackson.datatype.jsr310)

    testImplementation(libs.junit)
}

tasks.withType<JavaCompile>().configureEach {
    options.release.set(8)
    options.compilerArgs.add("-Xlint:-options")
}

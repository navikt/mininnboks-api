import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    id("org.jetbrains.kotlin.jvm") version "1.3.72"
    id("application")
}

val mainClassMinn = "no.nav.sbl.dialogarena.mininnboks.ApplicationKt"

val ktor_version = "1.3.2"
val kotlin_version = "1.3.72"
val tjenestespec_version = "1.2020.06.16-14.51-3b45df54f90a"
val mor_pom_version = "2.2020.08.28_14.16-420d1471cb04"
val logback_version = "1.2.1"
val spek_version = "2.0.12"
val konfig_version = "1.6.10.0"
val prometheusVersion = "0.4.0"

buildscript {

    repositories {
        mavenCentral()
    }
    dependencies {
        classpath(kotlin("gradle-plugin", version = "1.3.72"))
    }
}

apply(plugin = "kotlin")

repositories {
    mavenCentral()
    mavenLocal()
    jcenter()

    maven(
        "https://github-package-registry-mirror.gc.nav.no/cached/maven-release"
    )
}

dependencies {

    implementation(platform("no.nav.common:bom:$mor_pom_version"))
    implementation(kotlin("stdlib"))
    implementation("no.nav.common:cxf")
    implementation("no.nav.common:auth")
    implementation("no.nav.common:nais")
    implementation("no.nav.common:rest")
    implementation("no.nav.common:metrics")
    implementation("no.nav.common:health")
    implementation("no.nav.common:log")
    implementation("no.finn.unleash:unleash-client-java:3.2.2")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin:2.11.0")
    implementation("com.nimbusds:oauth2-oidc-sdk:6.23")
    implementation("no.nav.tjenestespesifikasjoner:dialogarena-behandlingsinformasjon:$tjenestespec_version")
    implementation("no.nav.tjenestespesifikasjoner:henvendelse-informasjon-v2:$tjenestespec_version")
    implementation("no.nav.tjenestespesifikasjoner:innsyn-henvendelse:$tjenestespec_version")
    implementation("io.micrometer:micrometer-registry-prometheus:1.5.5")
    implementation("com.github.ben-manes.caffeine:caffeine:3.0.4")
    implementation("com.sun.xml.ws:jaxws-ri:2.3.3")

    // Ktor
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-slf4j:1.3.3")
    implementation("io.ktor:ktor-server-netty:$ktor_version")
    implementation("ch.qos.logback:logback-classic:$logback_version")
    implementation("io.ktor:ktor-server-core:$ktor_version")
    implementation("io.ktor:ktor-locations:$ktor_version")
    implementation("io.ktor:ktor-auth-jwt:$ktor_version")
    implementation("com.natpryce:konfig:$konfig_version")
    implementation("io.ktor:ktor-jackson:$ktor_version")
    implementation("com.fasterxml.jackson.datatype:jackson-datatype-jsr310:2.9.9")
    implementation("io.ktor:ktor-metrics:$ktor_version")
    implementation("io.ktor:ktor-metrics-micrometer:$ktor_version")
    implementation("io.ktor:ktor-client-okhttp:$ktor_version")
    implementation("io.ktor:ktor-client-jackson:$ktor_version")
    implementation("io.ktor:ktor-client-logging-jvm:$ktor_version")

    // Java
    implementation("com.auth0:java-jwt:3.11.0")
    // test
    testImplementation("org.hamcrest:hamcrest:2.2")
    testImplementation("no.nav.common:auth:test-jar:tests")
    testImplementation("io.ktor:ktor-server-test-host:1.4.0")
    testImplementation("org.spekframework.spek2:spek-dsl-jvm:$spek_version")
    testRuntimeOnly("org.spekframework.spek2:spek-runner-junit5:$spek_version")
    testImplementation("io.mockk:mockk:1.10.2")
    testImplementation("com.github.tomakehurst:wiremock-standalone:2.27.2")
    testImplementation("io.ktor:ktor-client-mock:$ktor_version")
}

group = "no.nav.common"
version = "1"

java {
    sourceCompatibility = JavaVersion.VERSION_11
    targetCompatibility = JavaVersion.VERSION_11
}

tasks.named<KotlinCompile>("compileKotlin") {
    kotlinOptions.jvmTarget = "11"
}

tasks.named<KotlinCompile>("compileTestKotlin") {
    kotlinOptions.jvmTarget = "11"
}

// setup the test task
tasks.test {
    useJUnitPlatform()
    testLogging {
        events("passed", "skipped", "failed")
    }
}

project.configurations.implementation.get().isCanBeResolved = true

val thinJar = task("thinJar", type = Jar::class) {

    manifest {
        attributes["Implementation-Title"] = "Gradle Jar File Example"
        attributes["Main-Class"] = mainClassMinn
        attributes["Class-Path"] = configurations.implementation.get().joinToString(" ") {
            "lib/${it.name}"
        }
    }
    // To add all of the dependencies otherwise a "NoClassDefFoundError" error
    from(sourceSets.main.get().output)

    dependsOn(configurations.runtimeClasspath)
    from({
        configurations.implementation.get().filter { it.name.endsWith("jar") }.map { zipTree(it) }
        configurations.implementation.get().filter { it.isDirectory }.map { zipTree(it) }
    })
}

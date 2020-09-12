import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    id("org.jetbrains.kotlin.jvm") version "1.3.72"
    id ("com.github.johnrengelman.shadow") version "6.0.0"
}

val mainClassMinn = "no.nav.sbl.dialogarena.mininnboks.ApplicationKt"

val ktor_version = "1.3.2"
val kotlin_version = "1.3.72"
val tjenestespec_version = "1.2020.06.16-14.51-3b45df54f90a"
val mor_pom_version = "2.2020.08.28_14.16-420d1471cb04"
val logback_version="1.2.1"
val spek_version = "2.0.12"
val konfig_version = "1.6.10.0"
val prometheusVersion = "0.4.0"

buildscript {

    extra["kotlin_version"] = "1.3.72"
    extra["ktor_version"] = "1.3.2"
    repositories {
        mavenCentral()
    }
    dependencies {
        classpath(kotlin("gradle-plugin", version = "1.3.72"))
        classpath( "com.github.jengelman.gradle.plugins:shadow:6.0.0")
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
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin:2.11.0")
    implementation("com.nimbusds:oauth2-oidc-sdk:6.23")
    implementation("no.nav.tjenestespesifikasjoner:dialogarena-behandlingsinformasjon:$tjenestespec_version")
    implementation("no.nav.tjenestespesifikasjoner:henvendelse-informasjon-v2:$tjenestespec_version")
    implementation("no.nav.tjenestespesifikasjoner:send-inn-henvendelse:$tjenestespec_version")
    implementation("no.nav.tjenestespesifikasjoner:innsyn-henvendelse:$tjenestespec_version")
    implementation("no.nav.tjenestespesifikasjoner:person-v3-tjenestespesifikasjon:$tjenestespec_version")
    implementation("com.nhaarman.mockitokotlin2:mockito-kotlin:2.2.0")
    implementation("com.sun.xml.ws:jaxws-ri:2.3.3")


    //Ktor
    implementation("io.ktor:ktor-server-netty:$ktor_version")
    implementation("ch.qos.logback:logback-classic:$logback_version")
    implementation("io.ktor:ktor-server-core:$ktor_version")
    implementation("io.ktor:ktor-locations:$ktor_version")
    implementation("io.ktor:ktor-auth-jwt:$ktor_version")
    implementation("com.natpryce:konfig:$konfig_version")
    implementation("io.ktor:ktor-jackson:$ktor_version")
    implementation("com.fasterxml.jackson.datatype:jackson-datatype-jsr310:2.9.9")
    implementation("io.ktor:ktor-metrics:$ktor_version")
    implementation("io.prometheus:simpleclient_hotspot:$prometheusVersion")
    implementation("io.prometheus:simpleclient_common:$prometheusVersion")
    implementation("io.prometheus:simpleclient_dropwizard:$prometheusVersion")

    testImplementation("org.hamcrest:hamcrest:2.2")
    testImplementation("org.assertj:assertj-core:3.11.1")
    testImplementation("com.willowtreeapps.assertk:assertk-jvm:0.21")
    testImplementation("no.nav.common:auth:test-jar:tests")
    testImplementation("org.junit.platform:junit-platform-launcher:1.1.1")
   // testImplementation("org.junit.vintage:junit-vintage-engine:5.1.1")
    testImplementation("io.ktor:ktor-server-test-host:1.4.0")
    testImplementation("org.spekframework.spek2:spek-dsl-jvm:$spek_version")
    testRuntimeOnly("org.spekframework.spek2:spek-runner-junit5:$spek_version")
    testImplementation("io.mockk:mockk:1.9.3")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-api:5.4.2")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.4.2")

  compileOnly("org.projectlombok:lombok:1.18.4")
    annotationProcessor("org.projectlombok:lombok:1.18.4")
    testRuntimeOnly("org.spekframework.spek2:spek-runner-junit5:2.0.9")
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

}

tasks {
    named<com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar>("shadowJar") {
        archiveFileName.set("app.jar")


        manifest {
            attributes["Main-Class"] = mainClassMinn

    }
        mergeServiceFiles {
            setPath("META-INF/cxf")
            include("bus-extensions.txt")
        }

       exclude ("META-INF/*.SF")
        exclude ("META-INF/*.DSA")
        exclude ("META-INF/*.RSA")
        

    }

}


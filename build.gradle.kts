plugins {
    kotlin("jvm") version "2.1.10"
    `java-library`
    antlr
}

repositories {
    mavenCentral()
}

project.group = "io.github.kamilperczynski"
project.version = "1.0-SNAPSHOT"
dependencies {
    implementation("com.github.librepdf:openpdf:2.0.3")

    testImplementation("org.junit.jupiter:junit-jupiter:5.11.4")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
    antlr("org.antlr:antlr4:4.9.3")

    implementation("org.yaml:snakeyaml:2.4")

    testImplementation("org.assertj:assertj-core:3.27.3")
}

tasks.compileKotlin {
    dependsOn(tasks.generateGrammarSource)
}

tasks.compileTestKotlin {
    dependsOn(tasks.generateTestGrammarSource, tasks.generateGrammarSource)
}

tasks.generateGrammarSource {
    inputs.dir(file("src/main/antlr"))
    outputDirectory = file("build/generated/antlr/main/io/github/kamilperczynski/adocparser")

    sourceSets.main {
        java.srcDir("build/generated/antlr/main")
    }
}

tasks.named<Test>("test") {
    useJUnitPlatform()

    testLogging {
        showStandardStreams = true

        events("passed", "skipped", "failed")
    }
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(21))
    }
}

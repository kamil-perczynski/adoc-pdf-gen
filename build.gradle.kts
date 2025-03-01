plugins {
    kotlin("jvm") version "2.1.10"
    `java-library`
    antlr
}

repositories {
    mavenCentral()
}

dependencies {
    implementation("com.lowagie:itext:2.1.7")

    testImplementation("org.junit.jupiter:junit-jupiter:5.11.4")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
    antlr("org.antlr:antlr4:4.9.3")
}

tasks.compileKotlin {
    dependsOn(tasks.generateGrammarSource)
}

tasks.compileTestKotlin {
    dependsOn(tasks.generateGrammarSource)
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
        events("passed")
    }
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(21))
    }
}

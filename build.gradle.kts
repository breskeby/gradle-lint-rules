

import codegen.GenerateClasspathManifest
import org.gradle.api.plugins.JavaPluginConvention
import org.gradle.api.publish.PublishingExtension
import org.gradle.api.publish.maven.MavenPublication
import org.gradle.script.lang.kotlin.*

buildscript {
    val kotlinVersion = "1.1-M02"
    val kotlinRepo = "https://repo.gradle.org/gradle/repo"
    extra["kotlinVersion"] = kotlinVersion
    extra["kotlinRepo"] = kotlinRepo

    repositories {
        maven { setUrl("http://dl.bintray.com/kotlin/kotlin-eap-1.1")}
        maven { setUrl(kotlinRepo) }
    }

    dependencies {
        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:$kotlinVersion")
        classpath("org.jfrog.buildinfo:build-info-extractor-gradle:4.1.1")
    }
}

apply {
    plugin("kotlin")
    plugin("java-gradle-plugin")
    plugin("maven-publish")
    plugin("com.jfrog.artifactory")
}

group = "com.breskeby.gradle"
version = "0.1-SNAPSHOT"

repositories {
    jcenter()
    maven { setUrl("http://dl.bintray.com/kotlin/kotlin-eap-1.1")}
    maven {
        setUrl("https://plugins.gradle.org/m2/")
    }

}
dependencies {
    compileOnly(gradleApi())

    compile("com.netflix.nebula:gradle-lint-plugin:5.1.3")
    compile("org.ow2.asm:asm-all:5.1")

    compile(kotlin("stdlib"))
    compile(kotlin("reflect"))

    testCompile(gradleTestKit())
    testCompile("junit:junit:4.12")
    testCompile("com.nhaarman:mockito-kotlin:0.6.0")
    testCompile("com.fasterxml.jackson.module:jackson-module-kotlin:2.7.5")
}

val sourceSets = the<JavaPluginConvention>().sourceSets
val mainSourceSet = sourceSets.getByName("main")!!

// --- classpath.properties --------------------------------------------
val generatedResourcesDir = file("$buildDir/generate-resources/main")
task<GenerateClasspathManifest>("generateClasspathManifest") {
    outputDirectory = generatedResourcesDir
}
mainSourceSet.output.dir(mapOf("builtBy" to "generateClasspathManifest"), generatedResourcesDir)

configure<PublishingExtension> {
    publications {
        create<MavenPublication>("mavenJava") {
            from(components.getByName("java"))
        }
    }
}

//// --- Utility functions -----------------------------------------------
fun kotlin(module: String) = "org.jetbrains.kotlin:kotlin-$module:${extra["kotlinVersion"]}"


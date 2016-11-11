
import codegen.GenerateClasspathManifest
import com.gradle.publish.PluginBundleExtension
import com.jfrog.bintray.gradle.BintrayExtension
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
        maven { setUrl("http://dl.bintray.com/kotlin/kotlin-eap-1.1") }
        maven { setUrl(kotlinRepo) }
        maven { setUrl("https://plugins.gradle.org/m2/") }
        jcenter()
    }

    dependencies {
        classpath("com.jfrog.bintray.gradle:gradle-bintray-plugin:1.7")
        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:$kotlinVersion")
        classpath("org.jfrog.buildinfo:build-info-extractor-gradle:4.1.1")
        classpath("com.gradle.publish:plugin-publish-plugin:0.9.6")
    }
}

apply {
    plugin("kotlin")
    plugin("maven-publish")
    plugin("java-gradle-plugin")
    plugin("com.gradle.plugin-publish")
    plugin("com.jfrog.bintray")
}

group = "com.breskeby.gradle"
version = "0.0.1"

repositories {
    jcenter()
    maven { setUrl("http://dl.bintray.com/kotlin/kotlin-eap-1.1") }
    maven {
        setUrl("https://plugins.gradle.org/m2/")
    }

}
dependencies {
    compileOnly(gradleApi())
    compile("com.netflix.nebula:gradle-lint-plugin:5.1.3")
    compile(kotlin("stdlib"))

    testCompile(gradleTestKit())
    testCompile("junit:junit:4.12")
    testCompile("com.nhaarman:mockito-kotlin:0.6.0")
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

configure<BintrayExtension> {
    user = System.getProperty("bintrayUser")
    key =  System.getProperty("bintrayKey")
    setPublications("mavenJava")

    pkg.repo = "gradle-plugins"
    pkg.name = "com.breskeby.gradle:gradle-lint-addons"
    pkg.desc = "A set of lint rules for the gradle-lint-plugin."
    pkg.websiteUrl = "https://github.com/breskeby/${project.name}"
    pkg.vcsUrl = "https://github.com/breskeby/${project.name}"
    pkg.setLicenses("Apache-2.0")
    pkg.publicDownloadNumbers = true
    pkg.setLabels("gradle", "lint")
    pkg.version.vcsTag = "v${project.version}"
    pkg.version.attributes = mapOf("gradle-plugin" to listOf("com.breskeby.gradle:gradle-lint-addons"))
    pkg.version.gpg.sign = true
    pkg.version.gpg.passphrase = System.getProperty("gpgPassphrase")
}


// The configuration example below shows the minimum required properties
// configured to publish your plugin to the plugin portal
//
configure<PluginBundleExtension>{
    website = "https://github.com/breskeby/gradle-lint-rules"
    vcsUrl = "https://github.com/breskeby/gradle-lint-rules"
    tags = listOf("lint", "formatting")
    val pluginDescr = plugins.create("lint-rules")
    pluginDescr.id = "com.breskeby.lint.rules"
    pluginDescr.description = "Additional Lint rules on top of the nebula.lint plugin"
    pluginDescr.displayName = "Gradle Lint rules"
}

//// --- Utility functions -----------------------------------------------
fun kotlin(module: String) = "org.jetbrains.kotlin:kotlin-$module:${extra["kotlinVersion"]}"


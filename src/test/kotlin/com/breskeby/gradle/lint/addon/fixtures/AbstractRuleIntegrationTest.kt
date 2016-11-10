package com.breskeby.gradle.lint.addon.fixtures

import org.gradle.testkit.runner.BuildResult
import org.gradle.testkit.runner.GradleRunner
import org.junit.Rule
import org.junit.rules.TemporaryFolder
import java.io.File


open class AbstractRuleIntegrationTest {

    @JvmField
    @Rule val projectDir = TemporaryFolder()

    private fun buildSrcOutput(): File =
            existing("buildSrc/build/classes/main")

    private val fixturesRepository: File
        get() = File("fixtures/repository").absoluteFile

    fun withBuildScript(script: String) {
        withBuildScriptIn(".", script)
    }

    private fun withBuildScriptIn(baseDir: String, script: String) {
        withFile("$baseDir/settings.gradle", "rootProject.name = 'test-build'")
        withFile("$baseDir/build.gradle", script)
    }

    private fun withFile(fileName: String, text: String) {
        file(fileName).writeText(text)
    }

    private fun file(fileName: String) =
            projectDir.run {
                makeParentFoldersOf(fileName)
                newFile(fileName)
            }

    private fun existing(relativePath: String) =
            File(projectDir.root, relativePath).run {
                canonicalFile
            }

    private fun TemporaryFolder.makeParentFoldersOf(fileName: String) {
        File(root, fileName).parentFile.mkdirs()
    }

    fun build(vararg arguments: String): BuildResult =
            gradleRunner()
                    .withArguments(*arguments, "--stacktrace")
                    .withPluginClasspath()
                    .build()

    private fun gradleRunner() =
            gradleRunnerFor(projectDir.root)
}


fun gradleRunnerFor(projectDir: File): GradleRunner =
        GradleRunner
                .create()
                .withDebug(true)
                .forwardOutput()
                .withProjectDir(projectDir)

inline fun <T> withSystemProperty(key: String, value: String, block: () -> T): T {
    val originalValue = System.getProperty(key)
    try {
        System.setProperty(key, value)
        return block()
    } finally {
        setOrClearProperty(key, originalValue)
    }
}

fun setOrClearProperty(key: String, value: String?) {
    when (value) {
        null -> System.clearProperty(key)
        else -> System.setProperty(key, value)
    }
}
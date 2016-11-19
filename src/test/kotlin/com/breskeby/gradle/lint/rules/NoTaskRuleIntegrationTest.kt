package com.breskeby.gradle.lint.rules

import com.breskeby.gradle.lint.addon.fixtures.AbstractRuleIntegrationTest
import org.gradle.testkit.runner.TaskOutcome
import org.junit.Test

class NoTaskRuleIntegrationTest : AbstractRuleIntegrationTest() {

    @Test
    fun `if statements triggers lint warning`() {
        withBuildScript("""
            plugins {
                id 'nebula.lint'
            }
            apply plugin:"java"
            gradleLint.rules = ['no-task-rule']

            task someTask {
            }
        """)

        val result = build("lintGradle")
        assert(result.output.contains("warning   no-task-rule"))
        assert(result.output.contains("No Task Definition in bulid script allowed. Use plugins for maintaining custom logic."))
        assert(result.task(":lintGradle").outcome == TaskOutcome.SUCCESS)
    }

}


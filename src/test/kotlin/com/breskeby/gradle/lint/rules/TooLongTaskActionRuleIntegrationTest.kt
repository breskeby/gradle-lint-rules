package com.breskeby.gradle.lint.rules

import com.breskeby.gradle.lint.addon.fixtures.AbstractRuleIntegrationTest
import org.gradle.testkit.runner.TaskOutcome
import org.junit.Test


class TooLongTaskActionRuleIntegrationTest : AbstractRuleIntegrationTest() {
    @Test fun `can detect long action block in task definition configuration block`() {
        withBuildScript("""
            plugins {
                id 'nebula.lint'
            }
            apply plugin:"java"
            gradleLint.rules = ['long-task-action-blocks']

            task someTask {
                doLast {
                    println '1'
                    println '2'
                    println '3'
                    println '4'
                    println '5'
                    println '6'
                    println '7'
                    println '8'
                    println '9'
                }
            }
        """)

        val result = build("lintGradle")
        assert(result.output.contains("warning   long-task-action-blocks"))
        assert(result.output.contains("Max size for task action block exceeded (Max LOC: 5; found: 10) (no auto-fix available)"))
        assert(result.task(":lintGradle").outcome == TaskOutcome.SUCCESS)
    }

    @Test fun `can detect long action block in task configuration block`() {
        withBuildScript("""
            plugins {
                id 'nebula.lint'
            }
            apply plugin:"java"
            gradleLint.rules = ['long-task-action-blocks']
            task someTask
            someTask {
                doLast {
                    println '1'
                    println '2'
                    println '3'
                    println '4'
                    println '5'
                    println '6'
                    println '7'
                    println '8'
                    println '9'
                }
            }

            dependencies {
                    println '1'
                    println '2'
                    println '3'
                    println '4'
                    println '5'
                    println '6'
                    println '7'
                    println '8'
                    println '9'
            }
        """)

        val result = build("lintGradle")
        assert(result.output.contains("warning   long-task-action-blocks"))
        assert(result.output.contains("Max size for task action block exceeded (Max LOC: 5; found: 10) (no auto-fix available)"))
        assert(result.task(":lintGradle").outcome == TaskOutcome.SUCCESS)
    }

    @Test fun `can detect left shift action block in task definition configuration block`() {
        withBuildScript("""
            plugins {
                id 'nebula.lint'
            }
            apply plugin:"java"
            gradleLint.rules = ['long-task-action-blocks']
            task someTask2 << {
                    println '1'
                    println '2'
                    println '3'
                    println '4'
                    println '5'
                    println '6'
            }
        """)

        val result = build("lintGradle")
        assert(result.output.contains("warning   long-task-action-blocks"))
        assert(result.output.contains("Max size for task action block exceeded (Max LOC: 5; found: 7) (no auto-fix available)"))
        assert(result.task(":lintGradle").outcome == TaskOutcome.SUCCESS)
    }
}
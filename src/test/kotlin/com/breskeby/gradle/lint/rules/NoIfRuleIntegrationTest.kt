package com.breskeby.gradle.lint.rules

import com.breskeby.gradle.lint.addon.fixtures.AbstractRuleIntegrationTest
import org.gradle.testkit.runner.TaskOutcome
import org.junit.Test


class NoIfRuleIntegrationTest : AbstractRuleIntegrationTest() {

    @Test
    fun `if statements triggers lint warning`() {
        withBuildScript("""
            plugins {
                id 'nebula.lint'
            }
            apply plugin:"java"
            gradleLint.rules = ['no-if-rule']

            task someTask {
                if(2==2){
                }
            }
        """)

        val result = build("lintGradle")
        assert(result.output.contains("warning   no-if-rule"))
        assert(result.output.contains("If statements are not allowed in build scripts. Put imperative logic into a plugin! (no auto-fix available)"))
        assert(result.task(":lintGradle").outcome == TaskOutcome.SUCCESS)
    }

}



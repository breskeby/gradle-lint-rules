package com.breskeby.gradle.lint.rules

import com.breskeby.gradle.lint.addon.fixtures.AbstractRuleIntegrationTest
import org.gradle.testkit.runner.TaskOutcome
import org.junit.Test


class NoLoopRuleIntegrationTest : AbstractRuleIntegrationTest() {

    @Test
    fun `if statements triggers lint warning`() {
        withBuildScript("""
            plugins {
                id 'nebula.lint'
            }
            apply plugin:"java"
            gradleLint.rules = ['no-if-rule']

           if(1==1){
           }
            task someTask {
                if(2==2){
                }
            }
        """)

        val result = build("lintGradle")
        assert(result.output.contains("warning   no-if-rule"))
        assert(result.output.contains("No if statements are allowed in build scripts. Put imperative logic into a plugin! (no auto-fix available)"))
        assert(result.task(":lintGradle").outcome == TaskOutcome.SUCCESS)
    }

}



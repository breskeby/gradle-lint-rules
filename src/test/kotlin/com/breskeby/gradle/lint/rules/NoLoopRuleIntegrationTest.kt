package org.gradle.script.lang.kotlin.integration

import com.breskeby.gradle.lint.addon.fixtures.AbstractRuleIntegrationTest
import org.gradle.testkit.runner.TaskOutcome
import org.junit.Test

class NoLoopRuleIntegrationTest : AbstractRuleIntegrationTest() {

    @Test
    fun `while loop triggers lint warning`() {
        withBuildScript("""
            plugins {
                id 'nebula.lint'
            }
            apply plugin:"java"
            gradleLint.rules = ['no-loops-rule']

            int i = 1
            while (i ==1 ) {
                //some logic
                i++
            }
        """)

        val result = build("lintGradle")
        assert(result.output.contains("warning   no-loops-rule"))
        assert(result.output.contains("While loops are allowed in build scripts. Put imperative logic into a plugin! (no auto-fix available)"))
        assert(result.task(":lintGradle").outcome == TaskOutcome.SUCCESS)
    }

    @Test
    fun `for loop triggers lint warning`() {
        withBuildScript("""
            plugins {
                id 'nebula.lint'
            }
            apply plugin:"java"
            gradleLint.rules = ['no-loops-rule']
            for(int i=0;i<1;i++){}
        """)

        val result = build("lintGradle")
        assert(result.output.contains("warning   no-loops-rule"))
        assert(result.output.contains("For loops are allowed in build scripts. Put imperative logic into a plugin! (no auto-fix available)"))
        assert(result.task(":lintGradle").outcome == TaskOutcome.SUCCESS)
    }

    @Test
    fun `nested for loop triggers lint warning`() {
        withBuildScript("""
            plugins {
                id 'nebula.lint'
            }
            apply plugin:"java"
            gradleLint.rules = ['no-loops-rule']
            task acmeTask {
                for(int i=0;i<1;i++){}
            }
        """)

        val result = build("lintGradle")
        assert(result.output.contains("warning   no-loops-rule"))
        assert(result.output.contains("For loops are allowed in build scripts. Put imperative logic into a plugin! (no auto-fix available)"))
        assert(result.task(":lintGradle").outcome == TaskOutcome.SUCCESS)
    }

}



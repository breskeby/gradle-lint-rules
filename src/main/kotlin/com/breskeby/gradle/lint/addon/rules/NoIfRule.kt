package com.breskeby.gradle.lint.addon.rules

import com.netflix.nebula.lint.rule.GradleLintRule
import org.codehaus.groovy.ast.stmt.IfStatement

class NoIfRule: GradleLintRule() {
    override fun getDescription(): String = "Bann if statements in build scripts"

    override fun visitIfElse(ifElse: IfStatement?) {
        addBuildLintViolation("If statements are not allowed in build scripts. Put imperative logic into a plugin!")
        super.visitIfElse(ifElse)
    }
}
package com.breskeby.gradle.lint.addon.rules

import com.netflix.nebula.lint.rule.GradleLintRule
import org.codehaus.groovy.ast.stmt.DoWhileStatement
import org.codehaus.groovy.ast.stmt.ForStatement
import org.codehaus.groovy.ast.stmt.WhileStatement

class NoLoopsRule: GradleLintRule() {
    override fun getDescription(): String = "Bann (do-)while / for loops rom build scripts"

    override fun visitDoWhileLoop(loop: DoWhileStatement?) {
        addBuildLintViolation("Do-While loops are allowed in build scripts. Put imperative logic into a plugin!")
        super.visitDoWhileLoop(loop)

    }

    override fun visitForLoop(forLoop: ForStatement?) {
        addBuildLintViolation("For loops are allowed in build scripts. Put imperative logic into a plugin!")
        super.visitForLoop(forLoop)
    }

    override fun visitWhileLoop(loop: WhileStatement?) {
        addBuildLintViolation("While loops are allowed in build scripts. Put imperative logic into a plugin!")
        super.visitWhileLoop(loop)
    }
}

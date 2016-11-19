package com.breskeby.gradle.lint.addon.rules

import com.netflix.nebula.lint.rule.GradleLintRule
import org.codehaus.groovy.ast.expr.MethodCallExpression


class NoTasksRule : GradleLintRule() {
    override fun getDescription() = "Ban task definitions in build scripts"

    override fun visitTask(call: MethodCallExpression?, name: String?, args: MutableMap<String, String>?) {
        addBuildLintViolation("No Task Definition in bulid script allowed. Use plugins for maintaining custom logic.")
        super.visitTask(call, name, args)
    }
}
package com.breskeby.gradle.lint.addon.rules

import com.netflix.nebula.lint.rule.GradleLintRule
import com.netflix.nebula.lint.rule.GradleModelAware
import org.codehaus.groovy.ast.expr.*


class LongTaskActionsRule : GradleLintRule(), GradleModelAware {
    val MAX_TASK_ACTION_BLOCK_SIZE = 5;


    private var processedActionBlocks = setOf<Int>()
    private var processTaskActionExpression: Boolean = false

    override fun getDescription(): String = "Checks for to complex doLast blocks in build scripts"

    override fun visitMethodCallExpression(expression: MethodCallExpression) {
        val method = expression.method
        if (method is ConstantExpression && isTaskActionExpression(method)) {
            processTaskActionExpression = true;

        }

        super.visitMethodCallExpression(expression)
        processTaskActionExpression = false;
    }

    override fun visitConstantExpression(expression: ConstantExpression?) {
        super.visitConstantExpression(expression)
    }

    override fun visitBinaryExpression(expression: BinaryExpression) {
        if (isReferencingTask(expression.leftExpression) && expression.operation.text.equals("<<")) {
            processTaskActionExpression = true
        }
        super.visitBinaryExpression(expression)
    }

    override fun visitClosureExpression(expression: ClosureExpression) {
        if (processTaskActionExpression && notProcessed(expression)) {
            val blockSize = expression.lastLineNumber - expression.lineNumber
            if (blockSize > MAX_TASK_ACTION_BLOCK_SIZE) {
                addBuildLintViolation("Max size for task action block exceeded (Max LOC: $MAX_TASK_ACTION_BLOCK_SIZE; found: $blockSize)")
            }
            processTaskActionExpression = false;
            processedActionBlocks = processedActionBlocks + expression.hashCode()
        }
        super.visitClosureExpression(expression)
    }

    private fun notProcessed(expression: ClosureExpression): Boolean = !processedActionBlocks.contains(expression.hashCode())

    private fun isReferencingTask(expression: Expression): Boolean {
        if (expression is VariableExpression?) {
            return project.tasks.findByName(expression.name) != null
        } else {
            return false
        }
    }

    private fun isTaskActionExpression(method: ConstantExpression) = method.value.equals("doLast") || method.value.equals("doFirst") || method.value.equals("leftShift") || method.value.equals("<<")
}
package com.github.yona168.visualgorithms.arch.steps


import com.github.yona168.visualgorithms.arch.Condition
import com.github.yona168.visualgorithms.arch.variables.HasVars
import com.github.yona168.visualgorithms.arch.variables.Vars

sealed class Step(parentVars: Vars?) : HasVars() {
    override val vars = parentVars ?: Vars()
}

abstract class ParentStep(parentVars: Vars?) : Step(parentVars) {
    abstract fun steps(): List<Step>
}

class ContextedContainerStep(containerStep: ContainerStep, parentVars: Vars?) :
    ParentStep(parentVars) {
    private val children = containerStep.children.map { it.toContexted(parentVars) }
    override fun steps() = children
}


class Action(
    parentVars: Vars?,
    val desc: String,
    val plainAction: PlainAction
) : Step(parentVars)

class CheckCondition(val condition: Condition, parentVars: Vars?) :
    Step(parentVars)

class If(iff: UncontextedIf, vars: Vars?) : ParentStep(vars) {
    private val thenStep: Step =
        ContextedContainerStep(iff.then, vars)
    private val contextedCheckCondition: Step =
        CheckCondition(iff.condition, vars)
    override fun steps() = listOf(contextedCheckCondition, thenStep)

}

class IfChain(
    private val parentVars: Vars?,
    ifChain: UncontextedIfChain
) : ParentStep(parentVars) {
    val contextedIfs = ifChain.ifElseIfs.map {
        If(
            it,
            parentVars
        )
    }
    val contextedElse: ContextedContainerStep? =
        if (ifChain.els == null) null
        else ContextedContainerStep(
            ifChain.els,
            parentVars
        )

    override fun steps(): List<Step> {
        val stepList = mutableListOf<Step>()
        stepList += contextedIfs
        if (contextedElse != null) stepList += contextedElse
        return stepList
    }

}

class For(val forr: UncontextedFor, val parentVars: Vars?) : ParentStep(parentVars) {
    override fun steps(): List<Step> {
        val stepList = mutableListOf<Step>()
        stepList += Action(
            parentVars,
            forr.initialDesc,
            forr.initial
        )
        stepList += CheckCondition(
            forr.condition,
            parentVars
        )
        val container = ContainerStep()
        forr.doAction(container)
        stepList += ContextedContainerStep(
            container,
            parentVars
        )
        stepList += Action(
            parentVars,
            forr.afterDesc,
            forr.afterAction
        )
        return stepList
    }
}

class While(private val whil: UncontextedWhile, vars: Vars?) : ParentStep(vars) {
    override fun steps(): List<Step> {
        return listOf(
            CheckCondition(
                whil.condition,
                vars
            ),
            ContextedContainerStep(
                ContainerStep().apply(whil.action), vars
            )
        )
    }

}


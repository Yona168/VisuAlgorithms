package com.github.yona168.visualgorithms.arch

import com.github.yona168.visualgorithms.arch.variables.HasVars
import com.github.yona168.visualgorithms.arch.variables.Vars



sealed class Step(parentVars: Vars?) : HasVars {
    override val vars = parentVars?:Vars()
}

sealed class UncontextedStep {
    abstract fun toContexted(parentVars: Vars?): Step
}

abstract class ParentStep(parentVars: Vars?) : Step(parentVars) {
    abstract fun steps(): List<Step>
}

abstract class BarrenStep(parentVars: Vars?) : Step(parentVars)


class ContainerStep {
    val children = mutableListOf<UncontextedStep>()
    fun add(desc: String, barrenAction: BarrenAction) = add(ActionStep(desc, barrenAction))
    infix fun add(actionStep: ActionStep) {
        children += actionStep
    }

    infix fun add(iff: IfChain.Builder) {
        children += iff.build()
    }

    infix fun add(iff: () -> IfChain.Builder) {
        add(iff())
    }
}

class ContextedContainerStep(containerStep: ContainerStep, parentVars: Vars?) :
    ParentStep(parentVars) {
    private val children = containerStep.children.map { it.toContexted(parentVars) }
    override fun steps() = children
}

typealias BarrenAction = BarrenStep.() -> Unit
typealias ParentAction = ContainerStep.() -> Unit


//Simple action
class ActionStep(val desc: String, val barrenAction: BarrenAction) : UncontextedStep() {
    override fun toContexted(parentVars: Vars?) =
        ContextedActionStep(parentVars, desc, barrenAction)
}

class ContextedActionStep(
    parentVars: Vars?,
    val desc: String,
    val barrenAction: BarrenAction
) : BarrenStep(parentVars) {
    constructor(parentVars: Vars?, actionStep: ActionStep) : this(
        parentVars,
        actionStep.desc,
        actionStep.barrenAction
    )
}

//If and if chains
class If(val condition: Condition, val then: ContainerStep) : UncontextedStep() {
    override fun toContexted(parentVars: Vars?) =
        ContextedIf(this, parentVars)
}

class IfChain(val ifElseIfs: List<If>, val els: ContainerStep? = null) : UncontextedStep() {
    open class Builder(condition: Condition) {
        protected var elseIfs: List<If> = mutableListOf()
        private var els: ContainerStep? = null
        protected var currentIfCondition = condition

        fun elseIf(condition: Condition): ThenBuilder {
            this.currentIfCondition = condition
            return this as ThenBuilder
        }

        fun els(parentAction: ParentAction) = apply {
            val containerStep = ContainerStep()
            parentAction(containerStep)
            this.els = containerStep
        }

        fun build() = IfChain(elseIfs, els)
    }

    class ThenBuilder(condition: Condition) : Builder(condition) {
        fun then(containerAction: ParentAction): IfChain.Builder {
            this.elseIfs += If(currentIfCondition, ContainerStep().also(containerAction))
            return this
        }
    }

    override fun toContexted(parentVars: Vars?) =
        ContextedIfChain(parentVars,this)
}

class ContextedCheckCondition(val condition: Condition, parentVars: Vars?) :
    BarrenStep(parentVars) {
}

class ContextedIf(private val iff: If, vars: Vars?) : ParentStep(vars) {
    private val thenStep: Step = ContextedContainerStep(iff.then, vars)
    private val contextedCheckCondition: Step = ContextedCheckCondition(iff.condition, vars)
    override fun steps() = listOf(contextedCheckCondition, thenStep)

}

class ContextedIfChain(
    private val parentVars: Vars?,
    ifChain: IfChain
) : ParentStep(parentVars) {
    val contextedIfs = ifChain.ifElseIfs.map { ContextedIf(it, parentVars) }
    val contextedElse: ContextedContainerStep? =
        if (ifChain.els == null) null
        else ContextedContainerStep(ifChain.els, parentVars)

    override fun steps(): List<Step> {
        val stepList = mutableListOf<Step>()
        stepList += contextedIfs
        if (contextedElse != null) stepList += contextedElse
        return stepList
    }

}

class For(val initialDesc: String,val initial: BarrenAction, val condition: Condition, val doAction: ParentAction):
UncontextedStep(){
    override fun toContexted(parentVars: Vars?): Step {
        return ContextedFor(this, parentVars)
    }

}

class ContextedFor(val forr: For, val parentVars: Vars?):ParentStep(parentVars) {
    override fun steps(): List<Step> {
        val stepList= mutableListOf<Step>()
        stepList+= ContextedActionStep(parentVars,forr.initialDesc,forr.initial)
        stepList+=ContextedCheckCondition(forr.condition,parentVars)
        val container=ContainerStep()
        forr.doAction(container)
        stepList+=ContextedContainerStep(container, parentVars)
        return stepList
    }
}

fun iff(condition: Condition) = IfChain.ThenBuilder(condition)


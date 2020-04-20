package com.github.yona168.visualgorithms.arch

import com.github.yona168.visualgorithms.arch.variables.HasVars
import com.github.yona168.visualgorithms.arch.variables.Vars


enum class VarUsageStrategy {
    USE_AS_PARENT,
    USE_AS_SAME_LEVEL
}

sealed class Step(parentVars: Vars?, varUsageStrategy: VarUsageStrategy) : HasVars {
    override val vars = when (varUsageStrategy) {
        VarUsageStrategy.USE_AS_PARENT -> Vars(
            parentVars
        )
        VarUsageStrategy.USE_AS_SAME_LEVEL -> parentVars ?: Vars()
    }
}

sealed class UncontextedStep {
    abstract fun toContexted(parentVars: Vars?, varUsageStrategy: VarUsageStrategy): Step
}

abstract class ParentStep(parentVars: Vars?, varUsageStrategy: VarUsageStrategy) : Step(parentVars, varUsageStrategy) {
    abstract fun steps(): List<Step>
}

abstract class BarrenStep(parentVars: Vars?, varUsageStrategy: VarUsageStrategy) :
    Step(parentVars, varUsageStrategy)


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

class ContextedContainerStep(containerStep: ContainerStep, parentVars: Vars?, varUsageStrategy: VarUsageStrategy) :
    ParentStep(parentVars, varUsageStrategy) {
    private val children = containerStep.children.map { it.toContexted(parentVars, varUsageStrategyFor(it)) }
    override fun steps() = children

    private fun varUsageStrategyFor(uncontextedStep: UncontextedStep) = when (uncontextedStep) {
        is ActionStep -> VarUsageStrategy.USE_AS_SAME_LEVEL
        is IfChain -> VarUsageStrategy.USE_AS_PARENT
        else -> VarUsageStrategy.USE_AS_PARENT
    }
}

typealias BarrenAction = BarrenStep.() -> Unit
typealias ParentAction = ContainerStep.() -> Unit


//Simple action
class ActionStep(val desc: String, val barrenAction: BarrenAction) : UncontextedStep() {
    override fun toContexted(parentVars: Vars?, varUsageStrategy: VarUsageStrategy) =
        ContextedActionStep(parentVars, varUsageStrategy, desc, barrenAction)
}

class ContextedActionStep(
    parentVars: Vars?,
    usageStrategy: VarUsageStrategy,
    val desc: String,
    val barrenAction: BarrenAction
) : BarrenStep(parentVars, usageStrategy) {
    constructor(parentVars: Vars?, usageStrategy: VarUsageStrategy, actionStep: ActionStep) : this(
        parentVars,
        usageStrategy,
        actionStep.desc,
        actionStep.barrenAction
    )
}

//If and if chains
class If(val condition: Condition, val then: ContainerStep) : UncontextedStep() {
    override fun toContexted(parentVars: Vars?, varUsageStrategy: VarUsageStrategy) =
        ContextedIf(this, parentVars)
}

class IfChain(val ifElseIfs: List<If>, val els: ContainerStep? = null) : UncontextedStep() {
    open class Builder(private val condition: Condition) {
        protected var elseIfs: List<If> = mutableListOf()
        protected var els: ContainerStep? = null
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

    override fun toContexted(parentVars: Vars?, varUsageStrategy: VarUsageStrategy) =
        ContextedIfChain(parentVars, varUsageStrategy, this)
}

class ContextedCheckCondition(val condition: Condition, parentVars: Vars?) :
    BarrenStep(parentVars, VarUsageStrategy.USE_AS_PARENT) {
}

class ContextedIf(private val iff: If, vars: Vars?) : ParentStep(vars, VarUsageStrategy.USE_AS_PARENT) {
    private val thenStep: Step = ContextedContainerStep(iff.then, vars, VarUsageStrategy.USE_AS_PARENT)
    private val contextedCheckCondition: Step = ContextedCheckCondition(iff.condition, vars)
    override fun steps() = listOf(contextedCheckCondition, thenStep)

}

class ContextedIfChain(
    private val parentVars: Vars?,
    private val usageStrategy: VarUsageStrategy,
    private val ifChain: IfChain
) : ParentStep(parentVars, usageStrategy) {
    val contextedIfs = ifChain.ifElseIfs.map { ContextedIf(it, parentVars) }
    val contextedElse: ContextedContainerStep? =
        if (ifChain.els == null) null
        else ContextedContainerStep(ifChain.els, parentVars, VarUsageStrategy.USE_AS_PARENT)

    override fun steps(): List<Step> {
        val stepList = mutableListOf<Step>()
        stepList += contextedIfs
        if (contextedElse != null) stepList += contextedElse
        return stepList
    }

}


fun iff(condition: Condition) = IfChain.ThenBuilder(condition)


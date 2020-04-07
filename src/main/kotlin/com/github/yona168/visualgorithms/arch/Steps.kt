package com.github.yona168.visualgorithms.arch


enum class VarUsageStrategy {
    USE_AS_PARENT,
    USE_AS_SAME_LEVEL
}

sealed class Step(parentVars: Vars?, varUsageStrategy: VarUsageStrategy) {
    val vars = when (varUsageStrategy) {
        VarUsageStrategy.USE_AS_PARENT -> Vars(parentVars)
        VarUsageStrategy.USE_AS_SAME_LEVEL -> parentVars ?: Vars()
    }
}

interface HasSubSteps {
    fun steps(): Sequence<Step>
}

abstract class BarrenStep(parentVars: Vars?, varUsageStrategy: VarUsageStrategy) :
    Step(parentVars, varUsageStrategy)

class ParentStep(parentVars: Vars?, varUsageStrategy: VarUsageStrategy) : Step(parentVars, varUsageStrategy),
    HasSubSteps {
    val children = mutableListOf<Step>()
    override fun steps() = children.asSequence()

    fun add(desc: String, barrenAction: BarrenAction) {
        children += ContextedActionStep(this.vars, VarUsageStrategy.USE_AS_SAME_LEVEL, desc, barrenAction)
    }

    infix fun add(actionStep: ActionStep) {
        children += ContextedActionStep(this.vars, VarUsageStrategy.USE_AS_SAME_LEVEL, actionStep)
    }

    infix fun add(iff: IfChain.Builder) {
        children += ContextedIfChain(this.vars, VarUsageStrategy.USE_AS_PARENT, iff.build())
    }

    infix fun add(iff: () -> IfChain.Builder) {
        add(iff())
    }
}

typealias BarrenAction = BarrenStep.() -> Unit
typealias ParentAction = ParentStep.() -> Unit


class ActionStep(val desc: String, val barrenAction: BarrenAction)
class ContextedActionStep(
    parentVars: Vars?,
    usageStrategy: VarUsageStrategy,
    desc: String,
    val barrenAction: BarrenAction
) : BarrenStep(parentVars, usageStrategy) {
    constructor(parentVars: Vars?, usageStrategy: VarUsageStrategy, actionStep: ActionStep) : this(
        parentVars,
        usageStrategy,
        actionStep.desc,
        actionStep.barrenAction
    )
}

class If(val condition: Condition, val then: ActionStep)
class IfChain(val ifElseIfs: List<If>, val els: ActionStep? = null) {
    open class Builder(private val condition: Condition) {
        protected var elseIfs: List<If> = mutableListOf()
        protected var els: ActionStep? = null
        protected var currentIfCondition = condition

        fun elseIf(condition: Condition) = apply { this.currentIfCondition = condition }
        fun els(els: ActionStep) = apply { this.els = els }
        fun els(desc: String, barrenAction: BarrenAction) = els(ActionStep(desc, barrenAction))

        fun build() = IfChain(elseIfs, els)
    }

    class ThenBuilder(condition: Condition) : Builder(condition) {
        fun then(desc: String, barrenAction: BarrenAction): IfChain.Builder {
            this.elseIfs += If(currentIfCondition, ActionStep(desc, barrenAction))
            return this
        }
    }
}

class ContextedCheckCondition(val condition: Condition, parentVars: Vars?) :
    BarrenStep(parentVars, VarUsageStrategy.USE_AS_PARENT) {
}

class ContextedIf(private val iff: If, vars: Vars?) : BarrenStep(vars, VarUsageStrategy.USE_AS_PARENT), HasSubSteps {
    private val thenStep: Step = ContextedActionStep(vars, VarUsageStrategy.USE_AS_PARENT, iff.then)
    private val contextedCheckCondition: Step = ContextedCheckCondition(iff.condition, vars)
    override fun steps() = sequenceOf(contextedCheckCondition, thenStep)

}

class ContextedIfChain(
    private val parentVars: Vars?,
    private val usageStrategy: VarUsageStrategy,
    private val ifChain: IfChain,
    els: ActionStep? = null
) : BarrenStep(parentVars, usageStrategy), HasSubSteps {
    val contextedIfs = ifChain.ifElseIfs.map { ContextedIf(it, parentVars) }
    val contextedElse: ContextedActionStep? =
        if (els == null) null
        else ContextedActionStep(parentVars, VarUsageStrategy.USE_AS_PARENT, els)

    override fun steps(): Sequence<Step> {
        val stepList= mutableListOf<Step>()
        stepList+=contextedIfs
        if(contextedElse!=null) stepList+=contextedElse
        return stepList.asSequence()
    }

}


fun iff(condition: Condition) = IfChain.ThenBuilder(condition)


package com.github.yona168.visualgorithms.arch.steps

import com.github.yona168.visualgorithms.arch.Condition
import com.github.yona168.visualgorithms.arch.variables.Vars

sealed class UncontextedStep {
    abstract fun toContexted(parentVars: Vars?): Step
}

//Simple action
class UncontextedAction(val desc: String, val plainAction: PlainAction) : UncontextedStep() {
    override fun toContexted(parentVars: Vars?) =
        Action(
            parentVars,
            desc,
            plainAction
        )
}

//If and if chains
class UncontextedIf(val condition: Condition, val then: ContainerStep) : UncontextedStep() {
    override fun toContexted(parentVars: Vars?) =
        If(this, parentVars)
}

class UncontextedIfChain(val ifElseIfs: List<UncontextedIf>, val els: ContainerStep? = null) : UncontextedStep() {
    open class Builder(condition: Condition) {
        protected var elseIfs: List<UncontextedIf> = mutableListOf()
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

        fun build() = UncontextedIfChain(elseIfs, els)
    }

    class ThenBuilder(condition: Condition) : Builder(condition) {
        fun then(containerAction: ParentAction): UncontextedIfChain.Builder {
            this.elseIfs += UncontextedIf(
                currentIfCondition,
                ContainerStep().also(containerAction)
            )
            return this
        }
    }

    override fun toContexted(parentVars: Vars?) =
        IfChain(parentVars, this)
}

class UncontextedFor(
    val initialDesc: String,
    val initial: PlainAction,
    val condition: Condition,
    val afterDesc: String,
    val afterAction: PlainAction,
    val doAction: ParentAction
) : UncontextedStep() {
    override fun toContexted(parentVars: Vars?): Step {
        return For(this, parentVars)
    }

}

class UncontextedWhile(val condition: Condition, val action: ParentAction) : UncontextedStep() {
    override fun toContexted(parentVars: Vars?) =
        While(this, parentVars)
}

class ContainerStep : UncontextedStep() {
    val children = mutableListOf<UncontextedStep>()
    private var currentIfs = mutableListOf<UncontextedIf>()

    fun add(desc: String, plainAction: PlainAction) = add(
        UncontextedAction(
            desc,
            plainAction
        )
    )

    fun add(actionStep: UncontextedStep) {
        children += actionStep
    }

    fun forr(init: UncontextedAction, condition: Condition, after: UncontextedAction, loop: ParentAction) =
        resetAndAdd(
            UncontextedFor(
                init.desc, init.plainAction, condition, after.desc, after.plainAction, loop
            )
        )

    fun whil(condition: Condition, action: ParentAction) =
        resetAndAdd(UncontextedWhile(condition, action))

    private fun resetIfs(els: ContainerStep? = null) {
        if (currentIfs.isNotEmpty()) {
            children += UncontextedIfChain(currentIfs.toList(), els)
            currentIfs.clear()
        }
    }

    private fun resetAnd(action: () -> Unit) = resetIfs().also { action() }
    private fun resetAndAdd(step: UncontextedStep) = resetAnd { add(step) }
    fun iff(condition: Condition, action: ParentAction) = resetAnd {
        currentIfs.add(UncontextedIf(condition, from(action)))
    }

    fun elseIf(condition: Condition, action: ParentAction) {
        if (currentIfs.isEmpty()) {
            throw IllegalStateException("Trying to add an else if before an if!")
        } else {
            currentIfs.add(UncontextedIf(condition, from(action)))
        }
    }

    fun els(action: ParentAction) = resetIfs(from(action))
    override fun toContexted(parentVars: Vars?): ContextedContainerStep {
        resetIfs()
        return ContextedContainerStep(this, parentVars)
    }

    companion object {
        fun from(parentAction: ParentAction) = ContainerStep().also(parentAction)
    }
}

fun a(desc: String, action: PlainAction) =
    UncontextedAction(desc, action)

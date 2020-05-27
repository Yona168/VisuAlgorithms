package com.github.yona168.visualgorithms.arch.steps

import com.github.yona168.visualgorithms.arch.*
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
    override fun toContexted(parentVars: Vars?)=
        While(this, parentVars)
}

class ContainerStep:UncontextedStep() {
    val children = mutableListOf<UncontextedStep>()
    fun add(desc: String, plainAction: PlainAction) = add(
        UncontextedAction(
            desc,
            plainAction
        )
    )
    infix fun add(actionStep: UncontextedAction) {
        children += actionStep
    }

    infix fun addIf(iff: UncontextedIfChain.Builder) {
        children += iff.build()
    }

    infix fun addIf(iff: () -> UncontextedIfChain.Builder) {
        addIf(iff())
    }

    infix fun addFor(forr: UncontextedFor) {
        children += forr
    }

    infix fun addFor(forr: () -> UncontextedFor) = addFor(forr())

    infix fun addWhile(whil: UncontextedWhile){
        children+=whil
    }
    infix fun addWhile(whil: ()-> UncontextedWhile)=addWhile(whil())
    override fun toContexted(parentVars: Vars?)=ContextedContainerStep(this, parentVars)
}
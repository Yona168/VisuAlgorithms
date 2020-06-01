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

open class ContainerStep private constructor(private val parentVars: Vars?) :
    ParentStep(parentVars) {
    private val children = mutableListOf<Step>()
    private var currentIfs = mutableListOf<If>()
    fun add(desc: String, plainAction: PlainAction) = add(
        Action(
            parentVars,
            desc,
            plainAction
        )
    )

    fun add(actionStep: Step) {
        children += actionStep
    }

    fun forr(desc:String,iAction:PlainAction, condition: Condition, aDesc:String,aAction:PlainAction, loop: ParentAction) =
        resetAndAdd(
            For(
                desc, iAction, condition, aDesc, aAction, loop, parentVars
            )
        )

    fun whil(condition: Condition, action: ParentAction) =
        resetAndAdd(While(condition, action, parentVars))

    fun breakk()=resetAndAdd(Break(parentVars))
    fun continuee() = resetAndAdd(Continue(parentVars))

    internal fun resetIfs(els: ContainerStep? = null) {
        if (currentIfs.isNotEmpty()) {
            children += IfChain(currentIfs.toList(), els, parentVars)
            currentIfs.clear()
        }
    }

    private fun resetAnd(action: () -> Unit) = resetIfs().also { action() }
    private fun resetAndAdd(step: Step) = resetAnd { add(step) }

    fun iff(condition: Condition, action: ParentAction) = resetAnd {
        currentIfs.add(If(condition, from(action, parentVars), parentVars))
    }

    fun elseIf(condition: Condition, action: ParentAction) {
        if (currentIfs.isEmpty()) {
            throw IllegalStateException("Trying to add an else if before an if!")
        } else {
            currentIfs.add(If(condition, from(action,parentVars), parentVars))
        }
    }

    fun els(action: ParentAction) = resetIfs(from(action,parentVars))

    companion object {
        fun from(parentAction: ParentAction, vars: Vars?):ContainerStep = ContainerStep(vars).also(parentAction).also{it.resetIfs()}
    }
    override fun steps() = children

}


class Break(vars: Vars?):Step(vars)
class Continue(vars: Vars?):Step(vars)

class Action(
    parentVars: Vars?,
    val desc: String,
    val plainAction: PlainAction
) : Step(parentVars)

class CheckCondition(val condition: Condition, parentVars: Vars?) :
    Step(parentVars)

class If(private val condition: Condition, private val then: ContainerStep, vars: Vars?) : ParentStep(vars) {
    override fun steps() = listOf(CheckCondition(condition, vars), then)
}

class IfChain(
    private val ifElseIfs: List<If>,
    private val els: ContainerStep? = null,
    parentVars: Vars?
) : ParentStep(parentVars) {
    override fun steps(): List<Step> {
        val stepList = mutableListOf<Step>()
        stepList += ifElseIfs
        if (els != null) stepList += els
        return stepList
    }

}

class For(
    private val initialDesc: String,
    private val initial: PlainAction,
    private val condition: Condition,
    private val afterDesc: String,
    private val afterAction: PlainAction,
    private val doAction: ParentAction,
    private val parentVars: Vars?
) : ParentStep(parentVars) {
    override fun steps(): List<Step> {
        val stepList = mutableListOf<Step>()
        stepList += Action(
            parentVars,
            initialDesc,
            initial
        )
        stepList += CheckCondition(
            condition,
            parentVars
        )
        val container = ContainerStep.from(doAction,parentVars)
        stepList += container
        stepList += Action(
            parentVars,
            afterDesc,
            afterAction
        )
        return stepList
    }
}

class While(private val condition: Condition, private val action: ParentAction, vars: Vars?) : ParentStep(vars) {
    override fun steps(): List<Step> {
        return listOf(
            CheckCondition(
                condition,
                vars
            ),
            ContainerStep.from(action, vars)
        )
    }

}




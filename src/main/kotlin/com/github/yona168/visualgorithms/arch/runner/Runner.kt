package com.github.yona168.visualgorithms.arch.runner

import com.github.yona168.visualgorithms.arch.*

interface Runner {
    /**
     * Tracks current step on the parent level
     */
    val thisLevelCurrent: Step?

    val isDone: Boolean

    fun next(): RunResult
    fun previous(): RunResult
}

/*
Basically:
If a runner runs a Parent step, it creates a sub runner for those parents children. When the children are finished,
the sub notifies the main to keep going
next -> Simply returns next in list
runCurrent -> if we are pointing to a parent step, runs the deepest and returns true/false if we have finished that step or not.
               Otherwise just runs the current non parent step
 */
abstract class AbstractRunner(parentStep: ParentStep) : Runner {

    override val thisLevelCurrent: Step?
        get() = if (stepList.indices.contains(stepIndex).not()) null else stepList.get(stepIndex)

    protected val stepList: List<Step> = parentStep.steps()
    protected var stepIndex = -1
    protected var backingIsDone: Boolean = false
    override val isDone: Boolean
        get() = backingIsDone
    private var backingSubRunner: Runner? = null
    protected val currentSubRunner: Runner?
        get() {
            if (thisLevelCurrent is ParentStep) {
                if (backingSubRunner == null) {
                    val current=thisLevelCurrent
                    backingSubRunner = when (current) {
                        is ContextedContainerStep -> ContainerRunner(current)
                        is ContextedIfChain -> IfChainRunner(current)
                        is ContextedIf->IfRunner(current)
                        else->throw IllegalStateException()
                    }
                }
            } else {
                backingSubRunner = null
            }
            return backingSubRunner
        }

    protected fun markAsDone() {
        backingIsDone=true
    }

    override fun previous(): RunResult {
        TODO("Not yet implemented")
    }

    protected fun handleContainerStep(): RunResult {
        val result = currentSubRunner!!.next()
        if (currentSubRunner!!.isDone) {
            this.markAsDone()
        }
        return result
    }


}

class ContainerRunner(parentStep: ContextedContainerStep) : AbstractRunner(
    parentStep
) {
    override fun next(): RunResult {
        if(stepIndex==-1) {
            stepIndex++
        }
        if (thisLevelCurrent is ParentStep && currentSubRunner!!.isDone.not()) {
            val result = currentSubRunner!!.next()
            if (currentSubRunner!!.isDone && (stepIndex == stepList.lastIndex)) { //If we have finished all children
                markAsDone()
            }
            return result
        } else {
            val current=thisLevelCurrent
            val action = current as ContextedActionStep
            action.barrenAction(action)
            stepIndex++
            if (stepIndex > stepList.lastIndex) {
                markAsDone()
            }
            return SuccessDesc(action.desc)
        }
    }
}

class IfChainRunner(parentStep: ContextedIfChain) : AbstractRunner(
    parentStep
) {
    override fun next(): RunResult {
        if (stepIndex == -1) stepIndex++
        val current = thisLevelCurrent
        return when (current) {
            is ContextedIf -> {
                val subRunner = currentSubRunner as IfRunner
                val result = subRunner.next()
                if (subRunner.isDone) { //Either if is false, or true and the then block has stopped executing
                    if (subRunner.conditionIs == true) {
                        this.markAsDone()
                    } else if (subRunner.conditionIs == false) {
                        if(stepIndex==stepList.lastIndex){ //No else or next if statement
                            markAsDone()
                        }
                        else this.stepIndex++ //Move to next if statement or the last else
                    }
                }
                return result
            }
            is ContextedContainerStep -> return handleContainerStep()
            else -> throw IllegalStateException()
        }
    }
}

class IfRunner(parentStep: ContextedIf) : AbstractRunner(parentStep) {
    internal var conditionIs: Boolean? = null
    override fun next(): RunResult {
        if (stepIndex == -1) {
            stepIndex++
        }
        val current = thisLevelCurrent
        return when (current) {
            is ContextedCheckCondition -> {
                val conditionStep = thisLevelCurrent as ContextedCheckCondition
                conditionIs = conditionStep.condition.evaluate()
                if (conditionIs == false) {
                    this.markAsDone()
                } else stepIndex++
                return SuccessDesc("Evaluate ${conditionStep.condition.desc}")
            }
            is ContextedContainerStep -> handleContainerStep()
            else -> throw IllegalStateException()
        }
    }
}


sealed class RunResult
class SuccessDesc(val desc: String) : RunResult()
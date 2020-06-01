package com.github.yona168.visualgorithms.arch.runner

import com.github.yona168.visualgorithms.arch.steps.*

interface Runner {
    /**
     * Tracks current step on the parent level
     */
    val thisLevelCurrent: Step?

    val isDone: Boolean

    fun next(): Result
    fun previous(): Result
}

interface Result {
    val desc: String
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
    private var backingSubRunnerIndex: Int? = null
    protected val currentSubRunner: Runner?
        get() {
            if (thisLevelCurrent is ParentStep) {
                if (backingSubRunner == null || stepIndex != backingSubRunnerIndex) {
                    val current = thisLevelCurrent
                    backingSubRunner = when (current) {
                        is ContainerStep -> ContainerRunner(current)
                        is IfChain -> IfChainRunner(current)
                        is If -> IfRunner(current)
                        is For -> ForRunner(current)
                        is While -> WhileRunner(current)
                        else -> throw IllegalStateException()
                    }
                    backingSubRunnerIndex = stepIndex
                }
            } else {
                resetSubRunner()
            }
            return backingSubRunner
        }

    protected fun resetSubRunner() {
        backingSubRunnerIndex = null
        backingSubRunner = null
    }

    protected fun markAsDone() {
        backingIsDone = true
    }

    override fun previous(): Result {
        TODO("Not yet implemented")
    }

    protected fun handleContainerStep(doneAction: (() -> Unit)? = null): Result {
        val result = currentSubRunner!!.next()
        if (currentSubRunner!!.isDone) {
            if (doneAction != null) {
                doneAction()
            } else if (stepIndex == stepList.lastIndex) {
                this.markAsDone()
            }
        }
        return result
    }
}

class ContainerRunner(parentStep: ContainerStep) : AbstractRunner(
    parentStep
) {
    override fun next(): Result {
        if (stepIndex == -1) {
            stepIndex++
        }
        if (thisLevelCurrent is ParentStep && currentSubRunner!!.isDone.not()) {
            val result = currentSubRunner!!.next()
            if (currentSubRunner!!.isDone) {
                if(stepIndex==stepList.lastIndex){//If we have finished all children
                    markAsDone()
                }else{
                    stepIndex++
                }
            }
            return result
        } else {
            val current = thisLevelCurrent
            val result = when (current) {
                is Action -> {
                    val action = current
                    action.plainAction(action)
                    RunResult(action.desc)
                }
                is Break -> BreakResult()
                is Continue -> ContinueResult()
                else -> throw IllegalStateException()
            }
            stepIndex++
            if (stepIndex > stepList.lastIndex) {
                markAsDone()
            }
            return result
        }
    }
}

class IfChainRunner(parentStep: IfChain) : AbstractRunner(
    parentStep
) {
    override fun next(): Result {
        if (stepIndex == -1) stepIndex++
        val current = thisLevelCurrent

        return when (current) {
            is If -> {
                val subRunner = currentSubRunner as IfRunner
                val result = subRunner.next()
                if (subRunner.isDone) { //Either if is false, or true and the then block has stopped executing
                    if (subRunner.conditionIs == true) {
                        this.markAsDone()
                    } else if (subRunner.conditionIs == false) {
                        if (stepIndex == stepList.lastIndex) { //No else or next if statement
                            markAsDone()
                        } else this.stepIndex++ //Move to next if statement or the last else
                    }
                }
                return result
            }
            is ContainerStep -> return handleContainerStep()
            else -> throw IllegalStateException()
        }
    }
}

class IfRunner(parentStep: If) : AbstractRunner(parentStep) {
    internal var conditionIs: Boolean? = null
    override fun next(): Result {
        if (stepIndex == -1) {
            stepIndex++
        }
        val current = thisLevelCurrent
        return when (current) {
            is CheckCondition -> {
                val conditionStep = thisLevelCurrent as CheckCondition
                conditionIs = conditionStep.condition.evaluate(conditionStep)
                if (conditionIs == false) {
                    this.markAsDone()
                } else {
                    stepIndex++
                }
                val result = RunResult(conditionStep, conditionIs as Boolean)
                return result
            }
            is ContainerStep -> handleContainerStep()
            else -> throw IllegalStateException()
        }
    }
}

class ForRunner(parentStep: For) : AbstractRunner(parentStep) {
    override fun next(): Result {
        if (stepIndex == -1) {
            stepIndex++
        }
        val current = thisLevelCurrent
        when (current) {
            is Action -> {
                current.plainAction(current)
                stepIndex++
                if (stepIndex == stepList.size) {
                    stepIndex = 1
                }
                return RunResult(current.desc)
            }
            is CheckCondition -> {
                val done = current.condition.evaluate(current).not()
                if (done) this.markAsDone()
                stepIndex++
                return RunResult(current, done)
            }
            is ContainerStep -> {
                val result = handleContainerStep { resetSubRunner() }.also { stepIndex++ }
                if (result is BreakResult && !result.broken) {
                    this.markAsDone()
                    result.broken=true
                } else if (result is ContinueResult && !result.continued) {
                    resetSubRunner()
                    stepIndex++
                    result.continued=true
                }
                return result
            }
            else -> throw IllegalStateException()
        }
    }

}

class WhileRunner(parentStep: While) : AbstractRunner(parentStep) {
    override fun next(): Result {
        if (stepIndex == -1) stepIndex++
        val current = thisLevelCurrent
        when (current) {
            is CheckCondition -> {
                val done = current.condition.evaluate(current).not()
                if (done) {
                    this.markAsDone()
                }
                stepIndex++
                return RunResult(current, done.not())
            }
            is ContainerStep -> {
                val result = handleContainerStep {
                    resetSubRunner()
                    stepIndex=0
                }
                if (result is BreakResult && !result.broken) {
                    this.markAsDone()
                    result.broken=true
                } else if (result is ContinueResult && !result.continued) {
                    resetSubRunner()
                    stepIndex = 0
                    result.continued=true
                }
                return result
            }
            else -> throw IllegalStateException()
        }
    }
}


internal open class RunResult(override val desc: String) : Result {
    constructor(checkCondition: CheckCondition, result: Boolean) :
            this("Evaluate ${checkCondition.condition.desc} -> $result")
}

internal class BreakResult : RunResult("Break out of loop") {
    internal var broken = false
}

internal class ContinueResult : RunResult("Continue to next iteration") {
    internal var continued = false
}
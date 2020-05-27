package com.github.yona168.visualgorithms.arch

import com.github.yona168.visualgorithms.arch.runner.ContainerRunner
import com.github.yona168.visualgorithms.arch.steps.ContainerStep
import com.github.yona168.visualgorithms.arch.steps.ContextedContainerStep
import com.github.yona168.visualgorithms.arch.steps.ParentAction
import com.github.yona168.visualgorithms.arch.variables.Vars

fun algorithm(initialVars: Vars? = Vars(), main: ParentAction): ContextedContainerStep {
    return ContainerStep.from(main).toContexted(initialVars)
}

fun run(containerStep: ContextedContainerStep) {
    val runner = ContainerRunner(containerStep)
    while (runner.isDone.not()) {
        val result=runner.next()
    }
}
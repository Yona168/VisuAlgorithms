package com.github.yona168.visualgorithms.arch

import com.github.yona168.visualgorithms.arch.runner.ContainerRunner
import com.github.yona168.visualgorithms.arch.variables.Vars

fun algorithm(initialVars: Vars?, main: ParentAction): ContextedContainerStep {
    val parentStep = ContextedContainerStep(ContainerStep().also(main), initialVars, VarUsageStrategy.USE_AS_PARENT)
    return parentStep
}
fun algorithm(main: ParentAction)=algorithm(null, main)

fun run(containerStep: ContextedContainerStep) {
    val runner = ContainerRunner(containerStep)
    while (runner.isDone.not()) {
        val result=runner.next()
    }
}
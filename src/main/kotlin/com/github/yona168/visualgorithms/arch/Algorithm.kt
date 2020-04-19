package com.github.yona168.visualgorithms.arch

import com.github.yona168.visualgorithms.arch.runner.ContainerRunner

fun algorithm(main: ParentAction):ContextedContainerStep{
    val parentStep=ContextedContainerStep(ContainerStep().also(main), null, VarUsageStrategy.USE_AS_PARENT)
    return parentStep
}

fun run(containerStep:ContextedContainerStep){
    val runner= ContainerRunner(containerStep)
    while(runner.isDone.not()){
        runner.next()
    }
}
package com.github.yona168.visualgorithms.arch

fun algorithm(main: ParentAction):ContextedContainerStep{
    val parentStep=ContextedContainerStep(null, VarUsageStrategy.USE_AS_PARENT)
    main(parentStep)
    return parentStep
}
package com.github.yona168.visualgorithms.arch

fun algorithm(main: ParentAction):ContainerStep{
    val parentStep=ContainerStep(null, VarUsageStrategy.USE_AS_PARENT)
    main(parentStep)
    return parentStep
}
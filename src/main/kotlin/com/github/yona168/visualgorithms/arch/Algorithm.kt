package com.github.yona168.visualgorithms.arch

fun algorithm(main: ParentAction):ParentStep{
    val parentStep=ParentStep(null, VarUsageStrategy.USE_AS_PARENT)
    main(parentStep)
    return parentStep
}
package com.github.yona168.visualgorithms.arch

fun algorithm(main: Action):ContextedActionStep = ContextedActionStep(null, VarUsageStrategy.USE_AS_PARENT,"Begin Algorithm",main)
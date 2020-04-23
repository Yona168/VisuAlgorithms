package com.github.yona168.visualgorithms.arch.runner

import com.github.yona168.visualgorithms.arch.variables.Vars

interface ProgramState{
    val vars: Vars
    val result: RunResult
}


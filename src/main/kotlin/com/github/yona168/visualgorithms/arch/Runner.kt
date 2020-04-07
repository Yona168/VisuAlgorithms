package com.github.yona168.visualgorithms.arch

interface Runner{
    val current: Step
    fun runCurrent()
    fun stepForwards()
    fun stepBackwards()
}
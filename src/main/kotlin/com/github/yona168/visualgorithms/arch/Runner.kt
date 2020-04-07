package com.github.yona168.visualgorithms.arch

interface Runner{
    val current: Step
    fun runCurrent()
    fun stepForwards()
    fun stepBackwards()
}

class SimpleRunner(initial: HasSubSteps) :Runner{
    var innerCurrent:Step
    override val current: Step
        get() = TODO("Not yet implemented")

    override fun runCurrent() {
        TODO("Not yet implemented")
    }

    override fun stepForwards() {
        TODO("Not yet implemented")
    }

    override fun stepBackwards() {
        TODO("Not yet implemented")
    }

}
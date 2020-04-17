package com.github.yona168.visualgorithms.arch.runner
interface VarChange<T>{
    val old: T
    val new: T
}
class ProgramState(varChanges: Set<VarChange<*>>){
    class Builder{
        private val changes = mutableSetOf<VarChange<*>>()
        fun add(change: VarChange<*>){
            changes+=change
        }
    }
}

fun state()=ProgramState.Builder()
package com.github.yona168.visualgorithms.arch.steps

import com.github.yona168.visualgorithms.arch.algorithm
import com.github.yona168.visualgorithms.arch.variables.int

infix fun String.with(int:Int)=Pair(this, int)
infix fun String.from(intPair:Pair<Int, Int>)=Pair(this, intPair)

typealias VarIntPair = Pair<String, Int>
typealias VarStringPair = Pair<String, String>

class Result(internal val desc: String, internal val result: Any)

fun ContainerStep.set(pair: Pair<String, Any>) {
    val desc=when(pair.second){
        is Result->pair.first
        else->"Set ${pair.first} to ${pair.second}"
    }
    add(desc){
        vars[pair.first]=pair.second
    }
}

private fun ContainerStep.mathAction(pair: VarIntPair, desc: String, action: (Int, Int)->Int)=
add("$desc ${pair.first} by ${pair.second}"){
    var num=vars[pair.first] as Int
    vars[pair.first]=action(num, pair.second)
}
fun ContainerStep.increment(pair: VarIntPair)=mathAction(pair, "Increment"){a,b -> a+b}
fun ContainerStep.decrement(pair: VarIntPair)=mathAction(pair, "Decrement"){a,b->a-b}
fun ContainerStep.multiply(pair:VarIntPair)=mathAction(pair, "Multiply"){a,b->a*b}
fun ContainerStep.divide(pair:VarIntPair)=mathAction(pair, "Divide"){a,b->a/b}

fun substring(pair: Pair<String, Pair<Int, Int>>):Result{
    val result=pair.first.substring(pair.second.first, pair.second.second)
    return Result("Substring of ${pair.first} from ${pair.second.first} to ${pair.second.second}", result)
}

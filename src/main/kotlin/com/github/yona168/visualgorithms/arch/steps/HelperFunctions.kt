package com.github.yona168.visualgorithms.arch.steps

import com.github.yona168.visualgorithms.arch.algorithm

class Var(val value: String)

infix fun Var.with(int:Int)=Pair(this, int)
infix fun Var.from(intPair:Pair<Int, Int>)=Pair(this, intPair)

typealias VarIntPair = Pair<Var, Int>
typealias VarStringPair = Pair<Var, String>

class Result(internal val desc: String, internal val result: Any)

fun ContainerStep.set(pair: Pair<Var, Any>) {
    val desc=when(pair.second){
        is Result->"Set ${pair.first.value} to ${(pair.second as Result).desc}"
        else->"Set ${pair.first.value} to ${pair.second}"
    }
    add(desc){
        vars[pair.first.value]=(pair.second as Result).result
    }
}

private fun ContainerStep.mathAction(pair: VarIntPair, desc: String, action: (Int, Int)->Int)=
add("$desc ${pair.first.value} by ${pair.second}"){
    val num=vars[pair.first.value] as Int
    vars[pair.first.value]=action(num, pair.second)
}
fun ContainerStep.increment(pair: VarIntPair)=mathAction(pair, "Increment"){a,b -> a+b}
fun ContainerStep.decrement(pair: VarIntPair)=mathAction(pair, "Decrement"){a,b->a-b}
fun ContainerStep.multiply(pair:VarIntPair)=mathAction(pair, "Multiply"){a,b->a*b}
fun ContainerStep.divide(pair:VarIntPair)=mathAction(pair, "Divide"){a,b->a/b}
/*
fun ContainerStep.substring(pair: Pair<Any, Pair<Int, Int>>):Result{
    val first=pair.first
    val (value, desc)=when(first){
        is  String->listOf(first, first)
        is Var->listOf(this., fir)
    }
    val result=pair.first.substring(pair.second.first, pair.second.second)
    return Result("substring of ${pair.first} from ${pair.second.first} to ${pair.second.second}", result)
}

*/
/*
val algo=algorithm{
    set("x" to 4)
    increment("x" with 3)
    multiply("x" with 2)
    set("y" to substring("hello" from (2 to 4)))
}
 */
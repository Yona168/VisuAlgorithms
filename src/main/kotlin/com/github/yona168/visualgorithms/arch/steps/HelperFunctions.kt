package com.github.yona168.visualgorithms.arch.steps

import com.github.yona168.visualgorithms.arch.algorithm
import java.awt.Container
import java.lang.IndexOutOfBoundsException
import kotlin.reflect.full.functions
import kotlin.reflect.typeOf

class Var(val value: String)

infix fun Var.with(int: Int) = Pair(this, int)
infix fun String.from(intPair: Pair<Int, Int>) = Pair(this, intPair)
infix fun Var.from(intPair: Pair<Int, Int>)=this.value from intPair
infix fun Char.within(other: String)=Pair(this, other)
infix fun Char.within(other: Var)=Pair(this, other)
infix fun String.within(other: String)=Pair(this, other)
infix fun String.within(other: Var)=Pair(this, other)
infix fun Var.within(other: String)=Pair(this, other)
infix fun Var.within(other: Var)=Pair(this, other)
infix fun Any.atIndex(index: Int)=Pair(this, index)
typealias VarIntPair = Pair<Var, Int>
typealias VarDoublePair = Pair<Var, Double>
typealias VarAnyPair = Pair<Var, Any>
typealias VarStringPair = Pair<Var, String>

class Result(internal val desc: String, internal val result: Any)

fun ContainerStep.set(pair: Pair<Var, Any>) {
    val desc = when (pair.second) {
        is Result -> "Set ${pair.first.value} to ${(pair.second as Result).desc}"
        else -> "Set ${pair.first.value} to ${pair.second}"
    }
    add(desc) {
        vars[pair.first.value] = (pair.second as Result).result
    }
}

private fun <T : Number> ContainerStep.mathAction(pair: Pair<Var, T>, desc: String, action: (T, T) -> T) =
    add("$desc ${pair.first.value} by ${pair.second}") {
        val num = vars[pair.first.value] as T
        vars[pair.first.value] = action(num, pair.second)
    }


fun ContainerStep.increment(pair: VarAnyPair) = when (pair.second) {
    is Int -> mathAction(pair as VarIntPair, "Increment") { a, b -> a + b }
    is Double -> mathAction(pair as VarDoublePair, "Increment") { a, b -> a + b }
    else -> throw IllegalArgumentException("Cannot perform an increment operation on this kind of variable!")
}

fun ContainerStep.decrement(pair: VarAnyPair) = when (pair.second) {
    is Int -> mathAction(pair as VarIntPair, "Decrement") { a, b -> a - b }
    is Double -> mathAction(pair as VarDoublePair, "Decrement") { a, b -> a - b }
    else -> throw IllegalArgumentException("Cannot perform a decrement operation on this kind of variable!")
}

fun ContainerStep.multiply(pair: VarAnyPair) = when (pair.second) {
    is Int -> mathAction(pair as VarIntPair, "Multiply") { a, b -> a * b }
    is Double -> mathAction(pair as VarDoublePair, "Multiply") { a, b -> a * b }
    else -> throw IllegalArgumentException("Cannot perform a divide operation on this kind of variable!")
}

fun ContainerStep.divide(pair: VarAnyPair) = when (pair.second) {
    is Int -> mathAction(pair as VarIntPair, "Divide") { a, b -> a / b }
    is Double -> mathAction(pair as VarDoublePair, "Divide") { a, b -> a / b }
    else -> throw IllegalArgumentException("Cannot perform a division operation on this kind of variable!")
}

fun ContainerStep.modulo(pair: VarAnyPair) = when (pair.second) {
    is Int -> mathAction(pair as VarIntPair, "Modulo") { a, b -> a % b }
    is Double -> mathAction(pair as VarDoublePair, "Modulo") { a, b -> a % b }
    else -> throw IllegalArgumentException("Cannot perform a modulo operation on this kind of variable!")
}



fun ContainerStep.substring(pair: Pair<Any, Pair<Int, Int>>):Result{
    val first=pair.first
    val (value, desc)=when(first){
        is  String->listOf(first, "\"$first\"")
        is Var->listOf(vars[first.value] as String, first.value)
        else->throw IllegalArgumentException("Did not provide a String or a variable!")
    }
    val result=value.substring(pair.second.first, pair.second.second)
    return Result("substring of $desc from ${pair.second.first} to ${pair.second.second} = $result", result)
}

fun ContainerStep.indexOf(pair: Pair<Any, Any>):Result{
    val (target, from)=listOf(pair.first, pair.second)
    val fromMethod=when(from){
        is Var->vars[from.value]!!::class.javaObjectType.methods.find { it.name=="indexOf" && it.parameterCount==1 }
        else->from::class.javaObjectType.methods.find{it.name=="indexOf" && it.parameterCount==1}
    }
    if(fromMethod==null){
        throw IllegalArgumentException("Cannot call indexOf on the passed in argument!")
    }else{
        val result=fromMethod.invoke(from, target)
        return Result("index of ${toString(target)} within ${toString(from)} = result", result)
    }
}

@ExperimentalStdlibApi
fun ContainerStep.getFrom(args: Pair<Any, Int>):Result{
    val (collection, index) = listOf(args.first, args.second)
    val targetClass=getTargetClass(collection)
    val getMethod= targetClass.functions.find { it.name=="get" && it.parameters[1].type==typeOf<Int>() }
        ?: throw IllegalArgumentException("The passed in argument does not have a get function!")
    val result=getMethod.call(collection, index) as Any
    return Result("element #${index} in ${toString(collection)} = ${toString(result)}", result)
}

private fun toString(any: Any?):String = when(any){
    is String ->"\"$any\""
    is Char->"\'$any\'"
    is Var->any.value
    is Array<*> -> "[array]"
    is List<*> -> "[list]"
    is Set<*> -> "[set]"
    else ->if(any==null)"null" else "[object{"
}
private fun ContainerStep.getTargetClass(target: Any)=when(target){
    is Var->vars[target.value]!!::class
    else->target::class
}

val algo=algorithm{
    set(Var("x") to 4)
    increment(Var("x") with 3)
    multiply(Var("x") with 2)
    set(Var("y") to substring("hello" from (2 to 4)))
}

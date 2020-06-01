package com.github.yona168.visualgorithms.arch.steps

class Var(val value: String)

infix fun Var.with(int: Int) = Pair(this, int)
infix fun Var.from(intPair: Pair<Int, Int>) = Pair(this, intPair)

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
package com.github.yona168.visualgorithms.arch

import java.lang.UnsupportedOperationException

/**
 * Base class for all variables to be used in an algorithm.
 * @property[name] the name of this variable (ie "x")
 * @property[value] The value of this variable
 */
abstract class Variable<T>(protected val name: String, protected var value: T){

}
private fun throwExec(): Nothing =throw UnsupportedOperationException("Can't do this! (Yet)!")

/**
 * Represents an [Int]
 */
class IntVariable(name:String, value: Int):Variable<Int>(name, value){
    operator fun plus(other: IntVariable) = int(name, this.value+other.value)
    operator fun plusAssign(other: IntVariable){this.value+=other.value}
    operator fun plusAssign(other: Int){this.value+=other}
    operator fun minus(other: IntVariable)=int(name, this.value-other.value)
    operator fun minusAssign(other: IntVariable){this.value-=other.value}
    operator fun times(other: IntVariable)=int(name, this.value * other.value)
    operator fun timesAssign(other: IntVariable){this.value*=other.value}
    operator fun div(other: IntVariable)=int(name, this.value/other.value)
    operator fun divAssign(other: IntVariable){this.value/=other.value}

    /**
     * @return true if [other] is an Int that matches this [value], or if other is an [IntVariable] with a matching
     * [value]. Returns false otherwise
     */
    override fun equals(other: Any?)=(other is Int && this.value==other)||(other is IntVariable && this.value==other.value)
}

/**
 * Represents a [String]
 */
class StringVariable(name:String, value:String):Variable<String>(name, value){
    operator fun plus(other: StringVariable)=StringVariable(name, value+other.value)
    operator fun plusAssign(other: StringVariable){this.value+=other.value}

    /**
     * @return true if [other] is a String that matches this [value], or if other is a [StringVariable] with a matching
     * [value]. Returns false otherwise
     */
    override fun equals(other: Any?)=(other is String && this.value==other)||(other is StringVariable && this.value==other.value)
}

/**
 * Convenience function for creating an [IntVariable]
 */
fun int(name: String, value:Int)=IntVariable(name, value)

/**
 * Convenience function for creating a [StringVariable]
 */
fun string(name: String, value:String)=StringVariable(name, value)

/**
 * Represents the vars available within a scope.
 * @param[parent] vars from an outer scope that can also be accessed
 */
class Vars(private val parent: Vars? = null) {
    private val myVars: VarMap = mutableMapOf()

    private fun getOrDefault(key: String, default: Variable<*>):Variable<*>{
        return get(key)?:default
    }

    /**
     * Gets a variable based on its name. If no variable is found in the current scope, moves up to the next highest scope,
     * and so on.
     * @param[key] The name of the variable to find
     * @return the [Variable], or null if not found
     */
    operator fun get(key: String): Variable<*>? {
        return myVars.getOrDefault(key, parent?.get(key))
    }

    /**
     * Sets a variable name to a [Variable]. The scope at which this is set depends upon the following:
     * 1. If the name already exists in the current scope, it is set in this scope.
     * 2. Otherwise, if the name exists in a parent scope, it is set in that scope.
     * 3. Otherwise, the variable is newly intialized in this scope.
     *
     * @param[key] The name to set the variable to
     * @param[value] The variable
     */
    operator fun set(key: String, value: Variable<*>) {
        if(myVars[key]!=null){
            myVars[key]=value
        } else if(parent?.get(key)!=null){
            parent[key]=value
        }else{
            myVars[key]=value
        }
    }

    /**
     * Convenience function
     */
    operator fun set(key: String, value:Int)=set(key, int(key, value))

    /**
     * Convenience function
     */
    operator fun set(key: String, value: String)=set(key, string(key, value))
}
package com.github.yona168.visualgorithms.arch

import javafx.beans.property.SimpleIntegerProperty
import javafx.beans.property.SimpleStringProperty
import javafx.beans.value.ObservableValue
import javafx.collections.ObservableMap
import kotlin.properties.ReadOnlyProperty

/*
/**
 * Base class for all variables to be used in an algorithm.
 * @property[name] the name of this variable (ie "x")
 * @property[value] The value of this variable
 */
abstract class Variable<T> constructor(val name: String, var value: T) {

}

private fun throwExec(): Nothing = throw UnsupportedOperationException("Can't do this! (Yet)!")

/**
 * Represents an [Int]
 */
class IntVariable(name: String, value: Int) : Variable<Int>(name, value) {
    operator fun plus(other: IntVariable) = int(name, this.value + other.value)
    operator fun plus(other: Int) = int(name, this.value + other)
    operator fun plusAssign(other: IntVariable) {
        this.value += other.value
    }

    operator fun plusAssign(other: Int) {
        this.value += other
    }

    operator fun minus(other: IntVariable) = int(name, this.value - other.value)
    operator fun minus(other: Int) = int(name, this.value - other)
    operator fun minusAssign(other: IntVariable) {
        this.value -= other.value
    }

    operator fun minusAssign(other: Int) {
        this.value -= other
    }

    operator fun times(other: IntVariable) = int(name, this.value * other.value)
    operator fun times(other: Int) = int(name, this.value * other)
    operator fun timesAssign(other: IntVariable) {
        this.value *= other.value
    }

    operator fun timesAssign(other: Int) {
        this.value *= other
    }

    operator fun div(other: IntVariable) = int(name, this.value / other.value)
    operator fun div(other: Int) = int(name, this.value / other)
    operator fun divAssign(other: IntVariable) {
        this.value /= other.value
    }

    operator fun divAssign(other: Int) {
        this.value /= other
    }

    operator fun rem(other: IntVariable) = int(name, this.value % other.value)
    operator fun rem(other: Int) = int(name, this.value % other)
    operator fun remAssign(other: IntVariable) {
        this.value %= other.value
    }

    operator fun remAssign(other: Int) {
        this.value %= other
    }

    /**
     * @return true if [other] is an Int that matches this [value], or if other is an [IntVariable] with a matching
     * [value]. Returns false otherwise
     */
    override fun equals(other: Any?) =
        (other is Int && this.value == other) || (other is IntVariable && this.value == other.value)
}

/**
 * Represents a [String]
 */
class StringVariable(name: String, value: String) : Variable<String>(name, value) {
    operator fun plus(other: StringVariable) = StringVariable(name, value + other.value)
    operator fun plusAssign(other: StringVariable) {
        this.value += other.value
    }

    /**
     * @return true if [other] is a String that matches this [value], or if other is a [StringVariable] with a matching
     * [value]. Returns false otherwise
     */
    override fun equals(other: Any?) =
        (other is String && this.value == other) || (other is StringVariable && this.value == other.value)
}
**/
/**
 * Convenience function for creating an [IntVariable]
 */
fun int(name: String, value: Int) = SimpleIntegerProperty(null, name, value)

/**
 * Convenience function for creating a [StringVariable]
 */
fun string(name: String, value: String) = SimpleStringProperty(null, name, value)

/**
 * Represents the vars available within a scope.
 * @param[parent] vars from an outer scope that can also be accessed
 */
typealias VarMap = MutableMap<String, ObservableValue<*>>

class Vars(private val parent: Vars? = null){
    private val myVars: VarMap = mutableMapOf()
    private val onChanges: MutableList<((ObservableValue<*>?,ObservableValue<*>?) -> Unit)> = mutableListOf()

    init{
        if(parent!=null){
            onChanges+=parent.onChanges
        }
    }

    private fun getOrDefault(key: String, default: ObservableValue<*>): ObservableValue<*>{
        return get(key) ?: default
    }

    /**
     * Gets a variable based on its name. If no variable is found in the current scope, moves up to the next highest scope,
     * and so on.
     * @param[key] The name of the variable to find
     * @return the [Variable], or null if not found
     */
    operator fun get(key: String): ObservableValue<*>? {
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
    operator fun set(key: String, value: ObservableValue<*>) {
        val oldValue=get(key)
        if (myVars[key] != null) {
            myVars[key] = value
            onChanges.forEach { it(oldValue, value) }
        } else if (parent?.get(key) != null) {
            parent[key] = value
        } else {
            myVars[key] = value
            onChanges.forEach { it(oldValue, value) }
        }
    }

    /**
     * Convenience function
     */
    operator fun set(key: String, value: Int) = set(key, int(key, value))

    /**
     * Convenience function
     */
    operator fun set(key: String, value: String) = set(key, string(key, value))
}
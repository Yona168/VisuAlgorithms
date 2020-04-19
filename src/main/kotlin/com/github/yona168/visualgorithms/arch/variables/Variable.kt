package com.github.yona168.visualgorithms.arch.variables

import javafx.beans.property.SimpleIntegerProperty
import javafx.beans.property.SimpleStringProperty
import javafx.beans.value.ObservableValue


/**
 * Base class for all variables to be used in an algorithm.
 * @property[name] the name of this variable (ie "x")
 * @property[value] The value of this variable
 */
abstract class Variable<T> constructor(val name: String) {
    var value: T
        get() = observableProperty.value as T
        set(value) {
            setObservableValue(value)
        }
    internal abstract val observableProperty: ObservableValue<in T>
    protected abstract fun setObservableValue(value: T)
}

private fun throwExec(): Nothing = throw UnsupportedOperationException("Can't do this! (Yet)!")

/**
 * Represents an [Int]
 */
class IntVariable(name: String, value: Int) : Variable<Int>(name) {
    override val observableProperty = SimpleIntegerProperty(null, name, value)
    operator fun plus(other: IntVariable) =
        int(name, this.value + other.value)

    operator fun plus(other: Int) =
        int(name, this.value + other)

    operator fun plusAssign(other: IntVariable) {
        this.value += other.value
    }

    operator fun plusAssign(other: Int) {
        this.value += other
    }

    operator fun minus(other: IntVariable) =
        int(name, this.value - other.value)

    operator fun minus(other: Int) =
        int(name, this.value - other)

    operator fun minusAssign(other: IntVariable) {
        this.value -= other.value
    }

    operator fun minusAssign(other: Int) {
        this.value -= other
    }

    operator fun times(other: IntVariable) =
        int(name, this.value * other.value)

    operator fun times(other: Int) =
        int(name, this.value * other)

    operator fun timesAssign(other: IntVariable) {
        this.value *= other.value
    }

    operator fun timesAssign(other: Int) {
        this.value *= other
    }

    operator fun div(other: IntVariable) =
        int(name, this.value / other.value)

    operator fun div(other: Int) =
        int(name, this.value / other)

    operator fun divAssign(other: IntVariable) {
        this.value /= other.value
    }

    operator fun divAssign(other: Int) {
        this.value /= other
    }

    operator fun rem(other: IntVariable) =
        int(name, this.value % other.value)

    operator fun rem(other: Int) =
        int(name, this.value % other)

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

    override fun setObservableValue(value: Int) {
        observableProperty.value = value
    }
}

/**
 * Represents a [String]
 */
class StringVariable(name: String, value: String) : Variable<String>(name) {
    override val observableProperty = SimpleStringProperty(null, name, value)
    operator fun plus(other: StringVariable) = this.plus(other.value)
    operator fun plus(other: String) =
        string(name, value + other)

    operator fun plusAssign(other: StringVariable) = this.plusAssign(other.value)
    operator fun plusAssign(other: String) {
        this.value += other
    }

    /**
     * @return true if [other] is a String that matches this [value], or if other is a [StringVariable] with a matching
     * [value]. Returns false otherwise
     */
    override fun equals(other: Any?) =
        (other is String && this.value == other) || (other is StringVariable && this.value == other.value)

    override fun setObservableValue(value: String) {
        observableProperty.value = value
    }
}

/*
operator fun SimpleIntegerProperty.plus(other: Int)=int(name, value+other)
operator fun SimpleIntegerProperty.plus(other: SimpleIntegerProperty)=this.plus(other.value)
operator fun SimpleIntegerProperty.plusAssign(other:Int){
    this.value+=other
}
operator fun SimpleIntegerProperty.plusAssign(other:SimpleIntegerProperty)=this.plusAssign(other.value)

operator fun SimpleIntegerProperty.minus(other:Int)=int(name, value-other)
operator fun SimpleIntegerProperty.minus(other: SimpleIntegerProperty)=this.minus(other.value)
operator fun SimpleIntegerProperty.minusAssign(other:Int){
    this.value-=other
}
operator fun SimpleIntegerProperty.minusAssign(other:SimpleIntegerProperty)=this.minusAssign(other.value)

operator fun SimpleIntegerProperty.times(other:Int)=int(name, value*other)
operator fun SimpleIntegerProperty.times(other:SimpleIntegerProperty)=this.times(other.value)
operator fun SimpleIntegerProperty.timesAssign(other:Int){
    value+=other
}
operator fun SimpleIntegerProperty.timesAssign(other:SimpleIntegerProperty)=this.timesAssign(other.value)

operator fun SimpleIntegerProperty.div(other:Int)=int(name, value/other)
operator fun SimpleIntegerProperty.div(other: SimpleIntegerProperty)=this.div(other.value)
operator fun SimpleIntegerProperty.divAssign(other:Int){
    this.value/=other
}
operator fun SimpleIntegerProperty.divAssign(other:SimpleIntegerProperty)=this.divAssign(other.value)

operator fun SimpleIntegerProperty.rem(other:Int)=int(name, value%other)
operator fun SimpleIntegerProperty.rem(other: SimpleIntegerProperty)=this.div(other.value)
operator fun SimpleIntegerProperty.remAssign(other:Int){
    this.value%=other
}
operator fun SimpleIntegerProperty.remAssign(other: SimpleIntegerProperty)=this.remAssign(other.value)
*/
/**
 * Convenience function for creating an [IntVariable]
 */
fun int(name: String, value: Int) =
    IntVariable(name, value)

/**
 * Convenience function for creating a [StringVariable]
 */
fun string(name: String, value: String) =
    StringVariable(name, value)

val Int.v: IntVariable
    get() = int("Temp", this)
val String.v: StringVariable
    get() = string("Temp", this)
/**
 * Represents the vars available within a scope.
 * @param[parent] vars from an outer scope that can also be accessed
 */
typealias VarMap = MutableMap<String, Variable<*>>

class Vars(private val parent: Vars? = null) {
    private val myVars: VarMap = mutableMapOf()

    private fun getOrDefault(key: String, default: Variable<*>): Variable<*> {
        return get(key) ?: default
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
        val oldValue = get(key)
        if (myVars[key] != null) {
            myVars[key] = value
        } else if (parent?.get(key) != null) {
            parent[key] = value
        } else {
            myVars[key] = value
        }
    }

    /**
     * Convenience function
     */
    operator fun set(key: String, value: Int) = set(
        key,
        int(key, value)
    )

    /**
     * Convenience function
     */
    operator fun set(key: String, value: String) = set(
        key,
        string(key, value)
    )

}
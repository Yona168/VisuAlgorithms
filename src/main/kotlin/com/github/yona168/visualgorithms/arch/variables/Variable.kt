package com.github.yona168.visualgorithms.arch.variables

import javafx.beans.property.SimpleBooleanProperty
import javafx.beans.property.SimpleIntegerProperty
import javafx.beans.property.SimpleListProperty
import javafx.beans.property.SimpleStringProperty
import javafx.beans.value.ObservableValue
import javafx.collections.FXCollections
import javafx.collections.ObservableList


/*
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

class ListVariable<T> private constructor(name: String, value: SimpleListProperty<T>) :
    Variable<ObservableList<T>>(name), MutableList<T> by value {
    constructor(name: String, value: List<T>) : this(
        name,
        SimpleListProperty(null, name, FXCollections.observableArrayList(value))
    )

    override val observableProperty = value
    override fun setObservableValue(value: ObservableList<T>) {
        observableProperty.value = value
    }

    override fun equals(other: Any?): Boolean {
        return (other is List<*> && other == value)||(other is ListVariable<*> && other.value==value)
    }
}

/**
 * Represents an [Int]
 */
class IntVariable(name: String, value: Int) : Variable<Int>(name), Comparable<IntVariable> {
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

    override fun compareTo(other: IntVariable)=value.compareTo(other.value)
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

class BooleanVariable(name: String, value: Boolean) : Variable<Boolean>(name) {
    override val observableProperty = SimpleBooleanProperty(null, name, value)
    override fun setObservableValue(value: Boolean) {
        observableProperty.value = value
    }

    override fun equals(other: Any?)=(other is BooleanVariable && value==other.value)||(other is Boolean && value==other)
}

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

fun bool(name: String, value: Boolean) = BooleanVariable(name, value)
fun <T> list(name: String, vararg elements: T) = ListVariable(name, mutableListOf(*elements))

val Int.v: IntVariable
    get() = int("Temp", this)
val String.v: StringVariable
    get() = string("Temp", this)
val List<*>.v: ListVariable<*>
    get() = list("Temp", this)
val Boolean.v: BooleanVariable
    get() = bool("Temp", this)
/**
 * Represents the vars available within a scope.
 * @param[parent] vars from an outer scope that can also be accessed
 */
 */

typealias VarMap = MutableMap<String, Any>

class Vars(myVars: VarMap?=null) {

    private val myVars: VarMap = myVars ?: mutableMapOf()

    /**
     * Sets a variable name to a [Variable]. The scope at which this is set depends upon the following:
     * 1. If the name already exists in the current scope, it is set in this scope.
     * 2. Otherwise, if the name exists in a parent scope, it is set in that scope.
     * 3. Otherwise, the variable is newly intialized in this scope.
     *
     * @param[key] The name to set the variable to
     * @param[value] The variable
     */
    operator fun set(key: String, value: Any) {
        myVars[key]=value
    }
    operator fun get(key: String)=myVars[key]

    override fun equals(other: Any?) = other is Vars && this.myVars==other.myVars

    fun clone()=Vars(myVars.toMutableMap())
}
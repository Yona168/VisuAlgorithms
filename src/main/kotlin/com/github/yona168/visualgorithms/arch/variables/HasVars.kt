package com.github.yona168.visualgorithms.arch.variables

import kotlin.reflect.KProperty

abstract class HasVars {
    abstract val vars: Vars
    var i: Int by Delegate()
    var n: Int by Delegate()
    var str: String by Delegate()

    companion object {
        private class Delegate<T> {
            operator fun getValue(thisRef: HasVars, property: KProperty<*>): T {
                val varName = property.name
                return thisRef.vars[varName] as T
            }

            operator fun setValue(thisRef: HasVars, property: KProperty<*>, value: T) {
                thisRef.vars[property.name] = value as Any
            }
        }
    }
}

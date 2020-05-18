package com.github.yona168.visualgorithms.arch.variables

import kotlin.reflect.KProperty

abstract class HasVars {
    abstract val vars: Vars
    var i: IntVariable by IntDelegate()
    var n: IntVariable by IntDelegate()

    companion object{
        private class IntDelegate{
            operator fun getValue(thisRef:HasVars, property: KProperty<*>):IntVariable {
                val varName=property.name
                if(thisRef.vars[varName]==null){
                    thisRef.vars[varName]=0
                }
                return thisRef.vars[varName] as IntVariable
            }
            operator fun setValue(thisRef: HasVars, property: KProperty<*>, value: IntVariable){
                thisRef.vars[property.name]=value
            }
            operator fun setValue(thisRef: HasVars, property: KProperty<*>, value: Int){
                thisRef.vars[property.name]=value.v
            }
        }
        private class StringDelegate{
            operator fun getValue(thisRef: HasVars, property: KProperty<*>):StringVariable{
                val varName=property.name
                if(thisRef.vars[varName]==null){
                    thisRef.vars[varName]=""
                }
                return thisRef.vars[varName] as StringVariable
            }
            operator fun setValue(thisRef: HasVars, property: KProperty<*>, value:StringVariable){
                thisRef.vars[property.name]=value
            }
            operator fun setValue(thisRef: HasVars, property: KProperty<*>, value:String){
                thisRef.vars[property.name]=value
            }
        }
    }
}
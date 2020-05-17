package com.github.yona168.visualgorithms.arch.variables

interface HasVars {
    val vars: Vars
    var i: IntVariable
        get()=vars["i"] as IntVariable
        set(value) {
            vars["i"] = value
        }
    var n: IntVariable
        get()=vars["n"] as IntVariable
        set(value){
            vars["n"]=value
        }
}
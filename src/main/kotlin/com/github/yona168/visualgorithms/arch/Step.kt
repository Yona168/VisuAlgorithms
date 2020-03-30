package com.github.yona168.visualgorithms.arch

enum class VarUsageStrategy{
    USE_AS_PARENT,
    USE_AS_SAME_LEVEL
}
sealed class Step(parentVars: Vars?, varUsageStrategy: VarUsageStrategy){
    abstract fun run():Boolean
    val vars=when(varUsageStrategy){
        VarUsageStrategy.USE_AS_PARENT -> Vars(parentVars)
        VarUsageStrategy.USE_AS_SAME_LEVEL -> parentVars?:Vars()
    }
}

typealias Action = ContextedActionStep.() -> Unit
typealias VarMap = MutableMap<String, Variable<*>>


class ActionStep(val desc: String, val action: Action)
class ContextedActionStep(parentVars: Vars?, usageStrategy: VarUsageStrategy, desc: String, private val action: Action) : Step(parentVars, usageStrategy) {
    constructor(parentVars: Vars?, usageStrategy: VarUsageStrategy, actionStep: ActionStep):this(parentVars, usageStrategy, actionStep.desc, actionStep.action)
    val children= mutableListOf<Step>()
    fun add(desc: String, action: Action){
        children+=ContextedActionStep(this.vars, VarUsageStrategy.USE_AS_SAME_LEVEL, desc, action)
    }
    infix fun add(actionStep: ActionStep){
        children+=ContextedActionStep(this.vars, VarUsageStrategy.USE_AS_SAME_LEVEL, actionStep)
    }
    infix fun add(iff: IfChain){
        children+=ContextedIfChain(this.vars, VarUsageStrategy.USE_AS_PARENT, iff)
    }
    infix fun add(iff: ()->IfChain){
        add(iff())
    }
    override fun run():Boolean {
        action()
        children.forEach{it.run()}
        return true
    }

}

class If(val condition: Condition, val then: ActionStep)
class IfChain(val ifElseIfs: List<If>, val els:ActionStep? = null) {
    open class Builder(private val condition: Condition){
        protected var elseIfs: List<If> = mutableListOf()
        protected var els: ActionStep? = null
        protected var currentIfCondition=condition

        fun elseIf(condition: Condition)=apply{this.currentIfCondition=condition}
        fun els(els: ActionStep):IfChain{
            this.els=els
            return this.build()
        }
        fun els(desc: String, action: Action)=els(ActionStep(desc, action))

        fun build()=IfChain(elseIfs, els)
    }
    class ThenBuilder(condition: Condition):Builder(condition){
        fun then(desc: String, action: Action):IfChain.Builder{
            this.elseIfs+=If(currentIfCondition, ActionStep(desc, action))
            return this
        }
    }
}

class ContextedIf(private val iff: If, vars: Vars?):Step(vars, VarUsageStrategy.USE_AS_PARENT){
    private val thenStep = ContextedActionStep(vars, VarUsageStrategy.USE_AS_PARENT, iff.then)
    override fun run():Boolean {
        if(iff.condition.evaluate()){
            thenStep.run()
            return true
        }
        return false
    }

}
class ContextedIfChain(private val parentVars: Vars?, private val usageStrategy: VarUsageStrategy, private val ifElseIfs: List<If>, els:ActionStep? = null):Step(parentVars, usageStrategy){
    constructor(parentVars: Vars?,usageStrategy: VarUsageStrategy, iff: IfChain):this(parentVars, usageStrategy,iff.ifElseIfs, iff.els)
    val contextedIfs=ifElseIfs.map{ContextedIf(it, parentVars)}
    override fun run():Boolean {
        for(iff in contextedIfs){
            if(iff.run()){
                return true
            }
        }
        return false
    }

}


class While(parentVars: Vars?, val condition: Condition, private val loop: ActionStep) : Step(parentVars, VarUsageStrategy.USE_AS_PARENT){
    private val contextedActionStep = ContextedActionStep(this.vars, VarUsageStrategy.USE_AS_PARENT, loop)
    override fun run():Boolean {
        while(condition.evaluate()){
            contextedActionStep.run()
        }
        return true
    }
}
class For(val parentVars: Vars?,val looper: String, val range: IntRange, val loop: ActionStep) : Step(parentVars, VarUsageStrategy.USE_AS_PARENT){
    private val contextedActionStep = ContextedActionStep(this.vars, VarUsageStrategy.USE_AS_PARENT, loop)
    override fun run():Boolean {
        for(i in range){
            contextedActionStep.run()
        }
        return true
    }
}
fun iff(condition: Condition)=IfChain.ThenBuilder(condition)


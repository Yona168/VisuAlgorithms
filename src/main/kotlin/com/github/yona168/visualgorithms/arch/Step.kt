package com.github.yona168.visualgorithms.arch

sealed class Step(parentVars: Vars?){
    abstract fun run()
    val vars=Vars(parentVars)
    val children= mutableListOf<Step>()
    fun add(desc: String, action: Action){
        children+=ContextedActionStep(this.vars, desc, action)
    }
    infix fun add(actionStep: ActionStep){
        children+=ContextedActionStep(this.vars, actionStep)
    }
    infix fun add(iff: IfChain){
        children+=ContextedIfChain(this.vars, iff)
    }
    infix fun add(iff: ()->IfChain){
        add(iff())
    }
}

typealias Action = ContextedActionStep.() -> Unit
typealias VarMap = MutableMap<String, Variable<*>>


class ActionStep(val desc: String, val action: Action)
class ContextedActionStep(parentVars: Vars?, desc: String, private val action: Action) : Step(parentVars) {
    constructor(parentVars: Vars?, actionStep: ActionStep):this(parentVars, actionStep.desc, actionStep.action)

    override fun run() {
        action()
        children.forEach(Step::run)
    }

}

class If(val condition: Condition, val then: ActionStep){

}
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
class ContextedIfChain(parentVars: Vars?, ifElseIfs: List<If>? = null, els:ActionStep? = null):Step(parentVars){
    constructor(parentVars: Vars?,iff: IfChain):this(parentVars, iff.ifElseIfs, iff.els)
    override fun run() {
        TODO("Not yet implemented")
    }

}


class While(parentVars: Vars?, val condition: Condition, private val loop: ActionStep) : Step(parentVars){
    private val contextedActionStep = ContextedActionStep(this.vars, loop )
    override fun run() {
        while(condition.evaluate()){
            contextedActionStep.run()
        }
    }
}
class For(val parentVars: Vars?,val looper: String, val range: IntRange, val loop: ActionStep) : Step(parentVars){
    private val contextedActionStep = ContextedActionStep(this.vars, loop)
    override fun run() {
        for(i in range){
            contextedActionStep.run()
        }
    }
}
fun iff(condition: Condition)=IfChain.ThenBuilder(condition)


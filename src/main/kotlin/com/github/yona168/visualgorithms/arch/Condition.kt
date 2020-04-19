package com.github.yona168.visualgorithms.arch

import com.github.yona168.visualgorithms.arch.variables.HasVars

/**
 * Represents a classic condition (ie x==5)
 */
sealed class Condition {
    /**
     * Evaluates whether this condition is true
     * @return a [Boolean] representing if this condition is true or false
     */
    abstract fun evaluate(checkConditionStep: ContextedCheckCondition): Boolean
    abstract val desc: String
}

/**
 * A container for two conditions, evaluated by either both being true ([AndParent]) or one being true ([OrParent])
 */
abstract class ParentCondition(protected val conditionOne: Condition, protected val conditionTwo: Condition) :
    Condition()

/**
 * A [Condition] representing and (&&)
 */
class AndParent(one: Condition, two: Condition) : ParentCondition(one, two) {
    /**
     * @return true if both [one] and [two] are true, false otherwise
     */
    override fun evaluate(checkConditionStep: ContextedCheckCondition) =
        conditionOne.evaluate(checkConditionStep) && conditionTwo.evaluate(checkConditionStep)

    override val desc: String
        get() = "${conditionOne.desc} and ${conditionTwo.desc}"
}

/**
 * A [Condition] representing or (||)
 */
class OrParent(one: Condition, two: Condition) : ParentCondition(one, two) {
    /**
     * @return [true] if either [one] or [two] is true, false otherwise
     */
    override fun evaluate(checkConditionStep: ContextedCheckCondition) =
        conditionOne.evaluate(checkConditionStep) || conditionTwo.evaluate(checkConditionStep)
    override val desc: String
        get() = "${conditionOne.desc} or ${conditionTwo.desc}"
}

/**
 * A classic condition that takes a supplied [Boolean] to evaluate itself
 * @property[desc] A [String] describing this condition for readability
 * @property[supplier] The supplier for [evaluate] to call
 */
class BoolCondition(override val desc: String, private val supplier: HasVars.() -> Boolean) : Condition() {
    /**
     * @return the [Boolean] returned by [supplier]
     */
    override fun evaluate(checkConditionStep: ContextedCheckCondition) = supplier(checkConditionStep)
}

/**
 * Links two conditions together into an "and" relationship
 * @param[other] the other condition to link with this one
 * @return an [AndParent] containing this condition and the [other]
 */
infix fun Condition.and(other: Condition) = AndParent(this, other)
fun Condition.and(desc: String, condition: HasVars.() -> Boolean) = AndParent(this, BoolCondition(desc, condition))

/**
 * Links two conditions together into an "Or" relationship
 * @param[other] the other condition to link with this one
 * @return an [OrParent] containing this condition and the [other]
 */
infix fun Condition.or(other: Condition) = OrParent(this, other)
fun Condition.or(desc: String, other: HasVars.() -> Boolean) = OrParent(this, BoolCondition(desc, other))

/**
 * Convenience function for creating a [BoolCondition]
 */
fun c(desc: String, bool: HasVars.() -> Boolean) = BoolCondition(desc, bool)

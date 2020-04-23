package variables

import com.github.yona168.visualgorithms.arch.*
import com.github.yona168.visualgorithms.arch.variables.Vars
import com.github.yona168.visualgorithms.arch.variables.v
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe

class VarsEquality: StringSpec({
    "Empty Vars equal each other"{
        Vars() shouldBe Vars()
    }
    "One level vars equal each other"{
        val (one, two)=listOf(Vars(), Vars())
        one["x"]=5
        two["x"]=5
        one shouldBe two
        two["y"]=5
        one shouldNotBe two
    }
    "Multiple levels equal each other"{
        makeMultiLevelVars() shouldBe makeMultiLevelVars()
        makeMultiLevelVars() shouldNotBe makeOtherMultiLevelVars()
    }
    "Getting from multiple levels"{
        val vars= makeMultiLevelVars()
        vars["x"] shouldBe 5
        vars["a"] shouldBe "banana"
        vars["two"] shouldBe "three"
    }
})

private fun makeMultiLevelVars():Vars{
    val parent=Vars().also {
        it["x"]=5
        it["y"]=3
    }
    val childOne=Vars(parent).also{
        it["a"]="banana"
        it["b"]="apple"
    }
    val childTwo=Vars(childOne).also{
        it["one"]=4
        it["two"]="three"
    }
    return childTwo
}
private fun makeOtherMultiLevelVars():Vars{
    val parent=Vars().also {
        it["x"]=8
        it["y"]=3
    }
    val childOne=Vars(parent).also{
        it["a"]="banana"
        it["c"]="apyple"
    }
    val childTwo=Vars(childOne).also{
        it["one"]=3
        it["twoo"]="three"
    }
    return childTwo
}
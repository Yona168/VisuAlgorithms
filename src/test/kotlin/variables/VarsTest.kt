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
    "Cloning produces identical vars"{
        val vars=Vars()
        vars["x"]=5
        vars["y"]="Hello"
        vars["z"]=true
        vars["c"]=listOf("Hey","Hello")
        val cloned=vars.clone()
        val otherVars=Vars()
        otherVars["x"]=3
        vars shouldBe cloned
    }
})

package variables

import com.github.yona168.visualgorithms.arch.variables.bool
import com.github.yona168.visualgorithms.arch.variables.int
import com.github.yona168.visualgorithms.arch.variables.list
import com.github.yona168.visualgorithms.arch.variables.string
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import io.kotest.property.*
class VariableToKotlinEquality: StringSpec({
    "Int variable equals Int"{
        checkAll<Int>{a->
            int("a", a) shouldBe a
        }
    }
    "String variable equals String"{
        checkAll<String>{str->
            string("str", str) shouldBe str
        }
    }
    "Int variable equals Int variable"{
        checkAll<Int>{a->
            int(
                "a",
                a
            ) == int("a", a)
        }
    }
    "String variable equals String variable"{
        checkAll<String>{str->
            string(
                "str",
                str
            ) == string("str", str)
        }
    }
    "Boolean variable equals boolean variable"{
        bool("t", true) shouldBe bool("t",true)
        bool("t", true) shouldNotBe bool("t", false)
    }
    "Boolean variable equals boolean"{
        bool("t", true) shouldBe true
        bool("t", false) shouldBe false
        bool("t", true) shouldNotBe false
        bool("t", false) shouldNotBe true
    }
    "List variable equals list variable"{
        checkAll<Int, Int, Int>{a,b,c->
            list("temp",a,b,c) shouldBe list("temp",a,b,c)
            if(c!=a){
                list("Temp",a,b,c) shouldNotBe list("temp",c,b,a)
            }else{
                list("Temp",a,b,c) shouldBe list("Temp",c,b,a)
            }
        }
    }
    "List Variable equals list"{
        checkAll<Int, Int, Int>{a,b,c->
            list("temp",a,b,c) shouldBe listOf(a,b,c)
            if(c!=a){
                list("temp",a,b,c) shouldNotBe listOf(c,b,a)
            }else{
                list("Temp",a,b,c) shouldBe listOf(c,b,a)
            }
        }
    }
})
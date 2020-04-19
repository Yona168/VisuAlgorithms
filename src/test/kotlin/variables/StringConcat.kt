package variables

import com.github.yona168.visualgorithms.arch.variables.string
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import io.kotest.property.checkAll

class StringConcat : StringSpec({
    "String variable w/ string variable concatenation"{
        checkAll<String, String> { a, b ->
            val aVar = string("a", a)
            val bVar = string("b", b)
            val str = a+b
            str shouldBe a+b
            aVar += bVar
            aVar shouldBe a+b
        }
    }
    "String variable w/ string concatenation"{
        checkAll<String, String> { a, b ->
            val aVar= string("a", a)
            aVar + b shouldBe a + b
            aVar+=b
            aVar shouldBe a+b
        }
    }
})
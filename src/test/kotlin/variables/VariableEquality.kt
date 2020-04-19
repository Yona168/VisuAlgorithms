package variables

import com.github.yona168.visualgorithms.arch.int
import com.github.yona168.visualgorithms.arch.string
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import io.kotest.property.*
class VariableEquality: StringSpec({
    "Int variable equals Int"{
        checkAll<Int>{a->
            int("a",a) shouldBe a
        }
    }
    "String variable equals String"{
        checkAll<String>{str->
            string("str",str) shouldBe str
        }
    }
    "Int variable equals Int variable"{
        checkAll<Int>{a->
            int("a",a)==int("a",a)
        }
    }
    "String variable equals String variale"{
        checkAll<String>{str->
            string("str",str)==string("str",str)
        }
    }
})
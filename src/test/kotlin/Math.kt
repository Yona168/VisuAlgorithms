import com.github.yona168.visualgorithms.arch.int
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import io.kotest.property.PropTestConfig
import io.kotest.property.arbitrary.arb
import io.kotest.property.checkAll


class TestIntVarMath: StringSpec({
    "Adding two numbers"{
        checkAll<Int, Int>{a,b ->
            a+b shouldBe (int("A", a)+int("B", b)).value as Int
        }
    }
    "Subtracting two numbers"{
        checkAll<Int, Int>{a,b ->
            a-b shouldBe (int("A", a)-int("B", b)).value as Int
        }
    }
    "Multiplying two numbers"{
        checkAll<Int, Int>{a,b ->
            a*b shouldBe (int("A", a)*int("B", b)).value as Int
        }
    }
    "Dividing two numbers"{
        val nonZeroArb = arb{rs->
            generateSequence {
                var random=rs.random.nextInt()
                while(random==0){
                    random=rs.random.nextInt()
                }
                random
            }
        }
        checkAll<Int, Int>(nonZeroArb, nonZeroArb){ a, b ->
            a/b shouldBe (int("A", a)/int("B", b)).value as Int
        }
    }
})
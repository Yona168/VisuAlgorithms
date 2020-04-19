package variables

import com.github.yona168.visualgorithms.arch.int
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import io.kotest.property.Arb
import io.kotest.property.arbitrary.arb
import io.kotest.property.arbitrary.int
import io.kotest.property.checkAll


class TestIntVarMath : StringSpec({
    "Adding two numbers"{
        checkAll<Int, Int> { a, b ->
            a + b shouldBe (int("A", a) + int("B", b)).value
            (int("A", a) + b).value shouldBe a + b
        }
    }
    "Subtracting two numbers"{
        checkAll<Int, Int> { a, b ->
            a - b shouldBe (int("A", a) - int("B", b)).value
            (int("a", a) - b).value shouldBe a - b
        }
    }
    "Multiplying two numbers"{
        checkAll<Int, Int> { a, b ->
            a * b shouldBe (int("A", a) * int("B", b)).value
            (int("A", a) * b).value shouldBe a * b
        }
    }
    "Dividing two numbers"{
        checkAll<Int, Int>(nonZeroArb, nonZeroArb) { a, b ->
            a / b shouldBe (int("A", a) / int("B", b)).value
            (int("A", a) / b).value shouldBe a / b
        }
    }
    "Modulo two numbers"{
        checkAll(Arb.int(), nonZeroArb) { a, b ->
            int("A", a) % int("B", b) shouldBe a % b
            (int("A", a) % b).value shouldBe a % b
        }
    }
})

class TestIntVarMathAssign : StringSpec({
    "Plus Assign"{
        checkAll<Int, Int> { a, b ->
            val va = int("a", a)
            val ca = int("a", a)
            va += b
            ca += int("B", b)
            va.value shouldBe a + b
            ca.value shouldBe a + b
        }
    }
    "Minus Assign"{
        checkAll<Int, Int> { a, b ->
            val va = int("a", a)
            val ca = int("a", a)
            va -= b
            ca -= int("b", b)
            va.value shouldBe a - b
            ca.value shouldBe a - b
        }
    }
    "Multiply Assign"{
        checkAll<Int, Int> { a, b ->
            val va = int("a", a)
            val ca = int("a", a)
            va *= b
            ca *= int("b", b)
            ca.value shouldBe a * b
            va.value shouldBe a * b
        }
    }
    "Divide Assign"{
        checkAll(Arb.int(), nonZeroArb) { a, b ->
            val va = int("a", a)
            val ca = int("a", a)
            va /= b
            ca /= int("b", b)
            va.value shouldBe a / b
            ca.value shouldBe a / b
        }
    }
    "Modulo Assign"{
        checkAll(Arb.int(), nonZeroArb) { a, b ->
            val va = int("a", a)
            val ca = int("a", a)
            va %= b
            ca %= int("b", b)
            va.value shouldBe a % b
            ca.value shouldBe a % b
        }
    }
})

val nonZeroArb = arb { rs ->
    generateSequence {
        var random = rs.random.nextInt()
        while (random == 0) {
            random = rs.random.nextInt()
        }
        random
    }
}
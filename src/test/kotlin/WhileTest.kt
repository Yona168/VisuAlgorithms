import com.github.yona168.visualgorithms.arch.algorithm
import com.github.yona168.visualgorithms.arch.c
import com.github.yona168.visualgorithms.arch.run
import com.github.yona168.visualgorithms.arch.steps.increment
import com.github.yona168.visualgorithms.arch.steps.with

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe

class WhileTest : StringSpec({
    "Basic while loop test"{
        val emptyList = mutableListOf<Int>()
        val algo = algorithm {
            add("set i to 0"){i=0}
            whil(c("i is less than 5") { i < 5 }) {
                add("increment i by 1"){i++}
                add("Add i to emptyList") { emptyList.add(i) }
            }
        }
        run(algo)
        emptyList shouldBe listOf(1, 2, 3, 4)
    }
})
import com.github.yona168.visualgorithms.arch.algorithm
import com.github.yona168.visualgorithms.arch.c
import com.github.yona168.visualgorithms.arch.run
import com.github.yona168.visualgorithms.arch.steps.a
import com.github.yona168.visualgorithms.arch.variables.v
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe

class ForTest : StringSpec({
    "Basic for loop test"{
        val emptyList = mutableListOf<Int>()
        val algo = algorithm {
            forr(
                a(
                    "set i to 0"
                ) { i = 0},
                c("Check if i<5") { i < 5},
                a("increment i") { i = i + 1 }) {
                add("Add i to emptyList") { emptyList.add(i) }
            }
        }
        run(algo)
        emptyList shouldBe listOf(0, 1, 2, 3, 4)
    }
})
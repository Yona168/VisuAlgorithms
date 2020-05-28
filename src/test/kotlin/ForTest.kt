import com.github.yona168.visualgorithms.arch.algorithm
import com.github.yona168.visualgorithms.arch.c
import com.github.yona168.visualgorithms.arch.run
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe

class ForTest : StringSpec({
    "Basic for loop test"{
        val emptyList = mutableListOf<Int>()
        val algo = algorithm {
            forr(
                "set i to 0",
                {i=0},
                c("Check if i<5") { i < 5},
                "Increment i", {i=i+1}) {
                add("Add i to emptyList") { emptyList.add(i) }
            }
        }
        run(algo)
        emptyList shouldBe listOf(0, 1, 2, 3, 4)
    }
})
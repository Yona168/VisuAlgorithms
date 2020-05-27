import com.github.yona168.visualgorithms.arch.*
import com.github.yona168.visualgorithms.arch.steps.a
import com.github.yona168.visualgorithms.arch.steps.forr
import com.github.yona168.visualgorithms.arch.variables.v
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe

class ForTest : StringSpec({
    "Basic for loop test"{
        val emptyList = mutableListOf<Int>()
        val algo = algorithm {
            addFor {
                forr(
                    a(
                        "set i to 0"
                    ) { i = 0.v },
                    c("Check if i<5") { i < 5.v },
                    a("increment i") { i = i + 1 }) {
                    add("Add i to emptyList") { emptyList.add(i.value) }
                }
            }
    }
    run(algo)
    emptyList shouldBe listOf(0, 1, 2, 3, 4)
}
})
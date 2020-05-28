import com.github.yona168.visualgorithms.arch.algorithm
import com.github.yona168.visualgorithms.arch.c
import com.github.yona168.visualgorithms.arch.run
import com.github.yona168.visualgorithms.arch.steps.ContainerStep
import com.github.yona168.visualgorithms.arch.variables.Vars
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe

class IfTest : StringSpec({
    "If tests true conditions successfully"{
        var bool = false
        val program = algorithm {
            iff(c("True") { true }) {
                add("Change bool to true") { bool = true }
            }
        }
        run(program)
        bool shouldBe true
    }

    "If tests false conditions successfully"{
        var bool = true
        val program = algorithm {
            iff(c("False") { false }) {
                add("Change bool to false") { bool = false }
            }
        }
        run(program)
        bool shouldBe true
    }

    "If chain works on intermediate ifs"{
        var strr = ""
        fun getProgram(initial: Pair<String, String>? = null): ContainerStep {
            val initialVars = Vars()
            if (initial != null) {
                initialVars[initial.first] = initial.second
            }
            return algorithm(initialVars) {
                iff(c("x is apple") { vars["x"] == "apple" }) {
                    add("set str to apple") { strr = "apple" }
                }
                elseIf(c("z is banana") { vars["z"] == "banana" }) {
                    add("set str to banana") { strr = "banana" }
                }
                els {
                    add("set str to orange") { strr = "orange" }
                }

            }
        }
        run(getProgram("x" to "apple"))
        strr shouldBe "apple"
        run(getProgram("z" to "banana"))
        strr shouldBe "banana"
        run(getProgram())
        strr shouldBe "orange"
    }
})
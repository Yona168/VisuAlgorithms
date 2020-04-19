import com.github.yona168.visualgorithms.arch.algorithm
import com.github.yona168.visualgorithms.arch.c
import com.github.yona168.visualgorithms.arch.iff
import com.github.yona168.visualgorithms.arch.run
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe

class IfTest: StringSpec({
    "If tests true conditions successfully"{
        var bool=false
        val program=algorithm{
            add{iff(c("True"){true}).then{
                add("Change bool to true"){bool=true}
            }}
        }
        run(program)
        bool shouldBe true
    }

    "If tests false conditions successfully"{
        var bool=true
        val program=algorithm {
           add{iff(c("False"){false}).then{
                add("Change bool to false"){bool=false}
            }}
        }
        run(program)
        bool shouldBe true
    }


})
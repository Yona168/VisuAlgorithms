import com.github.yona168.visualgorithms.arch.algorithm
import com.github.yona168.visualgorithms.arch.c
import com.github.yona168.visualgorithms.arch.iff
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe

class IfTest: StringSpec({
    "If tests true conditions successfully"{
        var bool=false
        algorithm{
            add{iff(c("True"){true}).then("Change bool to true"){
                bool=true
            }}
        }.run()
        bool shouldBe true
    }
    "If tests false conditions successfully"{
        var bool=true
        algorithm {
           add{iff(c("False"){false}).then("Change bool to false"){
                bool=false
            }}
        }
        bool shouldBe true
    }
})
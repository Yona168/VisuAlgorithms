import com.github.yona168.visualgorithms.arch.algorithm
import com.github.yona168.visualgorithms.arch.c
import com.github.yona168.visualgorithms.arch.run
import com.github.yona168.visualgorithms.arch.variables.v
import com.github.yona168.visualgorithms.arch.whil
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe

class WhileTest: StringSpec({
    "Basic while loop test"{
        val emptyList=mutableListOf<Int>()
        val algo= algorithm {
            addWhile{
                whil(c("i is less than 5"){i<5.v}){
                    add("Increment i"){i=i+1}
                    add("Add i to emptyList"){emptyList.add(i.value)}
                }
            }
        }
        run(algo)
        emptyList shouldBe listOf(1,2,3,4)
    }
})
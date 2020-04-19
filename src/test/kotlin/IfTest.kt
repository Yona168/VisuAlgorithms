import com.github.yona168.visualgorithms.arch.*
import com.github.yona168.visualgorithms.arch.variables.StringVariable
import com.github.yona168.visualgorithms.arch.variables.Vars
import com.github.yona168.visualgorithms.arch.variables.v
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

    "If chain works on intermediate ifs"{
        var str=""
        fun getProgram(initial: Pair<String, String>? = null):ContextedContainerStep{
            val initialVars= Vars()
            if(initial!=null){
                initialVars[initial.first]=initial.second
            }
            return algorithm(initialVars){
                iff(c("x is apple"){vars["x"]=="apple".v}).then{
                    add("set str to apple"){str="apple"}
                }.elseIf(c("z is banana"){vars["z"]=="banana".v}).then{
                    add("set str to banana"){vars["z"]=="banana".v}
                }.els{
                    add("set str to orange"){str="orange"}
                }
            }
        }
        run(getProgram())
        str shouldBe "orange"
        run(getProgram("x" to "apple"))
        str shouldBe "apple"
        run(getProgram("z" to "banana"))
        str shouldBe "banana"
    }
})
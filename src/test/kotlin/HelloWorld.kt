import com.github.yona168.visualgorithms.arch.algorithm
import com.github.yona168.visualgorithms.arch.run
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe


class HelloWorld:StringSpec({
    "This algorithm should print Hello World"{
        var printedText: String = "Didn't work"
        val program=algorithm{
            add("set var printedText to Hello World"){vars["printedText"]="Hello World!"}
            add("Set outer var to it as well"){vars["printedText"]="Hello World!"}
        }
        run(program)
        printedText.shouldBe("Hello World!")
    }
})
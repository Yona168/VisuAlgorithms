import com.github.yona168.visualgorithms.arch.algorithm
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe


class HelloWorld:StringSpec({
    "This algorithm should print Hello World"{
        var printedText: String = "Didn't work"
        algorithm{
            vars["printedText"]="Hello World!"
            printedText=vars["printedText"]?.value as String
        }.run()
        printedText.shouldBe("Hello World!")
    }
})
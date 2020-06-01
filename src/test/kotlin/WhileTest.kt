import com.github.yona168.visualgorithms.arch.algorithm
import com.github.yona168.visualgorithms.arch.c
import com.github.yona168.visualgorithms.arch.run
import com.github.yona168.visualgorithms.arch.steps.increment
import com.github.yona168.visualgorithms.arch.steps.with

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe

class WhileTest : StringSpec({

    "Basic while loop test"{
        val emptyList = mutableListOf<Int>()
        val emptyList2 = mutableListOf<Int>()
        val algo = algorithm {
            add("set i to 0"){i=0}
            whil(c("i is less than 5") { i < 5 }) {
                add("increment i by 1"){i++}
                add("Add i to emptyList") { emptyList.add(i) }
            }
        }
        val algo2 = algorithm{
            add("set i to 0"){i=0}
            whil(c("i is less than 5"){i<5}){
                add("add i to the list"){emptyList2.add(i)}
                add("Increment i by 1"){i++}
            }
        }
        run(algo)
        run(algo2)
        emptyList shouldBe listOf(1, 2, 3, 4, 5)
        emptyList2 shouldBe listOf(0,1,2,3,4)
    }
    "Break"{
        var c=0
        val algo = algorithm {
            add("set i to 0"){i=0}
            whil(c("i is less than 5"){i<5}){
                add("Increment i by 1"){i++}
                iff(c("i is 3"){i==3}){
                    add("Set c to i"){c=i}
                    breakk()
                }
                add("Set c to 0"){c=0}
            }
        }
        run(algo)
        c shouldBe 3
    }

    "Continue"{
        var changed=false
        var c=0
        val algo= algorithm {
            add("Set i to 0"){i=0}
            whil(c("i<5"){i<5}){
                add("increment i"){i++}
                iff(c("i is 3"){i==3}){
                    continuee()
                }
                iff(c("i is 3"){i==3}){
                    add("Toggle changed"){changed=true}
                }
                add("increment c"){c++}
            }
        }
        run(algo)
        changed shouldBe false
        c shouldBe 4
        algo.i shouldBe 5
    }
})
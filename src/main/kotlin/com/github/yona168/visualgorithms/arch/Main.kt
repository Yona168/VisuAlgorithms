package com.github.yona168.visualgorithms.arch

fun main() {

    algorithm {
        vars["x"] = int("x", 2)
        add{
            iff(c("x is 5") {vars["x"]!!.equals(5)} or c("y is 5", {true}))
                .then("Do something"){/*Doing Something*/}
                .els("Do something else"){/*Doing Something Else*/}
        }

    }
}

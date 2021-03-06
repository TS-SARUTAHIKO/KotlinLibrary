package com.xxxsarutahikoxxx.kotlin.RMInvocation

fun main(args: Array<String>) {
    val rr = object : RR {
        override fun sample(): Any = "NoArg"
        override fun sample(a: Int): Any = "Arg $a"
    }

    RMIHostRegistry().let {
        it["test"] = rr

        it.openWebPort()
    }
}

interface RR : Remote {
    fun sample() : Any
    fun sample(a : Int) : Any
}

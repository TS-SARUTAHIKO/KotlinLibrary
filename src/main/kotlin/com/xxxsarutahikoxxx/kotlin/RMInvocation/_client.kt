package com.xxxsarutahikoxxx.kotlin.RMInvocation

import com.xxxsarutahikoxxx.kotlin.Utilitys.out

fun main(args: Array<String>) {
    RMIClientRegistry().let {
        it.connectWebPort()

        val rr : RR = it.lookup("test")
        out = rr.sample()
    }
}
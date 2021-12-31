package org.xxxsarutahikoxxx.kotlin.RMInvocation

import org.xxxsarutahikoxxx.kotlin.Utilitys.out

fun main(args: Array<String>) {
    RMIClientRegistry().let {
        it.connectWebPort("localhost", 53456)

        val rr : RR = it.lookup("test")
        out = rr.sample()
    }
}
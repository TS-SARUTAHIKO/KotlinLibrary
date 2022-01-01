package org.xxxsarutahikoxxx.kotlin.kotlinlibrary.RSInvocation

import org.xxxsarutahikoxxx.kotlin.kotlinlibrary.IORunner.ClientTCPRunner
import java.io.Serializable

fun main(args: Array<String>) {

    object : ClientTCPRunner("localhost", 53456), RSIExporter, RSIAccepter {
        init {
            gp.exporter = this
            gp.accepter = this
        }

        override fun exportRSI(rsi: RSInvocation<*>) = writeObject(rsi)
        override var onAccept: ((RSInvocation<*>) -> Unit)? = null

        override fun onAccept(obj: Serializable) {
            super.onAccept(obj)

            if( obj is RSInvocation<*> )onAccept?.invoke(obj)
        }
    }.apply {
        connect()
    }

}

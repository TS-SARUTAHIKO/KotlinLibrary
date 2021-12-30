package org.xxxsarutahikoxxx.kotlin.RSInvocation

import org.xxxsarutahikoxxx.kotlin.SocketRunner.ClientWebRunner
import java.io.Serializable

fun main(args: Array<String>) {

    object : ClientWebRunner(), RSIExporter, RSIAccepter {
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
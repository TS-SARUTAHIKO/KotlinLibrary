package org.xxxsarutahikoxxx.kotlin.kotlinlibrary.RSInvocation

import org.xxxsarutahikoxxx.kotlin.kotlinlibrary.IORunner.HostTCPRunner
import org.xxxsarutahikoxxx.kotlin.kotlinlibrary.Utilitys.out
import java.io.Serializable

fun main(args: Array<String>) {

    object : HostTCPRunner(53456), RSIExporter, RSIAccepter {
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
        open()
    }

    val obj : Test = Test.of("Tom")
    obj.parent("aaa", 20)

    // parent / child はそれぞれ一回ずつ実行される
}





class Test private constructor(val name : String, port : RSIPort) : RSISerializable, hasRSIPort by port {

    fun parent(arg1 : String, arg2 : Int) = rsiExport {
        out = "ARG1 : $arg1"
        child(arg2)
    }
    fun child(age : Int) = rsiExport {
        out = "Name : $name,  Age : $age"
    }

    companion object {
        @JvmStatic private val serialVersionUID: Long = 1L

        private fun of(name : String, port : RSIPort, id : String) : Test = port.rsiExport {
            Test(name, port).apply { RSIID = id }
        }
        fun of(name : String) : Test = of(name, gp, gp.nextID())
    }
}

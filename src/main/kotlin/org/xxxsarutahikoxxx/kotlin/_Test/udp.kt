package org.xxxsarutahikoxxx.kotlin._Test

import org.xxxsarutahikoxxx.kotlin.IORunner.HostUDPRunner
import org.xxxsarutahikoxxx.kotlin.Utilitys.out


fun main(){
    val port = 53456
    val size = 30

    object : HostUDPRunner(port, size){
        var temp = System.currentTimeMillis()
        override fun onAccept(byte: ByteArray) {
            var time = System.currentTimeMillis()
            out = "${time-temp} ${String(byte)}"
            temp = time
        }
    }.open()

}
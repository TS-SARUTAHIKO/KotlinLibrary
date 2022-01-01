package org.xxxsarutahikoxxx.kotlin.kotlinlibrary.IORunner

import org.xxxsarutahikoxxx.kotlin.kotlinlibrary.Utilitys.out
import kotlinx.coroutines.runBlocking
import java.net.*


val socket by lazy { DatagramSocket(53456) }
fun receiveUDP() : String {
    val buf = ByteArray(12)
    val packet : DatagramPacket = DatagramPacket(buf, buf.size)
    socket.receive(packet)

    return String(packet.data, 0, packet.length)
}


fun main() {
    runBlocking {
        var time1 = System.currentTimeMillis()

        while( true ) {
            var time2 = System.currentTimeMillis()

//            delay(4)
            out = receiveUDP() + " : " + (time2-time1)

            time1 = time2
        }
    }
}
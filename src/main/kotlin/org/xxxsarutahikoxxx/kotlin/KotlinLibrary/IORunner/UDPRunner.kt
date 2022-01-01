package org.xxxsarutahikoxxx.kotlin.KotlinLibrary.IORunner

import org.xxxsarutahikoxxx.kotlin.KotlinLibrary.Utilitys.out
import java.lang.RuntimeException
import java.net.DatagramPacket
import java.net.DatagramSocket
import java.net.InetAddress
import java.nio.ByteBuffer

abstract class UDPRunner(
    /** ターゲット・アドレス */
    val address : String?,
    /** ターゲット・ポート */
    val port : Int,
    /**  */
    val bufferSize : Int,
    /**  */
    val isBroadcast : Boolean,

    hostMode : Boolean,
    isAutoReconnect : Boolean,
    isAutoReader : Boolean
) : IORunner(hostMode, isAutoReconnect, isAutoReader){

    private var socket : DatagramSocket? = null
    var socketInit : DatagramSocket.()->(Unit) = {}

    override fun openPort() {
        socket = DatagramSocket(port).apply { /*broadcast = isBroadcast ;*/ socketInit() }

        onOpened()
    }
    override fun connectPort() {
        socket = DatagramSocket().apply { broadcast = isBroadcast ; socketInit() }

        onConnected()
    }

    override fun writeData(bytes: ByteArray) {
        val data = ByteBuffer.allocate(bufferSize).apply { put(bytes) }.array()

        val packet = DatagramPacket(data, data.size, InetAddress.getByName(address?:"255.255.255.255"), port)

        socket?.apply {
            send(packet)
            return
        }

        throw NoStreamException("No OutputStream")
    }
    override fun readData(): ByteArray {
        socket?.apply {
            val data = DatagramPacket(ByteArray(bufferSize), bufferSize)
            receive(data)

            return data.data
        }

        throw NoStreamException("No InputStream")
    }

    override fun close() {
        super.close()

        socket?.close()
    }
}

open class HostUDPRunner(
    port : Int,
    bufferSize: Int,
    isAutoReconnect : Boolean = true,
    isAutoReader: Boolean = true
) : UDPRunner(null, port, bufferSize, false, true, isAutoReconnect, isAutoReader) {
    @Deprecated("HostRunner can open, can't connect")
    override fun connectPort() = throw RuntimeException("HostWebRunner can open, can't connect")
}

open class ClientUDPRunner(
    address : String?,
    port : Int,
    bufferSize: Int,
    isBroadcast: Boolean,
    isAutoReconnect : Boolean = true,
    isAutoReader: Boolean = true
) : UDPRunner(address ?: if(isBroadcast) "255.255.255.255" else null, port, bufferSize, isBroadcast, false, isAutoReconnect, isAutoReader) {
    @Deprecated("ClientRunner can connect, can't open")
    override fun openPort() = throw RuntimeException("ClientWebRunner can connect, can't open")
}
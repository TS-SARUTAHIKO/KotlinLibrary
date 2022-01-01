package org.xxxsarutahikoxxx.kotlin.IORunner

import org.xxxsarutahikoxxx.kotlin.Utilitys.out
import java.lang.RuntimeException
import java.net.InetSocketAddress
import java.net.ServerSocket
import java.net.Socket

abstract class TCPRunner(
    /** ターゲット・アドレス */
    val address : String,
    /** ターゲット・ポート */
    val port : Int,

    hostMode : Boolean,
    isAutoReconnect : Boolean,
    isAutoReader : Boolean
) : IOStreamRunner(hostMode, isAutoReconnect, isAutoReader){

    private var socket : Socket? = null
    var socketInit : Socket.()->(Unit) = {}

    override fun openPort() {
        out = "Accept Connection..."

        socket = ServerSocket().run {
            this.reuseAddress = true
            this.bind(InetSocketAddress(port))

            val ret = accept()
            close()

            ret.socketInit()
            ret
        }

        out = "Opened : $socket"
        onOpened(socket!!.getInputStream(), socket!!.getOutputStream())
    }
    override fun connectPort() {
        out = "Connecting..."

        socket = Socket(address, port).apply {
            socketInit()
        }

        out = "Connected : $socket"
        onConnected(socket!!.getInputStream(), socket!!.getOutputStream())
    }

    override fun close() {
        super.close()

        socket?.close()
    }
}

open class HostTCPRunner(
    port : Int,
    isAutoReconnect : Boolean = true,
    isAutoReader: Boolean = true
) : TCPRunner("localhost", port, true, isAutoReconnect, isAutoReader) {
    @Deprecated("HostRunner can open, can't connect")
    override fun connectPort() = throw RuntimeException("HostWebRunner can open, can't connect")
}

open class ClientTCPRunner(
    address : String,
    port : Int,
    isAutoReconnect : Boolean = true,
    isAutoReader: Boolean = true
) : TCPRunner(address, port, false, isAutoReconnect, isAutoReader) {
    @Deprecated("ClientRunner can connect, can't open")
    override fun openPort() = throw RuntimeException("ClientWebRunner can connect, can't open")
}
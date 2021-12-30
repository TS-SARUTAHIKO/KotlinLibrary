package org.xxxsarutahikoxxx.kotlin.SocketRunner

import org.xxxsarutahikoxxx.kotlin.Utilitys.out
import java.lang.RuntimeException
import java.net.InetSocketAddress
import java.net.ServerSocket
import java.net.Socket

abstract class WebRunner(
    /** ターゲット・アドレス */
    val address : String,
    /** ターゲット・ポート */
    val port : Int,

    hostMode : Boolean,
    isAutoReconnect : Boolean,
    isAutoReader : Boolean
) : SocketRunner(hostMode, isAutoReconnect, isAutoReader){

    private var socket : Socket? = null
    var socketInit : Socket.()->(Unit) = {}

    override fun open() {
        out = "Accept Connection..."

        socket = ServerSocket().run {
            this.reuseAddress = true
            this.bind(InetSocketAddress(port))

            val ret = accept()
            close()

            ret
        }

        out = "Opened : $socket"
        onOpened(socket!!.getInputStream(), socket!!.getOutputStream())
    }
    override fun connect() {
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

    companion object {
        internal const val WEB_RUNNER_TEST_PORT = 53456
    }
}

open class HostWebRunner(
    port : Int = WEB_RUNNER_TEST_PORT,
    isAutoReconnect : Boolean = true,
    isAutoReader: Boolean = true
) : WebRunner("localhost", port, true, isAutoReconnect, isAutoReader) {
    @Deprecated("HostRunner can open, can't connect")
    override fun connect() = throw RuntimeException("HostWebRunner can open, can't connect")
}

open class ClientWebRunner(
    address : String = "localhost",
    port : Int = WEB_RUNNER_TEST_PORT,
    isAutoReconnect : Boolean = true,
    isAutoReader: Boolean = true
) : WebRunner(address, port, false, isAutoReconnect, isAutoReader) {
    @Deprecated("ClientRunner can connect, can't open")
    override fun open() = throw RuntimeException("ClientWebRunner can connect, can't open")
}
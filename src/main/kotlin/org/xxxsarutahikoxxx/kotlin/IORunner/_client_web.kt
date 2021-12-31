package org.xxxsarutahikoxxx.kotlin.IORunner

import org.xxxsarutahikoxxx.kotlin.Utilitys.out
import java.io.Serializable


fun main(args: Array<String>) {
    /**
     * デフォルトのポートを使って localhost に接続する
     * 接続したらデフォルトでは 'Hello Server!' と送信する
     * */
    object : ClientTCPRunner("localhost", 53456){
        override fun onConnected() {
            super.onConnected()

            writeObject("Hello Server!")
            writeObject("Are you ready?")
        }
        override fun onAccept(obj: Serializable) {
            out = "from server : $obj"
        }
    }.connect()

}
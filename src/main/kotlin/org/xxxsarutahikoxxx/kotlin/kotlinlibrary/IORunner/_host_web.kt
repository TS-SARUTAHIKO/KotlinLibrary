package org.xxxsarutahikoxxx.kotlin.kotlinlibrary.IORunner

import org.xxxsarutahikoxxx.kotlin.kotlinlibrary.Utilitys.out
import java.io.Serializable

fun main(args: Array<String>) {
    /**
     * デフォルトのポートを使って接続を待つ
     * 接続を受けたらデフォルトでは 'Hello Client!' と送信する
     * クライアントが切断した場合は再び接続待機状態になる
     * */
    object : HostTCPRunner(53456){
        override fun onOpened() {
            super.onOpened()

            writeObject("Hello Server!")
            writeObject("Are you ready?")
        }
        override fun onAccept(obj: Serializable) {
            out = "from client : $obj"
        }
    }.open()
}
package org.xxxsarutahikoxxx.kotlin.SocketRunner

import org.xxxsarutahikoxxx.kotlin.Utilitys.out
import java.io.Serializable

fun main(args: Array<String>) {
    /**
     * デフォルトのポートを使って接続を待つ
     * 接続を受けたらデフォルトでは 'Hello Client!' と送信する
     * クライアントが切断した場合は再び接続待機状態になる
     * */
    HostWebRunner().open()
}
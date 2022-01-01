package org.xxxsarutahikoxxx.kotlin.KotlinLibrary.RMInvocation

import java.lang.reflect.InvocationHandler
import java.lang.reflect.Method
import java.lang.reflect.Proxy

open class RMIClientRegistry() {
    private var nextRequestIndex : Int = 0
        get()  = field ++

    private var reactor : RMIClientReactor? = null

    /** [RMIClientReactor]をセットする */
    fun setReactor(reactor : RMIClientReactor){
        this.reactor = reactor
        reactor.onAccept = { unlock(it.requestIndex, it.ret) }
    }


    /**
     * インデックス・ロックオブジェクト・返り値のマッピング
     * */
    private val requestMap : MutableMap<Int, Pair<Object, Any?>> = mutableMapOf()

    /**
     * [requestIndex]でロックをかけて[rmi]をホストに送信する
     *
     * 受信スレッドがホスト側からの返り値を受信するまで待機してロックが解除されたら返り値を返す
     * */
    @Throws(NoExporterException::class)
    fun lock(requestIndex : Int, rmi : RMInvocation) : Any? {
        if( reactor == null ) throw NoExporterException("RMIClientRegistry の RMIExporter が未設定です")

        val lock = Object()
        requestMap[requestIndex] = lock to null

        reactor!!.exportRMI(rmi)

        synchronized(lock) { lock.wait() }

        return requestMap.remove(requestIndex)!!.second
    }
    /**
     * [requestIndex]に対応した返り値をセットしてロックを解除する
     * */
    fun unlock(requestIndex : Int, ret : Any?){
        val lock = requestMap[requestIndex]!!.first
        requestMap[requestIndex] = lock to ret

        synchronized(lock) { lock.notify() }
    }

    /**
     * [id]にバインディングされた[T]型のリモートインスタンスを取得する
     * */
    inline fun <reified T : Remote> lookup(id : String) : T {
        val clazz = T::class.java
        return Proxy.newProxyInstance(clazz.classLoader, listOf(clazz).toTypedArray(), RemoteHandler(id)) as T
    }

    /**
     * リモート参照の関数実行の実態部分
     *
     * ホストに処理を投げて返り値を待機する
     *  */
    inner class RemoteHandler(val id : String) : InvocationHandler {
        override fun invoke(proxy: Any?, method: Method, args: Array<Any?>?): Any? {
            val rIndex = nextRequestIndex
            val rmi = RMInvocation(rIndex, id, method, args?.toList() ?: listOf())

            return lock(rIndex, rmi)
        }
    }
}

/**
 * [RMIClientRegistry]の内部で[RMInvocation]を送信し、[RetMessage]を受信するためのインターフェース
 *
 * [RMIClientReactor]を継承したクラスは[RetMessage]を受信した際に[onAccept]を呼び出すように実装して下さい
 *
 * また[RMInvocation]を送信する[exportRMI]を実装して下さい
 * */
interface RMIClientReactor {
    var onAccept : ((RetMessage)->(Unit))?

    fun exportRMI(rmi : RMInvocation)
}

class NoExporterException(message : String) : RuntimeException(message)
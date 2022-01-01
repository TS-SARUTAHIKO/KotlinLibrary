package org.xxxsarutahikoxxx.kotlin.kotlinlibrary.RMInvocation


open class RMIHostRegistry(){
    private val reactors : MutableList<RMIHostReactor> = mutableListOf()

    /** [RMIHostReactor]を登録する */
    fun addReactor(reactor : RMIHostReactor){
        reactors.add(reactor)
        reactor.registry = this
    }
    /** [RMIHostReactor]を削除する */
    fun removeReactor(reactor : RMIHostReactor){
        reactors.remove(reactor)
        reactor.registry = null
    }


    /**
     * リモート参照が指し示す実体のマッピング
     *  */
    private val instanceMap : MutableMap<String, Remote> = mutableMapOf()

    /**
     * リモート参照の実態を登録する
     *  */
    operator fun set(id : String, value : Remote){ instanceMap[id] = value }
    /**
     * リモート参照の実態を削除する
     *  */
    fun remove(id : String){ instanceMap.remove(id) }

    /**
     * [value]をバインディングしているIDを返す
     *  */
    fun idOf(value : Remote) : String? {
        return instanceMap.filterValues { it == value }.toList().firstOrNull()?.first
    }


    /** 受信した[RMInvocation]を実行して結果を保存した[RetMessage]を返却する */
    fun execute(rmi : RMInvocation) : RetMessage {
        val receiver = instanceMap[rmi.id]
        val ret = rmi.method.invoke(receiver, *rmi.arguments.toTypedArray())

        return RetMessage(rmi.requestIndex, ret)
    }
}

/**
 * [RMIHostRegistry]の内部で[RMInvocation]を受信し、[RetMessage]を送信するためのインターフェース
 *
 * [RMIHostReactor]を継承したクラスは[RMInvocation]を受信した際に[onAccept]を呼び出すように実装して下さい
 *
 * また[RetMessage]を返信する[exportRetMessage]関数を実装して下さい
 * */
interface RMIHostReactor {
    var registry : RMIHostRegistry?

    fun onAccept(rmi : RMInvocation){
        val ret = registry!!.execute(rmi)
        exportRetMessage(ret)
    }
    fun exportRetMessage(message : RetMessage)
}
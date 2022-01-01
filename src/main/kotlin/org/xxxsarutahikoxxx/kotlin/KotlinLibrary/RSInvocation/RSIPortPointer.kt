package org.xxxsarutahikoxxx.kotlin.KotlinLibrary.RSInvocation

import java.io.Serializable

/**
 * [RSIPort] を継承したインスタンスを指し示す参照子
 *
 * [RSIPort] を一意に特定するためのID管理も行う
 *
 * TODO : 通信している RSIPort は送信・受信の双方で同じIDを持つように設定する必要がある
 * 現状では gp に `Global RSIPort` を設定することで通信しているが追加ポートのIDを一致させる仕組みは実装していない
 * */
class RSIPortPointer(private val id : String) : Serializable {
    /**
     * デシリアライズされる際は代わりに対応する {RSISerializable} を検索して渡す
     * */
    fun readResolve() : Any? {
        return RSIPortPointer.value(id)
    }

    companion object {
        /** <ID, RSISerializable> のマップ */
        private val portMap : MutableMap<RSIPort, String> = mutableMapOf()

        /** [RSIPort] に対応した ID を取得する */
        fun id(port : RSIPort) : String = portMap[port]!!
        /** id に対応した [RSIPort] を取得する */
        fun value(id : String) : RSIPort = portMap.toList().first { it.second == id }.first

        /** [RSIPort] を ID で登録する */
        operator fun set(id : String, value : RSIPort){
            portMap[value] = id
        }
    }
}
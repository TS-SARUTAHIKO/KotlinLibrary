package org.xxxsarutahikoxxx.kotlin.kotlinlibrary.RSInvocation

import java.io.Serializable

/**
 * 送信側・受信側でインスタンスを一意に特定するためのインターフェース
 *
 * 両方で同一の RSIID（RSInvocation ID）を設定すれば一意に扱われる
 * */
interface RSISerializable : Serializable {
    /** シリアライズされる際は代わりに[RSIPointer]を作成して渡す */
    fun writeReplace(): Any? {
        return RSIPointer(RSIPointer.id(this))
    }
    /** RSInvocation ID を設定/取得する。実際は[RSIPointer]に処理を任せている。 */
    var RSIID : String
        get() = RSIPointer.id(this)
        set(value) { RSIPointer[value] = this }
}

/**
* [RSISerializable] を継承したインスタンスを指し示す参照子
*
* [RSISerializable] を一意に特定するためのID管理も行う
* */
class RSIPointer(private val id : String) : Serializable {
    /**
     * デシリアライズされる際は代わりに対応する {RSISerializable} を検索して渡す
     * */
    fun readResolve() : Any? {
        return RSIPointer.value(id)
    }

    companion object {
        /** <ID, RSISerializable> のマップ */
        private val map : MutableMap<RSISerializable, String> = mutableMapOf()

        /** [RSISerializable] に対応した ID を取得する */
        fun id(rsi : RSISerializable) : String = map[rsi]!!
        /** id に対応した [RSISerializable] を取得する */
        fun <T : RSISerializable> value(id : String) : T = map.toList().first { it.second == id }.first as T

        /** [RSISerializable] を ID で登録する */
        operator fun <T : RSISerializable> set(id : String, value : T){
            map[value] = id
        }
    }
}


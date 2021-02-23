package com.xxxsarutahikoxxx.kotlin.Feature

import com.xxxsarutahikoxxx.kotlin.Utilitys.out


/**
 * ブロッキング処理を行うためのインターフェース
 *
 * [Blockable] に対して [key] をセットですることで、設定された [key] かそれ以外で処理を分岐する
 * */
interface Blockable {
    /**
     * [key] でブロックすると共に [func] を行う。
     *
     * ただし既に別の [key] でブロックされている場合は何もしない
     *
     * 返り値は [key] が一致して処理が実行された場合は Pair(true, func().invoke()), それ以外は Pair<false, null> である
     * */
    fun <Key : Any, RET : Any> blocking( key : Key, func : (Key)->(RET) ) : Pair<Boolean, RET?> {
        return when( BlockableManager[this] ){
            null, key.hashCode() -> {
                block(key)
                true to func(key)
            }
            else -> {
                false to null
            }
        }
    }
    /**
     * [func] を行う。
     *
     * ただし既に別の [key] でブロックされている場合は何もしない
     *
     * 返り値は [key] が一致して処理が実行された場合は Pair(true, func().invoke()), それ以外は Pair<false, null> である
     * */
    fun <Key : Any, RET : Any> unblocking(key : Key, func : (Key)->(RET) ) : Pair<Boolean, RET?> {
        return when( BlockableManager[this] ){
            null, key.hashCode() -> {
                true to func(key)
            }
            else -> {
                false to null
            }
        }
    }
    /** [key] でブロックされた状態にする */
    fun <Key : Any> block(key : Key){
        BlockableManager[this] = key
    }
    /** [key] でブロックされた状態を解除する */
    fun <Key : Any> unblock(key : Key){
        if( BlockableManager[this] == key ) BlockableManager.remove(this)
    }
    /** [key] に関わらずブロックされた状態を解除する */
    fun unblocks(){
        BlockableManager.remove(this)
    }
}

private object BlockableManager {
    private val keyMap : MutableMap<Int /* Blockable-Hash */, Int /* Key-Hash */> = mutableMapOf()

    operator fun get(blockable : Blockable) : Int? {
        return keyMap[blockable.hashCode()]
    }
    operator fun set(blockable : Blockable, key : Any){
        keyMap[blockable.hashCode()] = key.hashCode()
    }

    fun remove(blockable : Blockable){
        keyMap.remove(blockable.hashCode())
    }
}


fun main(args: Array<String>) {
    class AAA : Blockable

    val a = AAA()

    //
    a.block("key1") // Block する

    a.blocking("key1"){ out = "value1" } // 処理が実行される
    a.blocking("key2"){ out = "value2" } // 処理は実行されない

    //
    a.unblocks() // ブロックを解除する

    a.unblocking("key3"){ out = "value3" } // 処理が実行される。 unblocking なのでブロックはされない
    a.blocking("key4"){ out = "value4" } // 処理が実行される。 blocking なので "key4" でブロックされる

    a.unblocking("key5"){ out = "value5" } // 処理は実行されない

    //
    val (TRUE, RETURN) = a.blocking("key4"){ "Return" } // 処理が行われた場合の返り値は (true, return)
    val (FALSE, NULL) = a.blocking("key5"){ "Return" }  // 処理が行われなかった場合の返り値は (false, null)
}
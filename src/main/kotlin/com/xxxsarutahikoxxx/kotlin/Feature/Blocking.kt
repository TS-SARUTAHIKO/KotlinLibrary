package com.xxxsarutahikoxxx.kotlin.Feature

import com.xxxsarutahikoxxx.kotlin.Utilitys.out


/**
 * ブロッキング処理を行うためのインターフェース
 *
 * [Blockable] に対して [key] をセットですることで、設定された [key] かそれ以外で処理を分岐する
 *
 * --- 説明 ---
 *
 * Blockable.blocking(key){ // sentence } を実行するとブロッキング状態でないか、[key]によるブロッキング状態である場合のみ // sentence 部分が実行される
 * さらに Blackable は key によるブロッキング状態に移行する
 *
 * Blockable.nonBlocking(key){ // sentence } はブロッキング状態に移行しないことを除き、blocking と同じ処理を行う
 *
 * Blockable.unblock(key) もしくは Blockable.unblocks() によりブロッキングが解除されるまで他の key による処理( blocking(key){}, unBlocking(key){} )は行われない
 * */
interface Blockable {
    var blockingKey : Int?

    /**
     * [key] でブロックすると共に [func] を行う。
     *
     * ただし既に別の [key] でブロックされている場合は何もしない
     *
     * 返り値は [key] が一致して処理が実行された場合は Pair(true, func().invoke()), それ以外は Pair<false, null> である
     * */
    fun <Key : Any, RET : Any> blocking( key : Key, func : (Key)->(RET) ) : Pair<Boolean, RET?> {
        return when( blockingKey ){
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
    fun <Key : Any, RET : Any> nonBlocking(key : Key, func : (Key)->(RET) ) : Pair<Boolean, RET?> {
        return when( blockingKey ){
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
        blockingKey = key.hashCode()
    }
    /** [key] でブロックされた状態を解除する */
    fun <Key : Any> unblock(key : Key){
        if( blockingKey == key.hashCode() ) blockingKey = null
    }
    /** [key] に関わらずブロックされた状態を解除する */
    fun unblocks(){
        blockingKey = null
    }
}



fun main(args: Array<String>) {
    class AAA : Blockable {
        override var blockingKey: Int? = null
    }

    val a = AAA()

    //
    a.block("key1") // Block する

    a.blocking("key1"){ out = "value1" } // 処理が実行される
    a.blocking("key2"){ out = "value2" } // 処理は実行されない

    //
    a.unblocks() // ブロックを解除する

    //
    a.nonBlocking("key3"){ out = "value3" } // 処理が実行される。 nonblocking なのでブロックはされない
    a.blocking("key4"){ out = "value4" } // 処理が実行される。 blocking なので "key4" でブロックされる

    a.nonBlocking("key5"){ out = "value5" } // 処理は実行されない

    //
    val (TRUE, RETURN) = a.blocking("key4"){ "Return" } // 処理が行われた場合の返り値は (true, "Return")
    val (FALSE, NULL) = a.blocking("key5"){ "Return" }  // 処理が行われなかった場合の返り値は (false, null)



    //
    a.unblocks()
    a.blocking(a){ out = "AAAA" }
    a.unblock(a)

    a.nonBlocking("key1"){ out = "BBBB" }

}
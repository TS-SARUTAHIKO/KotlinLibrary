package com.xxxsarutahikoxxx.kotlin.Reterator

import com.xxxsarutahikoxxx.kotlin.Utilitys.MutableValue
import java.io.Serializable


/**
 * リストの操作用インスタンス（Iterator の機能拡張）
 *
 * 実体は Triple<Index, List, Info>
 *
 * - Index : インデックス
 * - List : リスト本体
 * - Info : 情報格納用の領域
 *
 * リストの操作関数
 *
 * - next() : 次に進める
 * - previous() : 前に戻す
 * - next(step : Int) : step 数、次に進める
 * - previous(step : Int) : step 数、前に戻す
 * - first() : 最初に戻す
 * - last() : 最後に移動する
 *
 * リスト関数 その他
 *
 * - nextValue : 次の値
 * - previousValue : 前の値
 * - nextValue(step : Int) : step 数、次の値
 * - previousValue(step : Int) : step 数、前の値
 * - firstValue : 最初の値
 * - lastValue : 最後の値
 *
 * シャッフル関係
 *
 * - shuffle() : ランダム化する
 * - restore() : ランダム化を元に戻す
 * - storeOnShuffle : ランダム化から戻す処理を許可する（shuffle の前に true にする）
 *
 * ループ関係
 *
 * - isLoop : ループ化設定（true かつリストのサイズが2以上の場合、リストをループとして扱う）
 *
 * */
typealias Reterator<Type> = Triple<MutableValue<Int>, MutableList<Type>, MutableMap<String, Any>>

/** Reterator の生成関数 */
val <Type> Iterable<Type>.reterator : Reterator<Type>
    get(){
        return Triple(MutableValue(0), this.toMutableList(), mutableMapOf<String, Any>()).apply {
            storeOnShuffle = true
        }
    }

/** Reterator の現状のインデックス */
var Reterator<*>.index : Int
    get() = first.value
    set(value) { first.value = value }

/** Reterator の現状の値 */
val <Type> Reterator<Type>.value : Type? get() = second[index]

/** Reterator の値のリスト Value */
val <Type> Reterator<Type>.values : List<Type> get() = second

val <Type> Reterator<Type>.size : Int get() = second.size
val <Type> Reterator<Type>.inRange : Boolean get() = index in 0.until(size)

val <Type> Reterator<Type>.isEmpty : Boolean get() = values.isEmpty()

operator fun <Type> Reterator<Type>.contains(obj : Type) = obj in values

// First
val <Type> Reterator<Type>.firstValue : Type? get() = values.getOrNull( 0 )
fun <Type> Reterator<Type>.first() : Type? {
    index = 0
    return value
}

// Last
val <Type> Reterator<Type>.lastValue : Type? get() = values.getOrNull( size-1 )
fun <Type> Reterator<Type>.last() : Type? {
    if( size == 0 ) return null

    index = size-1
    return value
}

//
private fun <Type> Reterator<Type>.previousIndex(step : Int) : Int {
    if( isLoop && (size > 1) ){
        return (((index - step) % size) + size) % size
    }else{
        return index - step
    }
}
private fun <Type> Reterator<Type>.nextIndex(step : Int) : Int {
    if( isLoop && (size > 1) ){
        return (((index + step) % size) + size) % size
    }else{
        return index + step
    }
}

fun <Type> Reterator<Type>.hasPrevious(step : Int = 1) : Boolean {
    return previousIndex(step) in 0.until(size)
}
fun <Type> Reterator<Type>.hasNext(step : Int = 1) : Boolean {
    return nextIndex(step) in 0.until(size)
}

val <Type> Reterator<Type>.hasPrevious : Boolean get() = hasPrevious()
val <Type> Reterator<Type>.hasNext : Boolean get() = hasNext()

fun <Type> Reterator<Type>.previousValue(step : Int = 1) : Type? = values.getOrNull( previousIndex(step) )
fun <Type> Reterator<Type>.nextValue(step : Int = 1) : Type? = values.getOrNull( nextIndex(step) )

val <Type> Reterator<Type>.previousValue : Type? get() = previousValue()
val <Type> Reterator<Type>.nextValue : Type? get() = nextValue()

fun <Type> Reterator<Type>.previous(step : Int = 1) : Type? {
    first.value = previousIndex(step)
    return value
}
fun <Type> Reterator<Type>.next(step : Int = 1) : Type? {
    first.value = nextIndex(step)
    return value
}



// Shuffle 関係
var <Type> Reterator<Type>.isShuffled : Boolean
    get() = third["isShuffled"] as? Boolean ?: false
    private set(value) { third["isShuffled"] = value }

fun <Type> Reterator<Type>.shuffle() : Type? {
    if( storeOnShuffle && ! hasStoredResources ) store()

    val list = second.toMutableList()
    list.shuffle()

    second.clear()
    second.addAll(list)

    isShuffled = true

    return value
}

// Shuffle の際にリソース列を保存するかどうか
var <Type> Reterator<Type>.storeOnShuffle : Boolean
    get() = third["storeOnShuffle"] as? Boolean ?: false
    set(value) {
        third["storeOnShuffle"] = value

        if( ! value ) third.remove("storedResources")
    }

private var <Type> Reterator<Type>.storedResources : List<Type>
    get() = third["storedResources"] as? List<Type> ?: throw RuntimeException("No Stored Resource")
    set(value) { third["storedResources"] = value.toList() }

private val <Type> Reterator<Type>.hasStoredResources : Boolean
    get() = third.containsKey("storedResources")

private fun <Type> Reterator<Type>.store(){
    if( ! hasStoredResources ){
        third["storedResources"] = second.toList()
    }
}

/** リストを[shuffle]していない状態に復元する */
fun <Type> Reterator<Type>.restore() : Boolean {
    if( hasStoredResources  ){
        second.clear()
        second.addAll(storedResources)

        isShuffled = false

        return true
    }

    return false
}


// Loop 関係
var <Type> Reterator<Type>.isLoop : Boolean
    get() = third["isLoop"] as? Boolean ?: false
    set(value) { third["isLoop"] = value }
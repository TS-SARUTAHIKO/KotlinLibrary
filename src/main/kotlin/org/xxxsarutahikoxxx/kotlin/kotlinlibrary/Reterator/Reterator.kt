package org.xxxsarutahikoxxx.kotlin.kotlinlibrary.Reterator


/**
 * リストの操作用インスタンス（Iterator の機能拡張）
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
class Reterator<Type>(
    /** リスト本体 */
    val list : MutableList<Type>,
    /** Reterator の現状のインデックス */
    var index : Int = 0,
    /** 追加情報 */
    val info : MutableMap<String, Any> = mutableMapOf()
)

/** Reterator の生成関数 */
val <Type> Iterable<Type>.reterator : Reterator<Type>
    get(){
        return Reterator<Type>(this.toMutableList()).apply {
            storeOnShuffle = true
        }
    }

/** Reterator の現状の値 */
val <Type> Reterator<Type>.value : Type? get() = list[index]

/** Reterator の値のリスト Value */
val <Type> Reterator<Type>.values : List<Type> get() = list.toList()

val <Type> Reterator<Type>.size : Int get() = list.size
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
    index = previousIndex(step)
    return value
}
fun <Type> Reterator<Type>.next(step : Int = 1) : Type? {
    index = nextIndex(step)
    return value
}



// Shuffle 関係
var <Type> Reterator<Type>.isShuffled : Boolean
    get() = info["isShuffled"] as? Boolean ?: false
    private set(value) { info["isShuffled"] = value }

fun <Type> Reterator<Type>.shuffle() : Type? {
    if( storeOnShuffle && ! hasStoredResources ) store()

    val sub = list.toMutableList()
    sub.shuffle()

    list.clear()
    list.addAll(sub)

    isShuffled = true

    return value
}

// Shuffle の際にリソース列を保存するかどうか
var <Type> Reterator<Type>.storeOnShuffle : Boolean
    get() = info["storeOnShuffle"] as? Boolean ?: false
    set(value) {
        info["storeOnShuffle"] = value

        if( ! value ) info.remove("storedResources")
    }

private var <Type> Reterator<Type>.storedResources : List<Type>
    get() = info["storedResources"] as? List<Type> ?: throw RuntimeException("No Stored Resource")
    set(value) { info["storedResources"] = value.toList() }

private val <Type> Reterator<Type>.hasStoredResources : Boolean
    get() = info.containsKey("storedResources")

private fun <Type> Reterator<Type>.store(){
    if( ! hasStoredResources ){
        info["storedResources"] = list.toList()
    }
}

/** リストを[shuffle]していない状態に復元する */
fun <Type> Reterator<Type>.restore() : Boolean {
    if( hasStoredResources  ){
        list.clear()
        list.addAll(storedResources)

        isShuffled = false

        return true
    }

    return false
}


// Loop 関係
var <Type> Reterator<Type>.isLoop : Boolean
    get() = info["isLoop"] as? Boolean ?: false
    set(value) { info["isLoop"] = value }
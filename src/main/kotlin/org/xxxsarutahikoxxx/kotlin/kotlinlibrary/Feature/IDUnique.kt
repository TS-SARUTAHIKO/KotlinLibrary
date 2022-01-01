package org.xxxsarutahikoxxx.kotlin.kotlinlibrary.Feature

import org.xxxsarutahikoxxx.kotlin.kotlinlibrary.Utilitys.ObjectToObject
import org.xxxsarutahikoxxx.kotlin.kotlinlibrary.Utilitys.out
import java.io.*


/**
 * ID によって一意に特定される Flyweight デザインパターン機能のためのインターフェース
 *
 * 対象のクラスは下記の作例を参考にこれを継承してコンストラクタの代わりにファクトリ関数でインスタンスを作成すること
 * */
interface IDUnique {
    val uniqueID : Any
}
/** [IDUnique]として登録から削除する */
fun IDUnique.delete(){
    IDUniques.delete(this)
}


/**
 * [IDUnique]のリストを管理するオブジェクト
 * */
object IDUniques {
    private val uniques : MutableMap<Class<out IDUnique>, MutableMap<Any, IDUnique>> = mutableMapOf()

    /**
     * 対象のクラス、IDを持つ[IDUnique]オブジェクトが存在するならそれを返し、ないなら作成と登録を行ってから返却する
     * */
    fun <T : IDUnique> getOrPut(clazz : Class<out IDUnique>, uniqueID : Any, factory : ()->(T) ) : T {
        return uniques.getOrPut(clazz){ mutableMapOf() }.getOrPut(uniqueID, factory) as T
    }

    /** 対象のクラス、IDを持つ[IDUnique]オブジェクトが存在するかどうか */
    internal fun exists(clazz : Class<out IDUnique>, uniqueID : Any ) : Boolean {
        return uniques[clazz]?.containsKey(uniqueID) ?: false
    }
    /** 対象のクラス、IDを持つ[IDUnique]オブジェクトを取得する */
    internal fun <T : IDUnique> get(clazz : Class<T>, uniqueID : Any ) : T {
        return uniques[clazz]?.get(uniqueID) as T
    }
    /** [IDUnique]を削除する */
    internal fun delete( obj : IDUnique ){
        uniques[obj::class.java]?.remove(obj)
    }
}


/**
 * [IDUnique]にシリアライズ関係の関数を追加
 *
 * 対象のクラスは下記の作例を参考にこれを継承してコンストラクタの代わりにファクトリ関数でインスタンスを作成すること
 *
 * シリアライズの際は自動的に[IDUniqueSerializablePointer]に変換され、逆変換の際は[IDUniques]から取得される
 *  */
interface IDUniqueSerializable : IDUnique, Serializable {
    fun writeReplace() : Any {
        return IDUniqueSerializablePointer(this::class.java, uniqueID)
    }
}

/** [IDUniqueSerializable]の変換・再生を行うためのポインター */
class IDUniqueSerializablePointer(private val clazz : Class<out IDUniqueSerializable>, private val id : Any) : Serializable {
    private fun readResolve() : Any {
        return IDUniques.get(clazz, id)
    }
}







// コンストラクタの使用は禁止する
private class Test private constructor(override val uniqueID: String) : IDUnique {
    companion object {
        // コンストラクタの代わりにファクトリ関数でインスタンスを作成する
        fun newInstance(id : String) : Test = IDUniques.getOrPut(Test::class.java, id){ Test(id) }
    }
}

// コンストラクタの使用は禁止する
private class SerializableTest private constructor(override val uniqueID: String) : IDUniqueSerializable {
    companion object {
        // コンストラクタの代わりにファクトリ関数でインスタンスを作成する
        fun newInstance(id : String) : SerializableTest = IDUniques.getOrPut(SerializableTest::class.java, id){ SerializableTest(id) }
    }
}

fun main() {
    // 同じuniqueIDを持つなら同じインスタンスである
    val A = Test.newInstance("A")
    out = (A == Test.newInstance("A")) // out = true

    A.delete()

    // 上記に加えてシリアライズに対応している
    // ※ ObjectToObject：シリアライズでバイト列に変換してから元に戻す関数
    val B = SerializableTest.newInstance("B")
    out = (B == SerializableTest.newInstance("B")) // out = true
    out = (B == B.ObjectToObject) // out = true

    B.delete()
}
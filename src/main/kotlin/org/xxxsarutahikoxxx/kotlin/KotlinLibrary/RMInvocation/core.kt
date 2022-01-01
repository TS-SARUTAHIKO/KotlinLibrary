package org.xxxsarutahikoxxx.kotlin.KotlinLibrary.RMInvocation

import java.io.Serializable
import java.lang.reflect.Method


/** リモート参照可能であることを示すインターフェース */
interface Remote

/**
 * クライアント側からのホスト側への関数の実行命令
 * */
class RMInvocation(
    val requestIndex : Int,
    val id : String,
    val arguments : List<Any?>,

    val Clazz : Class<*>,
    val mName : String,
    val mArgs : Array<Class<*>>
) : Serializable {
    constructor(requestIndex : Int, id : String, method : Method, arguments : List<Any?>) :
            this(requestIndex, id, arguments, method.declaringClass, method.name, method.parameterTypes)

    val method : Method get(){
        return Clazz.declaredMethods.first {
            it.name == mName && it.parameterTypes.toList() == mArgs.toList()
        }
    }
}

/**
 * [RMInvocation]を送信した際に[RMIHostRegistry]から返信される結果を表すオブジェクト
 * */
class RetMessage(
    val requestIndex : Int,
    val ret : Any?
) : Serializable


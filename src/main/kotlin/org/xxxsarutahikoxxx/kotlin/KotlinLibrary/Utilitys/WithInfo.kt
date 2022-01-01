package org.xxxsarutahikoxxx.kotlin.KotlinLibrary.Utilitys

import kotlin.reflect.KProperty


/**
 * 任意のデータを後付けで保存するためのインターフェース
 *
 * class A : WithInfo by WithInfoImpl(){}
 *
 * var A.name : String?
 *     get() = _info["name"] as String?
 *     set(value) { _info["name"] = value }
 *  */
interface WithInfo {
    val _info : MutableMap<Any, Any?>

    /** [WithInfo]にデータを保存します */
    operator fun set(key : Any, value : Any?){
        _info[key] = value
    }
    /** [WithInfo]からデータを呼び出します */
    operator fun get(key : Any) : Any? = _info[key]

    operator fun <Type> getValue(host : Any, property : KProperty<*>) : Type? {
        return _info[property.name] as Type?
    }
    operator fun <Type> setValue(host : Any, property : KProperty<*>, value : Type?){
        _info[property.name] = value
    }
}
open class WithInfoImpl : WithInfo {
    override val _info: MutableMap<Any, Any?> = mutableMapOf<Any, Any?>().withDefault { null }
}



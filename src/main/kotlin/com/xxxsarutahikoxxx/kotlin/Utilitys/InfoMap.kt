package com.xxxsarutahikoxxx.kotlin.Utilitys

import java.util.*
import kotlin.reflect.KProperty


/**
 * 任意のデータを後付けで保存するためのインターフェース
 *
 * class A : InfoMap by InfoMapImpl(){}
 *
 * var A.name : String?
 *     get() = _info["name"] as String?
 *     set(value) { _info["name"] = value }
 *  */
interface InfoMap {
    val _info : MutableMap<String, Any?>

    /** [InfoMap]にデータを保存します */
    operator fun set(key : String, value : Any?){
        _info[key] = value
    }
    /** [InfoMap]からデータを呼び出します */
    operator fun get(key : String) : Any? = _info[key]

    operator fun <Type> getValue(host : Any, property : KProperty<*>) : Type? {
        return _info[property.name] as Type?
    }
    operator fun <Type> setValue(host : Any, property : KProperty<*>, value : Type?){
        _info[property.name] = value
    }
}
open class InfoMapImpl : InfoMap {
    override val _info: MutableMap<String, Any?> = mutableMapOf<String, Any?>().withDefault { null }
}


/**  */
fun <RECIEVER, RET> (RECIEVER.()->(RET)).withName(name : String) : RECIEVER.()->(RET) {
    return object : (RECIEVER)->(RET) {
        val name : String = name
        override fun toString(): String = name
        override fun invoke(p1: RECIEVER): RET = this.invoke(p1)
    }
}
fun <RECIEVER, RET> (RECIEVER.()->(RET)).withInfo(name : String, vararg pairs: Pair<String, Any?>) : RECIEVER.()->(RET) {
    return object : (RECIEVER)->(RET), InfoMapImpl() {
        val name : String = name
        override fun toString(): String = name
        override fun invoke(p1: RECIEVER): RET = this.invoke(p1)
    }.apply {
        _info.putAll(pairs)
    }
}

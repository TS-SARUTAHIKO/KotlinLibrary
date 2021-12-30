package org.xxxsarutahikoxxx.kotlin.Utilitys

import java.lang.RuntimeException


/**
 * 関数の分岐実行のためのラッパークラス
 *
 * when( RECEIVER ){ case 1 -> RECEIVER.func1() ; case 2 -> RECEIVER.func2() ; ... } という形を動的に追加可能にする
 *
 * */
interface FunctionBinder<RECEIVER, FUNCTION> {
    var name : String?

    val funcs : MutableList< Pair<(RECEIVER)->(Boolean), FUNCTION> > //= mutableListOf()
    fun clear() = funcs.clear()

    operator fun set( condition : (RECEIVER)->(Boolean), func : FUNCTION ) = funcs.add(condition to func)
    fun putAll( binder : FunctionBinder<RECEIVER, FUNCTION>) = funcs.addAll( binder.funcs )
    operator fun plus( binder : FunctionBinder<RECEIVER, FUNCTION>) = putAll(binder)

    fun of(receiver : RECEIVER) : (FUNCTION)? = funcs.firstOrNull { it.first(receiver) }?.second
}

class FunctionBinder0<RECEIVER, RET> : FunctionBinder<RECEIVER, RECEIVER.()->(RET)>, (RECEIVER)->(RET), WithInfo by WithInfoImpl() {
    override var name : String? by _info
    override val funcs: MutableList<Pair<(RECEIVER) -> Boolean, RECEIVER.() -> RET>> = mutableListOf()

    override fun invoke(receiver: RECEIVER) : RET {
        return when( val func = of(receiver) ){
            null -> throw RuntimeException("$receiver に対応する関数が存在しません BinderName=$name")
            else -> func(receiver)
        }
    }
}
class FunctionBinder1<RECEIVER, ARG1, RET> : FunctionBinder<RECEIVER, RECEIVER.(ARG1)->(RET)>, (RECEIVER, ARG1)->(RET), WithInfo by WithInfoImpl() {
    override var name : String? by _info
    override val funcs: MutableList<Pair<(RECEIVER) -> Boolean, RECEIVER.(ARG1) -> RET>> = mutableListOf()

    override fun invoke(receiver: RECEIVER, arg1 : ARG1) : RET {
        return when( val func = of(receiver) ){
            null -> throw RuntimeException("$receiver に対応する関数が存在しません BinderName=$name")
            else -> func(receiver, arg1)
        }
    }
}
class FunctionBinder2<RECEIVER, ARG1, ARG2, RET> : FunctionBinder<RECEIVER, RECEIVER.(ARG1, ARG2)->(RET)>, (RECEIVER, ARG1, ARG2)->(RET), WithInfo by WithInfoImpl() {
    override var name : String? by _info
    override val funcs: MutableList<Pair<(RECEIVER) -> Boolean, RECEIVER.(ARG1, ARG2) -> RET>> = mutableListOf()

    override fun invoke(receiver: RECEIVER, arg1 : ARG1, arg2 : ARG2) : RET {
        return when( val func = of(receiver) ){
            null -> throw RuntimeException("$receiver に対応する関数が存在しません BinderName=$name")
            else -> func(receiver, arg1, arg2)
        }
    }
}
class FunctionBinder3<RECEIVER, ARG1, ARG2, ARG3, RET> : FunctionBinder<RECEIVER, RECEIVER.(ARG1, ARG2, ARG3)->(RET)>, (RECEIVER, ARG1, ARG2, ARG3)->(RET), WithInfo by WithInfoImpl() {
    override var name : String? by _info
    override val funcs: MutableList<Pair<(RECEIVER) -> Boolean, RECEIVER.(ARG1, ARG2, ARG3) -> RET>> = mutableListOf()

    override fun invoke(receiver: RECEIVER, arg1 : ARG1, arg2 : ARG2, arg3 : ARG3) : RET {
        return when( val func = of(receiver) ){
            null -> throw RuntimeException("$receiver に対応する関数が存在しません BinderName=$name")
            else -> func(receiver, arg1, arg2, arg3)
        }
    }
}
class FunctionBinder4<RECEIVER, ARG1, ARG2, ARG3, ARG4, RET> : FunctionBinder<RECEIVER, RECEIVER.(ARG1, ARG2, ARG3, ARG4)->(RET)>, (RECEIVER, ARG1, ARG2, ARG3, ARG4)->(RET), WithInfo by WithInfoImpl() {
    override var name : String? by _info
    override val funcs: MutableList<Pair<(RECEIVER) -> Boolean, RECEIVER.(ARG1, ARG2, ARG3, ARG4) -> RET>> = mutableListOf()

    override fun invoke(receiver: RECEIVER, arg1 : ARG1, arg2 : ARG2, arg3 : ARG3, arg4 : ARG4) : RET {
        return when( val func = of(receiver) ){
            null -> throw RuntimeException("$receiver に対応する関数が存在しません BinderName=$name")
            else -> func(receiver, arg1, arg2, arg3, arg4)
        }
    }
}
class FunctionBinder5<RECEIVER, ARG1, ARG2, ARG3, ARG4, ARG5, RET> : FunctionBinder<RECEIVER, RECEIVER.(ARG1, ARG2, ARG3, ARG4, ARG5)->(RET)>, (RECEIVER, ARG1, ARG2, ARG3, ARG4, ARG5)->(RET), WithInfo by WithInfoImpl() {
    override var name : String? by _info
    override val funcs: MutableList<Pair<(RECEIVER) -> Boolean, RECEIVER.(ARG1, ARG2, ARG3, ARG4, ARG5) -> RET>> = mutableListOf()

    override fun invoke(receiver: RECEIVER, arg1 : ARG1, arg2 : ARG2, arg3 : ARG3, arg4 : ARG4, arg5 : ARG5) : RET {
        return when( val func = of(receiver) ){
            null -> throw RuntimeException("$receiver に対応する関数が存在しません BinderName=$name")
            else -> func(receiver, arg1, arg2, arg3, arg4, arg5)
        }
    }
}

infix fun <RECEIVER, RET> ((RECEIVER)->(Boolean)).bind( func : RECEIVER.()->(RET) ) : FunctionBinder0<RECEIVER, RET>{
    return FunctionBinder0<RECEIVER, RET>().also { it[this] = func }
}
infix fun <RECEIVER, ARG1, RET> ((RECEIVER)->(Boolean)).bind( func : RECEIVER.(ARG1)->(RET) ) : FunctionBinder1<RECEIVER, ARG1, RET>{
    return FunctionBinder1<RECEIVER, ARG1, RET>().also { it[this] = func }
}
infix fun <RECEIVER, ARG1, ARG2, RET> ((RECEIVER)->(Boolean)).bind( func : RECEIVER.(ARG1, ARG2)->(RET) ) : FunctionBinder2<RECEIVER, ARG1, ARG2, RET>{
    return FunctionBinder2<RECEIVER, ARG1, ARG2, RET>().also { it[this] = func }
}
infix fun <RECEIVER, ARG1, ARG2, ARG3, RET> ((RECEIVER)->(Boolean)).bind( func : RECEIVER.(ARG1, ARG2, ARG3)->(RET) ) : FunctionBinder3<RECEIVER, ARG1, ARG2, ARG3, RET>{
    return FunctionBinder3<RECEIVER, ARG1, ARG2, ARG3, RET>().also { it[this] = func }
}
infix fun <RECEIVER, ARG1, ARG2, ARG3, ARG4, RET> ((RECEIVER)->(Boolean)).bind( func : RECEIVER.(ARG1, ARG2, ARG3, ARG4)->(RET) ) : FunctionBinder4<RECEIVER, ARG1, ARG2, ARG3, ARG4, RET>{
    return FunctionBinder4<RECEIVER, ARG1, ARG2, ARG3, ARG4, RET>().also { it[this] = func }
}
infix fun <RECEIVER, ARG1, ARG2, ARG3, ARG4, ARG5, RET> ((RECEIVER)->(Boolean)).bind( func : RECEIVER.(ARG1, ARG2, ARG3, ARG4, ARG5)->(RET) ) : FunctionBinder5<RECEIVER, ARG1, ARG2, ARG3, ARG4, ARG5, RET>{
    return FunctionBinder5<RECEIVER, ARG1, ARG2, ARG3, ARG4, ARG5, RET>().also { it[this] = func }
}


fun main(args: Array<String>) {
    val binder = { rec : Int -> rec%2 == 0 } bind { -> this*this }
    binder[{ rec : Int -> rec%2==1 }] = { -this*this }

    binder + binder
    val func : ( Int.()->(Int) )? = binder.of(3)

    out = binder(1)
    out = binder(2)
    out = binder(3)

    val f : Int.()->(Boolean) = { true }

    out = f::class
    out = f::class.java
}

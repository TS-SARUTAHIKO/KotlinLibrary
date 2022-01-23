package org.xxxsarutahikoxxx.kotlin.kotlinlibrary.RSInvocation

import java.io.Serializable


/**
 * Host-Client 間で Remote Method Invocation を行うためのデータクラス（の親クラス）
 *
 * 関数・引数・レシーバーを内包しているため [invoke] 命令のみで関数を実行できる
 *
 * 引数は [Serializable] である必要がある
 *
 * ※ レシーバー・ディスパッチレシーバーは [Serializable] のチェックを行わないために実行時エラーがでる可能性がある
 * */
interface RSInvocation<Ret> : Serializable {
    operator fun invoke() : Ret
}

/**
 * [RSInvocation] の実装クラス
 * */
internal class RSInvocationImpl<Ret>(val func : ()->(Ret)) : RSInvocation<Ret> {
    override operator fun invoke() : Ret = func()

    companion object {
        @JvmStatic private val serialVersionUID: Long = 1L
    }
}

/**
 * [KFunction] から [RSInvocation] への変換関数
 * */
fun <Ret> (()->(Ret)).rsi() : RSInvocation<Ret> = RSInvocationImpl(this)

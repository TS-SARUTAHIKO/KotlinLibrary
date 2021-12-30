package org.xxxsarutahikoxxx.kotlin.RSInvocation

import java.io.Serializable


/**
 * Host-Client 間で Remote Method Invocation を行うためのデータクラス（の親クラス）
 *
 * 関数・引数・レシーバーを内包しているため [invoke] 命令のみで関数を実行できる
 *
 * 引数は [Serializable] である必要がある
 *
 * ※ レシーバー・ディスパッチレシーバーは [Serializable] のチェックを行わないために実行時エラーがでる可能性がある
 *
 * 実際に用いるのは引数の数に応じて [RSInvocationImpl] ～ [RMInvocation4] が存在する実装版を用いる
 * */
interface RSInvocation<Ret> : Serializable {
    operator fun invoke() : Ret
}

/**
 * [RSInvocation] の実装クラス
 * */
internal class RSInvocationImpl<Ret>(
    val func : ()->(Ret)
) : RSInvocation<Ret> {
    override operator fun invoke() : Ret = func()

    companion object {
        @JvmStatic private val serialVersionUID: Long = 1L
    }
}


/**
 * [KFunction] から [RSInvocation] への変換関数
 * */
fun <Ret> (()->(Ret)).rsi() : RSInvocation<Ret> = RSInvocationImpl(this)

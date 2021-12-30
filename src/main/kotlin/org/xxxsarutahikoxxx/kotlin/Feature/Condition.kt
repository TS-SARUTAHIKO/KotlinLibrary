package org.xxxsarutahikoxxx.kotlin.Feature

import kotlin.math.abs

/**
 * 判定を行う関数をクラスとして扱うためのエイリアス
 * 今回は数字を扱う判定関数を対象とする
 * */
typealias nCondition = (Number)->(Boolean)


/**
 * 条件オブジェクトの生成＆合成用のクラス
 * ミキサー自体の継承を考慮しないなら、クラスではなく`object`で作成しても良い
 *
 * 直接は用いずにパッケージ関数 mixer{  } 経由で使用する
 *
 * クラスの役割は以下
 * - 汎用の条件オブジェクトを組み込みとして持ち、参照できるようにする
 * - 条件オブジェクトの合成関数を提供する
 * - 条件オブジェクトに対する各種の処理関数の使用範囲を限定することで関数名の衝突を避ける
 * */
class Mixer private constructor(){

    // nCondition の合成用関数
    /** and */
    infix fun nCondition.and(nCon : nCondition) : nCondition {
        return { it : Number -> this@and.invoke(it) && nCon.invoke(it)  }
    }
    /** or */
    infix fun nCondition.or(nCon : nCondition) : nCondition {
        return { it : Number -> this@or.invoke(it) || nCon.invoke(it)  }
    }
    /** not / (!) */
    operator fun nCondition.not() : nCondition {
        return { it : Number -> ! this@not.invoke(it) }
    }



    // 組み込みの条件オブジェクト
    val Plus : nCondition = { it.toDouble() > 0 }
    val Minus : nCondition = { it.toDouble() < 0 }

    val Int : nCondition = { it is Int }
    val Double : nCondition = { it is Double }
    val Float : nCondition = { it is Float }

    val Even : nCondition = Int and { it.toInt() % 2 == 0 }
    val Odd : nCondition = Int and { abs(it.toInt() % 2) == 1 }


    // 条件オブジェクトの生成関数
    val Number.less : nCondition get() = { it : Number -> it.toDouble() < this.toDouble() }
    val Number.more : nCondition get() = { it : Number -> it.toDouble() > this.toDouble() }


    companion object {
        private val host : Mixer = Mixer()

        internal fun nCondition(func : Mixer.()->(nCondition) ) : nCondition = host.func()
    }
}

fun mixer( func : Mixer.()->(nCondition) ) : nCondition {
    return Mixer.nCondition(func)
}


fun main() {
    val list = listOf<Number>(
        -7, -6, -1,
        0,
        1, 2, 5, 12,

        -0.2f, 4.0f, 7.2f, 13.0f,
        -8.3, 6.2, 14.1
    )

    var con : nCondition? = null

    con = mixer { Odd } // 奇数
    con = mixer { Even } // 偶数
    con = mixer { Plus and Int } // 正の整数
    con = mixer { Minus and Double } // 負の Double
    con = mixer { 5.more and 10.less } // 5～10 の数

    con = mixer { Int or Float or Minus } // Int か Float で負の数

    con = mixer { ! Int and 3.more } // Int 以外で 3 以上

    println( list.filter(con) ) // [4.0, 7.2, 13.0, 6.2, 14.1]
}

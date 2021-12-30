package org.xxxsarutahikoxxx.kotlin.Reterator

import org.xxxsarutahikoxxx.kotlin.Utilitys.out

fun main(args: Array<String>) {

    val re = listOf("a", "b", "c", "d").reterator


    // index value により現在のインデックスや現在の値を取得できる
    out = re.index // 0
    out = re.value // a

    // next() が呼ばれるとインデックスが増加し現在の値も変化する
    out = re.next() // b
    out = re.value // b

    // nextValue はインデックスを固定したまま次の値を参照する
    out = re.nextValue // c
    out = re.value // b

    // previous() が呼ばれるとインデックスが減少し現在の値も変化する
    out = re.previous() // a
    out = re.value // a

    // next(step : Int) が呼ばれると指定した数だけインデックスが増加する
    out = re.next(3) // d
    out = re.value // d


    // シャッフル関係
    // shuffle() が呼ばれると配列の順序がランダム化される、インデックスは変化しない
    re.shuffle()
    out = re.values // (example) [ b, d, c, a ]

    // restore() が呼ばれるとランダム化された順序が元に戻る、インデックスは変化しない
    re.restore()
    out = re.values // [a, b, c, d]


    // ループ関係
    // isLoop = true を設定することで配列を循環しているものとして扱う
    re.isLoop = true
}
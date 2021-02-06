package com.xxxsarutahikoxxx.kotlin.ChordStructure

import com.xxxsarutahikoxxx.kotlin.Utilitys.out

fun main(args: Array<String>) {

    // スケールを取得して、音程・トライアド・セブンスコードを取得する

    val scale = MajorScale.C
    out = scale.pitches()
    out = scale.chords3()
    out = scale.chords4()

    // 和音（chord）
    // Chord は根音(root)と根音からの配置情報で構成される (e.g) Cm.root = C, Cm.indexes = [0, 3, 7]
    // 配置情報は ChordIndexer によって管理される。 ChordIndexer は m, sus4, aug などの補正情報である ChordModifier を持つ

    val indexer = indexer(1) { minor + seventh }
    val chord = Pitch.C bind indexer
    out = "${chord.code} : ${chord.pitches}"

    // 文字列を解析して Chord に変換することも可能
    val Cm7 : Chord = "Cm7".chord


    // 条件
    // ChordIndexer, Chord, KeyChord の条件オブジェクト関数にはエイリアスが設定されている　cCondition = (Chord)->(Boolean)
    // cCondition はファクトリ関数 rChord { /* omit */ } により作成と合成を行える (e.g.) rChord { minor and seventh }

    val condition = rChord { minor and seventh and root(Pitch.C) }
    out = condition("Cm7".chord)
    out = condition.chords

    out = rChord { major and triad and bass(Pitch.Aflat) }.chords(true) // 転回したものを検索する場合は inversion = true を使用する

}
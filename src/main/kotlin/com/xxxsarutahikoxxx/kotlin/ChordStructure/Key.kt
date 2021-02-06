package com.xxxsarutahikoxxx.kotlin.ChordStructure

import kotlin.math.abs


interface Key {
    val octave : Int
    val pitch : Pitch
    val index : Int
//    val Frequency : Double

    /**
     * 音程(周波数)の大小関係を比較します
     * */
    operator fun compareTo(key : Key) : Int = this.index - key.index
    /**
     * [key]との関係がトライトーン(全半音)であるかを判定します
     * */
    infix fun Tritone(key : Key) : Boolean = (abs(this.compareTo(key)) == 6)

    /**
     * [key]に対するこの[Key]の音程を半音の数で返します
     * */
    operator fun minus(key : Key) : Int = this.index - key.index
    /**
     * [index]を[num]だけ増加させた[Key]を取得する
     * */
    operator fun plus(num : Int) : Key = KeyImpl.of(index + num)
}
internal class KeyImpl private constructor(
        override val octave: Int,
        override val pitch: Pitch
) : Key {
    override val index: Int = 12*octave + pitch.ordinal

    init {
        keyList.add(this)
    }

    companion object {
        private val keyList : MutableList<Key> = mutableListOf()
        fun of(octave : Int, pitch : Pitch) : Key = keyList.first { it.octave == octave && it.pitch == pitch }
        fun of(index : Int) = keyList.first { it.index == index }

        val C0 = KeyImpl(0, Pitch.C)
        val Dflat0 = KeyImpl(0, Pitch.Dflat)
        val D0 = KeyImpl(0, Pitch.D)
        val Eflat0 = KeyImpl(0, Pitch.Eflat)
        val E0 = KeyImpl(0, Pitch.E)
        val F0 = KeyImpl(0, Pitch.F)
        val Gflat0 = KeyImpl(0, Pitch.Gflat)
        val G0 = KeyImpl(0, Pitch.G)
        val Aflat0 = KeyImpl(0, Pitch.Aflat)
        val A0 = KeyImpl(0, Pitch.A)
        val Bflat0 = KeyImpl(0, Pitch.Bflat)
        val B0 = KeyImpl(0, Pitch.B)
        val C1 = KeyImpl(1, Pitch.C)
        val Dflat1 = KeyImpl(1, Pitch.Dflat)
        val D1 = KeyImpl(1, Pitch.D)
        val Eflat1 = KeyImpl(1, Pitch.Eflat)
        val E1 = KeyImpl(1, Pitch.E)
        val F1 = KeyImpl(1, Pitch.F)
        val Gflat1 = KeyImpl(1, Pitch.Gflat)
        val G1 = KeyImpl(1, Pitch.G)
        val Aflat1 = KeyImpl(1, Pitch.Aflat)
        val A1 = KeyImpl(1, Pitch.A)
        val Bflat1 = KeyImpl(1, Pitch.Bflat)
        val B1 = KeyImpl(1, Pitch.B)
        val C2 = KeyImpl(2, Pitch.C)
        val Dflat2 = KeyImpl(2, Pitch.Dflat)
        val D2 = KeyImpl(2, Pitch.D)
        val Eflat2 = KeyImpl(2, Pitch.Eflat)
        val E2 = KeyImpl(2, Pitch.E)
        val F2 = KeyImpl(2, Pitch.F)
        val Gflat2 = KeyImpl(2, Pitch.Gflat)
        val G2 = KeyImpl(2, Pitch.G)
        val Aflat2 = KeyImpl(2, Pitch.Aflat)
        val A2 = KeyImpl(2, Pitch.A)
        val Bflat2 = KeyImpl(2, Pitch.Bflat)
        val B2 = KeyImpl(2, Pitch.B)
        val C3 = KeyImpl(3, Pitch.C)
        val Dflat3 = KeyImpl(3, Pitch.Dflat)
        val D3 = KeyImpl(3, Pitch.D)
        val Eflat3 = KeyImpl(3, Pitch.Eflat)
        val E3 = KeyImpl(3, Pitch.E)
        val F3 = KeyImpl(3, Pitch.F)
        val Gflat3 = KeyImpl(3, Pitch.Gflat)
        val G3 = KeyImpl(3, Pitch.G)
        val Aflat3 = KeyImpl(3, Pitch.Aflat)
        val A3 = KeyImpl(3, Pitch.A)
        val Bflat3 = KeyImpl(3, Pitch.Bflat)
        val B3 = KeyImpl(3, Pitch.B)
        val C4 = KeyImpl(4, Pitch.C)
        val Dflat4 = KeyImpl(4, Pitch.Dflat)
        val D4 = KeyImpl(4, Pitch.D)
        val Eflat4 = KeyImpl(4, Pitch.Eflat)
        val E4 = KeyImpl(4, Pitch.E)
        val F4 = KeyImpl(4, Pitch.F)
        val Gflat4 = KeyImpl(4, Pitch.Gflat)
        val G4 = KeyImpl(4, Pitch.G)
        val Aflat4 = KeyImpl(4, Pitch.Aflat)
        val A4 = KeyImpl(4, Pitch.A)
        val Bflat4 = KeyImpl(4, Pitch.Bflat)
        val B4 = KeyImpl(4, Pitch.B)
        val C5 = KeyImpl(5, Pitch.C)
        val Dflat5 = KeyImpl(5, Pitch.Dflat)
        val D5 = KeyImpl(5, Pitch.D)
        val Eflat5 = KeyImpl(5, Pitch.Eflat)
        val E5 = KeyImpl(5, Pitch.E)
        val F5 = KeyImpl(5, Pitch.F)
        val Gflat5 = KeyImpl(5, Pitch.Gflat)
        val G5 = KeyImpl(5, Pitch.G)
        val Aflat5 = KeyImpl(5, Pitch.Aflat)
        val A5 = KeyImpl(5, Pitch.A)
        val Bflat5 = KeyImpl(5, Pitch.Bflat)
        val B5 = KeyImpl(5, Pitch.B)
        val C6 = KeyImpl(6, Pitch.C)
        val Dflat6 = KeyImpl(6, Pitch.Dflat)
        val D6 = KeyImpl(6, Pitch.D)
        val Eflat6 = KeyImpl(6, Pitch.Eflat)
        val E6 = KeyImpl(6, Pitch.E)
        val F6 = KeyImpl(6, Pitch.F)
        val Gflat6 = KeyImpl(6, Pitch.Gflat)
        val G6 = KeyImpl(6, Pitch.G)
        val Aflat6 = KeyImpl(6, Pitch.Aflat)
        val A6 = KeyImpl(6, Pitch.A)
        val Bflat6 = KeyImpl(6, Pitch.Bflat)
        val B6 = KeyImpl(6, Pitch.B)
        val C7 = KeyImpl(7, Pitch.C)
        val Dflat7 = KeyImpl(7, Pitch.Dflat)
        val D7 = KeyImpl(7, Pitch.D)
        val Eflat7 = KeyImpl(7, Pitch.Eflat)
        val E7 = KeyImpl(7, Pitch.E)
        val F7 = KeyImpl(7, Pitch.F)
        val Gflat7 = KeyImpl(7, Pitch.Gflat)
        val G7 = KeyImpl(7, Pitch.G)
        val Aflat7 = KeyImpl(7, Pitch.Aflat)
        val A7 = KeyImpl(7, Pitch.A)
        val Bflat7 = KeyImpl(7, Pitch.Bflat)
        val B7 = KeyImpl(7, Pitch.B)
    }
}


interface KeyChord : Chord {
    /** 音のリスト */
    val keys : List<Key>
    /** バス音 */
    val bassKey : Key
}
internal class KeyChordImpl internal constructor(
    octave : Int,
    chord : Chord
) : KeyChord, Chord by chord {
    override val keys: List<Key> = {
        val key = root.key(octave)
        indexer.indexes.map { key + it }
    }.invoke()

    override val bassKey: Key = keys.minBy { it.index }!!
}

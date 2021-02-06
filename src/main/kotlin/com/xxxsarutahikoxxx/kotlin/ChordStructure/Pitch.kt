package com.xxxsarutahikoxxx.kotlin.ChordStructure

import java.lang.RuntimeException


/** 音名 */
enum class Pitch(vararg val codes : String) {
    C("C"),
    Dflat("C#", "D♭"),
    D("D"),
    Eflat("D#", "E♭"),
    E("E"),
    F("F"),
    Gflat("F#", "G♭"),
    G("G"),
    Aflat("G#", "A♭"),
    A("A"),
    Bflat("A#", "B♭"),
    B("B")
    ;

    val code : String get() = if( flatOrSharp && codes.size >= 2 ) codes[1] else codes[0]
    override fun toString(): String = code

    /** [octave]を用いて対応する[Key]に変換する */
    fun key(octave : Int = 4) : Key = KeyImpl.of(octave, this)

    /** 半音を[num]個だけ増加させた[Pitch]を返す */
    operator fun plus(num : Int) : Pitch {
        val value = (ordinal + num)%12 .run { if(this<0) this+12 else this }
        return Pitch.values().first { it.ordinal == value }
    }
    /** 半音を[num]個だけ減少させた[Pitch]を返す */
    operator fun minus(num : Int) : Pitch {
        val value = (ordinal - num)%12 .run { if(this<0) this+12 else this }
        return Pitch.values().first { it.ordinal == value }
    }

    /** [pitch]を基準にこの音の高さを半音の数で返します */
    operator fun minus(pitch : Pitch) : Int {
        return (this.ordinal - pitch.ordinal).run { if(this<0) this+12 else this }
    }

    companion object {
        val CSharp = Dflat
        val DSharp = Eflat
        val FSharp = Gflat
        val GSharp = Aflat
        val ASharp = Bflat

        fun of(code : String) : Pitch = values().first { code in it.codes }
        fun of(ordinal : Int) : Pitch {
            val ordinal = ordinal % 12
            return values().first { ordinal == it.ordinal }
        }

        var flatOrSharp = true
    }
}

/**
 * [Pitch]のリストを[Pitch.ordinal]の昇順である配列パターンに変換する
 *
 * つまり値が減少する場合は +12 することで昇順を維持するように補正する
 * */
val List<Pitch>.ordinals : List<Int> get(){
    var ret : MutableList<Int> = this.map { it.ordinal }.toMutableList()

    while( true ){
        var finish = true
        for(index in 1.until(ret.size)){
            if( ret[index-1] >= ret[index] ){
                ret[index] += 12
                finish = false
            }
        }
        if( finish ) break
    }

    return ret
}
/**
 * [Pitch] 配列に [Pitch] を挿入する
 * */
fun List<Pitch>.insert(pitch : Pitch) : List<Pitch> {
    val ordinals = this.ordinals
    var ordinal = pitch.ordinal

    while( true ){
        val pre = ordinals.indexOfLast { it < ordinal }
        val post = ordinals.indexOfFirst { ordinal < it }

        when {
            // 挿入ポイントが見つかった場合
            pre+1 == post && pre != -1 && post != -1 -> {
                return ordinals.toMutableList().apply { add(post, ordinal) }.map { Pitch.of(it) }
            }
            // 末尾挿入の場合
            pre == this.size-1 -> {
                return ordinals.toMutableList().apply { add(this.size, ordinal) }.map { Pitch.of(it) }
            }
        }

        ordinal += 12

        if( ordinal >= 1000 ) throw RuntimeException("無限ループが発生 $this : $pitch")
    }
}
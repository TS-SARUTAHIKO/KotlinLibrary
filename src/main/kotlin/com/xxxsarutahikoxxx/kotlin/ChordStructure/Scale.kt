package com.xxxsarutahikoxxx.kotlin.ChordStructure


/** [Scale]におけるインデックス */
enum class Degree(val code : String) {
    D1("Ⅰ"), D2("Ⅱ"), D3("Ⅲ"), D4("Ⅳ"), D5("Ⅴ"), D6("Ⅵ"), D7("Ⅶ");

    /** スケールにおけるインデックスを[num]だけ増加させた[Degree]を返す */
    operator fun plus(num : Int) : Degree {
        val value = (this.ordinal + num)%7 .run { if(this<0) this+7 else this }
        return Degree.values().first { it.ordinal == value }
    }
}

/** スケール */
interface Scale {
    val code : String
    val tonic : Pitch

    val _pitches : Pair<List<Pitch>, List<Pitch>>
    val _chords3 : Pair<List<Chord>, List<Chord>>
    val _chords4 : Pair<List<Chord>, List<Chord>>

    fun pitches(incline: Boolean = true) = if(incline) _pitches.first else _pitches.second
    fun chords3(incline: Boolean = true) = if(incline) _chords3.first else _chords3.second
    fun chords4(incline: Boolean = true) = if(incline) _chords4.first else _chords4.second

    fun pitch(degree : Degree, incline : Boolean = true) = pitches(incline)[degree.ordinal]
    fun chord3(degree : Degree, incline : Boolean = true) = chords3(incline)[degree.ordinal]
    fun chord4(degree : Degree, incline : Boolean = true) = chords4(incline)[degree.ordinal]

    fun bind(chord : DegreeChord) : Chord = ChordImpl(pitch(chord.degree), chord.indexer)

    fun degree(chord : Chord, incline : Boolean? = null) : Degree? {
        fun cross(chords : List<List<Chord>>) : Map<Degree, List<Chord>> {
            return 0.until(Degree.values().size).associate {
                val index = it
                Degree.values()[index] to chords.map { it[index] }
            }
        }

        val map = cross(when( incline ){
            true  -> listOf(chords3(true), chords4(true))
            false -> listOf(chords3(false), chords4(false))
            null  -> listOf(chords3(true), chords4(true), chords3(false), chords4(false))
        })

        map.forEach {
            if( chord.code in it.value.map { it.code } ) return it.key
        }

        return null
    }
    fun degree(pitch : Pitch, incline: Boolean = true) : Degree? {
        return pitches(incline).zip(Degree.values()).toMap()[pitch]
    }

    /**
     * [chord]の構成音が全てこのスケールに所属しているかチェックします
     *
     * [incline] == true, 上昇に所属している
     * [incline] == false, 下降に所属している
     * [incline] == null, 上昇/下降のどちらかに所属している
     * */
    fun contains(chord : Chord, incline : Boolean? = null) : Boolean {
        return when(incline){
            true -> (chord.pitches - pitches(true)).isEmpty()
            false -> (chord.pitches - pitches(false)).isEmpty()
            null -> (chord.pitches - pitches(true)).isEmpty() || (chord.pitches - pitches(false)).isEmpty()
        }
    }
    /**
     * [chord]の構成音が全てこのスケールの上昇/下降のどちらかに所属しているかチェックする
     * */
    operator fun contains(chord : Chord) : Boolean = contains(chord, null)
}
internal fun Scale.createChord3(degree: Degree, incline : Boolean) : Chord {
    var list = listOf(
        0,
        pitch(degree+2, incline) - pitch(degree+0, incline),
        pitch(degree+4, incline) - pitch(degree+2, incline)
    )
    list = 0.until(list.size).map { list.subList(0, it+1).sum() }

    val indexer = ChordIndexerImpl.of(list, rIndexer { noOmit })

    return ChordImpl(pitch(degree), indexer)
}
internal fun Scale.createChord4(degree: Degree, incline : Boolean) : Chord {
    var list = listOf(
        0,
        pitch(degree+2, incline) - pitch(degree+0, incline),
        pitch(degree+4, incline) - pitch(degree+2, incline),
        pitch(degree+6, incline) - pitch(degree+4, incline)
    )
    list = 0.until(list.size).map { list.subList(0, it+1).sum() }

    val indexer = ChordIndexerImpl.of(list, rIndexer { noOmit and seventh })

    return ChordImpl(pitch(degree), indexer)
}


/** メジャー・スケール */
class MajorScale private constructor(
    override val tonic: Pitch
) : Scale {
    override val code: String = "$tonic Major"
    override val _pitches: Pair<List<Pitch>, List<Pitch>> = listOf(tonic, tonic+2, tonic+4, tonic+5, tonic+7, tonic+9, tonic+11).run { this to this }
    override val _chords3: Pair<List<Chord>, List<Chord>> by lazy {
        val list = listOf(Degree.D1, Degree.D2, Degree.D3, Degree.D4, Degree.D5, Degree.D6, Degree.D7)
        list.map { createChord3(it, true) } to list.map { createChord3(it, false) }
    }
    override val _chords4: Pair<List<Chord>, List<Chord>> by lazy {
        val list = listOf(Degree.D1, Degree.D2, Degree.D3, Degree.D4, Degree.D5, Degree.D6, Degree.D7)
        list.map { createChord4(it, true) } to list.map { createChord4(it, false) }
    }

    companion object {
        val C = MajorScale(Pitch.C)
        val Dflat = MajorScale(Pitch.Dflat)
        val D = MajorScale(Pitch.D)
        val Eflat = MajorScale(Pitch.Eflat)
        val E = MajorScale(Pitch.E)
        val F = MajorScale(Pitch.F)
        val Gflat = MajorScale(Pitch.Gflat)
        val G = MajorScale(Pitch.G)
        val Aflat = MajorScale(Pitch.Aflat)
        val A = MajorScale(Pitch.A)
        val Bflat = MajorScale(Pitch.Bflat)
        val B = MajorScale(Pitch.B)

        val CSharp = Dflat
        val DSharp = Eflat
        val FSharp = Gflat
        val GSharp = Aflat
        val ASharp = Bflat
    }
}
/** マイナー・スケール */
class MinorScale private constructor(
    override val tonic: Pitch
) : Scale {
    override val code: String = "$tonic Minor"
    override val _pitches: Pair<List<Pitch>, List<Pitch>> = listOf(tonic, tonic+2, tonic+3, tonic+5, tonic+7, tonic+8, tonic+10).run { this to this }
    override val _chords3: Pair<List<Chord>, List<Chord>> by lazy {
        val list = listOf(Degree.D1, Degree.D2, Degree.D3, Degree.D4, Degree.D5, Degree.D6, Degree.D7)
        list.map { createChord3(it, true) } to list.map { createChord3(it, false) }
    }
    override val _chords4: Pair<List<Chord>, List<Chord>> by lazy {
        val list = listOf(Degree.D1, Degree.D2, Degree.D3, Degree.D4, Degree.D5, Degree.D6, Degree.D7)
        list.map { createChord4(it, true) } to list.map { createChord4(it, false) }
    }

    companion object {
        val C = MinorScale(Pitch.C)
        val Dflat = MinorScale(Pitch.Dflat)
        val D = MinorScale(Pitch.D)
        val Eflat = MinorScale(Pitch.Eflat)
        val E = MinorScale(Pitch.E)
        val F = MinorScale(Pitch.F)
        val Gflat = MinorScale(Pitch.Gflat)
        val G = MinorScale(Pitch.G)
        val Aflat = MinorScale(Pitch.Aflat)
        val A = MinorScale(Pitch.A)
        val Bflat = MinorScale(Pitch.Bflat)
        val B = MinorScale(Pitch.B)

        val CSharp = Dflat
        val DSharp = Eflat
        val FSharp = Gflat
        val GSharp = Aflat
        val ASharp = Bflat
    }
}
/** ハーモニック・マイナー・スケール */
class HarmonicMinorScale private constructor(
    override val tonic: Pitch
) : Scale {
    override val code: String = "$tonic Harmonic Minor"
    override val _pitches: Pair<List<Pitch>, List<Pitch>> = listOf(tonic, tonic+2, tonic+3, tonic+5, tonic+7, tonic+8, tonic+11).run { this to this }
    override val _chords3: Pair<List<Chord>, List<Chord>> by lazy {
        val list = listOf(Degree.D1, Degree.D2, Degree.D3, Degree.D4, Degree.D5, Degree.D6, Degree.D7)
        list.map { createChord3(it, true) } to list.map { createChord3(it, false) }
    }
    override val _chords4: Pair<List<Chord>, List<Chord>> by lazy {
        val list = listOf(Degree.D1, Degree.D2, Degree.D3, Degree.D4, Degree.D5, Degree.D6, Degree.D7)
        list.map { createChord4(it, true) } to list.map { createChord4(it, false) }
    }

    companion object {
        val C = HarmonicMinorScale(Pitch.C)
        val Dflat = HarmonicMinorScale(Pitch.Dflat)
        val D = HarmonicMinorScale(Pitch.D)
        val Eflat = HarmonicMinorScale(Pitch.Eflat)
        val E = HarmonicMinorScale(Pitch.E)
        val F = HarmonicMinorScale(Pitch.F)
        val Gflat = HarmonicMinorScale(Pitch.Gflat)
        val G = HarmonicMinorScale(Pitch.G)
        val Aflat = HarmonicMinorScale(Pitch.Aflat)
        val A = HarmonicMinorScale(Pitch.A)
        val Bflat = HarmonicMinorScale(Pitch.Bflat)
        val B = HarmonicMinorScale(Pitch.B)

        val CSharp = Dflat
        val DSharp = Eflat
        val FSharp = Gflat
        val GSharp = Aflat
        val ASharp = Bflat
    }
}
/** メロディック・マイナー・スケール */
class MelodicMinorScale private constructor(
    override val tonic: Pitch
) : Scale {
    override val code: String = "$tonic Melodic Minor"
    override val _pitches: Pair<List<Pitch>, List<Pitch>> = listOf(tonic, tonic+2, tonic+3, tonic+5, tonic+7, tonic+9, tonic+11) to listOf(tonic, tonic+2, tonic+3, tonic+5, tonic+7, tonic+8, tonic+10)
    override val _chords3: Pair<List<Chord>, List<Chord>> by lazy {
        val list = listOf(Degree.D1, Degree.D2, Degree.D3, Degree.D4, Degree.D5, Degree.D6, Degree.D7)
        list.map { createChord3(it, true) } to list.map { createChord3(it, false) }
    }
    override val _chords4: Pair<List<Chord>, List<Chord>> by lazy {
        val list = listOf(Degree.D1, Degree.D2, Degree.D3, Degree.D4, Degree.D5, Degree.D6, Degree.D7)
        list.map { createChord4(it, true) } to list.map { createChord4(it, false) }
    }

    companion object {
        val C = MelodicMinorScale(Pitch.C)
        val Dflat = MelodicMinorScale(Pitch.Dflat)
        val D = MelodicMinorScale(Pitch.D)
        val Eflat = MelodicMinorScale(Pitch.Eflat)
        val E = MelodicMinorScale(Pitch.E)
        val F = MelodicMinorScale(Pitch.F)
        val Gflat = MelodicMinorScale(Pitch.Gflat)
        val G = MelodicMinorScale(Pitch.G)
        val Aflat = MelodicMinorScale(Pitch.Aflat)
        val A = MelodicMinorScale(Pitch.A)
        val Bflat = MelodicMinorScale(Pitch.Bflat)
        val B = MelodicMinorScale(Pitch.B)

        val CSharp = Dflat
        val DSharp = Eflat
        val FSharp = Gflat
        val GSharp = Aflat
        val ASharp = Bflat
    }
}

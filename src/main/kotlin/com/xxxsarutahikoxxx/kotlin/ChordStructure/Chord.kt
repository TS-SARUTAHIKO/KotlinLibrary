package com.xxxsarutahikoxxx.kotlin.ChordStructure

import java.lang.RuntimeException


enum class CIndex(val value : Int) {
    ROOT(0),
    SECOND(2),
    THIRD(4),
    FOURTH(5),
    FIFTH(7),
    SIXTH(9),
    SEVENTH(10),
    NINTH(14),
    ELEVENTH(18),
    THIRTEENTH(21)
}

/**
 * 一つの音に対する補正情報
 * */
sealed class ChordModifierPart : Comparable<ChordModifierPart> {
    override fun toString(): String = code

    abstract val code : String
    abstract val sortIndex : Double
    abstract fun invoke(map : MutableMap<CIndex, Int>) : Unit

    override fun compareTo(other: ChordModifierPart): Int = ((this.sortIndex - other.sortIndex)*10).toInt()


    class ChordModifierAddition(
        val index: CIndex
    ) : ChordModifierPart()  {
        override val code: String = "add : $index"
        override val sortIndex: Double = (0.0*100 + index.value)
        override fun invoke(map: MutableMap<CIndex, Int>) {
            map.putIfAbsent(index, 0)
        }
    }
    class ChordModifierOmit(
        val index: CIndex
    ) : ChordModifierPart() {
        override val code: String = "omit : $index"
        override val sortIndex: Double = (1.0*100 + index.value)
        override fun invoke(map: MutableMap<CIndex, Int>) {
            map.remove(index)
        }
    }
    class ChordModifierShift(
            val index: CIndex,
            val incline : Boolean
    ) : ChordModifierPart() {
        override val code: String = "shift : $incline"
        override val sortIndex: Double = (2.0*100 + index.value + if(incline)0.1 else 0.2)
        override fun invoke(map: MutableMap<CIndex, Int>) {
            map[index] = if(incline) 1 else -1
        }
    }
    class ChordModifierDim() : ChordModifierPart() {
        override val code: String = "dim"
        override val sortIndex: Double = (3.0*100)
        override fun invoke(map: MutableMap<CIndex, Int>) {
            map.keys.forEach { if( it != CIndex.ROOT ) map[it] = -1 }
        }
    }
}

/**
 * [ChordModifierPart]を複数もつ要素
 *
 * dim のような複合要素のためのクラス
 * */
class ChordModifier private constructor(
    val codes: List<String>,
    val parts: List<ChordModifierPart>,
    val branch: Boolean = false
){
    init {
        modifierList.add(this)
    }

    override fun toString(): String = codes.first()

    companion object {
        private val modifierList : MutableList<ChordModifier> = mutableListOf()
        fun of(code : String) : ChordModifier = modifierList.first { code in it.codes }

        @Throws(ModifierAnalyzeError::class)
        internal fun analyze(code : String) : List<ChordModifier> {
            val code = code. remove(" ", "(", ")")

            var rest = code
            val ret = mutableListOf<ChordModifier>()

            while( rest.isNotEmpty() ){
                // すでに主となる[ChordModifier]が存在するか
                val hasBranch = ret.any { it.branch }

                // [root] 以外、既に Branch があるなら Branch は対象から外す
                val mod = modifierList.firstOrNull { it != root && !(hasBranch && it.branch) && it.codes.any { rest.startsWith(it) } }
                        ?: throw ModifierAnalyzeError("$code の変換に失敗しました。残りの文字列は $rest です。")
                ret.add(mod)

                rest = mod.codes.first { rest.startsWith(it) }.run { rest.substring(this.length, rest.length) }
            }

            return ret
        }

        internal fun addition(index : CIndex, vararg codes : String) = ChordModifier(codes.toList(), listOf( ChordModifierPart.ChordModifierAddition(index) ))
        internal fun omit(index : CIndex, vararg codes : String) = ChordModifier(codes.toList(), listOf( ChordModifierPart.ChordModifierOmit(index) ))
        internal fun sharp(index : CIndex, vararg codes : String) = ChordModifier(codes.toList(), listOf( ChordModifierPart.ChordModifierShift(index, true) ))
        internal fun flat(index : CIndex, vararg codes : String) = ChordModifier(codes.toList(), listOf( ChordModifierPart.ChordModifierShift(index, false) ))
        internal fun addSharp(index : CIndex, vararg codes : String) = ChordModifier(codes.toList(), listOf( ChordModifierPart.ChordModifierAddition(index), ChordModifierPart.ChordModifierShift(index, true)  ))
        internal fun addFlat(index : CIndex, vararg codes : String) = ChordModifier(codes.toList(), listOf( ChordModifierPart.ChordModifierAddition(index), ChordModifierPart.ChordModifierShift(index, false) ))


        internal val root : ChordModifier = ChordModifier(listOf(""), listOf( ChordModifierPart.ChordModifierAddition(CIndex.ROOT), ChordModifierPart.ChordModifierAddition(CIndex.THIRD), ChordModifierPart.ChordModifierAddition(CIndex.FIFTH) ))

        val sus2 : ChordModifier = addFlat(CIndex.THIRD, "sus2")
        val sus4 : ChordModifier = addSharp(CIndex.THIRD, "sus4")
        val minor : ChordModifier = addFlat(CIndex.THIRD, "m")
        val major : ChordModifier = addSharp(CIndex.SEVENTH, "M", "△")
        val aug : ChordModifier = addSharp(CIndex.FIFTH, "aug")

        val flat5 : ChordModifier = addFlat(CIndex.FIFTH, "-5", "♭5")
        val sharp5 : ChordModifier = addSharp(CIndex.FIFTH, "+5", "#5")

        val sixth : ChordModifier = ChordModifier(listOf("6"), listOf( ChordModifierPart.ChordModifierAddition(CIndex.SIXTH)), true)
        val seventh : ChordModifier = ChordModifier(listOf("7"), listOf( ChordModifierPart.ChordModifierAddition(CIndex.SEVENTH)), true)
        val ninth : ChordModifier = ChordModifier(listOf("9"), listOf( ChordModifierPart.ChordModifierAddition(CIndex.SEVENTH), ChordModifierPart.ChordModifierAddition(CIndex.NINTH) ), true)
        val addition9 : ChordModifier = addition(CIndex.NINTH, "9")
        val flat9 : ChordModifier = addFlat(CIndex.NINTH, "-9", "♭9")
        val sharp9 : ChordModifier = addSharp(CIndex.NINTH, "+9", "#9")
        val eleventh : ChordModifier = ChordModifier(listOf("11"), listOf( ChordModifierPart.ChordModifierAddition(CIndex.SEVENTH), ChordModifierPart.ChordModifierAddition(CIndex.NINTH), ChordModifierPart.ChordModifierAddition(CIndex.ELEVENTH) ), true)
        val addition11 : ChordModifier = addition(CIndex.ELEVENTH, "11")
        val flat11 : ChordModifier = addFlat(CIndex.ELEVENTH, "-11", "♭11")
        val sharp11 : ChordModifier = addSharp(CIndex.ELEVENTH, "+11", "#11")
        val thirteenth : ChordModifier = ChordModifier(listOf("13"), listOf( ChordModifierPart.ChordModifierAddition(CIndex.SEVENTH), ChordModifierPart.ChordModifierAddition(CIndex.NINTH), ChordModifierPart.ChordModifierAddition(CIndex.ELEVENTH), ChordModifierPart.ChordModifierAddition(CIndex.THIRTEENTH) ), true)
        val addition13 : ChordModifier = addition(CIndex.THIRTEENTH, "13")
        val flat13 : ChordModifier = addFlat(CIndex.THIRTEENTH, "-13", "♭13")
        val sharp13 : ChordModifier = addSharp(CIndex.THIRTEENTH, "+13", "#13")

        val add2 : ChordModifier = addition(CIndex.SECOND, "add2")
        val add4 : ChordModifier = addition(CIndex.FOURTH, "add4")
        val add9 : ChordModifier = addition(CIndex.NINTH, "add9")

        val dim : ChordModifier = ChordModifier(listOf("dim"), listOf( ChordModifierPart.ChordModifierDim() ))

        val omitRoot : ChordModifier = ChordModifier(listOf("omitRoot", "omit root"), listOf( ChordModifierPart.ChordModifierOmit(CIndex.ROOT) ))
        val omit3 : ChordModifier = ChordModifier(listOf("omit3", "omit 3"), listOf( ChordModifierPart.ChordModifierOmit(CIndex.THIRD) ))
        val omit5 : ChordModifier = ChordModifier(listOf("omit5", "omit 5"), listOf( ChordModifierPart.ChordModifierOmit(CIndex.FIFTH) ))
        val omit6 : ChordModifier = ChordModifier(listOf("omit6", "omit 6"), listOf( ChordModifierPart.ChordModifierOmit(CIndex.SIXTH) ))
        val omit7 : ChordModifier = ChordModifier(listOf("omit7", "omit 7"), listOf( ChordModifierPart.ChordModifierOmit(CIndex.SEVENTH) ))
        val omit9 : ChordModifier = ChordModifier(listOf("omit9", "omit 9"), listOf( ChordModifierPart.ChordModifierOmit(CIndex.NINTH) ))
        val omit11 : ChordModifier = ChordModifier(listOf("omit11", "omit 11"), listOf( ChordModifierPart.ChordModifierOmit(CIndex.ELEVENTH) ))

        //
        operator fun ChordModifier.plus(mod : ChordModifier) : List<ChordModifier> = listOf(this, mod)
    }
}

/**
 * キーのインデックス配列を管理するクラス
 *
 * Chord の基本形の場合の各キーの配置を基準位置からの半音の個数という形で[baseIndexes]保持する
 *
 * [baseIndexes]と転回数[inversion]から作られる転回後のインデックス配列を[indexes]として保持する
 *
 * --- 解説 ---
 * 配置情報は[modifiers]としてコードの補正情報[ChordModifier]として管理されている。
 * [modifiers]を展開して指の追加・削除・半音移動の情報が[modifierMap]として保存される。
 * [modifierMap]の情報を元に[baseIndexes]が作成される。
 * [inversion]に基づいて[baseIndexes]を転回した配置が[indexes]に保存される。
 *
 * (e.g.  C.baseIndexes = [0,4,7], C.indexesPattern = [[0,4,7], [4,7,0], [7,0,4]]
 *
 * */
interface ChordIndexer {
    val code : String
    val modifiers : List<ChordModifier>
    /**
     * 第N音をスライドするための情報
     * */
    val modifierMap : Map<CIndex, Int>
    /**
     * 転回数
     * */
    val inversion : Int
    /**
     * コードのインデックス情報（転回前）
     *
     * 各キーの基準音からの半音の数
     *
     * [baseIndexes]では配列は必ず0から始まる
     * */
    val baseIndexes : List<Int>
    /**
     * コードのインデックス情報（転回後）
     *
     * 各キーの基準音からの半音の数
     *
     * 転回している場合は配列は必ず0以外から始まる
     * */
    val indexes : List<Int>
    /**
     * コードのインデックス情報の全転回パターン
     * */
    val indexesPattern : List<List<Int>>
    /**
     * トライトーンの関係となる音を持つか
     * */
    val hasTritone : Boolean

    /** [tonic]を開始点としてインデックス配列を[Pitch]配列に変換する */
    fun pitches(tonic : Pitch) : List<Pitch> = indexes.map { tonic + it }
    /** [tonic]を開始点としてインデックス配列を[Key]配列に変換する */
    fun keys(tonic : Key) : List<Key> = indexes.map { tonic + it }

    /**
     * インデックス配列[indexes]がこのコードの転回パターンの一つと一致するかをチェックする
     * */
    operator fun contains( indexes: List<Int> ) : Boolean = indexes.shorten in indexesPattern.map { it.shorten }
    /** [indexes]のパターンに対応する転回の値を返却する */
    fun inversion( indexes: List<Int> ): Int {
        val ret = indexesPattern.map { it.shorten }.indexOf( indexes.shorten )
        if( ret == -1 ) throw RuntimeException("$this は $indexes のパターンに対応できません")

        return ret
    }

    /** インデックス配列を転回する */
    val List<Int>.inversion : List<Int> get() = this.subList(1, this.size) + this[0]
    /** インデックス配列を[count]回だけ転回する */
    fun List<Int>.inversion(count : Int) : List<Int> {
        val count = count % baseIndexes.size
        return if( count == 0 ) this else this.inversion.inversion(count-1)
    }

    /** [inversion]だけ転回したバージョンを作成する */
    fun verIndexerInversion(inversion : Int) : ChordIndexer {
        return ChordIndexerImpl(inversion, modifiers)
    }
    /** 全ての転回のバージョンを取得します */
    fun verIndexerInversions() : List<ChordIndexer> {
        return 0.until(baseIndexes.size).map { verIndexerInversion(it) }
    }
}
internal class ChordIndexerImpl(
    inversion : Int = 0,
    mods: List<ChordModifier>
) : ChordIndexer {
    constructor(inversion : Int = 0, vararg modifiers: ChordModifier) : this(inversion, modifiers.toList())

    override val code: String = mods.joinToString(""){ it.codes.first() }
    override val modifiers: List<ChordModifier> = mods.toList()
    override val modifierMap: Map<CIndex, Int> = {
        val parts = modifiers.map { it.parts }.flatten().sorted()

        val ret = mutableMapOf<CIndex, Int>()
        parts.forEach { it.invoke(ret) }

        ret
    }.invoke()
    override val baseIndexes: List<Int> = {
        modifierMap.toList().map {
            it.first.value + it.second
        }.toMutableList()
    }.invoke()
    override val indexes: List<Int> = baseIndexes.inversion(inversion % baseIndexes.size)
    override val inversion: Int = inversion % (indexes.size)
    override val indexesPattern: List<List<Int>> = 0.until(indexes.size).map { baseIndexes.inversion(it) }
    override val hasTritone: Boolean = indexes.any {
        val index = it
        indexes.any { kotlin.math.abs(it - index) == 6 }
    }

    companion object {
        /** [indexer] の初期リスト。[of]で検索するために使用する */
        private val indexerList : MutableList<ChordIndexer> = mutableListOf()

        /** [indexerList]に登録されたリストからインデックス・リストが一致する最初の要素を返却する */
        fun of(indexes : List<Int>, filter : iCondition = {true}) : ChordIndexer {
            val indexer = indexerList.first( rIndexer { hasIndexesPattern(indexes) and filter } )
            val inv = indexer.inversion(indexes)

            return ChordIndexerImpl(inv, indexer.modifiers)
        }
        /** [indexerList]に登録されたリストからインデックス・リストが一致する要素のリストを返却する */
        fun listOf(indexes : List<Int>, filter : iCondition = {true}) : List<ChordIndexer> {
            val indexers = indexerList.filter( rIndexer { hasIndexesPattern(indexes) and filter } )

            return indexers.map {
                val inv = it.inversion(indexes)

                ChordIndexerImpl(inv, it.modifiers)
            }
        }
        /** [indexerList]から全要素を取得する */
        fun indexers(inversion : Boolean = false) : List<ChordIndexer> {
            val ret = indexerList.toList()
            return if( inversion ) ret.map { it.verIndexerInversions() }.flatten() else ret
        }

        /** [indexerList] にある程度の[indexer]を追加しておくための初期化関数 */
        private val initialized = {
            """
                C
                C-5
                C6
                C69
                Csus4
                C7
                CM7
                C7-5
                C7-9
                C7sus4
                C7+9
                C7+11
                C7+13
                Cadd9
                C9
                C9-5
                C-9
                C-9+5
                CM9
                Caug9
                C11
                C13
                Cm
                Cm69
                Cm7
                CmM7
                Cm6
                Cm7-5
                Cm9
                Cm11
                Cm13
                Cdim
                Cdim7
                Caug
                Caug7
                CaugM7
                Cadd2
                Cadd4
            """.trimIndent().split("\n").forEach {
                val indexer = indexer(it.substring(1, it.length))
                indexerList.add(indexer)
            }
        }.invoke()
    }
}


/** [root]とコードを構成するキーインデックス配列[ChordIndexer]の組み合わせ */
interface Chord : ChordIndexer {
    val root : Pitch
    val indexer : ChordIndexer
    val pitches : List<Pitch>
    val bass : Pitch

    operator fun contains(pitch : Pitch) : Boolean = pitch in pitches

    /** [chord]を[octave]にバインドして[KeyChord]を生成します */
    fun bind(octave : Int = 4) : KeyChord = KeyChordImpl(octave, this)

    /** [pitch]を追加したバージョンを作成する */
    fun verAddition(pitch : Pitch) : Chord {
        return if( pitch in this ){
            this
        }else{
            val indexes = pitches.insert(pitch).ordinals
            val indexer = ChordIndexerImpl.of(indexes)

            // TODO : インデックス配列だけだと複数にっとする。検索条件にこの[Chord]がもつ全ての[ChordModifierPort]を持つことを条件として入れる。若しくは検索をリストで取得して優先順位をそれで付ける。

            return ChordImpl(root, indexer)
        }
    }
    /** [bass]が最低音になるように転回したバージョンを作成する */
    fun verBass(bass : Pitch) : Chord {
        // bass が含まれていない場合に対応するために追加バージョンを作成する
        val chord = this.verAddition(bass)

        return chord.run {
            // 転回バージョンを作成する
            val inv = pitches.indexOf(bass)
            verInversion(inv)
        }
    }
    /** [inversion]だけ転回したバージョンを作成する */
    fun verInversion(inversion : Int) : Chord {
        return ChordImpl(root, ChordIndexerImpl(inversion, modifiers))
    }
    /** 全ての転回のバージョンを取得します */
    fun verInversions() : List<Chord> {
        return 0.until(baseIndexes.size).map { verInversion(it) }
    }
}
internal class ChordImpl(override val root: Pitch, override val indexer: ChordIndexer) : Chord, ChordIndexer by indexer {
    override val pitches: List<Pitch> = indexer.pitches(root)
    override val bass: Pitch = pitches[0]

    override val code : String get() = "$root${indexer.code}" + if(inversion != 0){ "/${bass.code}" }else{ "" }
    override fun toString(): String = code

    companion object {
        /**
         * [ChordIndexer]の登録されたリストを全ての[Pitch]をトニックとして展開した全リストを取得する
         *
         * [inversion] = true ならば全ての転回したバージョンも取得する
         * */
        fun chords(inversion : Boolean = false) : List<Chord> {
            return Pitch.values().map {
                val root = it
                ChordIndexerImpl.indexers(inversion).map { root bind it }
            }.flatten()
        }
    }
}
infix fun Pitch.bind(indexer : ChordIndexer) : Chord = ChordImpl(this, indexer)

/** [degree]とコードを構成するキーインデックス配列[ChordIndexer]の組み合わせ */
interface DegreeChord : ChordIndexer {
    val degree : Degree
    val indexer : ChordIndexer
}
internal class DegreeChordImpl(override val degree : Degree, override val indexer: ChordIndexer) : DegreeChord, ChordIndexer by indexer {
    override val code : String get() = "${degree.code}${indexer.code}"
    override fun toString(): String = code
}
infix fun Degree.bind(indexer : ChordIndexer) : DegreeChord = DegreeChordImpl(this, indexer)


//
fun indexer(code : String) : ChordIndexer {
    val code = code.refix(" ")

    val mods = ChordModifier.analyze(code)

    return ChordIndexerImpl(0, ChordModifier.root, *mods.toTypedArray())
}
fun chord(code : String) : Chord {
    val (code, bass) = code.split("/", "on").run{ this[0].refix(" ") to (if( this.size >=2 ) this[1].refix(" ") else "" ) }

    // #/♭ が先に検索対象になるようにソートする
    val pitches = Pitch.values().sortedBy { it.code.length }.reversed()

    // 先頭の文字に一致するピッチを決定する
    val pitch = pitches.first { it.codes.any { code.startsWith(it) } }
    // [ChordIndexer]部分を抽出する
    val mods = pitch.codes.first { code.startsWith(it) }.run { code.substring(this.length, code.length) }

    val indexer = indexer(mods)

    if( bass.isEmpty() ){
        return ChordImpl(pitch, indexer)
    }else{
        var chord = ChordImpl(pitch, indexer)
        val bass = Pitch.of(bass)

        return chord.verBass(bass)
    }
}
fun degree(code : String) : DegreeChord {
    val code = code.refix(" ")

    val degrees = Degree.values()

    // 先頭の文字に一致する[DegreeName]を決定する
    val degree = degrees.first { code.startsWith(it.code) }
    // [ChordIndexer]部分を抽出する
    val mods = code.substring(degree.code.length, code.length)

    val indexer = indexer(mods)

    return DegreeChordImpl(degree, indexer)
}

val String.indexer : ChordIndexer get() = indexer(this)
val String.chord : Chord get() = chord(this)
val String.degree : DegreeChord get() = degree(this)

/**
 * 転回数[inversion]と補正修飾子[ChordModifier]の生成関数[func]から[ChordIndexer]を作成する
 * */
fun indexer(inversion : Int=0, func : ChordModifier.Companion.()->(Any) ) : ChordIndexer {
    val list = ChordModifier.Companion.func().run { if( this is Iterable<*> ) this.toList() else listOf(this)  }
    val mods = list.filterIsInstance(ChordModifier::class.java)

    return ChordIndexerImpl(inversion, ChordModifier.root, *mods.toTypedArray())
}
/**
 * 既存の[ChordIndexer]からインデックス配列が一致する[ChordIndexer]を検索します
 *
 * 複数ぞんざいする場合は最初の要素を返却します
 *
 * @see [indexers]
 * */
fun indexer(vararg indexes : Int) : ChordIndexer {
    return ChordIndexerImpl.of(indexes.toList())
}
/**
 * 既存の[ChordIndexer]からインデックス配列が一致する[ChordIndexer]を検索します
 * */
fun indexers(vararg indexes : Int) : List<ChordIndexer> {
    return ChordIndexerImpl.listOf(indexes.toList())
}

/** 根音[root]、転回数[inversion]、補正修飾子[ChordModifier]の生成関数[func]から[Chord]を作成する */
fun chord(root : Pitch, inversion: Int = 0, func : ChordModifier.Companion.()->(Any) ) : Chord {
    return root bind indexer(inversion, func)
}
/** 根音[root]、インデックス配列[indexers]から[Chord]を作成する */
fun chord(root : Pitch, vararg indexes : Int) : Chord {
    return ChordImpl(root, indexer(*indexes))
}

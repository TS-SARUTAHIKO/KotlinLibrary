package com.xxxsarutahikoxxx.kotlin.ChordStructure


typealias iCondition = (ChordIndexer)->(Boolean)
typealias cCondition = (Chord)->(Boolean)
typealias kCondition = (KeyChord)->(Boolean)

interface iFactory
interface cFactory
interface kFactory

class iMixer internal constructor() : iFactory {
    infix fun iCondition.and(con : iCondition) : iCondition {
        return { this.invoke(it) && con.invoke(it) }
    }
    infix fun iCondition.or(con : iCondition) : iCondition {
        return { this.invoke(it) || con.invoke(it) }
    }
}
class cMixer internal constructor() : iFactory, cFactory {
    infix fun cCondition.and(con : cCondition) : cCondition {
        return { this.invoke(it) && con.invoke(it) }
    }
    infix fun cCondition.or(con : cCondition) : cCondition {
        return { this.invoke(it) || con.invoke(it) }
    }
}
class kMixer internal constructor() : iFactory, cFactory, kFactory {
    infix fun kCondition.and(con : kCondition) : kCondition {
        return { this.invoke(it) && con.invoke(it) }
    }
    infix fun kCondition.or(con : kCondition) : kCondition {
        return { this.invoke(it) || con.invoke(it) }
    }
}

internal val _iMixer = iMixer()
internal val _cMixer = cMixer()
internal val _kMixer = kMixer()

fun rIndexer(func : iMixer.()->(iCondition) ) : iCondition = _iMixer.func()
fun rChord(func : cMixer.()->(cCondition) ) : cCondition = _cMixer.func()
fun rKeyChord(func : kMixer.()->(kCondition) ) : kCondition = _kMixer.func()


/** 条件に合う[ChordIndexer]を全取得する */
fun iCondition.indexers(inversion : Boolean = false) : List<ChordIndexer> {
    return ChordIndexerImpl.indexers(inversion).filter(this)
}
/** 条件に合う[ChordIndexer]を全取得する（転回バージョンは含めない） */
val iCondition.indexers : List<ChordIndexer> get() = indexers(false)

/** 条件に合う[Chord]を全取得する */
fun cCondition.chords(inversion : Boolean = false) : List<Chord> {
    return ChordImpl.chords(inversion).filter(this)
}
/** 条件に合う[Chord]を全取得する（転回バージョンは含めない） */
val cCondition.chords : List<Chord> get() = chords(false)
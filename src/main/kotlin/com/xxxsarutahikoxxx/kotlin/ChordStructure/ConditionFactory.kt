package com.xxxsarutahikoxxx.kotlin.ChordStructure


// iFactory
val iFactory.hasTritone : iCondition get() = { it.hasTritone }

fun iFactory.number(num : Int) : iCondition = { it.baseIndexes.size == num }
val iFactory.triad : iCondition get() = { number(3)(it) && noOmit(it) }

val iFactory.inverse : iCondition get() = { it.inversion != 0 }
fun iFactory.inversion(num : Int) : iCondition = { it.inversion == num }
fun iFactory.inversion( range : (Int)->(Boolean) ) : iCondition = { range(it.inversion) }

fun iFactory.has(index : CIndex) : iCondition = { it.modifierMap.containsKey(index) }
fun iFactory.flat(index : CIndex) : iCondition = { it.modifierMap[index] == -1 }
fun iFactory.natural(index : CIndex) : iCondition = { it.modifierMap[index] == 0 }
fun iFactory.sharp(index : CIndex) : iCondition = { it.modifierMap[index] == 1 }

val iFactory.omit : iCondition get() = { it.modifiers.map { it.parts }.flatten().any { it is ChordModifierPart.ChordModifierOmit } }
val iFactory.noOmit : iCondition get() = { ! omit(it) }

val iFactory.dim : iCondition get() = { it.modifiers.map { it.parts }.flatten().any { it is ChordModifierPart.ChordModifierDim } }
val iFactory.noDim : iCondition get() = { ! dim(it) }

fun iFactory.hasIndexesPattern(indexes : List<Int>) : iCondition = { indexes in it.indexesPattern }

val iFactory.minor : iCondition get() = { flat(CIndex.THIRD)(it) }
val iFactory.major : iCondition get() = { (triad(it) && natural(CIndex.ROOT)(it) && natural(CIndex.THIRD)(it) && natural(CIndex.FIFTH)(it)) || sharp(CIndex.SEVENTH)(it) }

fun iFactory.hasBranch(index : CIndex) : iCondition = { it.modifiers.any { it.branch && it.parts.last().let { it is ChordModifierPart.ChordModifierAddition && it.index == index } } }
val iFactory.sixth : iCondition get() = hasBranch(CIndex.SIXTH)
val iFactory.seventh : iCondition get() = hasBranch(CIndex.SEVENTH)
val iFactory.ninth : iCondition get() = hasBranch(CIndex.NINTH)
val iFactory.eleventh : iCondition get() = hasBranch(CIndex.ELEVENTH)
val iFactory.thirteenth : iCondition get() = hasBranch(CIndex.THIRTEENTH)


// cFactory
fun cFactory.root(root : Pitch) : cCondition = { it.root == root }
fun cFactory.bass(bass : Pitch) : cCondition = { it.bass == bass }

fun cFactory.onPitches(vararg pitches : Pitch) : cCondition = { it.pitches.all { it in pitches } }
fun cFactory.onScale(scale : Scale, incline: Boolean? = true) : cCondition {
    val pitches = when(incline){
        true -> scale.pitches(incline)
        false -> scale.pitches(incline)
        null -> scale.pitches(true) + scale.pitches(false)
    }.distinct()

    return onPitches(*pitches.toTypedArray())
}

/** 構成要素が[pitches]と一致する */
fun cFactory.consistOf(vararg pitches : Pitch) : cCondition = {
    it.pitches.size == pitches.size && (it.pitches - pitches).isEmpty()
}
/** [pitches]を全て要素として含む */
fun cFactory.contains(vararg pitches : Pitch) : cCondition = {
    (pitches.toList() - it.pitches).isEmpty()
}


// kFactory


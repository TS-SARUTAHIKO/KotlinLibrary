package com.xxxsarutahikoxxx.kotlin.Utilitys

import java.io.Serializable

/**
 * 可変 Pair
 * */
data class MutablePair<A, B>(
    var first: A,
    var second: B
) : Serializable {
    override fun toString(): String = "($first, $second)"
}

/**
 * 可変 Triple
 * */
data class MutableTriple<A, B, C>(
    var first: A,
    var second: B,
    var third: C
) : Serializable {
    override fun toString(): String = "($first, $second, $third)"
}

package org.xxxsarutahikoxxx.kotlin.Utilitys


val String.removeSpaceSurrounding : String get(){
    return this.replace("^ +| +$".toRegex(), "")
}

fun String.isSurrounded(prefix : String, suffix : String) : Boolean {
    return startsWith(prefix) && endsWith(suffix)
}

package com.xxxsarutahikoxxx.kotlin.Utilitys

import java.io.File
import java.io.InputStream
import java.lang.RuntimeException


var errorstream : (Any?)->(Unit) = { println("$it") }
var error : Any?
    get() = throw RuntimeException("エラー出力ストリームは書き込み専用です")
    set(value) { errorstream(value) }

var outstream : (Any?)->(Unit) = { println("$it") }
var out : Any?
    get() = throw RuntimeException("標準出力ストリームは書き込み専用です")
    set(value) { outstream(value) }

var File.out : String
    get(){
        return inputStream().use {
            it.bufferedReader().use { it.readText() }
        }
    }
    set(value) {
        outputStream().use {
            it.bufferedWriter().use { it.write(value) }
        }
    }

/** Pair<A, B> から Triple<A, B, C> を作る関数 */
infix fun <A, B, C> Pair<A, B>.tt( third : C ) : Triple<A, B, C> = Triple(first, second, third)

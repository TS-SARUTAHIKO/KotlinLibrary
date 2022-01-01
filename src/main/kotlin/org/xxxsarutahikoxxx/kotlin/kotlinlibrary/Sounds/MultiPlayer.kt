package org.xxxsarutahikoxxx.kotlin.kotlinlibrary.Sounds

import java.io.File
import java.io.InputStream


/** 複数の音声ファイルの制御を行うためのエイリアス・クラス */
typealias MultiPlayer = List<AudioPlayer>

fun MultiPlayer(vararg files : File) : MultiPlayer = files.map { AudioPlayer(it) }
fun MultiPlayer(vararg streams : InputStream) : MultiPlayer = streams.map { AudioPlayer(it) }

fun MultiPlayer.start() = forEach { it.start() }
fun MultiPlayer.stop() = forEach { it.stop() }
fun MultiPlayer.startOrStop() = forEach { it.startOrStop() }
fun MultiPlayer.close() = forEach { it.close() }

var MultiPlayer.position : Long
    get() = first().position
    set(value) { forEach { it.position = value } }
var MultiPlayer.volumeBD : Float
    get() = first().volumeBD
    set(value) { forEach { it.volumeBD = value } }
var MultiPlayer.volume : Double
    get() = first().volume
    set(value) { forEach { it.volume = value } }


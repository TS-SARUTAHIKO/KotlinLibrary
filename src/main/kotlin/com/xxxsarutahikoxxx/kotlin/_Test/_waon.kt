package com.xxxsarutahikoxxx.kotlin._Test

import com.xxxsarutahikoxxx.kotlin.Sounds.AudioPlayer
import com.xxxsarutahikoxxx.kotlin.Sounds.MultiPlayer
import com.xxxsarutahikoxxx.kotlin.Sounds.start
import com.xxxsarutahikoxxx.kotlin.Utilitys.out
import java.io.ByteArrayInputStream
import java.io.File

fun main(args: Array<String>) {

    val down = "C:/Users/tshom_000/Downloads"
//
//    val index = 4
//    val list = listOf("ド", "レ", "ミ", "ファ", "ソ", "ラ", "シ").run {
//        val index = 0
//        listOf(this[index], this[index+2], this[index+4])
//    }
//
//    MultiPlayer(File("${down}/${index}${list[0]}.wav"), File("${down}/${index}${list[1]}.wav"), File("${down}/${index}${list[2]}.wav")).start()

    val input = File("${down}/0シ.wav").inputStream().readBytes().run { ByteArrayInputStream(this) }

    AudioPlayer(input).apply {
        start()
    }

    Thread.sleep(1000)
}

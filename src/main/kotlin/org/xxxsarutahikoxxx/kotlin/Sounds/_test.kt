package org.xxxsarutahikoxxx.kotlin.Sounds

import org.xxxsarutahikoxxx.kotlin.Utilitys.out
import java.io.File
import javax.sound.sampled.AudioSystem
import javax.sound.sampled.Port


fun main(args: Array<String>) {
//    RecAndPlayer(File("bgm.wav"), File("vocal.wav"), prefixTime = 3000, suffixTime = 1000 ).apply {
//        start()
//
//        Thread.sleep(6000)
//        stop()
//    }

    val info = AudioSystem.getMixerInfo()[4]
    val mixer = AudioSystem.getMixer(null)

    out = "${ mixer.mixerInfo }"
    mixer.sourceLineInfo.forEach {
        out = it
    }
    mixer.targetLineInfo.forEach {
        out = it
    }
}
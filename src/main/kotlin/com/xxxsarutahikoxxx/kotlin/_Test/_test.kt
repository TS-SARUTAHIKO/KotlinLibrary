package com.xxxsarutahikoxxx.kotlin._Test

import com.xxxsarutahikoxxx.kotlin.Sounds.InnerRecorder
import com.xxxsarutahikoxxx.kotlin.Utilitys.out
import java.awt.Robot
import java.awt.event.*
import java.io.File
import javax.swing.JFrame


fun main(args: Array<String>) {
    var dx = 1318.0 / 49

    val frame = JFrame().apply {
        defaultCloseOperation = JFrame.EXIT_ON_CLOSE
        isVisible = true
        setBounds(476,1035,945,100)

        addComponentListener(object : ComponentAdapter(){
            override fun componentResized(e: ComponentEvent?) {
                title = "$x, $y, $width"
            }
        })
    }


    val indexes : Pair<Int, Int> = 2 to 7
    val range = ("-1ラ".value)..("7ド".value)


    val white = white.subList(white.indexOf("${indexes.first}ド"), white.indexOf("${indexes.second}シ")+1)
    val black = black.subList(black.indexOf("${indexes.first}ド#"), black.indexOf("${indexes.second}ラ#")+1)

    val whites = white.mapIndexed { index, s -> s to index }
    val blacks = black.mapIndexed { index, s -> s to index }

    val (wx0, wy0) = 530 to 1035 -1
    val (bx0, by0) = 545 to 1010 -1


    val rb = Robot()
    listOf(true, false).forEach {
        val isBlack = it
        val keys = if(isBlack) blacks else whites

        keys.forEach {
            val (x, y) = when{
                isBlack -> (bx0 + dx*it.second) to by0
                else  -> (wx0 + dx*it.second) to wy0
            }

            if( it.first != null && it.first?.value in range ){
                File("sounds").mkdirs()

                InnerRecorder(File("sounds/${it.first}.wav")).start(700)

                rb.mouseMove(x.toInt(), y.toInt())
                rb.mousePress(InputEvent.BUTTON1_MASK)
                Thread.sleep(150)
                rb.mouseRelease(InputEvent.BUTTON1_MASK)
                Thread.sleep(550)
            }

        }

    }

}

val wKey = listOf("ド", "レ", "ミ", "ファ", "ソ", "ラ", "シ")
val bKey = listOf("ド#", "レ#", null, "ファ#", "ソ#", "ラ#", null)

val white = ((-2)..(8)).map {
    val index = it
    wKey.map { it?.run{"$index$it"} }
}.flatten()

val black = ((-2)..(8)).map {
    val index = it
    bKey.map { it?.run{"$index$it"} }
}.flatten()

val String.value : Int get(){
    val list = this.replace("#", ".5")
        .replace("ド", ".1")
        .replace("レ", ".2")
        .replace("ミ", ".3")
        .replace("ファ", ".4")
        .replace("ソ", ".5")
        .replace("ラ", ".6")
        .replace("シ", ".7").split(".")

    return list[0].toInt()*100 + list[1].toInt()*10 + (list.getOrNull(2)?.toInt()?:0)
}
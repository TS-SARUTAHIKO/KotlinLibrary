package org.xxxsarutahikoxxx.kotlin.KotlinLibrary.Feature

import java.awt.Robot
import java.awt.event.KeyEvent


private val rb = Robot()

val Char2Code : Map<Char, Int> = ('a'..'z').zip(KeyEvent.VK_A..KeyEvent.VK_Z).toMap()
val Char.toCode : Int get() = Char2Code[this]!!

fun Char.type(){
    this.toCode.run { rb.keyPress(this) ; rb.keyRelease(this) }
}

fun String.type(){
    this.forEach { it.toChar().toCode.run { rb.keyPress(this) ; rb.keyRelease(this) } }
}

fun onHIRAGANA( action : ()->(Unit) ){
    action.invoke()

    rb.keyPress(KeyEvent.VK_F6)
    rb.keyRelease(KeyEvent.VK_F6)

    rb.keyPress(KeyEvent.VK_ENTER)
    rb.keyRelease(KeyEvent.VK_ENTER)
}
fun onZENKANA( action : ()->(Unit) ){
    action()

    rb.keyPress(KeyEvent.VK_F7)
    rb.keyRelease(KeyEvent.VK_F7)

    rb.keyPress(KeyEvent.VK_ENTER)
    rb.keyRelease(KeyEvent.VK_ENTER)
}
fun onHANKANA( action : ()->(Unit) ){
    action()

    rb.keyPress(KeyEvent.VK_F8)
    rb.keyRelease(KeyEvent.VK_F8)

    rb.keyPress(KeyEvent.VK_ENTER)
    rb.keyRelease(KeyEvent.VK_ENTER)
}
fun onZENALPHA( action : ()->(Unit) ){
    action()

    rb.keyPress(KeyEvent.VK_F9)
    rb.keyRelease(KeyEvent.VK_F9)

    rb.keyPress(KeyEvent.VK_ENTER)
    rb.keyRelease(KeyEvent.VK_ENTER)
}
fun onHANALPHA( action : ()->(Unit) ){
    action()

    rb.keyPress(KeyEvent.VK_F10)
    rb.keyRelease(KeyEvent.VK_F10)

    rb.keyPress(KeyEvent.VK_ENTER)
    rb.keyRelease(KeyEvent.VK_ENTER)
}

val String.onHIRAGANA get() = onHIRAGANA { this.type() }
val String.onHANKANA get() = onHANKANA { this.type() }
val String.onZENKANA get() = onZENKANA { this.type() }
val String.onHANALPHA get() = onHANALPHA { this.type() }
val String.onZENALPHA get() = onZENALPHA { this.type() }



fun main(args: Array<String>) {
    val vowel = listOf(KeyEvent.VK_A, KeyEvent.VK_I, KeyEvent.VK_U, KeyEvent.VK_E, KeyEvent.VK_O)
    val consonant = (KeyEvent.VK_A..KeyEvent.VK_Z) - vowel

//    for(c in consonant){
//        for(v in vowel){
//            rb.keyPress(c)
//            rb.keyPress(v)
//            rb.keyRelease(v)
//            rb.keyRelease(c)
//        }
//        rb.keyPress(KeyEvent.VK_ENTER)
//        rb.keyRelease(KeyEvent.VK_ENTER)
//        rb.keyPress(KeyEvent.VK_ENTER)
//        rb.keyRelease(KeyEvent.VK_ENTER)
//    }
//
    rb.keyPress(KeyEvent.VK_INPUT_METHOD_ON_OFF)
    rb.keyRelease(KeyEvent.VK_INPUT_METHOD_ON_OFF)


    "sample".onHIRAGANA
}


package org.xxxsarutahikoxxx.kotlin.kotlinlibrary._Test

import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.xxxsarutahikoxxx.kotlin.kotlinlibrary.IORunner.ClientUDPRunner


fun main() {
    ClientUDPRunner("localhost", 53456, 200, false).apply {
        connect()

        runBlocking {
            launch {
                repeat(1000){
                    delay(1000)
                    writeObject("sample $it")
                }
            }
        }
    }
}
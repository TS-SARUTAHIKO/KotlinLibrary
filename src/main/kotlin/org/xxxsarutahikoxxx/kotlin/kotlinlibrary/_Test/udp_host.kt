package org.xxxsarutahikoxxx.kotlin.kotlinlibrary._Test

import org.xxxsarutahikoxxx.kotlin.kotlinlibrary.IORunner.HostUDPRunner


fun main(){
    HostUDPRunner(53456, 200).apply {
        open()
    }
}
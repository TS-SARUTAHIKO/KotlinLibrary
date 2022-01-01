package org.xxxsarutahikoxxx.kotlin.KotlinLibrary._Test

import org.xxxsarutahikoxxx.kotlin.KotlinLibrary.IORunner.ClientTCPRunner
import org.xxxsarutahikoxxx.kotlin.KotlinLibrary.IORunner.ClientUDPRunner
import org.xxxsarutahikoxxx.kotlin.KotlinLibrary.IORunner.HostUDPRunner
import org.xxxsarutahikoxxx.kotlin.KotlinLibrary.Utilitys.HostAddress
import org.xxxsarutahikoxxx.kotlin.KotlinLibrary.Utilitys.HostName
import org.xxxsarutahikoxxx.kotlin.KotlinLibrary.Utilitys.out


fun main(){
    HostUDPRunner(53456, 200).apply {
        open()
    }
}
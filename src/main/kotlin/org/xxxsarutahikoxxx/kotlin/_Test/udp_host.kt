package org.xxxsarutahikoxxx.kotlin._Test

import org.xxxsarutahikoxxx.kotlin.IORunner.ClientTCPRunner
import org.xxxsarutahikoxxx.kotlin.IORunner.ClientUDPRunner
import org.xxxsarutahikoxxx.kotlin.IORunner.HostUDPRunner
import org.xxxsarutahikoxxx.kotlin.Utilitys.HostAddress
import org.xxxsarutahikoxxx.kotlin.Utilitys.HostName
import org.xxxsarutahikoxxx.kotlin.Utilitys.out


fun main(){
    HostUDPRunner(53456, 200).apply {
        open()
    }
}
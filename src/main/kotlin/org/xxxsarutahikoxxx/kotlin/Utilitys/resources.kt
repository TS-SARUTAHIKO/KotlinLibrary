package org.xxxsarutahikoxxx.kotlin.Utilitys

import java.io.InputStream


private class Loader {
    companion object {
        fun getResourceAsStream(name : String) : InputStream =
            Loader::class.java.getResourceAsStream("/$name")
    }
}
fun getResourceAsStream(name : String) = Loader.getResourceAsStream(name)


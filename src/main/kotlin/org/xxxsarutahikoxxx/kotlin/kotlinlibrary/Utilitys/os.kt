package org.xxxsarutahikoxxx.kotlin.kotlinlibrary.Utilitys

import java.net.InetAddress


val isWindows : Boolean by lazy { System.getProperty("os.name").contains("windows", true) }
val isAndroid : Boolean by lazy { System.getProperty("os.name").contains("android", true) }
val is32Arch : Boolean by lazy { System.getProperty("os.arch").contains("86", true) }
val is64Arch : Boolean by lazy { System.getProperty("os.arch").contains("64", true) }


/**
 * ウィンドウズOSであった場合は関数を実行して返り値を返却する
 *
 * 違う場合は null を返却する
 *  */
fun <Ret> ifWindows( func : ()->(Ret) ) : Ret? {
    return if( isWindows ) func() else null
}
/**
 * アンドロイドOSであった場合は関数を実行して返り値を返却する
 *
 * 違う場合は null を返却する
 *  */
fun <Ret> ifAndroid( func : ()->(Ret) ) : Ret? {
    return if( isAndroid ) func() else null
}

/**
 * ウィンドウズOSであった場合は関数を実行して返り値を返却する
 *
 * 違う場合は null を返却する
 *  */
fun <T : Any, Ret> T.ifWindows( func : T.()->(Ret) ) : Ret? {
    return if( isWindows ) func() else null
}
/**
 * アンドロイドOSであった場合は関数を実行して返り値を返却する
 *
 * 違う場合は null を返却する
 *  */
fun <T : Any, Ret> T.ifAndroid( func : T.()->(Ret) ) : Ret? {
    return if( isAndroid ) func() else null
}



val HostName : String by lazy { InetAddress.getLocalHost().hostName }
val HostAddress : String by lazy { InetAddress.getLocalHost().hostAddress }

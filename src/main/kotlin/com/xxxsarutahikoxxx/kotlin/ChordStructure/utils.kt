package com.xxxsarutahikoxxx.kotlin.ChordStructure


internal fun String.rePrefix(vararg prefix : String) : String {
    var ret = this

    while( true ){
        var finish = true

        for(fix in prefix){
            if( ret.startsWith(fix) ) {
                ret = ret.substring(fix.length, ret.length)
                finish = false
            }
        }

        if( finish ) break
    }

    return ret
}
internal fun String.reSuffix(vararg suffix : String) : String {
    var ret = this

    while( true ){
        var finish = true

        for(fix in suffix){
            if( ret.endsWith(fix) ) {
                ret = ret.substring(0, ret.length-fix.length)
                finish = false
            }
        }

        if( finish ) break
    }

    return ret
}

/** [rePrefix] [reSuffix] によって文字列の先頭と末尾から文字列を除去する */
internal fun String.refix(vararg fix : String) : String = this.rePrefix(*fix).reSuffix(*fix)

/**  */
internal fun String.remove(vararg fix : String) : String {
    var ret = this
    fix.forEach { ret = ret.replace(it, "") }
    return ret
}

/** インデックス配列を最初の要素が0となる相対配列に変換する */
internal val List<Int>.shorten : List<Int> get() = this.map { it - this[0] }

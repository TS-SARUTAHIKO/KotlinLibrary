package org.xxxsarutahikoxxx.kotlin.KotlinLibrary.Sounds

import java.io.File
import java.io.InputStream
import kotlin.concurrent.thread

class RecAndPlayer(
    val players : MultiPlayer,
    outfile : File,

    /** 録音の開始から演奏が開始するまでの時間(ミリ秒) */
    var prefixTime : Long = 0,
    /** 演奏の終了から録音が終了するまでの時間(ミリ秒) */
    var suffixTime : Long = 0
){
    constructor(prefixTime : Long = 0, suffixTime : Long = 0, outfile : File, vararg infiles : File) : this(
        MultiPlayer(*infiles.toList().toTypedArray()), outfile, prefixTime, suffixTime
    )
    constructor(prefixTime : Long = 0, suffixTime : Long = 0, outfile : File, vararg instreams : InputStream) : this(
        MultiPlayer(*instreams.toList().toTypedArray()), outfile, prefixTime, suffixTime
    )


    val recorder = OuterRecorder(outfile)
    /** 出力するファイル */
    val outfile get() = recorder.outFile


    var run = false
        private set(value) { field = value }

    /**  */
    fun start(){
        if( run )return

        thread {
            val time = players.map { it.length }.maxOrNull()!!
            val maxtime = prefixTime + time + suffixTime
            run = true

            for( func in listOf({
                recorder.start(maxtime)
            }, {
                Thread.sleep(prefixTime)
            }, {
                players.start()
            }, {
                Thread.sleep(time + suffixTime)
            }, {
                recorder.stop()
            }, {
                onFinish()
            }) ) if( run ) func()
        }

        onStart()
    }
    /**  */
    fun stop(){
        if( ! run )return

        run = false

        recorder.stop()
        players.stop()

        onStop()
    }


    var onStart : ()->(Unit) = {}
    var onStop : ()->(Unit) = {}
    var onFinish : ()->(Unit) = {}
}

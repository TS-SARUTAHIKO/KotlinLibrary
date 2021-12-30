package org.xxxsarutahikoxxx.kotlin.Sounds

import org.xxxsarutahikoxxx.kotlin.Utilitys.out
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.io.File
import javax.sound.sampled.*


abstract class Recorder(
    val outFile : File
){
    protected abstract val format : AudioFormat // = AudioFormat(sampleRate, 16, 2, true, true)
    protected abstract val line : TargetDataLine // = AudioSystem.getTargetDataLine(format).apply { open(format) }

    protected var run = false
    protected var finished = false


    /** 録音を開始する */
    fun start(maxTime : Long){
        if( run || finished )return

        GlobalScope.launch {
            delay(maxTime)
            stop()
        }
        GlobalScope.launch {
            if( ! run && ! finished ){
                run = true
                out = "Recording : Start"

                line.start()

                val ais = AudioInputStream(line)
                AudioSystem.write(ais, AudioFileFormat.Type.WAVE, outFile)
            }
        }

        onStart()
    }
    var onStart : ()->(Unit) = {}

    /** 録音を終了する */
    fun stop(){
        if( run ){
            finished = true
            run = false
            out = "Recording : Stop"

            line.close()
            line.stop()

            onStop()
        }
    }
    var onStop : ()->(Unit) = {}
}

open class OuterRecorder(
    outFile : File,
    rate : Float = 48000f,
    sizeInBits : Int = 16,
    channels : Int = 2
) : Recorder(outFile) {
    override val format: AudioFormat = AudioFormat(rate, sizeInBits, channels, true, true)
    override val line: TargetDataLine = AudioSystem.getTargetDataLine(format).apply { open(format) }
}

/**
 * PC の内部音声用のレコーダー
 *
 * PC側で事前の準備が必要
 *
 * コントロール パネル/ハードウェアとサウンド/サウンド ->
 * 録音タブ ->
 * 個別のデバイスが表示されていない空白部分で右クリック -> 無効なデバイスの表示をONに変更する ->
 * ステレオミキサーで右クリック -> 有効化する ->
 * ステレオミキサーでプロパティ -> レベルを調整する(100に設定する)
 * */
open class InnerRecorder(
    outFile : File,
    rate : Float = 48000f,
    sizeInBits : Int = 16,
    channels : Int = 2
) : Recorder(outFile) {
    override val format: AudioFormat = AudioFormat(rate, sizeInBits, channels, true, true)
    override val line: TargetDataLine = AudioSystem.getTargetDataLine(format, mixer).apply { open(format) }

    protected open val mixer : Mixer.Info get(){
        val lines = AudioSystem.getMixerInfo().filter {
            val mixer: Mixer = AudioSystem.getMixer(it)
            mixer.isLineSupported(Line.Info(TargetDataLine::class.java))
        }
        return lines[0]
    }
}
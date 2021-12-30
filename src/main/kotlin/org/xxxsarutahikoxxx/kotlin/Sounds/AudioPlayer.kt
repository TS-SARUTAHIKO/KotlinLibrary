package org.xxxsarutahikoxxx.kotlin.Sounds

import org.xxxsarutahikoxxx.kotlin.Utilitys.out
import java.io.File
import java.io.InputStream
import javax.sound.sampled.*
import javax.sound.sampled.FloatControl
import javax.swing.JButton
import javax.swing.JFrame
import kotlin.math.log10
import kotlin.math.min
import kotlin.math.pow


/**
 * Audio File の再生のためのクラス
 * */
open class AudioPlayer(
    protected val input : AudioInputStream
){
    constructor(infile : File) : this(AudioSystem.getAudioInputStream(infile))
    constructor(stream : InputStream) : this( { val input = AudioSystem.getAudioInputStream(stream) ; stream.close() ; input }.invoke() )

    val format = input.format
    protected val dataline = DataLine.Info(Clip::class.java, format)
    protected val clip = (AudioSystem.getLine(dataline) as Clip).apply {
        open(input)
        input.close()

        addLineListener {
            if( it.type == LineEvent.Type.STOP && framePosition == frameLength ){ run = false ; onFinish() }
        }
    }

    protected var run = false


    /** 再生を開始する */
    fun start(){
        if( ! run ){
            run = true
            clip.start()

            onStart()
        }
    }
    /**
     * 開始した場合のフック関数
     * */
    var onStart : ()->(Unit) = {}

    /** 再生を停止する */
    fun stop(){
        if( run ){
            run = false
            clip.stop()
            clip.flush()

            onStop()
        }
    }
    /**
     * 終了した場合のフック関数
     * */
    var onStop : ()->(Unit) = {}

    /**
     * 停止中なら[start] 再生中なら [stop] を実行します
     * */
    fun startOrStop(){
        if( run ) stop() else start()
    }

    /**
     * 正規に終了した時に呼ばれるフック用関数
     *
     * 正規の終了とは FramePosition と FrameLength が一致した状態で Stop が呼ばれた場合である
     *  */
    var onFinish : ()->(Unit) = {}

    /**
     * 再生を終了してメモリーを開放する
     * */
    fun close(){
        stop()
        clip.close()
    }

    /**
     * 再生位置をミリ秒で指定する
     *
     * 再生中の場合は Stop -> 位置指定 -> Start を実行する
     * */
    var position : Long
        get() = clip.microsecondPosition / 1000
        set(value) {
            if( run ){
                clip.stop()
                clip.flush()

                clip.framePosition = (format.frameRate * value / 1000).toInt()

                clip.start()
            }else{
                clip.framePosition = (format.frameRate * value / 1000).toInt()
            }
        }
    /**
     * ボリューム設定(デシベル)
     * */
    var volumeBD : Float
        get() = (clip.getControl(FloatControl.Type.MASTER_GAIN) as FloatControl).value
        set(value) {
            val value = min(value, (clip.getControl(FloatControl.Type.MASTER_GAIN) as FloatControl).maximum)

            (clip.getControl(FloatControl.Type.MASTER_GAIN) as FloatControl).value = value
        }
    /**
     * ボリューム設定(0～1.0～2.0)
     *
     * デフォルト = 1.0
     * */
    var volume : Double
        get() = 10.0.pow(volumeBD/20.0)
        set(value) { volumeBD = (log10(value)*20.0).toFloat() }


    /** 音声ファイルの演奏時間(msecond)  */
    val length : Long = clip.microsecondLength / 1000
}



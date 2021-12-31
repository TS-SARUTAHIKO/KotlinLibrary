package org.xxxsarutahikoxxx.kotlin.IORunner

import org.xxxsarutahikoxxx.kotlin.Utilitys.out
import java.io.*
import java.lang.RuntimeException
import java.nio.ByteBuffer
import kotlin.concurrent.thread

/**
 * 通信用の処理の共有部分の実装
 * write / read / onAccept などを行う
 *
 *
 * 接続の確立
 * host 側は open, client 側は connect を行うことで接続が確立する
 * isAutoReconnect = true の場合は切断エラー時(onDisconnected)に自動的に再接続を行う
 *
 * 接続時
 * host/client 共に接続が確立すると受信用のスレッドが起動して自動的にデータの受信を開始する
 *
 * データの送信
 * データの送信はシリアライズ可能なものは writeObject(obj: Serializable) で行う
 * シリアライズ不可能なものはバイトデータに変換してから writeData(byte: ByteArray) で行う
 *
 * データの受信時
 * host/client 共にデータを受信すると onAccept(obj : ByteArray, isSerializable : Boolean) 関数が呼ばれる
 * その後 シリアライズ可能かどうかで処理は分岐し onAccept(byte : ByteArray) か onAccept(obj : Serializable) が呼ばれる
 * 受信したメッセージを処理する場合は onAccept 関数をオーバーライドして実装する
 *
 * 通信の暗号化
 * データの送信時にデータオブジェクトをバイトに変換する処理は encoder
 * データの受信時にバイトをデータオブジェクトに変換する処理は decoder が行う
 * デフォルトでは encoder / decoder 共にただの ObjectStream だが
 * setConverter(encoder : (Serializable)->(ByteArray), decoder : (ByteArray)->(Serializable)) によって任意の関数を設定することで
 * 暗号化通信を行うことができる
 *
 * */
abstract class IORunner(
    /** モード Host or Client */
    val HostMode : Boolean,
    /** ポートが切断された場合の再設定処理  */
    var isAutoReconnect : Boolean,
    /** 接続時に読み取りスレッドを起動する */
    var isAutoReader : Boolean
){
    var hasConnection = false


    /**
     * オブジェクトを送信する際にバイト情報に変換する関数
     * 通常はただの ObjectOutputStream だが暗号化する祭には任意の関数を設定する
     * encoder / decoder は対応したものをペアで設定すること
     * */
    private var encoder : (Serializable)->(ByteArray) = { obj ->
        ByteArrayOutputStream().use {
            ObjectOutputStream(it).use {
                it.writeObject(obj)
            }
            it.toByteArray()
        }
    }
    /**
     * 受信したバイト情報をオブジェクトに変換する関数
     * 通常はただの ObjectInputStream だが暗号化する祭には任意の関数を設定する
     * encoder / decoder は対応したものをペアで設定すること
     * */
    private var decoder : (ByteArray)->(Serializable) = { byte ->
        ByteArrayInputStream(byte).use {
            ObjectInputStream(it).use {
                it.readObject() as? Serializable
            }
        } ?: RuntimeException("Accept Non-Serializable Object")
    }
    /**
     * 通信を暗号化する際にペアである encoder / decoder をセットする関数
     * */
    fun setConverter(encoder : (Serializable)->(ByteArray), decoder : (ByteArray)->(Serializable)){
        this.encoder = encoder
        this.decoder = decoder
    }

    /**
     * 送信データの作成
     * 最初の 1 byte はシリアライズの可否
     * 次の 4 byte はデータの長さ
     * その後にデータ本体
     * */
    private fun dataToBytes(bytes: ByteArray, isSerializable : Boolean=false) : ByteArray {
        return ByteBuffer.allocate( 1+4+bytes.size ).apply {
            put( if(isSerializable) 1 else 0 )
            putInt( bytes.size )
            put(bytes)
        }.array()
    }
    /**
     * 受信データの変換
     * */
    private fun bytesToData(bytes: ByteArray) : Pair<Boolean, ByteArray> {
        return ByteBuffer.wrap(bytes).run {
            val isSerializable = (get().toInt() == 1)

            val ret = ByteArray(int)
            get(ret)

            isSerializable to ret
        }
    }
    protected fun byteToHead(bytes : ByteArray) : Pair<Boolean, Int> {
        return ByteBuffer.wrap(bytes).run { (get().toInt() == 1) to int }
    }

    /** データ送信関数 シリアライズ可能なオブジェクト用 */
    fun writeObject(obj: Serializable){
        try{
            if( ! hasConnection ) throw NoConnectionException("NoConnection")

            writeData( dataToBytes(encoder(obj), true) )
        }catch (e : Exception){
            throw NoStreamException("No OutputStream")
        }
    }
    /** データ送信関数 シリアライズ不可能なオブジェクト用 */
    fun writeObject(byte: ByteArray){
        try{
            if( ! hasConnection ) throw NoConnectionException("NoConnection")

            writeData( dataToBytes(byte, false) )
        }catch (e : Exception){
            throw NoStreamException("No OutputStream")
        }
    }
     /** データの送信 */
    abstract fun writeData(byte: ByteArray)

    /** バイト列データの受信 */
    abstract fun readData() : ByteArray
    /** データの受信 */
    fun readObject() : Pair<Boolean, ByteArray> {
        return bytesToData(readData())
    }


    /**
     * 読み取り用のスレッドがデータを受信した際に最初に呼ばれる関数
     * シリアライズの可否で処理する関数を分ける役割を持つ
     * */
    protected open fun onAccept(obj : ByteArray, isSerializable : Boolean){
        if( isSerializable ){
            onAccept(decoder(obj))
        }else{
            onAccept(obj)
        }
    }
    /**
     * 読み取り用のスレッドがシリアライズ不可能なデータを受信した際に呼ばれる関数
     * */
    protected open fun onAccept(byte : ByteArray){
        out = "Accept NonSerializable Data"
    }
    /**
     * 読み取り用のスレッドがシリアライズ可能なデータを受信した際に呼ばれる関数
     * */
    protected open fun onAccept(obj : Serializable){
        out = "Accept Serializable : $obj"
    }

    abstract fun open()

    /** ホストとしてポートが開かれた場合の処理関数 */
    protected open fun onOpened(){
        hasConnection = true
        if( isAutoReader ) runOnThread = true
    }

    abstract fun connect()
    /** クライアントとしてポートに接続した場合の処理関数 */
    protected open fun onConnected(){
        hasConnection = true
        if( isAutoReader ) runOnThread = true
    }

    /**
     * ポートなどを閉じる処理
     * */
    open fun close(){
        hasConnection = false
    }
    /**
     * 接続が切れたときに呼ばれる関数
     *
     * [isAutoReconnect] = true の場合は 再オープン or 再接続 を行う
     * */
    protected open fun onDisconnected(e : Exception){
        out = "DisConnected : $e"

        close()

        if( ! isAutoReconnect )return

        if( HostMode ){
            open()
        }else{
            connect()
        }
    }

    /**
     * 読み取り用のスレッドの設定
     *
     * true がセットされた場合はスレッドを構築して走らせる、スレッドが異常終了した場合は false をセットして終了する
     *
     * false がセットされた場合は既に走っているスレッドが停止する
     * */
    var runOnThread = false
        @Synchronized
        set(value) {
            if( field == value )return

            field = value

            if( value )
            thread {
                try{
                    while ( runOnThread ){
                        val (isSerializable, byte) = readObject()

                        onAccept(byte, isSerializable)
                    }
                }catch (e : Exception){
                    field = false

                    onDisconnected(e)
                    e.printStackTrace()
                }
            }
        }


    inner class NoStreamException(msg : String) : IOException(msg)
    inner class NoConnectionException(msg : String) : IOException(msg)
}

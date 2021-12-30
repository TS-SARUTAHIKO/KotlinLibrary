package org.xxxsarutahikoxxx.kotlin.SocketRunner

import org.xxxsarutahikoxxx.kotlin.Utilitys.out
import java.io.*
import java.lang.Exception
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
abstract class SocketRunner(
    /** モード Host or Client */
    val HostMode : Boolean,
    /** ポートが切断された場合の再設定処理  */
    var isAutoReconnect : Boolean,
    /** 接続時に読み取りスレッドを起動する */
    var isAutoReader : Boolean
){
    var hasConnection = false

    var input : InputStream? = null
    var output : OutputStream? = null

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


    /** データ送信関数 シリアライズ可能なオブジェクト用 */
    fun writeObject(obj: Serializable){
        writeData( encoder(obj) , true)
    }
    /** データ送信関数 シリアライズ不可能なオブジェクト用 */
    fun writeData(byte: ByteArray){
        writeData(byte, false)
    }
    /**
     * データ送信の実行部分
     * 最初の 4 byte はデータの長さ
     * 次の 1 byte はシリアライズの可否
     * その後にデータ本体を送信する
     * */
    private fun writeData(byte: ByteArray, isSerializable : Boolean=false){
        if( ! hasConnection ) throw NoConnectionException("NoConnection")

        output?.run {
            val length  = byte.size

            val lBytes = ByteBuffer.allocate(4).putInt(length).array()

            write(lBytes)
            write( if(isSerializable) 1 else 0 )
            write(byte)

            flush()

            return
        }

        throw NoStreamException("No OutputStream")
    }

    /** 指定した長さ分のバイトデータを読み込む */
    private fun readData(size : Int) : ByteArray {
        if( ! hasConnection ) throw NoConnectionException("NoConnection")

        input?.run {
            val ret = ByteArray(size)

            var num = 0
            while( num < size ){
                num += read(ret, num, size-num)
            }

            return ret
        }

        throw NoStreamException("No InputStream")
    }
    /**
     * データを受信する
     *
     * Pair< シリアライズ可能, データ >　の形式で返却される
     * */
    fun readData() : Pair<Boolean, ByteArray> {
        if( ! hasConnection ) throw NoConnectionException("NoConnection")

        input?.run {
            val b : ByteArray = readData(4)

            val length = ByteBuffer.wrap(b).int

            val isSerializable = read()

            return Pair(isSerializable==1, readData(length))
        }

        throw NoStreamException("No InputStream")
    }
    /**
     * 受信するオブジェクトがシリアライズ可能と仮定して受信処理を行う
     * */
    fun readObject() : Serializable {
        val (obj, byte) = readData()

        if( obj ){
            return decoder(byte)
        }else{
            throw RuntimeException("Accept Non-Serializable Object")
        }
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
    protected fun onOpened(inStream : InputStream, outStream : OutputStream){
        out = "Opened"

        hasConnection = true
        input = inStream
        output = outStream

        if( isAutoReader ) runOnThread = true
        onOpened()
    }
    /**
     * [onOpened]のフック用関数
     *
     * デフォルトでは "Hello Client !"と送信する
     * */
    protected open fun onOpened(){
        writeObject("Hello Client !")
    }

    abstract fun connect()
    /** クライアントとしてポートに接続した場合の処理関数 */
    protected fun onConnected(inStream : InputStream, outStream : OutputStream){
        out = "Connected"

        hasConnection = true
        input = inStream
        output = outStream

        if( isAutoReader ) runOnThread = true
        onConnected()
    }
    /**
     * [onConnected]のフック用関数
     *
     * デフォルトでは "Hello Server !"と送信する
     * */
    protected open fun onConnected(){
        writeObject("Hello Server !")
    }

    /**
     * ポートなどを閉じる処理
     * */
    open fun close(){
        hasConnection = false

        try{ input?.close() }catch (e : Exception){}
        try{ output?.close() }catch (e : Exception){}
    }
    /**
     * 接続が切れたときに呼ばれる関数
     *
     * [isAutoReconnect] = true の場合は 再オープン or 再接続 を行う
     * */
    protected open fun onDisconnected(e : Exception){
        out = "DisConnected"

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
                        val (isSerializable, byte) = readData()

                        onAccept(byte, isSerializable)
                    }
                }catch (e : Exception){
                    field = false

                    onDisconnected(e)
                }
            }
        }


    inner class NoStreamException(msg : String) : IOException(msg)
    inner class NoConnectionException(msg : String) : IOException(msg)
}

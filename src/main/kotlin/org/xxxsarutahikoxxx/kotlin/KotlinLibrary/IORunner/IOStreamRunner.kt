package org.xxxsarutahikoxxx.kotlin.KotlinLibrary.IORunner

import java.io.InputStream
import java.io.OutputStream
import java.lang.Exception
import java.nio.Buffer
import java.nio.ByteBuffer

abstract class IOStreamRunner(
    /** モード Host or Client */
    HostMode : Boolean,
    /** ポートが切断された場合の再設定処理  */
    isAutoReconnect : Boolean,
    /** 接続時に読み取りスレッドを起動する */
    isAutoReader : Boolean
) : IORunner(HostMode, isAutoReconnect, isAutoReader) {

    var input : InputStream? = null
    var output : OutputStream? = null

    override fun writeData(bytes: ByteArray) {
        output?.run {
            write(bytes)
            flush()

            return
        }

        throw NoStreamException("No OutputStream")
    }
    override fun readData() : ByteArray {
        input?.run {
            val b1 = readNBytes(1)
            val b2 = readNBytes(4)

            val size = ByteBuffer.wrap(b2).int
            val b3 = readNBytes(size)

            return b1 + b2 + b3
        }

        throw NoStreamException("No InputStream")
    }
    private fun InputStream.readNBytes(size : Int) : ByteArray {
        val bytes = ByteArray(size)
        var done = 0

        while( done < size ){
            done += read(bytes, done, size-done)
        }
        return bytes
    }

    /** ホストとしてポートが開かれた場合の処理関数 */
    protected open fun onOpened(inStream : InputStream, outStream : OutputStream){
        input = inStream
        output = outStream

        onOpened()
    }
    /** クライアントとしてポートに接続した場合の処理関数 */
    protected open fun onConnected(inStream : InputStream, outStream : OutputStream){
        input = inStream
        output = outStream

        onConnected()
    }

    override fun close() {
        super.close()

        try{ input?.close() }catch (e : Exception){}
        try{ output?.close() }catch (e : Exception){}
    }
}
package org.xxxsarutahikoxxx.kotlin.RSInvocation

import java.io.ObjectOutputStream
import java.io.Serializable


/**
 * RSI 関数の転送を行うためのインターフェース
 *
 * [ObjectOutputStream] などへの中継程度の役割を担う
 *
 * [onAccept] は [RSIPort] により上書きされます
 * */
interface RSIExporter {
    fun exportRSI(rsi : RSInvocation<*>)
}
/**
 * RSI 関数の受信を [RSIPort] へ転送するためのインターフェース
 *
 * [RSIPort] にセットすると [onAccept] が [RSIPort] によって上書きされる
 *
 * このインターフェースを実装したオブジェクトは [RSInvocation] を受信した際に[onAccept]を呼び出すように実装すること
 * */
interface RSIAccepter {
    var onAccept : ((RSInvocation<*>)->(Unit))?
}

/**
 * RSI 転送の処理を行うポート
 *
 * RSI 関数がネストされていた場合にネストされた子関数が二重実行されることを防ぐための処理を行う
 * */
interface RSIPort : hasRSIPort, Serializable {
    override val rsiPort: RSIPort get() = this

    var portID : String

    var exporter : RSIExporter?
    var accepter : RSIAccepter?

    /** RSI を処理中のスレッド名のリスト */
    val threads : MutableList<String>

    /**
     * [RSInvocation] を処理する
     *
     * [prefix] 処理を行った後に関数をローカルで実行する
     *
     * 現行のスレッドが既にRSI関数が処理中ならばローカルで実行する
     * */
    fun <Ret> parseRSI(rsi : RSInvocation<Ret>, prefix : RSIPort.()->(Unit) = {}) : Ret {
        val name = Thread.currentThread().name

        return if( name !in threads ){
            threads.add(name)
            prefix()
            val ret : Ret = rsi()
            threads.remove(name)
            ret
        }else{
            rsi()
        }
    }
    /**
     * [RSInvocation] を処理する
     *
     * クライアント側(もしくはホスト側)に転送した後にローカルで実行する
     *
     * 現行のスレッドが既にRSI関数を処理中ならばローカルで実行する
     * */
    fun <Ret> writeRSI(rsi : RSInvocation<Ret> ) : Ret {
        return parseRSI(rsi){ exporter?.exportRSI(rsi) }
    }

    fun nextID() : String

    fun writeReplace(): Any? {
        return RSIPortPointer(portID)
    }
}

/**
 * RSI 転送の処理を行うポート
 *
 * RSI 関数がネストされていた場合にネストされた子関数が二重実行されることを防ぐための処理を行う
 * */
open class RSIPortImpl(
    exporter : RSIExporter?,
    accepter : RSIAccepter?,
    portID : String
) : RSIPort {
    override var portID: String
        get() = RSIPortPointer.id(this)
        set(value) { RSIPortPointer[value] = this }

    override var exporter : RSIExporter? = exporter
    override var accepter : RSIAccepter? = accepter
        set(value) {
            field = value
            field?.onAccept = { parseRSI(it) }
        }

    override val threads : MutableList<String> = mutableListOf()

    var id = 0
    override fun nextID(): String = "${ id ++ }"

    init {
        this.portID = portID
    }
}


/**
 * RSI 転送ポートへの参照を持つことを示すインターフェース
 *
 * [rsiExport] 関数を用いて RSI 関数を記述するために用いる
 * */
interface hasRSIPort {
    val rsiPort : RSIPort

    fun <Ret> rsiExport(func:()->(Ret) ) : Ret {
        return rsiPort.writeRSI(func.rsi())
    }
}

/**
 * グローバルな[RSIPort]
 *
 * [RSIPort.exporter] に有効な送信ポートを設定することでグローバルに送信できる
 *
 * [RSIPort.accepter] に有効な受信ポートを設定することでグローバルに受信できる
 * */
object gp : RSIPortImpl(null, null, "Global RSIPort")

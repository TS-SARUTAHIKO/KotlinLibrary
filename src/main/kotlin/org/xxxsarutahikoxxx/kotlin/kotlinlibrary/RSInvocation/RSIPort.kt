package org.xxxsarutahikoxxx.kotlin.kotlinlibrary.RSInvocation

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
interface RSIAcceptor {
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
    var acceptor : RSIAcceptor?

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
    accepter : RSIAcceptor?,
    portID : String
) : RSIPort {
    override var portID: String
        get() = RSIPortPointer.id(this)
        set(value) { RSIPortPointer[value] = this }

    override var exporter : RSIExporter? = exporter
    override var acceptor : RSIAcceptor? = accepter
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
 * [RSIPort] を継承したインスタンスを指し示す参照子
 *
 * [RSIPort] を一意に特定するためのID管理も行う
 *
 * TODO : 通信している RSIPort は送信・受信の双方で同じIDを持つように設定する必要がある
 * 現状では gp に `Global RSIPort` を設定することで通信しているが追加ポートのIDを一致させる仕組みは実装していない
 * */
class RSIPortPointer(private val id : String) : Serializable {
    /**
     * デシリアライズされる際は代わりに対応する {RSISerializable} を検索して渡す
     * */
    fun readResolve() : Any? {
        return RSIPortPointer.value(id)
    }

    companion object {
        /** <ID, RSISerializable> のマップ */
        private val portMap : MutableMap<RSIPort, String> = mutableMapOf()

        /** [RSIPort] に対応した ID を取得する */
        fun id(port : RSIPort) : String = portMap[port]!!
        /** id に対応した [RSIPort] を取得する */
        fun value(id : String) : RSIPort = portMap.toList().first { it.second == id }.first

        /** [RSIPort] を ID で登録する */
        operator fun set(id : String, value : RSIPort){
            portMap[value] = id
        }
    }
}

/**
 * グローバルな[RSIPort]
 *
 * [RSIPort.exporter] に有効な送信ポートを設定することでグローバルに送信できる
 *
 * [RSIPort.acceptor] に有効な受信ポートを設定することでグローバルに受信できる
 * */
object gp : RSIPortImpl(null, null, "Global RSIPort")


# RSInvocation（Remote Synchronized Invocation） Package

Kotlin で RSI（Remote Synchronized Invocation） を行うためのパッケージ

RMI（Remote Method Invocation）とは違いホストとクライアントに同じ状態やインスタンスを作ることを目的とする<br>
（RMIではクライアント側のインスタンスはプロキシを用いた通信用の仮想インスタンス、関数実行時には通信が発生する）


# 使用例

最も簡単な実装例を以下に示す<br>
ただし以下の例では既にグローバルに定義された RSIPort (gp) が適切に接続されているものとする

```kotlin.kt
// 通常のグローバルな関数
fun makeNameLabel(name : String, age : Int) : String {
    return "Name : $name,  Age : $age"
}

// RSI に対応したグローバルな関数
fun MakeNameLabel(name : String, age : Int) : String = gp.rsiExport {
    "Name : $name,  Age : $age"
}
```

違いは `= gp.rsiExport` の部分と関数定義がラムダ式になったことである（`return`が使用できない）

関数を呼び出す側はそれがRSI関数であるかどうかは意識する必要がない

冗長な表現ではあるが無名関数を用いれば`return`を用いることができる

```kotlin.kt
fun MakeNameLabel_withReturn(name : String, age : Int) : String = gp.rsiExport( 
    fun() : String {
        return "Name : $name,  Age : $age"
    }
)
```


# 値渡し・参照渡し

送信できる関数に関して、値渡しされるものは`Serializable`である必要がある<br>
参照渡しされるものに関しては`Serializable`である必要はない（受信側の値が使用される）

引数は常に値で渡される、よって`Serializable`である必要がある

レシーバー・ディスパッチレシーバーは通常のクラスの場合は値で渡される<br>
シングルトンである場合、つまり`オブジェクト式で作られたシングルトン`や`クラスのコンパニオン・オブジェクト`の場合は参照で渡される

関数の外の変数は参照で渡される


参照渡しで渡されるケースを以下の表に示す

|      |  クラス  |  シングルトン  |
| ---- | ---- | ---- |
|  引数  |  ×  |  ×  |
|  レシーバー  |  ×  |  〇  |
|  外部変数  |  〇  |  〇  |

クラス<br>
 → 通常のクラス・インスタンス

シングルトン<br>
 → オブジェクト式やコンパニオン・オブジェクトなどで作られたシングルトンであるインスタンス


## クラス

以下のクラス関数 `function` を実行すると java.io.NotSerializableException が発生する<br>
理由はレシーバーである Human インスタンスを値渡しする必要がある（`name` `age`を参照するため）が クラスが`Serializable`でないからである

```kotlin.kt
class Human(val name : String, val age : Int){
    fun function() = gp.rsiExport {
        out = "Name : $name,  Age : $age"
    }
}
```

以下のようにクラスを`Serializable`にすることでエラーは回避できる<br>
ただし Human インスタンスは実体渡しとして送信されるために、受信側では Human インスタンスが新たに生成されていることに注意すること

事前に作成した同一インスタンスに対して処理を行うためには参照渡しを実装する必要がある<br>
（readResolve()メソッドとwriteReplace()メソッドを用いて実体渡しの代わりに参照渡しを実装することになるだろう）

```kotlin.kt
class Human(val name : String, val age : Int) : Serializable {
    // 省略
}
```

## 外部変数 / object 式 / Companion オブジェクト

以下の関数で用いている`country` `name` は参照渡しで渡される<br>
受信側で値を書き換えていた場合は関数を実行する際にその値が使用される


```kotli.kt
var country = "America"

object Singleton {
    var name = "Tom"

    fun function(age : Int) = gp.rsiExport {
        out = "country : $country,  Name : $name,  Age : $age"
    }
}

class Human(){
    companion object {
        val name = "Jon"

        fun function(age : Int) = gp.rsiExport {
            out = "Name : $name,  Age : $age"
        }
    }
}
```


# RSIPort

RSIPort は RSInvocation を送受信するためのポートである<br>
サーバー・クライアント間の情報伝達はこのポートを用いて行われる<br>


## グローバル・ポートの構築例

グローバル・ポートはグローバルネームスペースに定義された`RSIPort`である<br>
システムが１対１で運用することを前提で用いる、グローバルネームスペースを汚染することに注意すること<br>

以下では `HostWebRunner` `ClientWebRunner` を用いたグローバルポートの構築例を示す

要点は以下の通りである

・`RSIExporter, RSIAccepter` を継承する<br>
・自身を`gp.exporter` `gp.accepter`に登録する<br>
・`exportRSI(rsi: RSInvocation<*>)`関数は自身の書き込み関数`writeObject(rsi)` に転送する<br>
・自身の`onAccept(obj: Serializable)`が呼ばれたとき、それが`RSInvoccation`ならば`onAccept: ((RSInvocation<*>) -> Unit)?`に転送する<br>
・ポートの待ち受け(open) or ポートの接続(connect)を行う


ホスト側

```host.kt
    object : HostWebRunner(), RSIExporter, RSIAccepter {
        init {
            gp.exporter = this
            gp.accepter = this
        }

        override fun exportRSI(rsi: RSInvocation<*>) = writeObject(rsi)
        override var onAccept: ((RSInvocation<*>) -> Unit)? = null

        override fun onAccept(obj: Serializable) {
            super.onAccept(obj)

            if( obj is RSInvocation<*> )onAccept?.invoke(obj)
        }
    }.open()
```

クライアント側

```client.kt
    object : ClientWebRunner(), RSIExporter, RSIAccepter {
        init {
            gp.exporter = this
            gp.accepter = this
        }

        override fun exportRSI(rsi: RSInvocation<*>) = writeObject(rsi)
        override var onAccept: ((RSInvocation<*>) -> Unit)? = null

        override fun onAccept(obj: Serializable) {
            super.onAccept(obj)

            if( obj is RSInvocation<*> )onAccept?.invoke(obj)
        }
    }.connect()
```


# RSInvocation 可能なクラス``

RSI転送可能なクラスのサンプル・クラス

要点は以下の通りである

・クラスは RSISerializable を継承する（`RSIID`で一意に特定可能になる）<br>
・ hasRSIPort を port に委譲する（クラス内部でレシーバー無しの`rsiExport{ }`が使用可能になる）<br>
・クラスのインスタンスはコンストラクタではなく RSInvocation に対応したファクトリ関数で作る<br>
・ファクトリ関数で実行される関数部分（`rsiExport{  } 内部`）で`RSIID`を設定する

```kotlin.kt
class Test private constructor(val name : String, port : RSIPort) : RSISerializable, hasRSIPort by port {

    fun parent(arg1 : String, arg2 : Int) = rsiExport {
        out = "ARG1 : $arg1"
        child(arg2)
    }
    fun child(age : Int) = rsiExport {
        out = "Name : $name,  Age : $age"
    }

    companion object {
        @JvmStatic private val serialVersionUID: Long = 1L

        private fun of(name : String, port : RSIPort, id : String) : Test = port.rsiExport {
            Test(name, port).apply { RSIID = id }
        }
        fun of(name : String) : Test = of(name, gp, gp.nextID())
    }
}
```

Testクラスをファクトリ関数で作成すると双方で `Test(name).apply { RSIID = id }` が実行される<br>
結果的に双方で新たなインスタンスが作成されそのインスタンスが`id`で登録される

package org.xxxsarutahikoxxx.kotlin.KotlinLibrary.Span

import org.xxxsarutahikoxxx.kotlin.KotlinLibrary.Utilitys.out


interface Span

/**
 * テキストの構造解析用のクラス
 *
 * 特定のマークの組み合わせで囲まれた部分を階層的に切り出して構造化して保存するためのクラス
 * */
class ListSpan(open : Char?, close : Char?) : Span {
    internal var _open = open
    internal var _close = close
    val open get() = _open
    val close get() = _close

    /** open-close の修飾子を変更する */
    fun setModifier( mod : Pair<Char?, Char?> ){
        _open = mod.first
        _close = mod.second
    }


    internal var _spans : MutableList<Span> = mutableListOf()
    val spans : List<Span> get() = _spans.toList()

    /** 子要素の中で[ListSpan]であるもののリストを返す */
    val listSpans get() = spans.filterIsInstance(ListSpan::class.java).toList()
    /** 子要素の中で[TextSpan]であるもののリストを返す */
    val textSpans get() = spans.filterIsInstance(TextSpan::class.java).toList()

    /** 内包する全ての[Span]を再帰的に検索してリスト化して返す */
    val allSpans : List<Span> get(){
        return spans.map {
            when(it){
                is ListSpan -> listOf(it) + it.allSpans
                is TextSpan -> listOf(it)
                else -> listOf()
            }
        }.flatten()
    }

    /**  */
    internal fun parse( resource : String, index : Int, decorations : Map<Char, Char>) : Int {
        // 現在の検査位置
        var index = index
        // テキスト開始位置
        var tStart = index

        // open-Modifier リストの作成
        val opens = decorations.keys

        loop@ while( index < resource.length ){
            when( resource[index] ){
                close -> {
                    // テキスト部分を切り出して保存する
                    val text = resource.substring(tStart, index)
                    if( text.isNotEmpty() )_spans.add( TextSpan(text) )

                    // 処理を終了する
                    index++
                    tStart = index
                    break@loop
                }
                in opens -> {
                    // テキスト部分を切り出して保存する
                    val text = resource.substring(tStart, index)
                    if( text.isNotEmpty() )_spans.add( TextSpan(text) )

                    // 新たなSpanを生成して処理する
                    val span = ListSpan(resource[index], decorations[resource[index]]!!)
                    _spans.add(span)
                    index = span.parse(resource, index+1, decorations)
                    tStart = index
                }
                else -> { index++ }
            }
        }

        val text = resource.substring(tStart, index)
        if( text.isNotEmpty() )_spans.add( TextSpan(text) )

        return index
    }

    /**  */
    override fun toString(): String = ""+ (open?:"") + spans.joinToString("") + (close?:"")

    companion object {
        /** [parse]で用いられるデフォルトの組み合わせ */
        val Decorations = mutableMapOf(
                '(' to ')',
                '[' to ']',
                '{' to '}',
                '<' to '>',
                '〔' to '〕',
                '《' to '》',
                '〈' to '〉',
                '［' to '］',
                '【' to '】'
        )

        /**
         * [text]を指定されたデコレーターペア[decorations]を用いて解析する
         *
         * [decorations]を指定しない場合はデフォルト値[Decorations]が用いられる
         *  */
        fun parse(text : String, decorations : Map<Char, Char> = Decorations) : ListSpan {
            return ListSpan(null, null).apply {
                this.parse(text, 0, decorations)
            }
        }

        /** [parse] を実行して [ListSpan] を作成した後に [decorations] で区切られていない [TextSpan] だけを連結して返す */
        fun noDecorations(text : String, decorations : Map<Char, Char> = Decorations) : String {
            return parse(text).textSpans.joinToString("")
        }
    }
}
/** テキストのみを持つ[Span] */
class TextSpan(var text : String) : Span {
    override fun toString(): String = text
}


fun main(args: Array<String>) {
    val text = "xxx_[ pre <main> post ]_yyy_(marker)[a[b]c]"
    var listSpan = ListSpan.parse(text)

    out = listSpan.spans[0] // TextSpan text="xxx_"
    out = listSpan.spans[1] // ListSpan open='[', close=']'
    out = listSpan.spans[2] // TextSpan text="_yyy_"
    out = listSpan.spans[3] // ListSpan open='(', close=')'
    out = listSpan.spans[4] // ListSpan open='[', close=']'

    out = (listSpan.spans[1] as ListSpan).spans[0] // TextSpan text=" pre "
    out = (listSpan.spans[1] as ListSpan).spans[1] // ListSpan open='<' close='>'
    out = (listSpan.spans[1] as ListSpan).spans[2] // TextSpan text=" post "

    out = (listSpan.spans[3] as ListSpan).spans[0] // TextSpan text="marker"

    out = (listSpan.spans[4] as ListSpan).spans[0] // TextSpan text="a"
    out = (listSpan.spans[4] as ListSpan).spans[1] // ListSpan open='[' close=']'
    out = (listSpan.spans[4] as ListSpan).spans[2] // TextSpan text="c"


    // 解析に使用する装飾のペアを指定して解析する
    listSpan = ListSpan.parse("xxx < active [not active] > xxx", mapOf('<' to '>'))

    // デフォルトで使用される装飾のペアに '/' to '/' を追加する
    // 以降の parse() では '/' to '/' が使用されるようになる
    ListSpan.Decorations['/'] = '/'

    // 装飾を取り除いたテキスト部分
    out = ListSpan.noDecorations(text) // xxx__yyy_
}
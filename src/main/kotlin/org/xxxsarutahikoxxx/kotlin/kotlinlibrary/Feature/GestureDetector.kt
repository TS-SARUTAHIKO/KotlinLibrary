package org.xxxsarutahikoxxx.kotlin.kotlinlibrary.Feature

import org.xxxsarutahikoxxx.kotlin.kotlinlibrary.Utilitys.out
import java.awt.Rectangle
import java.awt.event.MouseAdapter
import java.awt.event.MouseEvent
import java.awt.event.MouseMotionAdapter
import javax.swing.JFrame
import kotlin.math.PI
import kotlin.math.atan2
import kotlin.math.floor
import kotlin.math.hypot


/**
 * 入力された点列を4/8/16方向のジェスチャーアクションとして解釈し処理するクラス
 *
 * 4/8/16 方向用の実装 [GestureDetectorD4] [GestureDetectorD8] [GestureDetectorD16] を用いること
 *
 * [distinct] = true である場合は同じ方向が連続した場合は連結される
 *
 * 点列を[parse]で順に追加していき[anchor]から[margin]以上の距離が離れた時点で方向を判定して[anchor]も更新する
 *
 * [complete]を呼ぶと保存した方向や[anchor]を初期化して、登録された条件-アクションに最初に合致したものを実行する
 * */
abstract class GestureDetector<Dir : GestureDetector.Direction>(private val margin : Float, val distinct : Boolean) {

    // 判定保存された方向とその処理・取得
    /** 現在の判定されて保存された方向列 */
    private val directions : MutableList<Direction16> = mutableListOf()
    /** 方向をクラスごとの[Dir]に合わせて取得する。[distinct]=true であるなら冗長性を除去する。 */
    fun directions() : List<Dir> {
        return directions.map { it.convertTo() }.run { if( distinct ) distinct() else this }
    }
    /** 冗長性を除去する */
    private fun List<Dir>.distinct() : List<Dir> {
        val ret = mutableListOf<Dir>()

        forEach { if( it != ret.lastOrNull() ) ret.add(it) }

        return ret
    }
    /** [Direction16]で保存された方向を自クラスに合わせた方向に変換する */
    abstract fun Direction16.convertTo() : Dir


    // 入力点の追加[parse]と終了処理[complete]
    /** 方向を判定するためのアンカーポイント */
    private var anchor : Pair<Float, Float>? = null
    /** 入力された差分値を方向に変換する */
    private fun toDirection(x : Float, y : Float) : Direction16 {
        return Direction16.values()[(16 + floor(atan2(y, x) / unit16).toInt()) % 16 ]
    }

    /**
     * 新たな入力点をもとに状態を更新する
     *
     * [anchor]が無いなら[point]をアンカーとして登録する
     *
     * [point]が[anchor]と[margin]以上離れているならその方向を判定して登録し[anchor]を[point]で更新する
     * */
    fun parse(point : Pair<Float, Float>){
        if( anchor == null ){
            anchor = point
            return
        }else {
            val dx = point.first-anchor!!.first
            val dy = point.second-anchor!!.second

            if( hypot(dx, dy) >= margin ){
                directions.add( toDirection( dx, dy ) )
                anchor = point
            }
        }
    }
    /** 入力処理を終了して対応したアクションを実行する。既存の入力は初期化される。 */
    fun complete() : List<Dir> {
        val dir = directions()
        directions.clear()
        anchor = null

        for( (condition, action) in actions ){
            if( condition(dir) ){
                action(dir)
                return dir
            }
        }
        return dir
    }


    // [complete]の際に実行されるアクションを登録する
    /** 登録された方向条件とアクションのマップ */
    private val actions : LinkedHashMap<(List<Dir>)->(Boolean), (List<Dir>)->(Unit)> = LinkedHashMap()
    /** 方向条件とアクションを登録する */
    fun putAction( condition : (List<Dir>)->(Boolean), action : (List<Dir>)->(Unit) ){
        actions[condition] = action
    }
    /** 方向条件とアクションを登録する */
    fun putAction(directionsCodeRegex : String, action : (List<Dir>)->(Unit) ){
        val regex = directionsCodeRegex.toRegex()
        val condition = { it : List<Dir> -> it.joinToString(""){""+it.code}.matches(regex) }
        actions[ condition ] = action
    }


    //
    interface Direction {
        val code : Char
    }

    enum class Direction4(override val code : Char) : Direction {
        North('↑'), South('↓'), East('→'), West('←')
    }
    enum class Direction8(override val code : Char) : Direction {
        North('↑'), South('↓'), East('→'), West('←'),
        NorthEast('↗'), NorthWest('↖'), SouthEast('↘'), SouthWest('↙')
    }
    enum class Direction16(val direction4: Direction4, val direction8: Direction8, override val code: Char) : Direction {
        EastSouth1(Direction4.East, Direction8.East, '0'),
        EastSouth2(Direction4.East, Direction8.SouthEast, '1'),
        SouthEast2(Direction4.South, Direction8.SouthEast, '2'),
        SouthEast1(Direction4.South, Direction8.South, '3'),
        SouthWest1(Direction4.South, Direction8.South, '4'),
        SouthWest2(Direction4.South, Direction8.SouthWest, '5'),
        WestSouth2(Direction4.West, Direction8.SouthWest, '6'),
        WestSouth1(Direction4.West, Direction8.West, '7'),
        WestNorth1(Direction4.West, Direction8.West, '8'),
        WestNorth2(Direction4.West, Direction8.NorthWest, '9'),
        NorthWest2(Direction4.North, Direction8.NorthWest, 'a'),
        NorthWest1(Direction4.North, Direction8.North, 'b'),
        NorthEast1(Direction4.North, Direction8.North, 'c'),
        NorthEast2(Direction4.North, Direction8.NorthEast, 'd'),
        EastNorth2(Direction4.East, Direction8.NorthEast, 'e'),
        EastNorth1(Direction4.East, Direction8.East, 'f')
    }

    companion object {
        private const val unit16 = PI/8
    }
}

class GestureDetectorD16(margin : Float, distinct : Boolean) : GestureDetector<GestureDetector.Direction16>(margin, distinct){
    override fun Direction16.convertTo(): Direction16 = this
}
class GestureDetectorD8(margin : Float, distinct : Boolean) : GestureDetector<GestureDetector.Direction8>(margin, distinct){
    override fun Direction16.convertTo(): Direction8 = this.direction8
}
class GestureDetectorD4(margin : Float, distinct : Boolean) : GestureDetector<GestureDetector.Direction4>(margin, distinct){
    override fun Direction16.convertTo(): Direction4 = this.direction4
}


fun main() {
    val dg = GestureDetectorD4(30f, true).apply {
        putAction( ".+" ){ out = it }
    }

    JFrame().apply {
        defaultCloseOperation = JFrame.EXIT_ON_CLOSE
        bounds = Rectangle(200, 200, 1200, 800)

        addMouseMotionListener(object : MouseMotionAdapter(){
            override fun mouseDragged(e: MouseEvent) {
                dg.parse(e.x.toFloat() to e.y.toFloat())
            }
        })
        addMouseListener(object : MouseAdapter(){
            override fun mouseReleased(e: MouseEvent?) {
                dg.complete()
            }
        })

        isVisible = true
    }
}
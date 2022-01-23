package org.xxxsarutahikoxxx.kotlin.kotlinlibrary.Feature

import javafx.beans.binding.Bindings
import javafx.beans.binding.StringBinding
import javafx.beans.property.SimpleObjectProperty
import javafx.scene.control.Labeled
import org.xxxsarutahikoxxx.kotlin.kotlinlibrary.Utilitys.out
import java.io.File
import java.net.URLClassLoader
import java.util.*


/**
 * [locale]に対応して変化する文字列[StringBinding]を作成するクラス
 *
 * 作成した[StringBinding]は[locale]が変更されるたびに[locale]に対応した[ResourceBundle]からキーを用いて取得した値が設定される
 *
 * ※ 取得する[ResourceBundle]は[dirs]・[fileName]・[locale]決定される
 *
 * ※ [locale]に設定された値は全ての[BundleProperties]を継承したクラスで共有される、
 * 又[Locale.setDefault]により[Locale]のデフォルト値に設定される
 *
 * --- How to Use ---
 *
 * --- resources/LabeledProperties.properties ファイルに以下の行を追加する
 *
 * nameLabel=Tom
 *
 * --- resources/LabeledProperties_ja_JP.properties ファイルに以下の行を追加する
 *
 * nameLabel=トム
 *
 * --- main.kt
 *
 * val nameLabel : Label (id="nameLabel")
 *
 * nameLabel.bind() // 表示する文字列をバインドする
 *
 * LabeledProperties.locale = Locale.JAPAN // 言語を変更する ラベルの表示はトムになる
 *
 * LabeledProperties.locale = Locale.GERMAN // 言語を変更する 対応するファイルが無いのでデフォルトが使用されラベルの表示はTomになる
 *
 * */
open class BundleProperties(private val fileName : String, private vararg val dirs : File) {
    companion object Common {
        private val localProperty = SimpleObjectProperty(Locale.getDefault())

        var locale : Locale
            get() = localProperty.value
            set(value) {
                Locale.setDefault(value)
                localProperty.value = value
            }
    }
    /** [Common.locale]への中継関数 */
    var locale : Locale
        get() = Common.locale
        set(value) { Common.locale = value }

    /** [ResourceBundle]のマップ */
    private val resourcesMap = mutableMapOf<Locale?, ResourceBundle>()
    /** [locale]に対応した[ResourceBundle]を読み込んでマッピングに保存する */
    private fun loadBundle(locale : Locale? = null) : ResourceBundle {
        return try{
            resourcesMap.getOrPut(locale){ ResourceBundle.getBundle(fileName, locale?: Locale(""), URLClassLoader( dirs.map { it.toURI().toURL() }.toTypedArray() )) }
        }catch (e : Exception){
            resourcesMap[null] ?: throw java.lang.Exception("${dirs.toList()} / $fileName .properties don't exist")
        }
    }
    /** [locale]に対応したリソース用ファイルが存在するかどうか */
    private fun isSupported(locale : Locale) : Boolean {
        loadBundle(locale)
        return resourcesMap.containsKey(locale)
    }

    /** [locale]に対応して変動する[StringBinding]を作成する */
    fun newProperty(id : String) : StringBinding {
        return Bindings.createStringBinding( { ->
            loadBundle(locale).getString(id) ?: loadBundle(null).getString(id) ?: id.split(".").last()
        }, localProperty)
    }
}


object LabeledProperties : BundleProperties("LabeledProperties", File("resources"))

fun Labeled.bind(){
    if( id != null ) textProperty().bind( LabeledProperties.newProperty(id) )
}

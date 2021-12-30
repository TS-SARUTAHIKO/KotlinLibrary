package org.xxxsarutahikoxxx.kotlin.Feature

import org.xxxsarutahikoxxx.kotlin.Utilitys.out
import java.io.Serializable


/** ツリーを構築するための基本ノード */
interface TreeNode : Serializable {
    val nodeParams : TreeNodeParams
    var content : Any?
}

/** [tNode] の属性情報 */
interface TreeNodeParams : Serializable {
    var isExpanded : Boolean
    var parent : TreeNode?
    val children : MutableList<TreeNode>
}
/** [TreeNodeParams]の実装 */
class TreeNodeParamsImpl(override var isExpanded : Boolean, override var parent : TreeNode?, override val children : MutableList<TreeNode>) : TreeNodeParams

/**
 * ツリーの Root となるノード
 *
 * ノードに変更があった場合のリスナ機能を持つ。
 * ツリーのルートが必ずしもこれを継承したノードである必要はない
 * */
interface TreeRoot : TreeNode {
    /** ノードが開いた場合に呼ばれる */
    var onExpanded : TreeRoot.(node : TreeNode) -> (Unit)
    /** ノードが閉じた場合に呼ばれる */
    var onCollapsed : TreeRoot.(node : TreeNode) -> (Unit)
    /** ノードが追加される直前に呼ばれる */
    var onPreAdded : TreeRoot.(parent : TreeNode, node : TreeNode) -> (Unit)
    /** ノードが追加された直後に呼ばれる */
    var onAdded : TreeRoot.(parent : TreeNode, node : TreeNode) -> (Unit)
    /** ノードが除去される直前に呼ばれる */
    var onPreRemoved : TreeRoot.(parent : TreeNode, node : TreeNode) -> (Unit)
    /** ノードが除去された直後に呼ばれる */
    var onRemoved : TreeRoot.(parent : TreeNode, node : TreeNode) -> (Unit)
}


/** ノードの展開状態 */
val TreeNode.isExpanded : Boolean get() = nodeParams.isExpanded
/** ノードを展開する */
fun TreeNode.expand(){
    if( ! isExpanded ){
        nodeParams.isExpanded = true
        (root as? TreeRoot)?.let { it.onExpanded(it, this) }
    }
}
/** ノードを閉じる */
fun TreeNode.collapse(){
    if( isExpanded ){
        nodeParams.isExpanded = false
        (root as? TreeRoot)?.let { it.onCollapsed(it, this) }
    }
}
/** ノードの展開状態を変更する */
fun TreeNode.toggle(){
    if( isExpanded ) collapse() else expand()
}
/** 自身と全ての子ノードを展開する */
fun TreeNode.expandAll(){
    (listOf(this) + allChildren).filter { ! it.isExpanded }.forEach { it.expand() }
}
/** 自身と全ての子ノードを閉じる */
fun TreeNode.collapseAll(){
    (listOf(this) + allChildren).filter { it.isExpanded }.forEach { it.collapse() }
}

/** 親ノード */
val TreeNode.parent : TreeNode? get() = nodeParams.parent
/** 親ノードのリスト、直近の親ノードは最後尾。 */
val TreeNode.parents : List<TreeNode> get(){
    return parent?.run { val ret = mutableListOf<TreeNode>() ; ret.addAll(parents) ; ret.add(this) ; ret } ?: listOf()
}
/** ルートノード */
val TreeNode.root : TreeNode get() = parents.firstOrNull() ?: this
/** ルートから見たノードの階層。ルートノードの場合は0。 */
val TreeNode.layer : Int get() = parents.size

/** 子ノードのリスト */
val TreeNode.children : List<TreeNode> get() = nodeParams.children
/** 子ノードを追加する。デフォルトでは最後尾に追加する。 */
fun TreeNode.add(node : TreeNode, index : Int = children.size) : TreeNode {
    val root = root
    (root as? TreeRoot)?.let { it.onPreAdded(it, this, node) }

    nodeParams.children.add(index, node)
    node.nodeParams.parent = this

    (root as? TreeRoot)?.let { it.onAdded(it, this, node) }

    return this
}
/** 子ノードを除去する。 */
fun TreeNode.remove(node : TreeNode){
    val root = root
    (root as? TreeRoot)?.let { it.onPreRemoved(it, this, node) }

    nodeParams.children.remove(node)
    node.nodeParams.parent = null

    (root as? TreeRoot)?.let { it.onRemoved(it, this, node) }
}

/** 全ての子ノードを返す */
val TreeNode.allChildren : List<TreeNode> get(){
    return children.map { listOf(it) + it.allChildren }.flatten()
}
/** 全ての展開された子ノードを返す */
val TreeNode.expandedChildren : List<TreeNode> get(){
    return children.map { listOf(it) + if( it.isExpanded ){ it.expandedChildren }else{ listOf() } }.flatten()
}
/** 子ノードを持つか。持たないなら真を返す。 */
val TreeNode.isLeaf : Boolean get() = children.isEmpty()
/** 子ノードを持つか。持つなら真を返す。 */
val TreeNode.isBranch : Boolean get() = children.isNotEmpty()



/**
 * [tNode] のデフォルト実装を返す
 *
 * [childFactory] が指定されている場合はそれによって子となる [CONTENT] を取得して再帰的に子ノードを作成する
 *
 * [init] が指定されている場合は [init] によって返されるノードの初期化処理が行われる
 * */
fun <CONTENT : Any?> tRoot(content : CONTENT, childFactory : (CONTENT)->(List<CONTENT>) = { listOf() }, init : TreeRoot.()->(Unit) = {} ) : TreeRoot {
    return object  : TreeRoot {
        override val nodeParams: TreeNodeParams = TreeNodeParamsImpl(false, null, mutableListOf())
        override var content : Any? = content

        override var onExpanded: TreeRoot.(node: TreeNode) -> Unit = {}
        override var onCollapsed: TreeRoot.(node: TreeNode) -> Unit = {}
        override var onPreAdded: TreeRoot.(parent : TreeNode, node: TreeNode) -> Unit = { _,_ ->}
        override var onAdded: TreeRoot.(parent : TreeNode, node: TreeNode) -> Unit = { _,_ ->}
        override var onPreRemoved: TreeRoot.(parent : TreeNode, node: TreeNode) -> Unit = { _,_ ->}
        override var onRemoved: TreeRoot.(parent : TreeNode, node: TreeNode) -> Unit = { _,_ ->}
    }.apply {
        childFactory(content).forEach {
            add( tNode(it, childFactory) )
        }

        init()
    }
}
/**
 * [tRoot] のデフォルト実装を返す
 *
 * [childFactory] が指定されている場合はそれによって子となる [CONTENT] を取得して再帰的に子ノードを作成する
 *
 * [init] が指定されている場合は [init] によって返されるノードの初期化処理が行われる
 * */
fun <CONTENT : Any?> tNode(content : CONTENT, childFactory : (CONTENT)->(List<CONTENT>) = { listOf() }, init : TreeNode.()->(Unit) = {} ) : TreeNode {
    return object : TreeNode {
        override val nodeParams: TreeNodeParams = TreeNodeParamsImpl(false, null, mutableListOf())
        override var content : Any? = content
    }.apply {
        childFactory(content).forEach {
            add( tNode(it, childFactory) )
        }

        init()
    }
}
/** 子ノード追加のシンタックス・シュガー */
fun <CONTENT : Any?> TreeNode.tChild(content : CONTENT, childFactory : (CONTENT)->(List<CONTENT>) = { listOf() }, init : TreeNode.()->(Unit) = {}){
    add(tNode(content, childFactory, init))
}



fun main(args: Array<String>) {

    val root = tRoot("Root") {
        onExpanded = { out = "expand : ${it.content}" } // ノードが展開された場合にノードを出力する関数を設定する

        tChild("c1")
        tChild("c2"){
            tChild("c2-1")
        }
        tChild("c3")
    }


    out = root.allChildren.map { it.content } // [c1, c2, c2-1, c3]
    out = root.expandedChildren.map { it.content } // [c1, c2, c3]

    root.allChildren[2].expand() // c2-1
}

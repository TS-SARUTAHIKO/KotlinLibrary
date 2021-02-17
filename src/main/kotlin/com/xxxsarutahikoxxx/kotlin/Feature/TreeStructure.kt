package com.xxxsarutahikoxxx.kotlin.Feature

import com.xxxsarutahikoxxx.kotlin.Utilitys.MutableTriple
import com.xxxsarutahikoxxx.kotlin.Utilitys.out


/**
 * [TreeNode] のノード属性情報を隠蔽するための typealias
 *
 * 実際は isExpanded : Boolean, parent : TreeNode?, children : MutableList<TreeNode> のセット情報
 * */
typealias TreeNodeParams = MutableTriple<Boolean, TreeNode?, MutableList<TreeNode>>

/**
 * ツリーを構築するための基本ノード
 * */
interface TreeNode {
    val treeNodeParams : TreeNodeParams
    val defaultTreeNodeParams : TreeNodeParams get() = MutableTriple(false, null, mutableListOf<TreeNode>())

    var content : Any?

    val isExpanded get() = treeNodeParams.first
    fun expand(){
        if( ! isExpanded ){
            treeNodeParams.first = true
            (root as? TreeRoot)?.let { it.onExpanded(it, this) }
        }
    }
    fun collapse(){
        if( isExpanded ){
            treeNodeParams.first = false
            (root as? TreeRoot)?.let { it.onCollapsed(it, this) }
        }
    }
    fun toggle() = if( isExpanded ) collapse() else expand()
    fun expandAll(){
        (listOf(this) + allChildren).filter { ! it.isExpanded }.forEach { it.expand() }
    }
    fun collapseAll(){
        (listOf(this) + allChildren).filter { it.isExpanded }.forEach { it.collapse() }
    }

    val parent : TreeNode? get() = treeNodeParams.second
    val parents : List<TreeNode> get(){
        return parent?.run { val ret = mutableListOf<TreeNode>() ; ret.addAll(parents) ; ret.add(this) ; ret } ?: listOf()
    }
    val root : TreeNode get() = parents.firstOrNull() ?: this
    val layer : Int get() = parents.size

    val children : List<TreeNode> get() = treeNodeParams.third
    fun add(node : TreeNode, index : Int = children.size) : TreeNode {
        treeNodeParams.third.add(index, node)
        node.treeNodeParams.second = this

        (root as? TreeRoot)?.let { it.onAdded(it, node) }

        return this
    }
    fun remove(node : TreeNode){
        (root as? TreeRoot)?.let { it.onPreRemoved(it, node) }

        treeNodeParams.third.remove(node)
        node.treeNodeParams.second = null

        (root as? TreeRoot)?.let { it.onRemoved(it, node) }
    }
    val allChildren : List<TreeNode>
        get() = children.map { listOf(it) + it.allChildren }.flatten()
    val expandedChildren : List<TreeNode>
        get() = children.map { listOf(it) + if( it.isExpanded ){ it.expandedChildren }else{ listOf() } }.flatten()

    val isLeaf : Boolean get() = children.isEmpty()
    val isBranch : Boolean get() = children.isNotEmpty()
}
/**
 * ツリーの Root となるノード
 * */
interface TreeRoot : TreeNode {
    /** ノードが開いた場合に呼ばれる */
    var onExpanded : TreeRoot.(node : TreeNode) -> (Unit)
    /** ノードが閉じた場合に呼ばれる */
    var onCollapsed : TreeRoot.(node : TreeNode) -> (Unit)
    /** ノードが追加された場合に呼ばれる（除去の処理が実行された後に呼ばれる） */
    var onAdded : TreeRoot.(node : TreeNode) -> (Unit)
    /** ノードが除去される直前に呼ばれる */
    var onPreRemoved : TreeRoot.(node : TreeNode) -> (Unit)
    /** ノードが除去された直後に呼ばれる */
    var onRemoved : TreeRoot.(node : TreeNode) -> (Unit)
}

/**
 * [TreeNode] のデフォルト実装を返す
 * */
fun TreeRoot( init : TreeRoot.()->(Unit) ) : TreeRoot {
    return object  : TreeRoot {
        override val treeNodeParams: TreeNodeParams = defaultTreeNodeParams
        override var content : Any? = "Root"

        override var onExpanded: TreeRoot.(node: TreeNode) -> Unit = {}
        override var onCollapsed: TreeRoot.(node: TreeNode) -> Unit = {}
        override var onAdded: TreeRoot.(node: TreeNode) -> Unit = {}
        override var onPreRemoved: TreeRoot.(node: TreeNode) -> Unit = {}
        override var onRemoved: TreeRoot.(node: TreeNode) -> Unit = {}
    }.apply {
       init()
    }
}
/**
 * [TreeRoot] のデフォルト実装を返す
 * */
fun TreeNode(content : Any) : TreeNode {
    return object : TreeNode {
        override val treeNodeParams: TreeNodeParams = defaultTreeNodeParams
        override var content : Any? = content
    }
}
/** 子ノード追加のシンタックス・シュガー */
fun TreeNode.create(content : Any, init : TreeNode.()->(Unit) = {}){
    val child = TreeNode(content)
    add(child)
    child.init()
}


fun main(args: Array<String>) {

    val root = TreeRoot {
        onExpanded = { out = "$it" } // ノードが展開された場合にノードを出力する関数を設定する

        create("c1")
        create("c2"){
            create("c2-1")
        }
        create("c3")
    }


    out = root.allChildren // [c1, c2, c2-1, c3]
    out = root.expandedChildren // [c1, c2, c3]

    root.allChildren[2].expand() // c2-1
}

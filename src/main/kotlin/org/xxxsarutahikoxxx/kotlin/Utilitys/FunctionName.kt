package org.xxxsarutahikoxxx.kotlin.Utilitys


/** 関数に名前を付ける。普通には参照できないが[toString]する際には反映される */
fun <RECEIVER, RET> (RECEIVER.()->(RET)).withName(name : String) : RECEIVER.()->(RET) {
    return object : (RECEIVER)->(RET) {
        val name : String = name
        override fun toString(): String = name
        override fun invoke(p1: RECEIVER): RET = this@withName.invoke(p1)
    }
}

/** 関数に名前を付ける。普通には参照できないが[toString]する際には反映される */
fun <RECEIVER, ARG, RET> (RECEIVER.(ARG)->(RET)).withName(name : String) : RECEIVER.(ARG)->(RET) {
    return object : (RECEIVER, ARG)->(RET) {
        val name : String = name
        override fun toString(): String = name
        override fun invoke(reciever: RECEIVER, arg : ARG): RET = this@withName.invoke(reciever, arg)
    }
}

/** 関数に名前を付ける。普通には参照できないが[toString]する際には反映される */
fun <RECEIVER, ARG1, ARG2, RET> (RECEIVER.(ARG1, ARG2)->(RET)).withName(name : String) : RECEIVER.(ARG1, ARG2)->(RET) {
    return object : (RECEIVER, ARG1, ARG2)->(RET) {
        val name : String = name
        override fun toString(): String = name
        override fun invoke(reciever: RECEIVER, arg1 : ARG1, arg2 : ARG2): RET = this@withName.invoke(reciever, arg1, arg2)
    }
}

/** 関数に名前を付ける。普通には参照できないが[toString]する際には反映される */
fun <RECEIVER, ARG1, ARG2, ARG3, RET> (RECEIVER.(ARG1, ARG2, ARG3)->(RET)).withName(name : String) : RECEIVER.(ARG1, ARG2, ARG3)->(RET) {
    return object : (RECEIVER, ARG1, ARG2, ARG3)->(RET) {
        val name : String = name
        override fun toString(): String = name
        override fun invoke(reciever: RECEIVER, arg1 : ARG1, arg2 : ARG2, arg3 : ARG3): RET = this@withName.invoke(reciever, arg1, arg2, arg3)
    }
}


fun main() {
    val func = {a:Int, b:Double->a*b}.withName("sample")
    out = func

    out = func(2, 3.0)
}
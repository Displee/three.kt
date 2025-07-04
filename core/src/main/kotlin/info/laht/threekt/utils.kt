package info.laht.threekt

import org.lwjgl.opengl.ARBFramebufferObject
import org.lwjgl.opengl.ARBImaging
import org.lwjgl.opengl.GL11
import org.lwjgl.system.APIUtil
import org.lwjgl.system.NativeType

internal val List<*>.length: Int
    get() = this.size

internal inline fun <reified T> MutableList<T?>.length(length: Int) {

    if (length < size) {
        while (length < size) {
            add(null)
        }
    } else if (length > size) {
        while (length > size) {
            removeAt(length - 1)
        }
    }

}

internal inline fun <reified T> MutableList<T>.shrinkToFit(length: Int) {

    if (length == 0) {
        clear()
    } else if (isNotEmpty()) {
        while (length > size) {
            removeAt(lastIndex)
        }
    }

}

internal inline fun <reified T> MutableList<T>.safeSet(index: Int, value: T) {

    when {
        index < size -> set(index, value)
        index == size -> add(value)
    }

}

internal inline fun <reified T> MutableList<T>.add(v1: T, v2: T) {
    add(v1)
    add(v2)
}

internal inline fun <reified T> MutableList<T>.add(v1: T, v2: T, v3: T) {
    add(v1)
    add(v2)
    add(v3)
}

internal inline fun <reified T> MutableList<T>.safeSet(v1: T) {
    if (size == 0) {
        add(v1)
    } else {
        set(0, v1)
    }
}

internal inline fun <reified T> MutableList<T>.safeSet(v1: T, v2: T) {
    if (size == 0) {
        add(v1)
        add(v2)
    } else if (size == 1) {
        set(0, v1)
        add(v2)
    } else {
        set(0, v1)
        set(1, v2)

    }
}

internal inline fun <reified T> MutableList<T>.safeSet(v1: T, v2: T, v3: T) {
    if (size == 0) {
        add(v1)
        add(v2)
        add(v3)
    } else if (size == 1) {
        set(0, v1)
        add(v2)
        add(v3)
    } else if (size == 2) {
        set(0, v1)
        set(1, v2)
        add(v3)
    } else {
        set(0, v1)
        set(1, v2)
        set(2, v3)
    }
}

internal inline fun <reified T> MutableList<T>.safeSet(v1: T, v2: T, v3: T, v4: T) {
    if (size == 0) {
        add(v1)
        add(v2)
        add(v3)
        add(v4)
    } else if (size == 1) {
        set(0, v1)
        add(v2)
        add(v3)
        add(v4)
    } else if (size == 2) {
        set(0, v1)
        set(1, v2)
        add(v3)
        add(v4)
    } else if (size == 3) {
        set(0, v1)
        set(1, v2)
        set(2, v3)
        add(v4)
    } else {
        set(0, v1)
        set(1, v2)
        set(2, v3)
        set(3, v4)
    }
}


internal fun FloatArray.contentEquals(list: List<Float>, allowLongerList: Boolean = false): Boolean {

    if (allowLongerList) {
        if (size > list.size) return false
    } else {
        if (size != list.size) return false
    }

    for (i in 0 until size) {
        if (get(i).compareTo(list[i]) != 0) return false
    }

    return true
}


internal fun FloatArray.copyInto(list: MutableList<Float>): MutableList<Float> {

    while (list.size < size) {
        list.add(0f)
    }

    forEachIndexed { i, v ->
        list[i] = v
    }

    return list

}

internal inline fun <reified T> MutableList<T>.splice(start: Int, deleteCount: Int, vararg elements: T) {

    if (deleteCount > 0) {
        subList(start, start + deleteCount).clear()
    }

    elements.forEachIndexed { i, v ->
        add(start + i, v)
    }

}


internal inline fun <reified T> MutableList<T>.push(value: T): Int {
    add(value)
    return size
}

internal inline fun <reified T> MutableList<T>.push(v1: T, v2: T): Int {
    add(v1)
    add(v2)
    return size
}

internal inline fun <reified T> MutableList<T>.push(v1: T, v2: T, v3: T): Int {
    add(v1)
    add(v2)
    add(v3)
    return size
}

internal inline fun <reified T> MutableList<T>.push(value: T, vararg values: T): Int {
    add(value)
    values.forEach { add(it) }
    return size
}

fun glGetIntegerDebug(@NativeType("GLenum") pname: Int): Int {
    checkGlError()
    val value = GL11.glGetInteger(pname)
    checkGlError()
    return value
}

fun glTexParameteriDebug(target: Int, pname: Int, param: Int) {
    checkGlError()
    GL11.glTexParameteri(target, pname, param)
    checkGlError()
}

fun glBindTextureDebug(target: Int, texture: Int) {
    checkGlError()
    GL11.glBindTexture(target, texture)
    checkGlError()
}

fun checkGlError() {
    val err: Int = GL11.glGetError()
    if (err != GL11.GL_NO_ERROR) {
        throw RuntimeException(String.format("%s [0x%X]", getErrorString(err), err))
    }
}

private fun getErrorString(errorCode: Int): String? {
    return when (errorCode) {
        GL11.GL_NO_ERROR -> "No error"
        GL11.GL_INVALID_ENUM -> "Enum argument out of range"
        GL11.GL_INVALID_VALUE -> "Numeric argument out of range"
        GL11.GL_INVALID_OPERATION -> "Operation illegal in current state"
        GL11.GL_STACK_OVERFLOW -> "Command would cause a stack overflow"
        GL11.GL_STACK_UNDERFLOW -> "Command would cause a stack underflow"
        GL11.GL_OUT_OF_MEMORY -> "Not enough memory left to execute command"
        ARBFramebufferObject.GL_INVALID_FRAMEBUFFER_OPERATION -> "Framebuffer object is not complete"
        ARBImaging.GL_TABLE_TOO_LARGE -> "The specified table is too large"
        else -> APIUtil.apiUnknownToken(errorCode)
    }
}
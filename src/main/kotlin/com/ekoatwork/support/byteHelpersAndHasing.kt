/*
 * Copyright 2016 Andries Spies
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.ekoatwork.support

import java.io.OutputStream
import java.nio.ByteBuffer
import java.nio.charset.Charset
import java.security.MessageDigest
import java.util.function.Supplier

private val HEX_CHARS = "0123456789ABCDEF".toCharArray()

fun ByteArray.toHex(): String {
    return fold(StringBuilder()) { stringBuilder, byte ->
        stringBuilder.apply {
            val octet = byte.toInt()
            val i0 = (octet and 0xF0) ushr 4
            val i1 = (octet and 0x0F)
            append(HEX_CHARS[i0])
            append(HEX_CHARS[i1])
        }
    }.toString()
}

const val ZERO_BYTE: Byte = 0
const val ONE_BYTE: Byte = 1

private const val LONG_BYTES_SIZE: Int = java.lang.Long.BYTES
private const val DOUBLE_BYTES_SIZE: Int = java.lang.Double.BYTES
private const val INTEGER_BYTES_SIZE: Int = java.lang.Integer.BYTES
private const val SHORT_BYTES_SIZE: Int = java.lang.Short.BYTES

fun Long.toBytes(): ByteArray {
    return ByteBuffer.allocate(LONG_BYTES_SIZE).let { buffer ->
        buffer.putLong(this)
        buffer.array()
    }
}

fun Double.toBytes(): ByteArray {
    return ByteBuffer.allocate(DOUBLE_BYTES_SIZE).let { buffer ->
        buffer.putDouble(this)
        buffer.array()
    }
}

fun Int.toBytes(): ByteArray {
    return ByteBuffer.allocate(INTEGER_BYTES_SIZE).let { buffer ->
        buffer.putInt(this)
        buffer.array()
    }
}

fun Short.toBytes(): ByteArray {
    return ByteBuffer.allocate(SHORT_BYTES_SIZE).let { buffer ->
        buffer.putShort(this)
        buffer.array()
    }
}


enum class HashFunction(private val digesterName: String) : Supplier<MessageDigest> {
    MD2("MD2"),
    MD5("md5"),
    SHA1("SHA-1"),
    SHA224("SHA-224"),
    SHA256("SHA-256"),
    SHA384("SHA-384"),
    SHA512("SHA-512")
    ;

    operator fun invoke(): MessageDigest = MessageDigest.getInstance(digesterName)
    override fun get(): MessageDigest = invoke()
    fun digest(collectHashableBytes: ByteSink.() -> Unit): ByteArray = invoke().let { md ->
        collectHashableBytes(md.toByteSink())
        return md.digest()
    }
}

interface ByteSink {
    fun put(byteArray: ByteArray): ByteSink
    fun put(text: String, charset: Charset): ByteSink = put(text.toByteArray(charset))
    fun put(text: String): ByteSink = put(text, Charsets.UTF_8)
    fun put(boolean: Boolean): ByteSink = put(byteArrayOf(if (true) ONE_BYTE else ZERO_BYTE))
    fun put(long: Long): ByteSink = put(long.toBytes())
    fun put(short: Short): ByteSink = put(short.toBytes())
    fun put(int: Int): ByteSink = put(int.toBytes())
    fun put(f: Double): ByteSink = put(f.toBytes())

    fun outputStream(): OutputStream = object : OutputStream() {

        override fun write(b: Int) {
            put(b)
        }

        override fun write(b: ByteArray?) {
            if (b != null) put(b)
        }
    }
}

fun MessageDigest.toByteSink(): ByteSink = byteSinkOf { bytes -> update(bytes) }

@FunctionalInterface
interface PrimitivesFunnel<in T> {
    fun funnelToSink(request: T, byteSink: ByteSink)
}

typealias FunnelToByteSink<T> = (T, ByteSink) -> Unit

inline fun <T> funnelOf(crossinline funnel: FunnelToByteSink<T>): PrimitivesFunnel<T> {
    return object : PrimitivesFunnel<T> {
        override fun funnelToSink(request: T, byteSink: ByteSink) {
            funnel(request, byteSink)
        }
    }
}

fun byteSinkOf(block: (ByteArray) -> Unit): ByteSink {
    return object : ByteSink {
        override fun put(byteArray: ByteArray): ByteSink {
            block(byteArray)
            return this
        }
    }
}


fun <T> MessageDigest.digest(item: T, funnel: PrimitivesFunnel<T>): ByteArray {
    funnel.funnelToSink(item, toByteSink())
    return digest()
}

fun <T> MessageDigest.digest(item: T, funnel: FunnelToByteSink<T>): ByteArray {
    funnel(item, toByteSink())
    return digest()
}


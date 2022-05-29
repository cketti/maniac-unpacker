package dev.int21.maniac.unpacker

import okio.BufferedSink
import okio.BufferedSource
import okio.ByteString

fun Int.toParagraphs(): UShort = ((this + 0x0F) / 0x10).toUShort()

fun UShort.paragraphsToBytes(): Int = this.toInt() * 0x10

fun BufferedSource.readUShort(): UShort = readShortLe().toUShort()

fun ByteString.getUByte(index: Int): UByte = this[index].toUByte()

fun ByteString.getUShort(index: Int): UShort {
  return ((this[index + 1].toUByte().toUInt() shl 8) or this[index].toUByte().toUInt()).toUShort()
}

fun BufferedSink.writeWord(data: UShort) = writeShortLe(data.toInt())

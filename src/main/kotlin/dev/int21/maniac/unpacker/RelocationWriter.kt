package dev.int21.maniac.unpacker

import okio.BufferedSink

fun Relocation.writeTo(sink: BufferedSink) {
  sink.writeWord(offset)
  sink.writeWord(segment)
}

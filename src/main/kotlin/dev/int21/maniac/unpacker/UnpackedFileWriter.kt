package dev.int21.maniac.unpacker

import okio.BufferedSink

fun UnpackedFile.writeTo(sink: BufferedSink) {
  writeHeader(sink)
  writeCode(sink)
}

private fun UnpackedFile.writeHeader(sink: BufferedSink) {
  writeFixedHeader(sink)
  writeRelocations(sink)
  writeHeaderPadding(sink)
}

private fun UnpackedFile.writeFixedHeader(sink: BufferedSink) {
  header.writeTo(sink)
}

private fun UnpackedFile.writeRelocations(sink: BufferedSink) {
  for (relocation in relocations) {
    relocation.writeTo(sink)
  }
}

private fun UnpackedFile.writeHeaderPadding(sink: BufferedSink) {
  val padding = header.headerSize - MzHeader.FIXED_SIZE - 4 * header.numRelocations.toInt()
  repeat(padding) {
    sink.writeByte(0x00)
  }
}

private fun UnpackedFile.writeCode(sink: BufferedSink) {
  for (operation in operations) {
    when (operation) {
      is Operation.Repeat -> {
        repeat(operation.count) {
          sink.writeByte(operation.byte.toInt())
        }
      }
      is Operation.Copy -> {
        sink.write(packedData, operation.offset, operation.count)
      }
    }
  }
}

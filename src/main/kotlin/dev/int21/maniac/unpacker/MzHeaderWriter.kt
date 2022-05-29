package dev.int21.maniac.unpacker

import okio.BufferedSink

fun MzHeader.writeTo(sink: BufferedSink) {
  with(sink) {
    writeWord(magic)
    writeWord(lastPageExtraBytes)
    writeWord(numPages)
    writeWord(numRelocations)
    writeWord(headerSizeParagraphs)
    writeWord(minAllocationParagraphs)
    writeWord(maxAllocationParagraphs)
    writeWord(initialSs)
    writeWord(initialSp)
    writeWord(checksum)
    writeWord(initialIp)
    writeWord(initialCs)
    writeWord(relocationOffset)
    writeWord(overlayId)
  }
}

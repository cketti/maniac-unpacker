package dev.int21.maniac.unpacker

import okio.BufferedSource

fun readExeFile(source: BufferedSource): ExeFile {
  val header = MzHeader(
    magic = source.readUShort(),
    lastPageExtraBytes = source.readUShort(),
    numPages = source.readUShort(),
    numRelocations = source.readUShort(),
    headerSizeParagraphs = source.readUShort(),
    minAllocationParagraphs = source.readUShort(),
    maxAllocationParagraphs = source.readUShort(),
    initialSs = source.readUShort(),
    initialSp = source.readUShort(),
    checksum = source.readUShort(),
    initialIp = source.readUShort(),
    initialCs = source.readUShort(),
    relocationOffset = source.readUShort(),
    overlayId = source.readUShort()
  )

  val paddingBeforeRelocations = header.relocationOffset.toInt() - MzHeader.FIXED_SIZE
  require(paddingBeforeRelocations >= 0)
  source.skip(paddingBeforeRelocations.toLong())

  val relocationCount = header.numRelocations.toInt()
  val relocations = buildList {
    repeat(relocationCount) {
      add(Relocation(offset = source.readUShort(), segment = source.readUShort()))
    }
  }

  val relocationSize = 4 * relocationCount
  val paddingAfterRelocations = header.headerSize - MzHeader.FIXED_SIZE - paddingBeforeRelocations - relocationSize
  require(paddingBeforeRelocations >= 0)
  source.skip(paddingAfterRelocations.toLong())

  val dataSize = header.totalSize - header.headerSize
  val data = source.readByteString()
  require(dataSize <= data.size)

  return ExeFile(header, relocations, data)
}

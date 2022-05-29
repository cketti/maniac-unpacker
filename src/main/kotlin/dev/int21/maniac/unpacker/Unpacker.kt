package dev.int21.maniac.unpacker

import dev.int21.maniac.unpacker.MzHeader.Companion.PAGE_SIZE
import okio.ByteString

private const val RELOCATION_OFFSET = 0x125

fun unpack(packedFile: ExeFile): UnpackedFile {
  val unpackerStart = packedFile.header.initialCs.paragraphsToBytes()

  val relocations = rebuildRelocations(packedFile.data, unpackerStart)
  val header = rebuildHeader(packedFile, unpackerStart, relocations)
  val operations = buildOperationsList(packedFile.data, unpackerStart)

  return UnpackedFile(packedFile.data, header, relocations, operations)
}

private fun rebuildRelocations(data: ByteString, unpackerStart: Int): List<Relocation> {
  var relocationOffset = unpackerStart + RELOCATION_OFFSET
  return buildList {
    for (segment in 0..0xF000 step 0x1000) {
      val count = data.getUShort(relocationOffset).toInt()
      relocationOffset += 2

      repeat(count) {
        add(Relocation(offset = data.getUShort(relocationOffset), segment = segment.toUShort()))
        relocationOffset += 2
      }
    }
  }
}

private fun rebuildHeader(
  packedFile: ExeFile,
  unpackerStart: Int,
  relocations: List<Relocation>
): MzHeader {
  val data = packedFile.data

  val initialIp = data.getUShort(unpackerStart + 0x00)
  val initialCs = data.getUShort(unpackerStart + 0x02)
  val initialSp = data.getUShort(unpackerStart + 0x08)
  val initialSs = data.getUShort(unpackerStart + 0x0A)
  val originalDataSize = data.getUShort(unpackerStart + 0x0C).paragraphsToBytes()

  val restoredHeaderSizeParagraphs = (MzHeader.FIXED_SIZE + 4 * relocations.size).toParagraphs()
  val originalTotalSize = restoredHeaderSizeParagraphs.paragraphsToBytes() + originalDataSize

  val (numPages, lastPageExtraBytes) = originalTotalSize.toPages()

  val stackBottom = initialSs.paragraphsToBytes() + initialSp.toInt()
  val minAllocationParagraphs = (stackBottom - originalDataSize).toParagraphs()

  return MzHeader(
    magic = MzHeader.MAGIC.toUShort(),
    lastPageExtraBytes = lastPageExtraBytes,
    numPages = numPages,
    numRelocations = relocations.size.toUShort(),
    headerSizeParagraphs = restoredHeaderSizeParagraphs,
    minAllocationParagraphs = minAllocationParagraphs,
    maxAllocationParagraphs = packedFile.header.maxAllocationParagraphs,
    initialSs = initialSs,
    initialSp = initialSp,
    checksum = 0u,
    initialIp = initialIp,
    initialCs = initialCs,
    relocationOffset = MzHeader.FIXED_SIZE.toUShort(),
    overlayId = 0u
  )
}

private fun buildOperationsList(data: ByteString, unpackerStart: Int): List<Operation> {
  var currentOffset = skipPadding(unpackerStart, data)

  return buildList {
    do {
      val operation = data.getUByte(currentOffset).toInt()
      val length = data.getUShort(currentOffset - 2).toInt()
      currentOffset -= 3

      when (operation and 0xFE) {
        0xB0 -> {
          this.add(Operation.Repeat(byte = data[currentOffset], count = length))
          currentOffset -= 1
        }
        0xB2 -> {
          currentOffset -= length
          this.add(Operation.Copy(offset = currentOffset + 1, count = length))
        }
        else -> {
          error("Unknown operation")
        }
      }
    } while (operation and 0x01 == 0)

    if (currentOffset > 0) {
      this.add(Operation.Copy(offset = 0, count = currentOffset + 1))
    }
  }.asReversed()
}

private fun skipPadding(unpackerStart: Int, data: ByteString): Int {
  var currentOffset = unpackerStart - 1
  while (data[currentOffset] == 0xFF.toByte()) {
    currentOffset--
  }

  return currentOffset
}

private fun Int.toPages(): Pair<UShort, UShort> {
  val pages = (this / PAGE_SIZE) + 1
  val lastPageSize = this % PAGE_SIZE

  return if (lastPageSize == 0) {
    (pages - 1).toUShort() to PAGE_SIZE.toUShort()
  } else {
    pages.toUShort() to lastPageSize.toUShort()
  }
}

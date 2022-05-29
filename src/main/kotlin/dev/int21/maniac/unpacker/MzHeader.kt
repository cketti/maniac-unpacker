package dev.int21.maniac.unpacker

data class MzHeader(
  val magic: UShort,
  val lastPageExtraBytes: UShort,
  val numPages: UShort,
  val numRelocations: UShort,
  val headerSizeParagraphs: UShort,
  val minAllocationParagraphs: UShort,
  val maxAllocationParagraphs: UShort,
  val initialSs: UShort,
  val initialSp: UShort,
  val checksum: UShort,
  val initialIp: UShort,
  val initialCs: UShort,
  val relocationOffset: UShort,
  val overlayId: UShort
) {
  val headerSize: Int
    get() = headerSizeParagraphs.paragraphsToBytes()

  val totalSize: Int
    get() = (numPages.toInt() - 1) * PAGE_SIZE + lastPageExtraBytes.toInt()

  companion object {
    const val MAGIC = 0x5A4D
    const val FIXED_SIZE = 14 * 2
    const val PAGE_SIZE = 512
  }
}

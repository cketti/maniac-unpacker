package dev.int21.maniac.unpacker

import okio.ByteString

data class UnpackedFile(
  val packedData: ByteString,
  val header: MzHeader,
  val relocations: List<Relocation>,
  val operations: List<Operation>
)

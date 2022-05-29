package dev.int21.maniac.unpacker

import okio.ByteString

data class ExeFile(
  val header: MzHeader,
  val relocations: List<Relocation>,
  val data: ByteString,
)

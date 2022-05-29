package dev.int21.maniac.unpacker

data class Relocation(
  val offset: UShort,
  val segment: UShort
)

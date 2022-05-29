package dev.int21.maniac.unpacker

sealed interface Operation {
  data class Repeat(val byte: Byte, val count: Int) : Operation
  data class Copy(val offset: Int, val count: Int) : Operation
}

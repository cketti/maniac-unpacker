package dev.int21.maniac.unpacker

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.parameters.arguments.argument
import com.github.ajalt.clikt.parameters.arguments.optional
import com.github.ajalt.clikt.parameters.types.file
import com.github.ajalt.clikt.parameters.types.inputStream
import okio.buffer
import okio.sink
import okio.source
import java.io.File
import java.io.InputStream

/**
 * Removes the runtime packer from the Maniac Mansion DOS executable (EGA Enhanced Version).
 *
 * Filename:  MANIAC.EXE
 * Sha256sum: 13dca67f29f8157fb41a19bb6c8ef4771eb34501ab0c16603865f361c19b6c88
 */
@Suppress("MemberVisibilityCanBePrivate")
class ManiacUnpacker : CliktCommand(
  help = "Removes the runtime packer from the Maniac Mansion DOS executable (EGA Enhanced Version)."
) {
  val input by argument(help = "Input file (MANIAC.EXE)")
    .inputStream()

  val output by argument(help = "Output file")
    .file(mustExist = false, canBeDir = false)
    .optional()

  override fun run() {
    val packedFile = readPackedFile(input)

    val unpackedFile = unpack(packedFile)

    outputUnpackedFile(unpackedFile, output)

    if (output != null) {
      echo("Done.")
    }
  }

  private fun readPackedFile(input: InputStream): ExeFile {
    return input.source().buffer().use { source ->
      readExeFile(source)
    }
  }

  private fun outputUnpackedFile(unpackedFile: UnpackedFile, outputFile: File?) {
    val output = outputFile?.sink() ?: System.out.sink()

    output.buffer().use { sink ->
      unpackedFile.writeTo(sink)
    }
  }
}

fun main(args: Array<String>) = ManiacUnpacker().main(args)

package atownsend.reactivestate.util

import android.content.Context

fun Context.getStringFromFile(path: String): String {
  val stream = this.resources.assets.open(path)
  val reader = stream.bufferedReader()
  val lines = reader.readLines()
  reader.close()
  stream.close()
  return lines.joinToString("\n")
}
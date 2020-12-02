package utils

import java.io.File

fun <R> fileLines(vararg files: String, transformation: (String) -> R) = files.map { File(it).readLines().map(transformation) }

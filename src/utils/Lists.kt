package utils

fun <T> List<T>.chunkWhen(predicate: (T) -> Boolean): List<List<T>> =
    fold(mutableListOf<MutableList<T>>()) { acc, elem ->
        if (acc.isEmpty() || predicate.invoke(elem)) acc.add(mutableListOf())
        acc.last().add(elem)
        acc
    }.map { it.toList() }.toList()

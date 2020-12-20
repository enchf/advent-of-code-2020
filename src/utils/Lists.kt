package utils

fun <T> List<T>.chunkWhen(predicate: (T) -> Boolean, removePredicate: Boolean = true): List<List<T>> =
    fold(mutableListOf<MutableList<T>>()) { acc, elem ->
        val cut = predicate.invoke(elem)
        if (acc.isEmpty() || cut) acc.add(mutableListOf())
        if (!cut || !removePredicate) acc.last().add(elem)
        acc
    }.map { it.toList() }.toList()

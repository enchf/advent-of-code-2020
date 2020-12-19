package utils

import java.util.function.Predicate

fun <T> chunkWhen(list: List<T>, predicate: (T) -> Boolean): List<List<T>> =
    list.fold(mutableListOf<MutableList<T>>()) { acc, elem ->
        if (acc.isEmpty() || predicate.invoke(elem)) acc.add(mutableListOf())
        acc.last().add(elem)
        acc
    }.map { it.toList() }.toList()

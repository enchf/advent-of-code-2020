package utils

fun String.findGroups(target: String) = toRegex()
        .matchEntire(target)!!
        .groupValues

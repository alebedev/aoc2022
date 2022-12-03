import java.lang.RuntimeException

fun getRucksackScore() {
    var result = 0
    for (group in readRucksack()) {
        result += charScore(findGroupBadge(group))
    }
    println("Total score: $result")
}

typealias Group = MutableList<String>

fun readRucksack(): Iterable<Group> {
    val result = mutableListOf<Group>()
    var group: Group
    generateSequence(::readLine).forEachIndexed { i, line ->
        if (i % 3 == 0) {
            group = mutableListOf()
            result.add(group)
        } else {
            group = result.last()
        }
        group.add(line)
    }
    return result.toList()
}

fun findGroupBadge(group: Group): Char {
    val sets = group.map {
        it.toSet()
    }
    val commonChars = sets.reduce {chars, lineChars -> chars.intersect(lineChars)}
    if (commonChars.size != 1) {
        throw Error("Expected to find exactly one unique char")
    }
    return commonChars.first()
}

fun charScore(char: Char) = when (char) {
    in 'a'..'z' -> {
        char.code - 'a'.code + 1
    }

    in 'A'..'Z' -> {
        char.code - 'A'.code + 27
    }

    else -> throw RuntimeException("Unexpected char $char")
}

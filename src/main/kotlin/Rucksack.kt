import java.lang.RuntimeException

fun getRucksackScore() {
    var result = 0
    for (pair in readRucksack()) {
        result += charScore(findDuplicate(pair))
    }
    println("Total score: $result")
}

typealias Content = Pair<String, String>

fun readRucksack(): Sequence<Content> {
    return generateSequence(::readLine).map {
        val len = it.length
        if (len % 2  > 0) {
            throw RuntimeException("Odd length $len")
        }
        Pair(it.substring(0 until (len / 2)), it.substring(len / 2))
    }

}

fun findDuplicate(content: Content): Char {
    val firstChars = mutableSetOf<Char>()
    for (char in content.first) {
        firstChars.add(char)
    }
    for (char in content.second) {
        if (firstChars.contains(char)) {
            return char
        }
    }
    throw RuntimeException("No duplicate it contents")
}

fun charScore(char: Char) = when (char) {
    in 'a'..'z' -> { char.code - 'a'.code + 1 }
    in 'A'..'Z' -> { char.code - 'A'.code + 27 }
    else -> throw RuntimeException("Unexpected char $char")
}

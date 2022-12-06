// mjqjpqmgbljsphdztnvjfqwrcgsmlb -> 7
fun main() {
    val input = readln()
    println("Signal starts at ${Signal.findIndex(input)}")
}

private object Signal {
    fun findIndex(input: String): Int {
        val targetLength = 14
        var chars = ""
        for (i in input.indices) {
            if (!chars.contains(input[i])) {
                chars += input[i]
                if (chars.length == targetLength) {
                    return i + 1
                }
            } else {
                chars = chars.substring(chars.indexOf(input[i]) + 1) + input[i]
            }
            if (i >= targetLength) {
                chars.drop(1)
            }
        }
        return -1
    }
}
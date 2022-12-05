// Day 1
fun main() {
    val lines = generateSequence(::readLine)
    val sums = mutableListOf<Int>()
    var cur = 0
    for (line in lines) {
        when(line) {
            "" -> {
                sums.add(cur)
                cur = 0
            }
            else -> {
                cur += line.toInt()
            }
        }
    }
    val byTotal = sums.sorted().reversed()
    println("Max: ${byTotal.get(0)}")
    println("Top 3: ${byTotal.take(3).sum()}")
}
fun main() = Decrypt.solve()

private object Decrypt {
    fun solve() {
        val numbers = readInput()
        val mixed = mixNumbers(numbers)
        val coords = listOf(1000, 2000, 3000).map {
            mixed[(mixed.indexOf(0) + it) % mixed.size]
        }
//        mixed.forEach { println("$it") }
        println(
            "$coords, total = ${coords.sum()}"
        )
    }

    private fun readInput(): List<Int> = generateSequence(::readLine).map { it.toInt(10) }.toList()

    private fun mixNumbers(numbers: List<Int>): List<Int> {
        val result = numbers.toMutableList()
//        println("$result")
        for (number in numbers) {
            val from = result.indexOf(number)
            val to = wrapIndex(from + number, result.size)
            swap(result, from, to)
//            println("$result $number $from->$to")
        }
        return result
    }

    private fun swap(numbers: MutableList<Int>, from: Int, to: Int) {
        val moveVal = numbers.removeAt(from)
        val target = to
        numbers.add(target, moveVal)
    }

    private fun wrapIndex(index: Int, size: Int): Int {
        var target = index % (size - 1)
        if (target < 0) {
            target = size - 1 + target
        }
        if (target == 0) {
            return size - 1
        }
        return target
    }
}
fun main() = Fuel.solve()

private object Fuel {
    fun solve() {
        println("${fromSnafu("1=11-2")}")
        println("-> ${toSnafu(12345)}")
        val nums = readInput()
        println("$nums")
        val sum = nums.sum()
        println("Sum: $sum, SNAFU: ${toSnafu(sum)}")
    }

    private fun readInput(): List<Long> = generateSequence(::readLine).map { fromSnafu(it) }.toList()

    private fun fromSnafu(snafu: String): Long {
        val ordinals = mutableListOf<Int>()
        for (char in snafu.toCharArray()) {
            val num = snafuChars.getOrElse(char) { throw Error("Unexpected char $char") }
            if (num < 0) {
                ordinals[ordinals.lastIndex] -= 1
                ordinals.add(5 + num)
            } else {
                ordinals.add(num)
            }
//            println("$char $num $ordinals")
        }
        var result = 0L
        for (ord in ordinals) {
            if (result > 0) {
                result *= 5
            }
            result += ord
        }
        return result
    }

    private fun toSnafu(snafu: Long): String {
        val chars = mutableListOf<Char>()
        var next = snafu
        while (next > 0) {
            val cur = (next % 5)
            chars.add(
                0,
                when (cur) {
                    0L -> '0'
                    1L -> '1'
                    2L -> '2'
                    3L -> '='
                    4L -> '-'
                    else -> throw Error("!!!")
                }
            )
            next /= 5
            if (cur > 2) {
                next += 1
            }
        }
        return chars.joinToString("")
    }

    private val snafuChars = buildMap {
        put('1', 1)
        put('2', 2)
        put('0', 0)
        put('-', -1)
        put('=', -2)
    }
}
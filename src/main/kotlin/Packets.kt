fun main() = Packets.solve()

private object Packets {
    fun solve() {
        val input = readInput().toMutableList()
        val dividers = setOf(
            Lst(listOf(Lst(listOf(Num(2))))),
            Lst(listOf(Lst(listOf(Num(6)))))
        )
        input.addAll(dividers)
        val sorted = input.sorted()
        println("Problem 2: ${(sorted.indexOf(dividers.first()) + 1) * (sorted.indexOf(dividers.last()) + 1)}")
    }

    private fun readInput(): List<Item> {
        val items = mutableListOf<Item>()
        generateSequence(::readLine).forEach {
            when {
                !it.isEmpty() -> {
                    items.add(parseItem(it))
                }
            }
        }
        return items
    }

    private fun parseItem(line: String): Item {
        fun extractNums(string: String): List<Num> =
            string.split(",").map { it.toIntOrNull(10) }.filterNotNull().map { Num(it) }

        fun parseLst(): Lst {
            val values = mutableListOf<MutableList<Item>>()
            val indices = mutableListOf<Int>()
            var lastGroupClose = 0
            for (i in line.indices) {
                when (line[i]) {
                    '[' -> {
                        if (!indices.isEmpty()) {
                            val substr = line.substring((indices.last() + 1) until i)
                            values.last().addAll(extractNums(substr))
                        }
                        indices.add(i)
                        values.add(mutableListOf())
                    }

                    ']' -> {
                        val from = indices.removeLast()
                        val items = values.removeLast()
                        // Non-empty list
                        val substr = line.substring(
                            (maxOf(from, lastGroupClose) + 1) until i
                        )
                        items.addAll(extractNums(substr))
                        lastGroupClose = i
                        if (values.isEmpty()) {
//                            println("Parsed $items")
                            return Lst(items)
                        } else {
                            values.last().add(Lst(items))
                        }
                    }

                    else -> continue
                }
            }
            throw Error("Did not terminate properly")
        }


        return when (line.first()) {
            '[' -> parseLst()
            else -> Num(line.toInt(10))
        }
    }

    private sealed interface Item : Comparable<Item>

    private data class Num(val value: Int) : Item {
        override fun compareTo(other: Item): Int {
            return when (other) {
                is Num -> value.compareTo(other.value)
                is Lst -> Lst(listOf(this)).compareTo(other)
            }
        }
    }

    private data class Lst(val value: List<Item>) : Item {
        override fun compareTo(other: Item): Int {
            return when (other) {
                is Num -> compareTo(Lst(listOf(other)))
                is Lst -> {
                    for (i in value.indices) {
                        if (i >= other.value.size) {
                            return 1
                        }
                        val cmp = value[i].compareTo(other.value[i])
                        if (cmp != 0) {
                            return cmp
                        }
                    }
                    return value.size.compareTo(other.value.size)
                }
            }
        }
    }
}

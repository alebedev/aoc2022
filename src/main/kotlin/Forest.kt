fun main() {
    Forest.solve()
}

private object Forest {
    fun solve() {
        println("Visible trees: ${countVisible(readInput())}")
    }

    private fun readInput(): Grid {
        return generateSequence(::readLine).map {
            it.split("").filterNot { it.isEmpty() }.map { it.toInt(10) }
        }.toList()
    }

    private fun countVisible(trees: Grid): Int {
        var result = 0
        trees.forEachIndexed { i, line ->
            line.forEachIndexed { j, item ->
                if (i == 0 || j == 0 || i == trees.size - 1 || j == line.size - 1) {
                    result += 1
                } else if (line.subList(0, j).max() < item) {
                    result += 1
                } else if (line.subList(j + 1, line.size).max() < item) {
                    result += 1
                } else {
                    var topVisible = true
                    var bottomVisible = true
                    for (z in trees.indices) {
                        if (z == i) continue
                        if (topVisible && z < i) {
                            topVisible = trees[z][j] < item
                        }
                        if (bottomVisible && z > i) {
                            bottomVisible = trees[z][j] < item
                        }
                    }
                    if (topVisible || bottomVisible) result += 1
                }
            }
        }
        return result
    }
}

private typealias Grid = List<List<Int>>
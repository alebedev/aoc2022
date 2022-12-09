fun main() {
    Forest.solve()
}

private object Forest {
    fun solve() {
        val grid = readInput()
        println("Visible trees: ${countVisible(grid)}")
        println("Max scenic score: ${maxScenicScore(grid)}")
    }

    private fun readInput(): Grid {
        return generateSequence(::readLine).map {
            it.split("").filterNot { it.isEmpty() }.map { it.toInt(10) }
        }.toList()
    }

    private fun maxScenicScore(trees: Grid): Int {
        var result = 0
        trees.forEachIndexed { i, line ->
            line.forEachIndexed { j, tree ->
                var leftView = 0
                for (left in (j - 1) downTo 0) {
                    leftView += 1
                    if (trees[i][left] >= tree) break
                }
                var rightView = 0
                for (right in (j + 1) until line.size) {
                    rightView += 1
                    if (trees[i][right] >= tree) break
                }

                var topView = 0
                for (top in (i - 1) downTo 0) {
                    topView += 1
                    if (trees[top][j] >= tree) break
                }
                var bottomView = 0
                for (bottom in (i + 1) until trees.size) {
                    bottomView += 1
                    if (trees[bottom][j] >= tree) break

                }
                result = maxOf(result, leftView * rightView * topView * bottomView)
            }
        }
        return result
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
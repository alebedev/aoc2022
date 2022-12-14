fun main() = Sand.solve()

private object Sand {
    fun solve() {
        val input = readInput()
        val (grid, start) = inputToGrid(input)
        drawGrid(grid)
        println("Start: $start")
        drawGrid(addSand(grid, start))
    }

    private fun addSand(grid: List<MutableList<Cell>>, start: Pos): List<List<Cell>> {
        val result = grid.map { it.toMutableList() }

        fun nextPos(pos: Pos): Pos? {
            val variants = listOf(
                Pos(pos.x, pos.y + 1),
                Pos(pos.x - 1, pos.y + 1),
                Pos(pos.x + 1, pos.y + 1)
            )
            for (variant in variants) {
                if (variant.y >= result.size) {
                    return null
                }
                if (variant.x < 0 || variant.x >= result.first().size) {
                    return null
                }
                if (result[variant.y][variant.x] == Cell.Empty) {
                    return variant
                }
            }
            return pos
        }

        fun percolate(): Boolean {
            var pos = start
            while (true) {
                val next = nextPos(pos) ?: return false
                if (next == pos) {
                    result[pos.y][pos.x] = Cell.Sand
                    return pos != start
                }
                pos = next
            }
        }

        var iterations = 1
        while (percolate()) {
            iterations += 1
        }
        println("Iterations: $iterations")

        return result
    }

    private fun readInput(): List<List<Pos>> {
        return generateSequence(::readLine).map {
            it.splitToSequence(" -> ").map {
                val parts = it.split(",")
                Pos(parts[0].toInt(10), parts[1].toInt(10))
            }.toList()
        }.toList()
    }

    private fun inputToGrid(input: List<List<Pos>>): Pair<List<MutableList<Cell>>, Pos> {
        val start = Pos(500, 0)
        val positions = input.flatten()
        val minX = minOf(start.x, positions.map { it.x }.min()) - 10
        val maxX = maxOf(start.x, positions.map { it.x }.max()) + 10
        val minY = minOf(start.y, positions.map { it.y }.min())
        val maxY = maxOf(start.y, positions.map { it.y }.max()) + 2
        println("$minX $maxX $minY $maxY")
        val result = mutableListOf<MutableList<Cell>>()
        for (y in minY..maxY) {
            val line = mutableListOf<Cell>()
            for (x in minX..maxX) {
                line.add(Cell.Empty)
            }
            result.add(line)
        }

        fun fillWithRock(grid: List<MutableList<Cell>>, from: Pos, to: Pos) {
//            println("$from $to")

            if (from.x != to.x) {
                val src = Pos(minOf(from.x, to.x), from.y)
                val dst = Pos(maxOf(from.x, to.x), from.y)
                for (x in src.x..dst.x) {
                    grid[src.y - minY][x - minX] = Cell.Rock
                }
            } else {
                val src = Pos(from.x, minOf(from.y, to.y))
                val dst = Pos(from.x, maxOf(from.y, to.y))
                for (y in src.y..dst.y) {
                    grid[y - minY][src.x - minX] = Cell.Rock
                }
            }
        }

        for (rock in input) {
            val points = rock.toMutableList()
            var from = points.removeFirst()
            while (!points.isEmpty()) {
                val to = points.removeFirst()
                fillWithRock(result, from, to)
                from = to
            }
        }
        fillWithRock(result, Pos(minX, maxY), Pos(maxX, maxY))

        return Pair(result, Pos(start.x - minX, start.y - minY))
    }

    private fun drawGrid(grid: List<List<Cell>>) {
        for (line in grid) {
            println(line.map {
                when (it) {
                    Cell.Empty -> '.'
                    Cell.Rock -> '#'
                    Cell.Sand -> 'o'
                }
            }.joinToString(""))
        }
    }

    enum class Cell {
        Empty, Rock, Sand,
    }

    data class Pos(val x: Int, val y: Int) : Comparable<Pos> {
        override fun compareTo(other: Pos): Int {
            val cmpX = x.compareTo(other.x)
            if (cmpX != 0) {
                return cmpX
            }
            val cmpY = y.compareTo(other.y)
            if (cmpY != 0) {
                return cmpY
            }
            return 0
        }
    }
}
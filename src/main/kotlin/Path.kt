fun main() = Path.solve()

private typealias Matrix = List<List<Int>>

private object Path {
    const val START = -1
    const val TARGET = ('z'.code - 'a'.code) + 1

    fun solve() {
        val input = readInput()
        val start = findPos(input, START)!!
        val target = findPos(input, TARGET)!!
        val graph = buildGraph(input)
        println("Shortest path from $start to $target : ${shortestPath(graph, start, target)} steps")
    }

    private fun readInput(): Matrix {
        return generateSequence(::readLine).map {
            it.splitToSequence("").filter { !it.isEmpty() }.map {
                when (val char = it.first()) {
                    in 'a'..'z' -> char.code - 'a'.code
                    'S' -> START
                    'E' -> TARGET
                    else -> throw Error("Unexpected input char $char")
                }
            }.toList()
        }.toList()
    }

    private fun findPos(matrix: Matrix, target: Int): Pos? {
        for (y in matrix.indices) {
            for (x in matrix[y].indices) {
                if (matrix[y][x] == target) {
                    return Pos(x, y)
                }
            }
        }
        return null
    }

    private fun buildGraph(matrix: Matrix): Map<Pos, Node> {
        val height = matrix.size
        val width = matrix.first().size
        fun neighbours(pos: Pos) = listOf(
            Pos(pos.x - 1, pos.y),
            Pos(pos.x + 1, pos.y),
            Pos(pos.x, pos.y - 1),
            Pos(pos.x, pos.y + 1)
        ).filter { it.x in 0 until width && it.y in 0 until height }

        val result = mutableMapOf<Pos, Node>()
        for (y in matrix.indices) {
            for (x in matrix[y].indices) {
                val pos = Pos(x, y)
                val node = result.getOrPut(pos) { Node(pos, matrix[y][x]) }
                for (nPos in neighbours(pos)) {
                    val nNode = result.getOrPut(nPos) { Node(nPos, matrix[nPos.y][nPos.x]) }
                    if (nNode.height <= node.height + 1) {
                        node.edges.add(nPos)
                    }
                }
            }
        }
        return result
    }

    private fun shortestPath(graph: Map<Pos, Node>, start: Pos, target: Pos): Int {
        val paths = mutableMapOf<Pos, Int>()
        val queue = mutableListOf(Pair(start, 0))
        while (queue.size > 0) {
            val (pos, pathLen) = queue.removeAt(0)
            if (pos in paths) continue
            paths[pos] = pathLen
            for (edge in graph[pos]!!.edges) {
                if (edge in paths) continue
                if (edge == target) {
                    return pathLen + 1
                }
                queue.add(Pair(edge, pathLen + 1))
            }
        }
        return -1
    }

    data class Pos(val x: Int, val y: Int)

    data class Node(val pos: Pos, val height: Int, val edges: MutableList<Pos> = mutableListOf())
}


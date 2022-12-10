import kotlin.math.abs
import kotlin.math.sign

fun main() {
    Rope.solve()
}

private object Rope {
    fun solve() {
        val moves = readInput()
        println("tail visited ${getTailPath(moves, 10).toSet().size}")
    }

    private fun readInput(): Sequence<Move> {
        return generateSequence(::readLine).map {
            val parts = it.split(" ")
            val direction = when (parts[0]) {
                "U" -> Direction.Up
                "D" -> Direction.Down
                "L" -> Direction.Left
                "R" -> Direction.Right
                else -> throw Error("Unexpected direction ${parts[0]}")
            }
            val length = parts[1].toInt(10)
            Move(direction, length)
        }
    }

    private fun getTailPath(moves: Sequence<Move>, ropeSize: Int): List<Pos> {
        var head = Pos(0, 0)
        var rope = Array<Pos>(ropeSize) {head}
        val path = mutableListOf<Pos>()
        moves.forEach {
            println("Move $it")
            for (i in 0 until it.length) {
                head = when (it.direction) {
                    Direction.Up -> Pos(head.x, head.y + 1)
                    Direction.Down -> Pos(head.x, head.y - 1)
                    Direction.Left -> Pos(head.x - 1, head.y)
                    Direction.Right -> Pos(head.x + 1, head.y)
                }
                var prev = head
                rope = Array<Pos>(ropeSize) { i ->
                    if (i == 0)
                        head
                    else {
                        val pos = nextTailPos(prev, rope[i])
                        prev = pos
                        pos
                    }
                }
                path.add(rope.last())
            }
        }
        return path
    }

    private fun nextTailPos(headPos: Pos, tailPos: Pos): Pos {
        val dx = headPos.x - tailPos.x
        val dy = headPos.y - tailPos.y
        if (abs(dx) > 1 || abs(dy) > 1) {
            var (x, y) = tailPos
            x += sign(dx.toDouble()).toInt()
            y += sign(dy.toDouble()).toInt()
            return Pos(x, y)
        }
        return tailPos
    }
}

private data class Pos(val x: Int, val y: Int)
private enum class Direction {
    Up,
    Down,
    Left,
    Right,
}

private data class Move(val direction: Direction, val length: Int)
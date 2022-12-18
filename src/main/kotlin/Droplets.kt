fun main() = Droplets.solve()

private object Droplets {
    fun solve() {
        val input = readInput()
        val droplet = Droplet(input)
        println("total surface area ${droplet.surfaceArea()}")
    }

    private fun readInput(): List<Pos> = generateSequence(::readLine).map {
        val parts = it.split(",").map { it.toInt(10) }
        Pos(parts[0], parts[1], parts[2])
    }.toList()


    data class Pos(val x: Int, val y: Int, val z: Int) {
        fun sides() = listOf(
            Pos(x + 1, y, z), Pos(x - 1, y, z),
            Pos(x, y + 1, z), Pos(x, y - 1, z),
            Pos(x, y, z + 1), Pos(x, y, z - 1)
        )
    }

    class Droplet(val points: List<Pos>) {
        fun surfaceArea(): Int {
            var area = 0

            val pointSet = points.toSet()
            for (point in points) {
                area += 6 - point.sides().count { pointSet.contains(it) }
            }
//            println("${joined.size} $area")
            return area
        }
    }
}
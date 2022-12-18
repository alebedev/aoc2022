fun main() = Droplets.solve()

private object Droplets {
    fun solve() {
        val input = readInput()
        val droplet = Droplet(input)
        println("total surface area ${droplet.surfaceArea1()}")
        println("outside surface area ${droplet.surfaceArea2()}")
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
        fun surfaceArea1(): Int {
            var area = 0

            val pointSet = points.toSet()
            for (point in points) {
                area += 6 - point.sides().count { pointSet.contains(it) }
            }
//            println("${joined.size} $area")
            return area
        }

        fun surfaceArea2(): Int {
            val minX = points.minOf { it.x }
            val maxX = points.maxOf { it.x }
            val minY = points.minOf { it.y }
            val maxY = points.maxOf { it.y }
            val minZ = points.minOf { it.z }
            val maxZ = points.maxOf { it.z }
            val xRange = (minX - 1)..(maxX + 1)
            val yRange = (minY - 1)..(maxY + 1)
            val zRange = (minZ - 1)..(maxZ + 1)
            println("$xRange $yRange $zRange")
            val outsideCells = mutableSetOf<Pos>()
            val pointsSet = points.toSet()
            val q = mutableListOf(Pos(xRange.start, yRange.start, zRange.start))
            while (q.isNotEmpty()) {
                val cell = q.removeFirst()
                if (cell in pointsSet || cell in outsideCells) {
                    continue
                }
                outsideCells.add(cell)
                q.addAll(cell.sides().filter { it.x in xRange && it.y in yRange && it.z in zRange })
            }
            var result = 0
            for (point in pointsSet) {
                result += point.sides().count { it in outsideCells }
            }
            return result
        }
    }
}
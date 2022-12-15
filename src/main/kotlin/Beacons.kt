import kotlin.math.abs

fun main() = Beacons.solve(0..4_000_000)

private object Beacons {
    fun solve(possibleCoordinates: IntRange) {
        val sensors = readInput()
        val distress = findDistressBeacon(sensors, possibleCoordinates)
        println("Distress breacon at $distress, freq: ${distress.x.toBigInteger() * 4_000_000.toBigInteger() + distress.y.toBigInteger()}")
    }

    private fun findDistressBeacon(sensors: List<Sensor>, possibleCoordinates: IntRange): Pos {
        for (y in possibleCoordinates) {
            val ranges: List<IntRange> = rangesAtRow(sensors, y)
            var prev: IntRange? = null
            for (range in ranges) {
                if (prev == null) {
                    prev = range
                    continue
                }
                if (range.last <= prev.last) {
                    continue
                } else if (range.first <= prev.last) {
                    prev = prev.first..range.last
                } else {
                    if (prev.last in possibleCoordinates) {
                        return Pos(prev.last + 1, y)
                    }
                }
            }
        }
        throw Error("Distress beacon not found")
    }

    // Problem 1
    private fun solveIntersections(sensors: List<Sensor>, targetRow: Int) {
        val ranges: List<IntRange> = rangesAtRow(sensors, targetRow)
        // Calculate intersections
        var result = 0
        var currentRange: IntRange? = null
        for (range in ranges) {
            if (currentRange == null) {
                currentRange = range
            } else {
                if (range.last <= currentRange.last) continue
                else if (range.first <= currentRange.last) {
                    currentRange = currentRange.first..range.last
                } else {
                    result += currentRange.last - currentRange.start + 1
                    currentRange = range
                }
            }
        }
        if (currentRange != null) {
            result += currentRange.last - currentRange.start + 1
        }
        println("Beacon cannot take $result positions in row $targetRow")
    }

    private fun readInput(): List<Sensor> = generateSequence(::readLine).map {
        val groupValues =
            "Sensor at x=([-\\d]+), y=([-\\d]+): closest beacon is at x=([-\\d]+), y=([-\\d]+)".toRegex()
                .matchEntire(it)!!.groupValues
        Sensor(
            Pos(groupValues[1].toInt(10), groupValues[2].toInt(10)),
            Pos(groupValues[3].toInt(10), groupValues[4].toInt(10))
        )
    }.toList()

    private fun rangesAtRow(sensors: List<Sensor>, targetRow: Int): List<IntRange> =
        sensors.filter { abs(targetRow - it.pos.y) <= it.distanceToBeacon() }.map {
            val dx = it.distanceToBeacon() - abs(targetRow - it.pos.y)
            (it.pos.x - dx)..(it.pos.x + dx)
        }.sortedBy { it.start }

    data class Pos(val x: Int, val y: Int) {
        fun distanceTo(other: Pos): Int = abs(x - other.x) + abs(y - other.y)
    }

    data class Sensor(val pos: Pos, val nearestBeacon: Pos) {
        fun distanceToBeacon(): Int = pos.distanceTo(nearestBeacon)
    }
}
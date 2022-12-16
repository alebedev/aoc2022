fun main() = Valves.solve()

private object Valves {
    fun solve() {
        val nodes = readInput()
        val maxPressure = findMaxPressureRelease(nodes, "AA", 26)
        println("Max releasable pressure: ${maxPressure}")
    }

    private fun readInput(): Map<String, Node> = buildMap<String, Node> {
        generateSequence(::readLine).forEach {
            val groupValues =
                "Valve (\\w+) has flow rate=(\\d+); tunnels? leads? to valves? ([\\w ,]+)".toRegex()
                    .matchEntire(it)!!.groupValues
            val node = Node(
                groupValues[1],
                groupValues[2].toInt(10),
                groupValues[3].split(", ").toList()
            )
            put(node.label, node)
        }
    }

    private fun findMaxPressureRelease(nodes: Map<String, Node>, startPos: String, remainingTime: Int): Int {
        val distances = mutableMapOf<String, Int>()

        fun distanceKey(a: String, b: String) = listOf(a, b).sorted().joinToString("")

        fun distanceBetween(a: String, b: String): Int {
            if (a == b) {
                return 0
            }
            if (distanceKey(a, b) in distances) {
                return distances[distanceKey(a, b)]!!
            }
            val queue = mutableListOf(a)
            while (queue.isNotEmpty()) {
                val item = queue.removeFirst()
                if (item == b) {
                    return distances[distanceKey(a, item)]!!
                }
                val dist = distanceBetween(a, item)
                for (edge in nodes[item]!!.edges) {
                    if (distanceKey(a, edge) !in distances) {
                        distances[distanceKey(a, edge)] = dist + 1
                    }
                    if (edge !in queue) {
                        queue.add(edge)
                    }
                }
            }
            throw Error("Path not found $a->$b")
        }

        fun pathTime(path: List<String>): Int {
            var result = 0
            var prev = startPos
            for (item in path) {
                result += distanceBetween(prev, item) + 1
                prev = item
            }
            return result
        }

        fun pathFlowPair(first: List<String>, second: List<String>): Int {
            var result = 0
            var flow = 0
            val openValves = mutableSetOf<String>()
            fun openValve(valve: String) {
                if (valve in openValves) return
                openValves.add(valve)
                flow += nodes[valve]!!.pressure
            }

            val pathA = first.toMutableList()
            val pathB = second.toMutableList()
            var a = startPos
            var b = startPos
            var timeA = 0
            var timeB = 0
            var nextA: String? = null
            var nextB: String? = null
            for (i in 0..remainingTime) {
                result += flow
                if (i == timeA) {
                    if (i > 0 && nextA != null) {
                        a = nextA
                        openValve(a)
                    }
                    nextA = pathA.removeFirstOrNull()
                    if (nextA != null) {
                        timeA += distanceBetween(a, nextA) + 1
                    }
                }
                if (i == timeB) {
                    if (i > 0 && nextB != null) {
                        b = nextB
                        openValve(b)
                    }
                    nextB = pathB.removeFirstOrNull()
                    if (nextB != null) {
                        timeB += distanceBetween(b, nextB) + 1
                    }

                }
            }
            return result
        }

        val paths = mutableSetOf<List<String>>(listOf())
        val valves = nodes.keys.filter { nodes[it]!!.pressure > 0 }
        for (level in 0..valves.size) {
            println("$level")
            val newItems = mutableSetOf<List<String>>()
            for (valve in valves) {
                for (prefix in paths) {
                    if (valve in prefix) {
                        continue
                    }

                    val path = prefix.plus(valve)
                    if (path in paths) {
                        continue
                    }
//                    println("$valve $prefix")
                    if (pathTime(path) < remainingTime) {
                        newItems.add(path)
                    }
                }
            }

            if (!paths.addAll(newItems)) {
                break
            } else {
                //println("NI ${newItems.size}")
            }
        }
        println("Number of paths ${paths.size}")
        var max = 0
        for (a in paths) {
            for (b in paths) {
                max = maxOf(max, pathFlowPair(a, b))
            }
        }
//        println("Distance ${distanceBetween("BB", "JJ")}")
//        val bestPath = paths.sortedBy { pathFlow(it) }.last()
//        println("Best path $bestPath")
        return max
    }

    data class Node(val label: String, val pressure: Int, val edges: List<String>)
}
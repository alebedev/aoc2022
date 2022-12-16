fun main() = Valves.solve()

private object Valves {
    fun solve() {
        val nodes = readInput()
        val maxPressure = findMaxPressureRelease(nodes, "AA", 30)
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
        data class State(val openValves: Set<String>, val pos: String) {
            fun releasePerMin() = openValves.sumOf { nodes[it]!!.pressure }
        }

        val initialState = State(emptySet(), startPos)

        val bestTimeFor = mutableMapOf<State, Int>()

        // DFS
        fun maxFor(state: State, remainingTime: Int): Int {
            if (remainingTime <= 1) {
                return state.releasePerMin() * remainingTime
            }
            val variants = mutableSetOf<State>()
            if (state.pos !in state.openValves) {
                variants.add(
                    State(
                        state.openValves.plus(state.pos),
                        state.pos
                    )
                )
            }
            variants.addAll(nodes[state.pos]!!.edges.map { edge ->
                State(
                    state.openValves,
                    edge
                )
            })
            variants.removeIf {
                bestTimeFor.getOrDefault(it, 0) >= remainingTime - 1
            }
            variants.forEach {
                bestTimeFor[it] = remainingTime - 1
            }
            var result = state.releasePerMin() * remainingTime
            if (variants.isNotEmpty()) {
//                println("variants")
                result = maxOf(result, variants.maxOf { state.releasePerMin() + maxFor(it, remainingTime - 1) })
            }
            return result
        }

        return maxFor(initialState, remainingTime)
    }

    data class Node(val label: String, val pressure: Int, val edges: List<String>)
}
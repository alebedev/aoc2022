fun main() = Robots.solve()

private object Robots {
    const val MAX_TURNS = 24

    fun solve() {
        val blueprints = readInput()
        val scores = blueprints.map { maxGeodes(it) * it.id }.sum()
        println("Total scores: ${scores}")
    }

    private fun readInput(): List<Blueprint> = generateSequence(::readLine).map { line ->
        val match = "Blueprint (\\d+):(.+)".toRegex().matchEntire(line)!!
        val id = match.groupValues[1].toInt(10)
        val recipes = buildMap<Resource, Recipe> {
            match.groupValues[2].split(".").filterNot { it.isEmpty() }.forEach { recipe ->
                val match = "\\s*Each (\\w+) robot costs (.+)".toRegex().matchEntire(recipe)
                    ?: throw Error("Recipe match failed")
                val resource = parseResource(match.groupValues[1])
                val ingredients = parseIngredients(match.groupValues[2])
                put(resource, Recipe(resource, ingredients))
            }
        }
        Blueprint(id, recipes)
    }.toList()

    private fun parseIngredients(string: String): Map<Resource, Int> {
        return buildMap {
            string.split(" and ".toRegex()).forEach {
                val values = "(\\d+) (\\w+)".toRegex().matchEntire(it)!!.groupValues
                put(parseResource(values[2]), values[1].toInt(10))
            }
        }
    }

    private fun maxGeodes(blueprint: Blueprint): Int {
        println("Max for ${blueprint.id}...")
        fun nextSteps(step: Step): List<Step> {
            val result = mutableListOf(step.nextStepWithIdle())
            for (resource in Resource.values()) {
                val cost = blueprint.recipes[resource]!!.cost
                if (step.hasEnough(cost)) {
                    result.add(step.nextStepWithBuild(resource, cost))
                }
            }
            return result
        }

        val queue = mutableListOf<Step>(
            Step(mapOf(), mapOf(Pair(Resource.Ore, 1)), null)
        )

        var maxGeodes = 0
        var i = 0
        while (queue.isNotEmpty()) {
            i += 1
            val step = queue.removeFirst()
            if (i % 100_000 == 0) {
                println("step $i, depth=${step.turnsTotal()}, max=$maxGeodes")
            }
            val geodes = step.resources.getOrDefault(Resource.Geode, 0)
            maxGeodes = maxOf(maxGeodes, geodes)
            if (step.turnsTotal() >= MAX_TURNS) {
                continue
            }
            if (geodes + 2 < maxGeodes) {
                continue
            }
            queue.addAll(nextSteps(step))
        }
        return maxGeodes
    }

    private data class Blueprint(val id: Int, val recipes: Map<Resource, Recipe>)

    private data class Recipe(val robotType: Resource, val cost: Map<Resource, Int>)

    private enum class Resource {
        Ore, Clay, Obsidian, Geode,
    }

    private fun parseResource(string: String): Resource = when (string) {
        "ore" -> Resource.Ore
        "clay" -> Resource.Clay
        "obsidian" -> Resource.Obsidian
        "geode" -> Resource.Geode
        else -> throw Error("Failed to parse resource: $string")
    }

    private data class Step(val resources: Map<Resource, Int>, val robots: Map<Resource, Int>, val parent: Step?) {
        fun turnsTotal(): Int = (parent?.turnsTotal() ?: 0) + 1
        fun nextStepWithIdle(): Step = Step(
            nextResources(),
            robots,
            this
        )

        fun nextStepWithBuild(robotType: Resource, cost: Map<Resource, Int>): Step {
            val resources = nextResources().mapValues {
                it.value - cost.getOrDefault(it.key, 0)
            }
            val nextRobots = robots.toMutableMap()
            nextRobots[robotType] = robots.getOrDefault(robotType, 0) + 1
            return Step(resources, nextRobots, this)
        }

        fun hasEnough(cost: Map<Resource, Int>): Boolean =
            cost.entries.all {
                resources.getOrDefault(it.key, 0) >= it.value
            }

        private fun nextResources(): Map<Resource, Int> = buildMap {
            Resource.values().forEach {
                put(it, resources.getOrDefault(it, 0) + robots.getOrDefault(it, 0))
            }
        }
    }
}
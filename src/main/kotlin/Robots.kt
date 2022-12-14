fun main() = Robots.solve()

private object Robots {
    const val MAX_TURNS = 32

    fun solve() {
        val blueprints = readInput().take(3)
//        println("1st: ${maxGeodes(blueprints.first())}")
        val score = blueprints.map { maxGeodes(it) }.reduce { x, y -> x * y }
        println("Total score: ${score}")
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
        println("Max for ${blueprint}...")
        val maxCost = buildMap {
            Resource.values().forEach { resource ->
                put(resource, blueprint.recipes.maxOf { it.value.cost.getOrDefault(resource, 0) })
            }
        }.plus(Pair(Resource.Geode, Int.MAX_VALUE))
        println("Max costs $maxCost")
        fun isGoodStep(step: Step, resource: Resource): Boolean {
//            println("$resource, $maxCost")
            if (resource === Resource.Geode) {
                return true
            }
            if (step.robots.getOrDefault(resource, 0) > maxCost[resource]!!) {
                return false
            }
            return true
        }

        fun normalizeNextStep(nextStep: Step): Step {
            val resources = nextStep.resources.mapValues { it -> minOf(it.value, maxCost[it.key]!! * 3) }
            return Step(resources, nextStep.robots, nextStep.time)
        }

        fun nextSteps(step: Step): List<Step> {
            val result = mutableListOf<Step>()
            for (resource in Resource.values()) {
                val cost = blueprint.recipes[resource]!!.cost
                if (step.hasEnough(cost) && isGoodStep(step, resource)) {
                    val nextStep = normalizeNextStep(step.nextStepWithBuild(resource, cost))
                    if (resource == Resource.Geode) {
                        return listOf(nextStep)
                    } else {
                        result.add(nextStep)
                    }
                }
            }
            if (result.size < Resource.values().size) {
                result.add(step.nextStepWithIdle())
            }
            return result
        }


        val stack = mutableListOf<Step>(
            Step(mapOf(), mapOf(Pair(Resource.Ore, 1)), 1)
        )
        val seenStates = mutableSetOf<String>()
        val maxGeodesAtStep = mutableMapOf<Int, Int>()

        var maxGeodes = 0
        var i = 0
        while (stack.isNotEmpty()) {
            val step = stack.removeLast()
            if (step.fingerprint() in seenStates) {
                continue
            }
            i += 1
            seenStates.add(step.fingerprint())
            if (i % 1_000_000 == 0) {
                println("step ${i / 1_000_000}M, depth=${step.time}, max=$maxGeodes, stack size=${stack.size}")
            }

            val oldMax = maxGeodes
            val geodes = step.resources.getOrDefault(Resource.Geode, 0)
            maxGeodes = maxOf(maxGeodes, geodes)
            maxGeodesAtStep[step.time] = maxOf(geodes, maxGeodesAtStep.getOrDefault(step.time, 0))

            if (maxGeodes > oldMax && step.time == MAX_TURNS + 1) {
                println("New best found $i ${step.robots} -> $maxGeodes")
            }

            val remainingTime = 1 + MAX_TURNS - step.time
            if (remainingTime <= 0) {
                continue
            }

            val maxPossibleGeodes =
                geodes + step.robots.getOrDefault(Resource.Geode, 0) * remainingTime + (1..remainingTime).sum()
            if (maxPossibleGeodes <= maxGeodes) {
                continue
            }

            if (maxGeodesAtStep[step.time]!! > geodes + 2) {
                continue
            }
//            if (geodes + 2 < maxGeodes) {
//                continue
//            }
            stack.addAll(nextSteps(step))
        }
        println("$maxGeodesAtStep")
        println("max= ${maxGeodes}")
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

    private data class Step(val resources: Map<Resource, Int>, val robots: Map<Resource, Int>, val time: Int) {
        fun nextStepWithIdle(): Step = Step(
            nextResources(),
            robots,
            time + 1
        )

        fun nextStepWithBuild(robotType: Resource, cost: Map<Resource, Int>): Step {
            val resources = nextResources().mapValues {
                it.value - cost.getOrDefault(it.key, 0)
            }
            val nextRobots = robots.toMutableMap()
            nextRobots[robotType] = robots.getOrDefault(robotType, 0) + 1
            return Step(resources, nextRobots, time + 1)
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

        fun fingerprint(): String = "$time.${
            Resource.values().map { robots.getOrDefault(it, 0) }.joinToString("_")
        }.${
            Resource.values().map { resources.getOrDefault(it, 0) }.joinToString("_")
        }"
    }
}
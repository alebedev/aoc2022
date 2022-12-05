// Day4
fun main() {
    val sections = readInput()
    val fullyContained = sections.count { fullyContains(it.first, it.second) || fullyContains(it.second, it.first) }
    val overlapping = sections.count { overlaps(it.first, it.second) }
    println("# of fully contained ${fullyContained}")
    println("# of overlapping ${overlapping}")
}

private fun readInput(): Iterable<Section> {
    val result = mutableListOf<Section>()
    generateSequence(::readLine).forEach {
        val segments = it.split(',').map(::parsePair)
        result.add(Pair(segments[0], segments[1]))
    }
    return result
}

private typealias Segment = Pair<Int, Int>
private typealias Section = Pair<Segment, Segment>

private fun parsePair(string: String) : Pair<Int, Int> {
    val parts = string.split('-')
    return Pair(parts[0].toInt(10), parts[1].toInt(10))
}
private fun fullyContains(a: Segment, b: Segment) = a.first <= b.first && a.second >= b.second

private fun overlaps(a: Segment, b: Segment) = (a.first <= b.first && a.second >= b.first) || (a.first <= b.second && a.second >= b.first)
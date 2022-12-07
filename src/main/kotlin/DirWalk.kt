fun main() {
    DirWalk.solve()
}

private object DirWalk {
    fun solve() {
        val tree = buildTree(readInput())
        val sizes = getDirSizes(tree)
//        for (size in sizes) {
//            println("${size.key.name} ${size.value}")
//        }
        val smallDirsTotal = sizes.filter { it.value <= 100_000 }.values.sum()
        println("Small dirs total: $smallDirsTotal")
        val totalSpace = 70_000_000
        val targetFreeSpace = 30_000_000
        val spaceToFree = targetFreeSpace - (totalSpace - sizes.getValue(tree))
        println("Space to free: $spaceToFree")
        val sortedDirs = sizes.toList().sortedBy { it.second }
        val smallestToDelete = sortedDirs.find { it.second >= spaceToFree }
        println("Delete dir ${smallestToDelete?.first?.name}, size: ${smallestToDelete?.second}")
    }

    private fun readInput(): Sequence<Input> {
        return generateSequence(::readLine).map(::parseLine)
    }

    private fun buildTree(input: Sequence<Input>): Dir {
        val root = Dir("/", mutableListOf<Node>())
        var dir: Dir = root
        val stack = mutableListOf<Dir>()
        for (item in input) {
            when (item) {
                is CommandCd -> {
                    when (item.path) {
                        "/" -> {
                            dir = root
                            stack.clear()
                            stack.add(dir)
                        }
                        ".." -> {
                            stack.removeLast()
                            dir = stack.last()
                        }
                        else -> {
                            dir = dir.items.find { if (it is Dir) it.name == item.path else false } as Dir
                            stack.add(dir)
                        }
                    }
                }
                is CommandLs -> {}
                is File -> dir.items.add(item)
                is Dir -> dir.items.add(item)
            }
        }
        return root
    }

    private fun parseLine(line: String): Input {
        return when {
            line == "$ ls" -> CommandLs()
            line.startsWith("$ cd") -> CommandCd(line.substringAfter("$ cd "))
            line.startsWith("dir") -> Dir(line.substringAfter("dir "))
            else -> {
                val parts = line.split(" ")
                val size = parts[0].toInt(10)
                File(parts[1],size)
            }
        }
    }

    private fun getDirSizes(root: Dir): Map<Dir, Int> {
        val result = mutableMapOf<Dir, Int>()
        fun walk(dir: Dir) {
            var current = 0
            for (item in dir.items) {
                current += when (item) {
                    is File -> item.size
                    is Dir -> {
                        walk(item)
                        result.getValue(item)
                    }
                }
            }
            result[dir] = current
        }
        walk(root)
        return result
    }

    sealed interface Input
    data class CommandCd(val path: String) : Input
    data class CommandLs(val path: String = ""): Input

    sealed interface Node
    data class File(val name: String, val size: Int): Node, Input
    data class Dir(val name: String, val items: MutableList<Node> = mutableListOf()): Node, Input
}


fun main() = Calculator.solve()

private object Calculator {
    fun solve() {
        val expressions = readInput()
        println("Part 1: root value ${calcValue(expressions, "root")}")
        println("Part 2, humn requires value ${matchValuesAt(expressions, "root")}")
    }

    private fun readInput(): Map<String, Expr> = buildMap {
        generateSequence(::readLine).forEach { line ->
            val parts = line.split(":")
            val label = parts.get(0).trim()
            val expr = parts.get(1).trim()
            when {
                expr.contains(" ") -> {
                    val parts = expr.split(" ")
                    val op = when (parts.get(1)) {
                        "+" -> Op.Plus
                        "-" -> Op.Minus
                        "*" -> Op.Mul
                        "/" -> Op.Div
                        else -> throw Error("Unexpected operation: ${parts.get(1)}")
                    }
                    put(label, Calc(parts[0], parts[2], op))

                }

                else -> {
                    put(label, Just(expr.toLong(10)))
                }
            }
        }
    }

    private fun calcValue(expressions: Map<String, Expr>, label: String): Long {
        val values = mutableMapOf<String, Long>()

        fun getValue(label: String): Long {
            var value = values[label]
            if (value == null) {
                val expr = expressions.getValue(label)
                value = when (expr) {
                    is Just ->
                        expr.value

                    is Calc -> {
                        val a = getValue(expr.a)
                        val b = getValue(expr.b)
                        expr.operation.calc(a, b)
                    }
                }
                values[label] = value
            }
            return value
        }

        return getValue(label)
    }

    private fun matchValuesAt(expressions: Map<String, Expr>, rootLabel: String): Long {
        val root = expressions.getValue(rootLabel) as Calc

        fun findDeps(start: String, needle: String): List<String> {
            val stack = mutableListOf(listOf(start))
            while (stack.isNotEmpty()) {
                val item = stack.removeFirst()
                if (item.first() == needle) {
                    return item
                }
                val expr = expressions.getValue(item.first())
                if (expr is Just) {
                    continue
                }
                if (expr is Calc) {
                    stack.add(listOf(expr.a).plus(item))
                    stack.add(listOf(expr.b).plus(item))
                }
            }
            return listOf()
        }

        val aDeps = findDeps(root.a, "humn")
        val bDeps = findDeps(root.b, "humn")
        if (aDeps.isNotEmpty() && bDeps.isNotEmpty()) {
            TODO("Dont know how to solve this yet")
        } else if (aDeps.isEmpty() && bDeps.isEmpty()) {
            throw Error("Failed to find dep list to humn")
        }
        val (targetValue, deps) = if (aDeps.isNotEmpty()) {
            println("A: ${root.a}")
            Pair(calcValue(expressions, root.b), aDeps)
        } else {
            println("B: ${root.b}")
            Pair(calcValue(expressions, root.a), bDeps)
        }
        println("$root, targetValue: $targetValue, deps: $deps")
        val targetValues = mutableMapOf(Pair(deps.last(), targetValue))
        println("$targetValues")
        for (pair in deps.reversed().windowed(2, 1)) {
            val calc = expressions.getValue(pair.first()) as Calc
            val next = pair.last()
            val other = if (next == calc.a) calc.b else calc.a
            val otherValue = calcValue(expressions, other)
            val target = targetValues.getValue(pair.first())
            val nextTarget = when (calc.operation) {
                Op.Plus -> target - otherValue
                Op.Mul -> target / otherValue
                Op.Minus -> if (next == calc.a) target + otherValue else otherValue - target
                Op.Div -> if (next == calc.a) target * otherValue else otherValue / target
            }
            targetValues[next] = nextTarget
        }
        return 0
    }

    sealed interface Expr

    data class Just(val value: Long) : Expr
    data class Calc(val a: String, val b: String, val operation: Op) : Expr

    enum class Op {
        Plus {
            override fun calc(a: Long, b: Long): Long = a + b
        },
        Minus {
            override fun calc(a: Long, b: Long): Long = a - b
        },
        Mul {
            override fun calc(a: Long, b: Long): Long = a * b
        },
        Div {
            override fun calc(a: Long, b: Long): Long = a / b
        };

        abstract fun calc(a: Long, b: Long): Long

    }
}
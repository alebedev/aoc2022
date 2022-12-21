fun main() = Calculator.solve()

private object Calculator {
    fun solve() {
        val expressions = readInput()
        println("Root value ${calcValue(expressions, "root")}")
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
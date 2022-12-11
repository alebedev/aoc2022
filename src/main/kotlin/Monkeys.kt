import java.math.BigInteger

fun main() = Monkeys.solve()

object Monkeys {
    private var inspectedBy = mutableListOf<Int>()

    fun solve() {
        val input = readInput()
        for (monkey in input) {
            inspectedBy.add(monkey.id, 0)
        }
        for (turn in 1..10000) {
            runTurn(input)
//            println("After turn $turn, items by monkey ${input.map { it.items }}")
        }
        val inspected = inspectedBy.sorted().takeLast(2)
        println("Inspected: $inspected, score: ${inspected.first().toBigInteger() * inspected.last().toBigInteger()}")
    }

    private fun readInput(): List<Monkey> {
        var id = 0
        var items = mutableListOf<BigInteger>()
        var operation: Operation? = null
        var testDiv = 0
        var ifTrue = 0
        var ifFalse = 0
        val result = mutableListOf<Monkey>()

        fun addMonkey() {
            if (operation == null) {
                throw Error("Invalid input, no operation before empty line")
            } else {
                val test = Test(testDiv, ifTrue, ifFalse)
                result.add(id, Monkey(id, items, operation!!, test))
            }
        }

        generateSequence(::readLine).forEach {
            when {
                it.startsWith(monkeyPrefix) -> {
                    id = it.substringAfter(monkeyPrefix).substringBefore(":").toInt(10)
                    items = mutableListOf<BigInteger>()
                    testDiv = 0
                    ifTrue = 0
                    ifFalse = 0
                }
                it.startsWith(itemsPrefix) -> {
                    items = it.substringAfter(itemsPrefix).split(", ").map { it.toBigInteger(10) }.toMutableList()
                }
                it.startsWith(operationPrefix) -> {
                    operation = parseOperation(it)
                }
                it.startsWith(testPrefix) -> {
                    testDiv = it.substringAfter(testPrefix).toInt(10)
                }
                it.startsWith(ifTruePrefix) -> {
                    ifTrue = it.substringAfter(ifTruePrefix).toInt(10)
                }
                it.startsWith(ifFalsePrefix) -> {
                    ifFalse = it.substringAfter(ifFalsePrefix).toInt(10)
                }
                (it == "") -> {
                    addMonkey()
                }
            }
        }
        addMonkey()
        return result
    }

    private fun runTurn(input: List<Monkey>) {
        for (monkey in input) {
            runRound(input, monkey)
//            println("round ${monkey.id} ends")
        }
    }

    private fun runRound(input: List<Monkey>, monkey: Monkey) {
        val base = input.map { it.test.testDiv }.reduce {acc, x -> x * acc}.toBigInteger()
        for (item in monkey.items) {
            val worry = monkey.operation.exec(item).mod(base)
            inspectedBy[monkey.id] += 1
//            println("Item $item, worry after inspection $worry")
            val target = if (worry.mod(monkey.test.testDiv.toBigInteger()) == BigInteger.ZERO) {
                input[monkey.test.ifTrue]
            } else {
                input[monkey.test.ifFalse]
            }
            target.items.add(worry)
//            println("Passing to monkey ${target.id}")
        }
        monkey.items.clear()
    }
}

val monkeyPrefix = "Monkey "
val itemsPrefix = "  Starting items: "
val operationPrefix = "  Operation: new = "
val testPrefix = "  Test: divisible by "
val ifTruePrefix = "    If true: throw to monkey "
val ifFalsePrefix = "    If false: throw to monkey "

private data class Monkey(val id: Int, val items: MutableList<BigInteger>, val operation: Operation, val test: Test)

sealed interface OpInput
object Old : OpInput
data class Just(val value: BigInteger): OpInput

enum class Op {
    Plus,
    Mul
}

data class Operation(val a: OpInput, val b: OpInput, val op: Op) {
    fun exec(input: BigInteger): BigInteger {
        val x = when (a) {
            Old -> input
            is Just -> a.value
        }
        val y = when (b) {
            Old -> input
            is Just -> b.value
        }
        return when (op) {
            Op.Plus -> x + y
            Op.Mul -> x * y
        }
    }
}

private fun parseOperation(line: String): Operation {
    val match = "(\\w+) ([+*]) (\\w+)".toRegex().matchEntire(line.substringAfter(operationPrefix))
    val op = when (match!!.groupValues[2]) {
        "*" -> Op.Mul
        "+" -> Op.Plus
        else -> throw Error("Unexpected op")
    }
    val a = parseOpInput(match.groupValues[1])
    val b = parseOpInput(match.groupValues[3])
    return Operation(a, b, op)
}


private fun parseOpInput(string: String): OpInput = when (string) {
    "old" -> Old
    else -> Just(string.toBigInteger(10))
}

private data class Test(val testDiv: Int, val ifTrue: Int, val ifFalse: Int)
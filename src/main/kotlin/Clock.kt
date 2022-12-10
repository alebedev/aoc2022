import java.lang.Error

fun main() = Clock.solve()

private object Clock {
    fun solve() {
        val input = readInput()
        val cpuStates = mutableListOf(Cpu(1))
        for (instruction in input) {
            cpuStates.addAll(instruction.exec(cpuStates.last()))
        }
        fun signalStength(i: Int) = i * cpuStates[i - 1].x
        var total = 0
        for (i in listOf(20, 60, 100, 140, 180, 220)) {
            println("$i x=${cpuStates[i].x}")
            println("$i: ${signalStength(i)}")
            total += signalStength(i)
        }
        println("Total $total")
    }

    private fun readInput(): List<Instruction> {
        return generateSequence(::readLine).map {
            when {
                (it == "noop") -> Noop
                it.startsWith("addx ") -> Add(it.substringAfter("addx ").toInt(10))
                else -> throw Error("!!")
            }
        }.toList()
    }

    data class Cpu(val x: Int)

    sealed interface Instruction {
        fun exec(cpu: Cpu): List<Cpu>
    }
    object Noop : Instruction {
        override fun exec(cpu: Cpu) = listOf(cpu)
    }
    data class Add(val value: Int): Instruction {
        override fun exec(cpu: Cpu) = listOf(cpu, Cpu(cpu.x + value))
    }
}
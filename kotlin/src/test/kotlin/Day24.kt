import org.junit.jupiter.api.Test
import java.io.File
import kotlin.math.pow
import kotlin.test.assertEquals

class Day24 {

    @Test
    fun part1DivTest() {
        val program = parse(
            """
                inp x
                div x 3
            """.trimIndent()
        )

        val reg1 = run(
            program,
            listOf("50")
        )

        assertEquals(16L, reg1['x'])

        val reg2 = run(
            program,
            listOf("-50")
        )

        assertEquals(-16L, reg2['x'])
    }

    @Test
    fun part1Test1() {
        val reg = run(
            parse(
                """
                inp x
                mul x -1
            """.trimIndent()
            ),
            listOf("12345")
        )

        assertEquals(-12345L, reg['x'])
    }

    @Test
    fun part1Test2() {
        val program = parse(
            """
                inp z
                inp x
                mul z 3
                eql z x
            """.trimIndent()
        )
        val reg1 = run(
            program,
            listOf("1", "3")
        )

        assertEquals(1L, reg1['z'])

        val reg2 = run(
            program,
            listOf("2", "3")
        )

        assertEquals(0L, reg2['z'])
    }

    @Test
    fun part1Test3() {
        val program = parse(
            """
                inp w
                add z w
                mod z 2
                div w 2
                add y w
                mod y 2
                div w 2
                add x w
                mod x 2
                div w 2
                mod w 2
            """.trimIndent()
        )

        val reg1 = run(
            program,
            listOf("0")
        )

        assertEquals(0L, reg1['w'])
        assertEquals(0L, reg1['x'])
        assertEquals(0L, reg1['y'])
        assertEquals(0L, reg1['z'])

        val reg2 = run(
            program,
            listOf("15")
        )

        assertEquals(1L, reg2['w'])
        assertEquals(1L, reg2['x'])
        assertEquals(1L, reg2['y'])
        assertEquals(1L, reg2['z'])
    }

    @Test
    fun part1() {
        val result = runDepth(0, 0, mutableMapOf(), 9 downTo 1)

        assertEquals(99919765949498L, result)
    }

    @Test
    fun part2() {
        val result = runDepth(0, 0, mutableMapOf(), 1..9)

        assertEquals(24913111616151L, result)
    }

    fun runDepth(zVal: Long, level: Int, cache: MutableMap<Pair<Long, Int>, Long?>, digitRange: IntProgression): Long? {
        if(cache.containsKey(Pair(zVal, level))) {
            return cache[Pair(zVal, level)]
        }

        if(level == 14) {
            return if(zVal == 0L) 0L else null
        }

        cache[Pair(zVal, level)] = null

        val registers = mapOf(
            'w' to 0L,
            'x' to 0L,
            'y' to 0L,
            'z' to zVal,
        )

        val program = modelNumberProgram.drop(level*18).take(18)

        for(i in digitRange) {
            val newZVal = runWithRegisters(program, listOf(i.toString()), registers)['z']!!
            val solution = runDepth(newZVal, level + 1, cache, digitRange)
            if(solution != null) {
                val pow = 10.0.pow(13 - level.toDouble()).toLong()
                cache[Pair(zVal, level)] = solution + (i * pow)
                return cache[Pair(zVal, level)]
            }
        }

        return cache[Pair(zVal, level)]
    }


    val modelNumberProgram = File(ClassLoader.getSystemResource("aoc-2021-24.txt").file).readLines()

    fun run(program: List<String>, input: List<String>): Map<Char, Long> {
        val registers = mapOf(
            'w' to 0L,
            'x' to 0L,
            'y' to 0L,
            'z' to 0L,
        )

        return runWithRegisters(program, input, registers)
    }

    private fun runWithRegisters(
        program: List<String>,
        input: List<String>,
        registers: Map<Char, Long>
    ): Map<Char, Long> {
        val (_, lastReg, _) = program.fold(Triple(input, registers, listOf(registers))) { (inp, reg, hist), inst ->
            val (remainingInp, newRegisters) = executeInstruction(inst, inp, reg)
            Triple(remainingInp, newRegisters, hist + listOf(newRegisters))
        }

        return lastReg
    }

    fun executeInstruction(
        inst: String,
        input: List<String>,
        registers: Map<Char, Long>
    ): Pair<List<String>, Map<Char, Long>> {
        val params = inst.split(" ")
        return when (params[0]) {
            "inp" -> {
                Pair(
                    input.drop(1),
                    updateReg(
                        params[1][0],
                        input[0].toLong(),
                        registers
                    )
                )
            }
            "add" -> {
                Pair(
                    input,
                    executeInstruction(params[1], params[2], registers) { a, b -> a + b }
                )
            }
            "mul" -> {
                Pair(
                    input,
                    executeInstruction(params[1], params[2], registers) { a, b -> a * b }
                )
            }
            "div" -> {
                Pair(
                    input,
                    executeInstruction(params[1], params[2], registers) { a, b -> a / b }
                )
            }
            "mod" -> {
                Pair(
                    input,
                    executeInstruction(params[1], params[2], registers) { a, b -> a % b }
                )
            }
            "eql" -> {
                Pair(
                    input,
                    executeInstruction(params[1], params[2], registers) { a, b -> if (a == b) 1 else 0 }
                )
            }
            else -> {
                throw RuntimeException("Invalid instruction: ${params[0]}")
            }
        }
    }

    private fun executeInstruction(
        a: String,
        b: String,
        registers: Map<Char, Long>,
        op: (Long, Long) -> Long
    ): Map<Char, Long> {
        val aRegName = a[0]
        val aVal = registers[aRegName]!!
        val bVal = b.toLongOrNull() ?: registers[b[0]]!!
        return updateReg(
            aRegName,
            op.invoke(aVal, bVal),
            registers
        )
    }

    private fun updateReg(
        regName: Char,
        value: Long,
        registers: Map<Char, Long>
    ): Map<Char, Long> {
        return registers.mapValues { (k, v) ->
            if (k == regName) {
                value
            } else {
                v
            }
        }
    }

    fun parse(input: String): List<String> {
        return input.split("\n")
    }
}

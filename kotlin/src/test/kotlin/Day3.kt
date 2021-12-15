import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import java.io.File

class Day3 {
    @Test
    fun part1() {
        val testResult = runPart1("aoc-2021-3-test.txt")
        assertEquals(198, testResult)

        val result = runPart1("aoc-2021-3.txt")
        assertEquals(3882564, result)
    }

    private fun runPart1(path: String): Int {
        val lines = File(ClassLoader.getSystemResource(path).file).readLines()

        val gamma = lines[0].indices.map { lines.mostCommon(it) }.joinToString("")
        val epsilon = gamma.map { it.inverse() }.joinToString("")

        return gamma.toInt(2) * epsilon.toInt(2)
    }

    @Test
    fun part2() {
        val testResult = runPart2("aoc-2021-3-test.txt")
        assertEquals(230, testResult)

        val result = runPart2("aoc-2021-3.txt")
        assertEquals(3385170, result)
    }

    private fun runPart2(path: String): Int {
        val lines = File(ClassLoader.getSystemResource(path).file).readLines()

        var oxy = lines
        var co2 = lines

        lines[0].indices.forEach { idx ->
            if (oxy.size > 1) {
                oxy = oxy.filter { it[idx] == oxy.mostCommon(idx) }
            }

            if (co2.size > 1) {
                co2 = co2.filter { it[idx] == co2.mostCommon(idx).inverse() }
            }
        }

        return oxy[0].toInt(2) * co2[0].toInt(2)
    }

    private fun Char.inverse(): Char {
        return if (this == '1') '0' else '1'
    }

    private fun List<String>.mostCommon(idx: Int): Char {
        val ones = this
            .map { it[idx] }
            .count { it == '1' }

        return if (0.5 <= ones/this.size.toDouble()) '1' else '0'
    }
}

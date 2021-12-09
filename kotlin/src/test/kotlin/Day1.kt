import org.junit.jupiter.api.Test
import java.io.File
import kotlin.test.assertEquals

class Day1 {

    @Test
    fun part1() {
        assertEquals(7, runPart1("aoc-2021-1-test.txt"))
        assertEquals(1681, runPart1("aoc-2021-1.txt"))
    }

    private fun runPart1(path: String): Int {
        val lines = File(ClassLoader.getSystemResource(path).file).readLines()

        return lines.asSequence()
            .map { it.toInt() }
            .windowed(2, 1, false)
            .count { it[0] < it[1] }
    }

    @Test
    fun part2() {
        assertEquals(5, runPart2("aoc-2021-1-test.txt"))
        assertEquals(1704, runPart2("aoc-2021-1.txt"))
    }

    private fun runPart2(path: String): Int {
        val lines = File(ClassLoader.getSystemResource(path).file).readLines()

        return lines.asSequence()
            .map { it.toInt() }
            .windowed(3, 1, false)
            .map { it.sum() }
            .windowed(2, 1, false)
            .count { it[0] < it[1] }
    }
}
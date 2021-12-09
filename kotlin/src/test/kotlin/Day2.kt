import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import java.io.File

class Day2 {
    @Test
    fun part1() {
        val testResult = runPart1("aoc-2021-2-test.txt")
        assertEquals(150, testResult)

        val result = runPart1("aoc-2021-2.txt")
        assertEquals(1451208, result)
    }

    private fun runPart1(path: String): Int {
        val lines = File(ClassLoader.getSystemResource(path).file).readLines()

        val (rx, ry) = lines
            .fold(Pair(0, 0)) { (x, y), line ->
                val (dir, dist) = line.split(" ")

                when (dir) {
                    "up" -> Pair(x, y - dist.toInt())
                    "down" -> Pair(x, y + dist.toInt())
                    "forward" -> Pair(x + dist.toInt(), y)
                    else -> throw Exception("err")
                }
            }

        return rx * ry
    }

    @Test
    fun part2() {
        val testResult = runPart2("aoc-2021-2-test.txt")
        assertEquals(900, testResult)

        val result = runPart2("aoc-2021-2.txt")
        assertEquals(1620141160, result)
    }

    private fun runPart2(path: String): Int {
        val lines = File(ClassLoader.getSystemResource(path).file).readLines()

        val (rx, ry) = lines
            .fold(Triple(0, 0, 0)) { (x, y, aim), line ->
                val (dir, dist) = line.split(" ")

                when (dir) {
                    "up" -> Triple(x, y, aim - dist.toInt())
                    "down" -> Triple(x, y, aim + dist.toInt())
                    "forward" -> Triple(x + dist.toInt(), y + (aim * dist.toInt()), aim)
                    else -> throw Exception("err")
                }
            }

        return rx * ry
    }
}
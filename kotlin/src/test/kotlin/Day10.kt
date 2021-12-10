import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import java.io.File
import java.util.*

class Day10 {
    val BRACKETS = mapOf(Pair('(', ')'), Pair('[', ']'), Pair('{', '}'), Pair('<', '>'))
    val OPENERS = BRACKETS.keys
    val CLOSERS = BRACKETS.values
    val VERIFY_SCORE = mapOf(Pair(')', 3), Pair(']', 57), Pair('}', 1197), Pair('>', 25137))
    val COMPLETE_SCORE = mapOf(Pair(')', 1), Pair(']', 2), Pair('}', 3), Pair('>', 4))

    @Test
    fun part1() {
        assertEquals(26397, runPart1("aoc-2021-10-test.txt"))
        assertEquals(339477, runPart1("aoc-2021-10.txt"))
    }

    private fun runPart1(path: String): Int {
        val lines = File(ClassLoader.getSystemResource(path).file).readLines()
        return lines.mapNotNull { verifyLine(it) }
            .sumOf { VERIFY_SCORE[it]!! }

    }

    @Test
    fun part2() {
        assertEquals(288957, runPart2("aoc-2021-10-test.txt"))
        assertEquals(3049320156, runPart2("aoc-2021-10.txt"))
    }

    private fun runPart2(path: String): Long {
        val lines = File(ClassLoader.getSystemResource(path).file).readLines()

        val results = lines
            .asSequence()
            .filter { verifyLine(it) == null }
            .map { completeLine(it) }
            .map { scoreLine(it) }
            .sortedBy { it }
            .toList()

        return results[results.size / 2]
    }

    private fun completeLine(line: String): String {
        val stack = Stack<Char>()

        line.forEach { c ->
            if(OPENERS.contains(c)) {
                stack.add(c)
            } else if(CLOSERS.contains(c)) {
                stack.pop()
            }
        }

        return stack
            .map { BRACKETS[it]!! }
            .toList()
            .reversed()
            .joinToString("")
    }

    private fun scoreLine(completionChars: String): Long {
        return completionChars.fold(0L) { acc, c ->
            acc * 5 + COMPLETE_SCORE[c]!!
        }
    }

    private fun verifyLine(line: String): Char? {
        val stack = Stack<Char>()

        line.forEach { c ->
            if(OPENERS.contains(c)) {
                stack.add(c)
            } else if(CLOSERS.contains(c)) {
                val opener = stack.pop()
                if(BRACKETS[opener]!! != c) {
                    return c
                }
            }
        }

        return null
    }
}